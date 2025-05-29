package com.utc2.facilityui.controller.room;

import com.utc2.facilityui.model.BuildingItem;
import com.utc2.facilityui.model.RoomTypeItem;
import com.utc2.facilityui.model.UserItem;
import com.utc2.facilityui.service.BuildingClientService; // Service bạn đã có
import com.utc2.facilityui.service.RoomService;       // Service chính
import com.utc2.facilityui.service.RoomTypeClientService; // Service bạn đã có
import com.utc2.facilityui.service.UserClientService;   // Service bạn đã có

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class addRoomController implements Initializable {

    @FXML private TextField nameTextField;
    @FXML private TextField descriptionTextField;
    @FXML private TextField locationTextField;
    @FXML private TextField capacityTextField;
    @FXML private TextField imageURLTextField;

    // --- FXML Injections cho ComboBoxes (khớp với fx:id trong FXML) ---
    @FXML private ComboBox<BuildingItem> buildingName;    // Trước là buildingIdTextField
    @FXML private ComboBox<RoomTypeItem> roomTypeName;      // Trước là roomTypeIdTextField
    @FXML private ComboBox<UserItem> facilityManagerId; // Trước là facilityManagerIdTextField

    @FXML private Button cancelButton;
    @FXML private Button addButton;

    private RoomService roomService;
    private BuildingClientService buildingClientService;
    private RoomTypeClientService roomTypeClientService;
    private UserClientService userClientService;

    public addRoomController() {
        // Khởi tạo các services
        this.roomService = new RoomService();
        this.buildingClientService = new BuildingClientService();
        this.roomTypeClientService = new RoomTypeClientService();
        this.userClientService = new UserClientService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("AddRoomController initialize() called!");
        capacityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                capacityTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Setup và tải dữ liệu cho các ComboBoxes
        setupBuildingComboBox();
        setupRoomTypeComboBox();
        setupFacilityManagerComboBox();
    }

    private void setupBuildingComboBox() {
        buildingName.setConverter(new StringConverter<BuildingItem>() {
            @Override
            public String toString(BuildingItem building) {
                return building == null ? null : building.getName();
            }
            @Override
            public BuildingItem fromString(String string) { return null; /* Không cần thiết */ }
        });
        // Đặt placeholder text nếu ComboBox rỗng
        buildingName.setPlaceholder(new Label("Đang tải tòa nhà..."));
        new Thread(() -> {
            try {
                List<BuildingItem> buildings = buildingClientService.getAllBuildings();
                Platform.runLater(() -> {
                    buildingName.setItems(FXCollections.observableArrayList(buildings));
                    if (buildings.isEmpty()) {
                        buildingName.setPlaceholder(new Label("Không có tòa nhà"));
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu", "Không thể tải danh sách tòa nhà: " + e.getMessage());
                    buildingName.setPlaceholder(new Label("Lỗi tải tòa nhà"));
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void setupRoomTypeComboBox() {
        roomTypeName.setConverter(new StringConverter<RoomTypeItem>() {
            @Override
            public String toString(RoomTypeItem roomType) {
                return roomType == null ? null : roomType.getName();
            }
            @Override
            public RoomTypeItem fromString(String string) { return null; }
        });
        roomTypeName.setPlaceholder(new Label("Đang tải loại phòng..."));
        new Thread(() -> {
            try {
                List<RoomTypeItem> roomTypes = roomTypeClientService.getAllRoomTypes();
                Platform.runLater(() -> {
                    roomTypeName.setItems(FXCollections.observableArrayList(roomTypes));
                    if (roomTypes.isEmpty()) {
                        roomTypeName.setPlaceholder(new Label("Không có loại phòng"));
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu", "Không thể tải danh sách loại phòng: " + e.getMessage());
                    roomTypeName.setPlaceholder(new Label("Lỗi tải loại phòng"));
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void setupFacilityManagerComboBox() {
        facilityManagerId.setConverter(new StringConverter<UserItem>() {
            @Override
            public String toString(UserItem user) {
                return user == null ? null : user.getDisplayName(); // Sử dụng getDisplayName()
            }
            @Override
            public UserItem fromString(String string) { return null; }
        });
        facilityManagerId.setPlaceholder(new Label("Đang tải người quản lý..."));
        new Thread(() -> {
            try {
                List<UserItem> managers = userClientService.getAllFacilityManagers();
                Platform.runLater(() -> {
                    facilityManagerId.setItems(FXCollections.observableArrayList(managers));
                    if (managers.isEmpty()) {
                        facilityManagerId.setPlaceholder(new Label("Không có người quản lý"));
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu", "Không thể tải danh sách người quản lý: " + e.getMessage());
                    facilityManagerId.setPlaceholder(new Label("Lỗi tải quản lý"));
                });
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void handleAddRoom(ActionEvent event) {
        System.out.println("handleAddRoom() called!");

        String name = nameTextField.getText().trim();
        String description = descriptionTextField.getText().trim();
        String location = locationTextField.getText().trim();
        String capacityText = capacityTextField.getText().trim();
        String imageURL = imageURLTextField.getText().trim();

        BuildingItem selectedBuilding = buildingName.getSelectionModel().getSelectedItem();
        RoomTypeItem selectedRoomType = roomTypeName.getSelectionModel().getSelectedItem();
        UserItem selectedManager = facilityManagerId.getSelectionModel().getSelectedItem();

        // --- Kiểm tra các trường bắt buộc ---
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Tên phòng không được để trống.");
            nameTextField.requestFocus(); return;
        }
        if (capacityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Sức chứa không được để trống.");
            capacityTextField.requestFocus(); return;
        }
        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity < 1) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Sức chứa phải ít nhất là 1.");
                capacityTextField.requestFocus(); return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Sức chứa phải là một số nguyên hợp lệ.");
            capacityTextField.requestFocus(); return;
        }
        if (selectedBuilding == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng chọn tòa nhà.");
            buildingName.requestFocus(); return;
        }
        if (selectedRoomType == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng chọn loại phòng.");
            roomTypeName.requestFocus(); return;
        }

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("name", name);
        requestData.put("capacity", capacity);
        requestData.put("buildingId", selectedBuilding.getId()); // Lấy ID từ ComboBox
        requestData.put("roomTypeId", selectedRoomType.getId()); // Lấy ID từ ComboBox

        if (!description.isEmpty()) requestData.put("description", description);
        if (selectedManager != null && selectedManager.getId() != null && !selectedManager.getId().isEmpty()) {
            requestData.put("facilityManagerId", selectedManager.getId()); // Lấy ID từ ComboBox
        }
        if (!location.isEmpty()) requestData.put("location", location);
        if (!imageURL.isEmpty()) requestData.put("img", imageURL);

        System.out.println("Request Data to Server: " + requestData);
        addButton.setDisable(true); // Vô hiệu hóa nút khi đang xử lý
        cancelButton.setDisable(true);

        new Thread(() -> { // Thực hiện gọi API trên luồng nền
            try {
                roomService.addRoomFromMap(requestData);
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm phòng thành công!");
                    closeStage();
                    // TODO: Thông báo cho màn hình danh sách phòng để làm mới (nếu cần)
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể thêm phòng: " + e.getMessage());
                    addButton.setDisable(false); // Kích hoạt lại nút
                    cancelButton.setDisable(false);
                });
                e.printStackTrace();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Lỗi không xác định", "Đã xảy ra lỗi không mong muốn.");
                    addButton.setDisable(false); // Kích hoạt lại nút
                    cancelButton.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void handleCancelAddRoom(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            if (cancelButton != null && cancelButton.getScene() != null && cancelButton.getScene().getWindow() != null) {
                alert.initOwner(cancelButton.getScene().getWindow());
            }
            alert.showAndWait();
        });
    }
}