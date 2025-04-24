package com.utc2.facilityui.controller.equipment;

import com.utc2.facilityui.controller.BaseReportController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class ReportEquipmentController extends BaseReportController {
    @Override
    protected void handleAdd() {
        // Xử lý thêm report cho equipment
        String reportDescription = description.getText();
        String equipmentName = name.getText();
        String userId = userID.getText();
        // TODO: Thêm logic xử lý report equipment
    }

    @FXML
    public void initialize() {
        // Thiết lập giá trị ban đầu
        description.setText("Description");
//
        // Thêm sự kiện click cho các TextField
        description.setOnMouseClicked(this::handleTextAreaClick);
        // Xử lý sự kiện cho nút Cancel
        bntCancel.setOnAction(e -> closeDialog());

        // Xử lý sự kiện cho nút Add
        bntAdd.setOnAction(e -> {
            handleAdd();
            addDiaLog();
        });
    }
    private void handleTextAreaClick(MouseEvent event) {
        TextArea textArea = (TextArea) event.getSource();
        if (textArea.getText().equals("Description")) {
            textArea.setText("");
        }

        // Thêm sự kiện focus out
        textArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && textArea.getText().isEmpty()) {  // Khi mất focus và text rỗng
                textArea.setText("Description");
            }
        });
    }
}
