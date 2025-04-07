package com.utc2.facility.service;

import com.utc2.facility.dto.request.MaintenanceRequest;
import com.utc2.facility.dto.request.MaintenanceUpdate;
import com.utc2.facility.dto.response.MaintenanceResponse;
import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.MaintenanceTicket;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.User;
import com.utc2.facility.enums.MaintenanceStatus;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.MaintenanceMapper;
import com.utc2.facility.repository.EquipmentItemRepository;
import com.utc2.facility.repository.MaintenanceRepository;
import com.utc2.facility.repository.RoomRepository;
import com.utc2.facility.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MaintenanceService {

    MaintenanceRepository maintenanceRepository;
    EquipmentItemRepository equipmentItemRepository;
    UserRepository userRepository;
    RoomRepository roomRepository;
    MaintenanceMapper maintenanceMapper;

    @PreAuthorize("isAuthenticated()")
    public MaintenanceResponse createMaintenanceRequest(MaintenanceRequest request) {
        // Lấy thông tin người dùng đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        // Giả sử Principal là UserDetails hoặc đối tượng chứa thông tin User
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        MaintenanceTicket maintenanceTicket = maintenanceMapper.toMaintenance(request);
        maintenanceTicket.setReportedBy(currentUser); // Gán người báo cáo
        maintenanceTicket.setStatus(MaintenanceStatus.REPORTED); // Set trạng thái ban đầu

        // Xử lý roomId HOẶC itemId
        String roomId = request.getRoomId();
        String itemId = request.getItemId();

        // DTO validation nên đảm bảo chỉ một trong hai được cung cấp, nhưng kiểm tra lại ở service
        if (StringUtils.hasText(roomId) && StringUtils.hasText(itemId)) {
            throw new AppException(ErrorCode.BOTH_ROOM_AND_ITEM_PROVIDED);
        } else if (StringUtils.hasText(roomId)) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
            maintenanceTicket.setRoom(room);
            maintenanceTicket.setItem(null); // Đảm bảo item là null
        } else if (StringUtils.hasText(itemId)) {
            EquipmentItem equipmentItem = equipmentItemRepository.findById(itemId)
                    .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_ITEM_NOT_FOUND));
            maintenanceTicket.setItem(equipmentItem);
            maintenanceTicket.setRoom(null); // Đảm bảo room là null
        } else {
            throw new AppException(ErrorCode.ROOM_OR_ITEM_REQUIRED);
        }

        // Lưu ticket
        MaintenanceTicket savedTicket = maintenanceRepository.save(maintenanceTicket);
        return maintenanceMapper.toMaintenanceResponse(savedTicket);
    }

    public MaintenanceResponse getMaintenanceTicket(@Param("id") String id) {
        MaintenanceTicket maintenanceTicket = maintenanceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAINTENANCE_TICKET_NOT_FOUND));
        return maintenanceMapper.toMaintenanceResponse(maintenanceTicket);
    }

    public List<MaintenanceResponse> getMaintenanceTicketsByRoomName(@Param("roomName") String roomName) {
        List<MaintenanceTicket> maintenanceTickets = maintenanceRepository.findByRoomName(roomName);
        return maintenanceTickets.stream()
                .map(maintenanceMapper::toMaintenanceResponse)
                .toList();
    }

    public List<MaintenanceResponse> getAllMaintenanceTickets() {
        List<MaintenanceTicket> maintenanceTickets = maintenanceRepository.findAll();
        return maintenanceTickets.stream()
                .map(maintenanceMapper::toMaintenanceResponse)
                .toList();
    }

    public List<MaintenanceResponse> getMaintenanceByReportedId(@Param("reportedId") String reportedId) {
        List<MaintenanceTicket> maintenanceTickets = maintenanceRepository.findByReportedBy_Id(reportedId);
        return maintenanceTickets.stream()
                .map(maintenanceMapper::toMaintenanceResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMaintenanceTicket(@Param("id") String id) {
        MaintenanceTicket maintenanceTicket = maintenanceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAINTENANCE_TICKET_NOT_FOUND));
        maintenanceRepository.delete(maintenanceTicket);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER') or @maintenanceSecurityService.isAssignedTechnician(#id, authentication.name)")
    public MaintenanceResponse updateMaintenanceTicket(@Param("id") String id, MaintenanceUpdate request) {
        MaintenanceTicket maintenanceTicket = maintenanceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAINTENANCE_TICKET_NOT_FOUND));

        maintenanceMapper.updateMaintenanceTicket(maintenanceTicket, request);

        return maintenanceMapper.toMaintenanceResponse(maintenanceRepository.save(maintenanceTicket));
    }

}
