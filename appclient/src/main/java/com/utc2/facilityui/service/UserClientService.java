package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.helper.Config;
import com.utc2.facilityui.model.UserItem; // Model client cho User
import com.utc2.facilityui.response.ApiResponse; // DTO ApiResponse chung

import com.utc2.facilityui.response.ApiSingleResponse;
import com.utc2.facilityui.response.Page;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserClientService {
    private static final String BASE_URL = Config.getOrDefault("BASE_URL", "http://localhost:8080/facility");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new GsonBuilder().create();

    /**
     * Lấy danh sách tất cả người dùng có vai trò là Facility Manager.
     * Gọi API Server: GET /users/fm (hoặc /facility/users/fm tùy BASE_URL)
     */
    public List<UserItem> getAllFacilityManagers() throws IOException {
        // Xây dựng URL dựa trên BASE_URL và endpoint "/users/fm"
        String url = BASE_URL;
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "users/fm"; // Endpoint mới từ UserController của bạn

        Request request = buildAuthenticatedGetRequest(url);
        System.out.println("Fetching facility managers from: " + url);
        String jsonData = null;

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            try{
                if (responseBody == null) throw new IOException("Response body rỗng khi lấy danh sách người quản lý.");
                jsonData = responseBody.string();
            } finally {
                if (responseBody != null) responseBody.close();
            }

            // Lỗi 404 sẽ bị bắt ở đây
            if (!response.isSuccessful()) {
                throw new IOException("Lấy danh sách người quản lý thất bại. Mã lỗi: " + response.code() + ". Phản hồi: " + jsonData);
            }

            // Parse vào ApiResponse<List<UserItem>>
            // Đảm bảo UserItem có các trường id, fullName, username (hoặc dùng @SerializedName)
            // để khớp với UserResponse từ server.
            Type apiResponseType = new TypeToken<ApiSingleResponse<List<UserItem>>>(){}.getType();
            ApiSingleResponse<List<UserItem>> apiResponse = gson.fromJson(jsonData, apiResponseType);

            if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                return apiResponse.getResult(); // apiResponse.getResult() bây giờ là List<UserItem>
            } else {
                String errorMsg = "API response (danh sách người quản lý) không hợp lệ hoặc báo lỗi.";
                if(apiResponse != null) errorMsg += " Code: " + apiResponse.getCode() + ", Message: " + apiResponse.getMessage();
                System.err.println(errorMsg + (jsonData != null ? ". JSON: " + jsonData : ""));
                return Collections.emptyList();
            }
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Lỗi parse JSON (danh sách người quản lý): " + e.getMessage() + (jsonData != null ? ". JSON: " + jsonData : ""));
            throw new IOException("Lỗi parse JSON (danh sách người quản lý): " + e.getMessage(), e);
        }
    }
    public List<UserItem> getAllUsersForFilter() throws IOException {
        List<UserItem> allUsers = new ArrayList<>();
        int currentPage = 0;
        int totalPages = 1;
        String baseUrlForUsers = BASE_URL + "/users";

        // Kiểm tra xem người dùng có token không, nếu không thì không cần gọi API
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            System.out.println("UserClientService: No token found, cannot fetch all users.");
            return Collections.emptyList(); // Hoặc ném một lỗi cụ thể nếu muốn controller xử lý
        }
        // Lưu ý: Việc kiểm tra vai trò (role) nên được thực hiện trong Controller trước khi gọi hàm này
        // để tránh gọi API không cần thiết nếu biết trước người dùng không có quyền.
        // Tuy nhiên, service vẫn sẽ cố gắng gọi và xử lý lỗi từ server.

        while (currentPage < totalPages) {
            String urlWithPage = baseUrlForUsers + "?page=" + currentPage + "&size=100";
            Request request = buildAuthenticatedGetRequest(urlWithPage); // buildAuthenticatedGetRequest đã kiểm tra token
            System.out.println("Fetching all users for filter (page " + currentPage + ") from: " + urlWithPage);
            String jsonData = null;

            try (Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                try {
                    if (responseBody == null) {
                        // Không ném lỗi ở đây ngay, có thể là trang cuối cùng không có body
                        // Nhưng nếu response không successful thì sẽ ném lỗi ở dưới
                        jsonData = "";
                    } else {
                        jsonData = responseBody.string();
                    }
                } finally {
                    if (responseBody != null) responseBody.close();
                }

                if (!response.isSuccessful()) {
                    // Nếu là lỗi 403 (Forbidden) do không phải Admin, chúng ta coi như không có dữ liệu
                    if (response.code() == 403) {
                        System.err.println("Lấy danh sách người dùng thất bại: Người dùng không có quyền ADMIN (403). Trả về danh sách rỗng.");
                        return Collections.emptyList(); // Trả về rỗng để ComboBox không lỗi
                    }
                    throw new IOException("Lấy danh sách tất cả người dùng thất bại (trang " + currentPage + "). Mã lỗi: " + response.code() + ". Phản hồi: " + jsonData);
                }

                if (jsonData.isEmpty() && response.isSuccessful()){ // Trường hợp 204 No Content
                    System.out.println("Received 204 No Content for page " + currentPage + ". Assuming end of data.");
                    break;
                }


                Type apiResponseType = new TypeToken<ApiSingleResponse<Page<UserItem>>>(){}.getType();
                ApiSingleResponse<Page<UserItem>> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    Page<UserItem> pageData = apiResponse.getResult();
                    if (pageData.getContent() != null) {
                        allUsers.addAll(pageData.getContent());
                    }
                    totalPages = pageData.getTotalPages();
                    currentPage++;
                    if (totalPages == 0 || pageData.isLast()) break; // Dừng nếu không có trang nào hoặc là trang cuối
                } else {
                    String errorMsg = "API response người dùng (cho filter) không hợp lệ hoặc báo lỗi (trang " + currentPage + ").";
                    if(apiResponse != null) errorMsg += " Code: " + apiResponse.getCode() + ", Message: " + apiResponse.getMessage();
                    System.err.println(errorMsg + (jsonData != null ? ". JSON: " + jsonData : ""));
                    break;
                }
            } catch (com.google.gson.JsonSyntaxException e) {
                System.err.println("Lỗi parse JSON người dùng (cho filter): " + e.getMessage() + (jsonData != null ? ". JSON: " + jsonData : ""));
                throw new IOException("Lỗi parse JSON người dùng (cho filter): " + e.getMessage(), e);
            }
        }
        System.out.println("Fetched " + allUsers.size() + " total user items for filter.");
        return allUsers;
    }

    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại.");
        }
        return new Request.Builder().url(url).header("Authorization", "Bearer " + token).get().build();
    }
}