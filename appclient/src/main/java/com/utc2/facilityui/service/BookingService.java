package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.ApiResponse;
import com.utc2.facilityui.model.BookingCreationRequest;
import com.utc2.facilityui.model.Result;
import com.utc2.facilityui.response.ApiErrorResponse;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.utils.LocalDateTimeAdapter;
import okhttp3.*;

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
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
    public BookingResponse createBooking(BookingCreationRequest request) throws IOException, IllegalArgumentException {
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

            // Đoạn code bạn cung cấp được đặt ở đây
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
                            System.err.println("Không thể parse error body thành ApiErrorResponse: " + errorBodyStr);
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
                return gson.fromJson(responseData, BookingResponse.class);
            }

        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON khi tạo booking: " + e.getMessage(), e);
        }
    }

    public List<Result<BookingResponse>> getMyBookings() throws IOException {
        String url = BASE_URL + "/booking/my";

        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                String errorBodyStr = (responseBody != null) ? responseBody.string() : "N/A";
                if (responseBody != null) responseBody.close();
                throw new IOException("Yêu cầu lấy booking thất bại: " + response.code() + ". Body: " + errorBodyStr);
            }
            if (responseBody == null) throw new IOException("Response body rỗng.");

            String jsonData;
            try {
                jsonData = responseBody.string();
                System.out.println("Raw JSON Response: " + jsonData); // Log the raw JSON
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }

            try {
                Type apiResponseType = new TypeToken<ApiResponse<Result<BookingResponse>>>() {
                }.getType();
                ApiResponse<Result<BookingResponse>> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    Result<Result<BookingResponse>> result = apiResponse.getResult();
                    if (result != null && result.getContent() != null) {
                        return result.getContent();
                    } else {
                        System.err.println("Không có dữ liệu booking trong response.");
                        return Collections.emptyList();
                    }
                } else {
                    System.err.println("API my-bookings response không hợp lệ.");
                    return Collections.emptyList();
                }
            } catch (JsonSyntaxException e) {
                throw new IOException("Lỗi parse JSON từ API my-bookings: " + e.getMessage(), e);
            }

        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON từ API my-bookings: " + e.getMessage(), e);
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