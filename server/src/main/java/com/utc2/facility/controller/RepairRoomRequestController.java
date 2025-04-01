package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.CancelRequestCreationRequest;
import com.utc2.facility.dto.request.CancelRequestUpdateRequest;
import com.utc2.facility.dto.request.RepairRequestCreationRequest;
import com.utc2.facility.dto.response.CancelRequestResponse;
import com.utc2.facility.dto.response.RepairRequestResponse;
import com.utc2.facility.service.CancelRequestService;
import com.utc2.facility.service.RepairRequestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repair-request")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RepairRequestController {
    RepairRequestService repairRequestService;

    @PostMapping
    ApiResponse<RepairRequestResponse> createRepairRequest(@RequestBody @Valid RepairRequestCreationRequest request) {
        return ApiResponse.<RepairRequestResponse>builder()
                .result(repairRequestService.createRepairRequest(request))
                .build();
    }

    @GetMapping("/{repairRequestId}")
    ApiResponse<RepairRequestResponse> getRepairRequest(@PathVariable String repairRequestId) {
        return ApiResponse.<RepairRequestResponse>builder()
                .result(repairRequestService.getRepairRequest(repairRequestId))
                .build();
    }

    @GetMapping("/user/{userId}")
    ApiResponse<List<RepairRequestResponse>> getRepairRequestByUserId(@PathVariable String userId) {
        return ApiResponse.<List<RepairRequestResponse>>builder()
                .result(repairRequestService.getRepairRequestByUserId(userId))
                .build();
    }

    @GetMapping("/room/{roomName}")
    ApiResponse<RepairRequestResponse> getRepairRequestByRoomName(@PathVariable String roomName) {
        return ApiResponse.<RepairRequestResponse>builder()
                .result(repairRequestService.getRepairRequestByRoomName(roomName))
                .build();
    }

    @GetMapping
    ApiResponse<List<RepairRequestResponse>> getAllRepairRequest() {
        return ApiResponse.<List<RepairRequestResponse>>builder()
                .result(repairRequestService.getAllRepairRequests())
                .build();
    }

    @DeleteMapping("/{repairRequestId}")
    ApiResponse<Void> deleteRepairRequest(@PathVariable String repairRequestId) {
        repairRequestService.deleteRepairRequest(repairRequestId);
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
