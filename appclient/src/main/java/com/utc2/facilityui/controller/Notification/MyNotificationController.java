package com.utc2.facilityui.controller.Notification;

import com.utc2.facilityui.response.NotificationResponse;
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.service.NotificationApiService;
import com.utc2.facilityui.auth.TokenStorage; // Sử dụng TokenStorage đã cập nhật

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyNotificationController implements Initializable {

    @FXML private VBox cardsContainerVBox;
    @FXML private Button reloadButton;
    @FXML private Button markAllAsReadButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label statusLabel;
    @FXML private HBox paginationControls;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;
    @FXML private Label unreadCountLabel;
    @FXML private Label mainTitleLabel;
    @FXML private HBox statusContainer;
    @FXML private ScrollPane scrollPane;

    private NotificationApiService notificationApiService;

    private String userDatabaseIdForApi; // Biến lưu trữ Database ID (UUID) cho các lời gọi API

    private int currentPage = 0;
    private final int PAGE_SIZE = 8; // Bạn có thể đặt PAGE_SIZE ở đây
    private int totalPages = 0;
    private long totalNotifications = 0;
    private long currentUnreadCount = 0;

    private List<CardNotificationController> activeCardControllers = new ArrayList<>();

    private static final int API_NOTIFICATION_SUCCESS_CODE = 0; // Giả sử mã thành công là 0

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        notificationApiService = new NotificationApiService();

        // Các bindings UI
        statusLabel.managedProperty().bind(statusLabel.visibleProperty());
        loadingIndicator.managedProperty().bind(loadingIndicator.visibleProperty());
        statusContainer.managedProperty().bind(statusContainer.visibleProperty());
        paginationControls.managedProperty().bind(paginationControls.visibleProperty());
        markAllAsReadButton.setDisable(true);

        if (!TokenStorage.isLoggedIn()) {
            System.err.println("LỖI MyNotificationController: Người dùng chưa đăng nhập (theo TokenStorage).");
            showErrorState("Không thể tải thông báo: Yêu cầu đăng nhập.");
            disableAllInteractions();
            return;
        }

        // Lấy Database ID (UUID) từ TokenStorage
        this.userDatabaseIdForApi = TokenStorage.getCurrentUserDatabaseId();
        String currentUsernameForDisplay = TokenStorage.getCurrentUsername();

        if (this.userDatabaseIdForApi == null || this.userDatabaseIdForApi.trim().isEmpty()) {
            System.err.println("LỖI MyNotificationController: Không lấy được Database ID (UUID) của người dùng từ TokenStorage.");
            showErrorState("Lỗi cấu hình client: Không tìm thấy ID người dùng hợp lệ để truy vấn.");
            disableAllInteractions();
            return;
        }

        System.out.println("MyNotificationController initialized for user Database ID: " + this.userDatabaseIdForApi +
                (currentUsernameForDisplay != null ? ", Username: " + currentUsernameForDisplay : ""));
        loadNotifications(0); // Tải trang đầu tiên
    }

    private void disableAllInteractions() {
        reloadButton.setDisable(true);
        markAllAsReadButton.setDisable(true);
        if(prevPageButton != null) prevPageButton.setDisable(true); // Thêm kiểm tra null
        if(nextPageButton != null) nextPageButton.setDisable(true); // Thêm kiểm tra null
    }

    @FXML
    private void handleReloadNotifications() {
        if (!TokenStorage.isLoggedIn()) {
            showErrorState("Không thể tải lại: Người dùng chưa đăng nhập hoặc phiên làm việc đã hết hạn.");
            disableAllInteractions();
            return;
        }
        // Lấy lại Database ID (UUID) mới nhất (phòng trường hợp có thay đổi, dù ít xảy ra)
        this.userDatabaseIdForApi = TokenStorage.getCurrentUserDatabaseId();
        if (this.userDatabaseIdForApi == null || this.userDatabaseIdForApi.trim().isEmpty()) {
            System.err.println("LỖI MyNotificationController (Reload): Không lấy được Database ID (UUID) của người dùng.");
            showErrorState("Lỗi cấu hình client: Không thể tải lại thông báo.");
            disableAllInteractions();
            return;
        }
        System.out.println("MyNotificationController: Reloading notifications for user Database ID: " + this.userDatabaseIdForApi);
        loadNotifications(0); // Tải lại từ trang đầu
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            loadNotifications(currentPage - 1);
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages - 1) {
            loadNotifications(currentPage + 1);
        }
    }

    @FXML
    private void handleMarkAllAsRead() {
        // Hiện tại API server chưa hỗ trợ, giữ nguyên thông báo
        showInfoAlert("Chức năng Đang Phát Triển", "Chức năng 'Đánh dấu tất cả đã đọc' cần API hỗ trợ từ server.");
    }

    private void showUiState(boolean isLoading, String messageText) {
        loadingIndicator.setVisible(isLoading);
        // Status container chỉ nên hiển thị khi loading hoặc có message lỗi/trống
        statusContainer.setVisible(isLoading || (messageText != null && !messageText.isEmpty()));
        statusLabel.setVisible(!isLoading && (messageText != null && !messageText.isEmpty()));
        if (statusLabel.isVisible()) {
            statusLabel.setText(messageText);
        }

        reloadButton.setDisable(isLoading);
        // Nút markAllAsReadButton chỉ bật khi không loading VÀ có thông báo chưa đọc
        markAllAsReadButton.setDisable(isLoading || currentUnreadCount == 0);
        cardsContainerVBox.setDisable(isLoading); // Vô hiệu hóa tương tác với card khi loading

        // Ẩn pagination controls khi loading hoặc khi có thông báo trạng thái (lỗi, không có data)
        // Chỉ hiển thị pagination khi có dữ liệu và không loading
        if (isLoading || (messageText != null && !messageText.isEmpty())) {
            paginationControls.setVisible(false);
        } else {
            // Sẽ được cập nhật lại trong updatePaginationControls sau khi có dữ liệu
        }
    }

    private void showErrorState(String message) {
        showUiState(false, message); // false cho isLoading
        cardsContainerVBox.getChildren().clear();
        activeCardControllers.clear();
        updateUnreadCountDisplay(); // Cập nhật lại số lượng chưa đọc (thành 0 nếu lỗi)
        updatePaginationControls(null); // Ẩn pagination controls khi lỗi
    }


    private void loadNotifications(int pageNumber) {
        showUiState(true, "Đang tải thông báo..."); // True cho isLoading, có message loading
        cardsContainerVBox.getChildren().clear();
        activeCardControllers.clear();
        if (scrollPane != null) {
            scrollPane.setVvalue(0.0); // Cuộn lên đầu khi tải trang mới
        }

        // userDatabaseIdForApi đã được kiểm tra ở initialize và handleReloadNotifications
        if (this.userDatabaseIdForApi == null || this.userDatabaseIdForApi.trim().isEmpty()) {
            Platform.runLater(() -> {
                showErrorState("Không thể tải thông báo: ID người dùng không hợp lệ.");
                disableAllInteractions(); // Đảm bảo các nút bị vô hiệu hóa
            });
            return;
        }

        notificationApiService.getAllNotifications(this.userDatabaseIdForApi, pageNumber, PAGE_SIZE)
                .thenAcceptAsync(apiResponse -> Platform.runLater(() -> {
                    if (apiResponse != null && apiResponse.getCode() == API_NOTIFICATION_SUCCESS_CODE && apiResponse.getResult() != null) {
                        Page<NotificationResponse> page = apiResponse.getResult();
                        this.currentPage = page.getNumber();
                        this.totalPages = page.getTotalPages();
                        this.totalNotifications = page.getTotalElements();

                        if (page.getContent() != null) {
                            // Tính toán số lượng chưa đọc CHỈ cho trang hiện tại nếu muốn,
                            // hoặc bạn cần một API riêng để lấy tổng số chưa đọc.
                            // Để đơn giản, nếu là trang đầu tiên, tính lại tổng số chưa đọc từ trang này.
                            // Lý tưởng nhất là API trả về tổng số chưa đọc.
                            if (pageNumber == 0) { // Hoặc bạn có một API riêng để lấy tổng số unread
                                this.currentUnreadCount = page.getContent().stream().filter(NotificationResponse::isUnread).count();
                            }
                        } else {
                            if (pageNumber == 0) this.currentUnreadCount = 0;
                        }
                        updateUnreadCountDisplay();

                        if (page.getContent() == null || page.getContent().isEmpty()) {
                            showUiState(false, "Không có thông báo nào.");
                        } else {
                            showUiState(false, null); // Xóa thông báo loading/lỗi
                            populateNotificationCards(page.getContent());
                        }
                        updatePaginationControls(page); // Cập nhật và hiển thị pagination controls nếu cần
                    } else {
                        String errorMessage = (apiResponse != null && apiResponse.getMessage() != null) ? apiResponse.getMessage() : "Không thể tải thông báo. Vui lòng thử lại.";
                        showErrorState(errorMessage); // Sử dụng showErrorState
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showErrorState("Lỗi kết nối đến server: " + ex.getMessage());
                        ex.printStackTrace();
                    });
                    return null;
                });
    }

    private void updateUnreadCountDisplay() {
        this.currentUnreadCount = Math.max(0, this.currentUnreadCount); // Đảm bảo không âm
        if (this.currentUnreadCount > 0) {
            unreadCountLabel.setText("(" + this.currentUnreadCount + " chưa đọc)");
        } else {
            unreadCountLabel.setText(""); // Không hiển thị gì nếu không có thông báo chưa đọc
        }
        // Nút "Đánh dấu tất cả đã đọc" chỉ bật khi có thông báo chưa đọc và không đang loading
        markAllAsReadButton.setDisable(this.currentUnreadCount == 0 || loadingIndicator.isVisible());
    }

    public void decrementUnreadCountAndUpdateDisplay() {
        if (this.currentUnreadCount > 0) {
            this.currentUnreadCount--;
        }
        updateUnreadCountDisplay();
    }

    public void incrementUnreadCountAndUpdateDisplay() { // Nếu cần khi có thông báo mới (qua WebSocket chẳng hạn)
        this.currentUnreadCount++;
        updateUnreadCountDisplay();
    }


    private void populateNotificationCards(List<NotificationResponse> notifications) {
        if (notifications == null) return;
        cardsContainerVBox.getChildren().clear(); // Xóa card cũ trước khi thêm mới
        activeCardControllers.clear();

        for (NotificationResponse notification : notifications) {
            try {
                System.out.println("MyNotificationController - Populating card for Notification ID: " + notification.getId() +
                        ", Raw Name from Response: '" + notification.getName() + // Đây là user.getFullName() từ server
                        "', Raw Message from Response: '" + notification.getMessage() + // Đây là notification.getMessage() từ server
                        "', Type: '" + notification.getType() +
                        "', Status: '" + notification.getStatus() +
                        "', CreatedAt: '" + notification.getCreatedAt() + "'");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardNotification.fxml"));
                Node cardNode = loader.load();
                CardNotificationController cardController = loader.getController();
                cardController.setData(notification, this); // Truyền MyNotificationController hiện tại
                activeCardControllers.add(cardController);
                cardsContainerVBox.getChildren().add(cardNode);
            } catch (IOException e) {
                e.printStackTrace();
                showErrorState("Lỗi khi tải giao diện một thẻ thông báo."); // Sử dụng showErrorState
                break;
            }
        }
    }

    private void updatePaginationControls(Page<NotificationResponse> page) {
        if (page == null || page.getTotalPages() <= 0) {
            pageInfoLabel.setText("");
            paginationControls.setVisible(false);
            // paginationControls.setManaged(false); // XÓA DÒNG NÀY
        } else {
            pageInfoLabel.setText("Trang " + (page.getNumber() + 1) + " / " + page.getTotalPages());
            boolean hasMultiplePages = page.getTotalPages() > 1;
            paginationControls.setVisible(hasMultiplePages);
            // paginationControls.setManaged(hasMultiplePages); // VÀ DÒNG NÀY NỮA NẾU CÓ

            if(prevPageButton != null) prevPageButton.setDisable(page.isFirst());
            if(nextPageButton != null) nextPageButton.setDisable(page.isLast());
        }
    }

    public void removeNotificationCardUI(Node cardNode, boolean wasUnreadWhenDeleted) {
        // Xóa controller khỏi danh sách quản lý
        activeCardControllers.removeIf(controller -> controller.getNotificationCardPane() == cardNode);
        boolean removed = cardsContainerVBox.getChildren().remove(cardNode);

        if (removed) {
            totalNotifications--;
            if (wasUnreadWhenDeleted) {
                decrementUnreadCountAndUpdateDisplay(); // Đã có sẵn
            }
        }

        // Nếu trang hiện tại trống và không phải là trang đầu tiên (và có tổng số thông báo > 0), lùi về trang trước
        // Hoặc nếu trang hiện tại trống, là trang đầu tiên, nhưng tổng số thông báo vẫn còn, tải lại trang 0
        if (cardsContainerVBox.getChildren().isEmpty() && totalNotifications > 0) {
            if (currentPage > 0) {
                loadNotifications(currentPage - 1);
            } else { // currentPage == 0
                loadNotifications(0); // Tải lại trang 0 nếu vẫn còn thông báo
            }
        } else if (totalNotifications == 0) { // Không còn thông báo nào cả
            showUiState(false, "Không có thông báo nào.");
            updateUnreadCountDisplay(); // Cập nhật số chưa đọc (thành 0)
            updatePaginationControls(null); // Ẩn pagination
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
}