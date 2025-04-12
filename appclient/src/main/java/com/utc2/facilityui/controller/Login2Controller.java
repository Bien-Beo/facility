
package com.utc2.facilityui.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.*;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.IOException;
import java.util.Random;

public class Login2Controller {

    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Label lbMessage;
    @FXML private TextField captchaInput;
    @FXML private Label captchaLabel;

    private static final String LOGIN_URL = "http://localhost:8080/facility/auth/token";
    private final OkHttpClient client = new OkHttpClient();
    private String generatedCaptcha;

    @FXML
    public void initialize() {
        generateCaptcha();
        loginButton.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = this.username.getText();
        String password = this.password.getText();
        String captcha = this.captchaInput.getText();

        if (username.isEmpty() || password.isEmpty() || captcha.isEmpty()) {
            lbMessage.setText("Vui lòng nhập tài khoản, mật khẩu và mã an toàn!");
            return;
        }

        if (!captcha.equals(generatedCaptcha)) {
            lbMessage.setText("Mã an toàn không chính xác!");
            return;
        }

        authenticateUser(username, password);
    }

    private void authenticateUser(String username, String password) {
        String json = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> lbMessage.setText("Lỗi kết nối server!"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                    if (jsonObject.has("result") && jsonObject.getAsJsonObject("result").has("token")) {
                        String token = jsonObject.getAsJsonObject("result").get("token").getAsString();
                        TokenStorage.setToken(token);
                        System.out.println("Token: " + token);

                        Platform.runLater(() -> switchToDashboard());
                        return;
                    }
                }
                Platform.runLater(() -> lbMessage.setText("Sai tài khoản hoặc mật khẩu!"));
            }
        });
    }

    private void switchToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/home.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lbMessage.setText("Lỗi chuyển màn hình!");
        }
    }

    private void generateCaptcha() {
        generatedCaptcha = generateRandomCode(5);
        captchaLabel.setText(generatedCaptcha);
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return code.toString();
    }

    @FXML
    private void refreshCaptcha() {
        generateCaptcha();
        captchaLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> captchaLabel.setStyle("-fx-text-fill: darkgreen; -fx-font-weight: bold;"));
        pause.play();
    }
}
