package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.MaintenanceRequest;
import com.utc2.facility.dto.request.MaintenanceUpdate;
import com.utc2.facility.dto.response.MaintenanceResponse;
import com.utc2.facility.enums.MaintenanceStatus;
import com.utc2.facility.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maintenance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MaintenanceController {
    MaintenanceService maintenanceService;

    @PostMapping
    ApiResponse<MaintenanceResponse> createMaintenanceRequest(@RequestBody @Valid MaintenanceRequest request) {
        return ApiResponse.<MaintenanceResponse>builder()
                .result(maintenanceService.createMaintenanceRequest(request))
                .build();
    }

//    @GetMapping("/{maintenanceTicketId}")
//    ApiResponse<MaintenanceResponse> getRepairRoomRequest(@PathVariable String maintenanceTicketId) {
//        return ApiResponse.<MaintenanceResponse>builder()
//                .result(maintenanceService.getMaintenanceTicket(maintenanceTicketId))
//                .build();
//    }

    @GetMapping("/user/{userId}")
    ApiResponse<List<MaintenanceResponse>> getMaintenanceTicketsByUserId(@PathVariable String userId) {
        return ApiResponse.<List<MaintenanceResponse>>builder()
                .result(maintenanceService.getMaintenanceByReportedId(userId))
                .build();
    }

    @GetMapping("/room/{roomName}")
    ApiResponse<List<MaintenanceResponse>> getMaintenanceTicketsByRoomName(@PathVariable String roomName) {
        return ApiResponse.<List<MaintenanceResponse>>builder()
                .result(maintenanceService.getMaintenanceTicketsByRoomName(roomName))
                .build();
    }

    @GetMapping
    ApiResponse<Page<MaintenanceResponse>> getMaintenanceTickets(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> status
    ) {
        return ApiResponse.<Page<MaintenanceResponse>>builder()
                .result(maintenanceService.getMaintenanceTickets(page, size, status))
                .build();
    }

    @DeleteMapping("/{maintenanceTicketId}")
    ApiResponse<Void> deleteMaintenanceTicket(@PathVariable String maintenanceTicketId) {
        maintenanceService.deleteMaintenanceTicket(maintenanceTicketId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{maintenanceTicketId}")
    ApiResponse<MaintenanceResponse> updateMaintenanceTicket(@RequestBody @Valid MaintenanceUpdate request, @PathVariable String maintenanceTicketId)  {
        return ApiResponse.<MaintenanceResponse>builder()
                .result(maintenanceService.updateMaintenanceTicket(maintenanceTicketId, request))
                .build();
    }
}
