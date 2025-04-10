package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder; // Dùng GsonBuilder nếu cần cấu hình đặc biệt (ví dụ date format)
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.ApiResponse; // Import lớp mới
import com.utc2.facilityui.model.Room;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody; // Import ResponseBody

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class RoomService {
    private final OkHttpClient client;
    private final Gson gson;

    public RoomService() {
        client = new OkHttpClient();
        // Cân nhắc dùng GsonBuilder nếu cần xử lý Date/Time phức tạp
        gson = new GsonBuilder().create();
    }

    public List<Room> getRooms() throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("Authentication token is missing. Cannot fetch rooms.");
            return Collections.emptyList();
            // Hoặc throw new IOException("Authentication token is missing.");
        }

        Request request = new Request.Builder()
                .url("http://localhost:8080/facility/rooms") // Đảm bảo URL đúng
                .header("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body(); // Lấy body

            if (!response.isSuccessful()) {
                String errorBody = (responseBody != null) ? responseBody.string() : "null";
                throw new IOException("Unexpected code " + response.code() + " - Body: " + errorBody);
            }

            if (responseBody == null) {
                throw new IOException("Response body is null");
            }

            String jsonData = responseBody.string(); // Đọc body một lần duy nhất

            // Định nghĩa kiểu dữ liệu phức tạp cho Gson
            Type apiResponseType = new TypeToken<ApiResponse<Room>>() {}.getType();

            // Parse JSON thành đối tượng ApiResponse<Room>
            ApiResponse<Room> apiResponse = gson.fromJson(jsonData, apiResponseType);

            // Kiểm tra kết quả từ API (ví dụ: mã lỗi do server trả về trong JSON)
            if (apiResponse == null || apiResponse.getCode() != 0 || apiResponse.getResult() == null || apiResponse.getResult().getContent() == null) {
                System.err.println("API response indicates failure or missing data. Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A"));
                // Có thể throw Exception hoặc trả về list rỗng tùy logic
                return Collections.emptyList();
            }

            // Trả về danh sách các phòng từ trường 'content'
            return apiResponse.getResult().getContent();

        } catch (com.google.gson.JsonSyntaxException e) {
            // Lỗi nếu JSON không đúng định dạng
            throw new IOException("Error parsing JSON response: " + e.getMessage(), e);
        }
    }
}