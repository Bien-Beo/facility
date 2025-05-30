package com.utc2.facilityui.controller.Notification;

import com.utc2.facilityui.response.NotificationResponse;
import com.utc2.facilityui.service.NotificationApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color; // Không còn dùng trực tiếp nếu không có icon

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CardNotificationController {

    @FXML
    private VBox notificationCardPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Label timestampLabel;
    @FXML
    private Label typeLabel;
    // @FXML private FontAwesomeIconView iconView; // ĐÃ XÓA
    @FXML
    private Button markAsReadButton;
    @FXML
    private Button deleteButton;
    @FXML
    private HBox actionsBox;

    private NotificationResponse notification;
    private NotificationApiService notificationApiService;
    private MyNotificationController parentController;

    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm '-' dd/MM/yyyy");
    private static final int API_SUCCESS_CODE = 0;

    public void initialize() {
        if (this.notificationApiService == null) {
            this.notificationApiService = new NotificationApiService();
        }

        // Thêm sự kiện click cho toàn bộ card (notificationCardPane)
        if (notificationCardPane != null) {
            notificationCardPane.setOnMouseClicked(event -> {
                handleCardClick();
            });
        } else {
            // Dòng này sẽ chỉ xuất hiện nếu FXML chưa được load xong khi initialize được gọi,
            // hoặc fx:id bị sai. Tuy nhiên, với @FXML, nó thường được inject trước initialize.
            System.err.println("CardNotificationController: notificationCardPane is null in initialize. Click event not set (this might be an issue if it persists).");
        }
    }

    private String vietnameseNotificationType(String englishType) {
        if (englishType == null) {
            return "CHUNG";
        }
        // Ánh xạ các type từ Enum NotificationType của bạn (phía server)
        switch (englishType.toUpperCase()) {
            case "BORROW": // Giữ lại nếu server có thể gửi type này
            case "CHECKIN_REMINDER":
                return "MƯỢN PHÒNG";
            case "RETURN": // Giữ lại nếu server có thể gửi type này
            case "CHECKOUT_REMINDER":
                return "TRẢ PHÒNG";
            case "SYSTEM": // Server đang dùng cho "Approved"
                return "HỆ THỐNG";
            case "CHECKOUT_OVERDUE":
                return "QUÁ HẠN";
            case "REPAIR":
                return "BẢO TRÌ";
            // case "APPROVED": // Nếu server bạn có NotificationType.APPROVED
            //     return "ĐÃ DUYỆT";
            default:
                System.out.println("CardNotificationController - vietnameseNotificationType - Unknown type received: " + englishType);
                return englishType; // Trả về type gốc nếu không có bản dịch
        }
    }

    public void setData(NotificationResponse notification, MyNotificationController parentController) {
        this.notification = notification;
        this.parentController = parentController;

        // Đảm bảo các label không null trước khi setText (an toàn hơn)
        if (titleLabel == null || messageLabel == null || typeLabel == null || timestampLabel == null) {
            System.err.println("CardNotificationController - setData - One or more FXML Labels are null. Check fx:id bindings.");
            return;
        }

        String titleToSet = notification.getName() != null ? notification.getName() : "Thông báo";
        String messageToSet = notification.getMessage();

        // Log dữ liệu nhận được (quan trọng để debug)
        System.out.println("CardNotificationController - setData - Received Notification ID: " + notification.getId());
        System.out.println("CardNotificationController - setData - Raw notification.getName(): '" + notification.getName() + "' -> Title to set: '" + titleToSet + "'");
        System.out.println("CardNotificationController - setData - Raw notification.getMessage(): '" + messageToSet + "'");
        System.out.println("CardNotificationController - setData - Raw notification.getRoomId(): '" + notification.getRoomId() + "'");
        System.out.println("CardNotificationController - setData - Raw notification.getBookingId(): '" + notification.getBookingId() + "'");
        System.out.println("CardNotificationController - setData - Raw notification.getType(): '" + notification.getType() + "'");


        titleLabel.setText(titleToSet);
        messageLabel.setText(messageToSet);

        String rawType = notification.getType();
        typeLabel.setText(vietnameseNotificationType(rawType));

        LocalDateTime createdAtDateTime = notification.getCreatedAtLocalDateTime();
        if (createdAtDateTime != null) {
            timestampLabel.setText(createdAtDateTime.format(outputFormatter));
        } else {
            timestampLabel.setText(notification.getCreatedAt() != null ? notification.getCreatedAt() : "Không rõ thời gian");
        }
        updateReadStatusUI();
    }

    private void handleCardClick() {
        if (this.notification == null) {
            System.out.println("Card clicked, but notification data is null.");
            return;
        }

        String nId = this.notification.getId();
        String rId = this.notification.getRoomId(); // Đây là cách bạn lấy roomId
        String bId = this.notification.getBookingId(); // Đây là cách bạn lấy bookingId

        System.out.println("Card Clicked! Notification ID: " + nId);

        if (rId != null && !rId.isEmpty()) {
            System.out.println("  Associated Room ID: " + rId);
            // TODO: Thực hiện hành động với rId (ví dụ: điều hướng đến chi tiết phòng)
            // Ví dụ: if (parentController != null) parentController.navigateToRoomDetails(rId);
            showInfoAlert("Thông tin Liên kết (Test)", "Thông báo này liên quan đến Phòng ID: " + rId +
                    (bId != null ? "\n(và Booking ID: " + bId + ")" : ""));
        } else if (bId != null && !bId.isEmpty()) {
            System.out.println("  No Room ID, but associated Booking ID: " + bId);
            // TODO: Thực hiện hành động với bId
            showInfoAlert("Thông tin Liên kết (Test)", "Thông báo này liên quan đến Booking ID: " + bId);
        } else {
            System.out.println("  No specific Room ID or Booking ID for click action.");
            // showInfoAlert("Thông báo", "Thông báo này không có liên kết phòng hoặc đặt chỗ cụ thể.");
        }
    }

    private void updateReadStatusUI() {
        if (notification == null || notificationCardPane == null || markAsReadButton == null) {
            System.err.println("CardNotificationController - updateReadStatusUI - notification or FXML elements are null.");
            return;
        }

        if (!notification.isUnread()) {
            notificationCardPane.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-opacity: 0.7;");
            markAsReadButton.setVisible(false);
            markAsReadButton.setManaged(false);
        } else {
            notificationCardPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #1976d2; -fx-border-width: 1.5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            markAsReadButton.setVisible(true);
            markAsReadButton.setManaged(true);
            markAsReadButton.setDisable(false);
        }
    }

    @FXML
    private void handleMarkAsRead() {
        if (notification == null || !notification.isUnread() || markAsReadButton == null) {
            return;
        }
        markAsReadButton.setDisable(true);

        notificationApiService.markAsRead(notification.getId())
                .thenAcceptAsync(apiResponse -> {
                    Platform.runLater(() -> {
                        if (apiResponse != null && apiResponse.getCode() == API_SUCCESS_CODE && apiResponse.getResult() != null) {
                            this.notification = apiResponse.getResult();
                            updateReadStatusUI();
                            if (parentController != null) {
                                parentController.decrementUnreadCountAndUpdateDisplay();
                            }
                        } else {
                            String errorMessage = (apiResponse != null && apiResponse.getMessage() != null) ? apiResponse.getMessage() : "Không thể đánh dấu đã đọc.";
                            showErrorAlert("Lỗi", errorMessage);
                            if (this.notification != null && this.notification.isUnread()) { // Kiểm tra lại this.notification
                                markAsReadButton.setDisable(false);
                            }
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showErrorAlert("Lỗi Kết Nối", "Xảy ra lỗi khi kết nối: " + ex.getMessage());
                        if (this.notification != null && this.notification.isUnread()) {
                            markAsReadButton.setDisable(false);
                        }
                    });
                    return null;
                });
    }

    @FXML
    private void handleDelete() {
        if (notification == null || deleteButton == null) {
            return;
        }

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Xác Nhận Xóa");
        confirmationDialog.setHeaderText("Bạn có chắc chắn muốn xóa thông báo này không?");

        String notificationName = notification.getName();
        String notificationMessage = notification.getMessage();
        String contentText = "";

        if (notificationName != null && !notificationName.isEmpty()) {
            contentText += notificationName + ": ";
        }
        if (notificationMessage != null && !notificationMessage.isEmpty()) {
            contentText += notificationMessage.substring(0, Math.min(notificationMessage.length(), 50)) + (notificationMessage.length() > 50 ? "..." : "");
        } else {
            contentText += "(Không có nội dung)";
        }
        confirmationDialog.setContentText(contentText);

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if(markAsReadButton != null) markAsReadButton.setDisable(true);
            deleteButton.setDisable(true);

            notificationApiService.deleteNotification(notification.getId())
                    .thenAcceptAsync(apiResponse -> {
                        Platform.runLater(() -> {
                            if (apiResponse != null && apiResponse.getCode() == API_SUCCESS_CODE) {
                                if (parentController != null) {
                                    parentController.removeNotificationCardUI(notificationCardPane, notification.isUnread());
                                }
                            } else {
                                String errorMessage = (apiResponse != null && apiResponse.getMessage() != null) ? apiResponse.getMessage() : "Không thể xóa thông báo.";
                                showErrorAlert("Lỗi Xóa", errorMessage);
                                deleteButton.setDisable(false); // Bật lại nút xóa nếu thất bại
                                if (notification.isUnread() && markAsReadButton != null) {
                                    markAsReadButton.setDisable(false);
                                }
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            showErrorAlert("Lỗi Kết Nối", "Xảy ra lỗi khi kết nối: " + ex.getMessage());
                            deleteButton.setDisable(false);
                            if (notification.isUnread() && markAsReadButton != null) {
                                markAsReadButton.setDisable(false);
                            }
                        });
                        return null;
                    });
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getNotificationCardPane() {
        return notificationCardPane;
    }
}