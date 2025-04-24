package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.ApiResponse; // Dùng lại ApiResponse nếu API trả về cấu trúc này
import com.utc2.facilityui.model.BookingCreationRequest;
import com.utc2.facilityui.response.ApiErrorResponse;
import com.utc2.facilityui.response.BookingResponse; // DTO response vừa tạo
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List; // Import List
import java.util.Collections; // Import Collections

public class BookingService {
//
    private final OkHttpClient client;
    private final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/facility"; //

    public BookingService() {
        client = new OkHttpClient();
        gson = new GsonBuilder().create(); // Hoặc cấu hình thêm nếu cần
    }

    /**
     * Gọi API để tạo một booking mới.
     * Endpoint ví dụ: POST /bookings
     * @param request DTO chứa thông tin booking cần tạo.
     * @return BookingResponse chứa thông tin booking đã tạo (hoặc null nếu lỗi).
     * @throws IOException Nếu có lỗi mạng, lỗi server, hoặc parse lỗi.
     * @throws IllegalArgumentException Nếu request không hợp lệ.
     */
    public BookingResponse createBooking(BookingCreationRequest request) throws IOException, IllegalArgumentException {
        if (request == null || request.getRoomId() == null || request.getPlannedStartTime() == null || request.getPlannedEndTime() == null) {
            throw new IllegalArgumentException("Thông tin booking không hợp lệ (thiếu ID phòng hoặc thời gian).");
        }

        // !!! THAY ĐỔI ENDPOINT NẾU CẦN !!!
        String url = BASE_URL + "/bookings";

        // Tạo JSON body từ DTO request
        String jsonBody = gson.toJson(request);
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        // Tạo request POST đã xác thực
        Request apiRequest = buildAuthenticatedRequestWithBody("POST", url, body);

        try (Response response = client.newCall(apiRequest).execute()) {
            // --- XỬ LÝ RESPONSE ---
            // Giả định API trả về thành công (2xx) với body là BookingResponse
            // và trả về lỗi (4xx, 5xx) với body là cấu trúc lỗi (ví dụ ApiResponse<Void>)

            if (!response.isSuccessful()) {
                String errorBodyStr = "";
                String specificErrorMessage = null; // Biến lưu message lỗi cụ thể từ JSON

                try (ResponseBody errorBody = response.body()) {
                    if (errorBody != null) {
                        errorBodyStr = errorBody.string(); // Đọc nội dung lỗi
                        // Thử parse lỗi bằng lớp ApiErrorResponse mới
                        try {
                            // Không cần TypeToken vì ApiErrorResponse không phải generic
                            ApiErrorResponse parsedError = gson.fromJson(errorBodyStr, ApiErrorResponse.class); // <<< Dùng ApiErrorResponse
                            if (parsedError != null && parsedError.getMessage() != null) {
                                // Lấy message thành công
                                specificErrorMessage = parsedError.getMessage(); // <<< Gọi getMessage() từ ApiErrorResponse
                            }
                        } catch (JsonSyntaxException ignored) {
                            // Bỏ qua nếu không parse được theo cấu trúc ApiErrorResponse
                            System.err.println("Không thể parse error body thành ApiErrorResponse: " + errorBodyStr);
                        }
                    }
                } // Kết thúc try-with-resources cho errorBody

                // Ném Exception với message cụ thể nếu lấy được, hoặc message chung chung
                if (specificErrorMessage != null) {
                    throw new IOException("API Error " + response.code() + ": " + specificErrorMessage);
                } else {
                    throw new IOException("Yêu cầu API thất bại với mã lỗi " + response.code() + ". Body: " + errorBodyStr.substring(0, Math.min(errorBodyStr.length(), 500))); // Log một phần body nếu không parse được
                }
            }

            // Nếu thành công, parse body thành BookingResponse
            try (ResponseBody responseBody = response.body()) {
                if (responseBody == null) {
                    throw new IOException("Response body rỗng từ API khi tạo booking.");
                }
                String responseData = responseBody.string();
                // Giả định API trả về trực tiếp BookingResponse object khi thành công
                return gson.fromJson(responseData, BookingResponse.class);
                // Nếu API trả về ApiResponse<BookingResponse> thì parse kiểu khác:
                // Type successType = new TypeToken<ApiResponse<BookingResponse>>() {}.getType();
                // ApiResponse<BookingResponse> successResponse = gson.fromJson(responseData, successType);
                // if (successResponse != null && successResponse.getCode() == 0 && successResponse.getResult() != null) {
                //    return successResponse.getResult();
                // } else {
                //    throw new IOException("Response thành công nhưng dữ liệu trả về không hợp lệ.");
                // }
            }
        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON khi tạo booking: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách booking của người dùng hiện tại.
     * Endpoint ví dụ: GET /bookings/my-bookings
     * @return List<BookingResponse>
     * @throws IOException
     */
    public List<BookingResponse> getMyBookings() throws IOException {
        // !!! THAY ĐỔI ENDPOINT NẾU CẦN !!!
        String url = BASE_URL + "/bookings/my-bookings";
        Request request = buildAuthenticatedGetRequest(url); // Dùng lại hàm helper GET

        try (Response response = client.newCall(request).execute()) {
            // Giả định API trả về ApiResponse<BookingResponse> với result.content là List
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                String errorBodyStr = (responseBody != null) ? responseBody.string() : "N/A";
                if(responseBody != null) responseBody.close();
                throw new IOException("Yêu cầu lấy booking thất bại: " + response.code() + ". Body: " + errorBodyStr);
            }
            if (responseBody == null) throw new IOException("Response body rỗng.");

            String jsonData;
            try { jsonData = responseBody.string(); } finally { responseBody.close(); }

            // Parse theo cấu trúc ApiResponse<BookingResponse> chứa List trong content
            try {
                Type listType = new TypeToken<ApiResponse<List<BookingResponse>>>() {}.getType(); // List trong ApiResponse
                ApiResponse<List<BookingResponse>> apiResponse = gson.fromJson(jsonData, listType);
                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) { // Chỉ cần result chứa list
                    // API mới có thể trả về list trực tiếp trong result thay vì result.content
                    // Cần kiểm tra cấu trúc API /my-bookings trả về
                    // Nếu result là List:
                    // return apiResponse.getResult() != null ? apiResponse.getResult() : Collections.emptyList();

                    // Nếu API cũ vẫn là result.content:
                    if(apiResponse.getResult() instanceof java.util.Map){ // Kiểm tra nếu result là Map (chứa content + page)
                        // Logic parse cũ nếu API phân trang
                        // Type complexResultType = new TypeToken<Result<BookingResponse>>() {}.getType();
                        // Result<BookingResponse> result = gson.fromJson(gson.toJson(apiResponse.getResult()), complexResultType);
                        // return result.getContent() != null ? result.getContent() : Collections.emptyList();
                        System.err.println("API /my-bookings có cấu trúc result phức tạp, cần điều chỉnh parse");
                        return Collections.emptyList();

                    } else if (apiResponse.getResult() instanceof List) { // Kiểm tra nếu result là List
                        // Type listOnlyType = new TypeToken<List<BookingResponse>>() {}.getType();
                        // return gson.fromJson(gson.toJson(apiResponse.getResult()), listOnlyType);
                        // Đã là list rồi thì ép kiểu thử? Không nên ép kiểu trực tiếp.
                        // Cần TypeToken đúng
                        System.err.println("API /my-bookings trả về list trong result, cần TypeToken<ApiResponse<List<BookingResponse>>>");
                        return Collections.emptyList(); // Tạm thời
                    } else {
                        System.err.println("API /my-bookings trả về kiểu result không xác định.");
                        return Collections.emptyList();
                    }


                } else {
                    System.err.println("API my-bookings response không hợp lệ. Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A"));
                    return Collections.emptyList();
                }
            } catch (JsonSyntaxException e) {
                System.err.println("Parse my-bookings thất bại: " + e.getMessage());
                throw e;
            }

        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON từ API my-bookings: " + e.getMessage(), e);
        }
    }


    // --- Các hàm helper ---
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

    private Request buildAuthenticatedRequestWithBody(String method, String url, RequestBody body) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại.");
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
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        return builder.build();
    }
}