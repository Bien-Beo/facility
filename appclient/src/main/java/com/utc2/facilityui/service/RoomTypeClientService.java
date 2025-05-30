package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.helper.Config;
import com.utc2.facilityui.model.RoomTypeItem;
import com.utc2.facilityui.response.ApiResponse; // Sử dụng ApiResponse<List<T>>

import com.utc2.facilityui.response.ApiSingleResponse;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RoomTypeClientService {
    private static final String BASE_URL = Config.getOrDefault("BASE_URL", "http://localhost:8080/api");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new GsonBuilder().create();

    public List<RoomTypeItem> getAllRoomTypes() throws IOException {
        String url = (BASE_URL.startsWith("http") ? BASE_URL : "http://" + BASE_URL);
        if (BASE_URL.contains("/facility")){
            url = Config.getOrDefault("BASE_URL", "http://localhost:8080/facility") + "/roomtypes";
        } else {
            url += "/roomtypes";
        }

        Request request = buildAuthenticatedGetRequest(url);
        System.out.println("Fetching room types from: " + url);
        String jsonData = null;

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            try {
                if (responseBody == null) throw new IOException("Response body rỗng khi lấy loại phòng.");
                jsonData = responseBody.string();
            } finally {
                if (responseBody != null) responseBody.close();
            }

            if (!response.isSuccessful()) {
                throw new IOException("Lấy danh sách loại phòng thất bại. Mã lỗi: " + response.code() + ". Phản hồi: " + jsonData);
            }

            // === THAY ĐỔI CHÍNH Ở ĐÂY ===
            // Parse vào ApiResponse<List<RoomTypeItem>> vì "result" trong JSON là một Array.
            Type apiResponseType = new TypeToken<ApiSingleResponse<List<RoomTypeItem>>>(){}.getType();
            ApiSingleResponse<List<RoomTypeItem>> apiResponse = gson.fromJson(jsonData, apiResponseType);

            if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                // Bây giờ apiResponse.getResult() sẽ trực tiếp là List<RoomTypeItem>
                return apiResponse.getResult();
            } else {
                String errorMsg = "API response loại phòng không hợp lệ hoặc báo lỗi.";
                if(apiResponse != null) errorMsg += " Code: " + apiResponse.getCode() + ", Message: " + apiResponse.getMessage();
                System.err.println(errorMsg + (jsonData != null ? ". JSON: " + jsonData : ""));
                return Collections.emptyList();
            }
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Lỗi parse JSON loại phòng: " + e.getMessage() + (jsonData != null ? ". JSON: " + jsonData : ""));
            throw new IOException("Lỗi parse JSON loại phòng: " + e.getMessage(), e);
        }
    }

    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại.");
        }
        return new Request.Builder().url(url).header("Authorization", "Bearer " + token).get().build();
    }
}