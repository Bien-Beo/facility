package com.utc2.facilityui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class ReportRoomController extends BaseReportController {
    @Override
    protected void handleAdd() {
        // Xử lý thêm report cho room
        String reportDescription = description.getText();
        String roomName = name.getText();
        String userId = userID.getText();
        // TODO: Thêm logic xử lý report room
    }

    @FXML
    public void initialize() {
        description.setText("Description");

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
