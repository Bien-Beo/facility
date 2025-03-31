package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.BorrowEquipmentCreationRequest;
import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.response.BorrowEquipmentResponse;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.service.BorrowEquipmentService;
import com.utc2.facility.service.BorrowRequestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/borrow-equipment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BorrowEquipmentController {
    BorrowEquipmentService borrowEquipmentService;

    @PostMapping
    ApiResponse<BorrowEquipmentResponse> createBorrowEquipment(@RequestBody @Valid BorrowEquipmentCreationRequest request) {
        return ApiResponse.<BorrowEquipmentResponse>builder()
                .result(borrowEquipmentService.createBorrowEquipment(request))
                .build();
    }

    @GetMapping("/{borrowEquipmentId}")
    ApiResponse<BorrowEquipmentResponse> getBorrowEquipment(@PathVariable String borrowEquipmentId) {
        return ApiResponse.<BorrowEquipmentResponse>builder()
                .result(borrowEquipmentService.getBorrowEquipment(borrowEquipmentId))
                .build();
    }

    @GetMapping("/request/{borrowRequestId}")
    ApiResponse<List<BorrowEquipmentResponse>> getAllBorrowEquipmentByBorrowRequest(
            @PathVariable String borrowRequestId) {
        return ApiResponse.<List<BorrowEquipmentResponse>>builder()
                .result(borrowEquipmentService.getBorrowEquipmentByBorrowRequestId(borrowRequestId))
                .build();
    }

    @GetMapping
    ApiResponse<List<BorrowEquipmentResponse>> getAllBorrowEquipment() {
        return ApiResponse.<List<BorrowEquipmentResponse>>builder()
                .result(borrowEquipmentService.getAllBorrowEquipments())
                .build();
    }

    @DeleteMapping("/{borrowEquipmentId}")
    ApiResponse<Void> deleteBorrowEquipment(@PathVariable String borrowEquipmentId) {
        borrowEquipmentService.deleteBorrowEquipment(borrowEquipmentId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{borrowEquipmentId}")
    ApiResponse<BorrowEquipmentResponse> updateBorrowEquipment(@RequestBody @Valid BorrowEquipmentCreationRequest request, @PathVariable String borrowEquipmentId)  {
        return ApiResponse.<BorrowEquipmentResponse>builder()
                .result(borrowEquipmentService.updateBorrowEquipment(borrowEquipmentId, request))
                .build();
    }
}
