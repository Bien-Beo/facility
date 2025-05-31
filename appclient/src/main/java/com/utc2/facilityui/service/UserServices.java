package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.helper.Config;
import com.utc2.facilityui.model.User;
import com.utc2.facilityui.model.UserUpdateRequest;
import com.utc2.facilityui.response.*;
// Bỏ import DTO request: import com.utc2.facilityui.dto.request.PasswordResetRequest;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UserServices { // Hoặc AuthService riêng
    private static final String BASE_URL = Config.getOrDefault("BASE_URL", "http://localhost:8080/api");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();
    private static final int API_SUCCESS_CODE = 0;
    public static User getMyInfo() throws IOException { // << THAY ĐỔI KIỂU TRẢ VỀ THÀNH User
        String token = TokenStorage.getToken(); // Giả sử TokenStorage đã được cập nhật để lấy token
        if (token == null || token.isEmpty()) {
            System.err.println("UserServices.getMyInfo: Token is missing.");
            throw new IOException("Authentication token is required to get user info.");
        }

        String url = (BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/") + "users/myInfo";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        System.out.println("UserServices.getMyInfo: Requesting User Info from: " + url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String responseData = (responseBody != null) ? responseBody.string() : null;
            System.out.println("UserServices.getMyInfo: Received Response: Code=" + response.code() + ", Body: " + (responseData != null && responseData.length() > 100 ? responseData.substring(0, 100) + "..." : responseData));


            if (!response.isSuccessful()) {
                System.err.println("UserServices.getMyInfo: API Call Failed. HTTP Status: " + response.code() + ". Body: " + responseData);
                throw new IOException("Failed to get user info with HTTP status: " + response.code() + ". Response: " + responseData);
            }
            if (responseData == null || responseData.isEmpty()) {
                System.err.println("UserServices.getMyInfo: Empty response body from server.");
                throw new IOException("Empty response body received from server for user info.");
            }

            try {
                // Parse response thành ApiSingleResponse<User>
                // Điều này yêu cầu JSON response từ server có trường "result" chứa object User
                // và các trường trong object User đó khớp với model User.java của client
                // (ví dụ: "id", "userId", "username", "email", "avatar")
                Type apiUserResponseType = new TypeToken<ApiSingleResponse<User>>() {}.getType();
                ApiSingleResponse<User> apiResponse = gson.fromJson(responseData, apiUserResponseType);

                if (apiResponse == null) {
                    System.err.println("UserServices.getMyInfo: Failed to parse main API response structure.");
                    throw new IOException("Could not parse the API response structure for user info.");
                }

                // QUAN TRỌNG: Xác nhận lại SUCCESS_CODE này với backend của bạn

                final int SUCCESS_CODE = 0;// Hoặc mã thành công thực tế của bạn
                if (apiResponse.getCode() != SUCCESS_CODE) {
                    String errorMessage = "Failed to get user info: " +
                            (apiResponse.getMessage() != null ? apiResponse.getMessage() : "Unknown API error") +
                            " (API Code: " + apiResponse.getCode() + ")";
                    System.err.println("UserServices.getMyInfo: API returned business error. " + errorMessage);
                    throw new IOException(errorMessage);
                }

                User user = apiResponse.getResult();
                if (user == null) {
                    System.err.println("UserServices.getMyInfo: User data (result) is null in API response.");
                    throw new IOException("User data (result) was null in the API response.");
                }

                System.out.println("UserServices.getMyInfo: Successfully parsed user info for user: " + user.getUsername());
                return user; // << TRẢ VỀ ĐỐI TƯỢNG User

            } catch (com.google.gson.JsonSyntaxException e) {
                System.err.println("UserServices.getMyInfo: JSON Parsing Error - " + e.getMessage() + ". Response data: " + responseData);
                throw new IOException("Invalid JSON response format from server for user info: " + e.getMessage(), e);
            }
        } catch (IOException e) { // Lỗi mạng hoặc IO từ client.newCall
            System.err.println("UserServices.getMyInfo: Network or IO Error - " + e.getMessage());
            throw e; // Ném lại để LoginController xử lý
        }
    }

    /**
     * Gửi yêu cầu thay đổi mật khẩu đến API backend.
     *
     * @param oldPassword Mật khẩu cũ.
     * @param newPassword Mật khẩu mới.
     * @return true nếu API trả về thành công, false nếu có lỗi logic từ API.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi parse JSON.
     */
    // Sửa đổi tham số: nhận trực tiếp String thay vì DTO
    public static boolean resetPassword(String oldPassword, String newPassword) throws IOException {
        if (!TokenStorage.hasToken()) {
            System.err.println("resetPassword: Token is missing.");
            throw new IOException("Authentication token is required to reset password.");
        }
        String token = TokenStorage.getToken();
        String url = (BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/") + "auth/password/reset";

        // Tạo Map để chứa dữ liệu request body
        Map<String, String> requestData = new HashMap<>();
        requestData.put("oldPassword", oldPassword);
        requestData.put("newPassword", newPassword);

        // Tạo request body từ Map đã tạo
        RequestBody body = RequestBody.create(
                gson.toJson(requestData), // Chuyển Map thành JSON
                MediaType.get("application/json; charset=utf-8")
        );

        Request apiRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();

        System.out.println("Requesting Password Reset to: " + url);

        try (Response response = client.newCall(apiRequest).execute()) {
            ResponseBody responseBody = response.body();
            String responseData = (responseBody != null) ? responseBody.string() : null;
            System.out.println("Received Reset Password Response: Code=" + response.code() + ", Body Length=" + (responseData != null ? responseData.length() : "null"));

            if (!response.isSuccessful()) {
                System.err.println("resetPassword: API Call Failed. HTTP Status Code: " + response.code() + ". Body: " + responseData);
                throw new IOException("Password reset failed with HTTP status: " + response.code());
            }

            if (responseData == null || responseData.isEmpty() || responseData.trim().equals("{}")) {
                System.out.println("resetPassword: Success (assuming based on HTTP 2xx and empty/no body).");
                return true;
            }

            // Parse response dùng ApiSingleResponse<Void> như cũ
            try {
                Type apiResponseType = new TypeToken<ApiSingleResponse<Void>>() {}.getType();
                ApiSingleResponse<Void> apiResponse = gson.fromJson(responseData, apiResponseType);

                if (apiResponse == null) {
                    System.err.println("resetPassword: Failed to parse JSON response body.");
                    return false; // Coi là lỗi nếu không parse được JSON hợp lệ
                }

                final int SUCCESS_CODE = 0; // Xác nhận lại mã này
                if (apiResponse.getCode() != SUCCESS_CODE) {
                    System.err.println("resetPassword: API returned business error. Code=" + apiResponse.getCode() + ", Message=" + apiResponse.getMessage());
                    throw new IOException("Password reset failed: " + apiResponse.getMessage() + " (Code: " + apiResponse.getCode() + ")");
                }

                System.out.println("resetPassword: Successfully reset password via API response.");
                return true;

            } catch (com.google.gson.JsonSyntaxException e) {
                System.err.println("resetPassword: JSON Parsing Error - " + e.getMessage());
                // Ném lại lỗi để Task biết
                throw new IOException("Invalid JSON response format from server: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            System.err.println("resetPassword: Network or IO Error - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Lấy danh sách người dùng có phân trang, sử dụng cấu trúc ApiContainerForPagedData.
     * Phương thức này sẽ trả về một đối tượng PaginatedUsers (một lớp tùy chỉnh)
     * chứa danh sách UserResponse và PageMetadata.
     */
    public static PaginatedUsers getUsers(int page, int size) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Cần token xác thực.");
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse((BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/") + "users").newBuilder();
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("size", String.valueOf(size));

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        System.out.println("UserServices.getUsers (ApiContainer): Đang yêu cầu từ: " + urlBuilder.build().toString());

        try (Response response = client.newCall(request).execute()) {
            String responseData = response.body() != null ? response.body().string() : null;
            System.out.println("UserServices.getUsers (ApiContainer): ResponseData Raw: " + responseData);


            if (!response.isSuccessful() || responseData == null) {
                throw new IOException("Lấy danh sách người dùng thất bại: " + response.code() + (responseData != null ? " - " + responseData : ""));
            }

            // Sử dụng ApiContainerForPagedData<UserResponse>
            Type apiResponseType = new TypeToken<ApiContainerForPagedData<UserResponse>>() {}.getType();
            ApiContainerForPagedData<UserResponse> apiContainer = gson.fromJson(responseData, apiResponseType);

            if (apiContainer == null || apiContainer.getCode() != API_SUCCESS_CODE || apiContainer.getResult() == null || apiContainer.getResult().getPage() == null) {
                String message = apiContainer != null && apiContainer.getMessage() != null ? apiContainer.getMessage() : "Phản hồi không hợp lệ hoặc thiếu thông tin phân trang.";
                throw new IOException("Lấy danh sách thất bại: " + message + " (Code: " + (apiContainer != null ? apiContainer.getCode() : "N/A") + ")");
            }

            // Trả về một đối tượng tùy chỉnh chứa cả content và metadata
            return new PaginatedUsers(apiContainer.getResult().getContent(), apiContainer.getResult().getPage());
        }
    }

    // Lớp nội bộ hoặc lớp riêng để chứa kết quả phân trang từ cấu trúc mới
    public static class PaginatedUsers {
        private final List<UserResponse> content;
        private final PageMetadata pageMetadata;

        public PaginatedUsers(List<UserResponse> content, PageMetadata pageMetadata) {
            this.content = content;
            this.pageMetadata = pageMetadata;
        }

        public List<UserResponse> getContent() {
            return content;
        }

        public PageMetadata getPageMetadata() {
            return pageMetadata;
        }
    }

    /**
     * Tạo người dùng mới.
     * Bạn cần tạo một DTO phía client (ví dụ: UserCreationRequest) để gửi đi.
     * Hoặc truyền một Map<String, Object> chứa dữ liệu.
     */
    public static UserResponse createUser(Object userCreationRequestDto /* Thay bằng DTO cụ thể */) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Cần token xác thực.");
        }

        String url = (BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/") + "users";
        RequestBody body = RequestBody.create(gson.toJson(userCreationRequestDto), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseData = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful() || responseData == null) {
                throw new IOException("Tạo người dùng thất bại: " + response.code() + (responseData != null ? " - " + responseData : ""));
            }

            Type apiResponseType = new TypeToken<ApiSingleResponse<UserResponse>>() {}.getType();
            ApiSingleResponse<UserResponse> apiResponse = gson.fromJson(responseData, apiResponseType);

            if (apiResponse == null || apiResponse.getCode() != API_SUCCESS_CODE || apiResponse.getResult() == null) {
                String message = apiResponse != null && apiResponse.getMessage() != null ? apiResponse.getMessage() : "Phản hồi không hợp lệ.";
                throw new IOException("Tạo người dùng thất bại: " + message + " (Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A") + ")");
            }
            return apiResponse.getResult();
        }
    }

    /**
     * Cập nhật thông tin người dùng.
     * Bạn cần tạo một DTO phía client (ví dụ: UserUpdateRequest) để gửi đi.
     */
    public static UserResponse updateUser(String userIdToUpdate, UserUpdateRequest userUpdateRequestDto /* Thay bằng DTO cụ thể */) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Cần token xác thực.");
        }

        String url = (BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/") + "users/" + userIdToUpdate;
        RequestBody body = RequestBody.create(gson.toJson(userUpdateRequestDto), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseData = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful() || responseData == null) {
                throw new IOException("Cập nhật người dùng thất bại: " + response.code() + (responseData != null ? " - " + responseData : ""));
            }

            Type apiResponseType = new TypeToken<ApiSingleResponse<UserResponse>>() {}.getType();
            ApiSingleResponse<UserResponse> apiResponse = gson.fromJson(responseData, apiResponseType);

            if (apiResponse == null || apiResponse.getCode() != API_SUCCESS_CODE || apiResponse.getResult() == null) {
                String message = apiResponse != null && apiResponse.getMessage() != null ? apiResponse.getMessage() : "Phản hồi không hợp lệ.";
                throw new IOException("Cập nhật người dùng thất bại: " + message + " (Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A") + ")");
            }
            return apiResponse.getResult();
        }
    }

    /**
     * Xóa người dùng.
     */
    public static void deleteUser(String userIdToDelete) throws IOException { // Thay đổi kiểu trả về thành void hoặc boolean
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Cần token xác thực.");
        }

        String url = (BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/") + "users/" + userIdToDelete;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseData = response.body() != null ? response.body().string() : null;
            // API Xóa có thể trả về HTTP 204 No Content (responseData sẽ là null hoặc trống)
            // hoặc một JSON xác nhận.
            if (!response.isSuccessful()) {
                throw new IOException("Xóa người dùng thất bại: " + response.code() + (responseData != null ? " - " + responseData : ""));
            }

            // Nếu API trả về JSON (ví dụ: ApiSingleResponse<Void> hoặc tương tự)
            if (responseData != null && !responseData.isEmpty()) {
                Type apiResponseType = new TypeToken<ApiSingleResponse<Void>>() {}.getType(); // Hoặc ApiSingleResponse<Object>
                ApiSingleResponse<Void> apiResponse = gson.fromJson(responseData, apiResponseType);

                if (apiResponse == null || apiResponse.getCode() != API_SUCCESS_CODE) {
                    String message = apiResponse != null && apiResponse.getMessage() != null ? apiResponse.getMessage() : "Phản hồi không hợp lệ.";
                    throw new IOException("Xóa người dùng thất bại: " + message + " (Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A") + ")");
                }
            }
            // Nếu HTTP 200/204 và không có lỗi parse ở trên, coi như thành công.
        }
    }
    private static final String DB_URL = "jdbc:mysql://localhost:3306/facility";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Tranbien2809@";

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = ((java.sql.Connection) conn).prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setUserId(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setAvatar(rs.getString("avatar"));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // hoặc log lỗi tùy dự án
        }

        return users;
    }

    public User getUserById(String userId) {
        String sql = "SELECT id, user_id, username, email, avatar FROM user WHERE id = ?";
        User user = null;

        try (Connection conn = java.sql.DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getString("id"));
                user.setUserId(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setAvatar(rs.getString("avatar"));
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
    public Map<String, String> getAllUserIdUsernameMap() {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT id, username FROM user";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("id"), rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}