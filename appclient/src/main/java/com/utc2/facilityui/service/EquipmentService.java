// File: src/main/java/com/utc2/facilityui/service/EquipmentService.java
package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.ApiResponse; // Dùng lại model ApiResponse
import com.utc2.facilityui.model.Equipment;   // Model Equipment đã tạo/sửa
import okhttp3.*; // Import các lớp OkHttp

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects; // Import Objects để kiểm tra null

public class EquipmentService {

    private final OkHttpClient client;
    private final Gson gson;
    // !!! THAY ĐỔI BASE_URL NẾU API CỦA BẠN CHẠY Ở ĐỊA CHỈ KHÁC !!!
    private static final String BASE_URL = "http://localhost:8080/facility"; // Giả sử base URL

    public EquipmentService() {
        client = new OkHttpClient();
        // Cấu hình Gson nếu cần (ví dụ: định dạng ngày tháng)
        gson = new GsonBuilder().create();
    }

    /**
     * Lấy danh sách tất cả các trang thiết bị (có thể cần phân trang).
     * Endpoint ví dụ: GET /equipments
     * @return Danh sách Equipment hoặc danh sách rỗng nếu có lỗi/không có dữ liệu.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi không mong muốn từ server.
     */
    public List<Equipment> getAllEquipments() throws IOException {
        // !!! THAY ĐỔI ENDPOINT NẾU CẦN !!!
        String url = BASE_URL + "/equipments";
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            // Giả định API trả về cấu trúc ApiResponse<Equipment> có result.content là List
            return parseEquipmentListFromResponse(response);
        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON từ API equipments: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách các trang thiết bị thuộc về một phòng cụ thể.
     * Endpoint ví dụ: GET /rooms/{roomId}/equipments
     * @param roomId ID của phòng cần lấy thiết bị.
     * @return Danh sách Equipment hoặc danh sách rỗng nếu có lỗi/không có dữ liệu.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi không mong muốn từ server.
     * @throws IllegalArgumentException Nếu roomId không hợp lệ (null hoặc rỗng).
     */
    public List<Equipment> getEquipmentsByRoom(String roomId) throws IOException {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID không được để trống");
        }
        // !!! THAY ĐỔI ENDPOINT NẾU CẦN !!!
        String url = BASE_URL + "/rooms/" + roomId.trim() + "/equipments";
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            // Giả định API trả về cấu trúc ApiResponse<Equipment> có result.content là List
            return parseEquipmentListFromResponse(response);
        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON từ API room equipments: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy thông tin chi tiết của một trang thiết bị cụ thể bằng ID.
     * Endpoint ví dụ: GET /equipments/{equipmentId}
     * @param equipmentId ID của thiết bị cần lấy thông tin.
     * @return Đối tượng Equipment hoặc null nếu không tìm thấy hoặc có lỗi.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi không mong muốn từ server.
     * @throws IllegalArgumentException Nếu equipmentId không hợp lệ (null hoặc rỗng).
     */
    public Equipment getEquipmentById(String equipmentId) throws IOException {
        if (equipmentId == null || equipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment ID không được để trống");
        }
        // !!! THAY ĐỔI ENDPOINT NẾU CẦN !!!
        String url = BASE_URL + "/equipments/" + equipmentId.trim();
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            // --- LOGIC PARSE Ở ĐÂY CẦN ĐIỀU CHỈNH THEO API CỦA BẠN ---
            // Giả định 1: API trả về ApiResponse<Equipment> với result là Equipment object
            /*
            Type apiResponseType = new TypeToken<ApiResponse<Equipment>>() {}.getType();
            ApiResponse<Equipment> apiResponse = parseResponse(response, apiResponseType); // Dùng hàm parse tổng quát hơn
            if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                 return apiResponse.getResult();
            }
            */

            // Giả định 2: API trả về trực tiếp một Equipment object (không có ApiResponse bao ngoài)
             /*
             if (!response.isSuccessful()) {
                  throw new IOException("Yêu cầu API thất bại với mã lỗi " + response.code());
             }
             ResponseBody body = response.body();
             if (body == null) throw new IOException("Response body rỗng.");
             return gson.fromJson(body.string(), Equipment.class);
             */

            // Giả định 3 (Như code cũ): API trả về dạng list dù chỉ có 1 phần tử
            List<Equipment> list = parseEquipmentListFromResponse(response);
            if (list != null && !list.isEmpty()) {
                return list.get(0); // Lấy phần tử đầu tiên
            }

            // Nếu không khớp giả định nào hoặc lỗi parse
            System.err.println("Không thể parse chi tiết equipment từ response.");
            return null;
        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON từ API equipment detail: " + e.getMessage(), e);
        }
    }


    // ----- Các phương thức tiện ích -----

