package com.utc2.facilityui.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.utils.TokenUtils; // Import lớp tiện ích
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;
import java.util.Optional; // Import Optional

public class Main extends Application {
    private static final String VERIFY_TOKEN_URL = "http://localhost:8080/facility/auth/introspect";
    private final OkHttpClient client = new OkHttpClient();

    // Giả sử đường dẫn đến FXML của admin/manager và user
    private static final String ADMIN_DASHBOARD_FXML = "/com/utc2/facilityui/view/menuAdmin.fxml";
    private static final String USER_DASHBOARD_FXML = "/com/utc2/facilityui/view/home.fxml";
    private static final String LOGIN_FXML = "/com/utc2/facilityui/view/login2.fxml";

    @Override
    public void start(Stage stage) throws IOException {
        checkTokenAndRedirect(stage);
    }

    private void checkTokenAndRedirect(Stage stage) {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            System.out.println("No token found, loading login screen.");
            loadScene(stage, LOGIN_FXML, "Đăng nhập");
            return;
        }

        System.out.println("Token found, verifying...");
        // Gửi token lên server để kiểm tra
        String json = "{\"token\":\"" + token + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(VERIFY_TOKEN_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Error connecting to verify token endpoint: " + e.getMessage());
                Platform.runLater(() -> loadScene(stage, LOGIN_FXML, "Đăng nhập"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String fxmlPath;
                String windowTitle;

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string(); // Đọc body 1 lần
                    System.out.println(responseBody);
                    try {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        System.out.println(jsonObject);
                        boolean isActive = jsonObject.has("active") && jsonObject.get("active").getAsBoolean();

                        if (isActive) {
                            System.out.println("Token is active. Checking scope...");
                            // Token hợp lệ, giờ kiểm tra scope
                            String currentToken = TokenStorage.getToken(); // Lấy lại token (phòng trường hợp bị thay đổi)
                            Optional<String> scopeOpt = TokenUtils.extractScope(currentToken);

                            if (scopeOpt.isPresent()) {
                                String scope = scopeOpt.get();
                                System.out.println("User scope (from Main): " + scope);
                                if ("ROLE_ADMIN".equals(scope) || "ROLE_FACILITY_MANAGER".equals(scope)) {
                                    fxmlPath = ADMIN_DASHBOARD_FXML;
                                    windowTitle = "Admin/Manager Dashboard";
                                } else {
                                    fxmlPath = USER_DASHBOARD_FXML;
                                    windowTitle = "User";
                                }
                            } else {
                                // Token hợp lệ nhưng không đọc được scope? -> Lỗi, về login
                                System.err.println("Token is active but scope could not be extracted. Clearing token.");
                                TokenStorage.clearToken(); // Xóa token lỗi
                                fxmlPath = LOGIN_FXML;
                                windowTitle = "Đăng nhập";
                            }
                        } else {
                            // Token không còn hợp lệ (đã hết hạn, bị thu hồi, ...)
                            System.out.println("Token is inactive. Clearing token and loading login screen.");
                            TokenStorage.clearToken(); // Xóa token không hợp lệ
                            fxmlPath = LOGIN_FXML;
                            windowTitle = "Đăng nhập";
                        }
                    } catch (JsonParseException | IllegalStateException e) {
                        System.err.println("Lỗi phân tích JSON response từ /introspect: " + e.getMessage());
                        TokenStorage.clearToken(); // Xóa token nếu response lỗi
                        fxmlPath = LOGIN_FXML;
                        windowTitle = "Đăng nhập";
                    }
                } else {
                    // Lỗi từ server khi gọi /introspect (vd: 401, 500)
                    System.err.println("Token verification failed with code: " + response.code());
                    TokenStorage.clearToken(); // Xóa token nếu không xác thực được
                    fxmlPath = LOGIN_FXML;
                    windowTitle = "Đăng nhập";
                }
                response.close(); // Luôn đóng response

                // Tải scene trên UI thread
                final String finalFxmlPath = fxmlPath;
                final String finalWindowTitle = windowTitle;
                Platform.runLater(() -> loadScene(stage, finalFxmlPath, finalWindowTitle));
            }
        });
    }

    // Sửa đổi phương thức này để nhận tiêu đề cửa sổ
    private void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            System.out.println("Loading scene: " + fxmlPath);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (fxmlLoader.getLocation() == null) {
                throw new IOException("Không thể tìm thấy file FXML: " + fxmlPath);
            }
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setTitle(title); // Đặt tiêu đề cửa sổ
            stage.centerOnScreen(); // Căn giữa màn hình
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Có thể hiển thị Alert lỗi ở đây nếu cần
            System.err.println("Không thể tải FXML: " + fxmlPath + " - " + e.getMessage());
            // Nếu lỗi nghiêm trọng, có thể đóng ứng dụng hoặc hiển thị màn hình lỗi cơ bản
            // Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}