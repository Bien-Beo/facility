package com.utc2.facility.controller;

import java.util.List; // Không cần nếu dùng Page

import jakarta.validation.Valid;

// Imports cho Page và Pageable
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// import org.springframework.security.core.context.SecurityContextHolder; // Không cần trực tiếp ở đây
import org.springframework.web.bind.annotation.*;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.UserCreationRequest;
import com.utc2.facility.dto.request.UserUpdateRequest; // Import DTO Update
import com.utc2.facility.dto.response.UserResponse;
import com.utc2.facility.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<UserResponse>> getUsers(Pageable pageable) {
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userService.getUsers(pageable))
                .build();
    }

    @GetMapping("/fm")
    ApiResponse<List<UserResponse>> getFacilityManagers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getFacilityManagers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(
            @PathVariable String userId,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder().build();
    }
}