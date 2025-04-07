package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.BookingEquipmentCreationRequest;
import com.utc2.facility.dto.response.BookingEquipmentResponse;
import com.utc2.facility.service.BookingEquipmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/booking-equipment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookingEquipmentController {
    BookingEquipmentService bookingEquipmentService;

    @PostMapping
    ApiResponse<BookingEquipmentResponse> createBookingEquipment(@RequestBody @Valid BookingEquipmentCreationRequest request) {
        return ApiResponse.<BookingEquipmentResponse>builder()
                .result(bookingEquipmentService.createBookingEquipment(request))
                .build();
    }

//    @GetMapping("/{bookingEquipmentId}")
//    ApiResponse<BookingEquipmentResponse> getBookingEquipment(@PathVariable String bookingEquipmentId) {
//        return ApiResponse.<BookingEquipmentResponse>builder()
//                .result(bookingEquipmentService.getBookingEquipment(bookingEquipmentId))
//                .build();
//    }

    @GetMapping("/booking/{bookingId}")
    ApiResponse<List<BookingEquipmentResponse>> getAllBookingEquipmentByBooking(
            @PathVariable String bookingId) {
        return ApiResponse.<List<BookingEquipmentResponse>>builder()
                .result(bookingEquipmentService.getBookingEquipmentByBookingId(bookingId))
                .build();
    }

    @GetMapping
    ApiResponse<List<BookingEquipmentResponse>> getAllBookingEquipment() {
        return ApiResponse.<List<BookingEquipmentResponse>>builder()
                .result(bookingEquipmentService.getAllBookingEquipments())
                .build();
    }

//    @DeleteMapping("/{bookingEquipmentId}")
//    ApiResponse<Void> deleteBookingEquipment(@PathVariable String bookingEquipmentId) {
//        bookingEquipmentService.deleteBookingEquipment(bookingEquipmentId);
//        return ApiResponse.<Void>builder()
//                .result(null)
//                .build();
//    }

//    @PutMapping("/{borrowEquipmentId}")
//    ApiResponse<BookingEquipmentResponse> updateBorrowEquipment(@RequestBody @Valid BookingEquipmentCreationRequest request, @PathVariable String borrowEquipmentId)  {
//        return ApiResponse.<BookingEquipmentResponse>builder()
//                .result(bookingEquipmentService.updateBorrowEquipment(borrowEquipmentId, request))
//                .build();
//    }
}
