package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.request.RoomUpdateRequest;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoomController {

    RoomService roomService;

    @PostMapping
    ApiResponse<RoomResponse> createRoom(@RequestBody @Valid RoomCreationRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.createRoom(request))
                .build();
    }

    @GetMapping("/{roomName}")
    ApiResponse<RoomResponse> getRoom(@PathVariable String roomName) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.getRoomByName(roomName))
                .build();
    }

    @GetMapping
    ApiResponse<Page<RoomResponse>> getRooms(Pageable pageable) {
        return ApiResponse.<Page<RoomResponse>>builder()
                .result(roomService.getRooms(pageable))
                .build();
    }

    @DeleteMapping("/{roomName}")
    ApiResponse<Void> deleteRoom(@PathVariable String roomName) {
        roomService.deleteRoom(roomName);
        return ApiResponse.<Void>builder().build();
    }

    @PatchMapping("/{roomName}")
    ApiResponse<RoomResponse> updateRoom(
            @PathVariable String roomName,
            @RequestBody @Valid RoomUpdateRequest request
    ) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoom(roomName, request))
                .build();
    }
}