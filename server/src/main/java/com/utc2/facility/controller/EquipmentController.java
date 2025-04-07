package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.EquipmentItemCreationRequest;
import com.utc2.facility.dto.request.EquipmentItemUpdateRequest;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/equipments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EquipmentController {
    EquipmentService equipmentService;

    @PostMapping
    ApiResponse<EquipmentResponse> createEquipment(@RequestBody @Valid EquipmentItemCreationRequest request) {
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.createEquipmentItem(request))
                .build();
    }

    @GetMapping("/{equipmentItemId}")
    ApiResponse<EquipmentResponse> getEquipment(@PathVariable String equipmentItemId) {
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.getEquipmentItemById(equipmentItemId))
                .build();
    }

    @GetMapping
    ApiResponse<Page<EquipmentResponse>> getEquipmentItems(Pageable pageable) {
        return ApiResponse.<Page<EquipmentResponse>>builder()
                .result(equipmentService.getEquipmentItems(pageable))
                .build();
    }

    @DeleteMapping("/{itemId}")
    ApiResponse<Void> deleteEquipment(@PathVariable String itemId) {
        equipmentService.deleteEquipmentItem(itemId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{itemId}")
    ApiResponse<EquipmentResponse> updateEquipmentItem(
            @PathVariable String itemId,
            @RequestBody @Valid EquipmentItemUpdateRequest request
    ) {
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.updateEquipmentItem(itemId, request))
                .build();
    }
}
