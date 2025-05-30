package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.helper.Config;
import com.utc2.facilityui.model.BuildingItem;
// Sử dụng các DTO response bạn đã cung cấp
import com.utc2.facilityui.response.ApiContainerForPagedData;
import com.utc2.facilityui.response.PageMetadata;
import com.utc2.facilityui.response.ResultWithNestedPage;


import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
// import java.util.stream.Collectors; // Không cần thiết nếu server trả về đúng kiểu BuildingItem

public class BuildingClientService {
    private static final String BASE_URL = Config.getOrDefault("BASE_URL", "http://localhost:8080/api");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new GsonBuilder().create();

    public List<BuildingItem> getAllBuildings() throws IOException {
        List<BuildingItem> allBuildings = new ArrayList<>();
        int currentPage = 0;
        int totalPages = 1;
        String initialUrl = (BASE_URL.startsWith("http") ? BASE_URL : "http://" + BASE_URL);
        if (BASE_URL.contains("/facility")){
            initialUrl = Config.getOrDefault("BASE_URL", "http://localhost:8080/facility") + "/buildings";
        } else {
            initialUrl += "/buildings";
        }


        while (currentPage < totalPages) {
            String urlWithPage = initialUrl + "?page=" + currentPage + "&size=100";
            Request request = buildAuthenticatedGetRequest(urlWithPage);
            System.out.println("Fetching buildings from: " + urlWithPage);
            String jsonData = null;

            try (Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                try {
                    if (responseBody == null) throw new IOException("Response body rỗng (buildings trang " + currentPage + ")");
                    jsonData = responseBody.string();
                } finally {
                    if (responseBody != null) responseBody.close();
                }

                if (!response.isSuccessful()) {
                    throw new IOException("Lấy danh sách tòa nhà thất bại (trang " + currentPage + "). Mã lỗi: " + response.code() + ". Phản hồi: " + jsonData);
                }

                // Parse vào ApiContainerForPagedData<BuildingItem>
                Type apiContainerType = new TypeToken<ApiContainerForPagedData<BuildingItem>>(){}.getType();
                ApiContainerForPagedData<BuildingItem> apiContainer = gson.fromJson(jsonData, apiContainerType);

                if (apiContainer != null && apiContainer.getCode() == 0 && apiContainer.getResult() != null) {
                    ResultWithNestedPage<BuildingItem> resultData = apiContainer.getResult();
                    if (resultData.getContent() != null) {
                        allBuildings.addAll(resultData.getContent());
                    }
                    PageMetadata pageMeta = resultData.getPage();
                    if (pageMeta != null) {
                        totalPages = pageMeta.getTotalPages();
                    } else if (resultData.getContent() == null || resultData.getContent().isEmpty()){
                        // Nếu không có page metadata và không có content, dừng lại
                        totalPages = 0;
                    } // Nếu có content mà không có page metadata, có thể là server chỉ trả 1 trang và không có metadata

                    currentPage++;
                    if (totalPages == 0 && !allBuildings.isEmpty()) { // Nếu server trả 1 trang nhưng totalPages=0
                        break; // Đã lấy hết
                    }
                    if (totalPages == 0 && allBuildings.isEmpty()) break;


                } else {
                    String errorMsg = "API response tòa nhà không hợp lệ hoặc báo lỗi (trang " + currentPage + ").";
                    if(apiContainer != null) errorMsg += " Code: " + apiContainer.getCode() + ", Message: " + apiContainer.getMessage();
                    System.err.println(errorMsg + (jsonData != null ? ". JSON: " + jsonData : ""));
                    break;
                }
            } catch (com.google.gson.JsonSyntaxException e) {
                System.err.println("Lỗi parse JSON tòa nhà: " + e.getMessage() + (jsonData != null ? ". JSON: " + jsonData : ""));
                throw new IOException("Lỗi parse JSON tòa nhà: " + e.getMessage(), e);
            }
        }
        System.out.println("Fetched " + allBuildings.size() + " buildings in total.");
        return allBuildings;
    }

    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại.");
        }
        return new Request.Builder().url(url).header("Authorization", "Bearer " + token).get().build();
    }
}