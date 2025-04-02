package com.utc2.facility.service;

import com.utc2.facility.dto.request.RepairEquipmentRequestCreationRequest;
import com.utc2.facility.dto.request.RepairRoomRequestCreationRequest;
import com.utc2.facility.dto.response.RepairEquipmentRequestResponse;
import com.utc2.facility.dto.response.RepairRoomRequestResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.RepairStatus;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.RepairEquipmentRequestMapper;
import com.utc2.facility.repository.EquipmentRepository;
import com.utc2.facility.repository.RepairEquipmentRequestRepository;
import com.utc2.facility.repository.UserRepository;
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
public class RepairEquipmentRequestService {//

    RepairEquipmentRequestRepository repairEquipmentRequestRepository;
    UserRepository userRepository;
    EquipmentRepository equipmentRepository;
    RepairEquipmentRequestMapper repairEquipmentRequestMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RepairEquipmentRequestResponse createRepairEquipmentRequest(RepairEquipmentRequestCreationRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

        RepairEquipmentRequest repairEquipmentRequest = repairEquipmentRequestMapper.toRepairEquipmentRequest(request);
        repairEquipmentRequest.setEquipment(equipment);
        repairEquipmentRequest.setUser(user);
        repairEquipmentRequest.setStatus(RepairStatus.PENDING);

        return repairEquipmentRequestMapper.toRepairEquipmentRequestResponse(repairEquipmentRequestRepository.save(repairEquipmentRequest));
    }

    public RepairEquipmentRequestResponse getRepairEquipmentRequest(@Param("id") String id) {
        RepairEquipmentRequest repairEquipmentRequest = repairEquipmentRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REPAIR_REQUEST_NOT_FOUND));
        return repairEquipmentRequestMapper.toRepairEquipmentRequestResponse(repairEquipmentRequest);
    }

    public List<RepairEquipmentRequestResponse> getAllRepairEquipmentRequests() {
        List<RepairEquipmentRequest> repairEquipmentRequests = repairEquipmentRequestRepository.findAll();
        return repairEquipmentRequests.stream()
                .map(repairEquipmentRequestMapper::toRepairEquipmentRequestResponse)
                .toList();
    }

    public List<RepairEquipmentRequestResponse> getRepairEquipmentRequestByUserId(@Param("userId") String userId) {
        List<RepairEquipmentRequest> repairEquipmentRequests = repairEquipmentRequestRepository.findByUserId(userId);
        return repairEquipmentRequests.stream()
                .map(repairEquipmentRequestMapper::toRepairEquipmentRequestResponse)
                .toList();
    }

    public void deleteRepairEquipmentRequest(@Param("id") String id) {
        RepairEquipmentRequest repairEquipmentRequest = repairEquipmentRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REPAIR_REQUEST_NOT_FOUND));
        repairEquipmentRequestRepository.delete(repairEquipmentRequest);
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
