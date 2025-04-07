package com.utc2.facilityui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Random;

public class Login2Controller {

    @FXML
    private TextField username;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField captchaInput;

    @FXML
    private Label captchaLabel;

    @FXML
    private Label lbMessage;

    @FXML
    private Button loginButton;

    private String generatedCaptcha;

    @FXML
    public void initialize() {
        generateCaptcha();

        loginButton.setOnAction(e -> handleLogin());
    }

    private void generateCaptcha() {
        generatedCaptcha = generateRandomCode(5);
        captchaLabel.setText(generatedCaptcha);
    }

    @FXML
    private void refreshCaptcha() {
        generateCaptcha();
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return code.toString();
    }

    private void handleLogin() {
        String userInput = captchaInput.getText();
        if (!generatedCaptcha.equalsIgnoreCase(userInput)) {
            lbMessage.setText("Incorrect captcha!");
            generateCaptcha(); // regenerate after failed attempt
            return;
        }

        // TODO: Validate username/password here
        lbMessage.setText("Captcha correct! Now checking credentials...");
    }
}
