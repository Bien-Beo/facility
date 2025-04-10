package com.utc2.facilityui.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;

public class Main extends Application {
    private static final String VERIFY_TOKEN_URL = "http://localhost:8080/facility/auth/introspect";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void start(Stage stage) throws IOException {
        checkTokenAndRedirect(stage);
    }

    private void checkTokenAndRedirect(Stage stage) {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            loadScene(stage, "/com/utc2/facilityui/view/login2.fxml");
            return;
        }

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
                Platform.runLater(() -> loadScene(stage, "/com/utc2/facilityui/view/login2.fxml"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                    boolean isActive = jsonObject.has("active") && jsonObject.get("active").getAsBoolean();

                    Platform.runLater(() -> {
                        if (isActive) {
                            loadScene(stage, "/com/utc2/facilityui/view/home.fxml");
                        } else {
                            loadScene(stage, "/com/utc2/facilityui/view/login.fxml");
                        }
                    });
                } else {
                    Platform.runLater(() -> loadScene(stage, "/com/utc2/facilityui/view/login2.fxml"));
                }
            }
        });
    }

    private void loadScene(Stage stage, String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setTitle("Facility Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
