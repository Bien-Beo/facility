package com.utc2.facility.service;

import com.utc2.facility.dto.request.EquipmentItemCreationRequest;
import com.utc2.facility.dto.request.EquipmentItemUpdateRequest;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.EquipmentStatus;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.EquipmentMapper;
import com.utc2.facility.repository.*;
import com.utc2.facility.specification.EquipmentSpecification;
import com.utc2.facility.specification.RoomSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentService {

    EquipmentItemRepository equipmentItemRepository;
    EquipmentModelRepository equipmentModelRepository;
    RoomRepository roomRepository;
    EquipmentMapper equipmentMapper;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public EquipmentResponse createEquipmentItem(EquipmentItemCreationRequest request) {

        // 1. Kiểm tra và lấy Model
        EquipmentModel model = equipmentModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new AppException(ErrorCode.MODEL_NOT_FOUND));

        // 2. Kiểm tra và lấy Phòng mặc định (nếu có)
        Room defaultRoom = null;
        if (StringUtils.hasText(request.getDefaultRoomId())) {
            defaultRoom = roomRepository.findById(request.getDefaultRoomId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        }

        // 3. (Tùy chọn) Kiểm tra trùng lặp Serial Number / Asset Tag nếu chúng là unique
        if (StringUtils.hasText(request.getSerialNumber()) && equipmentItemRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new AppException(ErrorCode.SERIAL_NUMBER_EXISTED);
        }
        if (StringUtils.hasText(request.getAssetTag()) && equipmentItemRepository.existsByAssetTag(request.getAssetTag())) {
            throw new AppException(ErrorCode.ASSET_TAG_EXISTED);
        }

        // 4. Tạo Entity EquipmentItem
        EquipmentItem newItem = new EquipmentItem();
        newItem.setSerialNumber(request.getSerialNumber());
        newItem.setAssetTag(request.getAssetTag());
        newItem.setPurchaseDate(request.getPurchaseDate());
        newItem.setWarrantyExpiryDate(request.getWarrantyExpiryDate());
        newItem.setNotes(request.getNotes());

        // 5. Set các mối quan hệ
        newItem.setModel(model);
        newItem.setDefaultRoom(defaultRoom); // Gán phòng mặc định (có thể null)

        // 6. Set trạng thái ban đầu
        newItem.setStatus(EquipmentStatus.AVAILABLE); // Trạng thái mặc định khi mới tạo

        // 7. Lưu vào DB
        EquipmentItem savedItem = equipmentItemRepository.save(newItem);

        // 8. Trả về Response DTO
        return buildFullEquipmentResponse(savedItem); // Dùng helper để tạo response đầy đủ
    }

    public EquipmentResponse getEquipmentItemById(String itemId) {
        EquipmentItem item = findEquipmentItemByIdOrThrow(itemId);
        return buildFullEquipmentResponse(item);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER', 'TECHNICIAN')")
    public Page<EquipmentResponse> getEquipmentItems(String roomId, String modelId, Integer year, Pageable pageable) {
        Page<EquipmentItem> itemPage = equipmentItemRepository.findAll(
                EquipmentSpecification.filterByRoomId(roomId)
                        .and(EquipmentSpecification.filterByModelId(modelId))
                        .and(EquipmentSpecification.filterByYear(year)), pageable);
        return itemPage.map(this::buildFullEquipmentResponse);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEquipmentItem(String itemId) {
        EquipmentItem item = findEquipmentItemByIdOrThrow(itemId);

        equipmentItemRepository.delete(item);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public EquipmentResponse updateEquipmentItem(String itemId, EquipmentItemUpdateRequest request) {
        EquipmentItem existingItem = findEquipmentItemByIdOrThrow(itemId);

        equipmentMapper.updateEquipmentItem(existingItem, request);

        // Xử lý cập nhật phòng mặc định
        if (request.getDefaultRoomId() != null) {
            if (StringUtils.hasText(request.getDefaultRoomId())) {
                Room newDefaultRoom = roomRepository.findById(request.getDefaultRoomId())
                        .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
                existingItem.setDefaultRoom(newDefaultRoom);
            } else {
                existingItem.setDefaultRoom(null);
            }
        }

        // Xử lý cập nhật trạng thái (nếu được phép và có trong request)
        // Cần cẩn thận khi cho phép cập nhật trạng thái thủ công qua API này
        if (request.getStatus() != null) {
            // TODO: Thêm logic kiểm tra xem trạng thái mới có hợp lệ không
            // Ví dụ: không thể chuyển từ BROKEN sang AVAILABLE mà không qua Maintenance?
            existingItem.setStatus(request.getStatus());
        }

        // (Tùy chọn) Kiểm tra trùng lặp Asset Tag nếu nó được cập nhật và là unique
        if (request.getAssetTag() != null && !request.getAssetTag().equals(existingItem.getAssetTag())) {
            if (equipmentItemRepository.existsByAssetTagAndIdNot(request.getAssetTag(), existingItem.getId())) {
                throw new AppException(ErrorCode.ASSET_TAG_EXISTED);
            }
            existingItem.setAssetTag(request.getAssetTag());
        }

        EquipmentItem updatedItem = equipmentItemRepository.save(existingItem);
        return buildFullEquipmentResponse(updatedItem);
    }

    private EquipmentItem findEquipmentItemByIdOrThrow(String itemId) {
        return equipmentItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_ITEM_NOT_FOUND));
    }

    private EquipmentResponse buildFullEquipmentResponse(EquipmentItem item) {
        EquipmentResponse response = equipmentMapper.toEquipmentResponse(item);

        // Lấy thông tin từ các đối tượng liên kết (kiểm tra null)
        if (item.getModel() != null) {
            response.setModelName(item.getModel().getName());
            if (item.getModel().getEquipmentType() != null) {
                response.setTypeName(item.getModel().getEquipmentType().getName());
            }
        }
        if (item.getDefaultRoom() != null) {
            response.setDefaultRoomName(item.getDefaultRoom().getName());
        }

        return response;
    }
}