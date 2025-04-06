package com.utc2.facility.service;

import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.BookingStatus;
import com.utc2.facility.enums.RoomStatus;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.BorrowRequestMapper;
import com.utc2.facility.repository.*;
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
public class BorrowRequestService {
//abc
    BorrowRequestRepository borrowRequestRepository;
    UserRepository userRepository;
    RoomRepository roomRepository;
    BorrowRequestMapper borrowRequestMapper;

    public BorrowRequestResponse createBorrowRequest(BorrowRequestCreationRequest request) {

        Room room = roomRepository.findByName(request.getRoomName())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        if (room.getStatus().equals(RoomStatus.BOOKED)) {
            throw new AppException(ErrorCode.ROOM_ALREADY_BOOKED);
        }
        if (room.getStatus().equals(RoomStatus.UNDER_MAINTENANCE)) {
            throw new AppException(ErrorCode.ROOM_UNAVAILABLE);
        }

        // Kiểm tra nếu thời gian mượn không hợp lệ (phải lớn hơn hiện tại ít nhất 1 tiếng)
        if (request.getBorrowDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new AppException(ErrorCode.BORROW_TIME_INVALID);
        }

        if (request.getExpectedReturnDate().isBefore(request.getBorrowDate().plusHours(1))) {
            throw new AppException(ErrorCode.BORROW_RETURN_TIME_INVALID);
        }

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Booking booking = borrowRequestMapper.toBorrowRequest(request);
        booking.setUser(user);
        booking.setRoom(room);

        booking.setStatus(BookingStatus.PENDING_APPROVAL);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(booking));
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public BorrowRequestResponse getBorrowRequest(@Param("id") String id) {
        Booking booking = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        if (booking.getBorrowDate().isBefore(LocalDateTime.now()) && booking.getStatus() == BookingStatus.PENDING_APPROVAL) {
            booking.setStatus(BookingStatus.REJECTED);
            borrowRequestRepository.save(booking);
            throw new AppException(ErrorCode.BORROW_REQUEST_EXPIRED);
        }

        return borrowRequestMapper.toBorrowRequestResponse(booking);
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public List<BorrowRequestResponse> getBorrowRequestByBorrowDate(String borrowDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime borrowDate = LocalDateTime.parse(borrowDateStr, formatter);

        List<Booking> bookings = borrowRequestRepository.findByBorrowDate(borrowDate);
        return bookings.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public List<BorrowRequestResponse> getBorrowRequestByReturnDate(String returnDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime returnDate = LocalDateTime.parse(returnDateStr, formatter);

        List<Booking> bookings = borrowRequestRepository.findByReturnDate(returnDate);
        return bookings.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public List<BorrowRequestResponse> getBorrowRequestByExpectedReturnDate(String expectedReturnDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expectedReturnDate = LocalDateTime.parse(expectedReturnDateStr, formatter);

        List<Booking> bookings = borrowRequestRepository.findByExpectedReturnDate(expectedReturnDate);
        return bookings.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BorrowRequestResponse> getAllBorrowRequests() {
        List<Booking> bookings = borrowRequestRepository.findAll();
        return bookings.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    //@PostAuthorize("returnObject.username == authentication.name")
    public List<BorrowRequestResponse> getBorrowRequestByUserId(@Param("userId") String userId) {
        List<Booking> bookings = borrowRequestRepository.findByUserId(userId);
        return bookings.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBorrowRequest(@Param("id") String id) {
        Booking booking = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));
        borrowRequestRepository.delete(booking);
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public BorrowRequestResponse updateBorrowRequest(@Param("id") String id, BorrowRequestCreationRequest request) {
        Booking booking = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Room room = roomRepository.findByName(request.getRoomName())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        booking.setUser(user);
        booking.setRoom(room);
        borrowRequestMapper.updateBorrowRequest(booking, request);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(booking));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BorrowRequestResponse approveBorrowRequest(String borrowRequestId) {
        Booking booking = borrowRequestRepository.findById(borrowRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        if (!booking.getStatus().equals(BookingStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.BOOKED);
        roomRepository.save(room);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(booking));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BorrowRequestResponse rejectBorrowRequest(String borrowRequestId) {
        Booking booking = borrowRequestRepository.findById(borrowRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        if (!booking.getStatus().equals(BookingStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        booking.setStatus(BookingStatus.REJECTED);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(booking));
    }

    public BorrowRequestResponse returnRoom(String borrowRequestId) {
        Booking booking = borrowRequestRepository.findById(borrowRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        if (!booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new AppException(ErrorCode.REQUEST_NOT_APPROVED);
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setReturnDate(LocalDateTime.now());

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(booking));
    }
}
