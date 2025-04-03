package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.UserCreationRequest;
import com.utc2.facility.dto.request.UserUpdateRequest;
import com.utc2.facility.dto.response.UserResponse;
import com.utc2.facility.entity.User;
import com.utc2.facility.enums.Role;
import com.utc2.facility.repository.RoleRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
    User toUser(UserCreationRequest request, @Context RoleRepository roleRepository);

    @Mapping(target = "role", source = "role.name") // Chuyển Role entity thành String
    UserResponse toUserResponse(User user);

    @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
    void updateUser(@MappingTarget User user, UserUpdateRequest request, @Context RoleRepository roleRepository);

    @Named("mapRole")
    default com.utc2.facility.entity.Role mapRole(Role roleName, @Context RoleRepository roleRepository) {
        return roleRepository.findByName(roleName);
    }
}

