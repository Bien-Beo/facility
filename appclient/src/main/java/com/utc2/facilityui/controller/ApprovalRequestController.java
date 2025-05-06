// --- File: src/main/java/com/utc2/facilityui/controller/ApprovalRequestController.java ---
package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
// *** THÊM IMPORT CHO Page ***
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.service.BookingService;

import javafx.application.Platform; // Import Platform
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections; // Thêm import này
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ApprovalRequestController implements Initializable {

    @FXML private VBox requestContainer;
    @FXML private Label mainTitleLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private final BookingService bookingService = new BookingService();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // --- THÊM BIẾN PHÂN TRANG (NẾU CHƯA CÓ) ---
    private int currentPage = 0; // Trang bắt đầu (0-based)
    private int pageSize = 10; // Kích thước trang (ví dụ)
    // Bạn có thể thêm các nút Previous/Next và ComboBox page size vào FXML nếu muốn phân trang đầy đủ

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (mainTitleLabel != null) mainTitleLabel.setText("Pending Booking Approvals");
        if (loadingIndicator != null) loadingIndicator.setVisible(false);
        System.out.println("ApprovalRequestController Initialized. Loading requests..."); // DEBUG
        loadPendingRequestsFromServer();
    }

    private void loadPendingRequestsFromServer() {
        if (loadingIndicator != null) loadingIndicator.setVisible(true);
        requestContainer.getChildren().clear(); // Xóa card cũ trước khi tải mới

        // *** THAY ĐỔI KIỂU DỮ LIỆU CỦA TASK ***
        Task<Page<BookingResponse>> loadTask = new Task<>() {
            @Override
            protected Page<BookingResponse> call() throws Exception {
                System.out.println("[Task] Calling bookingService.getAllBookings() with pagination...");

                // --- THAY THẾ DÒNG NÀY ---
                // List<BookingResponse> result = bookingService.getAllBookings(); // <<< DÒNG CŨ BỊ LỖI

                // +++ BẰNG DÒNG NÀY +++
                // Gọi với null filters và trạng thái phân trang hiện tại
                Page<BookingResponse> resultPage = bookingService.getAllBookings(
                        null, // roomId
                        null, // month
                        null, // year
                        null, // userId
                        currentPage, // page (0-based)
                        pageSize     // size
                );
                // +++ KẾT THÚC THAY THẾ +++

                System.out.println("[Task] bookingService.getAllBookings() returned page.");
                return resultPage; // Trả về đối tượng Page
            }

            @Override
            protected void succeeded() {
                Page<BookingResponse> resultPage = getValue(); // Lấy đối tượng Page
                List<BookingResponse> allBookingsOnPage = (resultPage != null) ? resultPage.getContent() : null;

                System.out.println("[UI Thread] Fetched bookings page count (before filter): " + (allBookingsOnPage != null ? allBookingsOnPage.size() : "null list"));

                Platform.runLater(() -> {
                    // Xóa nội dung cũ (quan trọng khi tải lại trang hoặc refresh)
                    requestContainer.getChildren().clear();

                    if (allBookingsOnPage != null && !allBookingsOnPage.isEmpty()) {
                        // Lọc các booking có trạng thái PENDING_APPROVAL (client-side)
                        List<BookingResponse> pendingBookings = allBookingsOnPage.stream()
                                .filter(booking -> "PENDING_APPROVAL".equalsIgnoreCase(booking.getStatus()))
                                .collect(Collectors.toList());

                        System.out.println("[UI Thread] Pending bookings count on this page (after filter): " + pendingBookings.size());

                        if (!pendingBookings.isEmpty()) {
                            pendingBookings.forEach(ApprovalRequestController.this::createAndAddRequestCardUI);
                        } else {
                            System.out.println("[UI Thread] No pending approvals found on this page after filtering.");
                            requestContainer.getChildren().add(new Label("No pending approvals found on this page."));
                        }
                        // TODO: Cập nhật các control phân trang (nút Previous/Next) dựa vào resultPage.isFirst(), resultPage.isLast(), resultPage.getTotalPages()
                    } else {
                        System.out.println("[UI Thread] No bookings found on this page or list was null/empty.");
                        // Kiểm tra xem tổng số phần tử có phải là 0 không
                        boolean trulyEmpty = (resultPage != null && resultPage.getTotalElements() == 0);
                        requestContainer.getChildren().add(new Label(trulyEmpty ? "No pending approvals found." : "No bookings found or failed to load."));
                        // TODO: Cập nhật các control phân trang (vô hiệu hóa hết)
                    }
                    if (loadingIndicator != null) {
                        System.out.println("[UI Thread] Hiding loading indicator (success)."); // DEBUG
                        loadingIndicator.setVisible(false);
                    }
                });
            }

            @Override
            protected void failed() {
                Throwable exc = getException();
                System.err.println("[Task] Failed to load bookings!"); // DEBUG
                if (exc != null) {
                    exc.printStackTrace(); // In lỗi đầy đủ ra console
                }
                Platform.runLater(() -> {
                    requestContainer.getChildren().clear(); // Xóa card cũ
                    showError("Load Failed", "Could not load bookings: " + (exc != null ? exc.getMessage() : "Unknown error"));
                    requestContainer.getChildren().add(new Label("Error loading requests. Check console for details."));
                    if (loadingIndicator != null) {
                        System.out.println("[UI Thread] Hiding loading indicator (failure)."); // DEBUG
                        loadingIndicator.setVisible(false);
                    }
                    // TODO: Cập nhật các control phân trang (vô hiệu hóa hết)
                });
            }
        };
        new Thread(loadTask).start(); // Bắt đầu chạy task nền
    }

    // ... (Các phương thức còn lại: createAndAddRequestCardUI, mapToApprovalRequest, handleAccept, handleReject, ...)
    // Bạn cũng cần thêm các phương thức xử lý sự kiện cho nút Previous/Next nếu bạn thêm chúng vào FXML
    // Ví dụ:
    // @FXML private void handlePreviousApprovalPage(ActionEvent event) {
    //     if (currentPage > 0) {
    //         currentPage--;
    //         loadPendingRequestsFromServer();
    //     }
    // }
    // @FXML private void handleNextApprovalPage(ActionEvent event) {
    //     // Cần biết tổng số trang (totalPages) từ resultPage
    //     // if (currentPage < totalPages - 1) {
    //     //    currentPage++;
    //     //    loadPendingRequestsFromServer();
    //     // }
    // }

    // mapToApprovalRequest (Giữ nguyên như trước)
    private ApprovalRequest mapToApprovalRequest(BookingResponse apiResponse) {
        System.out.println("  Mapping BookingResponse ID: " + apiResponse.getId()
                + ", Status: " + apiResponse.getStatus()
                + ", User: '" + apiResponse.getUserName() + "'"
                + ", Room: '" + apiResponse.getRoomName() + "'"
                + ", Start: " + apiResponse.getPlannedStartTime()
                + ", End: " + apiResponse.getPlannedEndTime()
                + ", Purpose: '" + apiResponse.getPurpose() + "'"
                + ", CreatedAt: " + apiResponse.getCreatedAt());

        String facilityName = apiResponse.getRoomName(); if (facilityName == null || facilityName.trim().isEmpty()) facilityName = "Thiết bị (không có phòng)";
        String requestedBy = apiResponse.getUserName(); if (requestedBy == null || requestedBy.trim().isEmpty()) requestedBy = "Người dùng ẩn danh";

        String timeRange = "N/A"; LocalDateTime startTime = apiResponse.getPlannedStartTime(); LocalDateTime endTime = apiResponse.getPlannedEndTime();
        if (startTime != null && endTime != null) { timeRange = startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER); }
        else if (startTime != null) { timeRange = startTime.format(TIME_FORMATTER) + " - ?"; } else if (endTime != null) { timeRange = "? - " + endTime.format(TIME_FORMATTER); }

        ApprovalRequest mappedRequest = new ApprovalRequest( apiResponse.getId(), facilityName, apiResponse.getPurpose(), startTime, timeRange, requestedBy, apiResponse.getCreatedAt() );
        System.out.println("  Mapped to ApprovalRequest: " + mappedRequest);
        return mappedRequest;
    }
    // ... (Các phương thức handleAccept, handleReject, disableCardButtons, showInfo, showError giữ nguyên) ...
    private void createAndAddRequestCardUI(BookingResponse booking) {
        System.out.println("[UI Thread] Creating card for Booking ID: " + booking.getId()); // DEBUG
        try {
            // Đảm bảo đường dẫn đúng
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardApprovalRequest.fxml"));
            Node cardNode = loader.load();
            ApprovalRequestCardController cardController = loader.getController();

            // DEBUG: Kiểm tra controller card có lấy được không
            if (cardController == null) {
                System.err.println("!!! CRITICAL: Failed to get ApprovalRequestCardController for booking ID: " + booking.getId());
                return; // Không thể tiếp tục nếu controller null
            }

            ApprovalRequest uiRequest = mapToApprovalRequest(booking); // Gọi hàm map

            // *** THAY ĐỔI: Thêm card vào container TRƯỚC khi gọi setData ***
            requestContainer.getChildren().add(cardNode);
            System.out.println("[UI Thread] Added card node to container for Booking ID: " + booking.getId());

            // Gọi setData SAU KHI đã thêm vào scene graph
            cardController.setData(uiRequest);

            // Gán sự kiện (giữ nguyên)
            cardController.setOnAcceptAction(event -> handleAccept(uiRequest.getBookingId(), cardNode));
            cardController.setOnRejectAction(event -> handleReject(uiRequest.getBookingId(), cardNode));

            System.out.println("[UI Thread] Successfully processed card for Booking ID: " + booking.getId()); // DEBUG
        } catch (Exception e) { // Bắt lỗi rộng hơn
            System.err.println("[UI Thread] Error creating/loading/setting card for booking " + booking.getId() + ": " + e.getMessage());
            e.printStackTrace();
            showError("UI Error", "Could not display card for booking " + booking.getId());
        }
    }

    private void handleAccept(String bookingId, Node cardNode) {
        System.out.println("[Action] handleAccept triggered for Booking ID: " + bookingId); // DEBUG
        disableCardButtons(cardNode, true);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Accept"); confirmation.setHeaderText("Approve Booking ID: " + bookingId); confirmation.setContentText("Are you sure you want to approve this request?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("[Action] User confirmed ACCEPT for Booking ID: " + bookingId + ". Starting task..."); // DEBUG
                Task<Void> approveTask = new Task<>() {
                    @Override protected Void call() throws Exception { System.out.println("  [Task] Calling bookingService.approveBooking(" + bookingId + ")"); bookingService.approveBooking(bookingId); System.out.println("  [Task] bookingService.approveBooking(" + bookingId + ") completed."); return null; } // DEBUG
                    @Override protected void succeeded() { System.out.println("  [Task] Approve Task Succeeded for Booking ID: " + bookingId); Platform.runLater(() -> { requestContainer.getChildren().remove(cardNode); if (requestContainer.getChildren().isEmpty()) requestContainer.getChildren().add(new Label("No pending approvals remaining.")); showInfo("Success", "Booking " + bookingId + " approved."); }); } // DEBUG
                    @Override protected void failed() { Throwable exc = getException(); System.err.println("  [Task] Approve Task Failed for Booking ID: " + bookingId); if(exc != null) exc.printStackTrace(); Platform.runLater(() -> { showError("Approval Failed", "Could not approve booking " + bookingId + ".\nError: " + (exc != null ? exc.getMessage() : "Unknown error")); disableCardButtons(cardNode, false); }); } // DEBUG
                }; new Thread(approveTask).start();
            } else {
                System.out.println("[Action] User cancelled ACCEPT for Booking ID: " + bookingId); // DEBUG
                disableCardButtons(cardNode, false);
            }
        });
    }

    // handleReject (Thêm log chi tiết hơn)
    private void handleReject(String bookingId, Node cardNode) {
        System.out.println("[Action] handleReject triggered for Booking ID: " + bookingId); // DEBUG
        disableCardButtons(cardNode, true);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Reject"); confirmation.setHeaderText("Reject Booking ID: " + bookingId); confirmation.setContentText("Are you sure you want to reject this request?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("[Action] User confirmed REJECT for Booking ID: " + bookingId + ". Starting task..."); // DEBUG
                Task<Void> rejectTask = new Task<>() {
                    @Override protected Void call() throws Exception { System.out.println("  [Task] Calling bookingService.rejectBooking(" + bookingId + ")"); bookingService.rejectBooking(bookingId); System.out.println("  [Task] bookingService.rejectBooking(" + bookingId + ") completed."); return null; } // DEBUG
                    @Override protected void succeeded() { System.out.println("  [Task] Reject Task Succeeded for Booking ID: " + bookingId); Platform.runLater(() -> { requestContainer.getChildren().remove(cardNode); if (requestContainer.getChildren().isEmpty()) requestContainer.getChildren().add(new Label("No pending approvals remaining.")); showInfo("Success", "Booking " + bookingId + " rejected."); }); } // DEBUG
                    @Override protected void failed() { Throwable exc = getException(); System.err.println("  [Task] Reject Task Failed for Booking ID: " + bookingId); if(exc != null) exc.printStackTrace(); Platform.runLater(() -> { showError("Rejection Failed", "Could not reject booking " + bookingId + ".\nError: " + (exc != null ? exc.getMessage() : "Unknown error")); disableCardButtons(cardNode, false); }); } // DEBUG
                }; new Thread(rejectTask).start();
            } else {
                System.out.println("[Action] User cancelled REJECT for Booking ID: " + bookingId); // DEBUG
                disableCardButtons(cardNode, false);
            }
        });
    }
    private void disableCardButtons(Node cardNode, boolean disable) {
        Object userData = cardNode.getUserData();
        // Try finding controller via UserData first (best practice)
        if (userData instanceof ApprovalRequestCardController) {
            ApprovalRequestCardController controller = (ApprovalRequestCardController) userData;
            if(controller.getAcceptButton()!=null) controller.getAcceptButton().setDisable(disable);
            if(controller.getRejectButton()!=null) controller.getRejectButton().setDisable(disable);
            System.out.println("Disabled buttons via UserData controller for card.");
        }
        // Fallback to lookup if UserData is not set correctly
        else if (cardNode instanceof Parent) {
            System.out.println("Attempting button disable via lookup...");
            Node acceptBtn = cardNode.lookup("#acceptButton"); // Use correct fx:id from card FXML
            Node rejectBtn = cardNode.lookup("#rejectButton"); // Use correct fx:id from card FXML
            if (acceptBtn instanceof Button) { acceptBtn.setDisable(disable); System.out.println("  -> Accept button found via lookup & disabled."); } else { System.err.println("  -> Accept button not found or not a Button via lookup.");}
            if (rejectBtn instanceof Button) { rejectBtn.setDisable(disable); System.out.println("  -> Reject button found via lookup & disabled."); } else { System.err.println("  -> Reject button not found or not a Button via lookup.");}
        } else {
            System.err.println("Warning: Cannot disable buttons. Card node is not Parent or missing Controller in UserData.");
        }
    }

    private void showInfo(String title, String message) { Alert alert = new Alert(Alert.AlertType.INFORMATION); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait(); }
    private void showError(String title, String message) { Alert alert = new Alert(Alert.AlertType.ERROR); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait(); }

}