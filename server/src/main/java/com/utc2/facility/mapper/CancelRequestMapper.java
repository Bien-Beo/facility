package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.CancelRequestCreationRequest;
import com.utc2.facility.dto.response.CancelRequestResponse;
import com.utc2.facility.entity.CancelRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CancelRequestMapper {
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "borrowRequestId", source = "borrowRequest.id")
    CancelRequestResponse toCancelRequestResponse(CancelRequest cancelRequest);

    CancelRequest toCancelRequest(CancelRequestCreationRequest request);

    @Mapping(target = "borrowRequest", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateCancelRequest(@MappingTarget CancelRequest cancelRequest, CancelRequestCreationRequest request);
}
