package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.Facility; // Model Facility đã cập nhật
import com.utc2.facilityui.model.OperationsTableCell;
import com.utc2.facilityui.model.OperationsTableCellFactory;
import com.utc2.facilityui.service.RoomService; // Service chứa hàm gọi API
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FacilityController implements Initializable, OperationsTableCell.OperationsEventHandler<Facility> {

    @FXML private TableView<Facility> facilityTable;
    @FXML private TableColumn<Facility, String> nameColumn;
    @FXML private TableColumn<Facility, String> descriptionColumn;
    @FXML private TableColumn<Facility, String> statusColumn;
    @FXML private TableColumn<Facility, String> createdAtColumn;
    @FXML private TableColumn<Facility, String> updatedAtColumn;
    @FXML private TableColumn<Facility, String> deletedAtColumn;
    @FXML private TableColumn<Facility, String> managerColumn; // fx:id="managerColumn" trong FXML
    @FXML private TableColumn<Facility, Void> operationsColumn;
    // @FXML private Button exportButton; // Giữ lại nếu có

    // Sử dụng ObservableList để TableView tự cập nhật khi list thay đổi
    private ObservableList<Facility> facilityDataList = FXCollections.observableArrayList();
    private RoomService roomService; // Service để gọi API

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.roomService = new RoomService(); // Khởi tạo service
        setupTableColumns(); // Gọi hàm cài đặt cột
        facilityTable.setItems(facilityDataList); // Gán list vào TableView
        loadFacilitiesData(); // Gọi hàm tải dữ liệu từ API
    }

    // Hàm cài đặt cell value factories cho các cột
    private void setupTableColumns() {
        // Đảm bảo tên property ("name", "description",...) khớp với tên Property trong Facility.java
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        createdAtColumn.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
        updatedAtColumn.setCellValueFactory(cellData -> cellData.getValue().updatedAtProperty());
        deletedAtColumn.setCellValueFactory(cellData -> cellData.getValue().deletedAtProperty());
        // Cột manager cần khớp với property trong Facility model
        managerColumn.setCellValueFactory(cellData -> cellData.getValue().nameFacilityManagerProperty());

        // Cell factory cho cột description để wrap text (giữ nguyên)
        descriptionColumn.setCellFactory(tc -> {
            TableCell<Facility, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE); // Dùng Control.USE_COMPUTED_SIZE
            text.wrappingWidthProperty().bind(descriptionColumn.widthProperty().subtract(15)); // Điều chỉnh padding
            text.textProperty().bind(cell.itemProperty());
            text.getStyleClass().add("table-cell-text"); // Thêm style class nếu muốn định dạng CSS
            return cell;
        });

        // Cell factory cho cột operations (giữ nguyên, cần OperationsTableCellFactory)
        // Đảm bảo bạn có lớp OperationsTableCellFactory và OperationsTableCell
        operationsColumn.setCellFactory(new OperationsTableCellFactory<>(this));

        // Đặt Placeholder cho bảng khi không có dữ liệu
        facilityTable.setPlaceholder(new Label("Đang tải dữ liệu..."));
    }

    // Hàm tải dữ liệu từ Service
    private void loadFacilitiesData() {
        facilityTable.setPlaceholder(new Label("Đang tải dữ liệu...")); // Hiển thị loading
        facilityDataList.clear(); // Xóa dữ liệu cũ

        new Thread(() -> {
            try {
                // Gọi phương thức service mới để lấy danh sách đã làm phẳng
                final ObservableList<Facility> fetchedFacilities = roomService.getDashboardFacilities();

                // Cập nhật UI trên luồng JavaFX
                Platform.runLater(() -> {
                    facilityDataList.setAll(fetchedFacilities); // Cập nhật ObservableList, TableView tự refresh
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
            } catch (Exception e) { // Bắt các lỗi khác nếu có
                Platform.runLater(() -> {
                    showError("Lỗi không xác định khi tải dữ liệu: " + e.getMessage());
                    facilityTable.setPlaceholder(new Label("Lỗi không xác định."));
                });
                System.err.println("Unexpected error loading facilities: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // Hàm hiển thị lỗi đơn giản
    private void showError(String message) {
        Platform.runLater(()-> { // Đảm bảo chạy trên luồng UI
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Đã xảy ra lỗi");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // --- Implement các phương thức xử lý sự kiện Edit/Delete ---
    @Override
    public void onEdit(Facility facility) {
        if (facility == null) return;
        System.out.println("Controller: Edit action for facility with Name: " + facility.getName());
        try {
            // TODO: Tạo file EditFacilityDialog.fxml và EditFacilityController.java
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/EditFacilityDialog.fxml"));
            Stage editStage = new Stage();
            // Cần tạo Scene với Parent trả về từ loader.load()
            editStage.setScene(new Scene(loader.load())); // Giả sử FXML trả về Parent (vd: AnchorPane, VBox)
            editStage.setTitle("Sửa thông tin: " + facility.getName());
            editStage.initModality(Modality.APPLICATION_MODAL);

            // TODO: Tạo lớp EditFacilityController
            // EditFacilityController editController = loader.getController();
            // if (editController != null) {
            //     editController.setFacilityToEdit(facility);
            //     editController.setParentController(this); // Để gọi lại refresh nếu cần
            // } else {
            //     throw new IOException("Could not get EditFacilityController.");
            // }

            editStage.showAndWait(); // Hiển thị và chờ dialog đóng

            // Load lại dữ liệu sau khi dialog đóng để cập nhật thay đổi
            loadFacilitiesData();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở cửa sổ chỉnh sửa: " + e.getMessage());
        } catch (Exception e) { // Bắt lỗi chung khác
            e.printStackTrace();
            showError("Lỗi không xác định khi mở cửa sổ chỉnh sửa: " + e.getMessage());
        }
    }

    @Override
    public void onDelete(Facility facility) {
        if (facility == null) return;
        System.out.println("Controller: Delete action for facility with Name: " + facility.getName());

        // Hiển thị dialog xác nhận
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa cơ sở '" + facility.getName() + "' không?");
        confirmAlert.setContentText("Hành động này sẽ đánh dấu là đã xóa (nếu backend hỗ trợ soft delete) hoặc xóa vĩnh viễn.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Gọi API xóa (cần thêm phương thức vào RoomService)
                new Thread(() -> {
                    try {
                        // TODO: Thêm phương thức deleteRoomById(String id) vào RoomService
                        // roomService.deleteRoomById(facility.getId());
                        System.out.println("TODO: Calling API to delete facility with ID: " + facility.getId());
                        Thread.sleep(1000); // Giả lập thời gian gọi API

                        // Nếu API thành công, cập nhật lại bảng trên luồng UI
                        Platform.runLater(() -> {
                            // Cách 1: Xóa trực tiếp khỏi list đang hiển thị
                            facilityDataList.remove(facility);
                            // Cách 2: Load lại toàn bộ dữ liệu (đảm bảo đồng bộ)
                            // loadFacilitiesData();
                        });
                    } catch (/*IOException e*/ Exception e) { // Bắt lỗi cụ thể từ service
                        Platform.runLater(() -> showError("Xóa thất bại: " + e.getMessage()));
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }
}