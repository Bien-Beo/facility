package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.request.EquipmentCreationRequest;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.service.BorrowRequestService;
import com.utc2.facility.service.EquipmentService;
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
public class BorrowRequestController {
    BorrowRequestService borrowRequestService;

    @PostMapping
    ApiResponse<BorrowRequestResponse> createBorrowRequest(@RequestBody @Valid BorrowRequestCreationRequest request) {
        return ApiResponse.<BorrowRequestResponse>builder()
                .result(borrowRequestService.createBorrowRequest(request))
                .build();
    }

    @GetMapping("/{borrowRequestId}")
    ApiResponse<BorrowRequestResponse> getBorrowRequest(@PathVariable String borrowRequestId) {
        return ApiResponse.<BorrowRequestResponse>builder()
                .result(borrowRequestService.getBorrowRequest(borrowRequestId))
                .build();
    }

    @GetMapping
    ApiResponse<List<BorrowRequestResponse>> getAllBorrowRequest() {
        return ApiResponse.<List<BorrowRequestResponse>>builder()
                .result(borrowRequestService.getAllBorrowRequests())
                .build();
    }

    @DeleteMapping("/{borrowRequestId}")
    ApiResponse<Void> deleteEquipment(@PathVariable String borrowRequestId) {
        borrowRequestService.deleteBorrowRequest(borrowRequestId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{borrowRequestId}")
    ApiResponse<BorrowRequestResponse> updateBorrowRequest(@RequestBody @Valid BorrowRequestCreationRequest request, @PathVariable String borrowRequestId)  {
        return ApiResponse.<BorrowRequestResponse>builder()
                .result(borrowRequestService.updateBorrowRequest(borrowRequestId, request))
                .build();
    }
}
