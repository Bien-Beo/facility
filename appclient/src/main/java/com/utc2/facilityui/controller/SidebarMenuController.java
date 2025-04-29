package com.utc2.facilityui.controller;

import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.service.UserServices;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class SidebarMenuController implements Initializable { // Implement Initializable

    // Tham chiếu đến BorderPane chính
    private BorderPane mainBorderPane;

    // --- Các thành phần FXML được inject ---
    // Thêm các @FXML cho avatar và text info
    @FXML private ImageView imgAdminAvatar;
    @FXML private Text txtAdminName;
    @FXML private Text txtAdminId;

    // Các Button và Label đã có
    @FXML private Button manageFacilitiesButton;
    @FXML private Button manageBookingsButton;
    @FXML private Button approvalRequestsButton;
    @FXML private Label approvalCountLabel;
    @FXML private Button cancellationRequestsButton;
    @FXML private Button reportButton;
    @FXML private Button resetPasswordButton;
    @FXML private Button logoutButton;

    // Đường dẫn ảnh mặc định (giống InfoPersonController)
    private static final String DEFAULT_AVATAR_PATH = "/com/utc2/facilityui/images/man.png";

    /**
     * Phương thức này được gọi bởi MainScreenController.
     */
    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    /**
     * Phương thức khởi tạo FXML. Được gọi tự động.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing SidebarMenuController...");
        // Đặt trạng thái UI ban đầu
        setUIToLoadingState();
        // Bắt đầu tải thông tin người dùng admin
        loadUserInfo();
        // Cập nhật số lượng approval ban đầu (ví dụ)
        updateApprovalCount(0); // Bắt đầu với 0 hoặc gọi API lấy số thực tế
    }

    /**
     * Đặt UI về trạng thái đang tải dữ liệu ban đầu.
     */
    private void setUIToLoadingState() {
        // Kiểm tra null trước khi truy cập FXML elements
        if (txtAdminName != null) txtAdminName.setText("Loading...");
        if (txtAdminId != null) txtAdminId.setText("ID: Loading...");
        setDefaultAvatar(); // Hiển thị avatar mặc định
    }


    /**
     * Tải thông tin người dùng (Admin) từ service và cập nhật UI.
     * Tương tự như trong InfoPersonController.
     */
    private void loadUserInfo() {
        System.out.println("Sidebar: Starting to load user info task...");
        Task<Map<String, Object>> loadUserTask = new Task<>() {
            @Override
            protected Map<String, Object> call() throws Exception {
                return UserServices.getMyInfo(); // Vẫn dùng service chung
            }
        };

        loadUserTask.setOnSucceeded(event -> {
            Map<String, Object> userMap = loadUserTask.getValue();
            System.out.println("Sidebar: User info task succeeded.");
            Platform.runLater(() -> {
                if (userMap != null && !userMap.isEmpty()) {
                    System.out.println("Sidebar: Updating UI with user info: " + userMap.keySet());
                    updateUserInfoUI(userMap);
                } else {
                    System.out.println("Sidebar: User info map is null or empty.");
                    setUIToDefaultOrError("Admin", "ID: Error", true); // Hiển thị lỗi cụ thể hơn
                }
            });
        });

        loadUserTask.setOnFailed(event -> {
            Throwable exception = loadUserTask.getException();
            System.err.println("Sidebar: User info task failed: " + exception.getMessage());
            exception.printStackTrace();
            Platform.runLater(() -> {
                setUIToDefaultOrError("Error", "ID: Error", true);
                showErrorAlert("Load User Info Error", "Could not load admin information: " + exception.getMessage());
            });
        });

        System.out.println("Sidebar: Starting user info loading thread...");
        Thread backgroundThread = new Thread(loadUserTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    /**
     * Cập nhật các thành phần UI với dữ liệu từ Map.
     * @param userMap Map chứa dữ liệu người dùng (đã kiểm tra không rỗng).
     */
    private void updateUserInfoUI(Map<String, Object> userMap) {
        // Lấy fullName hoặc username nếu fullName null
        String displayName = getStringValueFromMap(userMap, "fullName", null);
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = getStringValueFromMap(userMap, "username", "Admin"); // Dùng username làm dự phòng
        }
        if (txtAdminName != null) txtAdminName.setText(displayName);

        // Lấy userId
        String userId = "N/A";
        if (userMap.containsKey("userId") && userMap.get("userId") != null) {
            Object userIdObj = userMap.get("userId");
            if (userIdObj instanceof String) userId = (String) userIdObj;
            else if (userIdObj instanceof Number) userId = String.format("%.0f", ((Number)userIdObj).doubleValue());
            else userId = userIdObj.toString();
        }
        if (txtAdminId != null) txtAdminId.setText("ID: " + userId);

        // Lấy avatar
        String avatarUrl = getStringValueFromMap(userMap, "avatar", null);
        updateAvatarImage(avatarUrl);
    }

    /**
     * Cập nhật ImageView với URL ảnh đại diện.
     * @param avatarUrl URL của ảnh, có thể là null hoặc rỗng.
     */
    private void updateAvatarImage(String avatarUrl) {
        if (imgAdminAvatar == null) return; // Kiểm tra null
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            System.out.println("Sidebar: Attempting to load avatar from URL: " + avatarUrl);
            try {
                Image avatarImage = new Image(avatarUrl, true);
                avatarImage.errorProperty().addListener((obs, wasError, isError) -> {
                    if (isError) {
                        System.err.println("Sidebar: Failed to load avatar: " + avatarUrl);
                        Platform.runLater(this::setDefaultAvatar);
                    }
                });
                imgAdminAvatar.setImage(avatarImage);
            } catch (Exception e) {
                System.err.println("Sidebar: Error loading avatar: " + e.getMessage() + " URL: " + avatarUrl);
                setDefaultAvatar();
            }
        } else {
            System.out.println("Sidebar: Avatar URL missing. Setting default.");
            setDefaultAvatar();
        }
    }

    /**
     * Helper method để lấy giá trị String từ Map an toàn.
     */
    private String getStringValueFromMap(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        if (value instanceof String && !((String) value).trim().isEmpty()) { // Thêm kiểm tra rỗng
            return (String) value;
        }
        return defaultValue;
    }

    /**
     * Đặt các thành phần UI về trạng thái mặc định hoặc lỗi.
     */
    private void setUIToDefaultOrError(String nameText, String idText, boolean useDefaultAvatar) {
        if (txtAdminName != null) txtAdminName.setText(nameText);
        if (txtAdminId != null) txtAdminId.setText(idText);
        if (useDefaultAvatar) {
            setDefaultAvatar();
        }
        System.out.println("Sidebar UI set to default/error state - Name: " + nameText + ", ID: " + idText);
    }

    /**
     * Tải và đặt ảnh đại diện mặc định.
     */
    private void setDefaultAvatar() {
        if (imgAdminAvatar == null) return; // Kiểm tra null
        try (InputStream stream = getClass().getResourceAsStream(DEFAULT_AVATAR_PATH)) {
            if (stream == null) {
                System.err.println("Sidebar CRITICAL: Cannot find default avatar: " + DEFAULT_AVATAR_PATH);
                imgAdminAvatar.setImage(null); return;
            }
            Image defaultImage = new Image(stream);
            imgAdminAvatar.setImage(defaultImage);
            System.out.println("Sidebar: Default avatar set.");
        } catch (Exception e) {
            System.err.println("Sidebar CRITICAL: Failed to load default avatar: " + e.getMessage());
            imgAdminAvatar.setImage(null); e.printStackTrace();
        }
    }

    // --- Các phương thức xử lý sự kiện cho các nút menu (giữ nguyên) ---

    @FXML private void handleManageFacilities(ActionEvent event) { System.out.println("Manage Facilities clicked."); loadView("/com/utc2/facilityui/view/manageFacility.fxml"); }
    @FXML private void handleManageBookings(ActionEvent event) { System.out.println("Manage Bookings clicked."); loadView("/com/utc2/facilityui/view/manageBookings.fxml"); }
    @FXML private void handleApprovalRequests(ActionEvent event) { System.out.println("Approval Requests clicked."); loadView("/com/utc2/facilityui/view/approvalRequests.fxml"); }
    @FXML private void handleCancellationRequests(ActionEvent event) { System.out.println("Cancellation Requests clicked."); loadView("/com/utc2/facilityui/view/cancellationRequests.fxml"); }
    @FXML private void handleReport(ActionEvent event) { System.out.println("Report clicked."); loadView("/com/utc2/facilityui/view/report.fxml"); }
    @FXML private void handleResetPassword(ActionEvent event) { System.out.println("Reset Password clicked."); loadView("/com/utc2/facilityui/view/resetPassword.fxml"); }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked.");
        TokenStorage.clearToken();
        System.out.println("Token cleared via TokenStorage.");
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            if (stage == null) { System.err.println("Sidebar: Cannot get stage for logout."); return; }
            String loginFxmlPath = "/com/utc2/facilityui/view/Login2.fxml"; // *** KIỂM TRA ĐƯỜNG DẪN NÀY ***
            FXMLLoader loader = new FXMLLoader(getClass().getResource(loginFxmlPath));
            if (loader.getLocation() == null) throw new IOException("Cannot find FXML at: " + loginFxmlPath);
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.centerOnScreen();
            stage.show();
            System.out.println("Navigated back to Login screen.");
        } catch (Exception e) { // Bắt Exception chung
            System.err.println("Sidebar: Error during logout navigation: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Logout Error", "Failed to return to login screen: " + e.getMessage());
        }
    }

    // --- Phương thức tiện ích (giữ nguyên) ---

    private void loadView(String fxmlPath) {
        if (mainBorderPane == null) { System.err.println("Sidebar: Main BorderPane is not set."); showErrorAlert("UI Error", "Cannot load view."); return; }
        try {
            System.out.println("Loading view: " + fxmlPath);
            // *** KIỂM TRA CÁC ĐƯỜNG DẪN NÀY ***
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) throw new IOException("Cannot find FXML at: " + fxmlPath);
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
            System.out.println("View loaded: " + fxmlPath);
        } catch (IOException e) {
            System.err.println("Sidebar: Failed to load view " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Load View Error", "Cannot load view: " + fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1) + "\nError: " + e.getMessage());
        }
    }

    public void updateApprovalCount(int count) {
        if (approvalCountLabel == null) return; // Kiểm tra null
        if (count > 0) {
            approvalCountLabel.setText(String.valueOf(count));
            approvalCountLabel.setVisible(true);
        } else {
            approvalCountLabel.setVisible(false);
        }
        System.out.println("Sidebar: Approval count updated to " + count);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}