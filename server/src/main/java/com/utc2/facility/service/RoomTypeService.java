package com.utc2.facility.service;

import com.utc2.facility.dto.request.RoomTypeCreationRequest;
import com.utc2.facility.dto.request.RoomTypeUpdateRequest;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.dto.response.RoomTypeResponse;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.RoomType;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.RoomMapper;
import com.utc2.facility.mapper.RoomTypeMapper;
import com.utc2.facility.repository.RoomRepository;
import com.utc2.facility.repository.RoomTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomTypeService {

    RoomTypeRepository roomTypeRepository;
    RoomTypeMapper roomTypeMapper;
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public RoomTypeResponse createRoomType(RoomTypeCreationRequest request) {
        log.info("Creating new room type with name: {}", request.getName());
        if (roomTypeRepository.existsByName(request.getName())) {
            log.warn("Room type with name {} already exists.", request.getName());
            throw new AppException(ErrorCode.ROOM_TYPE_EXISTED);
        }

        // Map các trường cơ bản từ DTO
        RoomType roomType = roomTypeMapper.toRoomType(request);

        RoomType savedRoomType = roomTypeRepository.save(roomType);
        log.info("Room type created with ID: {}", savedRoomType.getId());

        return buildFullRoomTypeResponse(savedRoomType);
    }
    
    @PreAuthorize("isAuthenticated()")
    public RoomTypeResponse getRoomTypeById(String id) {
        log.debug("Fetching room type by ID: {}", id);
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        return buildFullRoomTypeResponse(roomType);
    }

    @PreAuthorize("isAuthenticated()")
    public List<RoomTypeResponse> getAllRoomTypes() {
        log.debug("Fetching all room types");
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        return roomTypes.stream()
                .map(this::buildFullRoomTypeResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')") 
    public void deleteRoomType(String id) {
        log.warn("Attempting to delete room type with ID: {}", id);
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        if (roomRepository.existsByRoomType_Id(id)) {
            log.error("Cannot delete room type {} as it is still in use by some rooms.", id);
            throw new AppException(ErrorCode.ROOM_TYPE_IN_USE);
        }

        roomTypeRepository.delete(roomType);
        log.info("Room type deleted successfully: {}", id);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public RoomTypeResponse updateRoomType(String id, RoomTypeUpdateRequest request) {
        log.info("Updating room type with ID: {}", id);
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        if (StringUtils.hasText(request.getName()) && !request.getName().equals(roomType.getName())) {
            if (roomTypeRepository.existsByNameAndIdNot(request.getName(), id)) {
                log.warn("Cannot update room type {}: name {} already exists.", id, request.getName());
                throw new AppException(ErrorCode.ROOM_TYPE_EXISTED);
            }
        }

        roomTypeMapper.updateRoomType(roomType, request);
        RoomType updatedRoomType = roomTypeRepository.save(roomType);
        log.info("Room type updated successfully: {}", updatedRoomType.getId());
        return buildFullRoomTypeResponse(updatedRoomType);
    }

    // Helper xây dựng response đầy đủ
    private RoomTypeResponse buildFullRoomTypeResponse(RoomType roomType) {
        // 1. Dùng mapper map các trường cơ bản của Room
        RoomTypeResponse response = roomTypeMapper.toRoomTypeResponse(roomType);

        // 2. Fetch và map room
        List<Room> rooms = roomRepository.findByRoomType_Id(roomType.getId());
        List<RoomResponse> roomResponses = rooms.stream()
                .map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
        response.setRoomList(roomResponses);

        return response;
    }
}
