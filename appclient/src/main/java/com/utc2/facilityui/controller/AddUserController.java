package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.UserCreationRequest;
import com.utc2.facilityui.response.UserResponse;
import com.utc2.facilityui.service.UserServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField; // Đảm bảo import đúng
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddUserController {

    @FXML
    private TextField userIdTextField;

    @FXML
    private TextField userNameTextField;

    @FXML
    private TextField fullNameTextField;

    @FXML
    private TextField emailTextField; // Khớp với fx:id="emailTextField" trong FXML đã sửa

    @FXML
    private PasswordField passwordField; // Khớp với fx:id="passwordField" trong FXML đã sửa

    @FXML
    private ComboBox<String> roleNameComboBox; // Khớp với fx:id="roleNameComboBox" trong FXML đã sửa

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    // Callback để thông báo cho cửa sổ cha nếu người dùng được thêm thành công
    private Runnable onUserAddedCallback;

    public void setOnUserAddedCallback(Runnable onUserAddedCallback) {
        this.onUserAddedCallback = onUserAddedCallback;
    }

    @FXML
    public void initialize() {
        // Khởi tạo danh sách vai trò cho ComboBox
        // Lý tưởng nhất, danh sách này nên được lấy từ một Enum hoặc API nếu có thể thay đổi
        roleNameComboBox.getItems().addAll("USER", "ADMIN", "FACILITY_MANAGER");
        roleNameComboBox.setValue("USER"); // Đặt vai trò mặc định
    }

    @FXML
    private void handleAddUserAction() {
        String userId = userIdTextField.getText().trim();
        String username = userNameTextField.getText().trim();
        String fullName = fullNameTextField.getText().trim(); // fullName có thể để trống
        String email = emailTextField.getText().trim();
        String password = passwordField.getText(); // Không trim mật khẩu
        String selectedRole = roleNameComboBox.getValue();

        // === Kiểm tra dữ liệu đầu vào cơ bản ===
        if (userId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Mã số không được để trống.");
            userIdTextField.requestFocus(); // Focus vào trường bị lỗi
            return;
        }
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Tên tài khoản không được để trống.");
            userNameTextField.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Email không được để trống.");
            emailTextField.requestFocus();
            return;
        }
        // Kiểm tra định dạng email cơ bản
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Định dạng email không hợp lệ.");
            emailTextField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Mật khẩu không được để trống.");
            passwordField.requestFocus();
            return;
        }
        // Ví dụ: Kiểm tra độ dài mật khẩu tối thiểu (phía client)
        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Mật khẩu phải có ít nhất 6 ký tự.");
            passwordField.requestFocus();
            return;
        }
        if (selectedRole == null || selectedRole.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Vui lòng chọn vai trò cho người dùng.");
            roleNameComboBox.requestFocus();
            return;
        }
        // === Kết thúc kiểm tra dữ liệu đầu vào ===

        // Avatar có thể null hoặc bạn có thể thêm trường nhập liệu cho nó nếu cần
        String avatar = null;

        UserCreationRequest newUserDto = new UserCreationRequest(
                userId, username, email, password, fullName, avatar, selectedRole
        );

        // Vô hiệu hóa các nút để tránh click nhiều lần
        addButton.setDisable(true);
        cancelButton.setDisable(true);

        // Thực hiện gọi API trên một luồng nền để không làm treo giao diện
        new Thread(() -> {
            try {
                UserResponse createdUser = UserServices.createUser(newUserDto);
                // Cập nhật giao diện trên luồng JavaFX Application Thread
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Tài khoản '" + createdUser.getUsername() + "' đã được thêm thành công!");
                    if (onUserAddedCallback != null) {
                        onUserAddedCallback.run(); // Gọi callback
                    }
                    closeWindow(); // Đóng cửa sổ sau khi thêm thành công
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    // Hiển thị thông báo lỗi chi tiết hơn
                    showAlert(Alert.AlertType.ERROR, "Lỗi Tạo Người Dùng", "Không thể thêm tài khoản:\n" + e.getMessage());
                });
            } finally {
                // Kích hoạt lại các nút dù thành công hay thất bại
                Platform.runLater(() -> {
                    addButton.setDisable(false);
                    cancelButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleCancelAction() {
        closeWindow();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Không có header text
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        // Lấy Stage (cửa sổ) hiện tại từ một control bất kỳ (ví dụ: cancelButton)
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}