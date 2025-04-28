package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage; // Đảm bảo bạn có lớp này và nó hoạt động
import com.utc2.facilityui.model.Facility;
import com.utc2.facilityui.model.Room;
import com.utc2.facilityui.response.ApiResponse;
import com.utc2.facilityui.response.DashboardRoomApiResponse;
import com.utc2.facilityui.response.RoomGroupResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okhttp3.*; // Import đầy đủ okhttp3

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoomService {
    private final OkHttpClient client;
    private final Gson gson;
    // *** THAY ĐỔI BASE_URL NẾU CẦN ***
    private static final String BASE_URL = "http://localhost:8080/facility";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public RoomService() {
        // Cân nhắc cấu hình thêm cho OkHttpClient nếu cần (timeouts, interceptors, v.v.)
        client = new OkHttpClient();
        gson = new GsonBuilder().create(); // Có thể thêm cấu hình cho Gson nếu cần
    }

    /**
     * Lấy danh sách Room cơ bản (ví dụ).
     * Endpoint: GET /facility/rooms
     */
    public List<Room> getRooms() throws IOException {
        String url = BASE_URL + "/rooms";
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();

            if (!response.isSuccessful()) {
                String errorBody = (responseBody != null) ? responseBody.string() : "N/A";
                if(responseBody != null) responseBody.close();
                throw new IOException("Lấy danh sách phòng thất bại. Mã lỗi: " + response.code() + ". Phản hồi: " + errorBody);
            }
            if (responseBody == null) throw new IOException("Response body rỗng khi lấy danh sách phòng.");

            String jsonData;
            try { jsonData = responseBody.string(); } finally { responseBody.close(); }

            Type apiResponseType = new TypeToken<ApiResponse<Room>>() {}.getType();
            ApiResponse<Room> apiResponse = gson.fromJson(jsonData, apiResponseType);

            if (apiResponse == null || apiResponse.getCode() != 0 || apiResponse.getResult() == null || apiResponse.getResult().getContent() == null) {
                System.err.println("API response không hợp lệ hoặc báo lỗi. Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A"));
                return Collections.emptyList();
            }
            return apiResponse.getResult().getContent();

        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON khi lấy danh sách phòng: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy dữ liệu Facility cho Dashboard.
     * Endpoint: GET /facility/dashboard/room
     */
    public ObservableList<Facility> getDashboardFacilities() throws IOException {
        String url = BASE_URL + "/dashboard/room";
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                String errorBodyStr = (responseBody != null) ? responseBody.string() : "N/A";
                if(responseBody != null) responseBody.close();
                throw new IOException("Yêu cầu API Dashboard thất bại: " + response.code() + ". Body: " + errorBodyStr);
            }
            if (responseBody == null) throw new IOException("Response body rỗng từ API Dashboard.");

            String jsonData;
            try { jsonData = responseBody.string(); } finally { responseBody.close(); }
            System.out.println("Dashboard Raw JSON: " + jsonData); // Log để debug

            try {
                DashboardRoomApiResponse apiResponse = gson.fromJson(jsonData, DashboardRoomApiResponse.class);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    ObservableList<Facility> allFacilities = FXCollections.observableArrayList();
                    for (RoomGroupResponse group : apiResponse.getResult()) {
                        if (group != null && group.getRooms() != null) {
                            // Lọc các facility null trước khi thêm vào danh sách
                            group.getRooms().stream()
                                    .filter(Objects::nonNull)
                                    .forEach(allFacilities::add);
                        }
                    }
                    System.out.println("Fetched and flattened " + allFacilities.size() + " facilities.");
                    return allFacilities;
                } else {
                    String apiCode = (apiResponse != null) ? String.valueOf(apiResponse.getCode()) : "N/A";
                    System.err.println("API Dashboard response không hợp lệ hoặc báo lỗi. Code: " + apiCode);
                    // Trả về danh sách rỗng thay vì ném lỗi nếu API báo lỗi logic (ví dụ code != 0)
                    return FXCollections.observableArrayList();
                }
            } catch (JsonSyntaxException e) {
                System.err.println("Lỗi parse JSON Dashboard: " + e.getMessage());
                throw new IOException("Không thể parse response dashboard: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Gửi yêu cầu POST để tạo phòng mới sử dụng dữ liệu từ Map.
     * Endpoint: POST /facility/rooms
     * @param roomData Map chứa thông tin phòng cần tạo.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi từ API.
     */
    public void addRoomFromMap(Map<String, Object> roomData) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại. Không thể thêm phòng.");
        }

        String url = BASE_URL + "/rooms"; // Endpoint để tạo phòng
        String jsonRequestBody = gson.toJson(roomData);
        System.out.println("Sending request (from map) to add room: " + jsonRequestBody);

        RequestBody body = RequestBody.create(jsonRequestBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                ResponseBody errorBody = response.body();
                String errorBodyString = (errorBody != null) ? errorBody.string() : "N/A";
                if(errorBody != null) errorBody.close();
                throw new IOException("Thêm phòng thất bại. Mã lỗi: " + response.code() + ". Phản hồi: " + errorBodyString);
            } else {
                System.out.println("Thêm phòng thành công! Code: " + response.code());
                if(response.body() != null) response.body().close();
            }
        }
    }

    /**
     * Gửi yêu cầu DELETE để xóa một facility/room dựa trên ID.
     * Endpoint: DELETE /facility/rooms/{facilityId} (***Kiểm tra lại endpoint này***)
     *
     * @param facilityId ID của facility/room cần xóa (kiểu String).
     * @return true nếu xóa thành công trên server (mã 2xx).
     * @throws IOException Nếu có lỗi mạng, lỗi parse, token không hợp lệ, ID trống,
     * hoặc server trả về mã lỗi HTTP không thành công (4xx, 5xx).
     */
    public boolean deleteFacilityById(String facilityId) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại. Không thể xóa.");
        }
        // Kiểm tra ID hợp lệ
        if (facilityId == null || facilityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Facility ID không được để trống khi yêu cầu xóa.");
        }

        // *** QUAN TRỌNG: Xác nhận lại URL endpoint xóa này với API backend của bạn ***
        String url = BASE_URL + "/rooms/" + facilityId.trim(); // Trim ID để loại bỏ khoảng trắng thừa

        System.out.println("Sending DELETE request for facility ID: " + facilityId + " to URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .delete() // Sử dụng phương thức DELETE
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // Xử lý lỗi từ server
                ResponseBody errorBody = response.body();
                String errorBodyString = (errorBody != null) ? errorBody.string() : "N/A";
                if(errorBody != null) errorBody.close();
                System.err.println("Xóa thất bại từ server. Mã lỗi: " + response.code() + ". Phản hồi: " + errorBodyString);
                // Ném Exception để báo lỗi cho Controller xử lý
                throw new IOException("Xóa thất bại. Mã lỗi server: " + response.code() + ". Phản hồi: " + errorBodyString);
            } else {
                // Thành công (mã 2xx)
                System.out.println("Xóa facility/room thành công! Code: " + response.code());
                // Đảm bảo đóng body ngay cả khi thành công (đặc biệt với 204 No Content)
                if(response.body() != null) response.body().close();
                return true; // Trả về true khi server xác nhận xóa thành công
            }
        }
        // OkHttp tự động đóng Response khi dùng try-with-resources
    }


    /**
     * Helper method để xây dựng yêu cầu GET có xác thực.
     * @param url URL đích.
     * @return Đối tượng Request đã có header Authorization.
     * @throws IOException Nếu token không tồn tại.
     */
    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            // Có thể ném lỗi cụ thể hơn hoặc điều hướng đến màn hình đăng nhập
            throw new IOException("Token xác thực không tồn tại.");
        }
        return new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .get()
                .build();
    }
}