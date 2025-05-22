package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage; // Cần implement lớp này
import com.utc2.facilityui.model.BookingCreationRequest; // Cần nếu dùng createBooking
import com.utc2.facilityui.model.Result;
import com.utc2.facilityui.response.*;
import com.utc2.facilityui.utils.LocalDateTimeAdapter; // Cần implement lớp này
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BookingService {

    private final OkHttpClient client;
    private final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/facility"; // Điều chỉnh nếu cần
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public BookingService() {
        client = new OkHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    // createBooking giữ nguyên logic parse cũ (vẫn cần xác minh cấu trúc trả về)
    // Sử dụng ApiResponse<BookingResponse> và logic xử lý phức tạp bên trong
    public BookingResponse createBooking(BookingCreationRequest request) throws IOException, IllegalArgumentException {
        // ... (Code giữ nguyên như trước) ...
        if (request == null || request.getRoomId() == null || request.getPlannedStartTime() == null || request.getPlannedEndTime() == null) {
            throw new IllegalArgumentException("Thông tin booking không hợp lệ (thiếu ID phòng hoặc thời gian).");
        }
        String url = BASE_URL + "/booking";
        String jsonBody = gson.toJson(request);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request apiRequest = buildAuthenticatedRequestWithBody("POST", url, body);
        System.out.println("Sending POST request to: " + url);
        try (Response response = client.newCall(apiRequest).execute()) {
            System.out.println("Create Booking Response Code: " + response.code());
            String responseData = "";
            try (ResponseBody responseBody = response.body()) {
                if (responseBody != null) { responseData = responseBody.string(); }
            }
            if (!response.isSuccessful()) {
                String specificErrorMessage = null;
                if (!responseData.isEmpty()) {
                    try {
                        ApiErrorResponse parsedError = gson.fromJson(responseData, ApiErrorResponse.class);
                        if (parsedError != null && parsedError.getMessage() != null) {
                            specificErrorMessage = parsedError.getMessage();
                        }
                    } catch (JsonSyntaxException ignored) { }
                }
                if (specificErrorMessage != null) { throw new IOException("API Error " + response.code() + ": " + specificErrorMessage); }
                else { throw new IOException("Yêu cầu tạo booking thất bại: " + response.code() + ". Body: " + responseData.substring(0, Math.min(responseData.length(), 500))); }
            }
            if (responseData.isEmpty()) { throw new IOException("Response body rỗng từ API khi tạo booking thành công."); }
            try { return gson.fromJson(responseData, BookingResponse.class); }
            catch (JsonSyntaxException e) { throw new IOException("Lỗi parse JSON response khi tạo booking: " + e.getMessage() + ". JSON: " + responseData, e); }
        }
    }


    // getMyBookings giữ nguyên logic parse cũ (có thể không chính xác)
    // Sử dụng ApiResponse<BookingResponse> và cố gắng xử lý Result bên trong
    public List<BookingResponse> getMyBookings() throws IOException {
        String url = BASE_URL + "/booking/my";
        Request request = buildAuthenticatedGetRequest(url);
        System.out.println("Sending GET request (My Bookings) to: " + url);
        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code (My Bookings): " + response.code());
            String jsonData = getResponseBody(response);
            if (!response.isSuccessful()) { parseAndThrowError(response, jsonData, "Yêu cầu lấy 'my bookings' thất bại"); }
            // ** Logic parse cũ cho getMyBookings **
            try {
                // Vẫn dùng ApiResponse<BookingResponse> như phiên bản p4
                Type apiResponseType = new TypeToken<ApiResponse<BookingResponse>>() {}.getType();
                ApiResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    // Cố gắng xử lý Result<BookingResponse> bên trong result của ApiResponse<BookingResponse>
                    // Logic này có thể không đúng nếu T của ApiResponse thực sự là Result<BookingResponse>
                    Object rawResult = apiResponse.getResult();
                    if (rawResult instanceof Result) { // Kiểm tra xem result có phải là kiểu Result không
                        Result<?> resultData = (Result<?>) rawResult;
                        if (resultData.getContent() != null && !resultData.getContent().isEmpty()) {
                            // Cố gắng ép kiểu các phần tử trong content thành BookingResponse
                            return resultData.getContent().stream()
                                    .map(item -> {
                                        try {
                                            // Nếu item đã là BookingResponse thì tốt
                                            if (item instanceof BookingResponse) return (BookingResponse) item;
                                            // Nếu không, thử parse lại từ JSON (phòng trường hợp nó là Map)
                                            String itemJson = gson.toJson(item);
                                            return gson.fromJson(itemJson, BookingResponse.class);
                                        } catch (Exception e) {
                                            System.err.println("Không thể chuyển đổi item thành BookingResponse trong getMyBookings: " + item);
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());
                        } else { return Collections.emptyList(); } // Content rỗng
                    } else {
                        // Nếu result không phải là Result (ví dụ API trả về sai cấu trúc)
                        System.err.println("Cấu trúc 'result' không mong đợi trong getMyBookings (không phải là Result).");
                        return Collections.emptyList();
                    }
                } else {
                    int code = (apiResponse != null) ? apiResponse.getCode() : -1;
                    throw new IOException("API my-bookings trả về lỗi logic (Code: " + code + ")");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API my-bookings");
                throw e;
            }
        }
    }

    // getAllBookings giữ nguyên logic parse cũ (có thể không chính xác)
    // Sử dụng ApiResponse<BookingResponse> và cố gắng xử lý Result bên trong
    public List<BookingResponse> getAllBookings() throws IOException {
        String url = BASE_URL + "/booking";
        Request request = buildAuthenticatedGetRequest(url);
        System.out.println("Sending GET request (Admin - All Bookings) to: " + url);
        try (Response response = client.newCall(request).execute()) {
            String jsonData = getResponseBody(response);
            if (!response.isSuccessful()) { parseAndThrowError(response, jsonData, "Yêu cầu lấy tất cả booking thất bại"); }
            // ** Logic parse cũ cho getAllBookings **
            try {
                // Vẫn dùng ApiResponse<BookingResponse> như phiên bản p4
                Type apiResponseType = new TypeToken<ApiResponse<BookingResponse>>() {}.getType();
                ApiResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    Object rawResult = apiResponse.getResult();
                    if (rawResult instanceof Result) {
                        Result<?> resultData = (Result<?>) rawResult;
                        if (resultData.getContent() != null && !resultData.getContent().isEmpty()) {
                            return resultData.getContent().stream()
                                    .map(item -> {
                                        try {
                                            if (item instanceof BookingResponse) return (BookingResponse) item;
                                            String itemJson = gson.toJson(item);
                                            return gson.fromJson(itemJson, BookingResponse.class);
                                        } catch (Exception e) {
                                            System.err.println("Không thể chuyển đổi item thành BookingResponse trong getAllBookings: " + item);
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());
                        } else { return Collections.emptyList(); }
                    } else {
                        System.err.println("Cấu trúc 'result' không mong đợi trong getAllBookings (không phải là Result).");
                        return Collections.emptyList();
                    }
                } else {
                    int code = (apiResponse != null) ? apiResponse.getCode() : -1;
                    throw new IOException("API /booking trả về lỗi logic (Code: " + code + ")");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking");
                throw e;
            }
        }
    }

    /**
     * Duyệt một booking. Endpoint: PUT /facility/booking/{bookingId}/approve
     * **Sử dụng ApiSingleResponse để parse kết quả**
     */
    public BookingResponse approveBooking(String bookingId) throws IOException {
        String url = BASE_URL + "/booking/" + bookingId + "/approve";
        RequestBody body = RequestBody.create(new byte[0]);
        Request request = buildAuthenticatedRequestWithBody("PUT", url, body);
        System.out.println("Sending PUT request (Approve Booking) to: " + url);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code (Approve Booking): " + response.code());
            String jsonData = getResponseBody(response);

            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Yêu cầu duyệt booking thất bại");
            }

            try {
                // *** THAY ĐỔI: Sử dụng TypeToken với ApiSingleResponse<BookingResponse> ***
                Type apiResponseType = new TypeToken<ApiSingleResponse<BookingResponse>>() {}.getType();
                ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    // *** THAY ĐỔI: Kết quả nằm trực tiếp trong apiResponse.getResult() ***
                    return apiResponse.getResult();
                } else {
                    int code = (apiResponse != null) ? apiResponse.getCode() : -1;
                    String message = (apiResponse != null) ? apiResponse.getMessage() : "Unknown error";
                    System.err.println("API approve response không thành công về mặt logic. Code: " + code + ", Message: " + message);
                    throw new IOException("API approve trả về lỗi logic (Code: " + code + ", Message: " + message + ")");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API approve");
                throw e;
            }
        }
    }

    /**
     * Từ chối một booking. Endpoint: PUT /facility/booking/{bookingId}/reject
     * **Sử dụng ApiSingleResponse để parse kết quả**
     */
    public BookingResponse rejectBooking(String bookingId) throws IOException {
        String url = BASE_URL + "/booking/" + bookingId + "/reject";
        RequestBody body = RequestBody.create(new byte[0]);
        Request request = buildAuthenticatedRequestWithBody("PUT", url, body);
        System.out.println("Sending PUT request (Reject Booking) to: " + url);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code (Reject Booking): " + response.code());
            String jsonData = getResponseBody(response);

            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Yêu cầu từ chối booking thất bại");
            }

            try {
                // *** THAY ĐỔI: Sử dụng TypeToken với ApiSingleResponse<BookingResponse> ***
                Type apiResponseType = new TypeToken<ApiSingleResponse<BookingResponse>>() {}.getType();
                ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    // *** THAY ĐỔI: Kết quả nằm trực tiếp trong apiResponse.getResult() ***
                    return apiResponse.getResult();
                } else {
                    int code = (apiResponse != null) ? apiResponse.getCode() : -1;
                    String message = (apiResponse != null) ? apiResponse.getMessage() : "Unknown error";
                    System.err.println("API reject response không thành công về mặt logic. Code: " + code + ", Message: " + message);
                    throw new IOException("API reject trả về lỗi logic (Code: " + code + ", Message: " + message + ")");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API reject");
                throw e;
            }
        }
    }


    // --- Helper Methods (Giữ nguyên như phiên bản p4) ---
    private String getResponseBody(Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            if (responseBody != null) { return responseBody.string(); }
            else if (!response.isSuccessful()) { throw new IOException("Yêu cầu thất bại: " + response.code() + " và không có response body."); }
            else { System.out.println("API response thành công (" + response.code() + ") nhưng body rỗng."); return ""; }
        }
    }

    // Hàm parse lỗi này có thể cần cập nhật để ưu tiên thử parse bằng ApiSingleResponse trước
    // nếu cấu trúc lỗi của bạn thường theo kiểu đó. Tạm thời giữ nguyên.
    private void parseAndThrowError(Response response, String responseData, String baseErrorMessage) throws IOException {
        String specificErrorMessage = null;
        if (!responseData.isEmpty()) {
            try {
                // Ưu tiên thử parse bằng ApiSingleResponse vì nó có trường message
                ApiSingleResponse<?> singleError = gson.fromJson(responseData, ApiSingleResponse.class);
                if (singleError != null && singleError.getMessage() != null && !singleError.getMessage().isEmpty()) {
                    specificErrorMessage = singleError.getMessage();
                } else {
                    // Nếu không được, thử parse bằng ApiErrorResponse (nếu bạn có định nghĩa lớp này)
                    try {
                        ApiErrorResponse legacyError = gson.fromJson(responseData, ApiErrorResponse.class);
                        if (legacyError != null && legacyError.getMessage() != null) {
                            specificErrorMessage = legacyError.getMessage();
                        }
                    } catch (JsonSyntaxException ignored) {} // Bỏ qua nếu không phải cấu trúc này

                    // Không thử parse bằng ApiResponse nữa vì nó không có message
                }
            } catch (JsonSyntaxException ignored) {
                // Không thể parse lỗi, ghi log và sử dụng thông báo chung
                System.err.println("Không thể parse error response body thành cấu trúc lỗi đã biết: " + responseData.substring(0, Math.min(responseData.length(), 200)));
            }
        }
        // Xây dựng thông báo lỗi cuối cùng
        String finalMessage = baseErrorMessage + ": " + response.code();
        if (specificErrorMessage != null) {
            finalMessage += " - " + specificErrorMessage; // Thêm message lỗi cụ thể nếu tìm thấy
        } else if (!responseData.isEmpty() && !responseData.startsWith("<") && responseData.length() < 1000) { // Chỉ thêm body nếu nó ngắn và không phải HTML
            finalMessage += ". Body: " + responseData.substring(0, Math.min(responseData.length(), 200)) + "...";
        }
        throw new IOException(finalMessage); // Ném lỗi
    }

    private void handleJsonParsingError(JsonSyntaxException e, String jsonData, String apiName) {
        System.err.println("Lỗi parse JSON từ " + apiName + ": " + e.getMessage());
        System.err.println("Raw JSON gây lỗi parse: " + jsonData.substring(0, Math.min(jsonData.length(), 500)) + "...");
    }

    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) { throw new IOException("Token xác thực không tồn tại. Vui lòng đăng nhập lại."); }
        return new Request.Builder().url(url).header("Authorization", "Bearer " + token).get().build();
    }

    private Request buildAuthenticatedRequestWithBody(String method, String url, RequestBody body) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) { throw new IOException("Token xác thực không tồn tại. Vui lòng đăng nhập lại."); }
        Request.Builder builder = new Request.Builder().url(url).header("Authorization", "Bearer " + token);
        if ("PUT".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            builder.header("Content-Type", JSON.toString());
        }
        switch (method.toUpperCase()) {
            case "POST": builder.post(body); break;
            case "PUT": builder.put(body); break;
            case "PATCH": builder.patch(body); break;
            case "DELETE": if (body != null) builder.delete(body); else builder.delete(); break;
            default: throw new IllegalArgumentException("Phương thức HTTP không được hỗ trợ: " + method);
        }
        return builder.build();
    }

    public List<EquipmentResponse> getEquipmentsByBookingId(String bookingId) throws IOException {
        String url = BASE_URL + "/booking/" + bookingId + "/equipments";
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            String jsonData = getResponseBody(response);
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Lỗi lấy thiết bị theo booking");
            }

            Type listType = new TypeToken<List<EquipmentResponse>>(){}.getType();
            return gson.fromJson(jsonData, listType);
        }
    }
}