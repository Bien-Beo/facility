package com.utc2.facility.service;

import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.request.RoomUpdateRequest;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.RoomStatus;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.EquipmentMapper;
import com.utc2.facility.mapper.RoomMapper;
import com.utc2.facility.repository.*;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomRepository roomRepository;
    RoomTypeRepository roomTypeRepository;
    BuildingRepository buildingRepository;
    UserRepository userRepository;
    EquipmentItemRepository equipmentItemRepository;
    RoomMapper roomMapper;
    EquipmentMapper equipmentMapper;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public RoomResponse createRoom(RoomCreationRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROOM_EXISTED);
        }

        // Fetch các entity liên quan bằng Name
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        User facilityManager = null;
        if (StringUtils.hasText(request.getFacilityManagerId())) {
            facilityManager = userRepository.findById(request.getFacilityManagerId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); 
            // TODO: Optional: Kiểm tra xem user này có đúng là role FACILITY_MANAGER không?
        }

        // Map các trường cơ bản từ DTO (Mapper cần ignore các object liên kết)
        Room room = roomMapper.toRoom(request);

        // Set các đối tượng liên kết đã fetch
        room.setBuilding(building);
        room.setRoomType(roomType);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setFacilityManager(facilityManager); 

        Room savedRoom = roomRepository.save(room);

        return buildFullRoomResponse(savedRoom);
    }
    
//    @PreAuthorize("isAuthenticated()")
    public RoomResponse getRoomById(String id) {
        Room room = findRoomByIdOrThrow(id);
        return buildFullRoomResponse(room);
    }

    @PreAuthorize("isAuthenticated()")
    public Page<RoomResponse> getRooms(Pageable pageable) {
        Page<Room> roomPage = roomRepository.findAll(pageable);
        return roomPage.map(this::buildFullRoomResponse); 
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')") 
    public void deleteRoom(String id) {
        Room room = findRoomByIdOrThrow(id);

        // TODO: Kiểm tra ràng buộc trước khi xóa:
        // 1. Có EquipmentItem nào đang đặt phòng này làm defaultRoom không? (FK nên là SET NULL)
        // 2. Có Booking nào đang active/pending cho phòng này không? (FK nên là RESTRICT hoặc SET NULL?)
        // boolean hasActiveBookings = bookingRepository.existsByRoomIdAndStatusIn(...);
        // if(hasActiveBookings) { throw new AppException(ErrorCode.ROOM_HAS_ACTIVE_BOOKINGS); }

        roomRepository.delete(room); 
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public RoomResponse updateRoom(String id, RoomUpdateRequest request) {
        Room existingRoom = findRoomByIdOrThrow(id);

//        if (StringUtils.hasText(request.getName())
//                && !request.getName().equals(existingRoom.getName())
//                && roomRepository.existsByNameAndIdNot(request.getName(), id)) {
//            throw new AppException(ErrorCode.ROOM_EXISTED);
//        }

        roomMapper.updateRoom(existingRoom, request);

        if (StringUtils.hasText(request.getBuildingName())) {
            Building building = buildingRepository.findByName(request.getBuildingName())
                    .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
            existingRoom.setBuilding(building);
        }

        if (StringUtils.hasText(request.getRoomTypeName())) {
            RoomType roomType = roomTypeRepository.findByName(request.getRoomTypeName())
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
            existingRoom.setRoomType(roomType);
        }

        if (request.getFacilityManagerId() != null) { 
            if (StringUtils.hasText(request.getFacilityManagerId())) {
                User facilityManager = userRepository.findById(request.getFacilityManagerId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                // TODO: Optional: Check role của user này?
                existingRoom.setFacilityManager(facilityManager);
            }
        }

        if (request.getStatus() != null) {
            // TODO: Thêm validation nếu cần (ví dụ: không cho chuyển từ UNDER_MAINTENANCE sang AVAILABLE qua đây?)
            existingRoom.setStatus(request.getStatus());
        }


        Room updatedRoom = roomRepository.save(existingRoom);
        return buildFullRoomResponse(updatedRoom);
    }

    // --- Helper Methods ---

    private Room findRoomByIdOrThrow(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
    }

    private Room findRoomByNameOrThrow(String roomName) {
        return roomRepository.findByName(roomName)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
    }

    // Helper xây dựng response đầy đủ, bao gồm cả default equipment
    private RoomResponse buildFullRoomResponse(Room room) {
        // 1. Dùng mapper map các trường cơ bản của Room
        RoomResponse response = roomMapper.toRoomResponse(room); // Mapper này chỉ map trường của Room

        // 2. Fetch và map default equipment
        List<EquipmentItem> defaultItems = equipmentItemRepository.findByDefaultRoom_Id(room.getId());
        List<EquipmentResponse> equipmentResponses = defaultItems.stream()
                .map(equipmentMapper::toEquipmentResponse)
                .collect(Collectors.toList());
        response.setDefaultEquipments(equipmentResponses);

        return response;
    }
}
