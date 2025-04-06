package com.utc2.facility.service;

import com.utc2.facility.dto.request.BookingCreationRequest;
import com.utc2.facility.dto.request.BookingUpdateRequest; // Sử dụng DTO update
import com.utc2.facility.dto.response.BookingEquipmentResponse; // Cần DTO này
import com.utc2.facility.dto.response.BookingResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.BookingStatus;
import com.utc2.facility.enums.EquipmentStatus; // Giả sử có Enum này
// import com.utc2.facility.enums.RoomStatus; // Không nên dùng RoomStatus.BOOKED tĩnh
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.BookingEquipmentMapper; // Cần Mapper này
import com.utc2.facility.mapper.BookingMapper;
import com.utc2.facility.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder; // Để lấy user
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional
import org.springframework.util.CollectionUtils; // Import CollectionUtils
import org.springframework.util.StringUtils; // Import StringUtils

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    RoomRepository roomRepository;
    EquipmentItemRepository equipmentItemRepository;
    BookingEquipmentRepository bookingEquipmentRepository;
    BookingMapper bookingMapper;
    BookingEquipmentMapper bookingEquipmentMapper;

    // --- Phương thức Tạo Booking ---
    @Transactional // Đảm bảo tính toàn vẹn
    public BookingResponse createBooking(BookingCreationRequest request) {

        // 1. Lấy người dùng hiện tại (Cần triển khai logic này)
        User currentUser = getCurrentUser(); // Giả sử có phương thức này

        // 2. Validate Thời gian cơ bản
        validateBookingTimes(request.getPlannedStartTime(), request.getPlannedEndTime());

        Room room = null;
        List<EquipmentItem> defaultEquipment = new ArrayList<>();
        Set<String> allRequiredItemIds = new HashSet<>();
        List<EquipmentItem> finalEquipmentList = new ArrayList<>(); // List các item entity thực tế sẽ được mượn

        // 3. Xử lý và kiểm tra Phòng (nếu có)
        if (StringUtils.hasText(request.getRoomId())) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

            if (room.getStatus() == com.utc2.facility.enums.RoomStatus.UNDER_MAINTENANCE) { // Chỉ check UNDER_MAINTENANCE
                throw new AppException(ErrorCode.ROOM_UNAVAILABLE);
            }

            // TODO: Implement kiểm tra lịch trống cho phòng trong khoảng thời gian yêu cầu
            checkRoomAvailability(room.getId(), request.getPlannedStartTime(), request.getPlannedEndTime());

            // Lấy thiết bị mặc định của phòng
            defaultEquipment = equipmentItemRepository.findByDefaultRoom_IdAndStatusNot(room.getId(), EquipmentStatus.DISPOSED);
            defaultEquipment.forEach(item -> allRequiredItemIds.add(item.getId()));
        }

        // 4. Xử lý và kiểm tra Thiết bị (mặc định + mượn thêm)
        if (!CollectionUtils.isEmpty(request.getAdditionalEquipmentItemIds())) {
            allRequiredItemIds.addAll(request.getAdditionalEquipmentItemIds());
        }

        if (!allRequiredItemIds.isEmpty()) {
            finalEquipmentList = equipmentItemRepository.findAllById(allRequiredItemIds);

            // Kiểm tra tất cả ID có hợp lệ không
            if (finalEquipmentList.size() != allRequiredItemIds.size()) {
                // Tìm ra ID không hợp lệ để báo lỗi rõ hơn (tùy chọn)
                throw new AppException(ErrorCode.EQUIPMENT_ITEM_NOT_FOUND);
            }

            // Kiểm tra trạng thái từng thiết bị (không phải DISPOSED, BROKEN, IN_MAINTENANCE?)
            for (EquipmentItem item : finalEquipmentList) {
                if (item.getStatus() != EquipmentStatus.AVAILABLE) {
                    // Có thể cho phép đặt đồ đang sửa/hỏng không? Nếu không:
                    throw new AppException(ErrorCode.EQUIPMENT_UNAVAILABLE, "Thiết bị " + item.getId() + " không sẵn sàng.");
                }
            }

            // TODO: Implement kiểm tra lịch trống cho TẤT CẢ các thiết bị trong khoảng thời gian yêu cầu
            checkItemsAvailability(allRequiredItemIds, request.getPlannedStartTime(), request.getPlannedEndTime());
        }

        // 5. Tạo và Lưu Booking Entity
        Booking booking = bookingMapper.toBooking(request);
        booking.setUser(currentUser);
        booking.setRoom(room); // Có thể là null
        booking.setStatus(BookingStatus.PENDING_APPROVAL); // Hoặc CONFIRMED nếu không cần duyệt?
        // createdAt được xử lý bởi @PrePersist

        Booking savedBooking = bookingRepository.save(booking);

        // 6. Tạo và Lưu BookingEquipment Entities
        if (!finalEquipmentList.isEmpty()) {
            List<BookingEquipment> bookingEquipmentsToSave = new ArrayList<>();
            // Tạo map id -> item để tra cứu nhanh isDefault
            Set<String> defaultItemIds = defaultEquipment.stream().map(EquipmentItem::getId).collect(Collectors.toSet());

            for (EquipmentItem item : finalEquipmentList) {
                BookingEquipmentId beId = new BookingEquipmentId(savedBooking.getId(), item.getId());
                BookingEquipment be = new BookingEquipment();
                be.setId(beId);
                be.setBooking(savedBooking);
                be.setItem(item);
                be.setIsDefaultEquipment(defaultItemIds.contains(item.getId())); // Xác định cờ isDefault
                // be.setNotes(...) // Có thể thêm notes cho từng thiết bị nếu cần
                bookingEquipmentsToSave.add(be);
            }
            bookingEquipmentRepository.saveAll(bookingEquipmentsToSave);
        }

        // 7. Tạo và Trả về Response Hoàn Chỉnh
        // Cần fetch lại booking hoặc dùng savedBooking và fetch equipment để tạo response đầy đủ
        return buildFullBookingResponse(savedBooking);
    }

    // --- Các phương thức Get Booking ---

    // Helper method để xây dựng response đầy đủ (bao gồm thiết bị)
    private BookingResponse buildFullBookingResponse(Booking booking) {
        BookingResponse response = bookingMapper.toBookingResponse(booking); // Map các trường cơ bản

        // Lấy và map danh sách thiết bị
        List<BookingEquipment> bookingEquipments = bookingEquipmentRepository.findByBookingId(booking.getId());
        List<BookingEquipmentResponse> equipmentResponses = bookingEquipments.stream()
                // Giả sử BookingEquipmentMapper có phương thức toBookingEquipmentResponse
                .map(bookingEquipmentMapper::toBookingEquipmentResponse)
                .toList();
        response.setBookedEquipments(equipmentResponses); // Set danh sách thiết bị

        return response;
    }

    // Chỉ trả về thông tin booking, không tự động reject
    @PostAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER') or returnObject.userName == authentication.name")
    public BookingResponse getBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        return buildFullBookingResponse(booking);
    }

    // Nên trả về Page<BookingResponse> và nhận Pageable
    // @PreAuthorize(...) // Cần phân quyền ai được xem theo thời gian
    public List<BookingResponse> getBookingByPlannedStartTime(String plannedStartTimeStr) {
        LocalDateTime plannedStartTime = parseDateTime(plannedStartTimeStr);
        List<Booking> bookings = bookingRepository.findByPlannedStartTime(plannedStartTime);
        return bookings.stream()
                .map(this::buildFullBookingResponse) // Dùng helper method
                .toList();
    }

    // Nên trả về Page<BookingResponse> và nhận Pageable
    // @PreAuthorize(...)
    public List<BookingResponse> getBookingByPlannedEndTime(String plannedEndTimeStr) {
        LocalDateTime plannedEndTime = parseDateTime(plannedEndTimeStr);
        List<Booking> bookings = bookingRepository.findByPlannedEndTime(plannedEndTime);
        return bookings.stream()
                .map(this::buildFullBookingResponse)
                .toList();
    }

    // Các phương thức getBookingByActual...Time tương tự, có thể cần Pageable

    // Trả về Page, dùng buildFullBookingResponse
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')") // Quyền hợp lý hơn
    public Page<BookingResponse> getAllBookings(Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findAll(pageable);
        return bookingsPage.map(this::buildFullBookingResponse);
    }

    // Trả về Page, dùng buildFullBookingResponse, sửa PreAuthorize
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER') or #userId == principal.username") // Giả sử principal.username là ID user
    public Page<BookingResponse> getBookingsByUserId(String userId, Pageable pageable) {
        // Nên có phương thức trong repo hỗ trợ Pageable: findByUserId(String userId, Pageable pageable)
        Page<Booking> bookingsPage = bookingRepository.findByUser_Id(userId, pageable); // Giả định repo method
        return bookingsPage.map(this::buildFullBookingResponse);
    }


    // --- Các phương thức Thay đổi Trạng thái Booking ---

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBooking(String id) {
        // Kiểm tra booking tồn tại trước khi xóa (findById làm việc này)
        if (!bookingRepository.existsById(id)) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        // BookingEquipment sẽ bị xóa theo CASCADE nếu FK được cấu hình đúng
        bookingRepository.deleteById(id);
    }

    // Cần viết lại hoàn toàn
    @Transactional
    // @PreAuthorize(...) // Ai được quyền update? Chỉ người tạo? Admin/Manager?
    public BookingResponse updateBooking(String id, BookingUpdateRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Kiểm tra trạng thái hiện tại có cho phép cập nhật không?
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new AppException(ErrorCode.BOOKING_NOT_UPDATABLE, "Booking đã hoàn thành hoặc bị hủy.");
        }

        // TODO: Kiểm tra quyền của người dùng hiện tại (có phải người tạo hoặc admin/manager?)
        // User currentUser = getCurrentUser();
        // if(!booking.getUser().getId().equals(currentUser.getId()) && !currentUserHasAdminOrManagerRole()) {
        //    throw new AppException(ErrorCode.FORBIDDEN);
        // }


        // Lưu thời gian cũ để kiểm tra nếu cần
        LocalDateTime oldStartTime = booking.getPlannedStartTime();
        LocalDateTime oldEndTime = booking.getPlannedEndTime();
        boolean timeChanged = false;

        // Áp dụng các thay đổi cơ bản từ DTO (mapper lo việc này)
        bookingMapper.updateBooking(booking, request); // Mapper đã cấu hình NullValue...IGNORE

        // Kiểm tra và Validate thời gian nếu có thay đổi
        if (request.getPlannedStartTime() != null || request.getPlannedEndTime() != null) {
            timeChanged = true;
            // Validate thời gian mới (end > start, future/present)
            validateBookingTimes(booking.getPlannedStartTime(), booking.getPlannedEndTime());
            // TODO: Kiểm tra lại lịch trống cho phòng (nếu có) và TẤT CẢ thiết bị (cũ+mới) trong khoảng thời gian MỚI
            // checkRoomAvailability(booking.getRoom() != null ? booking.getRoom().getId() : null, booking.getPlannedStartTime(), booking.getPlannedEndTime());
            // Set<String> currentItemIds = getCurrentBookingItemIds(booking.getId()); // Lấy ID item hiện tại
            // checkItemsAvailability(currentItemIds, booking.getPlannedStartTime(), booking.getPlannedEndTime());
        }


        // TODO: Xử lý cập nhật danh sách additionalEquipmentItemIds (Phức tạp)
        if (request.getAdditionalEquipmentItemIds() != null) {
            // 1. Lấy danh sách BookingEquipment hiện tại (chỉ loại isDefault=false)
            // List<BookingEquipment> currentNonDefaultBE = bookingEquipmentRepository.findByBookingIdAndIsDefaultEquipment(id, false);
            // Set<String> currentNonDefaultItemIds = currentNonDefaultBE.stream().map(be -> be.getItem().getId()).collect(Collectors.toSet());
            // Set<String> requestedItemIds = new HashSet<>(request.getAdditionalEquipmentItemIds());

            // 2. Xác định items cần thêm (requested - current) và items cần xóa (current - requested)
            // Set<String> itemsToAddIds = new HashSet<>(requestedItemIds);
            // itemsToAddIds.removeAll(currentNonDefaultItemIds);
            // Set<String> itemsToRemoveIds = new HashSet<>(currentNonDefaultItemIds);
            // itemsToRemoveIds.removeAll(requestedItemIds);

            // 3. Kiểm tra availability cho items cần thêm
            // if (!itemsToAddIds.isEmpty()) {
            //    checkItemsAvailability(itemsToAddIds, booking.getPlannedStartTime(), booking.getPlannedEndTime());
            // }

            // 4. Thực hiện xóa BookingEquipment cho itemsToRemoveIds
            // if (!itemsToRemoveIds.isEmpty()) {
            //    List<BookingEquipment> toDelete = currentNonDefaultBE.stream().filter(be -> itemsToRemoveIds.contains(be.getItem().getId())).toList();
            //    bookingEquipmentRepository.deleteAll(toDelete);
            // }

            // 5. Thực hiện thêm BookingEquipment cho itemsToAddIds (tương tự logic create)
            // if (!itemsToAddIds.isEmpty()) {
            //     List<EquipmentItem> itemsToAdd = equipmentItemRepository.findAllById(itemsToAddIds);
            //     // Kiểm tra item tồn tại và status AVAILABLE
            //     List<BookingEquipment> toSave = new ArrayList<>();
            //     for(EquipmentItem item : itemsToAdd) {
            //         // Tạo BookingEquipment mới, set isDefault=false
            //     }
            //     bookingEquipmentRepository.saveAll(toSave);
            // }
            log.warn("Equipment list update logic needs full implementation for booking ID: {}", id); // Nhắc nhở implement
        }

        Booking savedBooking = bookingRepository.save(booking);
        return buildFullBookingResponse(savedBooking); // Trả về response đầy đủ
    }


    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')") // Quyền duyệt
    public BookingResponse approveBooking(String bookingId) {
        Booking booking = findBookingByIdOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        // TODO: Kiểm tra lại lần cuối lịch trống cho phòng và TẤT CẢ thiết bị liên quan
        // vì có thể có thay đổi từ lúc tạo đến lúc duyệt
        // checkRoomAvailability(...)
        // checkItemsAvailability(...)

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setApprovedByUser(getCurrentUser()); // Gán người duyệt

        // KHÔNG cập nhật trạng thái tĩnh của phòng
        // Room room = booking.getRoom();
        // if(room != null) { room.setStatus(RoomStatus.BOOKED); roomRepository.save(room); }

        Booking savedBooking = bookingRepository.save(booking);
        return buildFullBookingResponse(savedBooking);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')") // Quyền từ chối
    public BookingResponse rejectBooking(String bookingId) {
        Booking booking = findBookingByIdOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setApprovedByUser(getCurrentUser()); // Ghi nhận người từ chối

        Booking savedBooking = bookingRepository.save(booking);
        return buildFullBookingResponse(savedBooking);
    }

    // Đổi tên và logic check-in/hoàn thành
    @Transactional
    // @PreAuthorize(...) // Ai được quyền hoàn thành? Admin/Manager/Người mượn?
    public BookingResponse completeBooking(String bookingId) {
        Booking booking = findBookingByIdOrThrow(bookingId);

        // Chỉ cho phép hoàn thành các booking đã được duyệt hoặc đang diễn ra
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.BOOKING_STATUS_INVALID, "Không thể hoàn thành booking ở trạng thái " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setActualCheckInTime(LocalDateTime.now()); // Gán thời gian thực trả

        // KHÔNG cập nhật trạng thái tĩnh của phòng
        // Room room = booking.getRoom();
        // if(room != null) { room.setStatus(RoomStatus.AVAILABLE); roomRepository.save(room); }

        // TODO: Optional: Cập nhật trạng thái thiết bị nếu cần (ví dụ kiểm tra khi trả)
        // List<BookingEquipment> returnedEquipment = bookingEquipmentRepository.findByBookingId(bookingId);
        // for (BookingEquipment be : returnedEquipment) {
        //    // Kiểm tra tình trạng item -> cập nhật item.status nếu BROKEN -> tạo MaintenanceTicket
        // }

        Booking savedBooking = bookingRepository.save(booking);
        return buildFullBookingResponse(savedBooking);
    }

    // TODO: Thêm các phương thức cho Check-out (nếu cần trạng thái IN_PROGRESS)
    // public BookingResponse checkOutBooking(String bookingId) { ... setStatus(BookingStatus.IN_PROGRESS), setActualCheckOutTime... }

    // TODO: Thêm phương thức cho người dùng tự Cancel booking (nếu trạng thái là PENDING/CONFIRMED và chưa đến giờ)
    // public BookingResponse cancelBookingByUser(String bookingId) { ... setStatus(BookingStatus.CANCELLED), setCancelledByUser, setCancellationReason ... }


    // --- Helper Methods ---

    // Cần triển khai phương thức này để lấy User entity đang đăng nhập
    private User getCurrentUser() {
        // Ví dụ:
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepository.findByUsername(username) // Giả sử repo có findByUsername
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Hoặc inject một AuthService/UserService để lấy user
        // return authService.getCurrentAuthenticatedUser();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // Linh hoạt hơn với nhiều format hoặc dùng format chuẩn ISO
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // return LocalDateTime.parse(dateTimeStr); // Nếu dùng ISO 8601 format
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_DATE_FORMAT, "Sai định dạng thời gian: " + dateTimeStr);
        }
    }

    // Validate thời gian cơ bản
    private void validateBookingTimes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Thời gian bắt đầu và kết thúc không được để trống.");
        }
        // Có thể bỏ check này nếu không phải rule cứng
        // if (start.isBefore(LocalDateTime.now().plusHours(1))) {
        //     throw new AppException(ErrorCode.BORROW_TIME_INVALID);
        // }
        if (!end.isAfter(start)) { // Chỉ cần end > start là đủ
            throw new AppException(ErrorCode.INVALID_INPUT, "Thời gian kết thúc phải sau thời gian bắt đầu.");
        }
        // Có thể bỏ check này nếu không phải rule cứng
        // if (end.isBefore(start.plusHours(1))) {
        //     throw new AppException(ErrorCode.BORROW_RETURN_TIME_INVALID);
        // }
    }

    // Helper tìm booking hoặc throw exception
    private Booking findBookingByIdOrThrow(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }


    // --- Placeholder cho Logic phức tạp cần Implement ---

    private void checkRoomAvailability(String roomId, LocalDateTime start, LocalDateTime end) {
        log.warn("TODO: Implement checkRoomAvailability logic for roomId: {}, start: {}, end: {}", roomId, start, end);
        // Query bookingRepository: existsByRoomIdAndStatusInAndPlannedStartTimeBeforeAndPlannedEndTimeAfter(...)
        // Cần xử lý các trường hợp trùng lặp/chồng lấn thời gian chính xác
        // boolean isOverlapping = bookingRepository.existsOverlappingBookingForRoom(roomId, start, end, List.of(BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS));
        // if(isOverlapping){ throw new AppException(ErrorCode.ROOM_UNAVAILABLE_TIMESLOT); }
    }

    private void checkItemsAvailability(Set<String> itemIds, LocalDateTime start, LocalDateTime end) {
        log.warn("TODO: Implement checkItemsAvailability logic for itemIds: {}, start: {}, end: {}", itemIds, start, end);
        // Query bookingEquipmentRepository/bookingRepository: existsByItemIdInAndBookingStatusInAndPlannedStartTimeBeforeAndPlannedEndTimeAfter(...)
        // Cần xử lý các trường hợp trùng lặp/chồng lấn thời gian chính xác cho từng item
        // List<String> unavailableItems = bookingRepository.findUnavailableItems(itemIds, start, end, List.of(BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS));
        // if(!unavailableItems.isEmpty()){ throw new AppException(ErrorCode.EQUIPMENT_UNAVAILABLE_TIMESLOT, "Items unavailable: " + unavailableItems); }
    }

    // Helper lấy ID item hiện tại của booking (dùng cho update)
    private Set<String> getCurrentBookingItemIds(String bookingId) {
        return bookingEquipmentRepository.findByBookingId(bookingId)
                .stream()
                .map(be -> be.getItem().getId())
                .collect(Collectors.toSet());
    }

}