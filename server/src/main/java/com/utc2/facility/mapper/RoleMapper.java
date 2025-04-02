package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.PermissionRequest;
import com.utc2.facility.dto.request.RoleRequest;
import com.utc2.facility.dto.response.PermissionResponse;
import com.utc2.facility.dto.response.RoleResponse;
import com.utc2.facility.entity.Permission;
import com.utc2.facility.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {//
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
