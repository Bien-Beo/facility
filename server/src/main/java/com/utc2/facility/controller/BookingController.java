package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.BookingCreationRequest;
import com.utc2.facility.dto.response.BookingResponse;
import com.utc2.facility.service.BookingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//
@RestController
@RequestMapping("/borrow-request")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookingRequestController {
    BookingService bookingService;

    @PostMapping
    ApiResponse<BookingResponse> createBorrowRequest(@RequestBody @Valid BookingCreationRequest request) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.createBorrowRequest(request))
                .build();
    }

    @GetMapping("/{borrowRequestId}")
    ApiResponse<BookingResponse> getBorrowRequest(@PathVariable String borrowRequestId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBorrowRequest(borrowRequestId))
                .build();
    }

    @GetMapping
    ApiResponse<List<BookingResponse>> getAllBorrowRequest() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getAllBorrowRequests())
                .build();
    }

    @GetMapping("/user/{userId}")
    ApiResponse<List<BookingResponse>> getBorrowRequestByUserId(@PathVariable String userId) {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getBorrowRequestByUserId(userId))
                .build();
    }

    @DeleteMapping("/{borrowRequestId}")
    ApiResponse<Void> deleteBorrowRequest(@PathVariable String borrowRequestId) {
        bookingService.deleteBorrowRequest(borrowRequestId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{borrowRequestId}")
    ApiResponse<BookingResponse> updateBorrowRequest(@RequestBody @Valid BookingCreationRequest request, @PathVariable String borrowRequestId)  {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.updateBorrowRequest(borrowRequestId, request))
                .build();
    }

    @PutMapping("/{borrowRequestId}/approve")
    ApiResponse<BookingResponse> approveBorrowRequest(@PathVariable String borrowRequestId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.approveBorrowRequest(borrowRequestId))
                .build();
    }

    @PutMapping("/{borrowRequestId}/reject")
    ApiResponse<BookingResponse> rejectBorrowRequest(@PathVariable String borrowRequestId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.rejectBorrowRequest(borrowRequestId))
                .build();
    }

    @PutMapping("/{borrowRequestId}/return")
    ApiResponse<BookingResponse> returnRoom(@PathVariable String borrowRequestId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.returnRoom(borrowRequestId))
                .build();
    }
}