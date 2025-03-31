package com.utc2.facility.service;

import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.request.CancelRequestCreationRequest;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.dto.response.CancelRequestResponse;
import com.utc2.facility.entity.BorrowRequest;
import com.utc2.facility.entity.CancelRequest;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.User;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.BorrowRequestMapper;
import com.utc2.facility.mapper.CancelRequestMapper;
import com.utc2.facility.repository.BorrowRequestRepository;
import com.utc2.facility.repository.CancelRequestRepository;
import com.utc2.facility.repository.RoomRepository;
import com.utc2.facility.repository.UserRepository;
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
public class CancelRequestService {

    CancelRequestRepository cancelRequestRepository;
    UserRepository userRepository;
    BorrowRequestRepository borrowRequestRepository;
    CancelRequestMapper cancelRequestMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CancelRequestResponse createCancelRequest(CancelRequestCreationRequest request) {

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        BorrowRequest borrowRequest = borrowRequestRepository.findById(request.getBorrowRequestId())
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        CancelRequest cancelRequest = cancelRequestMapper.toCancelRequest(request);
        cancelRequest.setUser(user);
        cancelRequest.setBorrowRequest(borrowRequest);

        return cancelRequestMapper.toCancelRequestResponse(cancelRequestRepository.save(cancelRequest));
    }

    public CancelRequestResponse getCancelRequest(@Param("id") String id) {
        CancelRequest cancelRequest = cancelRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CANCEL_REQUEST_NOT_FOUND));
        return cancelRequestMapper.toCancelRequestResponse(cancelRequest);
    }

    public CancelRequestResponse getCancelRequestByBorrowRequestId(String borrowRequestId) {
        CancelRequest cancelRequest = cancelRequestRepository.findByBorrowRequestId(borrowRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.CANCEL_REQUEST_NOT_FOUND));
        return cancelRequestMapper.toCancelRequestResponse(cancelRequest);
    }

    public List<CancelRequestResponse> getAllCancelRequests() {
        List<CancelRequest> cancelRequests = cancelRequestRepository.findAll();
        return cancelRequests.stream()
                .map(cancelRequestMapper::toCancelRequestResponse)
                .toList();
    }

    public List<CancelRequestResponse> getCancelRequestByUserId(@Param("userId") String userId) {
        List<CancelRequest> cancelRequests = cancelRequestRepository.findByUserId(userId);
        return cancelRequests.stream()
                .map(cancelRequestMapper::toCancelRequestResponse)
                .toList();
    }

    public void deleteCancelRequest(@Param("id") String id) {
        CancelRequest cancelRequest = cancelRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CANCEL_REQUEST_NOT_FOUND));
        cancelRequestRepository.delete(cancelRequest);
    }

    public CancelRequestResponse updateCancelRequest(@Param("id") String id, CancelRequestCreationRequest request) {
        CancelRequest cancelRequest = cancelRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CANCEL_REQUEST_NOT_FOUND));

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        BorrowRequest borrowRequest = borrowRequestRepository.findById(request.getBorrowRequestId())
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_REQUEST_NOT_FOUND));

        cancelRequest.setUser(user);
        cancelRequest.setBorrowRequest(borrowRequest);
        cancelRequestMapper.updateCancelRequest(cancelRequest, request);

        return cancelRequestMapper.toCancelRequestResponse(cancelRequestRepository.save(cancelRequest));
    }

}
