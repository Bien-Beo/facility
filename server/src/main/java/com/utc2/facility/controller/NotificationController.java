package com.utc2.facility.controller;

import com.utc2.facility.dto.request.ApiResponse;
import com.utc2.facility.dto.request.NotificationCreationRequest;
import com.utc2.facility.dto.response.NotificationResponse;
import com.utc2.facility.entity.Notification;
import com.utc2.facility.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationController {
    NotificationService notificationService;

    @PostMapping
    ApiResponse<NotificationResponse> create(@RequestBody NotificationCreationRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<NotificationResponse>> getAll(@RequestParam(required = false) String userId , Pageable pageable) {
        return ApiResponse.<Page<NotificationResponse>>builder()
                .result(notificationService.getAll(userId, pageable))
                .build();
    }

    @DeleteMapping("/{notification}")
    ApiResponse<Void> delete(@PathVariable String notification) {
        notificationService.delete(notification);
        return ApiResponse.<Void>builder().build();
    }

    // Cập nhật trạng thái thông báo đã đọc
    @PutMapping("{notificationId}/read")
    public ApiResponse<NotificationResponse> markNotificationAsRead(@PathVariable String notificationId) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.markNotificationAsRead(notificationId))
                .build();
    }
}
