package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminController {

    RoomService roomService;

    @GetMapping("/rooms")
    ApiResponse<Page<RoomResponse>> getRooms(
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String roomTypeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String userId,
            Pageable pageable) {
        {
            return ApiResponse.<Page<RoomResponse>>builder()
                    .result(roomService.getRooms(buildingId, roomTypeId, year, userId, pageable))
                    .build();
        }
    }
}