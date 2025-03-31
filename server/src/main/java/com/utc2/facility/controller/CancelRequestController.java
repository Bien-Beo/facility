package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.request.CancelRequestCreationRequest;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.dto.response.CancelRequestResponse;
import com.utc2.facility.service.BorrowRequestService;
import com.utc2.facility.service.CancelRequestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow-request")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CancelRequestController {
    CancelRequestService cancelRequestService;

    @PostMapping
    ApiResponse<CancelRequestResponse> createCancelRequest(@RequestBody @Valid CancelRequestCreationRequest request) {
        return ApiResponse.<CancelRequestResponse>builder()
                .result(cancelRequestService.createCancelRequest(request))
                .build();
    }

    @GetMapping("/{cancelRequestId}")
    ApiResponse<CancelRequestResponse> getCancelRequest(@PathVariable String cancelRequestId) {
        return ApiResponse.<CancelRequestResponse>builder()
                .result(cancelRequestService.getCancelRequest(cancelRequestId))
                .build();
    }

    @GetMapping("/user/{userId}")
    ApiResponse<List<CancelRequestResponse>> getCancelRequestByUserId(@PathVariable String userId) {
        return ApiResponse.<List<CancelRequestResponse>>builder()
                .result(cancelRequestService.getCancelRequestByUserId(userId))
                .build();
    }

    @GetMapping("/borrow-request/{borrowRequestId}")
    ApiResponse<CancelRequestResponse> getCancelRequestByBorrowRequestId(@PathVariable String borrowRequestId) {
        return ApiResponse.<CancelRequestResponse>builder()
                .result(cancelRequestService.getCancelRequestByBorrowRequestId(borrowRequestId))
                .build();
    }

    @GetMapping
    ApiResponse<List<CancelRequestResponse>> getAllCancelRequest() {
        return ApiResponse.<List<CancelRequestResponse>>builder()
                .result(cancelRequestService.getAllCancelRequests())
                .build();
    }

    @DeleteMapping("/{cancelRequestId}")
    ApiResponse<Void> deleteCancelRequest(@PathVariable String cancelRequestId) {
        cancelRequestService.deleteCancelRequest(cancelRequestId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{borrowRequestId}")
    ApiResponse<CancelRequestResponse> updateCancelRequest(@RequestBody @Valid CancelRequestCreationRequest request, @PathVariable String cancelRequestId)  {
        return ApiResponse.<CancelRequestResponse>builder()
                .result(cancelRequestService.updateCancelRequest(cancelRequestId, request))
                .build();
    }
}
