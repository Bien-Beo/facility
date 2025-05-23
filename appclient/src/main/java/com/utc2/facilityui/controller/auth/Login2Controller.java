package com.utc2.facilityui.controller.auth; // Hoặc package đúng của bạn

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.utils.TokenUtils;
import com.utc2.facilityui.model.User; // << THÊM IMPORT CHO MODEL USER
import com.utc2.facilityui.service.UserServices; // << THÊM IMPORT CHO USER SERVICES

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import okhttp3.*;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class Login2Controller {

    @FXML private TextField username;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePassword;
    @FXML private ImageView togglePasswordIcon;
    @FXML private Button loginButton;
    @FXML private Label lbMessage;
    @FXML private TextField captchaInput;
    @FXML private Label captchaLabel;

    private static final String LOGIN_URL = "http://localhost:8080/facility/auth/token";
    private final OkHttpClient client = new OkHttpClient();
    private String generatedCaptcha;
    private boolean passwordVisible = false;

    private static final String ADMIN_DASHBOARD_FXML = "/com/utc2/facilityui/view/mainscreen.fxml";
    private static final String USER_DASHBOARD_FXML = "/com/utc2/facilityui/view/home.fxml";


    @FXML
    public void initialize() {
        generateCaptcha();
        loginButton.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String usernameInput = this.username.getText(); // Đổi tên biến để tránh trùng
        String passwordInput = this.passwordField.getText(); // Đổi tên biến
        String captcha = this.captchaInput.getText();

        if (usernameInput.isEmpty() || passwordInput.isEmpty() || captcha.isEmpty()) {
            lbMessage.setText("Vui lòng nhập tài khoản, mật khẩu và mã an toàn!");
            generateCaptcha();
            return;
        }

        if (!captcha.equals(generatedCaptcha)) {
            lbMessage.setText("Mã an toàn không chính xác!");
            generateCaptcha();
            captchaInput.clear();
            return;
        }

        lbMessage.setText("Đang đăng nhập...");
        loginButton.setDisable(true);
        authenticateUser(usernameInput, passwordInput);
    }

    private void authenticateUser(String usernameAuth, String passwordAuth) { // Đổi tên tham số
        String json = "{\"username\":\"" + usernameAuth + "\", \"password\":\"" + passwordAuth + "\"}";
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
                    loginButton.setDisable(false);
                    generateCaptcha();
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
                        // Nếu body null và response không thành công, xử lý lỗi ngay
                        if (!response.isSuccessful()) {
                            Platform.runLater(() -> {
                                lbMessage.setText("Lỗi từ server (Code: " + response.code() + ")");
                                loginButton.setDisable(false);
                                generateCaptcha();
                            });
                            return; // Dừng xử lý
                        }
                    }

                    if (response.isSuccessful() && responseBody != null && !responseBody.isEmpty()) {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        System.out.println("<<< Login2Controller: Parsed JSON: " + jsonObject.toString());

                        if (jsonObject.has("result") && jsonObject.getAsJsonObject("result").has("token")) {
                            String token = jsonObject.getAsJsonObject("result").get("token").getAsString();

                            // QUAN TRỌNG: Lưu token tạm thời để UserServices.getMyInfo() có thể sử dụng
                            // (Nếu UserServices.getMyInfo() của bạn lấy token từ TokenStorage)
                            // Sử dụng setOnlyToken để không xóa user details nếu có từ lần trước (dù ít khả năng)
                            TokenStorage.setOnlyToken(token);
                            System.out.println("<<< Login2Controller: Token received and temporarily set: " + token);

                            // BƯỚC TIẾP THEO: LẤY THÔNG TIN USER CHI TIẾT
                            try {
                                User userDetails = UserServices.getMyInfo(); // Giả định getMyInfo() trả về User object

                                if (userDetails != null && userDetails.getUserId() != null) {
                                    // Đăng nhập hoàn tất, LƯU CẢ TOKEN VÀ USERDETAILS VÀO TOKENSTORAGE
                                    TokenStorage.login(token, userDetails);
                                    System.out.println("<<< Login2Controller: User details fetched and session populated for " + userDetails.getUsername());

                                    // Logic xử lý scope và chuyển màn hình như cũ
                                    Optional<String> scopeOpt = TokenUtils.extractScope(token);
                                    String fxmlPath;
                                    String windowTitle;

                                    if (scopeOpt.isPresent()) {
                                        String scope = scopeOpt.get();
                                        System.out.println("<<< Login2Controller: Extracted scope: [" + scope + "]");
                                        if ("ROLE_ADMIN".equals(scope) || "ROLE_FACILITY_MANAGER".equals(scope)) {
                                            fxmlPath = ADMIN_DASHBOARD_FXML;
                                            windowTitle = "Admin/Manager Dashboard";
                                        } else {
                                            fxmlPath = USER_DASHBOARD_FXML;
                                            windowTitle = "User Dashboard";
                                        }
                                        System.out.println("<<< Login2Controller: Choosing view: " + fxmlPath);
                                    } else {
                                        System.err.println("<<< Login2Controller: Failed to extract scope from token.");
                                        Platform.runLater(() -> {
                                            lbMessage.setText("Lỗi token không hợp lệ (scope)!");
                                            loginButton.setDisable(false);
                                            generateCaptcha();
                                            TokenStorage.logout(); // Xóa session nếu có lỗi
                                        });
                                        return;
                                    }

                                    final String finalFxmlPath = fxmlPath;
                                    final String finalWindowTitle = windowTitle;
                                    Platform.runLater(() -> switchToDashboard(finalFxmlPath, finalWindowTitle));
                                    return; // Kết thúc thành công

                                } else { // Không lấy được userDetails hoặc userId null
                                    System.err.println("<<< Login2Controller: Failed to fetch valid user details after login.");
                                    Platform.runLater(() -> {
                                        lbMessage.setText("Lỗi lấy thông tin người dùng!");
                                        loginButton.setDisable(false);
                                        generateCaptcha();
                                        TokenStorage.logout(); // Xóa session
                                    });
                                    return;
                                }

                            } catch (IOException serviceException) { // Lỗi khi gọi UserServices.getMyInfo()
                                System.err.println("<<< Login2Controller: IOException while fetching user details: " + serviceException.getMessage());
                                serviceException.printStackTrace();
                                Platform.runLater(() -> {
                                    lbMessage.setText("Lỗi dịch vụ người dùng!");
                                    loginButton.setDisable(false);
                                    generateCaptcha();
                                    TokenStorage.logout(); // Xóa session
                                });
                                return;
                            }
                            // --- KẾT THÚC PHẦN LẤY USER DETAILS ---

                        } else { // JSON không đúng cấu trúc mong đợi
                            System.err.println("<<< Login2Controller: JSON structure incorrect. 'result' or 'token' missing.");
                            Platform.runLater(() -> {
                                lbMessage.setText("Dữ liệu đăng nhập không hợp lệ!");
                                loginButton.setDisable(false);
                                generateCaptcha();
                            });
                        }

                    } else { // Response không thành công (ví dụ: 401, 500) hoặc body rỗng
                        System.err.println("<<< Login2Controller: Response not successful or empty body. Code: " + response.code());
                        String errorMsg = "Lỗi hệ thống (Code: " + response.code() + ")"; // Mặc định
                        if(response.code() == 401) { // Unauthorized
                            errorMsg = "Sai tài khoản hoặc mật khẩu!";
                        } else if (responseBody != null && !responseBody.isEmpty()) {
                            // Thử parse lỗi từ body nếu có thể (giả sử body lỗi cũng là JSON ApiSingleResponse)
                            try {
                                JsonObject errorJson = JsonParser.parseString(responseBody).getAsJsonObject();
                                if (errorJson.has("message") && !errorJson.get("message").isJsonNull()) {
                                    errorMsg = errorJson.get("message").getAsString();
                                } else if (errorJson.has("error") && !errorJson.get("error").isJsonNull()){
                                    errorMsg = errorJson.get("error").getAsString();
                                }
                            } catch (Exception parseError) {
                                System.err.println("<<< Login2Controller: Could not parse error response body: " + parseError.getMessage());
                            }
                        }
                        final String finalErrorMsg = errorMsg;
                        Platform.runLater(() -> {
                            lbMessage.setText(finalErrorMsg);
                            loginButton.setDisable(false);
                            generateCaptcha();
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
                        response.close(); // Luôn đóng response
                    }
                }
            }
        });
    }

    private void switchToDashboard(String fxmlPath, String title) {
        try {
            System.out.println("<<< Login2Controller: Switching to dashboard: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) { // Kiểm tra xem resource có được tìm thấy không
                System.err.println("<<< Login2Controller: Cannot find FXML file at path: " + fxmlPath);
                Platform.runLater(() -> {
                    lbMessage.setText("Lỗi cấu hình: Không tìm thấy giao diện người dùng.");
                    loginButton.setDisable(false);
                    generateCaptcha();
                });
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
                generateCaptcha();
            });
        }
    }

    // ... (Các phương thức togglePasswordVisibility, generateCaptcha, refreshCaptcha giữ nguyên) ...
    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            visiblePassword.setText(passwordField.getText());
            visiblePassword.positionCaret(visiblePassword.getText().length());
            visiblePassword.setVisible(true);
            visiblePassword.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
            try (var in = getClass().getResourceAsStream("/com/utc2/facilityui/images/hide.png")) {
                if (in != null) togglePasswordIcon.setImage(new Image(in));
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            passwordField.setText(visiblePassword.getText());
            passwordField.positionCaret(passwordField.getText().length());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            visiblePassword.setVisible(false);
            visiblePassword.setManaged(false);
            try (var in = getClass().getResourceAsStream("/com/utc2/facilityui/images/visible.png")) {
                if (in != null) togglePasswordIcon.setImage(new Image(in));
            } catch (Exception e) { e.printStackTrace(); }
        }
    }


    private void generateCaptcha() {
        generatedCaptcha = generateRandomCode(5);
        captchaLabel.setText(generatedCaptcha);
        captchaLabel.setStyle("-fx-text-fill: darkgreen; -fx-font-weight: bold;");
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
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