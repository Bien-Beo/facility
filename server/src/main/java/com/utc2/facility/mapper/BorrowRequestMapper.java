package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.entity.BorrowRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BorrowRequestMapper {
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "roomName", source = "room.name")
    BorrowRequestResponse toBorrowRequestResponse(BorrowRequest borrowRequest);

    BorrowRequest toBorrowRequest(BorrowRequestCreationRequest request);

    @Mapping(target = "room", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateBorrowRequest(@MappingTarget BorrowRequest borrowRequest, BorrowRequestCreationRequest request);
}