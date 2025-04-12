package com.utc2.facility.controller;

import com.utc2.facility.dto.request.*;
import com.utc2.facility.dto.response.BuildingResponse;
import com.utc2.facility.dto.response.RoomTypeResponse;
import com.utc2.facility.service.RoomTypeService;
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
@RequestMapping("/roomtypes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoomTypeController {

    RoomTypeService roomTypeService;

    @PostMapping
    ApiResponse<RoomTypeResponse> createRoomType(@RequestBody @Valid RoomTypeCreationRequest request) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.createRoomType(request))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<RoomTypeResponse> getRoomType(@PathVariable String id) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.getRoomTypeById(id))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoomTypeResponse>> getRoomTypes() {
        return ApiResponse.<List<RoomTypeResponse>>builder()
                .result(roomTypeService.getAllRoomTypes())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteRoomType(@PathVariable String id) {
        roomTypeService.deleteRoomType(id);
        return ApiResponse.<Void>builder().build();
    }

    @PatchMapping("/{id}")
    ApiResponse<RoomTypeResponse> updateRoomType(
            @PathVariable String id,
            @RequestBody @Valid RoomTypeUpdateRequest request
    ) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.updateRoomType(id, request))
                .build();
    }
}