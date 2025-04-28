package com.utc2.facilityui.controller.room;

// Không cần import DTO nào cả
import com.utc2.facilityui.service.RoomService; // Import Service
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert; // Import Alert
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap; // Import HashMap
import java.util.Map; // Import Map
import java.util.ResourceBundle;

public class addRoomController implements Initializable {

    @FXML private TextField nameTextField;
    @FXML private TextField descriptionTextField;
    @FXML private TextField locationTextField;
    @FXML private TextField capacityTextField;
    @FXML private TextField imageURLTextField;
    @FXML private TextField buildingIdTextField;
    @FXML private TextField roomTypeIdTextField;
    @FXML private TextField facilityManagerIdTextField;
    @FXML private Button cancelButton;
    @FXML private Button addButton;

    private RoomService roomService;

    public addRoomController() {
        System.out.println("AddRoomController constructor called!");
        this.roomService = new RoomService(); // Khởi tạo service
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("AddRoomController initialize() called!");
        // Listener để chỉ cho phép nhập số vào capacityTextField
        capacityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                capacityTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    void handleAddRoom(ActionEvent event) {
        System.out.println("handleAddRoom() called!");

        // --- Lấy dữ liệu từ các trường nhập liệu ---
        String name = nameTextField.getText().trim();
        String description = descriptionTextField.getText().trim();
        String location = locationTextField.getText().trim();
        String capacityText = capacityTextField.getText().trim();
        String imageURL = imageURLTextField.getText().trim();
        String buildingId = buildingIdTextField.getText().trim();
        String roomTypeId = roomTypeIdTextField.getText().trim();
        String facilityManagerId = facilityManagerIdTextField.getText().trim();

        // --- Kiểm tra các trường bắt buộc ---
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Tên phòng không được để trống.");
            return;
        }
        if (capacityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Sức chứa không được để trống.");
            return;
        }
        if (buildingId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "ID Tòa nhà không được để trống.");
            return;
        }
        if (roomTypeId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "ID Loại phòng không được để trống.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity < 1) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Sức chứa phải ít nhất là 1.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Sức chứa phải là một số nguyên hợp lệ.");
            return;
        }

        // --- TẠO MAP ĐỂ GIỮ DỮ LIỆU ---
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("name", name); // Key phải khớp tên trường DTO backend
        requestData.put("capacity", capacity); // Key phải khớp tên trường DTO backend
        requestData.put("buildingId", buildingId); // Key phải khớp tên trường DTO backend
        requestData.put("roomTypeId", roomTypeId); // Key phải khớp tên trường DTO backend

        // Thêm các trường tùy chọn vào Map CHỈ KHI chúng không rỗng
        if (!description.isEmpty()) {
            requestData.put("description", description); // Key phải khớp tên trường DTO backend
        }
        if (!facilityManagerId.isEmpty()) {
            requestData.put("facilityManagerId", facilityManagerId); // Key phải khớp tên trường DTO backend
        }
        if (!location.isEmpty()) {
            requestData.put("location", location); // Key phải khớp tên trường DTO backend
        }
        if (!imageURL.isEmpty()) {
            requestData.put("img", imageURL); // Key phải khớp tên trường DTO backend (giả sử là 'img')
        }


        // --- Gọi service để gửi yêu cầu ---
        try {
            // Gọi phương thức trong service nhận Map
            roomService.addRoomFromMap(requestData);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm phòng thành công!");
            closeStage(); // Đóng cửa sổ sau khi thành công

        } catch (IOException e) {
            System.err.println("Error adding room via service: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể thêm phòng. Vui lòng kiểm tra kết nối hoặc liên hệ quản trị viên.\nChi tiết: " + e.getMessage());
        } catch (Exception e) { // Bắt các lỗi khác có thể xảy ra
            System.err.println("Unexpected error adding room: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi không xác định", "Đã xảy ra lỗi không mong muốn khi thêm phòng.");
        }
    }

    @FXML
    void handleCancelAddRoom(ActionEvent event) {
        System.out.println("handleCancelAddRoom() called!");
        closeStage();
    }

    private void closeStage() {
        System.out.println("closeStage() called!");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    // Helper method để hiển thị Alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Không hiển thị header
        alert.setContentText(message);
        // Đảm bảo Alert hiển thị trên cùng (nếu cần thiết)
        // Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        // stage.setAlwaysOnTop(true);
        alert.showAndWait();
    }
}