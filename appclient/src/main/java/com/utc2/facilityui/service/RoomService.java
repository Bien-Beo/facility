package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder; // Dùng GsonBuilder nếu cần cấu hình đặc biệt (ví dụ date format)
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.ApiResponse; // Import lớp mới
import com.utc2.facilityui.model.Facility;
import com.utc2.facilityui.model.Room;
import com.utc2.facilityui.response.DashboardRoomApiResponse;
import com.utc2.facilityui.response.RoomGroupResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    // --- PHƯƠNG THỨC MỚI LẤY DATA DASHBOARD ---
    /**
     * Lấy dữ liệu cơ sở vật chất (phòng) cho Dashboard, đã được gom nhóm theo loại.
     * Gọi API: GET /facility/dashboard/room
     * @return ObservableList<Facility> chứa tất cả các phòng từ các nhóm.
     * @throws IOException Lỗi mạng hoặc parse.
     */
    public ObservableList<Facility> getDashboardFacilities() throws IOException {
        // !!! ĐẢM BẢO ENDPOINT ĐÚNG VỚI BACKEND CỦA BẠN !!!
        String url = "http://localhost:8080/facility/dashboard/room";
        Request request = buildAuthenticatedGetRequest(url); // Dùng lại hàm helper

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                String errorBodyStr = (responseBody != null) ? responseBody.string() : "N/A";
                if(responseBody != null) responseBody.close();
                throw new IOException("Yêu cầu API Dashboard thất bại: " + response.code() + ". Body: " + errorBodyStr);
            }
            if (responseBody == null) {
                throw new IOException("Response body rỗng từ API Dashboard.");
            }

            String jsonData;
            try { jsonData = responseBody.string(); } finally { responseBody.close(); }
            System.out.println("Dashboard Raw JSON: " + jsonData); // Log để debug

            try {
                // Parse bằng lớp response mới
                DashboardRoomApiResponse apiResponse = gson.fromJson(jsonData, DashboardRoomApiResponse.class);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    // Làm phẳng danh sách: gom tất cả 'Facility' từ các nhóm vào một list
                    ObservableList<Facility> allFacilities = FXCollections.observableArrayList();
                    for (RoomGroupResponse group : apiResponse.getResult()) {
                        if (group != null && group.getRooms() != null) { // Thêm kiểm tra group null
                            // Lọc bỏ facility null nếu có thể xảy ra
                            group.getRooms().stream()
                                    .filter(java.util.Objects::nonNull)
                                    .forEach(allFacilities::add);
                        }
                    }
                    System.out.println("Fetched and flattened " + allFacilities.size() + " facilities.");
                    return allFacilities;
                } else {
                    System.err.println("API Dashboard response không hợp lệ. Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A"));
                    return FXCollections.observableArrayList(); // Trả về list rỗng
                }
            } catch (JsonSyntaxException e) {
                System.err.println("Lỗi parse JSON Dashboard: " + e.getMessage());
                throw new IOException("Không thể parse response dashboard: " + e.getMessage(), e);
            }

        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON chung khi gọi Dashboard API: " + e.getMessage(), e);
        }
    }
    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại.");
        }
        return new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .get()
                .build();
    }
}