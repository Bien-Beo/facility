package com.utc2.facilityui.controller.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.utils.TokenUtils;
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
import java.util.Optional;

public class LoginController {
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Label lbMessage;
//
    private static final String LOGIN_URL = "http://localhost:8080/facility/auth/token";
    private final OkHttpClient client = new OkHttpClient();

    // Giả sử đường dẫn đến FXML của admin/manager
    private static final String ADMIN_DASHBOARD_FXML = "/com/utc2/facilityui/view/manageFacility.fxml";
    private static final String USER_DASHBOARD_FXML = "/com/utc2/facilityui/view/home.fxml"; // FXML cho user thường

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
        lbMessage.setText("Đang đăng nhập..."); // Thông báo chờ
        loginButton.setDisable(true); // Vô hiệu hóa nút khi đang xử lý
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
                Platform.runLater(() -> {
                    lbMessage.setText("Lỗi kết nối server!");
                    loginButton.setDisable(false); // Kích hoạt lại nút
                });
                e.printStackTrace(); // In lỗi ra console để debug
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string(); // Đọc body chỉ một lần
                    System.out.println(responseBody);
                    try {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        System.out.println(jsonObject);

                        if (jsonObject.has("result") && jsonObject.getAsJsonObject("result").has("token")) {
                            String token = jsonObject.getAsJsonObject("result").get("token").getAsString();
                            TokenStorage.setToken(token); // Lưu token
                            System.out.println("Token received: " + token);

                            // Trích xuất scope từ token
                            Optional<String> scopeOpt = TokenUtils.extractScope(token);
                            System.out.println(scopeOpt);
                            String fxmlPath;
                            String windowTitle;

                            if (scopeOpt.isPresent()) {
                                String scope = scopeOpt.get();
                                System.out.println("User scope: " + scope);
                                // Kiểm tra scope
                                if ("ROLE_ADMIN".equals(scope) || "ROLE_FACILITY_MANAGER".equals(scope)) {
                                    // Nếu là Admin hoặc Facility Manager
                                    fxmlPath = ADMIN_DASHBOARD_FXML;
                                    windowTitle = "Admin/Manager Dashboard";
                                } else {
                                    // Mặc định là user thường
                                    fxmlPath = USER_DASHBOARD_FXML;
                                    windowTitle = "User Dashboard";
                                }
                            } else {
                                // Lỗi: Không thể trích xuất scope, quay lại đăng nhập hoặc hiển thị lỗi
                                System.err.println("Không thể trích xuất scope từ token.");
                                Platform.runLater(() -> {
                                    lbMessage.setText("Lỗi token không hợp lệ!");
                                    loginButton.setDisable(false);
                                });
                                return; // Dừng xử lý
                            }

                            // Chuyển màn hình trên UI thread
                            final String finalFxmlPath = fxmlPath;
                            final String finalWindowTitle = windowTitle;
                            Platform.runLater(() -> switchToDashboard(finalFxmlPath, finalWindowTitle));
                            return; // Kết thúc thành công
                        }
                    } catch (JsonParseException | IllegalStateException e) {
                        // Lỗi phân tích JSON hoặc cấu trúc JSON không đúng
                        System.err.println("Lỗi phân tích response JSON từ server: " + e.getMessage());
                        Platform.runLater(() -> {
                            lbMessage.setText("Lỗi dữ liệu trả về từ server!");
                            loginButton.setDisable(false);
                        });
                        return;
                    }
                }

                // Xử lý lỗi: Sai tài khoản/mật khẩu hoặc lỗi server khác
                String errorMsg = "Sai tài khoản hoặc mật khẩu!";
                // Có thể đọc message lỗi từ response nếu API trả về chi tiết
                // Ví dụ: if (response.code() == 401) { errorMsg = ... }
                if (!response.isSuccessful()) {
                    System.err.println("Login failed with code: " + response.code());
                    // response.body().string(); // Đọc body lỗi nếu cần (nhưng chỉ đọc 1 lần)
                }

                final String finalErrorMsg = errorMsg;
                Platform.runLater(() -> {
                    lbMessage.setText(finalErrorMsg);
                    loginButton.setDisable(false); // Kích hoạt lại nút
                });
                response.close(); // Đóng response khi không thành công
            }
        });
    }

    // Sửa đổi phương thức này để nhận đường dẫn FXML và tiêu đề cửa sổ
    private void switchToDashboard(String fxmlPath, String title) {
        try {
            System.out.println("Switching to dashboard: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("Không thể tìm thấy file FXML: " + fxmlPath);
            }
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title); // Đặt tiêu đề cửa sổ
            stage.centerOnScreen(); // Căn giữa màn hình
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lbMessage.setText("Lỗi chuyển màn hình!");
            loginButton.setDisable(false); // Kích hoạt lại nút nếu lỗi
        }
    }
}