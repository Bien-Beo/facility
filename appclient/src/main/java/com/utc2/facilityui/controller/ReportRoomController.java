package com.utc2.facilityui.controller;

import javafx.fxml.FXML;

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
        // Xử lý sự kiện cho nút Cancel
        bntCancel.setOnAction(e -> closeDialog());
        
        // Xử lý sự kiện cho nút Add
        bntAdd.setOnAction(e -> {
            handleAdd();
            addDiaLog();
        });
    }
}
