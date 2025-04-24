package com.utc2.facilityui.service;

import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.helper.Config;
import okhttp3.*;

import java.io.IOException;
///
public class UserServices {
    private static final String BASE_URL = Config.get("BASE_URL"); // Lấy từ config.properties
    private static final OkHttpClient client = new OkHttpClient();

    public static String getMyInfo() throws IOException {
        String token = TokenStorage.getToken(); // Lấy token từ TokenStorage

        if (token == null || token.isEmpty()) {
            throw new IOException("Token is missing. Please log in first.");
        }

        Request request = new Request.Builder()
                .url(BASE_URL + "/users/myInfo")
                .addHeader("Authorization", "Bearer " + token) // Truyền token vào header
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Đọc response body một lần rồi trả về
            String responseData = response.body().string();
            return responseData;
        }
    }


}
