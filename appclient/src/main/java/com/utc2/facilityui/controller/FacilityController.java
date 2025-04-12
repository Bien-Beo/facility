package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.OperationsTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.utc2.facilityui.model.Facility;
import com.utc2.facilityui.model.OperationsTableCellFactory; // Import Factory
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class FacilityController implements OperationsTableCell.OperationsEventHandler<Facility> {

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
    private TableColumn<Facility, Void> operationsColumn; // Thêm dòng này
    @FXML
    private Button exportButton;

    private ObservableList<Facility> facilities;

    @FXML
    private void initialize() {
        facilities = FXCollections.observableArrayList(
                new Facility(
                        "304DN/ " +
                                "DN",
                        "Phòng học số 4" +
                                " Tầng 3 Giảng đường Đa năng",
                        "Available",
                        "09:02 PM\nWed Dec 20 2023",
                        "09:07 PM\nWed Dec 20 2023",
                        "N/A",
                        "Trần Ngọc Biên"
                ),
                new Facility(
                        "304DN/ " +
                                "DN",
                        "Phòng học số 4" +
                                " Tầng 3 Giảng đường Đa năng",
                        "Available",
                        "09:02 PM\nWed Dec 20 2023",
                        "09:07 PM\nWed Dec 20 2023",
                        "N/A",
                        "Trần Ngọc Biên"
                )
        );

        // Mapping các cột trong TableView
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());
        createdAtColumn.setCellValueFactory(cell -> cell.getValue().createdAtProperty());
        updatedAtColumn.setCellValueFactory(cell -> cell.getValue().updatedAtProperty());
        deletedAtColumn.setCellValueFactory(cell -> cell.getValue().deletedAtProperty());
        managerColumn.setCellValueFactory(cell -> cell.getValue().managerNameProperty());

        facilityTable.setItems(facilities);

        // Áp dụng Cell Factory cho cột Operations
        operationsColumn.setCellFactory(new OperationsTableCellFactory<>(this));
        descriptionColumn.setCellFactory(tc -> {
            TableCell<Facility, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(TableCell.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionColumn.widthProperty().subtract(10)); // Điều chỉnh khoảng đệm nếu cần
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    // Implement các phương thức từ OperationsTableCell.OperationsEventHandler
    public void onEdit(Facility facility) {
        System.out.println("Controller: Edit action for facility with Name: " + facility.getName());
        // Logic để mở form chỉnh sửa hoặc thực hiện hành động chỉnh sửa
        try {
            // 1. Load FXML cho giao diện chỉnh sửa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/EditFacilityDialog.fxml"));
            Stage editStage = new Stage();
            editStage.setScene(new Scene(loader.load()));
            editStage.setTitle("Edit Facility");
            editStage.initModality(Modality.APPLICATION_MODAL); // Ngăn chặn tương tác với cửa sổ chính

            // 2. Lấy Controller của giao diện chỉnh sửa và truyền dữ liệu
            EditFacilityController editController = loader.getController();
            editController.setFacility(facility);
            editController.setFacilityList(facilities); // Truyền cả list để có thể cập nhật

            // 3. Hiển thị dialog chỉnh sửa
            editStage.showAndWait();

            // Sau khi dialog đóng, TableView sẽ tự động cập nhật nếu các thuộc tính trong Facility là Property
            // Hoặc bạn có thể làm mới TableView nếu cần
            // facilityTable.refresh();

        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi khi tải FXML
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load edit dialog");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void onDelete(Facility facility) {
        System.out.println("Controller: Delete action for facility with ID: " + facility.getName());
        // Logic để hiển thị dialog xác nhận xóa và thực hiện xóa
        facilities.remove(facility); // Ví dụ: Xóa khỏi ObservableList
    }
}