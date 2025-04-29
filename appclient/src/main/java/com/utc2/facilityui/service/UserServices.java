package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.helper.Config;
import com.utc2.facilityui.response.ApiSingleResponse; // Sử dụng ApiSingleResponse
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserServices {
    private static final String BASE_URL = Config.getOrDefault("BASE_URL", "http://localhost:8080/api");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();

    /**
     * Lấy thông tin người dùng hiện tại từ API /users/myInfo.
     * Endpoint này trả về trực tiếp đối tượng UserResponse trong trường 'result'.
     *
     * @return Map chứa thông tin người dùng, hoặc Map rỗng nếu lỗi.
     * @throws IOException Nếu có lỗi mạng không mong muốn hoặc lỗi parse JSON.
     */
    public static Map<String, Object> getMyInfo() throws IOException {
        if (!TokenStorage.hasToken()) {
            System.err.println("getMyInfo: Token is missing.");
            return Collections.emptyMap();
        }
        String token = TokenStorage.getToken();
        String url = (BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/") + "users/myInfo";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        System.out.println("Requesting User Info (expecting single object) from: " + url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String responseData = (responseBody != null) ? responseBody.string() : null;
            System.out.println("Received Response: Code=" + response.code() + ", Body Length=" + (responseData != null ? responseData.length() : "null"));
            // System.out.println("DEBUG Raw Response /myInfo: " + responseData); // Bỏ comment nếu cần xem raw data

            if (!response.isSuccessful()) { // Kiểm tra mã HTTP 2xx
                System.err.println("getMyInfo: API Call Failed. HTTP Status Code: " + response.code() + ". Body: " + responseData);
                return Collections.emptyMap();
            }
            if (responseData == null || responseData.isEmpty()) {
                System.err.println("getMyInfo: API response body is null or empty.");
                return Collections.emptyMap();
            }

            // Phân tích JSON - Sử dụng ApiSingleResponse<Map<String, Object>>
            try {
                Type apiResponseType = new TypeToken<ApiSingleResponse<Map<String, Object>>>() {}.getType();
                ApiSingleResponse<Map<String, Object>> apiResponse = gson.fromJson(responseData, apiResponseType);

                if (apiResponse == null) {
                    System.err.println("getMyInfo: Failed to parse JSON into ApiSingleResponse object.");
                    return Collections.emptyMap();
                }

                // --- !!! ĐIỂM CẦN KIỂM TRA !!! ---
                // Xác nhận mã thành công thực tế từ API /myInfo là 0 hay 200?
                // Giả sử là 0 dựa trên JSON mẫu trước đó. Sửa lại nếu cần.
                final int SUCCESS_CODE = 0; // Hoặc 200
                if (apiResponse.getCode() != SUCCESS_CODE) {
                    System.err.println("getMyInfo: API returned business error. Expected Code=" + SUCCESS_CODE
                            + ", Actual Code=" + apiResponse.getCode() + ", Message=" + apiResponse.getMessage());
                    // Nếu backend không dùng trường 'code' mà chỉ dựa vào HTTP 200, hãy xóa bỏ điều kiện if này.
                    return Collections.emptyMap();
                }

                // Lấy kết quả Map user info trực tiếp từ result
                Map<String, Object> resultData = apiResponse.getResult();
                if (resultData == null) {
                    System.err.println("getMyInfo: API response 'result' field (user map) is null.");
                    return Collections.emptyMap();
                }

                System.out.println("getMyInfo: Successfully parsed single user info.");
                return resultData; // Trả về Map thông tin user

            } catch (com.google.gson.JsonSyntaxException e) {
                System.err.println("getMyInfo: JSON Parsing Error for /myInfo response - " + e.getMessage());
                // Ném lại lỗi để Task biết và xử lý trong setOnFailed
                throw new IOException("Failed to parse API response JSON for myInfo: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            System.err.println("getMyInfo: Network or IO Error - " + e.getMessage());
            throw e; // Ném lại lỗi mạng
        }
    }
}