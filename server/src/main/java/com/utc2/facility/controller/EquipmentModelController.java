package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.EquipmentItemCreationRequest;
import com.utc2.facility.dto.request.EquipmentItemUpdateRequest;
import com.utc2.facility.dto.response.EquipmentModelResponse;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.service.EquipmentModelService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/models")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EquipmentModelController {
    EquipmentModelService equipmentModelService;

    @GetMapping
    ApiResponse<Page<EquipmentModelResponse>> getEquipmentModels(Pageable pageable) {
        return ApiResponse.<Page<EquipmentModelResponse>>builder()
                .result(equipmentModelService.getEquipmentModels(pageable))
                .build();
    }
}
