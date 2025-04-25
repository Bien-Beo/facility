package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.BookingCreationRequest;
import com.utc2.facilityui.model.Result; // Lớp Result wrapper
import com.utc2.facilityui.response.ApiErrorResponse;
import com.utc2.facilityui.response.ApiResponse;
import com.utc2.facilityui.response.BookingResponse; // DTO cho một booking
import com.utc2.facilityui.utils.LocalDateTimeAdapter; // Adapter cho Gson
import okhttp3.*; // OkHttp cho việc gọi API

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

public class BookingService {

    private final OkHttpClient client;
    private final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/facility";

    public BookingService() {
        client = new OkHttpClient();
        // Không cần @Expose nếu không dùng excludeFieldsWithoutExposeAnnotation()
        // Có thể thêm SerializedName vào ApiResponse mới nếu tên biến khác JSON key
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    /**
     * Gửi yêu cầu tạo một booking mới lên API.
     * (Giả sử API tạo booking trả về BookingResponse trực tiếp, không qua ApiResponse/Result)
     * => Phần này giữ nguyên, không bị ảnh hưởng bởi thay đổi ApiResponse cho getMyBookings
     */
    public BookingResponse createBooking(BookingCreationRequest request) throws IOException, IllegalArgumentException {
        // ... (Code phần createBooking giữ nguyên) ...
        if (request == null || request.getRoomId() == null || request.getPlannedStartTime() == null || request.getPlannedEndTime() == null) {
            throw new IllegalArgumentException("Thông tin booking không hợp lệ (thiếu ID phòng hoặc thời gian).");
        }

        String url = BASE_URL + "/booking";
        String jsonBody = gson.toJson(request);
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
        Request apiRequest = buildAuthenticatedRequestWithBody("POST", url, body);

        System.out.println("Sending POST request to: " + url);
        System.out.println("Request Body: " + jsonBody);

        try (Response response = client.newCall(apiRequest).execute()) {
            System.out.println("Response Code: " + response.code());

            if (!response.isSuccessful()) {
                String errorBodyStr = "";
                String specificErrorMessage = null;
                try (ResponseBody errorBody = response.body()) {
                    if (errorBody != null) {
                        errorBodyStr = errorBody.string();
                        try {
                            ApiErrorResponse parsedError = gson.fromJson(errorBodyStr, ApiErrorResponse.class);
                            if (parsedError != null && parsedError.getMessage() != null) {
                                specificErrorMessage = parsedError.getMessage();
                            }
                        } catch (JsonSyntaxException ignored) {
                            System.err.println("Không thể parse error body thành ApiErrorResponse: " + errorBodyStr.substring(0, Math.min(errorBodyStr.length(), 200)) + "...");
                        }
                    }
                }

                if (specificErrorMessage != null) {
                    throw new IOException("API Error " + response.code() + ": " + specificErrorMessage);
                } else {
                    throw new IOException("Yêu cầu API thất bại với mã lỗi " + response.code() + ". Body: " + errorBodyStr.substring(0, Math.min(errorBodyStr.length(), 500)));
                }
            }

            try (ResponseBody responseBody = response.body()) {
                if (responseBody == null) {
                    throw new IOException("Response body rỗng từ API khi tạo booking.");
                }
                String responseData = responseBody.string();
                System.out.println("Response Body: " + responseData);
                // API tạo booking thường trả về BookingResponse trực tiếp
                return gson.fromJson(responseData, BookingResponse.class);
            }

        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON khi tạo booking: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách các booking của người dùng hiện tại từ API.
     * *** ĐÃ CẬP NHẬT THEO ApiResponse MỚI (result là Result<T>) ***
     *
     * @return Một List các đối tượng BookingResponse.
     * @throws IOException Nếu có lỗi mạng, parse, hoặc lỗi logic từ API.
     */
    public List<BookingResponse> getMyBookings() throws IOException {
        String url = BASE_URL + "/booking/my";
        Request request = buildAuthenticatedGetRequest(url);

        System.out.println("Sending GET request to: " + url);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code: " + response.code());

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                if (!response.isSuccessful()) { throw new IOException("Yêu cầu lấy booking thất bại: " + response.code() + " và không có response body."); }
                else { System.err.println("API my-bookings trả về response thành công nhưng body rỗng."); return Collections.emptyList(); }
            }

            String jsonData = "";
            try {
                jsonData = responseBody.string();
            } finally {
                responseBody.close();
            }
            System.out.println("Raw JSON Response: " + jsonData);

            if (!response.isSuccessful()) {
                throw new IOException("Yêu cầu lấy booking thất bại: " + response.code() + ". Body: " + jsonData.substring(0, Math.min(jsonData.length(), 500)));
            }

            // Parse JSON nếu HTTP request thành công (2xx)
            try {
                // *** 1. SỬA TypeToken ***
                // Vì ApiResponse<T> chứa Result<T>, và Result<T> chứa List<BookingResponse> (List<T>),
                // nên T ở đây phải là BookingResponse.
                Type apiResponseType = new TypeToken<ApiResponse<BookingResponse>>() {}.getType();
                ApiResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                // Kiểm tra mã lỗi logic và dữ liệu trả về từ API
                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {

                    // *** 2. Lấy đối tượng Result<BookingResponse> ***
                    // apiResponse.getResult() giờ trả về Result<BookingResponse> theo định nghĩa mới
                    Result<BookingResponse> resultData = apiResponse.getResult();

                    // *** 3. Lấy List<BookingResponse> từ content của Result ***
                    // resultData.getContent() trả về List<T>, tức là List<BookingResponse>
                    if (resultData.getContent() != null) {
                        return resultData.getContent(); // Trả về danh sách booking thành công
                    } else {
                        // Có result nhưng không có content? Coi như không có dữ liệu.
                        System.err.println("Không có dữ liệu booking (content) trong response result.");
                        return Collections.emptyList();
                    }
                } else {
                    // API trả về HTTP 2xx nhưng có mã lỗi logic (code != 0) hoặc result null
                    String message = "Không rõ lỗi logic"; // Cần thêm getMessage() vào ApiResponse mới nếu API trả về
                    // if (apiResponse != null && apiResponse.getMessage() != null) message = apiResponse.getMessage();
                    int code = (apiResponse != null) ? apiResponse.getCode() : -1;
                    System.err.println("API my-bookings response không thành công về mặt logic. Code: " + code + ", Message: " + message);
                    throw new IOException("API my-bookings trả về lỗi logic: " + message + " (Code: " + code + ")");
                }
            } catch (JsonSyntaxException e) {
                System.err.println("Raw JSON gây lỗi parse: " + jsonData.substring(0, Math.min(jsonData.length(), 500)) + "...");
                throw new IOException("Lỗi parse JSON từ API my-bookings: " + e.getMessage(), e);
            }
        }
        // Các IOException khác (lỗi mạng, lỗi token...) sẽ được ném ra từ các phần khác
    }

    /**
     * Xây dựng một GET request đã được xác thực (thêm Authorization header).
     */
    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        // ... (Code phần buildAuthenticatedGetRequest giữ nguyên) ...
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
     * Xây dựng một request (POST, PUT, PATCH, DELETE) có body và đã được xác thực.
     */
    private Request buildAuthenticatedRequestWithBody(String method, String url, RequestBody body) throws IOException {
        // ... (Code phần buildAuthenticatedRequestWithBody giữ nguyên) ...
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại hoặc đã hết hạn.");
        }
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token);

        switch (method.toUpperCase()) {
            case "POST":
                builder.post(body);
                break;
            case "PUT":
                builder.put(body);
                break;
            case "PATCH":
                builder.patch(body);
                break;
            case "DELETE":
                if (body != null) builder.delete(body);
                else builder.delete();
                break;
            default:
                throw new IllegalArgumentException("Phương thức HTTP không được hỗ trợ: " + method);
        }
        return builder.build();
    }
}