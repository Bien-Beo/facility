package com.utc2.facility.service;

import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.BorrowRequestStatus;
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
//
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

        BorrowRequest borrowRequest = borrowRequestMapper.toBorrowRequest(request);
        borrowRequest.setUser(user);
        borrowRequest.setRoom(room);

        borrowRequest.setStatus(BorrowRequestStatus.PENDING);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(borrowRequest));
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public BorrowRequestResponse getBorrowRequest(@Param("id") String id) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        if (borrowRequest.getBorrowDate().isBefore(LocalDateTime.now()) && borrowRequest.getStatus() == BorrowRequestStatus.PENDING) {
            borrowRequest.setStatus(BorrowRequestStatus.REJECTED);
            borrowRequestRepository.save(borrowRequest);
            throw new AppException(ErrorCode.BORROW_REQUEST_EXPIRED);
        }

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequest);
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public List<BorrowRequestResponse> getBorrowRequestByBorrowDate(String borrowDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime borrowDate = LocalDateTime.parse(borrowDateStr, formatter);

        List<BorrowRequest> borrowRequests = borrowRequestRepository.findByBorrowDate(borrowDate);
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public List<BorrowRequestResponse> getBorrowRequestByReturnDate(String returnDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime returnDate = LocalDateTime.parse(returnDateStr, formatter);

        List<BorrowRequest> borrowRequests = borrowRequestRepository.findByReturnDate(returnDate);
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public List<BorrowRequestResponse> getBorrowRequestByExpectedReturnDate(String expectedReturnDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expectedReturnDate = LocalDateTime.parse(expectedReturnDateStr, formatter);

        List<BorrowRequest> borrowRequests = borrowRequestRepository.findByExpectedReturnDate(expectedReturnDate);
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BorrowRequestResponse> getAllBorrowRequests() {
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findAll();
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    //@PostAuthorize("returnObject.username == authentication.name")
    public List<BorrowRequestResponse> getBorrowRequestByUserId(@Param("userId") String userId) {
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findByUserId(userId);
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBorrowRequest(@Param("id") String id) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));
        borrowRequestRepository.delete(borrowRequest);
    }

    //@PostAuthorize("returnObject.userName == authentication.name")
    public BorrowRequestResponse updateBorrowRequest(@Param("id") String id, BorrowRequestCreationRequest request) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Room room = roomRepository.findByName(request.getRoomName())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        borrowRequest.setUser(user);
        borrowRequest.setRoom(room);
        borrowRequestMapper.updateBorrowRequest(borrowRequest, request);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(borrowRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BorrowRequestResponse approveBorrowRequest(String borrowRequestId) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(borrowRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        if (!borrowRequest.getStatus().equals(BorrowRequestStatus.PENDING)) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        borrowRequest.setStatus(BorrowRequestStatus.APPROVED);

        Room room = borrowRequest.getRoom();
        room.setStatus(RoomStatus.BOOKED);
        roomRepository.save(room);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(borrowRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BorrowRequestResponse rejectBorrowRequest(String borrowRequestId) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(borrowRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        if (!borrowRequest.getStatus().equals(BorrowRequestStatus.PENDING)) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        borrowRequest.setStatus(BorrowRequestStatus.REJECTED);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(borrowRequest));
    }

    public BorrowRequestResponse returnRoom(String borrowRequestId) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(borrowRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        if (!borrowRequest.getStatus().equals(BorrowRequestStatus.APPROVED)) {
            throw new AppException(ErrorCode.REQUEST_NOT_APPROVED);
        }

        borrowRequest.setStatus(BorrowRequestStatus.COMPLETED);
        borrowRequest.setReturnDate(LocalDateTime.now());

        Room room = borrowRequest.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(borrowRequest));
    }
}
