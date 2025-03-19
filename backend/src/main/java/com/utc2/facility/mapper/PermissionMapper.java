package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.PermissionRequest;
import com.utc2.facility.dto.response.PermissionResponse;
import com.utc2.facility.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
