package com.utc2.facility.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class LoginController {
    private static final String LOGIN_URL = "http://localhost:8080/facility/auth/token";
    private final OkHttpClient client = new OkHttpClient();

    public String login(String username, String password) {
        String json = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                String token = jsonObject.getAsJsonObject("result").get("token").getAsString();

                TokenStorage.setToken(token);

                return token;
            } else {
                System.out.println("Login failed: " + response.message());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
