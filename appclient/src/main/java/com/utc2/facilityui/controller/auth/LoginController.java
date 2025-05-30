package com.utc2.facilityui.controller.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.utils.TokenUtils;
import com.utc2.facilityui.model.User;         // << THÊM IMPORT User
import com.utc2.facilityui.service.UserServices; // << THÊM IMPORT UserServices

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
// Bỏ import Image, ImageView nếu không dùng trong LoginController này
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import okhttp3.*;
// Bỏ import Duration, PauseTransition nếu không dùng trong LoginController này
// import javafx.util.Duration;
// import javafx.animation.PauseTransition;


import java.io.IOException;
import java.util.Optional;
// Bỏ import Random nếu không dùng trong LoginController này
// import java.util.Random;

public class LoginController { // Giả sử tên lớp là LoginController theo lỗi
    @FXML private TextField username;
    @FXML private PasswordField password; // Đổi tên passwordField thành password để khớp FXML (nếu FXML dùng password)
    @FXML private Button loginButton;
    @FXML private Label lbMessage;
    // Nếu LoginController này không có Captcha, hãy xóa các FXML field liên quan đến Captcha
    // @FXML private TextField captchaInput;
    // @FXML private Label captchaLabel;


    private static final String LOGIN_URL = "http://localhost:8080/facility/auth/token"; // Kiểm tra lại URL API
    private final OkHttpClient client = new OkHttpClient();
    // private String generatedCaptcha; // Xóa nếu không dùng Captcha

    private static final String ADMIN_DASHBOARD_FXML = "/com/utc2/facilityui/view/mainscreen.fxml";
    private static final String USER_DASHBOARD_FXML = "/com/utc2/facilityui/view/home.fxml";

    @FXML
    private void initialize() {
        // generateCaptcha(); // Xóa nếu không dùng Captcha
        // loginButton.setOnAction(e -> handleLogin()); // Không cần thiết nếu FXML đã gán onAction
    }

    @FXML
    private void handleLogin() {
        String usernameInput = this.username.getText();
        String passwordInput = this.password.getText(); // Sử dụng password nếu fx:id là password

        // String captcha = this.captchaInput.getText(); // Xóa nếu không dùng Captcha

        if (usernameInput.isEmpty() || passwordInput.isEmpty() /*|| captcha.isEmpty()*/) { // Bỏ captcha nếu không dùng
            lbMessage.setText("Vui lòng nhập tài khoản và mật khẩu!");
            // generateCaptcha();
            return;
        }

        // if (!captcha.equals(generatedCaptcha)) { // Bỏ captcha nếu không dùng
        //     lbMessage.setText("Mã an toàn không chính xác!");
        //     generateCaptcha();
        //     captchaInput.clear();
        //     return;
        // }

        lbMessage.setText("Đang đăng nhập...");
        loginButton.setDisable(true);
        authenticateUser(usernameInput, passwordInput);
    }

