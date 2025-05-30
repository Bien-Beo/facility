package com.utc2.facilityui.controller;

import com.utc2.facilityui.response.BookingResponse; // Model BookingResponse từ client
import com.utc2.facilityui.response.Page;          // Model Page từ client
import com.utc2.facilityui.service.BookingService;  // Service để gọi API

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window; // Import Window

import java.io.IOException;
import java.net.URL;
import java.util.Collections; // Thêm import
import java.util.List;      // Thêm import
import java.util.Optional;  // Thêm import
import java.util.ResourceBundle;

public class OverdueRequestsController implements Initializable {

    @FXML private ScrollPane scrollPane; // Có thể không cần inject nếu không thao tác trực tiếp
    @FXML private VBox requestContainer;
    @FXML private Label titleLabel;
    @FXML private ProgressIndicator loadingIndicatorOverdue;

    // FXML cho phân trang (đảm bảo fx:id khớp với OverdueRequests.fxml)
    @FXML private Button btnPreviousOverdue;
    @FXML private Button btnNextOverdue;
    @FXML private Label overduePageInfoLabel;
    // @FXML private ComboBox<Integer> overdueRowsPerPageComboBox; // Nếu bạn có

    private final BookingService bookingService = new BookingService();

    // Biến trạng thái phân trang
    private int currentPage = 0;
    private int pageSize = 5; // Hoặc giá trị mặc định bạn muốn cho màn hình này
    private int totalPages = 0;
    private long totalElements = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (titleLabel != null) titleLabel.setText("Các Đặt Phòng Quá Hạn");
        if (scrollPane != null) scrollPane.setFitToWidth(true);
        if (loadingIndicatorOverdue != null) loadingIndicatorOverdue.setVisible(false);

        // Gán sự kiện cho nút phân trang
        if (btnPreviousOverdue != null) {
            btnPreviousOverdue.setOnAction(event -> handlePreviousPage());
        }
        if (btnNextOverdue != null) {
            btnNextOverdue.setOnAction(event -> handleNextPage());
        }
        // Khởi tạo ComboBox pageSize nếu có
        // if (overdueRowsPerPageComboBox != null) {
        //     overdueRowsPerPageComboBox.setItems(FXCollections.observableArrayList(5, 10, 15));
        //     overdueRowsPerPageComboBox.setValue(pageSize);
        //     overdueRowsPerPageComboBox.setOnAction(e -> {
        //         pageSize = overdueRowsPerPageComboBox.getValue();
        //         currentPage = 0;
        //         loadOverdueBookingsData();
        //     });
        // }

