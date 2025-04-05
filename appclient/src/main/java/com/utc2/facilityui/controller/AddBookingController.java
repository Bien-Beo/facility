package com.utc2.facilityui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddBookingController {
    @FXML
    private Label Status;

    @FXML
    private Button bntAddBooking;

    @FXML
    private Button bntCancel;

    @FXML
    private TextField borrowDate;

    @FXML
    private TextField expectedReturnDate;

    @FXML
    private TextField expectedTimeReturn;

    @FXML
    private Label name;

    @FXML
    private TextArea reason;

    @FXML
    private TextField timeBorrow;

    @FXML
    private Label userID;
    // Thêm getter cho nút Cancel
    public Button getBntCancel() {
        return bntCancel;
    }
    public Button getBntAddBooking() {
        return bntAddBooking;
    }
    @FXML
    public void initialize() {
        // Có thể thêm các khởi tạo khác ở đây
    }
    public Label getName(){
        return name;
    }
}
