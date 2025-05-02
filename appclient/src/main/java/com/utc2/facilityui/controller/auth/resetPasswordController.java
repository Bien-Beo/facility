package com.utc2.facilityui.controller.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class resetPasswordController {

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label oldPasswordEye;
    @FXML private Label newPasswordEye;
    @FXML private StackPane oldPasswordPane;
    @FXML private StackPane newPasswordPane;

    private boolean isOldPasswordVisible = false;
    private boolean isNewPasswordVisible = false;

    private TextField oldPasswordTextField;
    private TextField newPasswordTextField;

    @FXML
    private void initialize() {
        // Tạo TextField cho hiển thị password
        oldPasswordTextField = createTextField(oldPasswordField);
        newPasswordTextField = createTextField(newPasswordField);

        // Thêm TextField vào cùng StackPane với PasswordField
        oldPasswordPane.getChildren().add(0, oldPasswordTextField); // thêm vào dưới icon
        newPasswordPane.getChildren().add(0, newPasswordTextField);

        // Mặc định ẩn TextField (chỉ hiện PasswordField lúc đầu)
        oldPasswordTextField.setVisible(false);
        newPasswordTextField.setVisible(false);
    }

    private TextField createTextField(PasswordField passwordField) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(passwordField.textProperty());
        textField.setPromptText(passwordField.getPromptText());
        textField.getStyleClass().addAll(passwordField.getStyleClass());
        return textField;
    }

    @FXML
    private void toggleOldPasswordVisibility() {
        isOldPasswordVisible = !isOldPasswordVisible;
        oldPasswordField.setVisible(!isOldPasswordVisible);
        oldPasswordTextField.setVisible(isOldPasswordVisible);
    }

    @FXML
    private void toggleNewPasswordVisibility() {
        isNewPasswordVisible = !isNewPasswordVisible;
        newPasswordField.setVisible(!isNewPasswordVisible);
        newPasswordTextField.setVisible(isNewPasswordVisible);
    }

    @FXML
    private void handleChangePassword() {
        String oldPassword = oldPasswordField.isVisible() ? oldPasswordField.getText() : oldPasswordTextField.getText();
        String newPassword = newPasswordField.isVisible() ? newPasswordField.getText() : newPasswordTextField.getText();

        if (oldPassword == null || newPassword == null || newPassword.length() < 5) {
            System.out.println("Password is invalid!");
            return;
        }

        System.out.println("Old Password: " + oldPassword);
        System.out.println("New Password: " + newPassword);
    }
}
