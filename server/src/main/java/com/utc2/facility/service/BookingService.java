package com.utc2.facility.service;

import com.utc2.facility.dto.request.BookingCreationRequest;
import com.utc2.facility.dto.request.BookingUpdateRequest;
import com.utc2.facility.dto.request.CancelBookingRequest;
import com.utc2.facility.dto.response.BookingEquipmentResponse;
import com.utc2.facility.dto.response.BookingResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.enums.BookingStatus;
import com.utc2.facility.enums.EquipmentStatus;
import com.utc2.facility.enums.MaintenanceStatus;
import com.utc2.facility.enums.Role;
import com.utc2.facility.enums.RoomStatus; // Cần để check phòng maintenance
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.BookingEquipmentMapper;
import com.utc2.facility.mapper.BookingMapper;
import com.utc2.facility.repository.*;
import com.utc2.facility.specification.BookingSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    MaintenanceRepository maintenanceRepository;
    BookingMapper bookingMapper;
    BookingEquipmentMapper bookingEquipmentMapper;

    // --- Phương thức Tạo Booking ---
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public BookingResponse createBooking(BookingCreationRequest request) {
        log.info("Creating new booking request from user");

        User currentUser = getCurrentUser();
        validateBookingTimes(request.getPlannedStartTime(), request.getPlannedEndTime());

        Room room = null;
        List<EquipmentItem> defaultEquipment = new ArrayList<>();
        Set<String> allRequiredItemIds = new HashSet<>();
        List<EquipmentItem> finalEquipmentList = new ArrayList<>();

        if (StringUtils.hasText(request.getRoomId())) {
            room = findRoomByIdOrThrow(request.getRoomId());
            log.debug("Booking includes room: {}", room.getId());

            if (room.getStatus() == RoomStatus.UNDER_MAINTENANCE) {
                log.warn("Attempted to book room under maintenance: {}", room.getId());
                throw new AppException(ErrorCode.ROOM_UNAVAILABLE, "Phòng " + room.getName() + " đang được bảo trì.");
            }

            // Kiểm tra lịch trống cho phòng
            checkRoomAvailability(room.getId(), request.getPlannedStartTime(), request.getPlannedEndTime());
            log.debug("Room {} availability checked successfully for the requested timeslot.", room.getId());

            // Lấy thiết bị mặc định của phòng (chỉ những cái không bị DISPOSED)
            defaultEquipment = equipmentItemRepository.findByDefaultRoom_IdAndStatusNot(room.getId(), EquipmentStatus.DISPOSED);
            defaultEquipment.forEach(item -> allRequiredItemIds.add(item.getId()));
            log.debug("Found {} default equipment items for room {}: {}", defaultEquipment.size(), room.getId(), allRequiredItemIds);
        } else {
            log.debug("Booking does not include a specific room.");
        }

        // Xử lý và kiểm tra Thiết bị (mặc định + mượn thêm)
        if (!CollectionUtils.isEmpty(request.getAdditionalEquipmentItemIds())) {
            log.debug("Adding additional equipment items: {}", request.getAdditionalEquipmentItemIds());
            allRequiredItemIds.addAll(request.getAdditionalEquipmentItemIds());
        }

        if (!allRequiredItemIds.isEmpty()) {
            log.debug("Total required equipment items IDs: {}", allRequiredItemIds);
            finalEquipmentList = equipmentItemRepository.findAllById(allRequiredItemIds);

            if (finalEquipmentList.size() != allRequiredItemIds.size()) {
                log.error("Could not find all requested equipment items. Requested: {}, Found: {}", allRequiredItemIds, finalEquipmentList.stream().map(EquipmentItem::getId).toList());
                throw new AppException(ErrorCode.EQUIPMENT_ITEM_NOT_FOUND, "Một hoặc nhiều thiết bị yêu cầu không tồn tại.");
            }
            if (!CollectionUtils.isEmpty(request.getAdditionalEquipmentItemIds())) {
                Set<String> additionalIdsRequested = new HashSet<>(request.getAdditionalEquipmentItemIds());
                String bookingRoomId = request.getRoomId(); // ID phòng đang được đặt (có thể null)

                for (EquipmentItem item : finalEquipmentList) {
                    // Chỉ xét những item nằm trong danh sách yêu cầu thêm tường minh
                    if (additionalIdsRequested.contains(item.getId())) {
                        // Kiểm tra xem item này có thuộc về một phòng mặc định nào không
                        if (item.getDefaultRoom() != null) {
                            // Nếu có phòng mặc định, kiểm tra xem booking hiện tại CÓ PHẢI là cho phòng đó không
                            // Nếu bookingRoomId là null (chỉ mượn thiết bị) HOẶC bookingRoomId khác với phòng mặc định của item
                            if (bookingRoomId == null || !bookingRoomId.equals(item.getDefaultRoom().getId())) {
                                log.warn("Attempt to borrow default equipment item {} (belongs to room {}) independently or for wrong room {}.",
                                        item.getId(), item.getDefaultRoom().getId(), bookingRoomId);
                                // Ném lỗi ngăn chặn việc mượn này
                                throw new AppException(ErrorCode.DEFAULT_EQUIPMENT_CANNOT_BE_BORROWED_SEPARATELY,
                                        "Thiết bị " + item.getId() + " (" + item.getModel().getName() +")"
                                                + " là thiết bị cố định của phòng " + item.getDefaultRoom().getName()
                                                + " và không thể mượn riêng lẻ hoặc cho phòng khác.");
                            }
                            // Trường hợp bookingRoomId trùng với phòng mặc định của item -> OK (dù hơi thừa vì nó nên được thêm tự động)
                        }
                        // Nếu item.getDefaultRoom() là null -> đây là thiết bị độc lập, cho phép mượn riêng lẻ -> OK
                    }
                }
                log.debug("Check for borrowing default items independently passed.");
            }

            List<String> unavailableItems = new ArrayList<>();
            for (EquipmentItem item : finalEquipmentList) {
                if (item.getStatus() != EquipmentStatus.AVAILABLE) {
                    log.warn("Equipment item {} is not available (Status: {})", item.getId(), item.getStatus());
                    unavailableItems.add(item.getId() + " (" + item.getStatus() + ")");
                    throw new AppException(ErrorCode.EQUIPMENT_UNAVAILABLE, "Thiết bị " + item.getId() + " không sẵn sàng ("+ item.getStatus() +").");
                }
            }
            if (!unavailableItems.isEmpty()) {
                throw new AppException(ErrorCode.EQUIPMENT_UNAVAILABLE, "Một số thiết bị không sẵn sàng: " + String.join(", ", unavailableItems));
            }

            // Kiểm tra lịch trống cho TẤT CẢ các thiết bị
            checkItemsAvailability(allRequiredItemIds, request.getPlannedStartTime(), request.getPlannedEndTime());
            log.debug("All required equipment items availability checked successfully.");
        } else {
            log.debug("No specific equipment items required for this booking.");
        }

        // Tạo và Lưu Booking Entity
        Booking booking = bookingMapper.toBooking(request);
        booking.setUser(currentUser);
        booking.setRoom(room);
        booking.setStatus(BookingStatus.PENDING_APPROVAL); // Mặc định chờ duyệt

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking entity saved with ID: {}", savedBooking.getId());

        // Tạo và Lưu BookingEquipment Entities
        if (!finalEquipmentList.isEmpty()) {
            List<BookingEquipment> bookingEquipmentsToSave = new ArrayList<>();
            Set<String> defaultItemIds = defaultEquipment.stream().map(EquipmentItem::getId).collect(Collectors.toSet());

            for (EquipmentItem item : finalEquipmentList) {
                BookingEquipmentId beId = new BookingEquipmentId(savedBooking.getId(), item.getId());
                System.out.println("BookingEquipmentId: " + beId);
                // Kiểm tra lại nếu liên kết đã tồn tại (phòng trường hợp logic bị lặp)
                if (!bookingEquipmentRepository.existsById(beId)) {
                    BookingEquipment be = new BookingEquipment();
                    be.setId(beId);
                    be.setBooking(savedBooking);
                    be.setItem(item);
                    be.setIsDefaultEquipment(defaultItemIds.contains(item.getId()));
                    bookingEquipmentsToSave.add(be);
                } else {
                    log.warn("BookingEquipment link already exists for booking {} and item {}. Skipping.", savedBooking.getId(), item.getId());
                }
            }
            if (!bookingEquipmentsToSave.isEmpty()) {
                bookingEquipmentRepository.saveAll(bookingEquipmentsToSave);
                log.info("Saved {} BookingEquipment links for booking {}", bookingEquipmentsToSave.size(), savedBooking.getId());
            }
        }

        // Tạo và Trả về Response Hoàn Chỉnh
        return buildFullBookingResponse(savedBooking);
    }

    // --- Các phương thức Get Booking ---

    private Room findRoomByIdOrThrow(String roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND, "Phòng không tồn tại."));
    }

    // Helper xây dựng response đầy đủ
    private BookingResponse buildFullBookingResponse(Booking booking) {
        BookingResponse response = bookingMapper.toBookingResponse(booking);
        List<BookingEquipment> bookingEquipments = bookingEquipmentRepository.findByBookingId(booking.getId());
        if (bookingEquipments != null) {
            List<BookingEquipmentResponse> equipmentResponses = bookingEquipments.stream()
                    .map(bookingEquipmentMapper::toBookingEquipmentResponse)
                    .toList();
            response.setBookedEquipments(equipmentResponses);
        } else {
            response.setBookedEquipments(Collections.emptyList());
        }
        return response;
    }

    // Get một booking cụ thể, kiểm tra quyền xem
    @PostAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER') or returnObject.userName == authentication.name")
    public BookingResponse getBooking(String id) {
        log.debug("Fetching booking with ID: {}", id);
        Booking booking = findBookingByIdOrThrow(id);
        return buildFullBookingResponse(booking);
    }

    public Page<BookingResponse> getBookingsByRoomId(String roomId, Pageable pageable) {
        log.debug("Fetching bookings for room ID: {} with pageable: {}", roomId, pageable);
        Page<Booking> bookingsPage = bookingRepository.findByRoom_Id(roomId, pageable);
        return bookingsPage.map(this::buildFullBookingResponse);
    }

    // Các phương thức Get theo thời gian
    public List<BookingResponse> getBookingByPlannedStartTime(String plannedStartTimeStr) {
        LocalDateTime plannedStartTime = parseDateTime(plannedStartTimeStr);
        log.debug("Fetching bookings with plannedStartTime: {}", plannedStartTime);
        List<Booking> bookings = bookingRepository.findByPlannedStartTime(plannedStartTime);
        return bookings.stream().map(this::buildFullBookingResponse).toList();
    }

    public List<BookingResponse> getBookingByPlannedEndTime(String plannedEndTimeStr) {
        LocalDateTime plannedEndTime = parseDateTime(plannedEndTimeStr);
        log.debug("Fetching bookings with plannedEndTime: {}", plannedEndTime);
        List<Booking> bookings = bookingRepository.findByPlannedEndTime(plannedEndTime);
        return bookings.stream().map(this::buildFullBookingResponse).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public Page<BookingResponse> getOverdueBookings(Pageable pageable, int page, int size) {
        List<Booking> allOverdues = bookingRepository.findByStatus(BookingStatus.OVERDUE)
                .stream()
                .filter(booking -> booking.getPlannedEndTime().isBefore(LocalDateTime.now()))
                .toList();

        // Tạo phân trang thủ công
        int start = page * size;
        int end = Math.min(start + size, allOverdues.size());
        List<BookingResponse> pagedList = allOverdues.subList(start, end)
                .stream()
                .map(bookingMapper::toBookingResponse)
                .toList();

        return new PageImpl<>(pagedList, PageRequest.of(page, size), allOverdues.size());
    }

    // Get tất cả bookings (phân trang)
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public Page<BookingResponse> getAllBookings(String roomId, Integer month, Integer year, String userId, Pageable pageable) {
        log.debug("Fetching all bookings with pageable: {}", pageable);
        Page<Booking> bookingsPage = bookingRepository.findAll(
                BookingSpecification.filterByRoomId(roomId)
                        .and(BookingSpecification.filterByUserId(userId))
                        .and(BookingSpecification.filterByMonthAndYear(month, year)), pageable);
        return bookingsPage.map(this::buildFullBookingResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Page<BookingResponse> getMyBookings(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "User đăng nhập không tồn tại trong DB: " + username));
        String userId = currentUser.getId();

        log.debug("Fetching bookings for user ID: {} with pageable: {}", userId, pageable);

        Page<Booking> bookingPage = bookingRepository.findByUser_Id(userId, pageable);

        return bookingPage.map(this::buildFullBookingResponse);
    }

    // --- Các phương thức Thay đổi ---

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBooking(String id) {
        log.warn("Attempting to delete booking with ID: {}", id);
        if (!bookingRepository.existsById(id)) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        bookingRepository.deleteById(id);
        log.info("Booking deleted successfully: {}", id);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER') or @bookingSecurityService.isOwner(#id, principal.username)")
    public BookingResponse updatePendingBookingDetails(String id, BookingUpdateRequest request) {
        log.info("Updating booking with ID: {}", id);
        Booking booking = findBookingByIdOrThrow(id);
        User currentUser = getCurrentUser();

        boolean isAdminOrManager = currentUser.getRole().getName().equals(Role.ADMIN) || currentUser.getRole().getName().equals(Role.FACILITY_MANAGER);
        if (!booking.getUser().getId().equals(currentUser.getId()) && !isAdminOrManager) {
            log.warn("User {} forbidden to update booking {}", currentUser.getUsername(), id);
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền cập nhật booking này.");
        }

        // CHỈ cho phép update khi đang chờ duyệt
        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            log.warn("Attempted to update booking {} with status {} (only PENDING_APPROVAL allowed here)", id, booking.getStatus());
            throw new AppException(ErrorCode.BOOKING_NOT_UPDATABLE, "Chỉ có thể cập nhật chi tiết booking khi đang chờ duyệt.");
        }

        boolean timeChanged = false;
        LocalDateTime newStartTime = request.getPlannedStartTime() != null ? request.getPlannedStartTime() : booking.getPlannedStartTime();
        LocalDateTime newEndTime = request.getPlannedEndTime() != null ? request.getPlannedEndTime() : booking.getPlannedEndTime();

        if (request.getPlannedStartTime() != null || request.getPlannedEndTime() != null) {
            if(!newStartTime.equals(booking.getPlannedStartTime()) || !newEndTime.equals(booking.getPlannedEndTime())){
                timeChanged = true;
                log.debug("Booking time changed for booking {}. New start: {}, New end: {}", id, newStartTime, newEndTime);
                validateBookingTimes(newStartTime, newEndTime);
            }
        }

        // Áp dụng thay đổi cơ bản từ DTO
        bookingMapper.updateBooking(booking, request);

        // Xử lý cập nhật danh sách thiết bị
        Set<String> finalItemIds = new HashSet<>();
        if (booking.getRoom() != null) {
            // Lấy lại default items (phòng không đổi trong update này)
            equipmentItemRepository.findByDefaultRoom_IdAndStatusNot(booking.getRoom().getId(), EquipmentStatus.DISPOSED)
                    .forEach(item -> finalItemIds.add(item.getId()));
        }

        if (request.getAdditionalEquipmentItemIds() != null) { // Chỉ xử lý nếu client gửi danh sách mới
            log.debug("Processing update for additional equipment items for booking {}. Requested: {}", id, request.getAdditionalEquipmentItemIds());
            // 1. Lấy BookingEquipment hiện tại (non-default)
            List<BookingEquipment> currentNonDefaultBE = bookingEquipmentRepository.findByBookingIdAndIsDefaultEquipment(id, false);
            Set<String> currentNonDefaultItemIds = currentNonDefaultBE.stream().map(be -> be.getItem().getId()).collect(Collectors.toSet());

            // 2. Xác định thêm/xóa
            Set<String> requestedItemIds = new HashSet<>(request.getAdditionalEquipmentItemIds());
            finalItemIds.addAll(requestedItemIds); // Add requested items to the final list for availability check

            Set<String> itemsToAddIds = new HashSet<>(requestedItemIds);
            itemsToAddIds.removeAll(currentNonDefaultItemIds);

            Set<String> itemsToRemoveIds = new HashSet<>(currentNonDefaultItemIds);
            itemsToRemoveIds.removeAll(requestedItemIds);

            log.debug("Items to add: {}, Items to remove: {}", itemsToAddIds, itemsToRemoveIds);

            // 3. Xóa các liên kết không còn cần thiết
            if (!itemsToRemoveIds.isEmpty()) {
                List<BookingEquipment> toDelete = currentNonDefaultBE.stream()
                        .filter(be -> itemsToRemoveIds.contains(be.getItem().getId()))
                        .toList();
                if(!toDelete.isEmpty()){
                    log.info("Removing {} non-default equipment links for booking {}", toDelete.size(), id);
                    bookingEquipmentRepository.deleteAll(toDelete);
                }
            }

            // Kiểm tra và Thêm các liên kết mới
            if (!itemsToAddIds.isEmpty()) {
                List<EquipmentItem> itemsToAdd = equipmentItemRepository.findAllById(itemsToAddIds);
                if (itemsToAdd.size() != itemsToAddIds.size()) {
                    throw new AppException(ErrorCode.EQUIPMENT_ITEM_NOT_FOUND, "Một hoặc nhiều thiết bị cần thêm không tồn tại.");
                }
                // Kiểm tra trạng thái AVAILABLE của đồ cần thêm
                List<String> unavailableItemsToAdd = itemsToAdd.stream()
                        .filter(item -> item.getStatus() != EquipmentStatus.AVAILABLE)
                        .map(item -> item.getId() + " (" + item.getStatus() + ")")
                        .toList();
                if (!unavailableItemsToAdd.isEmpty()) {
                    throw new AppException(ErrorCode.EQUIPMENT_UNAVAILABLE, "Thiết bị cần thêm không sẵn sàng: " + String.join(", ", unavailableItemsToAdd));
                }

                // Check lịch trống cho đồ cần thêm (trong khoảng thời gian MỚI)
                checkItemsAvailability(itemsToAddIds, newStartTime, newEndTime);

                // Tạo và lưu liên kết mới
                List<BookingEquipment> toSave = new ArrayList<>();
                for (EquipmentItem item : itemsToAdd) {
                    BookingEquipmentId beId = new BookingEquipmentId(booking.getId(), item.getId());
                    if (!bookingEquipmentRepository.existsById(beId)) { // Kiểm tra lại trước khi thêm
                        BookingEquipment be = new BookingEquipment();
                        be.setId(beId);
                        be.setBooking(booking);
                        be.setItem(item);
                        be.setIsDefaultEquipment(false); // Luôn là false khi thêm thủ công
                        toSave.add(be);
                    }
                }
                if (!toSave.isEmpty()) {
                    log.info("Adding {} new non-default equipment links for booking {}", toSave.size(), id);
                    bookingEquipmentRepository.saveAll(toSave);
                }
            }
        } else {
            // Nếu request.getAdditionalEquipmentItemIds() là null, giữ nguyên danh sách hiện tại
            // Cần lấy lại ID item hiện tại để check availability nếu thời gian thay đổi
            bookingEquipmentRepository.findByBookingIdAndIsDefaultEquipment(id, false)
                    .forEach(be -> finalItemIds.add(be.getItem().getId()));
            log.debug("No changes requested for additional equipment items for booking {}.", id);
        }


        // Kiểm tra lại lịch trống tổng thể nếu thời gian thay đổi
        if (timeChanged) {
            log.debug("Re-checking availability for final setup after time change for booking {}", id);
            if (booking.getRoom() != null) {
                checkRoomAvailability(booking.getRoom().getId(), newStartTime, newEndTime);
            }
            if (!finalItemIds.isEmpty()) {
                checkItemsAvailability(finalItemIds, newStartTime, newEndTime);
            }
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking updated successfully: {}", savedBooking.getId());
        return buildFullBookingResponse(savedBooking);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public BookingResponse approveBooking(String bookingId) {
        log.info("Approving booking with ID: {}", bookingId);
        Booking booking = findBookingByIdOrThrow(bookingId);
        User approver = getCurrentUser();

        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            log.warn("Booking {} already processed, cannot approve.", bookingId);
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        // Kiểm tra lại lịch trống lần cuối trước khi duyệt
        log.debug("Final availability check before approving booking {}", bookingId);
        if (booking.getRoom() != null) {
            checkRoomAvailability(booking.getRoom().getId(), booking.getPlannedStartTime(), booking.getPlannedEndTime());
        }
        Set<String> currentItemIds = getCurrentBookingItemIds(booking.getId());
        if (!currentItemIds.isEmpty()) {
            checkItemsAvailability(currentItemIds, booking.getPlannedStartTime(), booking.getPlannedEndTime());
        }
        log.debug("Final availability check passed for booking {}", bookingId);


        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setApprovedByUser(approver);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking {} approved by {}", bookingId, approver.getUsername());
        // TODO: Gửi thông báo cho người đặt?
        return buildFullBookingResponse(savedBooking);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER')")
    public BookingResponse rejectBooking(String bookingId) {
        log.info("Rejecting booking with ID: {}", bookingId);
        Booking booking = findBookingByIdOrThrow(bookingId);
        User rejector = getCurrentUser(); // Người thực hiện từ chối

        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            log.warn("Booking {} already processed, cannot reject.", bookingId);
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setApprovedByUser(rejector); // Ghi nhận ai đã từ chối

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking {} rejected by {}", bookingId, rejector.getUsername());
        // TODO: Gửi thông báo cho người đặt?
        return buildFullBookingResponse(savedBooking);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER', 'TECHNICIAN') or @bookingSecurityService.isOwner(#bookingId, principal.username)")
    public BookingResponse completeBooking(String bookingId) { // Đổi tên thành completeBooking
        log.info("Completing booking with ID: {}", bookingId);
        Booking booking = findBookingByIdOrThrow(bookingId);

        if (!EnumSet.of(BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS).contains(booking.getStatus())) {
            log.warn("Cannot complete booking {} with status {}", bookingId, booking.getStatus());
            throw new AppException(ErrorCode.BOOKING_STATUS_INVALID, "Không thể hoàn thành booking ở trạng thái " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setActualCheckInTime(LocalDateTime.now()); // Thời gian trả/hoàn thành

        // Optional: Kiểm tra và cập nhật trạng thái thiết bị khi trả
        List<BookingEquipment> returnedEquipmentLinks = bookingEquipmentRepository.findByBookingId(bookingId);
        for (BookingEquipment be : returnedEquipmentLinks) {
            EquipmentItem item = be.getItem();
            // TODO: Thêm logic kiểm tra tình trạng thực tế của item khi trả ở đây
            boolean isReportedBroken = false; // Giả sử có cách biết item hỏng khi trả
            if (isReportedBroken && item.getStatus() == EquipmentStatus.AVAILABLE) { // Chỉ cập nhật nếu nó đang AVAILABLE
                log.warn("Equipment item {} reported broken upon return for booking {}", item.getId(), bookingId);
                item.setStatus(EquipmentStatus.BROKEN);
                equipmentItemRepository.save(item);
                // Tự động tạo Maintenance Ticket?
                MaintenanceTicket ticket = MaintenanceTicket.builder()
                        .item(item)
                        .reportedBy(getCurrentUser()) // Người trả có thể là người báo cáo
                        .description("Thiết bị được báo cáo hỏng khi trả từ booking ID: " + bookingId)
                        .status(MaintenanceStatus.REPORTED)
                        .build();
                maintenanceRepository.save(ticket);
                log.info("Maintenance ticket created for broken item {} from booking {}", item.getId(), bookingId);
            }
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking {} completed.", bookingId);
        return buildFullBookingResponse(savedBooking);
    }

    // --- Các phương thức Hành động khác (Ví dụ) ---

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER', 'TECHNICIAN') or @bookingSecurityService.isOwner(#bookingId, principal.username)")
    public BookingResponse checkOutBooking(String bookingId) {
        log.info("Checking out booking with ID: {}", bookingId);
        Booking booking = findBookingByIdOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            log.warn("Cannot check out booking {} with status {}", bookingId, booking.getStatus());
            throw new AppException(ErrorCode.BOOKING_STATUS_INVALID, "Chỉ có thể check-out booking đã được duyệt (CONFIRMED).");
        }

        // Kiểm tra xem đã đến giờ mượn chưa?
        if (LocalDateTime.now().isBefore(booking.getPlannedStartTime())) {
            throw new AppException(ErrorCode.BOOKING_NOT_YET_STARTED);
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setActualCheckOutTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking {} checked out.", bookingId);
        return buildFullBookingResponse(savedBooking);
    }

    @Transactional
    //@PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER') or @bookingSecurityService.isOwner(#bookingId, principal.username)")
    public void cancelBookingByUser(String bookingId, CancelBookingRequest cancelBookingRequest) {
        log.info("User cancelling booking with ID: {}", bookingId);
        Booking booking = findBookingByIdOrThrow(bookingId);
        User currentUser = getCurrentUser();

        // Kiểm tra trạng thái cho phép hủy
        if (!EnumSet.of(BookingStatus.PENDING_APPROVAL, BookingStatus.CONFIRMED).contains(booking.getStatus())) {
            log.warn("Cannot cancel booking {} with status {}", bookingId, booking.getStatus());
            throw new AppException(ErrorCode.BOOKING_NOT_CANCELLABLE, "Không thể hủy booking ở trạng thái này.");
        }

        // Thêm quy tắc không cho hủy nếu quá gần giờ bắt đầu
        final int CANCELLATION_HOURS_BEFORE = 1;
        if (booking.getStatus() == BookingStatus.CONFIRMED &&
                LocalDateTime.now().isAfter(booking.getPlannedStartTime().minusHours(CANCELLATION_HOURS_BEFORE))) {
            log.warn("Attempted to cancel booking {} too close to start time.", bookingId);
            throw new AppException(ErrorCode.CANCELLATION_WINDOW_EXPIRED, "Đã quá thời hạn cho phép hủy (phải hủy trước " + CANCELLATION_HOURS_BEFORE + " giờ).");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledByUser(currentUser);
        booking.setCancellationReason(Objects.equals(cancelBookingRequest.getReason(), "") ? "Cancelled by user." : cancelBookingRequest.getReason());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking {} cancelled by user {}", bookingId, currentUser.getUsername());
        buildFullBookingResponse(savedBooking);
    }

    // --- Helper Methods ---

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "Authenticated user not found: " + username));
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_DATE_FORMAT, "Sai định dạng thời gian: " + dateTimeStr);
        }
    }

    private void validateBookingTimes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Thời gian bắt đầu và kết thúc không được để trống.");
        }
        if (!end.isAfter(start)) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Thời gian kết thúc phải sau thời gian bắt đầu.");
        }
    }

    private Booking findBookingByIdOrThrow(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    // --- Triển khai Logic kiểm tra lịch trống ---

    private void checkRoomAvailability(String roomId, LocalDateTime start, LocalDateTime end) {
        if (roomId == null) return; // Bỏ qua nếu không đặt phòng
        log.debug("Checking room availability for roomId: {}, start: {}, end: {}", roomId, start, end);
        boolean isOverlapping = bookingRepository.existsOverlappingBookingForRoom(
                roomId,
                List.of(BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS), // Chỉ kiểm tra các trạng thái đã chắc chắn chiếm dụng
                start,
                end
        );
        if (isOverlapping) {
            log.warn("Room {} is unavailable during the requested time slot.", roomId);
            throw new AppException(ErrorCode.ROOM_UNAVAILABLE_TIMESLOT, "Phòng đã được đặt trong khoảng thời gian này.");
        }
        log.debug("Room {} is available.", roomId);
    }

    private void checkItemsAvailability(Set<String> itemIds, LocalDateTime start, LocalDateTime end) {
        if (itemIds == null || itemIds.isEmpty()) return;
        log.debug("Checking item availability for itemIds: {}, start: {}, end: {}", itemIds, start, end);
        // Cách 1: Tìm các item ID bị trùng lịch
        List<String> unavailableItemIds = bookingRepository.findUnavailableItemsInTimeRange(
                itemIds,
                List.of(BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS),
                start,
                end
        );

        if (!unavailableItemIds.isEmpty()) {
            log.warn("Items {} are unavailable during the requested time slot.", unavailableItemIds);
            throw new AppException(ErrorCode.EQUIPMENT_UNAVAILABLE_TIMESLOT, "Một số thiết bị đã được đặt trong khoảng thời gian này: " + String.join(", ", unavailableItemIds));
        }
        log.debug("All requested items are available.");
    }

    // Helper lấy ID item hiện tại của booking (dùng cho update)
    private Set<String> getCurrentBookingItemIds(String bookingId) {
        return bookingEquipmentRepository.findByBookingId(bookingId)
                .stream()
                .map(be -> be.getItem().getId())
                .collect(Collectors.toSet());
    }
    // Helper lấy ID item non-default hiện tại (dùng cho update diff)
    private Set<String> getCurrentNonDefaultBookingItemIds(String bookingId) {
        return bookingEquipmentRepository.findByBookingIdAndIsDefaultEquipment(bookingId, false)
                .stream()
                .map(be -> be.getItem().getId())
                .collect(Collectors.toSet());
    }
}