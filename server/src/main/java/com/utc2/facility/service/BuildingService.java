package com.utc2.facility.service;

import com.utc2.facility.dto.request.BuildingCreationRequest;
import com.utc2.facility.dto.request.BuildingUpdateRequest;
import com.utc2.facility.dto.response.BuildingResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.BuildingMapper;
import com.utc2.facility.mapper.RoomMapper;
import com.utc2.facility.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuildingService {

    BuildingRepository buildingRepository;
    BuildingMapper buildingMapper;
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public BuildingResponse createBuilding(BuildingCreationRequest request) {
        if (buildingRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.BUILDING_EXISTED);
        }

        // Map các trường cơ bản từ DTO
        Building building = buildingMapper.toBuilding(request);

        Building savedBuilding = buildingRepository.save(building);

        return buildFullBuildingResponse(savedBuilding);
    }
    
    @PreAuthorize("isAuthenticated()")
    public BuildingResponse getBuildingById(String id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        return buildFullBuildingResponse(building);
    }

    @PreAuthorize("isAuthenticated()")
    public Page<BuildingResponse> getBuildingsResponse(Pageable pageable) {
        Page<Building> buildingPage = buildingRepository.findAll(pageable);
        return buildingPage.map(this::buildFullBuildingResponse);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')") 
    public void deleteBuilding(String id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        buildingRepository.delete(building);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public BuildingResponse updateBuilding(String id, BuildingUpdateRequest request) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        buildingMapper.updateBuilding(building, request);
        Building updatedBuilding = buildingRepository.save(building);
        return buildFullBuildingResponse(updatedBuilding);
    }

    // Helper xây dựng response đầy đủ
    private BuildingResponse buildFullBuildingResponse(Building building) {
        // 1. Dùng mapper map các trường cơ bản của Room
        BuildingResponse response = buildingMapper.toBuildingResponse(building);

        // 2. Fetch và map room
        List<Room> rooms = roomRepository.findByBuilding_Id(building.getId());
        List<RoomResponse> roomResponses = rooms.stream()
                .map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
        response.setRoomList(roomResponses);

        return response;
    }
}
