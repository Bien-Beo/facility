package com.utc2.facility.service;

import com.utc2.facility.dto.request.RepairRoomRequestCreationRequest;
import com.utc2.facility.dto.response.RepairRoomRequestResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.RepairStatus;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.RepairRoomRequestMapper;
import com.utc2.facility.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepairRoomRequestService {//

    RepairRoomRequestRepository repairRoomRequestRepository;
    UserRepository userRepository;
    EquipmentRepository equipmentRepository;
    RoomRepository roomRepository;
    RepairRoomRequestMapper repairRoomRequestMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RepairRoomRequestResponse createRepairRoomRequest(RepairRoomRequestCreationRequest request) {
        Room room = roomRepository.findByName(request.getRoomName())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        RepairRoomRequest repairRoomRequest = repairRoomRequestMapper.toRepairRoomRequest(request);
        repairRoomRequest.setRoom(room);
        repairRoomRequest.setUser(user);
        repairRoomRequest.setStatus(RepairStatus.PENDING);

        return repairRoomRequestMapper.toRepairRoomRequestResponse(repairRoomRequestRepository.save(repairRoomRequest));
    }

    public RepairRoomRequestResponse getRepairRoomRequest(@Param("id") String id) {
        RepairRoomRequest repairRoomRequest = repairRoomRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REPAIR_REQUEST_NOT_FOUND));
        return repairRoomRequestMapper.toRepairRoomRequestResponse(repairRoomRequest);
    }

    public RepairRoomRequestResponse getRepairRoomRequestByRoomName(@Param("roomName") String roomName) {
        RepairRoomRequest repairRoomRequest = repairRoomRequestRepository.findByRoomName(roomName)
                .orElseThrow(() -> new AppException(ErrorCode.REPAIR_REQUEST_NOT_FOUND));
        return repairRoomRequestMapper.toRepairRoomRequestResponse(repairRoomRequest);
    }

    public List<RepairRoomRequestResponse> getAllRepairRoomRequests() {
        List<RepairRoomRequest> repairRoomRequests = repairRoomRequestRepository.findAll();
        return repairRoomRequests.stream()
                .map(repairRoomRequestMapper::toRepairRoomRequestResponse)
                .toList();
    }

    public List<RepairRoomRequestResponse> getRepairRoomRequestByUserId(@Param("userId") String userId) {
        List<RepairRoomRequest> repairRoomRequests = repairRoomRequestRepository.findByUserId(userId);
        return repairRoomRequests.stream()
                .map(repairRoomRequestMapper::toRepairRoomRequestResponse)
                .toList();
    }

    public void deleteRepairRoomRequest(@Param("id") String id) {
        RepairRoomRequest repairRoomRequest = repairRoomRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REPAIR_REQUEST_NOT_FOUND));
        repairRoomRequestRepository.delete(repairRoomRequest);
    }

//    public RepairRequestResponse updateRepairRequest(@Param("id") String id, CancelRequestUpdateRequest request) {
//        CancelRequest cancelRequest = cancelRequestRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.CANCEL_REQUEST_NOT_FOUND));
//
//        cancelRequestMapper.updateCancelRequest(cancelRequest, request);
//
//        return cancelRequestMapper.toCancelRequestResponse(cancelRequestRepository.save(cancelRequest));
//    }

}
