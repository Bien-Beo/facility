package com.utc2.facility.service;

import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.dto.response.UserResponse;
import com.utc2.facility.entity.Building;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.RoomType;
import com.utc2.facility.entity.User;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.RoomMapper;
import com.utc2.facility.repository.BuildingRepository;
import com.utc2.facility.repository.RoomRepository;
import com.utc2.facility.repository.RoomTypeRepository;
import com.utc2.facility.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomRepository roomRepository;
    RoomTypeRepository roomTypeRepository;
    BuildingRepository buildingRepository;
    UserRepository userRepository;
    RoomMapper roomMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse createRoom(RoomCreationRequest request) {
        if (roomRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.ROOM_EXISTED);

        Building building = buildingRepository.findByName(request.getBuildingName())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        RoomType roomType = roomTypeRepository.findByName(request.getNameTypeRoom())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        User facilityManager = userRepository.findByUserId(request.getFacilityManagerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Room room = roomMapper.toRoom(request);
        room.setBuilding(building);
        room.setRoomType(roomType);
        room.setFacilityManager(facilityManager);

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    public RoomResponse getRoomByName(String name) {
        return roomMapper.toRoomResponse(roomRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND)));
    }

    public List<RoomResponse> getRooms() {
        return roomRepository.findAll().stream().map(roomMapper::toRoomResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoom(String slug) {
        Room room = roomRepository.findByName(slug)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        roomRepository.delete(room);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse updateRoom(RoomCreationRequest request, String slug) {
        Room room = roomRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        roomMapper.updateRoom(room, request);

        if (request.getBuildingName() != null) {
            Building building = buildingRepository.findByName(request.getBuildingName())
                    .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
            room.setBuilding(building);
        }

        if (request.getNameTypeRoom() != null) {
            RoomType roomType = roomTypeRepository.findByName(request.getNameTypeRoom())
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
            room.setRoomType(roomType);
        }

        if (request.getFacilityManagerId() != null) {
            User facilityManager = userRepository.findByUserId(request.getFacilityManagerId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            room.setFacilityManager(facilityManager);
        }

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

}