    private void authenticateUser(String authUsername, String authPassword) {
        String json = "{\"username\":\"" + authUsername + "\", \"password\":\"" + authPassword + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("LoginController: Lỗi kết nối: " + e.getMessage());
                Platform.runLater(() -> {
                    lbMessage.setText("Lỗi kết nối server!");
                    loginButton.setDisable(false);
                    // generateCaptcha(); // Xóa nếu không dùng Captcha
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = null;
                try {
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    }

                    if (response.isSuccessful() && responseBody != null && !responseBody.isEmpty()) {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                        if (jsonObject.has("result") && jsonObject.getAsJsonObject("result").has("token")) {
                            String token = jsonObject.getAsJsonObject("result").get("token").getAsString();
                            System.out.println("LoginController: Token received: " + token);

                            // Bước 1: (Tùy chọn) Lưu token tạm thời nếu UserServices.getMyInfo() cần
                            // TokenStorage.setOnlyToken(token);

                            // Bước 2: Gọi UserServices.getMyInfo() để lấy thông tin User chi tiết
                            // UserServices.getMyInfo() nên sử dụng token (từ TokenStorage.getToken() hoặc truyền vào)
                            try {
                                User userDetails = UserServices.getMyInfo(); // Giả định đã cập nhật getMyInfo trả về User

                                if (userDetails != null && userDetails.getUserId() != null) {
                                    // Bước 3: Đăng nhập thành công, lưu cả token và User vào TokenStorage
                                    TokenStorage.login(token, userDetails);
                                    System.out.println("LoginController: User details fetched. Session populated for " + userDetails.getUsername());

                                    // Bước 4: Trích xuất scope và chuyển màn hình
                                    Optional<String> scopeOpt = TokenUtils.extractScope(token);
                                    String fxmlPath;
                                    String windowTitle;

                                    if (scopeOpt.isPresent()) {
                                        String scope = scopeOpt.get();
                                        System.out.println("LoginController: User scope: " + scope);
                                        if ("ROLE_ADMIN".equals(scope) || "ROLE_FACILITY_MANAGER".equals(scope)) {
                                            fxmlPath = ADMIN_DASHBOARD_FXML;
                                            windowTitle = "Admin/Manager Dashboard";
                                        } else {
                                            fxmlPath = USER_DASHBOARD_FXML;
                                            windowTitle = "User Dashboard";
                                        }
                                    } else {
                                        System.err.println("LoginController: Không thể trích xuất scope từ token.");
                                        Platform.runLater(() -> {
                                            lbMessage.setText("Lỗi token không hợp lệ (scope)!");
                                            loginButton.setDisable(false);
                                            // generateCaptcha(); // Xóa nếu không dùng
                                            TokenStorage.logout(); // Xóa session
                                        });
                                        return;
                                    }

                                    final String finalFxmlPath = fxmlPath;
                                    final String finalWindowTitle = windowTitle;
                                    Platform.runLater(() -> switchToDashboard(finalFxmlPath, finalWindowTitle));
                                    return; // Kết thúc thành công

                                } else { // userDetails null hoặc không có userId
                                    System.err.println("LoginController: Không lấy được thông tin người dùng hợp lệ sau khi login.");
                                    Platform.runLater(() -> {
                                        lbMessage.setText("Lỗi lấy thông tin người dùng!");
                                        loginButton.setDisable(false);
                                        // generateCaptcha(); // Xóa
                                        TokenStorage.logout(); // Xóa session
                                    });
                                }
                            } catch (IOException serviceEx) { // Lỗi từ UserServices.getMyInfo()
                                System.err.println("LoginController: Lỗi khi gọi UserServices.getMyInfo(): " + serviceEx.getMessage());
                                serviceEx.printStackTrace();
                                Platform.runLater(() -> {
                                    lbMessage.setText("Lỗi dịch vụ người dùng: " + serviceEx.getMessage());
                                    loginButton.setDisable(false);
                                    // generateCaptcha(); // Xóa
                                    TokenStorage.logout(); // Xóa session
                                });
                            }
                        } else { // JSON không có token
                            System.err.println("LoginController: JSON response không chứa token hoặc cấu trúc không đúng.");
                            Platform.runLater(() -> {
                                lbMessage.setText("Dữ liệu đăng nhập không hợp lệ!");
                                loginButton.setDisable(false);
                                // generateCaptcha(); // Xóa
                            });
                        }
                    } else { // response không thành công (ví dụ 401)
                        String errorMsg = "Sai tài khoản hoặc mật khẩu!";
                        if (response.code() != 401 && responseBody != null && !responseBody.isEmpty()) {
                            // Thử parse lỗi từ body nếu server trả về JSON lỗi
                            try {
                                JsonObject errorJson = JsonParser.parseString(responseBody).getAsJsonObject();
                                if (errorJson.has("message") && !errorJson.get("message").isJsonNull()) {
                                    errorMsg = errorJson.get("message").getAsString();
                                } else {
                                    errorMsg = "Lỗi từ server (Code: " + response.code() + ")";
                                }
                            } catch (Exception e) {
                                System.err.println("LoginController: Không parse được body lỗi: " + responseBody);
                                errorMsg = "Lỗi không xác định từ server (Code: " + response.code() + ")";
                            }
                        } else if (response.code() != 401) {
                            errorMsg = "Lỗi từ server (Code: " + response.code() + ")";
                        }
                        System.err.println("LoginController: Login failed. HTTP Code: " + response.code() + ". Message: " + errorMsg);
                        final String finalErrorMsg = errorMsg;
                        Platform.runLater(() -> {
                            lbMessage.setText(finalErrorMsg);
                            loginButton.setDisable(false);
                            // generateCaptcha(); // Xóa
                        });
                    }
                } catch (JsonParseException | IllegalStateException | IOException e) { // Lỗi parse JSON chung hoặc IO
                    System.err.println("LoginController: Lỗi xử lý response: " + e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        lbMessage.setText("Lỗi xử lý dữ liệu trả về!");
                        loginButton.setDisable(false);
                        // generateCaptcha(); // Xóa
                    });
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
        });
    }

    private void switchToDashboard(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                System.err.println("LoginController: Không thể tìm thấy file FXML: " + fxmlPath);
                Platform.runLater(() -> lbMessage.setText("Lỗi cấu hình: Thiếu file giao diện người dùng."));
                loginButton.setDisable(false);
                return;
            }
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                lbMessage.setText("Lỗi nghiêm trọng khi chuyển màn hình!");
                loginButton.setDisable(false);
            });
        }
    }
}