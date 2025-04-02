package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.RepairEquipmentRequestCreationRequest;
import com.utc2.facility.dto.request.RepairRoomRequestCreationRequest;
import com.utc2.facility.dto.response.RepairEquipmentRequestResponse;
import com.utc2.facility.dto.response.RepairRoomRequestResponse;
import com.utc2.facility.service.RepairEquipmentRequestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repair-equipment-request")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RepairEquipmentRequestController {
    RepairEquipmentRequestService repairEquipmentRequestService;

    @PostMapping
    ApiResponse<RepairEquipmentRequestResponse> createRepairEquipmentRequest(@RequestBody @Valid RepairEquipmentRequestCreationRequest request) {
        return ApiResponse.<RepairEquipmentRequestResponse>builder()
                .result(repairEquipmentRequestService.createRepairEquipmentRequest(request))
                .build();
    }

    @GetMapping("/{repairEquipmentRequestId}")
    ApiResponse<RepairEquipmentRequestResponse> getRepairEquipmentRequest(@PathVariable String repairEquipmentRequestId) {
        return ApiResponse.<RepairEquipmentRequestResponse>builder()
                .result(repairEquipmentRequestService.getRepairEquipmentRequest(repairEquipmentRequestId))
                .build();
    }

    @GetMapping("/user/{userId}")
    ApiResponse<List<RepairEquipmentRequestResponse>> getRepairEquipmentRequestByUserId(@PathVariable String userId) {
        return ApiResponse.<List<RepairEquipmentRequestResponse>>builder()
                .result(repairEquipmentRequestService.getRepairEquipmentRequestByUserId(userId))
                .build();
    }

    @GetMapping
    ApiResponse<List<RepairEquipmentRequestResponse>> getAllRepairEquipmentRequest() {
        return ApiResponse.<List<RepairEquipmentRequestResponse>>builder()
                .result(repairEquipmentRequestService.getAllRepairEquipmentRequests())
                .build();
    }

    @DeleteMapping("/{repairEquipmentRequestId}")
    ApiResponse<Void> deleteRepairEquipmentRequest(@PathVariable String repairEquipmentRequestId) {
        repairEquipmentRequestService.deleteRepairEquipmentRequest(repairEquipmentRequestId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{cancelRequestId}")
    ApiResponse<CancelRequestResponse> updateCancelRequest(@RequestBody @Valid CancelRequestUpdateRequest request, @PathVariable String cancelRequestId)  {
        return ApiResponse.<CancelRequestResponse>builder()
                .result(cancelRequestService.updateCancelRequest(cancelRequestId, request))
                .build();
    }
}
