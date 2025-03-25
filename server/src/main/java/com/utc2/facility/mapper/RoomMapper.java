package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.RoomType;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.repository.RoomTypeRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "roomTypeName", source = "room.roomType.name")
    RoomResponse toRoomResponse(Room room);

    Room toRoom(RoomCreationRequest request);
}
