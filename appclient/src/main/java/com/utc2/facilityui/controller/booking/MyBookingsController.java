package com.utc2.facilityui.controller.booking;

// Import các lớp model
import com.utc2.facilityui.model.CardAcceptBooking;
import com.utc2.facilityui.model.CardBooking;
import com.utc2.facilityui.model.CardRejectBooking; // *** Import model mới (Giả định) ***

// Import các controller của component card (Đảm bảo đường dẫn package đúng)
// import com.utc2.facilityui.controller.component.CardAcceptBookingController; // FQCN sẽ được dùng trong code
// import com.utc2.facilityui.controller.component.CardBookingController;
// import com.utc2.facilityui.controller.component.CardRejectBookingController; // *** Controller mới (Giả định) ***

// Import các lớp cần thiết khác
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.service.BookingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller quản lý màn hình hiển thị danh sách các yêu cầu đặt phòng
 * của người dùng đang đăng nhập.
 * Hiển thị PENDING_APPROVAL bằng CardBooking.
 * Hiển thị CONFIRMED bằng CardAcceptBooking.
 * Hiển thị REJECTED bằng CardRejectBooking.
 */
public class MyBookingsController implements Initializable {

    @FXML private VBox bookingListContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Label statusLabel;

    private BookingService bookingService;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    // Hằng số cho các trạng thái cần xử lý
    private static final String PENDING_APPROVAL_STATUS = "PENDING_APPROVAL";
    private static final String CONFIRMED_STATUS = "CONFIRMED";
    private static final String REJECTED_STATUS = "REJECTED"; // Giả định đây là giá trị đúng

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bookingService = new BookingService();
        loadMyBookings();
    }

    /**
     * Tải danh sách các booking có trạng thái PENDING_APPROVAL, CONFIRMED, hoặc REJECTED
     * từ backend và hiển thị chúng lên giao diện bằng các card tương ứng.
     */
    public void loadMyBookings() {
        statusLabel.setText("Đang tải danh sách booking...");
        statusLabel.setVisible(true);
        bookingListContainer.getChildren().clear();

        new Thread(() -> {
            try {
                final List<BookingResponse> allMyBookings = bookingService.getMyBookings();

                final List<BookingResponse> bookingsToShow = allMyBookings.stream()
                        .filter(booking -> booking != null &&
                                (PENDING_APPROVAL_STATUS.equalsIgnoreCase(booking.getStatus()) ||
                                        CONFIRMED_STATUS.equalsIgnoreCase(booking.getStatus()) ||
                                        REJECTED_STATUS.equalsIgnoreCase(booking.getStatus())
                                ))
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    statusLabel.setText("");
                    statusLabel.setVisible(false);

                    if (!bookingsToShow.isEmpty()) {
                        for (BookingResponse booking : bookingsToShow) {
                            try {
                                FXMLLoader loader;
                                Node cardNode;

                                // 6. Tải FXML và Controller phù hợp dựa trên trạng thái booking
                                if (PENDING_APPROVAL_STATUS.equalsIgnoreCase(booking.getStatus())) {
                                    // Tải card booking tiêu chuẩn cho trạng thái chờ duyệt
                                    loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardBooking.fxml"));
                                    cardNode = loader.load();
                                    CardBookingController cardController = loader.getController();

                                    // Tạo model CardBooking và truyền vào controller
                                    CardBooking cardBookingModel = createCardBookingModel(booking);
                                    cardController.setBooking(cardBookingModel);

                                } else if (CONFIRMED_STATUS.equalsIgnoreCase(booking.getStatus())) {
                                    // Tải card booking đã duyệt cho trạng thái đã xác nhận
                                    loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardAcceptBooking.fxml"));
                                    cardNode = loader.load();
                                    CardAcceptBookingController acceptCardController = loader.getController();

                                    // Tạo model CardAcceptBooking và truyền vào controller
                                    CardAcceptBooking cardAcceptBookingModel = createCardAcceptBookingModel(booking);
                                    acceptCardController.setAcceptBooking(cardAcceptBookingModel);
                                } else if (REJECTED_STATUS.equalsIgnoreCase(booking.getStatus())) {

                                    loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardRejectBooking.fxml"));
                                    cardNode = loader.load();
                                    CardRejectBookingController rejectCardController = loader.getController();

                                    // Tạo model CardAcceptBooking và truyền vào controller
                                    CardRejectBooking cardRejectBookingModel = createCardRejectBookingModel(booking);
                                    rejectCardController.setRejectBooking(cardRejectBookingModel);

                                } else {
                                    System.err.println("WARN: Bỏ qua booking với trạng thái không xử lý: " + booking.getStatus() + ", ID: " + booking.getId());
                                    continue;
                                }

                                bookingListContainer.getChildren().add(cardNode);

                            } catch (IOException e) {
                                String bookingIdForError = (booking != null) ? booking.getId() : "UNKNOWN_ID";
                                System.err.println("ERROR: Không thể tải FXML cho booking ID: " + bookingIdForError + " - Status: " + (booking != null ? booking.getStatus() : "N/A") + " - " + e.getMessage());
                                e.printStackTrace();
                                statusLabel.setText("Lỗi khi hiển thị một booking (Không tìm thấy FXML?).");
                                statusLabel.setVisible(true);
                            } catch (Exception e) {
                                String bookingIdForError = (booking != null) ? booking.getId() : "UNKNOWN_ID";
                                System.err.println("ERROR: Lỗi xử lý dữ liệu cho booking ID: " + bookingIdForError + " - Status: " + (booking != null ? booking.getStatus() : "N/A") + " - " + e.getMessage());
                                e.printStackTrace();
                                statusLabel.setText("Lỗi dữ liệu booking.");
                                statusLabel.setVisible(true);
                            }
                        } // Kết thúc vòng lặp for
                    } else {
                        // Cập nhật thông báo nếu không có booking nào phù hợp
                        statusLabel.setText("Bạn không có yêu cầu đặt phòng nào đang chờ, đã duyệt hoặc bị từ chối.");
                        statusLabel.setVisible(true);
                    }
                }); // Kết thúc Platform.runLater
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Lỗi kết nối: Không thể tải danh sách booking.");
                    statusLabel.setVisible(true);
                    System.err.println("ERROR: Lỗi IOException khi gọi BookingService.getMyBookings(): " + e.getMessage());
                    e.printStackTrace();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Đã xảy ra lỗi không mong muốn.");
                    statusLabel.setVisible(true);
                    System.err.println("ERROR: Lỗi không xác định trong luồng tải booking: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start(); // Bắt đầu chạy luồng
    } // Kết thúc phương thức loadMyBookings

    /**
     * Tạo đối tượng model CardBooking từ dữ liệu BookingResponse. (Dùng cho PENDING_APPROVAL)
     */
    private CardBooking createCardBookingModel(BookingResponse booking) {
        CardBooking model = new CardBooking();
        model.setBookingId(booking.getId());
        model.setNameBooking(getValueOrDefault(booking.getRoomName()));
        model.setPurposeBooking(getValueOrDefault(booking.getPurpose()));
        model.setPlannedStartTimeDisplay(formatDateTime(booking.getPlannedStartTime()));
        model.setPlannedEndTimeDisplay(formatDateTime(booking.getPlannedEndTime()));
        model.setRequestBooking(formatDateTime(booking.getCreatedAt()));
        model.setStatusBooking(mapStatus(booking.getStatus()));
        return model;
    }

    /**
     * Tạo đối tượng model CardAcceptBooking từ dữ liệu BookingResponse. (Dùng cho CONFIRMED)
     */
    private CardAcceptBooking createCardAcceptBookingModel(BookingResponse booking) {
        CardAcceptBooking model = new CardAcceptBooking();
        // Điền các trường kế thừa
        model.setBookingId(booking.getId());
        model.setNameBooking(getValueOrDefault(booking.getRoomName()));
        model.setPurposeBooking(getValueOrDefault(booking.getPurpose()));
        model.setPlannedStartTimeDisplay(formatDateTime(booking.getPlannedStartTime()));
        model.setPlannedEndTimeDisplay(formatDateTime(booking.getPlannedEndTime()));
        model.setRequestBooking(formatDateTime(booking.getCreatedAt()));
        model.setStatusBooking(mapStatus(booking.getStatus()));
        return model;
    }

    /**
     * *** MỚI: Tạo đối tượng model CardRejectBooking từ dữ liệu BookingResponse. (Dùng cho REJECTED) ***
     * *** Giả định lớp CardRejectBooking tồn tại và có phương thức setRejectionReason ***
     */
    private CardRejectBooking createCardRejectBookingModel(BookingResponse booking) {
        // *** Giả định lớp CardRejectBooking kế thừa CardBooking và thêm rejectionReason ***
        CardRejectBooking model = new CardRejectBooking();
        // Điền các trường kế thừa
        model.setBookingId(booking.getId());
        model.setNameBooking(getValueOrDefault(booking.getRoomName()));
        model.setPurposeBooking(getValueOrDefault(booking.getPurpose()));
        model.setPlannedStartTimeDisplay(formatDateTime(booking.getPlannedStartTime()));
        model.setPlannedEndTimeDisplay(formatDateTime(booking.getPlannedEndTime()));
        model.setRequestBooking(formatDateTime(booking.getCreatedAt()));
        model.setStatusBooking(mapStatus(booking.getStatus())); // Sẽ là "Đã từ chối"


        return model;
    }

    /**
     * Định dạng đối tượng LocalDateTime thành chuỗi "HH:mm dd/MM/yyyy".
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            try {
                return dateTime.format(DISPLAY_FORMATTER);
            } catch (Exception e) {
                System.err.println("WARN: Lỗi định dạng thời gian: " + dateTime + " - " + e.getMessage());
                return dateTime.toString();
            }
        }
        return "N/A";
    }

    /**
     * Ánh xạ mã trạng thái từ API sang chuỗi thân thiện với người dùng.
     */
    private String mapStatus(String status) {
        if (status == null) return "Không xác định";
        switch (status.toUpperCase()) {
            case PENDING_APPROVAL_STATUS: return "Chờ duyệt";
            case CONFIRMED_STATUS: return "Đã duyệt";
            case REJECTED_STATUS: return "Đã từ chối";
            case "CANCELLED": return "Đã hủy";
            case "COMPLETED": return "Đã hoàn thành";
            case "CHECKED_IN": return "Đã check-in";
            default:
                System.out.println("INFO: Trạng thái không xác định trong mapStatus: " + status);
                return status;
        }
    }

    /**
     * Helper nhỏ để trả về giá trị mặc định "N/A" nếu chuỗi đầu vào là null hoặc rỗng.
     */
    private String getValueOrDefault(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "N/A";
    }
}