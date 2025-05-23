package com.utc2.facilityui.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.utc2.facilityui.auth.TokenStorage; // Đảm bảo import TokenStorage
import com.utc2.facilityui.response.ApiSingleResponse;
import com.utc2.facilityui.response.NotificationResponse;
import com.utc2.facilityui.response.Page;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class NotificationApiService {

    private static final String API_BASE_URL = "http://localhost:8080/facility/api/notifications";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NotificationApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private HttpRequest.Builder createAuthenticatedRequestBuilder(String url) {
        String token = TokenStorage.getToken(); // Lấy token
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json");

        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    public CompletableFuture<ApiSingleResponse<Page<NotificationResponse>>> getAllNotifications(String userId, int page, int size) {
        String url = API_BASE_URL + "?page=" + page + "&size=" + size;
        if (userId != null && !userId.trim().isEmpty()) {
            url += "&userId=" + userId;
        }

        HttpRequest request = createAuthenticatedRequestBuilder(url) // Sử dụng builder đã có xác thực
                .GET()
                .build();

        // Phần còn lại của phương thức giữ nguyên như bạn đã cung cấp
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(httpResponse -> {
                    try {
                        if (httpResponse.statusCode() == 200 && httpResponse.body() != null && !httpResponse.body().isEmpty()) {
                            var apiResponseTypeRef = new TypeReference<ApiSingleResponse<Page<NotificationResponse>>>() {};
                            return objectMapper.readValue(httpResponse.body(), apiResponseTypeRef);
                        } else {
                            ApiSingleResponse<Page<NotificationResponse>> errorResponse = new ApiSingleResponse<>();
                            errorResponse.setCode(httpResponse.statusCode());
                            String responseBodyContent = httpResponse.body() != null ? httpResponse.body() : "No response body";
                            try {
                                ApiSingleResponse<?> parsedError = objectMapper.readValue(httpResponse.body(), ApiSingleResponse.class);
                                errorResponse.setMessage(parsedError.getMessage() != null ? parsedError.getMessage() : "Lỗi không xác định từ server.");
                            } catch (Exception e) {
                                errorResponse.setMessage("Không thể lấy thông báo, mã lỗi HTTP: " + httpResponse.statusCode() + ", body: " + responseBodyContent);
                            }
                            System.err.println("NotificationApiService: Failed to fetch notifications (HTTP " + httpResponse.statusCode() + "): " + errorResponse.getMessage());
                            return errorResponse;
                        }
                    } catch (Exception e) {
                        System.err.println("NotificationApiService: Exception processing notifications page response: " + e.getMessage());
                        e.printStackTrace();
                        ApiSingleResponse<Page<NotificationResponse>> exceptionResponse = new ApiSingleResponse<>();
                        exceptionResponse.setCode(500);
                        exceptionResponse.setMessage("Lỗi xử lý phản hồi từ server: " + e.getMessage());
                        return exceptionResponse;
                    }
                });
    }

    public CompletableFuture<ApiSingleResponse<NotificationResponse>> markAsRead(String notificationId) {
        HttpRequest request = createAuthenticatedRequestBuilder(API_BASE_URL + "/" + notificationId + "/read") // Sử dụng builder
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        // Phần còn lại của phương thức giữ nguyên
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(httpResponse -> {
                    try {
                        if (httpResponse.statusCode() == 200 && httpResponse.body() != null && !httpResponse.body().isEmpty()) {
                            var apiResponseTypeRef = new TypeReference<ApiSingleResponse<NotificationResponse>>() {};
                            return objectMapper.readValue(httpResponse.body(), apiResponseTypeRef);
                        } else {
                            ApiSingleResponse<NotificationResponse> errorResponse = new ApiSingleResponse<>();
                            errorResponse.setCode(httpResponse.statusCode());
                            String responseBodyContent = httpResponse.body() != null ? httpResponse.body() : "No response body";
                            try {
                                ApiSingleResponse<?> parsedError = objectMapper.readValue(responseBodyContent, ApiSingleResponse.class);
                                errorResponse.setMessage(parsedError.getMessage() != null ? parsedError.getMessage() : "Lỗi không xác định từ server.");
                            } catch (Exception e) {
                                errorResponse.setMessage("Không thể đánh dấu đã đọc, mã lỗi HTTP: " + httpResponse.statusCode() + ", body: " + responseBodyContent);
                            }
                            System.err.println("NotificationApiService: Failed to mark as read (HTTP " + httpResponse.statusCode() + "): " + errorResponse.getMessage());
                            return errorResponse;
                        }
                    } catch (Exception e) {
                        System.err.println("NotificationApiService: Exception processing mark as read response: " + e.getMessage());
                        ApiSingleResponse<NotificationResponse> exceptionResponse = new ApiSingleResponse<>();
                        exceptionResponse.setCode(500);
                        exceptionResponse.setMessage("Lỗi xử lý phản hồi từ server: " + e.getMessage());
                        return exceptionResponse;
                    }
                });
    }

    public CompletableFuture<ApiSingleResponse<Object>> deleteNotification(String notificationId) {
        HttpRequest request = createAuthenticatedRequestBuilder(API_BASE_URL + "/" + notificationId) // Sử dụng builder
                .DELETE()
                .build();

        // Phần còn lại của phương thức giữ nguyên
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(httpResponse -> {
                    try {
                        String responseBodyContent = httpResponse.body() != null ? httpResponse.body() : "";
                        if (httpResponse.statusCode() == 200 && !responseBodyContent.isEmpty()) {
                            var apiResponseTypeRef = new TypeReference<ApiSingleResponse<Object>>() {};
                            return objectMapper.readValue(responseBodyContent, apiResponseTypeRef);
                        } else if (httpResponse.statusCode() == 204) {
                            ApiSingleResponse<Object> successResponse = new ApiSingleResponse<>();
                            successResponse.setCode(0);
                            successResponse.setMessage("Thông báo đã được xóa thành công (HTTP 204).");
                            return successResponse;
                        }
                        else {
                            ApiSingleResponse<Object> errorResponse = new ApiSingleResponse<>();
                            errorResponse.setCode(httpResponse.statusCode());
                            try {
                                ApiSingleResponse<?> parsedError = objectMapper.readValue(responseBodyContent, ApiSingleResponse.class);
                                errorResponse.setMessage(parsedError.getMessage() != null ? parsedError.getMessage() : "Lỗi không xác định từ server.");
                            } catch (Exception e){
                                errorResponse.setMessage("Không thể xóa thông báo, mã lỗi HTTP: " + httpResponse.statusCode() + ", body: " + responseBodyContent);
                            }
                            System.err.println("NotificationApiService: Failed to delete notification (HTTP " + httpResponse.statusCode() + "): " + errorResponse.getMessage());
                            return errorResponse;
                        }
                    } catch (Exception e) {
                        System.err.println("NotificationApiService: Exception processing delete notification response: " + e.getMessage());
                        ApiSingleResponse<Object> exceptionResponse = new ApiSingleResponse<>();
                        exceptionResponse.setCode(httpResponse.statusCode() != 0 ? httpResponse.statusCode() : 500);
                        exceptionResponse.setMessage("Lỗi xử lý phản hồi xóa từ server: " + e.getMessage());
                        return exceptionResponse;
                    }
                });
    }

    public CompletableFuture<ApiSingleResponse<Object>> sendOverdueReminder(String bookingId) {
        String url = API_BASE_URL + "/overdue-reminder?bookingId=" + bookingId;
        HttpRequest request = createAuthenticatedRequestBuilder(url) // Sử dụng builder
                .GET()
                .build();

        // Phần còn lại của phương thức giữ nguyên
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(httpResponse -> {
                    try {
                        String responseBodyContent = httpResponse.body() != null ? httpResponse.body() : "";
                        if (httpResponse.statusCode() == 200 && !responseBodyContent.isEmpty()) {
                            var apiResponseTypeRef = new TypeReference<ApiSingleResponse<Object>>() {};
                            return objectMapper.readValue(responseBodyContent, apiResponseTypeRef);
                        } else {
                            ApiSingleResponse<Object> errorResponse = new ApiSingleResponse<>();
                            errorResponse.setCode(httpResponse.statusCode());
                            try {
                                ApiSingleResponse<?> parsedError = objectMapper.readValue(responseBodyContent, ApiSingleResponse.class);
                                errorResponse.setMessage(parsedError.getMessage() != null ? parsedError.getMessage() : "Lỗi không xác định từ server.");
                            } catch (Exception e) {
                                errorResponse.setMessage("Không thể gửi nhắc nhở, mã lỗi HTTP: " + httpResponse.statusCode() + ", body: " + responseBodyContent);
                            }
                            System.err.println("NotificationApiService: Failed to send overdue reminder (HTTP " + httpResponse.statusCode() + "): " + errorResponse.getMessage());
                            return errorResponse;
                        }
                    } catch (Exception e) {
                        System.err.println("NotificationApiService: Exception processing send overdue reminder response: " + e.getMessage());
                        ApiSingleResponse<Object> exceptionResponse = new ApiSingleResponse<>();
                        exceptionResponse.setCode(httpResponse.statusCode() != 0 ? httpResponse.statusCode() : 500);
                        exceptionResponse.setMessage("Lỗi xử lý phản hồi gửi nhắc nhở: " + e.getMessage());
                        return exceptionResponse;
                    }
                });
    }
}