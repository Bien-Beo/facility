package com.utc2.facility.service;

import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.request.EquipmentCreationRequest;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.BorrowRequestMapper;
import com.utc2.facility.mapper.EquipmentMapper;
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

    BorrowRequestRepository borrowRequestRepository;
    UserRepository userRepository;
    RoomRepository roomRepository;
    BorrowRequestMapper borrowRequestMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public BorrowRequestResponse createBorrowRequest(BorrowRequestCreationRequest request) {

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Room room = roomRepository.findByName(request.getRoomName())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        BorrowRequest borrowRequest = borrowRequestMapper.toBorrowRequest(request);
        borrowRequest.setUser(user);
        borrowRequest.setRoom(room);

        return borrowRequestMapper.toBorrowRequestResponse(borrowRequestRepository.save(borrowRequest));
    }

    public BorrowRequestResponse getBorrowRequest(@Param("id") String id) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));
        return borrowRequestMapper.toBorrowRequestResponse(borrowRequest);
    }

    public List<BorrowRequestResponse> getBorrowRequestByBorrowDate(String borrowDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime borrowDate = LocalDateTime.parse(borrowDateStr, formatter);

        List<BorrowRequest> borrowRequests = borrowRequestRepository.findByBorrowDate(borrowDate);
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    public List<BorrowRequestResponse> getBorrowRequestByReturnDate(String returnDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime returnDate = LocalDateTime.parse(returnDateStr, formatter);

        List<BorrowRequest> borrowRequests = borrowRequestRepository.findByReturnDate(returnDate);
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    public List<BorrowRequestResponse> getAllBorrowRequests() {
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findAll();
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    public List<BorrowRequestResponse> getBorrowRequestByUserId(@Param("userId") String userId) {
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findByUserId(userId);
        return borrowRequests.stream()
                .map(borrowRequestMapper::toBorrowRequestResponse)
                .toList();
    }

    public void deleteBorrowRequest(@Param("id") String id) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));
        borrowRequestRepository.delete(borrowRequest);
    }

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

}
