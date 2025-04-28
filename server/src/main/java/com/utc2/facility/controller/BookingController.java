package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.BookingCreationRequest;
import com.utc2.facility.dto.request.BookingUpdateRequest;
import com.utc2.facility.dto.request.CancelBookingRequest;
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

import java.util.List;

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
    ApiResponse<Page<BookingResponse>> getAllBooking(
            @RequestParam(required = false) String roomId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String userId,
            Pageable pageable
    ) {
        return ApiResponse.<Page<BookingResponse>>builder()
                .result(bookingService.getAllBookings(roomId, month, year, userId, pageable))
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<Page<BookingResponse>> getMyBookings(Pageable pageable) {
        Page<BookingResponse> resultPage = bookingService.getMyBookings(pageable);

        return ApiResponse.<Page<BookingResponse>>builder()
                .result(resultPage)
                .build();
    }

    @GetMapping("/room/{roomId}")
    ApiResponse<Page<BookingResponse>> getBookingByRoomId(@PathVariable String roomId, Pageable pageable) {
        return ApiResponse.<Page<BookingResponse>>builder()
                .result(bookingService.getBookingsByRoomId(roomId, pageable))
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
    ApiResponse<BookingResponse> updatePendingBookingDetails(@RequestBody @Valid BookingUpdateRequest request,
                                                             @PathVariable String bookingId)  {
        log.info("Update booking with ID: {}", bookingId);
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.updatePendingBookingDetails(bookingId, request))
                .build();
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelBooking(@PathVariable String id, @RequestBody(required = false) CancelBookingRequest request) {
        bookingService.cancelBookingByUser(id, request);
        return ApiResponse.<Void>builder().build();
    }

    @PutMapping("/{bookingId}/checkout")
    ApiResponse<BookingResponse> checkOut(@PathVariable String bookingId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.checkOutBooking(bookingId))
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