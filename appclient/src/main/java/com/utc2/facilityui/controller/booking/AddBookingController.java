package com.utc2.facilityui.controller.booking;

import com.google.gson.Gson;
import com.utc2.facilityui.utils.LocalDateTimeAdapter; // Import lớp Adapter vừa tạo
import com.google.gson.GsonBuilder; // Import GsonBuilder
import java.time.LocalDateTime; // Import LocalDateTime
import com.utc2.facilityui.model.BookingCreationRequest;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.service.BookingService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class AddBookingController {

    @FXML
    private Button bntAddBooking;
    @FXML
    private Button bntCancel;
    @FXML
    private TextField plannedStartTime;
    @FXML
    private TextField plannedEndTime;
    @FXML
    private Label name;
    @FXML
    private TextArea purpose;
    @FXML
    private Text Status;

    private String targetId;
    private BookingService bookingService;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @FXML
    public void initialize() {
        this.bookingService = new BookingService();
        // Thêm gợi ý (prompt text) cho các trường nhập liệu
        plannedStartTime.setPromptText("yyyy-MM-ddTHH:mm:ss");
        plannedEndTime.setPromptText("yyyy-MM-ddTHH:mm:ss");
        purpose.setPromptText("Nhập lý do đặt phòng...");

        // Thêm tooltip hướng dẫn chi tiết (tùy chọn)
        Tooltip startTimeTooltip = new Tooltip("Nhập thời gian bắt đầu theo định dạng: yyyy-MM-ddTHH:mm:ss (ví dụ: 2025-04-17T10:00:00)");
        plannedStartTime.setTooltip(startTimeTooltip);

        Tooltip endTimeTooltip = new Tooltip("Nhập thời gian kết thúc theo định dạng: yyyy-MM-ddTHH:mm:ss (ví dụ: 2025-04-17T11:00:00)");
        plannedEndTime.setTooltip(endTimeTooltip);

        Tooltip purposeTooltip = new Tooltip("Mô tả chi tiết mục đích bạn muốn đặt phòng.");
        purpose.setTooltip(purposeTooltip);
    }

    public void setTargetInfo(String id, String targetName) {
        this.targetId = id;
        if (name != null && targetName != null) {
            name.setText(targetName);
        } else if (name != null) {
            name.setText("N/A");
        }
        System.out.println("AddBookingController received targetId: " + this.targetId);
    }

    @FXML
    public void handleAddBooking(ActionEvent event) {
        // --- BƯỚC KIỂM TRA EVENT HANDLER ---
        System.out.println("handleAddBooking được gọi!");
        // --- KẾT THÚC BƯỚC KIỂM TRA ---
        String startTimeStr = plannedStartTime.getText();
        String endTimeStr = plannedEndTime.getText();
        String reasonStr = purpose.getText();

        if (targetId == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Target ID not set.");
            return;
        }
        if (startTimeStr == null || startTimeStr.trim().isEmpty() || endTimeStr == null || endTimeStr.trim().isEmpty() || reasonStr == null || reasonStr.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "Vui lòng điền đầy đủ thông tin về thời gian và lý do.");
            return;
        }

        LocalDateTime plannedStart;
        LocalDateTime plannedEnd;
        try {
            plannedStart = LocalDateTime.parse(startTimeStr, ISO_FORMATTER);
            plannedEnd = LocalDateTime.parse(endTimeStr, ISO_FORMATTER);

            if (!plannedEnd.isAfter(plannedStart)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Time", "Thời gian kết thúc phải sau thời gian bắt đầu.");
                return;
            }
            if (plannedStart.isBefore(LocalDateTime.now().minusMinutes(1))) {
                showAlert(Alert.AlertType.ERROR, "Invalid Time", "Thời gian bắt đầu không được ở trong quá khứ.");
                return;
            }

        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Format", "Định dạng thời gian không hợp lệ (yyyy-MM-ddTHH:mm:ss).");
            e.printStackTrace();
            return;
        }

        BookingCreationRequest requestDto = new BookingCreationRequest();
        requestDto.setRoomId(this.targetId);
        requestDto.setPurpose(reasonStr);
        requestDto.setPlannedStartTime(plannedStart);
        requestDto.setPlannedEndTime(plannedEnd);
        requestDto.setAdditionalEquipmentItemIds(new ArrayList<>());
        requestDto.setNote("");

        // In ra JSON request body để debug
        String jsonRequestBody = gson.toJson(requestDto);
        System.out.println("JSON Request Body: " + jsonRequestBody);

        disableButtons(true);
        showLoadingIndicator(true);

        new Thread(() -> {
            try {
                BookingResponse bookingResponse = bookingService.createBooking(requestDto);

                Platform.runLater(() -> {
                    showLoadingIndicator(false);
                    if (bookingResponse != null && bookingResponse.getId() != null) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Yêu cầu đặt phòng đã được gửi thành công!");
                        closeDialog();
                        System.out.println("TODO: Chuyển hướng đến trang quản lý đặt phòng hoặc làm mới danh sách.");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Unknown Error", "Yêu cầu đã được gửi nhưng không nhận được phản hồi xác nhận.");
                        disableButtons(false);
                    }
                });

            } catch (IOException | IllegalArgumentException e) {
                Platform.runLater(() -> {
                    showLoadingIndicator(false);
                    showAlert(Alert.AlertType.ERROR, "Booking Failed", "Không thể gửi yêu cầu đặt phòng: " + e.getMessage());
                    disableButtons(false);
                });
                e.printStackTrace();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showLoadingIndicator(false);
                    showAlert(Alert.AlertType.ERROR, "System Error", "Đã xảy ra lỗi không mong muốn.");
                    disableButtons(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage) bntCancel.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void disableButtons(boolean disable) {
        bntAddBooking.setDisable(disable);
        bntCancel.setDisable(disable);
    }

    private void showLoadingIndicator(boolean show) {
        System.out.println("Loading: " + show);
        // Bạn có thể thêm một visual indicator (ví dụ: ProgressBar) ở đây nếu muốn
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void closeDialog() {
        Stage stage = (Stage) bntAddBooking.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public Button getBntCancel() {
        return bntCancel;
    }

    public Button getBntAddBooking() {
        return bntAddBooking;
    }

    public Label getName() {
        return name;
    }
}