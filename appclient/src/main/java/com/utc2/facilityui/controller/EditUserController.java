package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.UserUpdateRequest;
import com.utc2.facilityui.model.User; // Model User của client
import com.utc2.facilityui.response.UserResponse;
import com.utc2.facilityui.service.UserServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EditUserController {

    @FXML private Label userid; // Để hiển thị ID (UUID) và UserID (business key)
    @FXML private TextField name; // Tên tài khoản (username)
    @FXML private TextField fullName; // Họ và tên
    @FXML private ComboBox<String> roleName; // Vai trò

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private User currentUser; // Lưu trữ thông tin người dùng đang được sửa
    private String originalUserId; // Lưu trữ UserID (business key) gốc để gọi API

    private Runnable onUserUpdatedCallback; // Callback để thông báo cho AccountManagementController

    public void setOnUserUpdatedCallback(Runnable onUserUpdatedCallback) {
        this.onUserUpdatedCallback = onUserUpdatedCallback;
    }

    @FXML
    public void initialize() {
        // Khởi tạo ComboBox vai trò
        roleName.getItems().addAll("USER", "ADMIN", "FACILITY_MANAGER");
        // Không đặt giá trị mặc định ở đây, sẽ đặt khi setUserData
    }

    public void setUserData(User userToEdit) {
        this.currentUser = userToEdit;
        this.originalUserId = userToEdit.getUserId(); // Lấy UserID gốc (business key)

        // Hiển thị ID (UUID) và UserID (business key)
        // Giả sử User.java có getRawId() trả về UUID và getRawUserId() trả về mã số
        userid.setText("ID: " + userToEdit.getId() + "  |   " + userToEdit.getFullName());

        // Đổ dữ liệu gốc (chưa định dạng "N/A") vào các trường
        // Yêu cầu User.java có các phương thức getRaw...()
        name.setText(userToEdit.getUsername());
        fullName.setText(userToEdit.getFullName());
        if (userToEdit.getRoleName() != null && !userToEdit.getRoleName().isEmpty()) {
            roleName.setValue(userToEdit.getRoleName());
        } else {
            roleName.setValue("USER"); // Hoặc một giá trị mặc định nếu role gốc là null/empty
        }
    }

    @FXML
    private void handleSaveAction() {
        if (currentUser == null || originalUserId == null || originalUserId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Dữ Liệu", "Không có thông tin người dùng để cập nhật.");
            return;
        }

        String updatedUsername = name.getText().trim();
        String updatedFullName = fullName.getText().trim();
        String updatedRoleName = roleName.getValue();

        // Kiểm tra đầu vào
        if (updatedUsername.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Tên tài khoản không được để trống.");
            name.requestFocus();
            return;
        }
        if (updatedRoleName == null || updatedRoleName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Vui lòng chọn vai trò.");
            roleName.requestFocus();
            return;
        }
        // fullName có thể trống

        UserUpdateRequest updateRequest = new UserUpdateRequest(updatedUsername, updatedFullName, updatedRoleName);

        saveButton.setDisable(true);
        cancelButton.setDisable(true);

        new Thread(() -> {
            try {
                // Gọi service updateUser với originalUserId (business key) và DTO mới
                UserResponse updatedUserResponse = UserServices.updateUser(originalUserId, updateRequest);
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Thông tin tài khoản '" + updatedUserResponse.getUsername() + "' đã được cập nhật.");
                    if (onUserUpdatedCallback != null) {
                        onUserUpdatedCallback.run(); // Thông báo cho AccountManagementController
                    }
                    closeWindow();
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Cập Nhật", "Không thể cập nhật tài khoản: " + e.getMessage());
                });
            } finally {
                Platform.runLater(() -> {
                    saveButton.setDisable(false);
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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}