package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.BuildingCreationRequest;
import com.utc2.facility.dto.request.BuildingUpdateRequest;
import com.utc2.facility.dto.request.RoomUpdateRequest;
import com.utc2.facility.dto.response.BuildingResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.service.BuildingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BuildingController {

    BuildingService buildingService;

    @PostMapping
    ApiResponse<BuildingResponse> createBuilding(@RequestBody @Valid BuildingCreationRequest request) {
        return ApiResponse.<BuildingResponse>builder()
                .result(buildingService.createBuilding(request))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<BuildingResponse> getBuilding(@PathVariable String id) {
        return ApiResponse.<BuildingResponse>builder()
                .result(buildingService.getBuildingById(id))
                .build();
    }

    @GetMapping
    ApiResponse<Page<BuildingResponse>> getBuildings(Pageable pageable) {
        return ApiResponse.<Page<BuildingResponse>>builder()
                .result(buildingService.getBuildingsResponse(pageable))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteBuilding(@PathVariable String id) {
        buildingService.deleteBuilding(id);
        return ApiResponse.<Void>builder().build();
    }

    @PatchMapping("/{id}")
    ApiResponse<BuildingResponse> updateBuilding(
            @PathVariable String id,
            @RequestBody @Valid BuildingUpdateRequest request
    ) {
        return ApiResponse.<BuildingResponse>builder()
                .result(buildingService.updateBuilding(id, request))
                .build();
    }
}