package com.utc2.facilityui.controller;

import com.utc2.facilityui.controller.room.addRoomController; // Corrected package!
import com.utc2.facilityui.model.Facility; // Your Facility model
import com.utc2.facilityui.model.OperationsTableCell;
import com.utc2.facilityui.model.OperationsTableCellFactory;
import com.utc2.facilityui.service.RoomService; // Your RoomService (if you have one)
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class FacilityController implements Initializable, OperationsTableCell.OperationsEventHandler<Facility> {

    @FXML
    private TableView<Facility> facilityTable;

    @FXML
    private TableColumn<Facility, String> nameColumn;

    @FXML
    private TableColumn<Facility, String> descriptionColumn;

    @FXML
    private TableColumn<Facility, String> statusColumn;

    @FXML
    private TableColumn<Facility, String> createdAtColumn;

    @FXML
    private TableColumn<Facility, String> updatedAtColumn;

    @FXML
    private TableColumn<Facility, String> deletedAtColumn;

    @FXML
    private TableColumn<Facility, String> managerColumn;

    @FXML
    private TableColumn<Facility, Void> operationsColumn;

    private ObservableList<Facility> facilityDataList = FXCollections.observableArrayList();
    private RoomService roomService; // Assuming you have a RoomService

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("FacilityController initialize() called!"); // Debugging

        this.roomService = new RoomService(); // Initialize your service
        setupTableColumns();
        facilityTable.setItems(facilityDataList);
        loadFacilitiesData();
    }

    private void setupTableColumns() {
        System.out.println("setupTableColumns() called!"); // Debugging

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Simple string properties
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        deletedAtColumn.setCellValueFactory(new PropertyValueFactory<>("deletedAt"));
        managerColumn.setCellValueFactory(new PropertyValueFactory<>("manager"));

        // *** Cell factory for wrapping text in description column ***
        descriptionColumn.setCellFactory(tc -> {
            TableCell<Facility, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionColumn.widthProperty().subtract(15));
            text.textProperty().bind(cell.itemProperty());
            text.getStyleClass().add("table-cell-text"); // Optional CSS class
            return cell;
        });

        operationsColumn.setCellFactory(new OperationsTableCellFactory<>(this)); // Custom cell factory
        facilityTable.setPlaceholder(new Label("Đang tải dữ liệu...")); // Placeholder text
    }

    private void loadFacilitiesData() {
        System.out.println("loadFacilitiesData() called!"); // Debugging
        facilityTable.setPlaceholder(new Label("Đang tải dữ liệu..."));
        facilityDataList.clear();

        new Thread(() -> {
            try {
                // *** Example: Fetching data from your service (replace with your actual call) ***
                final ObservableList<Facility> fetchedFacilities = roomService.getDashboardFacilities();
                // final ObservableList<Facility> fetchedFacilities = FXCollections.observableArrayList(createDummyFacilities()); // For testing

                Platform.runLater(() -> {
                    facilityDataList.setAll(fetchedFacilities);
                    if (fetchedFacilities.isEmpty()) {
                        facilityTable.setPlaceholder(new Label("Không có dữ liệu cơ sở vật chất nào."));
                    }
                    System.out.println("Facility Table updated with " + fetchedFacilities.size() + " items.");
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Không thể tải dữ liệu cơ sở: " + e.getMessage());
                    facilityTable.setPlaceholder(new Label("Lỗi tải dữ liệu. Vui lòng thử lại."));
                });
                System.err.println("Error loading facilities: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Lỗi không xác định khi tải dữ liệu: " + e.getMessage());
                    facilityTable.setPlaceholder(new Label("Lỗi không xác định."));
                });
                System.err.println("Unexpected error loading facilities: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void showError(String message) {
        System.out.println("showError() called!"); // Debugging
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Đã xảy ra lỗi");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void onEdit(Facility facility) {
        System.out.println("onEdit() called!"); // Debugging
        if (facility == null) return;
        System.out.println("Controller: Edit action for facility with Name: " + facility.getName());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/EditFacilityDialog.fxml"));
            Stage editStage = new Stage();
            editStage.setScene(new Scene(loader.load()));
            editStage.setTitle("Sửa thông tin: " + facility.getName());
            editStage.initModality(Modality.APPLICATION_MODAL);

            // TODO: Create EditFacilityController and pass data if needed
            // EditFacilityController editController = loader.getController();
            // if (editController != null) {
            //     editController.setFacilityToEdit(facility);
            //     editController.setParentController(this);
            // }

            editStage.showAndWait();
            loadFacilitiesData();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở cửa sổ chỉnh sửa: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi không xác định khi mở cửa sổ chỉnh sửa: " + e.getMessage());
        }
    }

    @Override
    public void onDelete(Facility facility) {
        System.out.println("onDelete() called!"); // Debugging
        if (facility == null) return;
        System.out.println("Controller: Delete action for facility with Name: " + facility.getName());

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa cơ sở '" + facility.getName() + "' không?");
        confirmAlert.setContentText("Hành động này sẽ đánh dấu là đã xóa (nếu backend hỗ trợ soft delete) hoặc xóa vĩnh viễn.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        // TODO: Implement deleteRoomById in RoomService
                        // roomService.deleteRoomById(facility.getId());
                        System.out.println("TODO: Calling API to delete facility with ID: " + facility.getId());
                        Thread.sleep(1000);

                        Platform.runLater(() -> {
                            facilityDataList.remove(facility);
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> showError("Xóa thất bại: " + e.getMessage()));
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }

    @FXML
    void handleAddFacility(ActionEvent event) {
        System.out.println("handleAddFacility() called!"); // Debugging
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/addRoom.fxml"));
            Parent addRoomRoot = loader.load();

            addRoomController addRoomController = loader.getController();
            // You can pass data or set up the controller here if needed

            Stage addRoomStage = new Stage();
            addRoomStage.setTitle("Thêm Phòng Mới");
            addRoomStage.setScene(new Scene(addRoomRoot));
            addRoomStage.initModality(Modality.APPLICATION_MODAL);
            addRoomStage.showAndWait();

            loadFacilitiesData();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở cửa sổ thêm phòng: " + e.getMessage());
        }
    }


}