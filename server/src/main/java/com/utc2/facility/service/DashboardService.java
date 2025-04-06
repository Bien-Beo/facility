package com.utc2.facility.service;

import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.dto.response.NavigationResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.EquipmentModel;
import com.utc2.facility.entity.EquipmentType;
import com.utc2.facility.entity.Room;
import com.utc2.facility.repository.EquipmentItemRepository;
import com.utc2.facility.repository.RoomRepository;
import com.utc2.facility.repository.RoomTypeRepository;
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
    EquipmentItemRepository equipmentItemRepository;
    RoomTypeRepository roomTypeRepository;

    public List<Map<String, Object>> getDashboardDefault() {
        List<Room> rooms = roomRepository.findAll();

        // Nhóm phòng theo loại
        return rooms.stream()
                .collect(Collectors.groupingBy(
                        room -> (room.getRoomType() != null && room.getRoomType().getName() != null)
                                ? room.getRoomType().getName() : "Không xác định",
                        Collectors.mapping(RoomResponse::fromEntity, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> Map.of(
                        "type", entry.getKey(),
                        "rooms", entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDashboardEquipment() {
        List<EquipmentItem> equipmentItems = equipmentItemRepository.findAll();

        return equipmentItems.stream()
                .collect(Collectors.groupingBy(
                        // SỬA Ở ĐÂY: Đi qua model để lấy type và kiểm tra null
                        equipment -> {
                            EquipmentModel model = equipment.getModel(); // Lấy model
                            if (model != null) {
                                EquipmentType type = model.getEquipmentType(); // Lấy type từ model
                                if (type != null && type.getName() != null) { // Kiểm tra type và tên type
                                    return type.getName();
                                }
                            }
                            return "Không xác định"; // Giá trị mặc định nếu không lấy được tên type
                        },
                        // Giả sử EquipmentResponse.fromEntity tồn tại và đúng
                        Collectors.mapping(EquipmentResponse::fromEntity, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> Map.of(
                        "type", entry.getKey(),
                        "equipments", entry.getValue() // Key là "equipments"
                ))
                .collect(Collectors.toList());
    }

     public NavigationResponse getRoomCountByEmployeeId(String employeeId) {
         int count = roomRepository.countRoomsManagedByUser(employeeId);
         return new NavigationResponse(employeeId, count);
     }
}