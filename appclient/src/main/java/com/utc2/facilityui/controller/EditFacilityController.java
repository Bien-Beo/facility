package com.utc2.facilityui.controller;

import com.google.gson.Gson;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.Facility;
import javafx.application.Platform; // Import Platform nếu chưa có
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class EditFacilityController {

    // Các @FXML fields khớp với fx:id trong FXML (đã đổi tên locationTextField)
    @FXML private TextField name;
    @FXML private TextField description;
    @FXML private Spinner<Integer> capacity;
    @FXML private TextField locationTextField; // Đã đổi tên
    @FXML private TextField buildingName;
    @FXML private TextField roomTypeName;
    @FXML private TextField facilityManagerId;
    @FXML private ComboBox<String> status;
    @FXML private TextField img;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Facility currentFacility;
    private ObservableList<Facility> facilityList;
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Phương thức này cần được gọi từ FacilityController
    public void setFacilityList(ObservableList<Facility> facilityList) {
        this.facilityList = facilityList;
        // Thêm debug để xác nhận facilityList đã được nhận
        System.out.println("DEBUG (EditFacilityController): facilityList " + (this.facilityList != null ? "đã được set." : "là null."));
    }

    public void setFacility(Facility facility) {
        this.currentFacility = facility;
        if (facility == null) {
            System.err.println("Error: Facility object is null in EditFacilityController.");
            clearFields();
            return;
        }
        System.out.println("DEBUG: Bắt đầu setFacility cho Facility ID: " + facility.getId());

        // Populate fields with existing data
        name.setText(facility.getName());
        description.setText(facility.getDescription());
        capacity.getValueFactory().setValue(facility.getCapacity() > 0 ? facility.getCapacity() : 1);
        locationTextField.setText(facility.getLocation());
        buildingName.setText(facility.getBuildingName());
        roomTypeName.setText(facility.getRoomTypeName());
        facilityManagerId.setText(facility.getFacilityManagerId());
        img.setText(facility.getImg());

        // Populate Status ComboBox với các String cố định khớp Enum backend
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "AVAILABLE",
                "UNDER_MAINTENANCE"
        );

        // Debug và set Items cho ComboBox status
        if (status == null) {
            System.err.println("LỖI DEBUG: ComboBox status BỊ NULL trong setFacility!");
            return;
        } else {
            // System.out.println("DEBUG: ComboBox status không null."); // Có thể bỏ bớt debug này
        }

        try {
            status.setItems(statusOptions);
            // System.out.println("DEBUG: Đã gọi status.setItems. Số lượng items: " + (status.getItems() != null ? status.getItems().size() : "null"));
            // if (status.getItems() != null && !status.getItems().isEmpty()) {
            // System.out.println("DEBUG: Item đầu tiên trong ComboBox: '" + status.getItems().get(0) + "'");
            // System.out.println("DEBUG: Danh sách items trong ComboBox: " + status.getItems());
            // } else {
            // System.out.println("DEBUG: ComboBox không có items sau khi setItems.");
            // }
        } catch (Exception e) {
            System.err.println("LỖI DEBUG: Exception khi gọi status.setItems:");
            e.printStackTrace();
        }

        // Set the current status
        String currentStatusValue = facility.getStatus();
        // System.out.println("DEBUG: Trạng thái đọc từ Facility Model: '" + currentStatusValue + "'");

        ObservableList<String> currentOptionsInComboBox = status.getItems();
        if (currentOptionsInComboBox == null) {
            // System.err.println("LỖI DEBUG: status.getItems() trả về null sau khi setItems!");
            currentOptionsInComboBox = FXCollections.observableArrayList();
        }

        if (currentStatusValue != null && currentOptionsInComboBox.contains(currentStatusValue)) {
            // System.out.println("DEBUG: Tìm thấy trạng thái '" + currentStatusValue + "' trong options. Đang đặt giá trị...");
            try {
                status.setValue(currentStatusValue);
                // System.out.println("DEBUG: Đã gọi status.setValue('" + currentStatusValue + "'). Giá trị hiện tại của ComboBox: '" + status.getValue() + "'");
            } catch (Exception e) {
                System.err.println("LỖI DEBUG: Exception khi gọi status.setValue:");
                e.printStackTrace();
            }
        } else {
            // if (currentStatusValue == null) {
            // System.out.println("DEBUG: Trạng thái từ Model là null.");
            // } else {
            // System.out.println("DEBUG: Trạng thái '" + currentStatusValue + "' không có trong options: " + currentOptionsInComboBox);
            // }
            // System.out.println("DEBUG: Đang clear selection.");
            try {
                status.getSelectionModel().clearSelection();
                // System.out.println("DEBUG: Đã gọi clearSelection. Giá trị hiện tại của ComboBox: '" + status.getValue() + "'");
            } catch (Exception e) {
                System.err.println("LỖI DEBUG: Exception khi gọi status.getSelectionModel().clearSelection():");
                e.printStackTrace();
            }
        }
        // System.out.println("DEBUG: Kết thúc setFacility cho Facility ID: " + facility.getId());
    }

    private void clearFields() {
        name.clear();
        description.clear();
        capacity.getValueFactory().setValue(1);
        locationTextField.clear();
        buildingName.clear();
        roomTypeName.clear();
        facilityManagerId.clear();
        img.clear();
        if (status != null) {
            status.getSelectionModel().clearSelection();
            status.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void handleSave() {
        System.out.println("DEBUG: handleSave() method entered!"); // Giữ lại dòng debug này

        if (currentFacility == null || currentFacility.getId() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot save: No facility selected or facility has no ID.");
            return;
        }
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "User not logged in or token is missing.");
            return;
        }

        // 1. Tạo Map
        Map<String, Object> updateRequestData = new HashMap<>();
        updateRequestData.put("name", name.getText());
        updateRequestData.put("description", description.getText());
        updateRequestData.put("capacity", capacity.getValue());
        updateRequestData.put("location", locationTextField.getText());
        updateRequestData.put("buildingName", buildingName.getText());
        updateRequestData.put("roomTypeName", roomTypeName.getText());
        updateRequestData.put("facilityManagerId", facilityManagerId.getText());
        updateRequestData.put("img", img.getText());
        String selectedStatus = null;
        if (status != null) {
            selectedStatus = status.getValue();
        }
        updateRequestData.put("status", selectedStatus);

        // 2. Convert Map to JSON
        String requestBody = gson.toJson(updateRequestData);
        System.out.println("Sending JSON for PATCH: " + requestBody);

        // 3. Build và gửi HTTP Request
        String apiBaseUrl = "http://localhost:8080"; // Port 8080
        String contextPath = "/facility";             // Context path
        // --- ĐÃ SỬA LẠI API URL ĐỂ BỎ /api/v1 ---
        String apiUrl = apiBaseUrl + contextPath + "/rooms/" + currentFacility.getId();
        System.out.println("API URL (PATCH - Corrected): " + apiUrl); // URL đúng
        // --- KẾT THÚC SỬA ---

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl)) // Sử dụng apiUrl đã sửa
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody)) // Dùng PATCH
                .build();

        // 4. Send the request asynchronously và xử lý response (Có debug client)
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("DEBUG CLIENT: Received response! Status Code: " + response.statusCode());
                    // System.out.println("DEBUG CLIENT: Response Body: " + response.body()); // Bật nếu cần xem body
                    return response.statusCode();
                })
                .thenAccept(statusCode -> {
                    System.out.println("DEBUG CLIENT: Entering thenAccept block. Status Code: " + statusCode);
                    Platform.runLater(() -> { // Luôn dùng Platform.runLater cho các tác vụ UI
                        System.out.println("DEBUG CLIENT: Executing Platform.runLater in thenAccept.");
                        // Các mã thành công cho PATCH có thể là 200 hoặc 204
                        if (statusCode == 200 || statusCode == 204) {
                            System.out.println("DEBUG CLIENT: Success condition met.");
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Facility updated successfully!");
                            // Chỉ gọi update nếu facilityList không null
                            if (facilityList != null) {
                                updateFacilityInListFromMap(updateRequestData);
                            } else {
                                System.err.println("WARN: facilityList is null in handleSave, cannot update table directly.");
                                // Có thể thông báo người dùng cần làm mới bảng thủ công
                            }
                            closeDialog();
                        } else {
                            System.out.println("DEBUG CLIENT: Failure condition met.");
                            // Có thể thêm xử lý chi tiết hơn cho các mã lỗi khác (400, 401, 403, 404...)
                            showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update facility. Status code: " + statusCode);
                        }
                    });
                })
                .exceptionally(e -> {
                    System.err.println("DEBUG CLIENT: Entering exceptionally block.");
                    Throwable cause = (e instanceof java.util.concurrent.CompletionException) ? e.getCause() : e;
                    System.err.println("DEBUG CLIENT: Exception type: " + cause.getClass().getName());
                    System.err.println("DEBUG CLIENT: Exception message: " + cause.getMessage());
                    Platform.runLater(() -> { // Luôn dùng Platform.runLater cho các tác vụ UI
                        System.out.println("DEBUG CLIENT: Executing Platform.runLater in exceptionally.");
                        showAlert(Alert.AlertType.ERROR, "Request Error", "Error sending update request: " + cause.getMessage());
                        // cause.printStackTrace(); // In full stack trace nếu cần debug sâu
                    });
                    return null; // exceptionally phải trả về một giá trị (có thể là null)
                });
    }

    // Phương thức cập nhật Facility trong ObservableList từ dữ liệu trong Map
    private void updateFacilityInListFromMap(Map<String, Object> dataMap) {
        // Thêm kiểm tra null cho facilityList ngay đầu
        if (facilityList == null) {
            System.err.println("ERROR in updateFacilityInListFromMap: facilityList is null.");
            return;
        }
        if (currentFacility == null) {
            System.err.println("ERROR in updateFacilityInListFromMap: currentFacility is null.");
            return;
        }

        int index = -1;
        for(int i=0; i< facilityList.size(); i++){
            // Đảm bảo facilityList.get(i) và getId() không null trước khi gọi equals
            Facility item = facilityList.get(i);
            if(item != null && item.getId() != null && item.getId().equals(currentFacility.getId())){
                index = i;
                break;
            }
        }

        if(index != -1){
            Facility facilityToUpdate = facilityList.get(index);
            if (facilityToUpdate == null) {
                System.err.println("ERROR in updateFacilityInListFromMap: facilityToUpdate at index " + index + " is null.");
                return;
            }
            try {
                // Cập nhật các trường của facilityToUpdate từ dataMap
                facilityToUpdate.setName((String)dataMap.getOrDefault("name", facilityToUpdate.getName()));
                facilityToUpdate.setDescription((String)dataMap.getOrDefault("description", facilityToUpdate.getDescription()));
                Object capacityObj = dataMap.get("capacity");
                if (capacityObj instanceof Number) {
                    facilityToUpdate.setCapacity(((Number) capacityObj).intValue());
                }
                facilityToUpdate.setLocation((String)dataMap.getOrDefault("location", facilityToUpdate.getLocation()));
                facilityToUpdate.setBuildingName((String)dataMap.getOrDefault("buildingName", facilityToUpdate.getBuildingName()));
                facilityToUpdate.setRoomTypeName((String)dataMap.getOrDefault("roomTypeName", facilityToUpdate.getRoomTypeName()));
                // Sử dụng setter đã đổi tên trong Facility model
                facilityToUpdate.setFacilityManagerId((String)dataMap.getOrDefault("facilityManagerId", facilityToUpdate.getFacilityManagerId()));
                facilityToUpdate.setImg((String)dataMap.getOrDefault("img", facilityToUpdate.getImg()));
                facilityToUpdate.setStatus((String)dataMap.getOrDefault("status", facilityToUpdate.getStatus()));

                // Thay thế phần tử cũ bằng phần tử đã cập nhật để kích hoạt cập nhật TableView
                facilityList.set(index, facilityToUpdate);
                System.out.println("DEBUG: Updated facility in list at index: " + index);
            } catch (ClassCastException e) {
                System.err.println("Error updating facility from map due to ClassCastException: " + e.getMessage());
            }
        } else {
            System.err.println("Could not find the facility with ID " + currentFacility.getId() + " in the list to update.");
        }
    }


    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // Đảm bảo alert hiển thị trên UI thread
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            // Cố gắng đặt owner cho Alert để nó hiển thị đúng vị trí
            try {
                if (cancelButton != null && cancelButton.getScene() != null && cancelButton.getScene().getWindow() != null) {
                    alert.initOwner(cancelButton.getScene().getWindow());
                }
            } catch (Exception e) {
                // Bỏ qua nếu không lấy được owner
            }
            alert.showAndWait();
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                try {
                    if (cancelButton != null && cancelButton.getScene() != null && cancelButton.getScene().getWindow() != null) {
                        alert.initOwner(cancelButton.getScene().getWindow());
                    }
                } catch (Exception e) {
                    // Bỏ qua nếu không lấy được owner
                }
                alert.showAndWait();
            });
        }
    }
}