package com.utc2.facilityui.controller.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;

public class LoginController {
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Label lbMessage;

    private static final String LOGIN_URL = "http://localhost:8080/facility/auth/token";
    private final OkHttpClient client = new OkHttpClient();

    @FXML
    private void initialize() {}

    @FXML
    private void handleLogin() {
        String username = this.username.getText();
        String password = this.password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lbMessage.setText("Vui lòng nhập tài khoản và mật khẩu!");
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
}