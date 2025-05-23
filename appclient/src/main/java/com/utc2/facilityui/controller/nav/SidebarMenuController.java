package com.utc2.facilityui.controller.nav;

import com.utc2.facilityui.auth.TokenStorage; // Vẫn dùng để đăng xuất
import com.utc2.facilityui.service.UserServices;
import com.utc2.facilityui.model.User; // << THÊM IMPORT CHO USER MODEL

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
import javafx.scene.text.Text; // Giữ lại Text nếu FXML dùng Text
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
// Bỏ import java.util.Map; nếu không dùng nữa
import java.util.ResourceBundle;

public class SidebarMenuController implements Initializable {

    private BorderPane mainBorderPane;

    @FXML private ImageView imgAdminAvatar;
    @FXML private Text txtAdminName; // Hoặc Label tùy FXML của bạn
    @FXML private Text txtAdminId;   // Hoặc Label

    @FXML private Button manageFacilitiesButton;
    @FXML private Button manageBookingsButton;
    @FXML private Button approvalRequestsButton;
    @FXML private Label approvalCountLabel;
    @FXML private Button cancellationRequestsButton;
    @FXML private Button reportButton;
    @FXML private Button resetPasswordButton;
    @FXML private Button logoutButton;

    private static final String DEFAULT_AVATAR_PATH = "/com/utc2/facilityui/images/man.png";

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing SidebarMenuController...");
        setUIToLoadingState();
        loadUserInfo();
        updateApprovalCount(0);
    }

    private void setUIToLoadingState() {
        if (txtAdminName != null) txtAdminName.setText("Đang tải...");
        if (txtAdminId != null) txtAdminId.setText("ID: Đang tải...");
        setDefaultAvatar();
    }

    private void loadUserInfo() {
        System.out.println("Sidebar: Starting to load user info task...");
        // THAY ĐỔI KIỂU CỦA TASK THÀNH User
        Task<User> loadUserTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                // Giả định UserServices.getMyInfo() giờ đây trả về User object
                return UserServices.getMyInfo();
            }
        };

        loadUserTask.setOnSucceeded(event -> {
            // THAY ĐỔI KIỂU CỦA BIẾN NHẬN KẾT QUẢ
            User user = loadUserTask.getValue(); // DÒNG 93 SẼ LÀ ĐÂY (hoặc gần đó)
            System.out.println("Sidebar: User info task succeeded.");
            Platform.runLater(() -> {
                if (user != null && user.getUserId() != null) { // Kiểm tra user và userId
                    System.out.println("Sidebar: Updating UI with user info for: " + user.getUsername());
                    updateUserInfoUI(user); // << TRUYỀN ĐỐI TƯỢNG User
                } else {
                    System.out.println("Sidebar: User object or user ID is null or empty.");
                    setUIToDefaultOrError("Lỗi Tải User", "ID: Lỗi", true);
                }
            });
        });

        loadUserTask.setOnFailed(event -> {
            Throwable exception = loadUserTask.getException();
            System.err.println("Sidebar: User info task failed: " + exception.getMessage());
            exception.printStackTrace();
            Platform.runLater(() -> {
                setUIToDefaultOrError("Lỗi", "ID: Lỗi", true);
                showErrorAlert("Lỗi Tải Thông Tin Người Dùng", "Không thể tải thông tin admin: " + exception.getMessage());
            });
        });

        System.out.println("Sidebar: Starting user info loading thread...");
        Thread backgroundThread = new Thread(loadUserTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    /**
     * Cập nhật các thành phần UI với dữ liệu từ đối tượng User.
     * @param user Đối tượng User chứa dữ liệu (đã kiểm tra không null).
     */
    private void updateUserInfoUI(User user) {
        // Lớp User của bạn có getUsername(). Nếu server trả về fullName và bạn muốn dùng nó,
        // bạn cần thêm trường fullName vào model User.java client
        // và đảm bảo UserServices.getMyInfo() parse nó vào User object.
        // Hiện tại, chúng ta dùng getUsername() vì đó là những gì User.java có.
        String displayName = user.getUsername() != null ? user.getUsername() : "Admin";
        if (txtAdminName != null) txtAdminName.setText(displayName);

        // Lấy userId (hoặc id tùy theo định danh chính bạn dùng)
        // Model User của bạn có cả getId() và getUserId(). Hãy chọn đúng.
        // Dựa trên các API khác, có vẻ "userId" là định danh dùng chung.
        String displayId = user.getUserId() != null ? user.getUserId() : "N/A";
        if (txtAdminId != null) txtAdminId.setText("ID: " + displayId);

        // Lấy avatar
        String avatarUrl = user.getAvatar();
        updateAvatarImage(avatarUrl);
    }

    private void updateAvatarImage(String avatarUrl) {
        if (imgAdminAvatar == null) return;
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            System.out.println("Sidebar: Attempting to load avatar from URL: " + avatarUrl);
            try {
                Image avatarImage = new Image(avatarUrl, true); // true để tải nền
                avatarImage.errorProperty().addListener((obs, wasError, isError) -> {
                    if (isError) {
                        System.err.println("Sidebar: Failed to load avatar image from URL: " + avatarUrl);
                        Platform.runLater(this::setDefaultAvatar);
                    }
                });
                // Kiểm tra nếu ảnh lỗi ngay khi khởi tạo (ví dụ URL không hợp lệ ngay từ đầu)
                if (avatarImage.isError()) {
                    System.err.println("Sidebar: Initial error loading avatar: " + avatarUrl);
                    setDefaultAvatar();
                } else {
                    imgAdminAvatar.setImage(avatarImage);
                }
            } catch (Exception e) { // Bắt các lỗi như IllegalArgumentException nếu URL sai định dạng
                System.err.println("Sidebar: Exception loading avatar: " + e.getMessage() + " URL: " + avatarUrl);
                setDefaultAvatar();
            }
        } else {
            System.out.println("Sidebar: Avatar URL is missing or empty. Setting default avatar.");
            setDefaultAvatar();
        }
    }

    // Bỏ phương thức getStringValueFromMap vì không cần nữa

    private void setUIToDefaultOrError(String nameText, String idText, boolean useDefaultAvatar) {
        if (txtAdminName != null) txtAdminName.setText(nameText);
        if (txtAdminId != null) txtAdminId.setText(idText);
        if (useDefaultAvatar) {
            setDefaultAvatar();
        }
        System.out.println("Sidebar UI set to default/error state - Name: " + nameText + ", ID: " + idText);
    }

    private void setDefaultAvatar() {
        if (imgAdminAvatar == null) return;
        try (InputStream stream = getClass().getResourceAsStream(DEFAULT_AVATAR_PATH)) {
            if (stream == null) {
                System.err.println("Sidebar CRITICAL: Cannot find default avatar resource: " + DEFAULT_AVATAR_PATH);
                imgAdminAvatar.setImage(null); return;
            }
            Image defaultImage = new Image(stream);
            imgAdminAvatar.setImage(defaultImage);
            System.out.println("Sidebar: Default avatar set.");
        } catch (Exception e) {
            System.err.println("Sidebar CRITICAL: Failed to load default avatar resource: " + e.getMessage());
            imgAdminAvatar.setImage(null); e.printStackTrace();
        }
    }

    // --- Các phương thức xử lý sự kiện cho các nút menu ---
    @FXML private void handleManageFacilities(ActionEvent event) { System.out.println("Manage Facilities clicked."); loadView("/com/utc2/facilityui/view/manageFacility.fxml"); }
    @FXML private void handleManageBookings(ActionEvent event) { System.out.println("Manage Bookings clicked."); loadView("/com/utc2/facilityui/view/manageBookings.fxml"); }
    @FXML private void handleApprovalRequests(ActionEvent event) { System.out.println("Approval Requests clicked."); loadView("/com/utc2/facilityui/view/approvalRequests.fxml"); }
    @FXML private void handleCancellationRequests(ActionEvent event) { System.out.println("Cancellation Requests clicked."); loadView("/com/utc2/facilityui/view/cancellationRequests.fxml"); }
    @FXML private void handleReport(ActionEvent event) { System.out.println("Report clicked."); loadView("/com/utc2/facilityui/view/report.fxml"); }
    @FXML private void handleResetPassword(ActionEvent event) { System.out.println("Reset Password clicked."); loadView("/com/utc2/facilityui/view/resetPassword.fxml"); }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked.");
        // Sử dụng TokenStorage đã được cập nhật để xóa cả token và user info
        TokenStorage.logout(); // Giả sử TokenStorage.logout() xóa cả token và currentUser
        System.out.println("Session cleared via TokenStorage.logout().");
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            if (stage == null) { System.err.println("Sidebar: Cannot get stage for logout."); return; }
            // Đảm bảo đường dẫn đến FXML đăng nhập là chính xác
            String loginFxmlPath = "/com/utc2/facilityui/view/login2.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(loginFxmlPath));
            if (loader.getLocation() == null) { // Kiểm tra resource
                System.err.println("Sidebar CRITICAL: Cannot find login FXML: " + loginFxmlPath);
                showErrorAlert("Logout Error", "Cannot find login screen resource.");
                return;
            }
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("Đăng nhập"); // Đổi tiêu đề lại cho phù hợp
            stage.centerOnScreen();
            stage.show();
            System.out.println("Navigated back to Login screen.");
        } catch (Exception e) {
            System.err.println("Sidebar: Error during logout navigation: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Lỗi Đăng Xuất", "Không thể quay về màn hình đăng nhập: " + e.getMessage());
        }
    }

    private void loadView(String fxmlPath) {
        if (mainBorderPane == null) {
            System.err.println("Sidebar: Main BorderPane is not set. Cannot load view: " + fxmlPath);
            showErrorAlert("Lỗi Giao Diện", "Không thể tải giao diện. Vui lòng thử lại sau.");
            return;
        }
        try {
            System.out.println("Loading view: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) { // Kiểm tra resource
                System.err.println("Sidebar CRITICAL: Cannot find FXML to load into center: " + fxmlPath);
                showErrorAlert("Lỗi Tải Giao Diện", "Không tìm thấy file giao diện: " + fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1));
                return;
            }
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
            System.out.println("View loaded into BorderPane center: " + fxmlPath);
        } catch (IOException e) {
            System.err.println("Sidebar: Failed to load view " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Lỗi Tải Giao Diện", "Không thể tải giao diện: " + fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1) + "\nLỗi: " + e.getMessage());
        }
    }

    public void updateApprovalCount(int count) {
        if (approvalCountLabel == null) return;
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