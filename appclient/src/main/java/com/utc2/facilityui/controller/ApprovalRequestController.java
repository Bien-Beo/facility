package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.service.BookingService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea; // Dùng cho Alert
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
// import java.util.stream.Collectors; // Không cần nữa

public class ApprovalRequestController implements Initializable {

    @FXML private VBox requestContainer;
    @FXML private Label mainTitleLabel;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML private Button btnPreviousPending;
    @FXML private Button btnNextPending;
    @FXML private Label pendingPageInfoLabel;
    // @FXML private ComboBox<Integer> pendingRowsPerPageComboBox; // Nếu bạn muốn thêm

    private final BookingService bookingService = new BookingService();

    // Định dạng ngày giờ dùng chung
    private static final DateTimeFormatter VNF_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy", new Locale("vi", "VN"));
    private static final DateTimeFormatter VNF_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", new Locale("vi", "VN"));
    private static final DateTimeFormatter VNF_DATE_TIME_SHORT_FOR_RANGE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM", new Locale("vi", "VN"));
    private static final DateTimeFormatter VNF_FULL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm, EEE dd/MM/yyyy", new Locale("vi", "VN"));
    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;
    private long totalElements = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (mainTitleLabel != null) mainTitleLabel.setText("Yêu cầu Chờ Duyệt");
        if (loadingIndicator != null) loadingIndicator.setVisible(false);

