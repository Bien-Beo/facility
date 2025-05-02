package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.BookingCreationRequest;
import com.utc2.facility.dto.request.BookingUpdateRequest; // Import DTO update
import com.utc2.facility.dto.response.BookingResponse;
import com.utc2.facility.entity.Booking;
import com.utc2.facility.entity.User; // Import User nếu cần cho mapping phức tạp hơn
import com.utc2.facility.entity.Room;  // Import Room nếu cần
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Chiến lược bỏ qua giá trị null khi update
public interface BookingMapper {

    @Mapping(target = "userName", source = "user.username") // Lấy username từ User object
    @Mapping(target = "roomName", source = "room.name")     // Lấy name từ Room object
    @Mapping(target = "status", source = "status") // MapStruct thường tự chuyển Enum -> String bằng .name()
    @Mapping(target = "approvedByUserName", source = "approvedByUser.username") // Lấy username từ User duyệt
    @Mapping(target = "cancelledByUserName", source = "cancelledByUser.username") // Lấy username từ User hủy
    BookingResponse toBookingResponse(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Sẽ được set ở service từ context
    @Mapping(target = "room", ignore = true) // Sẽ được set ở service sau khi fetch từ roomId
    @Mapping(target = "status", ignore = true) // Sẽ được set ở service (mặc định)
    @Mapping(target = "createdAt", ignore = true) // Tự động bởi @PrePersist
    @Mapping(target = "updatedAt", ignore = true) // Tự động bởi DB hoặc @PreUpdate
    @Mapping(target = "actualCheckInTime", ignore = true)
    @Mapping(target = "actualCheckOutTime", ignore = true)
    @Mapping(target = "approvedByUser", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "cancelledByUser", ignore = true)
    @Mapping(target = "note", source = "note") // Nếu có note trong request
    Booking toBooking(BookingCreationRequest request);


    @Mapping(target = "id", ignore = true) // Không cập nhật ID
    @Mapping(target = "user", ignore = true) // Không cập nhật người tạo
    @Mapping(target = "room", ignore = true) // Không cho cập nhật phòng qua đây
    @Mapping(target = "status", ignore = true) // Trạng thái cập nhật qua hành động riêng
    @Mapping(target = "createdAt", ignore = true) // Không cập nhật ngày tạo
    @Mapping(target = "updatedAt", ignore = true) // Sẽ tự cập nhật
    @Mapping(target = "actualCheckInTime", ignore = true) // Cập nhật qua check-in
    @Mapping(target = "actualCheckOutTime", ignore = true) // Cập nhật qua check-out
    @Mapping(target = "approvedByUser", ignore = true) // Cập nhật qua approve
    @Mapping(target = "cancellationReason", ignore = true) // Cập nhật qua cancel
    @Mapping(target = "cancelledByUser", ignore = true) // Cập nhật qua cancel
    void updateBooking(@MappingTarget Booking booking, BookingUpdateRequest request);
}