package com.utc2.facilityui.chatbot;

import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeminiAPIClient {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final String API_KEY = "AIzaSyBsE7EBe9sQKSF7S0TIxIwYAyvSS0QTTlQ";

    private final OkHttpClient client;
    private final Gson gson;

    public GeminiAPIClient() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public String askGemini(String userInput) throws IOException {
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", userInput);

        JsonArray requestParts = new JsonArray(); // Đổi tên biến để tránh trùng
        requestParts.add(part);

        JsonObject message = new JsonObject();
        message.add("parts", requestParts);  // Thêm "requestParts" vào "message"

        contents.add(message);
        requestBody.add("contents", contents);

        String jsonBody = gson.toJson(requestBody);

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + API_KEY)
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Lỗi API: " + response.body().string()); // In lỗi chi tiết
                return "Lỗi khi gọi API: " + response.message();
            }

            String responseBody = response.body().string();
            System.out.println("Phản hồi API: " + responseBody); // In phản hồi API

            // Xử lý JSON response
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");

            if (candidates != null && candidates.size() > 0) {
                JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
                JsonArray responseParts = content.getAsJsonArray("parts");  // Đổi tên ở đây

                if (responseParts != null && responseParts.size() > 0) {
                    String responseText = responseParts.get(0).getAsJsonObject().get("text").getAsString();
                    return responseText;
                }
            }
            return "Không có phản hồi từ chatbot.";
        }
    }
}