        if (btnPreviousPending != null) {
            btnPreviousPending.setOnAction(event -> handlePreviousApprovalPage());
        }
        if (btnNextPending != null) {
            btnNextPending.setOnAction(event -> handleNextApprovalPage());
        }
        // Khởi tạo ComboBox pageSize nếu có
        // if (pendingRowsPerPageComboBox != null) {
        //     pendingRowsPerPageComboBox.setItems(FXCollections.observableArrayList(5, 10, 15, 20));
        //     pendingRowsPerPageComboBox.setValue(pageSize);
        //     pendingRowsPerPageComboBox.setOnAction(e -> {
        //         pageSize = pendingRowsPerPageComboBox.getValue();
        //         currentPage = 0;
        //         loadPendingRequestsFromServer();
        //     });
        // }
        System.out.println("ApprovalRequestController Initialized. Loading requests...");
        loadPendingRequestsFromServer();
    }

    private void loadPendingRequestsFromServer() {
        if (loadingIndicator != null) loadingIndicator.setVisible(true);
        requestContainer.getChildren().clear();

        Task<Page<BookingResponse>> loadTask = new Task<>() {
            @Override
            protected Page<BookingResponse> call() throws Exception {
                return bookingService.getPendingApprovalBookings(currentPage, pageSize);
            }

            @Override
            protected void succeeded() {
                Page<BookingResponse> resultPage = getValue();
                Platform.runLater(() -> {
                    requestContainer.getChildren().clear();
                    if (resultPage != null && resultPage.getContent() != null && !resultPage.getContent().isEmpty()) {
                        List<BookingResponse> pendingBookings = resultPage.getContent();
                        pendingBookings.forEach(ApprovalRequestController.this::createAndAddRequestCardUI);

                        totalPages = resultPage.getTotalPages();
                        totalElements = resultPage.getTotalElements();
                        currentPage = resultPage.getNumber();
                    } else {
                        requestContainer.getChildren().add(new Label("Hiện không có yêu cầu nào chờ duyệt."));
                        totalPages = 0;
                        totalElements = 0;
                        // currentPage có thể giữ nguyên hoặc reset về 0
                    }
                    updatePendingPaginationControls();
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Throwable exc = getException();
                exc.printStackTrace();
                Platform.runLater(() -> {
                    requestContainer.getChildren().clear();
                    showError("Tải Thất Bại", "Không thể tải danh sách yêu cầu: " + exc.getMessage());
                    requestContainer.getChildren().add(new Label("Lỗi khi tải yêu cầu."));
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                    totalPages = 0;
                    totalElements = 0;
                    updatePendingPaginationControls();
                });
            }
        };
        new Thread(loadTask).start();
    }

    private void updatePendingPaginationControls() {
        if (pendingPageInfoLabel != null) {
            if (totalElements == 0) {
                pendingPageInfoLabel.setText("Không có dữ liệu");
            } else if (totalPages > 0) {
                pendingPageInfoLabel.setText("Trang " + (currentPage + 1) + " / " + totalPages + " (Tổng: " + totalElements + ")");
            } else { // totalPages = 0 nhưng totalElements > 0 (chỉ 1 trang)
                pendingPageInfoLabel.setText("Trang " + (currentPage + 1));
            }
        }
        if (btnPreviousPending != null) {
            btnPreviousPending.setDisable(currentPage == 0 || totalElements == 0);
        }
        if (btnNextPending != null) {
            btnNextPending.setDisable(currentPage >= totalPages - 1 || totalElements == 0);
        }
    }

    @FXML
    private void handlePreviousApprovalPage() {
        if (currentPage > 0) {
            currentPage--;
            loadPendingRequestsFromServer();
        }
    }
    @FXML
    private void handleNextApprovalPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadPendingRequestsFromServer();
        }
    }

    private ApprovalRequest mapToApprovalRequest(BookingResponse apiResponse) {
        String bookingId = apiResponse.getId();
        String facilityName = Objects.requireNonNullElse(apiResponse.getRoomName(), "N/A");
        String requestedBy = Objects.requireNonNullElse(apiResponse.getUserName(), "N/A");
        String purpose = Objects.requireNonNullElse(apiResponse.getPurpose(), "N/A");

        LocalDateTime startTime = apiResponse.getPlannedStartTime();
        LocalDateTime endTime = apiResponse.getPlannedEndTime();
        LocalDateTime requestedAtTime = apiResponse.getCreatedAt();

        String finalFormattedDate = "N/A";
        String finalTimeRange = "N/A";

        if (startTime != null) {
            finalFormattedDate = startTime.format(VNF_DATE_FORMATTER);
            if (endTime != null) {
                if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
                    finalTimeRange = startTime.format(VNF_TIME_FORMATTER) + " - " + endTime.format(VNF_TIME_FORMATTER);
                } else {
                    finalTimeRange = startTime.format(VNF_DATE_TIME_SHORT_FOR_RANGE_FORMATTER) + " " + startTime.getYear() +
                            " đến " +
                            endTime.format(VNF_DATE_TIME_SHORT_FOR_RANGE_FORMATTER) + " " + endTime.getYear();
                    finalFormattedDate = ""; // Ngày đã bao gồm trong timeRange
                }
            } else {
                finalTimeRange = startTime.format(VNF_TIME_FORMATTER) + " - (Chưa rõ)";
            }
        } else if (endTime != null) {
            finalTimeRange = "(Chưa rõ) - " + endTime.format(VNF_TIME_FORMATTER);
            finalFormattedDate = endTime.format(VNF_DATE_FORMATTER);
        }

        String formattedRequestedAt = (requestedAtTime != null) ? requestedAtTime.format(VNF_FULL_DATE_TIME_FORMATTER) : "N/A";
        String statusDisplay = translateBookingStatus(apiResponse.getStatus());
        String statusKey = apiResponse.getStatus();

        return new ApprovalRequest(
                bookingId, facilityName, purpose,
                finalFormattedDate, finalTimeRange, requestedBy,
                formattedRequestedAt, statusDisplay, statusKey,
                startTime, requestedAtTime
        );
    }

    private String translateBookingStatus(String statusKey) {
        if (statusKey == null) return "N/A";
        return switch (statusKey.toUpperCase()) {
            case "PENDING_APPROVAL" -> "Chờ duyệt";
            case "CONFIRMED" -> "Đã duyệt";
            // ... (các trạng thái khác)
            default -> statusKey;
        };
    }

    private void createAndAddRequestCardUI(BookingResponse booking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardApprovalRequest.fxml"));
            Node cardNode = loader.load();
            ApprovalRequestCardController cardController = loader.getController();

            if (cardController == null) {
                System.err.println("CRITICAL: Không thể lấy ApprovalRequestCardController cho booking ID: " + booking.getId());
                return;
            }

            cardNode.setUserData(cardController); // Quan trọng: Lưu controller vào cardNode

            ApprovalRequest uiRequest = mapToApprovalRequest(booking);
            cardController.setData(uiRequest);

            cardController.setOnAcceptAction(event -> handleAccept(booking.getId(), cardNode));
            cardController.setOnRejectAction(event -> {
                String reason = cardController.getRejectReason();
                handleReject(booking.getId(), reason, cardNode);
            });

            requestContainer.getChildren().add(cardNode);
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo card cho booking " + booking.getId() + ": " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi Giao Diện", "Không thể hiển thị card cho yêu cầu " + booking.getId());
        }
    }

    private void handleAccept(String bookingId, Node cardNode) {
        disableCardButtons(cardNode, true);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận Duyệt");
        confirmation.setHeaderText("Duyệt Yêu Cầu ID: " + bookingId);
        confirmation.setContentText("Bạn có chắc chắn muốn duyệt yêu cầu này không?");
        getAlertOwner().ifPresent(confirmation::initOwner);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Boolean> approveTask = createTaskForBookingAction(
                        () -> bookingService.approveBooking(bookingId),
                        "Đã duyệt thành công yêu cầu ID: " + bookingId,
                        "Duyệt Thất Bại",
                        cardNode, true
                );
                new Thread(approveTask).start();
            } else {
                disableCardButtons(cardNode, false);
            }
        });
    }

    private void handleReject(String bookingId, String reason, Node cardNode) {
        disableCardButtons(cardNode, true);

        System.out.println("Sẽ từ chối booking ID: " + bookingId + (reason != null && !reason.isEmpty() ? " với lý do: '" + reason + "'" : " (không có lý do)."));

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận Từ Chối");
        confirmation.setHeaderText("Từ Chối Yêu Cầu ID: " + bookingId);
        confirmation.setContentText("Bạn có chắc chắn muốn từ chối yêu cầu này?");
        getAlertOwner().ifPresent(confirmation::initOwner);

        confirmation.showAndWait().ifPresent(confirmResponse -> {
            if (confirmResponse == ButtonType.OK) {
                Task<Boolean> rejectTask = createTaskForBookingAction(
                        // Nếu server của bạn đã cập nhật để nhận reason cho /reject:
                        // () -> bookingService.rejectBookingWithReason(bookingId, reason),
                        // Nếu server chưa cập nhật (chỉ nhận bookingId cho /reject):
                        () -> bookingService.rejectBooking(bookingId,reason),
                        "Đã từ chối thành công yêu cầu ID: " + bookingId,
                        "Từ Chối Thất Bại",
                        cardNode, true
                );
                new Thread(rejectTask).start();
            } else {
                disableCardButtons(cardNode, false);
            }
        });
    }

    private interface BookingAction { void execute() throws IOException; }

    private Task<Boolean> createTaskForBookingAction(BookingAction action, String successMessage, String failureTitle, Node cardNode, boolean removeCardOnSuccess) {
        return new Task<>() {
            @Override protected Boolean call() throws Exception { action.execute(); return true; }
            @Override protected void succeeded() {
                Platform.runLater(() -> {
                    if (removeCardOnSuccess) {
                        requestContainer.getChildren().remove(cardNode);
                        if (requestContainer.getChildren().isEmpty()) {
                            requestContainer.getChildren().add(new Label("Không còn yêu cầu nào chờ duyệt."));
                        }
                    }
                    showInfo("Thành công", successMessage);
                    // Sau khi duyệt/từ chối, có thể tải lại để cập nhật số lượng/phân trang
                    // loadPendingRequestsFromServer(); // Cân nhắc việc này
                });
            }
            @Override protected void failed() {
                Throwable exc = getException(); exc.printStackTrace();
                Platform.runLater(() -> {
                    showError(failureTitle, "Lỗi: " + exc.getMessage());
                    disableCardButtons(cardNode, false);
                });
            }
        };
    }

    private void disableCardButtons(Node cardNode, boolean disable) {
        if (cardNode == null) return;
        Object controller = cardNode.getUserData();
        if (controller instanceof ApprovalRequestCardController) {
            ApprovalRequestCardController cardCtrl = (ApprovalRequestCardController) controller;
            if(cardCtrl.getAcceptButton()!=null) cardCtrl.getAcceptButton().setDisable(disable);
            if(cardCtrl.getRejectButton()!=null) cardCtrl.getRejectButton().setDisable(disable);
        } else {
            System.err.println("Không thể disable nút: Controller không tìm thấy trong UserData của card. Thử lookup...");
            // Fallback nếu UserData không được set đúng cách
            Node acceptBtnNode = cardNode.lookup("#btnApprove");
            Node rejectBtnNode = cardNode.lookup("#btnReject");
            if (acceptBtnNode instanceof Button) ((Button) acceptBtnNode).setDisable(disable); else System.err.println("btnApprove not found by lookup");
            if (rejectBtnNode instanceof Button) ((Button) rejectBtnNode).setDisable(disable); else System.err.println("btnReject not found by lookup");
        }
    }

    private Optional<Window> getAlertOwner() {
        if (requestContainer != null && requestContainer.getScene() != null && requestContainer.getScene().getWindow() != null) {
            return Optional.of(requestContainer.getScene().getWindow());
        }
        return Optional.empty();
    }

    private void showInfo(String title, String message) { showAlert(Alert.AlertType.INFORMATION, title, message); }
    private void showError(String title, String message) { showAlert(Alert.AlertType.ERROR, title, message); }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        getAlertOwner().ifPresent(alert::initOwner);
        alert.showAndWait();
    }
}