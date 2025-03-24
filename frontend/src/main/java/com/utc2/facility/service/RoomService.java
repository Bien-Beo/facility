package com.utc2.facility.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.utc2.facility.controller.TokenStorage;
import com.utc2.facility.model.ModelCard;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class RoomService {
    private static final String API_URL = "http://localhost:8080/facility/rooms";
    private final OkHttpClient client;
    private final Gson gson;

    public RoomService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public List<ModelCard> fetchRooms() throws IOException {
        String token = TokenStorage.getToken(); // Lấy token từ TokenStorage
        if (token == null || token.isEmpty()) {
            throw new IOException("Token không hợp lệ !");
        }

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Lỗi API: " + response.code());

            String jsonResponse = response.body().string();

            // Chuyển JSON về đối tượng
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            Type listType = new TypeToken<List<ModelCard>>() {}.getType();

            // Lấy danh sách phòng từ "result"
            return gson.fromJson(jsonObject.get("result"), listType);
        }
    }
}
