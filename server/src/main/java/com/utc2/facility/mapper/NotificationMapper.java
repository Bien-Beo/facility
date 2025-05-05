package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.NotificationCreationRequest;
import com.utc2.facility.dto.response.NotificationResponse;
import com.utc2.facility.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
     Notification toEntity(NotificationCreationRequest request);
     NotificationResponse toResponse(Notification notification);
}
