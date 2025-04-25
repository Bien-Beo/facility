package com.utc2.facilityui.controller; // Hoặc package đúng của bạn

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.utils.TokenUtils; // <<< Import lớp tiện ích
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
import java.util.Optional; // <<< Import Optional
import java.util.Random;

public class Login2Controller {

    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Label lbMessage;
    @FXML private TextField captchaInput;
    @FXML private Label captchaLabel;
//
    private static final String LOGIN_URL = "http://localhost:8080/facility/auth/token";
    private final OkHttpClient client = new OkHttpClient();
    private String generatedCaptcha;

    // --- THÊM CÁC HẰNG SỐ FXML ---
    private static final String ADMIN_DASHBOARD_FXML = "/com/utc2/facilityui/view/menuAdmin.fxml"; // Đường dẫn admin view
    private static final String USER_DASHBOARD_FXML = "/com/utc2/facilityui/view/home.fxml"; // Đường dẫn user view
    // -----------------------------

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
            generateCaptcha(); // Tạo captcha mới khi nhập thiếu
            return;
        }

        if (!captcha.equals(generatedCaptcha)) {
            lbMessage.setText("Mã an toàn không chính xác!");
            generateCaptcha(); // Tạo captcha mới khi nhập sai
            captchaInput.clear(); // Xóa ô nhập captcha
            return;
        }

        // Thông báo chờ và vô hiệu hóa nút
        lbMessage.setText("Đang đăng nhập...");
        loginButton.setDisable(true);
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
                System.err.println("<<< Login2Controller: onFailure called! Lỗi kết nối: " + e.getMessage());
                Platform.runLater(() -> {
                    lbMessage.setText("Lỗi kết nối server!");
                    loginButton.setDisable(false); // Kích hoạt lại nút
                    generateCaptcha(); // Tạo captcha mới
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("<<< Login2Controller: onResponse called! Code: " + response.code());
                String responseBody = null;
                try{
                    if (response.body() != null) {
                        responseBody = response.body().string();
                        System.out.println("<<< Login2Controller: Raw Response Body: " + responseBody);
                    } else {
                        System.err.println("<<< Login2Controller: Response body is null!");
                    }

                    // Chỉ xử lý nếu thành công và body không null
                    if (response.isSuccessful() && responseBody != null) {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        System.out.println("<<< Login2Controller: Parsed JSON: " + jsonObject.toString());

                        if (jsonObject.has("result") && jsonObject.getAsJsonObject("result").has("token")) {
                            String token = jsonObject.getAsJsonObject("result").get("token").getAsString();
                            TokenStorage.setToken(token);
                            System.out.println("<<< Login2Controller: Token received: " + token);

                            // --- THÊM LOGIC XỬ LÝ SCOPE ---
                            Optional<String> scopeOpt = TokenUtils.extractScope(token);
                            String fxmlPath;
                            String windowTitle;

                            if (scopeOpt.isPresent()) {
                                String scope = scopeOpt.get();
                                System.out.println("<<< Login2Controller: Extracted scope: [" + scope + "]");

                                // Kiểm tra scope
                                if ("ROLE_ADMIN".equals(scope) || "ROLE_FACILITY_MANAGER".equals(scope)) {
                                    // Nếu là Admin hoặc Facility Manager
                                    fxmlPath = ADMIN_DASHBOARD_FXML;
                                    windowTitle = "Admin/Manager Dashboard";
                                    System.out.println("<<< Login2Controller: Choosing ADMIN view: " + fxmlPath);
                                } else {
                                    // Mặc định là user thường
                                    fxmlPath = USER_DASHBOARD_FXML;
                                    windowTitle = "User Dashboard";
                                    System.out.println("<<< Login2Controller: Choosing USER view: " + fxmlPath);
                                }
                            } else {
                                // Lỗi: Không thể trích xuất scope
                                System.err.println("<<< Login2Controller: Failed to extract scope from token.");
                                Platform.runLater(() -> {
                                    lbMessage.setText("Lỗi token không hợp lệ (scope)!");
                                    loginButton.setDisable(false); // Kích hoạt lại nút
                                    generateCaptcha(); // Tạo captcha mới
                                });
                                return; // Dừng xử lý, không chuyển màn hình
                            }

                            // Chuyển màn hình trên UI thread với đường dẫn và tiêu đề đúng
                            final String finalFxmlPath = fxmlPath;
                            final String finalWindowTitle = windowTitle;
                            Platform.runLater(() -> switchToDashboard(finalFxmlPath, finalWindowTitle)); // <<< Gọi hàm đã sửa đổi
                            return; // Kết thúc thành công
                            // --- KẾT THÚC LOGIC XỬ LÝ SCOPE ---

                        } else {
                            System.err.println("<<< Login2Controller: JSON structure incorrect. 'result' or 'token' missing.");
                            Platform.runLater(() -> {
                                lbMessage.setText("Dữ liệu đăng nhập không hợp lệ!"); // Thông báo chung chung hơn
                                loginButton.setDisable(false);
                                generateCaptcha();
                            });
                        }

                    } else { // Nếu response không thành công
                        System.err.println("<<< Login2Controller: Response was not successful (Code: " + response.code() + ")");
                        String errorMsg = "Sai tài khoản hoặc mật khẩu!"; // Mặc định
                        if(response.code() == 401) {
                            errorMsg = "Sai tài khoản hoặc mật khẩu!";
                        } else {
                            errorMsg = "Lỗi hệ thống (Code: " + response.code() + ")";
                        }
                        final String finalErrorMsg = errorMsg;
                        Platform.runLater(() -> {
                            lbMessage.setText(finalErrorMsg);
                            loginButton.setDisable(false); // Kích hoạt lại nút
                            generateCaptcha(); // Tạo captcha mới
                        });
                    }
                } catch (JsonParseException | IllegalStateException | IOException e) {
                    System.err.println("<<< Login2Controller: Exception during response processing: " + e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        lbMessage.setText("Lỗi xử lý dữ liệu trả về!");
                        loginButton.setDisable(false);
                        generateCaptcha();
                    });
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            } // Kết thúc onResponse
        });
    }

    // --- SỬA ĐỔI PHƯƠNG THỨC NÀY ---
    // private void switchToDashboard() { // <<< Xóa hoặc sửa đổi
    private void switchToDashboard(String fxmlPath, String title) { // <<< Nhận tham số
        try {
            System.out.println("<<< Login2Controller: Switching to dashboard: " + fxmlPath);
            // Sử dụng fxmlPath và title được truyền vào
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("Không thể tìm thấy file FXML: " + fxmlPath);
            }
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) username.getScene().getWindow(); // Lấy stage hiện tại
            stage.setScene(scene);
            stage.setTitle(title); // Đặt tiêu đề cửa sổ
            stage.centerOnScreen(); // Căn giữa màn hình (tùy chọn)
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Hiển thị lỗi trên lbMessage nếu có thể, hoặc dùng Alert
            Platform.runLater(() -> {
                lbMessage.setText("Lỗi chuyển màn hình!");
                loginButton.setDisable(false); // Cho phép thử lại
                generateCaptcha();
            });
        }
    }

    private void generateCaptcha() {
        generatedCaptcha = generateRandomCode(5); // Độ dài mã captcha
        captchaLabel.setText(generatedCaptcha);
        captchaLabel.setStyle("-fx-text-fill: darkgreen; -fx-font-weight: bold;"); // Reset style
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789"; // Bỏ O, 0 để tránh nhầm lẫn
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
        // Hiệu ứng ngắn khi refresh
        captchaLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> captchaLabel.setStyle("-fx-text-fill: darkgreen; -fx-font-weight: bold;"));
        pause.play();
    }
}