package com.utc2.facility.service;

import com.utc2.facility.dto.request.UserCreationRequest;
import com.utc2.facility.dto.request.UserUpdateRequest; // Import DTO đúng
import com.utc2.facility.dto.response.UserResponse;
import com.utc2.facility.entity.Role; // Import Role entity
import com.utc2.facility.entity.User;
// import com.utc2.facility.enums.Role; // Không dùng trực tiếp Enum Role ở đây nữa nếu có Role Entity
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.UserMapper;
import com.utc2.facility.repository.RoleRepository;
import com.utc2.facility.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(UserCreationRequest request) {
        // 1. Kiểm tra trùng lặp các trường unique
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = userMapper.toUser(request);

        String requestedRoleName = request.getRoleName() != null ? request.getRoleName() : "USER";
        Role userRole = roleRepository.findByName(com.utc2.facility.enums.Role.valueOf(requestedRoleName));

        user.setRole(userRole);

        // 4. Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 5. Lưu user
        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    public UserResponse getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #id == principal.username")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);

        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userRepository.deleteByUserId(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String userId) {
        return userMapper.toUserResponse(
                userRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PreAuthorize("isAuthenticated()")
    public List<UserResponse> getFacilityManagers() {
        List<User> facilityManagers = userRepository.findByRole_Name(com.utc2.facility.enums.Role.FACILITY_MANAGER);
        List<UserResponse> facilityManagersResponse = facilityManagers.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
        return facilityManagersResponse;
    }
}