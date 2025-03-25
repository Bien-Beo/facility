package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.model.Room;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class RoomService {
    private final OkHttpClient client;
    private final Gson gson;

    public RoomService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public List<Room> getRooms() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:8080/api/rooms")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String jsonData = response.body().string();
            Type listType = new TypeToken<List<Room>>() {}.getType();
            return gson.fromJson(jsonData, listType);
        }
    }
}
