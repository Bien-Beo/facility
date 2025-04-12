package com.utc2.facilityui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

import com.utc2.facilityui.model.Facility;

public class EditFacilityController {
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Facility currentFacility;
    private ObservableList<Facility> facilityList;

    public void setFacility(Facility facility) {
        this.currentFacility = facility;
        // Điền các trường bằng dữ liệu hiện tại
        nameTextField.setText(facility.getName());
        descriptionTextField.setText(facility.getDescription());
        statusComboBox.getItems().addAll("Available", "In Use");
        statusComboBox.setValue(facility.getStatus());
        // Điền các trường khác
    }

    public void setFacilityList(ObservableList<Facility> facilityList) {
        this.facilityList = facilityList;
    }

    @FXML
    private void handleSave() {
        if (currentFacility != null) {
            // Lấy dữ liệu đã chỉnh sửa từ các trường
            currentFacility.setName(nameTextField.getText());
            currentFacility.setDescription(descriptionTextField.getText());
            currentFacility.setStatus(statusComboBox.getValue());
            // Cập nhật các thuộc tính khác

            // Đóng dialog
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        // Đóng dialog mà không lưu
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}