    /**
     * Xây dựng một GET request đã được xác thực bằng token.
     * @param url URL của request.
     * @return Đối tượng Request.
     * @throws IOException Nếu token không tồn tại.
     */
    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại hoặc đã hết hạn.");
        }
        return new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .get()
                .build();
    }

    /**
     * Parse một danh sách Equipment từ Response.
     * *** Giả định cấu trúc trả về là ApiResponse<Equipment> có result.content là List<Equipment>. ***
     * *** Nếu API trả về trực tiếp List<Equipment>, hãy sửa lại logic parse bên trong. ***
     * @param response Response từ OkHttp.
     * @return List<Equipment> hoặc Collections.emptyList() nếu lỗi hoặc không có dữ liệu.
     * @throws IOException Nếu response không thành công hoặc body null.
     * @throws JsonSyntaxException Nếu JSON không hợp lệ.
     */
    private List<Equipment> parseEquipmentListFromResponse(Response response) throws IOException, JsonSyntaxException {
        ResponseBody responseBody = response.body(); // Lấy body một lần

        // Luôn kiểm tra response thành công và body khác null trước khi đọc
        if (!response.isSuccessful()) {
            String errorBodyStr = (responseBody != null) ? responseBody.string() : "N/A";
            // Đóng body nếu đã đọc lỗi
            if(responseBody != null) responseBody.close();
            throw new IOException("Yêu cầu API thất bại với mã lỗi " + response.code() + ". Body: " + errorBodyStr);
        }
        if (responseBody == null) {
            throw new IOException("Response body rỗng từ API.");
        }

        // Chỉ đọc body một lần duy nhất trong khối try-with-resources cho String
        String jsonData;
        try {
            jsonData = responseBody.string();
        } finally {
            responseBody.close(); // Đảm bảo body được đóng ngay sau khi đọc xong
        }


        // --- LOGIC PARSE ---
        // **Cách 1: Giả định cấu trúc ApiResponse<Equipment> với result.content**
        try {
            Type listType = new TypeToken<ApiResponse<Equipment>>() {}.getType();
            ApiResponse<Equipment> apiResponse = gson.fromJson(jsonData, listType);

            if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null && apiResponse.getResult().getContent() != null) {
                return apiResponse.getResult().getContent();
            } else {
                System.err.println("API response không hợp lệ hoặc không chứa content equipment. Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A") + ", Data: " + jsonData.substring(0, Math.min(jsonData.length(), 500))); // Log một phần dữ liệu để debug
                return Collections.emptyList();
            }
        } catch (JsonSyntaxException e) {
            // Nếu parse theo cấu trúc ApiResponse lỗi, thử cách khác nếu có thể
            System.err.println("Parse theo cấu trúc ApiResponse<List<Equipment>> thất bại: " + e.getMessage());
            // **Cách 2: Giả định API trả về trực tiếp List<Equipment>**
              /*
              try {
                   Type directListType = new TypeToken<List<Equipment>>() {}.getType();
                   List<Equipment> directList = gson.fromJson(jsonData, directListType);
                   return (directList != null) ? directList : Collections.emptyList();
              } catch (JsonSyntaxException e2) {
                   System.err.println("Parse trực tiếp List<Equipment> cũng thất bại: " + e2.getMessage());
                   throw e2; // Ném lại lỗi parse gốc hoặc lỗi thứ 2
              }
              */
            throw e; // Ném lại lỗi parse đầu tiên nếu chỉ thử 1 cách
        }
    }

    /**
     * Hàm parse response tổng quát hơn (nếu bạn cần parse các kiểu trả về khác nhau).
     * @param response Response từ OkHttp.
     * @param typeOfT Kiểu dữ liệu mong đợi (ví dụ: TypeToken<ApiResponse<Equipment>>(){}.getType()).
     * @return Đối tượng đã parse hoặc null nếu có lỗi.
     * @throws IOException Nếu response không thành công hoặc body null.
     * @throws JsonSyntaxException Nếu JSON không hợp lệ.
     */
    private <T> T parseResponse(Response response, Type typeOfT) throws IOException, JsonSyntaxException {
        ResponseBody responseBody = response.body();
        if (!response.isSuccessful()) {
            String errorBodyStr = (responseBody != null) ? responseBody.string() : "N/A";
            if (responseBody != null) responseBody.close();
            throw new IOException("Yêu cầu API thất bại với mã lỗi " + response.code() + ". Body: " + errorBodyStr);
        }
        if (responseBody == null) {
            throw new IOException("Response body rỗng từ API.");
        }
        String jsonData;
        try {
            jsonData = responseBody.string();
        } finally {
            responseBody.close();
        }
        // Trả về null nếu jsonData rỗng hoặc chỉ chứa "null"
        if (jsonData == null || jsonData.trim().isEmpty() || jsonData.trim().equalsIgnoreCase("null")) {
            return null;
        }
        return gson.fromJson(jsonData, typeOfT);
    }

}