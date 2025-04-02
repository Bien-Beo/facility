package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.EquipmentCreationRequest;
import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EquipmentController {
    EquipmentService equipmentService;
//
    @PostMapping
    ApiResponse<EquipmentResponse> createEquipment(@RequestBody @Valid EquipmentCreationRequest request) {
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.createEquipment(request))
                .build();
    }

    @GetMapping("/{equipmentName}")
    ApiResponse<EquipmentResponse> getEquipment(@PathVariable String equipmentName) {
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.getEquipmentByName(equipmentName))
                .build();
    }

    @GetMapping
    ApiResponse<List<EquipmentResponse>> getEquipments() {
        return ApiResponse.<List<EquipmentResponse>>builder()
                .result(equipmentService.getEquipments())
                .build();
    }

    @DeleteMapping("/{slug}")
    ApiResponse<Void> deleteEquipment(@PathVariable String slug) {
        equipmentService.deleteEquipment(slug);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @PutMapping("/{slug}")
    ApiResponse<EquipmentResponse> updateEquipment(@RequestBody @Valid EquipmentCreationRequest request, @PathVariable String slug)  {
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.updateEquipment(request, slug))
                .build();
    }
}
