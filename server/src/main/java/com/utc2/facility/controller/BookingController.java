package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.BookingCreationRequest;
import com.utc2.facility.dto.request.BookingUpdateRequest;
import com.utc2.facility.dto.response.BookingResponse;
import com.utc2.facility.service.BookingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookingController {
    BookingService bookingService;

    @PostMapping
    ApiResponse<BookingResponse> createBooking(@RequestBody @Valid BookingCreationRequest request) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.createBooking(request))
                .build();
    }

    @GetMapping("/{bookingId}")
    ApiResponse<BookingResponse> getBooking(@PathVariable String bookingId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBooking(bookingId))
                .build();
    }

    @GetMapping
    ApiResponse<Page<BookingResponse>> getAllBooking(Pageable pageable) {
        return ApiResponse.<Page<BookingResponse>>builder()
                .result(bookingService.getAllBookings(pageable))
                .build();
    }

    @GetMapping("/user/{userId}")
    ApiResponse<Page<BookingResponse>> getBookingByUserId(@PathVariable String userId, Pageable pageable) {
        return ApiResponse.<Page<BookingResponse>>builder()
                .result(bookingService.getBookingsByUserId(userId, pageable))
                .build();
    }

    @DeleteMapping("/{bookingId}")
    ApiResponse<Void> deleteBooking(@PathVariable String bookingId) {
        bookingService.deleteBooking(bookingId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{bookingId}")
    ApiResponse<BookingResponse> updateBooking(@RequestBody @Valid BookingUpdateRequest request,
                                               @PathVariable String bookingId)  {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.updateBooking(bookingId, request))
                .build();
    }

    @PutMapping("/{bookingId}/approve")
    ApiResponse<BookingResponse> approveBooking(@PathVariable String bookingId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.approveBooking(bookingId))
                .build();
    }

    @PutMapping("/{bookingId}/reject")
    ApiResponse<BookingResponse> rejectBooking(@PathVariable String bookingId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.rejectBooking(bookingId))
                .build();
    }

    @PutMapping("/{bookingId}/complete") // Đổi path
    ApiResponse<BookingResponse> completeBooking(@PathVariable String bookingId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.completeBooking(bookingId))
                .build();
    }
}