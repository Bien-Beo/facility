// File: src/main/java/com/utc2/facilityui/service/EquipmentService.java
package com.utc2.facilityui.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.Equipment;   // Model Equipment đã tạo/sửa

import com.utc2.facilityui.model.EquipmentModel;
import com.utc2.facilityui.model.Room;
import com.utc2.facilityui.response.ApiResponse;
import okhttp3.*; // Import các lớp OkHttp

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EquipmentService {
//
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
    /**
     * Cập nhật thông tin thiết bị qua API.
     * Endpoint: PUT /equipments/{id}
     * @param equipment Thiết bị cần cập nhật.
     * @return true nếu thành công, false nếu thất bại.
     * @throws IOException nếu lỗi mạng hoặc server.
     */
    public boolean updateEquipment(Equipment equipment) throws IOException {
        if (equipment == null || equipment.getId() == null || equipment.getId().isEmpty()) {
            throw new IllegalArgumentException("Thiết bị không hợp lệ hoặc chưa có ID.");
        }

        String url = BASE_URL + "/equipments/" + equipment.getId(); // Giả định có id

        String json = gson.toJson(equipment);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .header("Authorization", "Bearer " + TokenStorage.getToken())
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String error = response.body() != null ? response.body().string() : "N/A";
                throw new IOException("Lỗi cập nhật thiết bị: " + response.code() + ", " + error);
            }
            return true;
        }
    }

    private static final String DB_URL = "jdbc:mysql://localhost:3306/facility";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Tranbien2809@";

    public boolean updateModelNameByEquipmentId(String equipmentId, String newModelName) {
        String modelId = null;

        // 🪵 Lấy modelId trước để debug
        String selectSql = "SELECT model_id FROM equipment_item WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, equipmentId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                modelId = rs.getString("model_id");
                System.out.println("✅ model_id from equipment_item: " + modelId);
            } else {
                System.out.println("❌ Không tìm thấy thiết bị với id = " + equipmentId);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String updateSql = "UPDATE equipment_models SET name = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, newModelName);
            updateStmt.setString(2, modelId);
            int rows = updateStmt.executeUpdate();
            System.out.println("🔧 Update model name rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTypeNameByEquipmentId(String equipmentId, String newTypeName) {
        String sql = """
        UPDATE equipment_type
        SET name = ?
        WHERE id = (
            SELECT type_id FROM equipment_models
            WHERE id = (
                SELECT model_id FROM equipment_item WHERE id = ?
            )
        )
    """;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newTypeName);
            stmt.setString(2, equipmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearDefaultRoomByEquipmentId(String equipmentId) {
        String sql = "UPDATE equipment_item SET default_room_id = NULL WHERE id = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, equipmentId);

                int rowsUpdated = stmt.executeUpdate();
                System.out.println("🔧 Rows updated in equipment_item (default_room_id): " + rowsUpdated);
                return rowsUpdated > 0;
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Lỗi khi xóa default_room_id: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDefaultRoomByEquipmentId(String equipmentId, String newRoomName) {
        String sql = """
        UPDATE equipment_item 
        SET default_room_id = (
            SELECT id FROM room WHERE name = ?
        ) 
        WHERE id = ?
    """;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, newRoomName);
                stmt.setString(2, equipmentId);

                int rowsUpdated = stmt.executeUpdate();
                System.out.println("🔄 Updated default_room_id rows: " + rowsUpdated);
                return rowsUpdated > 0;
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật default_room_id: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSerialNumberByEquipmentId(String equipmentId, String newSerialNumber) {
        String sql = "UPDATE equipment_item SET serial_number = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newSerialNumber);
            stmt.setString(2, equipmentId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật serial_number: " + e.getMessage());
            return false;
        }
    }

    public boolean updateImgUrlByEquipmentId(String equipmentId, String newImgUrl) {
        String sql = "UPDATE equipment_models SET image_url = ? WHERE id = (" +
                "SELECT model_id FROM equipment_item WHERE id = ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newImgUrl);
            stmt.setString(2, equipmentId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật img_url: " + e.getMessage());
            return false;
        }
    }

    public boolean createEquipment(Equipment equipment) throws IOException {
        String apiUrl = BASE_URL + "/equipments";

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = mapper.writeValueAsString(equipment);
        System.out.println("📦 JSON gửi lên API: " + json);

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        // ✅ Thêm Authorization header
        String token = TokenStorage.getToken(); // Bạn cần triển khai class SessionManager để lưu token sau khi đăng nhập
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
            System.out.println("🔑 Token được gửi: Bearer " + token);
        } else {
            System.out.println("⚠️ Không tìm thấy token! Có thể bạn chưa đăng nhập hoặc chưa lưu token.");
        }

        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();
        System.out.println("📥 Mã phản hồi: " + responseCode + " - " + responseMessage);

        if (responseCode != 200 && responseCode != 201) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                System.out.println("📥 Lỗi phản hồi từ server:");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }

        return responseCode == 200 || responseCode == 201;
    }

    public List<EquipmentModel> getAllModels() {
        List<EquipmentModel> models = new ArrayList<>();

        String sql = "SELECT id, name FROM equipment_models"; // sửa lại tên bảng + cột

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EquipmentModel model = new EquipmentModel();
                model.setModelId(rs.getString("id"));
                model.setModelName(rs.getString("name"));
                models.add(model);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return models;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT id, name FROM room";  // thay 'rooms' và các cột theo DB bạn

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getString("id"));
                room.setName(rs.getString("name"));
                rooms.add(room);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public boolean deleteEquipmentByIdAndSerial(String id, String serialNumber) {
        String sql = "DELETE FROM equipment_item WHERE id = ? AND serial_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.setString(2, serialNumber);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}