package com.utc2.facilityui.controller.booking;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class AddBookingController {
    @FXML
    private Text Status;

    @FXML
    private Button bntAddBooking;

    @FXML
    private Button bntCancel;

    @FXML
    private TextField borrowDate;

    @FXML
    private TextField expectedDateReturn;

    @FXML
    private TextField expectedTimeReturn;

    @FXML
    private Label name;

    @FXML
    private TextArea reason;

    @FXML
    private TextField timeBorrow;

    @FXML
    public void initialize() {
        // Thiết lập giá trị ban đầu
        borrowDate.setText("Borrow date");
        expectedDateReturn.setText("Return date");
        timeBorrow.setText("Time borrow");
        expectedTimeReturn.setText("Time return");
        reason.setText("Reason");

        // Thêm sự kiện click cho các TextField
        borrowDate.setOnMouseClicked(this::handleTextFieldClick);
        expectedDateReturn.setOnMouseClicked(this::handleTextFieldClick);
        timeBorrow.setOnMouseClicked(this::handleTextFieldClick);
        expectedTimeReturn.setOnMouseClicked(this::handleTextFieldClick);
        reason.setOnMouseClicked(this::handleTextAreaClick);
    }

    private void handleTextFieldClick(MouseEvent event) {
        TextField textField = (TextField) event.getSource();
        String defaultText = "";
        
        switch(textField.getId()) {
            case "borrowDate":
                defaultText = "Borrow date";
                break;
            case "expectedDateReturn":
                defaultText = "Return date";
                break;
            case "timeBorrow":
                defaultText = "Time borrow";
                break;
            case "expectedTimeReturn":
                defaultText = "Time return";
                break;
        }

        if (textField.getText().equals(defaultText)) {
            textField.setText("");
        }

        // Thêm sự kiện focus out
        String finalDefaultText = defaultText;
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && textField.getText().isEmpty()) {  // Khi mất focus và text rỗng
                textField.setText(finalDefaultText);
            }
        });
    }

    private void handleTextAreaClick(MouseEvent event) {
        TextArea textArea = (TextArea) event.getSource();
        if (textArea.getText().equals("Reason")) {
            textArea.setText("");
        }

        // Thêm sự kiện focus out
        textArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && textArea.getText().isEmpty()) {  // Khi mất focus và text rỗng
                textArea.setText("Reason");
            }
        });
    }

    public Button getBntCancel() {
        return bntCancel;
    }

    public Button getBntAddBooking() {
        return bntAddBooking;
    }

    public Label getName() {
        return name;
    }
}