        System.out.println("OverdueRequestsController Initialized. Loading overdue bookings...");
        loadOverdueBookingsData();
    }

    private void loadOverdueBookingsData() {
        if (loadingIndicatorOverdue != null) loadingIndicatorOverdue.setVisible(true);
        if (requestContainer != null) requestContainer.getChildren().clear();

        Task<Page<BookingResponse>> loadTask = new Task<>() {
            @Override
            protected Page<BookingResponse> call() throws Exception {
                // Gọi service để lấy các booking quá hạn từ server
                // Đảm bảo BookingService.getOverdueBookings trả về Page<BookingResponse>
                System.out.println("[Task] Calling bookingService.getOverdueBookings for page: " + currentPage + ", size: " + pageSize);
                return bookingService.getOverdueBookings(currentPage, pageSize);
            }

            @Override
            protected void succeeded() {
                Page<BookingResponse> resultPage = getValue();
                Platform.runLater(() -> {
                    if (requestContainer != null) requestContainer.getChildren().clear();
                    if (resultPage != null && resultPage.getContent() != null && !resultPage.getContent().isEmpty()) {
                        List<BookingResponse> overdueBookings = resultPage.getContent();
                        System.out.println("[UI Thread] Fetched " + overdueBookings.size() + " overdue bookings for this page.");
                        overdueBookings.forEach(OverdueRequestsController.this::createAndAddOverdueCardUI);

                        totalPages = resultPage.getTotalPages();
                        totalElements = resultPage.getTotalElements();
                        currentPage = resultPage.getNumber(); // Server trả về 0-indexed
                    } else {
                        System.out.println("[UI Thread] No overdue bookings found or list was null/empty.");
                        if (requestContainer != null) requestContainer.getChildren().add(new Label("Không có đặt phòng nào quá hạn."));
                        totalPages = 0;
                        totalElements = 0;
                    }
                    updatePaginationUI();
                    if (loadingIndicatorOverdue != null) loadingIndicatorOverdue.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Throwable exc = getException();
                exc.printStackTrace();
                Platform.runLater(() -> {
                    if (requestContainer != null) requestContainer.getChildren().clear();
                    showError("Tải Thất Bại", "Không thể tải danh sách đặt phòng quá hạn: " + exc.getMessage());
                    if (requestContainer != null) requestContainer.getChildren().add(new Label("Lỗi khi tải dữ liệu."));
                    if (loadingIndicatorOverdue != null) loadingIndicatorOverdue.setVisible(false);
                    totalPages = 0;
                    totalElements = 0;
                    updatePaginationUI();
                });
            }
        };
        new Thread(loadTask).start();
    }

    private void createAndAddOverdueCardUI(BookingResponse booking) {
        try {
            // Đường dẫn đến FXML của card quá hạn
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardOverdueRequest.fxml"));
            Node cardNode = loader.load(); // FXML gốc của card nên là HBox hoặc VBox
            OverdueRequestCardController cardController = loader.getController();

            if (cardController == null) {
                System.err.println("CRITICAL: Không thể lấy OverdueRequestCardController cho booking ID: " + booking.getId());
                return;
            }

            cardNode.setUserData(cardController); // Lưu controller vào cardNode

            // Truyền trực tiếp BookingResponse, OverdueRequestCardController sẽ xử lý việc hiển thị
            cardController.setData(booking);

            // Gán hành động cho các nút trên card
            // Nút "NHẮC NHỞ" (fx:id="btnRemind" trong card FXML và CardController)
            Button remindButton = cardController.getRemindButton();
            if (remindButton != null) {
                remindButton.setOnAction(event -> handleSendReminder(booking, cardNode));
            }

            // Nút "THU HỒI" (fx:id="btnRevoke" trong card FXML và CardController)
            Button revokeButton = cardController.getRevokeButton();
            if (revokeButton != null) {
                revokeButton.setOnAction(event -> {
                    String reason = cardController.getActionReason(); // Lấy lý do từ card
                    handleRevokeBooking(booking, reason, cardNode);
                });
            }

            if (requestContainer != null) {
                requestContainer.getChildren().add(cardNode);
            } else {
                System.err.println("Lỗi: requestContainer là null, không thể thêm card.");
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi tạo card quá hạn cho booking " + booking.getId() + ": " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi Giao Diện", "Không thể hiển thị card cho đặt phòng quá hạn ID: " + booking.getId());
        }
    }

    private void handleSendReminder(BookingResponse booking, Node cardNode) {
        System.out.println("Hành động: Gửi nhắc nhở cho booking ID: " + booking.getId());
        OverdueRequestCardController cardCtrl = getCardController(cardNode);
        if (cardCtrl != null && cardCtrl.getRemindButton() != null) {
            cardCtrl.getRemindButton().setDisable(true); // Vô hiệu hóa nút tạm thời
        }

        Task<Void> remindTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                bookingService.sendOverdueReminder(booking.getId());
                return null;
            }
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showInfo("Thành công", "Đã gửi nhắc nhở cho đặt phòng ID: " + booking.getId());
                    if (cardCtrl != null && cardCtrl.getRemindButton() != null) cardCtrl.getRemindButton().setDisable(false);
                });
            }
            @Override
            protected void failed() {
                Throwable exc = getException();
                exc.printStackTrace();
                Platform.runLater(() -> {
                    showError("Gửi Thất Bại", "Gửi nhắc nhở thất bại cho ID: " + booking.getId() + "\nLỗi: " + exc.getMessage());
                    if (cardCtrl != null && cardCtrl.getRemindButton() != null) cardCtrl.getRemindButton().setDisable(false);
                });
            }
        };
        new Thread(remindTask).start();
    }

    private void handleRevokeBooking(BookingResponse booking, String reason, Node cardNode) {
        OverdueRequestCardController cardCtrl = getCardController(cardNode);
        if (cardCtrl != null && cardCtrl.getRevokeButton() != null) {
            cardCtrl.getRevokeButton().setDisable(true); // Vô hiệu hóa nút ngay
        }

        System.out.println("Hành động: Thu hồi booking ID: " + booking.getId() + " với lý do: " + reason);

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận Thu Hồi");
        confirmation.setHeaderText("Thu hồi Đặt Phòng ID: " + booking.getId());
        confirmation.setContentText("Bạn có chắc chắn muốn thu hồi đặt phòng này?" +
                (reason.isEmpty() ? "" : "\nLý do: " + reason));
        getAlertOwner().ifPresent(confirmation::initOwner);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Task<Void> revokeTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    bookingService.revokeBooking(booking.getId(), reason);
                    return null;
                }
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        showInfo("Thành công", "Đã thu hồi đặt phòng ID: " + booking.getId());
                        if (requestContainer != null) requestContainer.getChildren().remove(cardNode);

                        // Cập nhật lại số lượng và phân trang sau khi xóa card
                        totalElements--;
                        if (requestContainer != null && requestContainer.getChildren().isEmpty()) {
                            if (currentPage > 0) { // Nếu không phải trang đầu và trang hiện tại rỗng
                                currentPage--;
                                loadOverdueBookingsData(); // Tải lại trang trước
                            } else { // Nếu là trang đầu và rỗng, hoặc không còn trang nào
                                requestContainer.getChildren().add(new Label("Không có đặt phòng nào quá hạn."));
                                updatePaginationUI();
                            }
                        } else {
                            updatePaginationUI();
                        }
                    });
                }
                @Override
                protected void failed() {
                    Throwable exc = getException();
                    exc.printStackTrace();
                    Platform.runLater(() -> {
                        showError("Thu Hồi Thất Bại", "Thu hồi đặt phòng thất bại cho ID: " + booking.getId() + "\nLỗi: " + exc.getMessage());
                        if (cardCtrl != null && cardCtrl.getRevokeButton() != null) cardCtrl.getRevokeButton().setDisable(false);
                    });
                }
            };
            new Thread(revokeTask).start();
        } else {
            // Người dùng nhấn Cancel trên dialog xác nhận
            if (cardCtrl != null && cardCtrl.getRevokeButton() != null) cardCtrl.getRevokeButton().setDisable(false);
        }
    }

    private OverdueRequestCardController getCardController(Node cardNode) {
        if (cardNode != null && cardNode.getUserData() instanceof OverdueRequestCardController) {
            return (OverdueRequestCardController) cardNode.getUserData();
        }
        System.err.println("Lỗi: Không thể lấy OverdueRequestCardController từ UserData của cardNode.");
        return null;
    }

    private void updatePaginationUI() { // Đổi tên cho nhất quán
        if (overduePageInfoLabel != null) {
            if (totalElements == 0) {
                overduePageInfoLabel.setText("Không có dữ liệu");
            } else if (totalPages > 0) {
                overduePageInfoLabel.setText("Trang " + (currentPage + 1) + " / " + totalPages);
            } else {
                overduePageInfoLabel.setText("Trang " + (currentPage + 1));
            }
        }
        if (btnPreviousOverdue != null) {
            btnPreviousOverdue.setDisable(currentPage == 0 || totalElements == 0);
        }
        if (btnNextOverdue != null) {
            btnNextOverdue.setDisable(currentPage >= totalPages - 1 || totalElements == 0);
        }
    }

    @FXML
    private void handlePreviousPage() { // Đổi tên cho nhất quán
        if (currentPage > 0) {
            currentPage--;
            loadOverdueBookingsData();
        }
    }

    @FXML
    private void handleNextPage() { // Đổi tên
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadOverdueBookingsData();
        }
    }

    // --- Alert Helpers ---
    private Optional<Window> getAlertOwner() {
        if (requestContainer != null && requestContainer.getScene() != null && requestContainer.getScene().getWindow() != null) {
            return Optional.of(requestContainer.getScene().getWindow());
        }
        return Optional.empty();
    }
    private void showInfo(String title, String message) { showAlert(Alert.AlertType.INFORMATION, title, message); }
    private void showError(String title, String message) { showAlert(Alert.AlertType.ERROR, title, message); }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(()->{
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            TextArea area = new TextArea(message); // Dùng TextArea cho thông báo dài
            area.setWrapText(true);
            area.setEditable(false);
            area.setPrefHeight(100); // Giới hạn chiều cao ban đầu của TextArea
            alert.getDialogPane().setContent(area);
            alert.setResizable(true);
            getAlertOwner().ifPresent(alert::initOwner);
            alert.showAndWait();
        });
    }
}