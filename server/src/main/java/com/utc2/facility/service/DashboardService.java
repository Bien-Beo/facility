package com.utc2.facility.service;

import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.dto.response.NavigationResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.EquipmentModel;
import com.utc2.facility.entity.EquipmentType;
import com.utc2.facility.entity.Room;
import com.utc2.facility.mapper.BookingEquipmentMapper;
import com.utc2.facility.mapper.EquipmentMapper;
import com.utc2.facility.mapper.RoomMapper;
import com.utc2.facility.repository.BookingEquipmentRepository;
import com.utc2.facility.repository.EquipmentItemRepository;
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
//public class DashboardService {
//    RoomRepository roomRepository;
//    EquipmentItemRepository equipmentItemRepository;
//
//    public List<Map<String, Object>> getDashboardDefault() {
//        List<Room> rooms = roomRepository.findAll();
//
//        // Nhóm phòng theo loại
//        return rooms.stream()
//                .collect(Collectors.groupingBy(
//                        room -> (room.getRoomType() != null && room.getRoomType().getName() != null)
//                                ? room.getRoomType().getName() : "Không xác định",
//                        Collectors.mapping(RoomResponse::fromEntity, Collectors.toList())
//                ))
//                .entrySet().stream()
//                .map(entry -> Map.of(
//                        "type", entry.getKey(),
//                        "rooms", entry.getValue()
//                ))
//                .collect(Collectors.toList());
//    }
//
//    public List<Map<String, Object>> getDashboardEquipment() {
//        List<EquipmentItem> equipmentItems = equipmentItemRepository.findAll();
//
//        return equipmentItems.stream()
//                .collect(Collectors.groupingBy(
//                        // Đi qua model để lấy type và kiểm tra null
//                        equipment -> {
//                            EquipmentModel model = equipment.getModel(); // Lấy model
//                            if (model != null) {
//                                EquipmentType type = model.getEquipmentType(); // Lấy type từ model
//                                if (type != null && type.getName() != null) { // Kiểm tra type và tên type
//                                    return type.getName();
//                                }
//                            }
//                            return "Không xác định"; // Giá trị mặc định nếu không lấy được tên type
//                        },
//                        // Giả sử EquipmentResponse.fromEntity tồn tại và đúng
//                        Collectors.mapping(EquipmentResponse::fromEntity, Collectors.toList())
//                ))
//                .entrySet().stream()
//                .map(entry -> Map.of(
//                        "type", entry.getKey(),
//                        "equipments", entry.getValue() // Key là "equipments"
//                ))
//                .collect(Collectors.toList());
//    }
//
//     public NavigationResponse getRoomCountByEmployeeId(String employeeId) {
//         int count = roomRepository.countRoomsManagedByUser(employeeId);
//         return new NavigationResponse(employeeId, count);
//     }
//}
public class DashboardService {
    RoomRepository roomRepository;
    EquipmentItemRepository equipmentItemRepository;
    RoomMapper roomMapper;
    EquipmentMapper equipmentMapper;
    BookingEquipmentRepository bookingEquipmentRepository;
    BookingEquipmentMapper bookingEquipmentMapper;

    public List<Map<String, Object>> getDashboardDefault() {
        List<Room> rooms = roomRepository.findAll();

        // Nhóm các đối tượng Room entity trước
        Map<String, List<Room>> groupedRooms = rooms.stream()
                .collect(Collectors.groupingBy(
                        room -> (room.getRoomType() != null && room.getRoomType().getName() != null)
                                ? room.getRoomType().getName() : "Không xác định"
                ));

        // Sau đó map sang cấu trúc kết quả, chuyển đổi List<Room> -> List<RoomResponse>
        return groupedRooms.entrySet().stream()
                .map(entry -> {
                    List<RoomResponse> roomResponses = entry.getValue().stream()
                            .map(this::buildFullRoomResponseForDashboard)
                            .collect(Collectors.toList());
                    return Map.<String, Object>of(
                            "type", entry.getKey(),
                            "rooms", roomResponses
                    );
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDashboardEquipment() {
        List<EquipmentItem> equipmentItems = equipmentItemRepository.findAll();

        // Nhóm các EquipmentItem entity trước
        Map<String, List<EquipmentItem>> groupedItems = equipmentItems.stream()
                .collect(Collectors.groupingBy(
                        equipment -> {
                            EquipmentModel model = equipment.getModel();
                            if (model != null) {
                                EquipmentType type = model.getEquipmentType();
                                if (type != null && type.getName() != null) {
                                    return type.getName();
                                }
                            }
                            return "Không xác định";
                        }
                ));

        // Map sang cấu trúc kết quả, chuyển đổi List<EquipmentItem> -> List<EquipmentResponse>
        return groupedItems.entrySet().stream()
                .map(entry -> {
                    List<EquipmentResponse> equipmentResponses = entry.getValue().stream()
                            .map(this::buildFullEquipmentResponseForDashboard)
                            .collect(Collectors.toList());
                    return Map.<String, Object>of(
                            "type", entry.getKey(),
                            "equipments", equipmentResponses
                    );
                })
                .collect(Collectors.toList());
    }

    // Helper method để tạo RoomResponse
    private RoomResponse buildFullRoomResponseForDashboard(Room room) {
        // Sử dụng RoomMapper để map các trường cơ bản
        RoomResponse response = roomMapper.toRoomResponse(room);
        // Lấy và map danh sách thiết bị mặc định
        List<EquipmentItem> defaultItems = equipmentItemRepository.findByDefaultRoom_Id(room.getId());
        List<EquipmentResponse> equipmentResponses = defaultItems.stream()
                .map(equipmentMapper::toEquipmentResponse) // Dùng EquipmentMapper
                .collect(Collectors.toList());
        response.setDefaultEquipments(equipmentResponses);
        return response;
    }

    // Helper method để tạo EquipmentResponse
    private EquipmentResponse buildFullEquipmentResponseForDashboard(EquipmentItem item) {
        return equipmentMapper.toEquipmentResponse(item);
    }


    public NavigationResponse getRoomCountByEmployeeId(String employeeId) {
        int count = roomRepository.countRoomsManagedByUser(employeeId);
        return new NavigationResponse(employeeId, count);
    }
}