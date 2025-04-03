package com.utc2.facility.service;

import com.utc2.facility.dto.request.BorrowEquipmentCreationRequest;
import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.response.BorrowEquipmentResponse;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.BorrowRequestStatus;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.BorrowEquipmentMapper;
import com.utc2.facility.repository.BorrowEquipmentRepository;
import com.utc2.facility.repository.BorrowRequestRepository;
import com.utc2.facility.repository.EquipmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowEquipmentService {//

    BorrowEquipmentRepository borrowEquipmentRepository;
    BorrowRequestRepository borrowRequestRepository;
    EquipmentRepository equipmentRepository;
    BorrowEquipmentMapper borrowEquipmentMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public BorrowEquipmentResponse createBorrowEquipment(BorrowEquipmentCreationRequest request) {

        BorrowRequest borrowRequest = borrowRequestRepository.findById(request.getBorrowRequestId())
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));
        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

        BorrowEquipment borrowEquipment = borrowEquipmentMapper.toBorrowEquipment(request);
        borrowEquipment.setBorrowRequest(borrowRequest);
        borrowEquipment.setEquipment(equipment);

        return borrowEquipmentMapper.toBorrowEquipmentResponse(borrowEquipmentRepository.save(borrowEquipment));
    }

    public BorrowEquipmentResponse getBorrowEquipment(@Param("id") String id) {
        BorrowEquipment borrowEquipment = borrowEquipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));
        return borrowEquipmentMapper.toBorrowEquipmentResponse(borrowEquipment);
    }

    public List<BorrowEquipmentResponse> getBorrowEquipmentByBorrowRequestId(@Param("borrowRequestId") String borrowRequestId) {
        List<BorrowEquipment> borrowEquipments = borrowEquipmentRepository.findByBorrowRequestId(borrowRequestId);
        return borrowEquipments.stream()
                .map(borrowEquipmentMapper::toBorrowEquipmentResponse)
                .toList();
    }

    public List<BorrowEquipmentResponse> getAllBorrowEquipments() {
        List<BorrowEquipment> borrowEquipments = borrowEquipmentRepository.findAll();
        return borrowEquipments.stream()
                .map(borrowEquipmentMapper::toBorrowEquipmentResponse)
                .toList();
    }

    public List<BorrowEquipmentResponse> getBorrowEquipmentByEquipmentName(@Param("equipmentName") String equipmentName) {
        Equipment equipment = equipmentRepository.findByName(equipmentName)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

        List<BorrowEquipment> borrowEquipments = borrowEquipmentRepository.findByEquipmentId(equipment.getId());
        return borrowEquipments.stream()
                .map(borrowEquipmentMapper::toBorrowEquipmentResponse)
                .toList();
    }

    public void deleteBorrowEquipment(@Param("id") String id) {
        BorrowEquipment borrowEquipment = borrowEquipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));
        borrowEquipmentRepository.delete(borrowEquipment);
    }

    public BorrowEquipmentResponse updateBorrowEquipment(@Param("id") String id, BorrowEquipmentCreationRequest request) {
        BorrowEquipment borrowEquipment = borrowEquipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));
        BorrowRequest borrowRequest = borrowRequestRepository.findById(request.getBorrowRequestId())
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        borrowEquipment.setEquipment(equipment);
        borrowEquipment.setBorrowRequest(borrowRequest);
        borrowEquipmentMapper.updateBorrowEquipment(borrowEquipment, request);

        return borrowEquipmentMapper.toBorrowEquipmentResponse(borrowEquipmentRepository.save(borrowEquipment));
    }

}
