package com.utc2.facilityui.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.helper.Config;
import com.utc2.facilityui.model.BuildingItem;
import com.utc2.facilityui.model.Facility;
import com.utc2.facilityui.model.RoomTypeItem;
import com.utc2.facilityui.model.UserItem;
import com.utc2.facilityui.service.BuildingClientService;
import com.utc2.facilityui.service.RoomTypeClientService;
import com.utc2.facilityui.service.UserClientService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditFacilityController implements Initializable {

    @FXML private Label id;
    @FXML private TextField name;
    @FXML private TextField description;
    @FXML private TextField capacity;
    @FXML private TextField locationTextField;
    @FXML private TextField img;
    @FXML private ComboBox<String> status;

    @FXML private ComboBox<BuildingItem> building;
    @FXML private ComboBox<RoomTypeItem> typeRoom;
    @FXML private ComboBox<UserItem> facilityManager;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Facility currentFacility;
    private ObservableList<Facility> facilityList;

    private final Gson gson = new GsonBuilder().create();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private BuildingClientService buildingClientService;
    private RoomTypeClientService roomTypeClientService;
    private UserClientService userClientService;

    public EditFacilityController() {
        this.buildingClientService = new BuildingClientService();
        this.roomTypeClientService = new RoomTypeClientService();
        this.userClientService = new UserClientService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "AVAILABLE",
                "UNDER_MAINTENANCE"
        );
        status.setItems(statusOptions);

        capacity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                capacity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        setupBuildingComboBox();
        setupRoomTypeComboBox();
        setupFacilityManagerComboBox();
    }

    public void setFacilityToEdit(Facility facility) {
        this.currentFacility = facility;
        if (facility == null) {
            showErrorAlert("Lỗi Dữ Liệu", "Không có thông tin phòng để chỉnh sửa.");
            closeDialog();
            return;
        }
        displayFacilityData();
    }

    public void setFacilityObservableList(ObservableList<Facility> facilityList) {
        this.facilityList = facilityList;
    }

    private void displayFacilityData() {
        if (currentFacility == null) return;

        id.setText("ID: " + getStringOrEmpty(currentFacility.getId()));
        name.setText(getStringOrEmpty(currentFacility.getName()));
        description.setText(getStringOrEmpty(currentFacility.getDescription()));
        capacity.setText(String.valueOf(currentFacility.getCapacity() > 0 ? currentFacility.getCapacity() : ""));
        locationTextField.setText(getStringOrEmpty(currentFacility.getLocation()));
        img.setText(getStringOrEmpty(currentFacility.getImg()));

        if (currentFacility.getStatus() != null && status.getItems().contains(currentFacility.getStatus())) {
            status.setValue(currentFacility.getStatus());
        } else {
            status.getSelectionModel().clearSelection();
            status.setPromptText("Chọn trạng thái");
        }
    }

    private void setupBuildingComboBox() {
        // Mục placeholder/hướng dẫn cho ComboBox Tòa nhà
        final BuildingItem placeholderBuilding = new BuildingItem(null, "Chọn Tòa nhà");

        building.setConverter(new StringConverter<BuildingItem>() {
            @Override public String toString(BuildingItem object) {
                // Nếu là đối tượng placeholder và ID là null, hiển thị tên của nó (ví dụ "Chọn Tòa nhà")
                // Ngược lại, hiển thị tên thật của tòa nhà
                return object == null ? null : object.getName();
            }
            @Override public BuildingItem fromString(String string) { return null; }
        });
        building.setPlaceholder(new Label("Đang tải tòa nhà...")); // Hiển thị trong khi tải

        new Thread(() -> {
            try {
                List<BuildingItem> buildingsData = buildingClientService.getAllBuildings();
                Platform.runLater(() -> {
                    ObservableList<BuildingItem> buildingItems = FXCollections.observableArrayList();
                    buildingItems.add(placeholderBuilding); // Thêm mục placeholder vào đầu danh sách
                    buildingItems.addAll(buildingsData);
                    building.setItems(buildingItems);

                    if (buildingsData.isEmpty()) {
                        // Nếu không có dữ liệu thật, vẫn giữ placeholder "Chọn Tòa nhà" làm mục duy nhất có thể chọn
                        building.getSelectionModel().select(placeholderBuilding);
                        // Hoặc có thể đặt prompt text lại nếu muốn
                        // building.setPromptText("Không có tòa nhà");
                    }

                    // Chọn tòa nhà hiện tại của facility
                    if (currentFacility != null && currentFacility.getBuildingName() != null) {
                        Optional<BuildingItem> currentBuildingOpt = buildingItems.stream()
                                // So sánh bằng ID nếu bạn lưu ID của building trong Facility model,
                                // hoặc bằng tên nếu bạn lưu tên. Ưu tiên ID để chính xác.
                                // Giả sử Facility model có getBuildingId() hoặc getBuildingName()
                                .filter(b -> b.getId() != null && b.getId().equals(currentFacility.getBuildingId())) // Giả sử có getBuildingId()
                                .findFirst();
                        if (currentBuildingOpt.isPresent()) {
                            building.setValue(currentBuildingOpt.get());
                        } else {
                            // Nếu không tìm thấy, chọn placeholder
                            building.getSelectionModel().select(placeholderBuilding);
                        }
                    } else {
                        // Nếu facility không có building nào, chọn placeholder
                        building.getSelectionModel().select(placeholderBuilding);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    building.setPlaceholder(new Label("Lỗi tải tòa nhà"));
                    building.setItems(FXCollections.observableArrayList(placeholderBuilding)); // Vẫn hiển thị placeholder
                    building.getSelectionModel().select(placeholderBuilding);
                    showErrorAlert("Lỗi Dữ Liệu", "Không thể tải danh sách tòa nhà: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void setupRoomTypeComboBox() {
        // Mục placeholder/hướng dẫn cho ComboBox Loại phòng
        final RoomTypeItem placeholderRoomType = new RoomTypeItem(null, "Chọn Loại phòng");

        typeRoom.setConverter(new StringConverter<RoomTypeItem>() {
            @Override public String toString(RoomTypeItem object) {
                return object == null ? null : object.getName();
            }
            @Override public RoomTypeItem fromString(String string) { return null; }
        });
        typeRoom.setPlaceholder(new Label("Tải loại phòng..."));
        new Thread(() -> {
            try {
                List<RoomTypeItem> roomTypesData = roomTypeClientService.getAllRoomTypes();
                Platform.runLater(() -> {
                    ObservableList<RoomTypeItem> roomTypeItems = FXCollections.observableArrayList();
                    roomTypeItems.add(placeholderRoomType); // Thêm mục placeholder
                    roomTypeItems.addAll(roomTypesData);
                    typeRoom.setItems(roomTypeItems);

                    if (roomTypesData.isEmpty()) {
                        typeRoom.getSelectionModel().select(placeholderRoomType);
                    }
                    // Chọn loại phòng hiện tại
                    if (currentFacility != null && currentFacility.getRoomTypeName() != null) {
                        Optional<RoomTypeItem> currentRoomTypeOpt = roomTypeItems.stream()
                                // Giả sử Facility model có getRoomTypeId()
                                .filter(rt -> rt.getId() != null && rt.getId().equals(currentFacility.getRoomTypeId()))
                                .findFirst();
                        if (currentRoomTypeOpt.isPresent()) {
                            typeRoom.setValue(currentRoomTypeOpt.get());
                        } else {
                            typeRoom.getSelectionModel().select(placeholderRoomType);
                        }
                    } else {
                        typeRoom.getSelectionModel().select(placeholderRoomType);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    typeRoom.setPlaceholder(new Label("Lỗi tải loại phòng"));
                    typeRoom.setItems(FXCollections.observableArrayList(placeholderRoomType));
                    typeRoom.getSelectionModel().select(placeholderRoomType);
                    showErrorAlert("Lỗi Dữ Liệu", "Không thể tải danh sách loại phòng: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void setupFacilityManagerComboBox() {
        // Mục placeholder/hướng dẫn cho ComboBox Người quản lý
        final UserItem placeholderManager = new UserItem(null, "Chọn Người quản lý (Tùy chọn)", null);

        facilityManager.setConverter(new StringConverter<UserItem>() {
            @Override public String toString(UserItem object) {
                return object == null ? null : object.getDisplayName();
            }
            @Override public UserItem fromString(String string) { return null; }
        });
        facilityManager.setPlaceholder(new Label("Tải người quản lý..."));
        new Thread(() -> {
            try {
                List<UserItem> managersData = userClientService.getAllFacilityManagers();
                Platform.runLater(() -> {
                    ObservableList<UserItem> managerItems = FXCollections.observableArrayList();
                    managerItems.add(placeholderManager); // Thêm mục placeholder
                    managerItems.addAll(managersData);
                    facilityManager.setItems(managerItems);

                    if (managersData.isEmpty()) {
                        facilityManager.getSelectionModel().select(placeholderManager);
                    }
                    // Chọn người quản lý hiện tại
                    if (currentFacility != null && currentFacility.getFacilityManagerId() != null) {
                        Optional<UserItem> currentManagerOpt = managerItems.stream()
                                .filter(user -> user.getId() != null && user.getId().equals(currentFacility.getFacilityManagerId()))
                                .findFirst();
                        if (currentManagerOpt.isPresent()) {
                            facilityManager.setValue(currentManagerOpt.get());
                        } else {
                            // Nếu ID người quản lý hiện tại không có trong danh sách (hoặc không hợp lệ)
                            facilityManager.getSelectionModel().select(placeholderManager);
                        }
                    } else {
                        // Nếu facility không có manager hoặc ID là null, chọn placeholder "Không chọn" / "Chọn..."
                        facilityManager.getSelectionModel().select(placeholderManager);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    facilityManager.setPlaceholder(new Label("Lỗi tải quản lý"));
                    facilityManager.setItems(FXCollections.observableArrayList(placeholderManager));
                    facilityManager.getSelectionModel().select(placeholderManager);
                    showErrorAlert("Lỗi Dữ Liệu", "Không thể tải danh sách người quản lý: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleSave() {
        if (currentFacility == null || currentFacility.getId() == null) {
            showErrorAlert("Lỗi", "Không có phòng để lưu hoặc thiếu ID phòng.");
            return;
        }
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            showErrorAlert("Lỗi Xác Thực", "Chưa đăng nhập hoặc token không hợp lệ.");
            return;
        }

        String nameText = name.getText().trim();
        String capacityText = capacity.getText().trim();

        if (nameText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Tên phòng không được để trống.");
            name.requestFocus(); return;
        }
        if (capacityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Sức chứa không được để trống.");
            capacity.requestFocus(); return;
        }

        int capacityValue;
        try {
            capacityValue = Integer.parseInt(capacityText);
            if (capacityValue < 1) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Sức chứa phải ít nhất là 1.");
                capacity.requestFocus(); return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Sức chứa phải là một số nguyên hợp lệ.");
            capacity.requestFocus(); return;
        }

        BuildingItem selectedBuilding = building.getSelectionModel().getSelectedItem();
        // Kiểm tra xem có phải là mục placeholder không (ID là null)
        if (selectedBuilding == null || selectedBuilding.getId() == null) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng chọn tòa nhà hợp lệ.");
            building.requestFocus(); return;
        }
        RoomTypeItem selectedRoomType = typeRoom.getSelectionModel().getSelectedItem();
        if (selectedRoomType == null || selectedRoomType.getId() == null) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng chọn loại phòng hợp lệ.");
            typeRoom.requestFocus(); return;
        }
        if (status.getValue() == null || status.getValue().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng chọn trạng thái phòng.");
            status.requestFocus(); return;
        }

        Map<String, Object> updateRequestData = new HashMap<>();
        updateRequestData.put("name", nameText);
        updateRequestData.put("description", description.getText().trim());
        updateRequestData.put("capacity", capacityValue);
        updateRequestData.put("location", locationTextField.getText().trim());
        updateRequestData.put("img", img.getText().trim());
        updateRequestData.put("status", status.getValue());
        updateRequestData.put("buildingId", selectedBuilding.getId());
        updateRequestData.put("roomTypeId", selectedRoomType.getId());

        UserItem selectedManager = facilityManager.getSelectionModel().getSelectedItem();
        // Chỉ gửi facilityManagerId nếu người dùng chọn một người quản lý hợp lệ (không phải mục placeholder)
        if (selectedManager != null && selectedManager.getId() != null) {
            updateRequestData.put("facilityManagerId", selectedManager.getId());
        } else {
            updateRequestData.put("facilityManagerId", null); // Gửi null nếu "Không chọn"
        }

        String requestBody = gson.toJson(updateRequestData);
        System.out.println("Sending JSON for PATCH update: " + requestBody);

        String apiUrl = Config.getOrDefault("BASE_URL", "http://localhost:8080/facility") + "/rooms/" + currentFacility.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        saveButton.setDisable(true);
        cancelButton.setDisable(true);

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("EditFacilityController: Update Response Status Code: " + response.statusCode());
                    return response;
                })
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            showInfoAlert("Thành công", "Thông tin phòng đã được cập nhật!");

                            if (currentFacility != null) {
                                currentFacility.setName(name.getText().trim());
                                currentFacility.setDescription(description.getText().trim());
                                currentFacility.setCapacity(capacityValue);
                                currentFacility.setLocation(locationTextField.getText().trim());
                                currentFacility.setImg(img.getText().trim());
                                currentFacility.setStatus(status.getValue());
                                if (selectedBuilding != null) currentFacility.setBuildingName(selectedBuilding.getName()); // Cập nhật tên
                                if (selectedRoomType != null) currentFacility.setRoomTypeName(selectedRoomType.getName()); // Cập nhật tên

                                // Cập nhật facilityManagerId và nameFacilityManager
                                if (selectedManager != null && selectedManager.getId() != null) {
                                    currentFacility.setNameFacilityManager(selectedManager.getDisplayName());
                                    currentFacility.setFacilityManagerId(selectedManager.getId());
                                } else {
                                    currentFacility.setNameFacilityManager(null); // Hoặc tên hiển thị của placeholder nếu muốn
                                    currentFacility.setFacilityManagerId(null);
                                }
                                // Cập nhật các trường khác nếu cần, ví dụ updatedAt từ response nếu server trả về
                            }
                            if (facilityList != null) {
                                int index = -1;
                                for(int i=0; i< facilityList.size(); i++){
                                    Facility item = facilityList.get(i);
                                    if(item != null && item.getId() != null && item.getId().equals(currentFacility.getId())){
                                        index = i;
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    facilityList.set(index, currentFacility); // Kích hoạt TableView update
                                }
                            }
                            closeDialog();
                        } else {
                            String errorMsg = "Cập nhật thất bại. Mã lỗi: " + response.statusCode();
                            String responseBodyString = response.body(); // Lấy body một lần
                            if (responseBodyString != null && !responseBodyString.isEmpty()) {
                                try {
                                    Map<String, Object> errorResponseMap = gson.fromJson(responseBodyString, new TypeToken<Map<String, Object>>(){}.getType());
                                    if (errorResponseMap != null && errorResponseMap.containsKey("message")) {
                                        errorMsg += "\nChi tiết: " + errorResponseMap.get("message");
                                    } else {
                                        errorMsg += "\nPhản hồi: " + responseBodyString;
                                    }
                                } catch (JsonSyntaxException parseEx) {
                                    errorMsg += "\nPhản hồi không thể đọc: " + responseBodyString;
                                }
                            }
                            showErrorAlert("Cập nhật Thất Bại", errorMsg);
                        }
                        saveButton.setDisable(false);
                        cancelButton.setDisable(false);
                    });
                })
                .exceptionally(e -> {
                    Throwable cause = (e instanceof java.util.concurrent.CompletionException) ? e.getCause() : e;
                    Platform.runLater(() -> {
                        showErrorAlert("Lỗi Yêu Cầu", "Lỗi khi gửi yêu cầu cập nhật: " + cause.getMessage());
                        saveButton.setDisable(false);
                        cancelButton.setDisable(false);
                    });
                    cause.printStackTrace();
                    return null;
                });
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

    private void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private void showInfoAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            TextArea textArea = new TextArea(message);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            alert.getDialogPane().setContent(textArea);
            alert.setResizable(true);
            Window owner = getWindow();
            if (owner != null) alert.initOwner(owner);
            alert.showAndWait();
        } else {
            Platform.runLater(() -> showAlert(alertType, title, message));
        }
    }

    private Window getWindow() {
        try {
            if (cancelButton != null && cancelButton.getScene() != null && cancelButton.getScene().getWindow() != null) {
                return cancelButton.getScene().getWindow();
            }
        } catch (Exception e) {
            System.err.println("Không thể xác định cửa sổ chủ cho Alert: " + e.getMessage());
        }
        return null;
    }

    private String getStringOrEmpty(String str) {
        return str != null ? str : "";
    }
}