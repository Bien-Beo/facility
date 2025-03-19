package com.utc2.facility.service;

import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.dto.response.UserResponse;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.RoomType;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.RoomMapper;
import com.utc2.facility.repository.RoomRepository;
import com.utc2.facility.repository.RoomTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomRepository roomRepository;
    RoomTypeRepository roomTypeRepository;
    RoomMapper roomMapper;

    public RoomResponse createRoom(RoomCreationRequest request) {
        if (roomRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.ROOM_EXISTED);

        RoomType roomType = roomTypeRepository.findByName(request.getNameTypeRoom())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        Room room = roomMapper.toRoom(request);
        room.setRoomType(roomType);

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    public RoomResponse getRoomByName(String name) {
        return roomMapper.toRoomResponse(roomRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Room not found")));
    }

    public List<RoomResponse> getRooms() {
        return roomRepository.findAll().stream().map(roomMapper::toRoomResponse).toList();
    }

}
