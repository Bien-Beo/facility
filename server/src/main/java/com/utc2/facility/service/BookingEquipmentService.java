package com.utc2.facility.service;

import com.utc2.facility.dto.request.BookingEquipmentCreationRequest;
import com.utc2.facility.dto.response.BookingEquipmentResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.BookingEquipmentMapper;
import com.utc2.facility.repository.BookingEquipmentRepository;
import com.utc2.facility.repository.BookingRepository;
import com.utc2.facility.repository.EquipmentItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingEquipmentService {

    BookingEquipmentRepository bookingEquipmentRepository;
    BookingRepository bookingRepository;
    EquipmentItemRepository equipmentItemRepository;
    BookingEquipmentMapper bookingEquipmentMapper;

    public BookingEquipmentResponse createBookingEquipment(BookingEquipmentCreationRequest request) {

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));
        EquipmentItem equipmentItem = equipmentItemRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

        BookingEquipment bookingEquipment = bookingEquipmentMapper.toBookingEquipment(request);
        bookingEquipment.setBooking(booking);
        bookingEquipment.setItem(equipmentItem);

        return bookingEquipmentMapper.toBookingEquipmentResponse(bookingEquipmentRepository.save(bookingEquipment));
    }

//    public BookingEquipmentResponse getBookingEquipment(@Param("id") String id) {
//        BookingEquipment bookingEquipment = bookingEquipmentRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));
//        return bookingEquipmentMapper.toBookingEquipmentResponse(bookingEquipment);
//    }

    public List<BookingEquipmentResponse> getBookingEquipmentByBookingId(@Param("bookingId") String bookingId) {
        List<BookingEquipment> bookingEquipments = bookingEquipmentRepository.findByBookingId(bookingId);
        return bookingEquipments.stream()
                .map(bookingEquipmentMapper::toBookingEquipmentResponse)
                .toList();
    }

    public List<BookingEquipmentResponse> getAllBookingEquipments() {
        List<BookingEquipment> bookingEquipments = bookingEquipmentRepository.findAll();
        return bookingEquipments.stream()
                .map(bookingEquipmentMapper::toBookingEquipmentResponse)
                .toList();
    }

//    public void deleteBookingEquipment(@Param("id") String id) {
//        BookingEquipment bookingEquipment = bookingEquipmentRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));
//        bookingEquipmentRepository.delete(bookingEquipment);
//    }

//    public BookingEquipmentResponse updateBorrowEquipment(@Param("id") String id, BookingEquipmentCreationRequest request) {
//        BookingEquipment bookingEquipment = bookingEquipmentRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.BORROW_EQUIPMENT_NOT_FOUND));
//
//        EquipmentItem equipmentItem = equipmentRepository.findById(request.getEquipmentId())
//                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));
//        Booking booking = bookingRepository.findById(request.getBorrowRequestId())
//                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));
//
//        bookingEquipment.setEquipmentItem(equipmentItem);
//        bookingEquipment.setBooking(booking);
//        bookingEquipmentMapper.updateBorrowEquipment(bookingEquipment, request);
//
//        return bookingEquipmentMapper.toBookingEquipmentResponse(bookingEquipmentRepository.save(bookingEquipment));
//    }

}
