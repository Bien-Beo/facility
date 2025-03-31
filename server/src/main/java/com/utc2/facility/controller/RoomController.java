package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    ApiResponse<List<RoomResponse>> getRooms() {
        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.getRooms())
                .build();
    }

    @DeleteMapping("/{slug}")
    ApiResponse<Void> deleteRoom(@PathVariable String slug) {
        roomService.deleteRoom(slug);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{slug}")
    ApiResponse<RoomResponse> updateRoom(@RequestBody @Valid RoomCreationRequest request, @PathVariable String slug)  {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoom(request, slug))
                .build();
    }
}
