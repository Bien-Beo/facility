package com.utc2.facilityui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.util.Random;

public class Login2Controller {

    // Đã khai báo các trường FXML với @FXML
    @FXML
    private TextField username; // TextField cho username

    @FXML
    private PasswordField password; // PasswordField cho password

    @FXML
    private final String TEST_USERNAME = "testuser";

    @FXML
    private final String TEST_PASSWORD = "123456";

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

        // Gán sự kiện bấm nút Login
        loginButton.setOnAction(e -> handleLogin());
    }

    // Hàm xử lý khi bấm nút đăng nhập
    public void handleLogin() {
        String inputUsername = username.getText();
        String inputPassword = password.getText();
        String inputCaptcha = captchaInput.getText(); // Get the captcha input from the user

        // Check if username or password or captcha is empty
        if (inputUsername == null || inputPassword == null || inputCaptcha == null || inputCaptcha.isEmpty()) {
            lbMessage.setText("Username, password or mã an toàn không được để trống!");
            return;
        }

        // Check if the captcha is correct
        if (!inputCaptcha.equals(generatedCaptcha)) {
            lbMessage.setText("❌ Incorrect captcha!");
            System.out.println("❌ Incorrect captcha!");
            return;
        }

        // Check if the username and password are correct
        if (inputUsername.equals(TEST_USERNAME) && inputPassword.equals(TEST_PASSWORD)) {
            lbMessage.setText("✅ Login successful!");
            System.out.println("✅ Login successful!");
            // Transition to the main screen or show success message
        } else {
            lbMessage.setText("❌ Sai tên đăng nhập hoặc mật khẩu!");
            System.out.println("❌ Sai tên đăng nhập hoặc mật khẩu!");
            // Show warning or error message
        }
    }


    // Hàm tạo mã captcha ngẫu nhiên
    private void generateCaptcha() {
        generatedCaptcha = generateRandomCode(5);
        captchaLabel.setText(generatedCaptcha);
    }

    // Sinh mã từ ký tự chữ và số
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return code.toString();
    }

    // Gọi khi click vào captcha hoặc biểu tượng làm mới
    @FXML
    private void refreshCaptcha() {
        generateCaptcha();

        // hiệu ứng đổi màu nhẹ
        captchaLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> captchaLabel.setStyle("-fx-text-fill: darkgreen; -fx-font-weight: bold;"));
        pause.play();
    }
}
