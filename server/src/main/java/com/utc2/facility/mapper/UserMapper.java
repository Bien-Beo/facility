package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.UserCreationRequest;
import com.utc2.facility.dto.request.UserUpdateRequest;
import com.utc2.facility.dto.response.UserResponse;
import com.utc2.facility.entity.User;
import com.utc2.facility.enums.Role;
import com.utc2.facility.repository.RoleRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roleName", source = "role.name")
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "managedRooms", ignore = true)
    User toUser(UserCreationRequest request);

    // Mapper này cập nhật các trường cho phép từ UserUpdateRequest
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "managedRooms", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}

