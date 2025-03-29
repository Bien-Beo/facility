package com.utc2.facility.service;

import com.utc2.facility.dto.response.NavigationResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.Room;
import com.utc2.facility.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {
    RoomRepository roomRepository;

    public List<Map<String, Object>> getDashboard() {
        List<Room> rooms = roomRepository.findAll();

        // Nhóm phòng theo loại
        return rooms.stream()
                .collect(Collectors.groupingBy(
                        room -> room.getRoomType() != null ? room.getRoomType().getName() : "Không xác định",
                        Collectors.mapping(RoomResponse::fromEntity, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> Map.of(
                        "type", entry.getKey(),
                        "room", entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    public NavigationResponse getRoomCountByEmployeeId(String employeeId) {
        int count = roomRepository.countRoomsManagedByUser(employeeId);
        return new NavigationResponse(employeeId, count);
    }
}
