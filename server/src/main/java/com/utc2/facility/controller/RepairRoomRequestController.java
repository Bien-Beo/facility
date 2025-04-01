package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.RepairRoomRequestCreationRequest;
import com.utc2.facility.dto.response.RepairRoomRequestResponse;
import com.utc2.facility.service.RepairRoomRequestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repair-room-request")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RepairRoomRequestController {
    RepairRoomRequestService repairRoomRequestService;

    @PostMapping
    ApiResponse<RepairRoomRequestResponse> createRepairRoomRequest(@RequestBody @Valid RepairRoomRequestCreationRequest request) {
        return ApiResponse.<RepairRoomRequestResponse>builder()
                .result(repairRoomRequestService.createRepairRoomRequest(request))
                .build();
    }

    @GetMapping("/{repairRoomRequestId}")
    ApiResponse<RepairRoomRequestResponse> getRepairRoomRequest(@PathVariable String repairRoomRequestId) {
        return ApiResponse.<RepairRoomRequestResponse>builder()
                .result(repairRoomRequestService.getRepairRoomRequest(repairRoomRequestId))
                .build();
    }

    @GetMapping("/user/{userId}")
    ApiResponse<List<RepairRoomRequestResponse>> getRepairRoomRequestByUserId(@PathVariable String userId) {
        return ApiResponse.<List<RepairRoomRequestResponse>>builder()
                .result(repairRoomRequestService.getRepairRoomRequestByUserId(userId))
                .build();
    }

    @GetMapping("/room/{roomName}")
    ApiResponse<RepairRoomRequestResponse> getRepairRoomRequestByRoomName(@PathVariable String roomName) {
        return ApiResponse.<RepairRoomRequestResponse>builder()
                .result(repairRoomRequestService.getRepairRoomRequestByRoomName(roomName))
                .build();
    }

    @GetMapping
    ApiResponse<List<RepairRoomRequestResponse>> getAllRepairRoomRequest() {
        return ApiResponse.<List<RepairRoomRequestResponse>>builder()
                .result(repairRoomRequestService.getAllRepairRoomRequests())
                .build();
    }

    @DeleteMapping("/{repairRoomRequestId}")
    ApiResponse<Void> deleteRepairRoomRequest(@PathVariable String repairRoomRequestId) {
        repairRoomRequestService.deleteRepairRoomRequest(repairRoomRequestId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

//    @PutMapping("/{cancelRequestId}")
//    ApiResponse<CancelRequestResponse> updateCancelRequest(@RequestBody @Valid CancelRequestUpdateRequest request, @PathVariable String cancelRequestId)  {
//        return ApiResponse.<CancelRequestResponse>builder()
//                .result(cancelRequestService.updateCancelRequest(cancelRequestId, request))
//                .build();
//    }
}
