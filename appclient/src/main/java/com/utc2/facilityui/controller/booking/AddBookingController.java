package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.BookingCreationRequest;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.service.BookingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
//
public class AddBookingController {

    @FXML private Button bntAddBooking;
    @FXML private Button bntCancel;
    @FXML private TextField borrowDate;
    @FXML private TextField expectedDateReturn;
    @FXML private TextField expectedTimeReturn;
    @FXML private Label name; // Hiển thị tên phòng/thiết bị
    @FXML private TextArea reason;
    @FXML private TextField timeBorrow;

    private String targetId; // ID của phòng/thiết bị
    private BookingService bookingService;
    private static final String DATE_FORMAT_INPUT = "yyyy-MM-dd";
    private static final String TIME_FORMAT_INPUT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_INPUT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT_INPUT);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @FXML
    public void initialize() {
        this.bookingService = new BookingService();

        // Thiết lập placeholder text
        setupPlaceholder(borrowDate, "Borrow date (" + DATE_FORMAT_INPUT + ")");
        setupPlaceholder(expectedDateReturn, "Return date (" + DATE_FORMAT_INPUT + ")");
        setupPlaceholder(timeBorrow, "Time borrow (" + TIME_FORMAT_INPUT + ")");
        setupPlaceholder(expectedTimeReturn, "Time return (" + TIME_FORMAT_INPUT + ")");
        setupPlaceholder(reason, "Reason for booking");

        // Gán hành động cho nút ADD
        bntAddBooking.setOnAction(event -> handleAddBooking());
    }

    /**
     * Nhận ID và tên của đối tượng (Phòng/Thiết bị) cần đặt.
     */
    public void setTargetInfo(String id, String targetName) {
        this.targetId = id;
        if (name != null && targetName != null) {
            name.setText(targetName);
        } else if (name != null){
            name.setText("N/A");
        }
        System.out.println("AddBookingController received targetId: " + this.targetId);
    }

    // --- Xử lý sự kiện nút ADD ---
    private void handleAddBooking() {
        // 1. Get user input & Validate
        String borrowDateStr = getValueFromInput(borrowDate, "Borrow date (" + DATE_FORMAT_INPUT + ")");
        String timeBorrowStr = getValueFromInput(timeBorrow, "Time borrow (" + TIME_FORMAT_INPUT + ")");
        String returnDateStr = getValueFromInput(expectedDateReturn, "Return date (" + DATE_FORMAT_INPUT + ")");
        String timeReturnStr = getValueFromInput(expectedTimeReturn, "Time return (" + TIME_FORMAT_INPUT + ")");
        String reasonStr = getValueFromInput(reason, "Reason for booking");

        if (targetId == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Target ID not set."); return;
        }
        if (borrowDateStr == null || timeBorrowStr == null || returnDateStr == null || timeReturnStr == null || reasonStr == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all date, time, and reason fields."); return;
        }

        // 2. Format Date/Time
        LocalDateTime plannedStart;
        LocalDateTime plannedEnd;
        try {
            LocalDate startDate = LocalDate.parse(borrowDateStr, DATE_FORMATTER);
            LocalTime startTime = LocalTime.parse(timeBorrowStr, TIME_FORMATTER);
            LocalDate endDate = LocalDate.parse(returnDateStr, DATE_FORMATTER);
            LocalTime endTime = LocalTime.parse(timeReturnStr, TIME_FORMATTER);

            plannedStart = LocalDateTime.of(startDate, startTime);
            plannedEnd = LocalDateTime.of(endDate, endTime);

            if (!plannedEnd.isAfter(plannedStart)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Time", "Planned end time must be after planned start time."); return;
            }
            if (plannedStart.isBefore(LocalDateTime.now().minusMinutes(1))) { // Cho phép sai số 1 phút
                showAlert(Alert.AlertType.ERROR, "Invalid Time", "Planned start time cannot be in the past."); return;
            }

        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Format", "Invalid date ("+DATE_FORMAT_INPUT+") or time ("+TIME_FORMAT_INPUT+") format.");
            e.printStackTrace(); return;
        }

        // 3. Create Request DTO
        BookingCreationRequest requestDto = new BookingCreationRequest();
        requestDto.setRoomId(this.targetId); // Chỉ dùng roomId theo DTO backend
        requestDto.setPurpose(reasonStr);
        requestDto.setPlannedStartTime(plannedStart.format(ISO_FORMATTER)); // Format sang ISO String
        requestDto.setPlannedEndTime(plannedEnd.format(ISO_FORMATTER));   // Format sang ISO String
        requestDto.setAdditionalEquipmentItemIds(new ArrayList<>()); // Danh sách rỗng
        requestDto.setNote(""); // Note rỗng

        // 4. Call API via Service (Background Thread)
        disableButtons(true);
        showLoadingIndicator(true);

        new Thread(() -> {
            try {
                BookingResponse bookingResponse = bookingService.createBooking(requestDto);

                // 5. Handle Result on JavaFX Thread
                Platform.runLater(() -> {
                    showLoadingIndicator(false);
                    if (bookingResponse != null && bookingResponse.getId() != null) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Booking request sent successfully!");
                        closeDialog();
                        // Trigger navigation/refresh here if implemented
                        System.out.println("TODO: Navigate to My Booking or refresh list.");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Unknown Error", "Request sent but no confirmation received.");
                        disableButtons(false);
                    }
                });

            } catch (IOException | IllegalArgumentException e) {
                Platform.runLater(() -> {
                    showLoadingIndicator(false);
                    showAlert(Alert.AlertType.ERROR, "Booking Failed", "Could not send request: " + e.getMessage());
                    disableButtons(false);
                });
                e.printStackTrace();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showLoadingIndicator(false);
                    showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
                    disableButtons(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    // --- Các hàm Helper ---
    private void setupPlaceholder(TextInputControl input, String placeholder) {
        input.setText(placeholder); input.setStyle("-fx-text-fill: gray;");
        input.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { if (input.getText().equals(placeholder)) { input.setText(""); input.setStyle("-fx-text-fill: black;"); } }
            else { if (input.getText().isEmpty()) { input.setText(placeholder); input.setStyle("-fx-text-fill: gray;"); } } });
    }
    private void setupPlaceholder(TextArea input, String placeholder) {
        input.setText(placeholder); input.setStyle("-fx-text-fill: gray;");
        input.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { if (input.getText().equals(placeholder)) { input.setText(""); input.setStyle("-fx-text-fill: black;"); } }
            else { if (input.getText().isEmpty()) { input.setText(placeholder); input.setStyle("-fx-text-fill: gray;"); } } });
    }
    private String getValueFromInput(TextInputControl input, String placeholder) {
        String text = input.getText(); input.setStyle("-fx-text-fill: black;"); // Reset style
        if (text == null || text.trim().isEmpty() || text.equals(placeholder)) {
            input.setStyle("-fx-border-color: red; -fx-text-fill: black;"); return null; }
        return text.trim();
    }
    private String getValueFromInput(TextArea input, String placeholder) {
        String text = input.getText(); input.setStyle("-fx-text-fill: black;");
        if (text == null || text.trim().isEmpty() || text.equals(placeholder)) {
            input.setStyle("-fx-border-color: red; -fx-text-fill: black;"); return null; }
        return text.trim();
    }
    private void disableButtons(boolean disable) { bntAddBooking.setDisable(disable); bntCancel.setDisable(disable); }
    private void showLoadingIndicator(boolean show) { System.out.println("Loading: " + show); /* Thêm logic hiển thị */ }
    private void showAlert(Alert.AlertType type, String title, String message) { Platform.runLater(() -> { Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait(); }); }
    private void closeDialog() { Stage stage = (Stage) bntAddBooking.getScene().getWindow(); if (stage != null) { stage.close(); } }
    public Button getBntCancel() { return bntCancel; }
    public Button getBntAddBooking() { return bntAddBooking; }
    public Label getName() { return name; }
}