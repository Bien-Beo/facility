package com.utc2.facility.service;

import com.utc2.facility.dto.request.BorrowEquipmentCreationRequest;
import com.utc2.facility.dto.response.BorrowEquipmentResponse;
import com.utc2.facility.entity.*;
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

        Booking booking = borrowRequestRepository.findById(request.getBorrowRequestId())
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));
        EquipmentItem equipmentItem = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

        BookingEquipment bookingEquipment = borrowEquipmentMapper.toBorrowEquipment(request);
        bookingEquipment.setBooking(booking);
        bookingEquipment.setEquipmentItem(equipmentItem);

        return borrowEquipmentMapper.toBorrowEquipmentResponse(borrowEquipmentRepository.save(bookingEquipment));
    }

    public BorrowEquipmentResponse getBorrowEquipment(@Param("id") String id) {
        BookingEquipment bookingEquipment = borrowEquipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));
        return borrowEquipmentMapper.toBorrowEquipmentResponse(bookingEquipment);
    }

    public List<BorrowEquipmentResponse> getBorrowEquipmentByBorrowRequestId(@Param("borrowRequestId") String borrowRequestId) {
        List<BookingEquipment> bookingEquipments = borrowEquipmentRepository.findByBorrowRequestId(borrowRequestId);
        return bookingEquipments.stream()
                .map(borrowEquipmentMapper::toBorrowEquipmentResponse)
                .toList();
    }

    public List<BorrowEquipmentResponse> getAllBorrowEquipments() {
        List<BookingEquipment> bookingEquipments = borrowEquipmentRepository.findAll();
        return bookingEquipments.stream()
                .map(borrowEquipmentMapper::toBorrowEquipmentResponse)
                .toList();
    }

    public List<BorrowEquipmentResponse> getBorrowEquipmentByEquipmentName(@Param("equipmentName") String equipmentName) {
        EquipmentItem equipmentItem = equipmentRepository.findByName(equipmentName)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

        List<BookingEquipment> bookingEquipments = borrowEquipmentRepository.findByEquipmentId(equipmentItem.getId());
        return bookingEquipments.stream()
                .map(borrowEquipmentMapper::toBorrowEquipmentResponse)
                .toList();
    }

    public void deleteBorrowEquipment(@Param("id") String id) {
        BookingEquipment bookingEquipment = borrowEquipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));
        borrowEquipmentRepository.delete(bookingEquipment);
    }

    public BorrowEquipmentResponse updateBorrowEquipment(@Param("id") String id, BorrowEquipmentCreationRequest request) {
        BookingEquipment bookingEquipment = borrowEquipmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));

        EquipmentItem equipmentItem = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));
        Booking booking = borrowRequestRepository.findById(request.getBorrowRequestId())
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        bookingEquipment.setEquipmentItem(equipmentItem);
        bookingEquipment.setBooking(booking);
        borrowEquipmentMapper.updateBorrowEquipment(bookingEquipment, request);

        return borrowEquipmentMapper.toBorrowEquipmentResponse(borrowEquipmentRepository.save(bookingEquipment));
    }

}
