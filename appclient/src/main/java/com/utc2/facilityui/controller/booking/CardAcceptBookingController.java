package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardAcceptBooking;
import com.utc2.facilityui.service.BookingService;
import com.utc2.facilityui.response.BookingResponse;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CardAcceptBookingController {

    @FXML private HBox cardContainerHBox; // fx:id cho HBox chính của card, đã thêm ở bước trước
    @FXML private Label nameBookingLabel;
    @FXML private Text userNameText;
    @FXML private HBox purposeContainer; // fx:id cho HBox chứa mục đích
    @FXML private Text purposeBookingText;
    @FXML private Text timeRangeText;
    @FXML private Text requestBookingText;
    @FXML private HBox equipmentContainer; // fx:id cho HBox chứa thiết bị
    @FXML private Text equipmentsListText;
    @FXML private Label equipmentsStaticLabel; // Nhãn "Thiết bị kèm theo:"
    @FXML private Text statusBookingText;
    @FXML private HBox approvedByContainer; // fx:id cho HBox chứa thông tin "Đã duyệt bởi"
    @FXML private Text approvedByUserName;
    @FXML private Button btnCheckIn; // Nút Nhận phòng/Trả phòng

    // FXML elements mới cho thời gian check-in thực tế
    @FXML private HBox actualCheckInTimeContainer;
    @FXML private Text actualCheckInTimeText;

    private CardAcceptBooking booking; // Model chứa dữ liệu của card
    private BookingService bookingService; // Service để gọi API
    private Timeline overdueChecker; // Timeline để kiểm tra quá hạn

    // Hằng số trạng thái (đảm bảo khớp với giá trị từ API server)
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_OVERDUE = "OVERDUE"; // Trạng thái quá hạn

    // Định dạng thời gian
    private static final DateTimeFormatter TOOLTIP_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    private static final DateTimeFormatter ACTUAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");
    // Sử dụng ISO_LOCAL_DATE_TIME cho log để có cả ngày và giờ, dễ theo dõi hơn
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public void setAcceptBooking(CardAcceptBooking bookingData) {
        this.booking = bookingData;
        if (this.bookingService == null) {
            this.bookingService = new BookingService();
        }
        updateCard(); // Cập nhật giao diện ban đầu dựa trên trạng thái từ server/model

        if (this.booking != null) {
            // KIỂM TRA QUÁ HẠN NGAY LẬP TỨC KHI SET DATA
            if (STATUS_IN_PROGRESS.equalsIgnoreCase(this.booking.getStatusBooking()) &&
                    this.booking.getPlannedEndTime() != null &&
                    LocalDateTime.now().isAfter(this.booking.getPlannedEndTime())) {

                System.out.println("[" + LocalDateTime.now().format(ISO_LOCAL_DATE_TIME_FORMATTER) +
                        "] NGAY KHI SET DATA, phát hiện QUÁ HẠN cho ID: " + this.booking.getBookingId() + ". Thời gian kết thúc dự kiến: " + this.booking.getPlannedEndTime().format(ISO_LOCAL_DATE_TIME_FORMATTER));
                this.booking.setStatusBooking(STATUS_OVERDUE);
                updateCard(); // Cập nhật lại giao diện ngay lập tức với trạng thái OVERDUE
                stopOverdueChecker(); // Dừng mọi checker, không cần chạy Timeline nữa vì đã quá hạn
            }
            // Nếu không quá hạn ngay, và đang IN_PROGRESS, thì mới bắt đầu Timeline
            else if (STATUS_IN_PROGRESS.equalsIgnoreCase(this.booking.getStatusBooking())) {
                startOverdueChecker();
            } else {
                // Các trạng thái khác (CONFIRMED, COMPLETED, CANCELLED, REJECTED, hoặc OVERDUE từ server)
                stopOverdueChecker();
            }
        } else {
            stopOverdueChecker(); // Booking is null
        }
    }

    private void startOverdueChecker() {
        stopOverdueChecker(); // Luôn dừng cái cũ trước khi bắt đầu cái mới

        if (booking == null || booking.getPlannedEndTime() == null) {
            // System.out.println("Không thể bắt đầu overdueChecker: booking hoặc plannedEndTime là null cho ID: " + (booking != null ? booking.getBookingId() : "unknown"));
            return;
        }
        // Chỉ bắt đầu nếu trạng thái hiện tại thực sự là IN_PROGRESS
        if (!STATUS_IN_PROGRESS.equalsIgnoreCase(booking.getStatusBooking())) {
            // System.out.println("Không bắt đầu overdueChecker vì trạng thái không phải IN_PROGRESS cho ID: " + booking.getBookingId());
            return;
        }

        System.out.println("[" + LocalDateTime.now().format(ISO_LOCAL_DATE_TIME_FORMATTER) + "] Bắt đầu overdueChecker cho booking ID: " + booking.getBookingId() + ", Thời gian kết thúc dự kiến: " + booking.getPlannedEndTime().format(ISO_LOCAL_DATE_TIME_FORMATTER));
        overdueChecker = new Timeline(new KeyFrame(Duration.seconds(15), event -> { // Kiểm tra mỗi 15 giây (có thể điều chỉnh)
            // Kiểm tra lại booking và trạng thái trong mỗi lần tick của Timeline
            if (this.booking != null && STATUS_IN_PROGRESS.equalsIgnoreCase(this.booking.getStatusBooking())) {
                if (LocalDateTime.now().isAfter(this.booking.getPlannedEndTime())) {
                    System.out.println("[" + LocalDateTime.now().format(ISO_LOCAL_DATE_TIME_FORMATTER) + "] Client phát hiện QUÁ HẠN (Timeline) cho booking ID: " + this.booking.getBookingId());
                    this.booking.setStatusBooking(STATUS_OVERDUE); // Cập nhật trạng thái trong model của card
                    // Đảm bảo updateCard được gọi trên UI thread, Timeline mặc định làm vậy.
                    updateCard(); // Cập nhật giao diện để hiển thị trạng thái OVERDUE
                    stopOverdueChecker(); // Dừng kiểm tra một khi đã chuyển sang OVERDUE
                }
            } else {
                // Nếu trạng thái không còn là IN_PROGRESS (ví dụ: đã COMPLETED bởi một hành động khác), dừng kiểm tra
                stopOverdueChecker();
            }
        }));
        overdueChecker.setCycleCount(Animation.INDEFINITE);
        overdueChecker.play();
    }

    /**
     * Dừng Timeline kiểm tra quá hạn.
     * Phương thức này được gọi nội bộ và có thể được gọi từ bên ngoài (ví dụ bởi MyBookingsController)
     * khi card này không còn được sử dụng hoặc bị loại bỏ khỏi giao diện.
     */
    public void stopPotentialChecker() {
        // System.out.println("Yêu cầu dừng potential checker cho booking ID: " + (booking != null ? booking.getBookingId() : " (booking is null)"));
        stopOverdueChecker();
    }

    private void stopOverdueChecker() {
        if (overdueChecker != null) {
            overdueChecker.stop();
            overdueChecker = null; // Giải phóng tham chiếu
            System.out.println("[" + LocalDateTime.now().format(ISO_LOCAL_DATE_TIME_FORMATTER) + "] Đã dừng overdueChecker cho booking ID: " + (booking != null ? booking.getBookingId() : "unknown"));
        }
    }

    private void updateCard() {
        if (booking != null) {
            // Reset styles trước khi áp dụng cái mới
            if (cardContainerHBox != null) {
                cardContainerHBox.getStyleClass().remove("card-overdue-style");
                // Thêm lệnh xóa cho các class style trạng thái khác nếu bạn có, ví dụ:
                // cardContainerHBox.getStyleClass().remove("card-confirmed-style");
                // cardContainerHBox.getStyleClass().remove("card-in-progress-style");
            }
            // Kiểm tra null cho statusBookingText trước khi thao tác
            if (statusBookingText != null) {
                statusBookingText.getStyleClass().clear(); // Xóa class CSS cũ của status text
            } else {
                // Ghi log nếu statusBookingText là null, chỉ để debug
                // System.err.println("statusBookingText is null in updateCard for booking: " + (booking.getBookingId() != null ? booking.getBookingId() : "ID_UNKNOWN"));
            }


            // Cập nhật thông tin cơ bản
            nameBookingLabel.setText(booking.getNameBooking() != null ? booking.getNameBooking() : "Chưa có tên phòng");
            userNameText.setText(booking.getUserName() != null ? booking.getUserName() : "Không rõ người đặt");

            boolean hasPurpose = booking.getPurposeBooking() != null && !booking.getPurposeBooking().isEmpty();
            purposeBookingText.setText(hasPurpose ? booking.getPurposeBooking() : "");
            if (purposeContainer != null) {
                purposeContainer.setVisible(hasPurpose);
                purposeContainer.setManaged(hasPurpose);
            }

            timeRangeText.setText(booking.getTimeRangeDisplay() != null ? booking.getTimeRangeDisplay() : "N/A");
            requestBookingText.setText(booking.getRequestBooking() != null ? booking.getRequestBooking() : "N/A");

            boolean hasEquipments = booking.getEquipmentsDisplay() != null && !booking.getEquipmentsDisplay().isEmpty();
            equipmentsListText.setText(hasEquipments ? booking.getEquipmentsDisplay() : "");
            if (equipmentContainer != null) {
                equipmentContainer.setVisible(hasEquipments);
                equipmentContainer.setManaged(hasEquipments);
            }
            if (equipmentsStaticLabel != null) {
                equipmentsStaticLabel.setVisible(hasEquipments);
                equipmentsStaticLabel.setManaged(hasEquipments);
            }


            // Mặc định ẩn các HBox "Đã duyệt bởi" và "Check-in lúc"
            if (approvedByContainer != null) {
                approvedByContainer.setVisible(false);
                approvedByContainer.setManaged(false);
            }
            if (actualCheckInTimeContainer != null) {
                actualCheckInTimeContainer.setVisible(false);
                actualCheckInTimeContainer.setManaged(false);
            }

            String currentStatus = booking.getStatusBooking();
            // Kiểm tra null cho btnCheckIn trước khi thao tác
            if (btnCheckIn != null) {
                btnCheckIn.setTooltip(null); // Xóa tooltip cũ của nút
            } else {
                // System.err.println("btnCheckIn is null in updateCard for booking: " + (booking.getBookingId() != null ? booking.getBookingId() : "ID_UNKNOWN"));
            }

            // Xử lý hiển thị dựa trên trạng thái
            if (STATUS_IN_PROGRESS.equalsIgnoreCase(currentStatus)) {
                if (statusBookingText != null) {
                    statusBookingText.setText(mapStatusToVietnamese(currentStatus)); // "Đang sử dụng"
                    statusBookingText.getStyleClass().add("status-in-progress");
                }

                if (btnCheckIn != null) {
                    btnCheckIn.setText("TRẢ PHÒNG");
                    btnCheckIn.setStyle("-fx-background-color: #30348c; -fx-text-fill: white;");
                    btnCheckIn.getStyleClass().removeAll("button-checkIn"); // Xóa class CSS cũ (nếu có)
                    btnCheckIn.getStyleClass().add("button-checkOut");    // Thêm class CSS mới (nếu có)
                    btnCheckIn.setOnAction(event -> handleCheckOut());
                    btnCheckIn.setVisible(true);
                    btnCheckIn.setManaged(true);
                    btnCheckIn.setDisable(false); // Luôn cho phép trả phòng khi đang sử dụng
                }

                if (approvedByContainer != null) {
                    approvedByContainer.setVisible(true);
                    approvedByContainer.setManaged(true);
                    approvedByUserName.setText(booking.getApprovedByUserName() != null && !booking.getApprovedByUserName().isEmpty() ? booking.getApprovedByUserName() : "Admin");
                }

                if (actualCheckInTimeContainer != null) {
                    actualCheckInTimeContainer.setVisible(true);
                    actualCheckInTimeContainer.setManaged(true);
                    if (booking.getActualCheckInTime() != null) {
                        actualCheckInTimeText.setText(booking.getActualCheckInTime().format(ACTUAL_TIME_FORMATTER));
                    } else {
                        actualCheckInTimeText.setText("Chưa ghi nhận"); // Phòng trường hợp actualCheckInTime là null
                    }
                }
                // Timeline được quản lý bởi setAcceptBooking và handleCheckIn, không gọi start/stop ở đây trực tiếp nữa

            } else if (STATUS_CONFIRMED.equalsIgnoreCase(currentStatus)) {
                if (statusBookingText != null) {
                    statusBookingText.setText(mapStatusToVietnamese(currentStatus)); // "Đã duyệt"
                    statusBookingText.getStyleClass().add("status-confirmed"); // Hoặc class bạn dùng cho "Đã duyệt"
                }

                if (btnCheckIn != null) {
                    btnCheckIn.setText("NHẬN PHÒNG");
                    btnCheckIn.setStyle(""); // Xóa style inline để FXML/CSS class có tác dụng
                    btnCheckIn.getStyleClass().setAll("button", "button-checkIn", "shadow"); // Áp dụng lại các class từ FXML
                    btnCheckIn.setOnAction(event -> handleCheckIn());
                    btnCheckIn.setVisible(true);
                    btnCheckIn.setManaged(true);

                    LocalDateTime plannedStart = booking.getPlannedStartTime(); // Lấy từ model CardAcceptBooking
                    if (plannedStart != null) {
                        if (LocalDateTime.now().isBefore(plannedStart)) {
                            btnCheckIn.setDisable(true);
                            Tooltip tooltip = new Tooltip("Chưa đến giờ nhận phòng (Bắt đầu lúc: " + plannedStart.format(TOOLTIP_TIME_FORMATTER) + ")");
                            btnCheckIn.setTooltip(tooltip);
                        } else {
                            btnCheckIn.setDisable(false);
                        }
                    } else {
                        btnCheckIn.setDisable(true); // Nếu không có thông tin giờ, vô hiệu hóa
                        Tooltip tooltip = new Tooltip("Không rõ thời gian bắt đầu dự kiến.");
                        btnCheckIn.setTooltip(tooltip);
                    }
                }

                if (approvedByContainer != null) {
                    approvedByContainer.setVisible(true);
                    approvedByContainer.setManaged(true);
                    approvedByUserName.setText(booking.getApprovedByUserName() != null && !booking.getApprovedByUserName().isEmpty() ? booking.getApprovedByUserName() : "Admin");
                }
                stopOverdueChecker(); // Dừng kiểm tra nếu không phải IN_PROGRESS

            } else if (STATUS_COMPLETED.equalsIgnoreCase(currentStatus)) {
                if (statusBookingText != null) {
                    statusBookingText.setText(mapStatusToVietnamese(currentStatus)); // "Đã hoàn thành"
                    statusBookingText.getStyleClass().add("status-completed");
                }
                if (btnCheckIn != null) {
                    btnCheckIn.setVisible(false); // Ẩn nút khi đã hoàn thành
                    btnCheckIn.setManaged(false);
                }

                if (approvedByContainer != null) {
                    approvedByContainer.setVisible(true);
                    approvedByContainer.setManaged(true);
                    approvedByUserName.setText(booking.getApprovedByUserName() != null && !booking.getApprovedByUserName().isEmpty() ? booking.getApprovedByUserName() : "Admin");
                }

                // Tùy chọn: Hiển thị "Check-in lúc:" cho trạng thái COMPLETED
                if (actualCheckInTimeContainer != null && booking.getActualCheckInTime() != null) {
                    actualCheckInTimeContainer.setVisible(true);
                    actualCheckInTimeContainer.setManaged(true);
                    actualCheckInTimeText.setText(booking.getActualCheckInTime().format(ACTUAL_TIME_FORMATTER));
                }
                stopOverdueChecker(); // Dừng kiểm tra khi đã hoàn thành

            } else if (STATUS_OVERDUE.equalsIgnoreCase(currentStatus)) {
                System.out.println("updateCard() ĐANG XỬ LÝ trạng thái OVERDUE cho ID: " + booking.getBookingId());
                if (statusBookingText != null) {
                    statusBookingText.setText(mapStatusToVietnamese(currentStatus)); // "Quá hạn"
                    statusBookingText.getStyleClass().add("status-overdue");
                }
                if(approvedByContainer != null) {
                    approvedByContainer.setVisible(false);
                    approvedByContainer.setManaged(false);
                }
                if (cardContainerHBox != null) {
                    System.out.println("Áp dụng style 'card-overdue-style' cho HBox của booking ID: " + booking.getBookingId());
                    cardContainerHBox.getStyleClass().add("card-overdue-style"); // CSS class cho viền và nền card
                } else {
                    System.err.println("LỖI: cardContainerHBox là null khi updateCard cho trạng thái OVERDUE, booking ID: " + (booking.getBookingId() != null ? booking.getBookingId() : "ID_UNKNOWN"));
                }

                if (btnCheckIn != null) {
                    btnCheckIn.setText("TRẢ PHÒNG");
                    btnCheckIn.setStyle("-fx-background-color: #E65100; -fx-text-fill: white;"); // Màu cam đậm cho nút
                    btnCheckIn.getStyleClass().removeAll("button-checkIn");
                    btnCheckIn.getStyleClass().add("button-checkOut"); // Giữ nguyên hoặc tạo class mới "button-checkout-overdue"
                    btnCheckIn.setOnAction(event -> handleCheckOut());
                    btnCheckIn.setVisible(true);
                    btnCheckIn.setManaged(true);
                    btnCheckIn.setDisable(false); // Cho phép trả phòng
                }

                if (actualCheckInTimeContainer != null) {
                    actualCheckInTimeContainer.setVisible(true);
                    actualCheckInTimeContainer.setManaged(true);
                    if (booking.getActualCheckInTime() != null) {
                        actualCheckInTimeText.setText(booking.getActualCheckInTime().format(ACTUAL_TIME_FORMATTER));
                    } else {
                        actualCheckInTimeText.setText("Chưa ghi nhận");
                    }
                }
                // Timeline đã tự dừng khi chuyển sang OVERDUE (do logic trong startOverdueChecker)
                // hoặc nó đã được dừng bởi logic kiểm tra tức thời trong setAcceptBooking/handleCheckIn.
            } else {
                // Xử lý các trạng thái khác (PENDING_APPROVAL, CANCELLED, REJECTED...)
                if (statusBookingText != null) {
                    statusBookingText.setText(currentStatus != null ? mapStatusToVietnamese(currentStatus) : "Không xác định");
                    statusBookingText.getStyleClass().add("status-default"); // Một class CSS mặc định
                }
                if (btnCheckIn != null) {
                    btnCheckIn.setVisible(false);
                    btnCheckIn.setManaged(false);
                }

                if (("REJECTED".equalsIgnoreCase(currentStatus) || "CANCELLED".equalsIgnoreCase(currentStatus))
                        && approvedByContainer != null ) {
                    approvedByContainer.setVisible(true);
                    approvedByContainer.setManaged(true);
                    approvedByUserName.setText(booking.getApprovedByUserName() != null && !booking.getApprovedByUserName().isEmpty() ? booking.getApprovedByUserName() : "N/A");
                }
                stopOverdueChecker(); // Dừng kiểm tra cho các trạng thái không liên quan
            }
        } else {
            // Xử lý khi booking là null (ví dụ: hiển thị thông báo lỗi trên card)
            nameBookingLabel.setText("Lỗi dữ liệu");
            userNameText.setText("N/A");
            purposeBookingText.setText("N/A");
            timeRangeText.setText("N/A");
            requestBookingText.setText("N/A");
            equipmentsListText.setText("N/A");
            if(statusBookingText != null) statusBookingText.setText("N/A");
            if (approvedByUserName != null) approvedByUserName.setText("N/A");
            if (actualCheckInTimeText != null) actualCheckInTimeText.setText("N/A");


            if (purposeContainer != null) { purposeContainer.setVisible(false); purposeContainer.setManaged(false); }
            if (equipmentContainer != null) { equipmentContainer.setVisible(false); equipmentContainer.setManaged(false); }
            if (approvedByContainer != null) { approvedByContainer.setVisible(false); approvedByContainer.setManaged(false); }
            if (actualCheckInTimeContainer != null) { actualCheckInTimeContainer.setVisible(false); actualCheckInTimeContainer.setManaged(false); }

            if (cardContainerHBox != null) {
                cardContainerHBox.getStyleClass().remove("card-overdue-style");
            }
            if (btnCheckIn != null) {
                btnCheckIn.setVisible(false);
                btnCheckIn.setManaged(false);
            }
            stopOverdueChecker(); // Dừng kiểm tra nếu booking bị null
        }
    }

    private String mapStatusToVietnamese(String apiStatus) {
        if (apiStatus == null) return "Không xác định";
        switch (apiStatus.toUpperCase()) {
            case "PENDING_APPROVAL": return "Chờ duyệt";
            case "CONFIRMED": return "Đã duyệt";
            case "IN_PROGRESS": return "Đang sử dụng";
            case "COMPLETED": return "Đã hoàn thành";
            case "CANCELLED": return "Đã hủy";
            case "REJECTED": return "Bị từ chối";
            case "OVERDUE": return "Quá hạn";
            default: return apiStatus;
        }
    }

    @FXML
    private void handleCheckIn() {
        if (booking == null || booking.getBookingId() == null) {
            showErrorAlert("Lỗi", "Không có thông tin đặt phòng để nhận phòng.");
            return;
        }
        if (btnCheckIn != null) btnCheckIn.setDisable(true);

        new Thread(() -> {
            try {
                BookingResponse updatedBookingResponse = bookingService.checkInBooking(booking.getBookingId());
                Platform.runLater(() -> {
                    if (updatedBookingResponse != null && STATUS_IN_PROGRESS.equalsIgnoreCase(updatedBookingResponse.getStatus())) {
                        showSuccessAlert("Thành công", "Đã nhận phòng!");
                        booking.setStatusBooking(updatedBookingResponse.getStatus());
                        booking.setActualCheckInTime(updatedBookingResponse.getActualCheckInTime());
                        if (updatedBookingResponse.getApprovedByUserName() != null) {
                            booking.setApprovedByUserName(updatedBookingResponse.getApprovedByUserName());
                        }
                        // Cập nhật plannedEndTime từ server nếu có, để overdueChecker dùng thông tin mới nhất
                        if (updatedBookingResponse.getPlannedEndTime() != null) {
                            booking.setPlannedEndTime(updatedBookingResponse.getPlannedEndTime());
                        }
                        updateCard(); // Cập nhật giao diện với trạng thái IN_PROGRESS

                        // KIỂM TRA QUÁ HẠN NGAY LẬP TỨC SAU KHI CHECK-IN
                        if (this.booking.getPlannedEndTime() != null &&
                                LocalDateTime.now().isAfter(this.booking.getPlannedEndTime())) {
                            System.out.println("[" + LocalDateTime.now().format(ISO_LOCAL_DATE_TIME_FORMATTER) +
                                    "] NGAY SAU KHI CHECK-IN, phát hiện QUÁ HẠN cho ID: " + this.booking.getBookingId() + ". Thời gian kết thúc dự kiến: " + this.booking.getPlannedEndTime().format(ISO_LOCAL_DATE_TIME_FORMATTER));
                            this.booking.setStatusBooking(STATUS_OVERDUE);
                            updateCard(); // Cập nhật lại giao diện ngay lập tức với trạng thái OVERDUE
                            stopOverdueChecker(); // Dừng mọi checker, không cần chạy Timeline nữa
                        } else {
                            // Chỉ bắt đầu Timeline nếu chưa quá hạn ngay sau khi check-in
                            startOverdueChecker();
                        }
                    } else {
                        String errorMsg = "Nhận phòng không thành công.";
                        String errorDetail = "";
                        if (updatedBookingResponse != null && updatedBookingResponse.getStatus() != null) {
                            errorDetail = "Trạng thái trả về: " + mapStatusToVietnamese(updatedBookingResponse.getStatus()) + ".";
                        } else if (updatedBookingResponse == null) {
                            errorDetail = "Không nhận được phản hồi cập nhật từ máy chủ.";
                        }
                        showErrorAlert("Lỗi Nhận Phòng", errorMsg + " " + errorDetail);
                        // Kích hoạt lại nút và cập nhật card để nó có thể hiển thị tooltip đúng nếu vẫn chưa đến giờ
                        if (btnCheckIn != null) btnCheckIn.setDisable(false); // Cho phép thử lại
                        updateCard(); // Gọi updateCard để nó tự quyết định trạng thái disable của nút
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showErrorAlert("Lỗi Nhận Phòng", "Không thể nhận phòng: " + e.getMessage());
                    if (btnCheckIn != null) btnCheckIn.setDisable(false);
                    updateCard(); // Gọi updateCard để nó tự quyết định trạng thái disable của nút
                });
            } catch (Exception e) { // Các lỗi không lường trước khác
                Platform.runLater(() -> {
                    showErrorAlert("Lỗi không xác định", "Đã xảy ra lỗi khi nhận phòng: " + e.getMessage());
                    e.printStackTrace(); // In stack trace ra console để debug
                    if (btnCheckIn != null) btnCheckIn.setDisable(false);
                    updateCard(); // Gọi updateCard để nó tự quyết định trạng thái disable của nút
                });
            }
        }).start();
    }

    private void handleCheckOut() {
        if (booking == null || booking.getBookingId() == null) {
            showErrorAlert("Lỗi", "Không có thông tin đặt phòng để trả phòng.");
            return;
        }
        if (btnCheckIn != null) btnCheckIn.setDisable(true); // Vô hiệu hóa nút ngay để tránh click nhiều lần

        new Thread(() -> {
            try {
                // System.out.println("Gọi API Check-out cho booking ID: " + booking.getBookingId());
                BookingResponse updatedBookingResponse = bookingService.checkOutBooking(booking.getBookingId()); // Gọi service của client

                Platform.runLater(() -> {
                    if (updatedBookingResponse != null && STATUS_COMPLETED.equalsIgnoreCase(updatedBookingResponse.getStatus())) {
                        showSuccessAlert("Thành công", "Đã trả phòng!");
                        stopOverdueChecker(); // Dừng kiểm tra quá hạn khi trả phòng thành công

                        // Yêu cầu MyBookingsController làm mới danh sách
                        MyBookingsController myBookingsCtrl = MyBookingsController.getInstance();
                        if (myBookingsCtrl != null) {
                            myBookingsCtrl.refreshBookingsListPublic();
                        } else {
                            System.err.println("[CardAcceptBookingController] Không thể lấy instance của MyBookingsController để làm mới danh sách.");
                            booking.setStatusBooking(updatedBookingResponse.getStatus());
                            if(updatedBookingResponse.getActualCheckOutTime() != null) {
                                booking.setActualCheckOutTime(updatedBookingResponse.getActualCheckOutTime());
                            }
                            updateCard(); // Cập nhật card hiện tại (sẽ ẩn nút đi)
                        }
                    } else {
                        String errorMsg = "Trả phòng không thành công.";
                        String errorDetail = "";
                        if (updatedBookingResponse != null && updatedBookingResponse.getStatus() != null) {
                            errorDetail = "Trạng thái trả về: " + mapStatusToVietnamese(updatedBookingResponse.getStatus()) + ".";
                        } else if (updatedBookingResponse == null) {
                            errorDetail = "Không nhận được phản hồi cập nhật từ máy chủ.";
                        }
                        showErrorAlert("Lỗi Trả Phòng", errorMsg + " " + errorDetail);
                        // Nếu trả phòng thất bại, kích hoạt lại nút nếu booking vẫn còn IN_PROGRESS hoặc OVERDUE
                        String currentBookingStatus = booking.getStatusBooking();
                        if (btnCheckIn != null && (STATUS_IN_PROGRESS.equalsIgnoreCase(currentBookingStatus) || STATUS_OVERDUE.equalsIgnoreCase(currentBookingStatus))) {
                            btnCheckIn.setDisable(false);
                        } else {
                            updateCard();
                        }
                    }
                });
            } catch (IOException e) { // Lỗi giao tiếp hoặc lỗi nghiệp vụ được gói trong IOException bởi service
                Platform.runLater(() -> {
                    showErrorAlert("Lỗi Trả Phòng", "Không thể trả phòng: " + e.getMessage());
                    // Kích hoạt lại nút nếu booking vẫn còn IN_PROGRESS hoặc OVERDUE
                    String currentBookingStatus = booking.getStatusBooking();
                    if (btnCheckIn != null && (STATUS_IN_PROGRESS.equalsIgnoreCase(currentBookingStatus) || STATUS_OVERDUE.equalsIgnoreCase(currentBookingStatus))) {
                        btnCheckIn.setDisable(false);
                    }
                });
            } catch (Exception e) { // Các lỗi không lường trước khác
                Platform.runLater(() -> {
                    showErrorAlert("Lỗi không xác định", "Đã xảy ra lỗi khi trả phòng: " + e.getMessage());
                    e.printStackTrace(); // In stack trace ra console để debug
                    String currentBookingStatus = booking.getStatusBooking();
                    if (btnCheckIn != null && (STATUS_IN_PROGRESS.equalsIgnoreCase(currentBookingStatus) || STATUS_OVERDUE.equalsIgnoreCase(currentBookingStatus))) {
                        btnCheckIn.setDisable(false);
                    }
                });
            }
        }).start();
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}