package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.BookingCreationRequest; // Cần cho createBooking
// Import các lớp response cần thiết
import com.utc2.facilityui.response.ApiPageResponse;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.response.ApiErrorResponse; // Giữ lại nếu dùng parseAndThrowError
import com.utc2.facilityui.response.ApiSingleResponse;
import com.utc2.facilityui.utils.LocalDateTimeAdapter; // Đảm bảo lớp này tồn tại và đúng
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

public class BookingService {

    private final OkHttpClient client;
    private final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/facility"; // Điều chỉnh nếu cần
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public BookingService() {
        client = new OkHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Đảm bảo adapter này hoạt động đúng
                .create();
    }

    /**
     * Creates a new booking by sending a request to the backend API.
     *
     * @param bookingRequest The booking creation request data.
     * @return The created BookingResponse object from the backend.
     * @throws IOException If network error or API returns an error.
     * @throws IllegalArgumentException If the input request is invalid.
     */
    public BookingResponse createBooking(BookingCreationRequest bookingRequest) throws IOException, IllegalArgumentException {
        if (bookingRequest == null || bookingRequest.getRoomId() == null || bookingRequest.getPlannedStartTime() == null || bookingRequest.getPlannedEndTime() == null) {
            // Basic validation, more can be added
            throw new IllegalArgumentException("Invalid booking request data (missing room ID or times).");
        }

        String url = BASE_URL + "/booking";
        String jsonBody = gson.toJson(bookingRequest); // Convert request DTO to JSON
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request apiRequest = buildAuthenticatedRequestWithBody("POST", url, body); // Use POST

        System.out.println("Sending POST request (Create Booking) to: " + url);
        System.out.println("Request Body JSON: " + jsonBody); // Log the request body

        try (Response response = client.newCall(apiRequest).execute()) {
            System.out.println("Response Code (Create Booking): " + response.code());
            String jsonData = getResponseBody(response);
            System.out.println("Response Body JSON: " + jsonData.substring(0, Math.min(jsonData.length(), 1000))); // Log response body


            if (!response.isSuccessful()) {
                // Error handling using existing helper
                parseAndThrowError(response, jsonData, "Failed to create booking");
            }

            // Parse the successful response
            try {
                // Backend returns ApiResponse<BookingResponse> which is like ApiSingleResponse<T>
                Type apiResponseType = new TypeToken<ApiSingleResponse<BookingResponse>>() {}.getType();
                ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getResult() != null) {
                    // Assuming backend code 0 means success if applicable, otherwise just check result != null
                    if (apiResponse.getCode() != 0) { // Optional: Check logical code if backend sends one
                        throw new IOException("API create booking returned logical error code " + apiResponse.getCode() + ": " + apiResponse.getMessage());
                    }
                    System.out.println("Successfully created and parsed booking: " + apiResponse.getResult().getId());
                    return apiResponse.getResult(); // Return the nested BookingResponse
                } else {
                    System.err.println("Parsed ApiSingleResponse or its result is null. JSON: " + jsonData.substring(0, Math.min(jsonData.length(), 500)));
                    throw new IOException("Failed to parse the expected booking structure from API response after creation.");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking (POST)");
                throw new IOException("Failed to parse successful JSON response: " + e.getMessage(), e);
            }
        }
    }


    /**
     * Fetches a paginated list of bookings from the backend API, applying optional filters.
     *
     * @param roomId Optional room ID filter.
     * @param month Optional month filter (1-12).
     * @param year Optional year filter.
     * @param userId Optional user ID filter.
     * @param page The page number to retrieve (0-indexed).
     * @param size The number of items per page.
     * @return A Page object containing the list of bookings and pagination info.
     * @throws IOException If network error or API returns an error.
     */
    public Page<BookingResponse> getAllBookings(String roomId, Integer month, Integer year, String userId, int page, int size) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/booking"))
                .newBuilder();

        if (roomId != null && !roomId.trim().isEmpty()) urlBuilder.addQueryParameter("roomId", roomId.trim());
        if (month != null && month >= 1 && month <= 12) urlBuilder.addQueryParameter("month", String.valueOf(month));
        if (year != null && year > 1900) urlBuilder.addQueryParameter("year", String.valueOf(year));
        if (userId != null && !userId.trim().isEmpty()) urlBuilder.addQueryParameter("userId", userId.trim());
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("size", String.valueOf(size));

        String url = urlBuilder.build().toString();
        Request request = buildAuthenticatedGetRequest(url);
        System.out.println("Sending GET request (Paged All Bookings) to: " + url);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code (Paged All Bookings): " + response.code());
            String jsonData = getResponseBody(response);

            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Failed to fetch all bookings");
            }

            try {
                Type apiResponseType = new TypeToken<ApiPageResponse<BookingResponse>>() {}.getType();
                ApiPageResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getResult() != null) {
                    System.out.println("Successfully parsed ApiPageResponse (All Bookings). Returning Page object.");
                    return apiResponse.getResult();
                } else {
                    System.err.println("Parsed ApiPageResponse or its result is null (All Bookings). JSON: " + jsonData.substring(0, Math.min(jsonData.length(), 500)));
                    throw new IOException("Failed to parse expected page structure (All Bookings).");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking (paged)");
                throw new IOException("Failed to parse JSON response (All Bookings): " + e.getMessage(), e);
            }
        }
    }

    /**
     * Fetches a paginated list of the current user's bookings from the backend API.
     *
     * @param page The page number to retrieve (0-indexed).
     * @param size The number of items per page.
     * @return A Page object containing the list of the user's bookings and pagination info.
     * @throws IOException If network error or API returns an error.
     */
    public Page<BookingResponse> getMyBookingsPaged(int page, int size) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/booking/my"))
                .newBuilder();

        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("size", String.valueOf(size));
        // urlBuilder.addQueryParameter("sort", "plannedStartTime,desc"); // Optional sorting

        String url = urlBuilder.build().toString();
        Request request = buildAuthenticatedGetRequest(url); // GET request
        System.out.println("Sending GET request (My Paged Bookings) to: " + url);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code (My Paged Bookings): " + response.code());
            String jsonData = getResponseBody(response);

            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Failed to fetch my bookings");
            }

            try {
                // Parse using the ApiPageResponse structure
                Type apiResponseType = new TypeToken<ApiPageResponse<BookingResponse>>() {}.getType();
                ApiPageResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getResult() != null) {
                    // Check backend's logical code if necessary
                    // if (apiResponse.getCode() != 0) { ... }
                    System.out.println("Successfully parsed ApiPageResponse for my bookings. Returning Page object.");
                    return apiResponse.getResult(); // Return the Page object
                } else {
                    System.err.println("Parsed ApiPageResponse or its result is null for my bookings. JSON: " + jsonData.substring(0, Math.min(jsonData.length(), 500)));
                    throw new IOException("Failed to parse the expected page structure for my bookings from API response.");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking/my (paged)");
                throw new IOException("Failed to parse JSON response for my bookings: " + e.getMessage(), e);
            }
        }
    }

    // ========================================================================
    // CÁC PHƯƠNG THỨC KHÁC (Approve, Reject, ...)
    // ========================================================================
    public BookingResponse approveBooking(String bookingId) throws IOException {
        String url = BASE_URL + "/booking/" + bookingId + "/approve";
        RequestBody body = RequestBody.create(new byte[0]); // Empty body for PUT often needed
        Request request = buildAuthenticatedRequestWithBody("PUT", url, body);
        System.out.println("Sending PUT request (Approve Booking) to: " + url);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code (Approve Booking): " + response.code());
            String jsonData = getResponseBody(response);

            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Failed to approve booking");
            }

            try {
                Type apiResponseType = new TypeToken<ApiSingleResponse<BookingResponse>>() {}.getType();
                ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getResult() != null) {
                    if (apiResponse.getCode() != 0) { // Check logical code
                        throw new IOException("API approve returned logical error code " + apiResponse.getCode() + ": " + apiResponse.getMessage());
                    }
                    return apiResponse.getResult();
                } else {
                    int code = (apiResponse != null) ? apiResponse.getCode() : -1;
                    String message = (apiResponse != null) ? apiResponse.getMessage() : "Unknown error";
                    System.err.println("API approve response logic error. Code: " + code + ", Message: " + message);
                    throw new IOException("API approve returned logical error (Code: " + code + ", Message: " + message + ")");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API approve");
                throw e;
            }
        }
    }

    /**
     * Rejects a booking request via the backend API.
     *
     * @param bookingId The ID of the booking to reject.
     * @return The updated BookingResponse object from the backend (usually with REJECTED status).
     * @throws IOException If network error or API returns an error.
     */
    public BookingResponse rejectBooking(String bookingId) throws IOException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty for rejection.");
        }

        String url = BASE_URL + "/booking/" + bookingId + "/reject";
        // PUT request often requires a body, even if empty.
        RequestBody body = RequestBody.create(new byte[0], null); // Empty body
        Request request = buildAuthenticatedRequestWithBody("PUT", url, body); // Use PUT

        System.out.println("Sending PUT request (Reject Booking) to: " + url);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code (Reject Booking): " + response.code());
            String jsonData = getResponseBody(response);
            System.out.println("Response Body JSON (Reject): " + jsonData.substring(0, Math.min(jsonData.length(), 1000))); // Log response body

            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Failed to reject booking");
            }

            // Parse the successful response using ApiSingleResponse structure
            try {
                // Giả sử backend trả về cấu trúc tương tự approve
                Type apiResponseType = new TypeToken<ApiSingleResponse<BookingResponse>>() {}.getType();
                ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getResult() != null) {
                    if (apiResponse.getCode() != 0) { // Optional check for logical errors
                        throw new IOException("API reject booking returned logical error code " + apiResponse.getCode() + ": " + apiResponse.getMessage());
                    }
                    System.out.println("Successfully rejected booking: " + apiResponse.getResult().getId());
                    return apiResponse.getResult(); // Return the updated BookingResponse
                } else {
                    System.err.println("Parsed ApiSingleResponse or its result is null for reject booking. JSON: " + jsonData.substring(0, Math.min(jsonData.length(), 500)));
                    throw new IOException("Failed to parse the expected booking structure from API response after rejection.");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking/{id}/reject (PUT)");
                throw new IOException("Failed to parse successful JSON response for reject booking: " + e.getMessage(), e);
            }
        }
    }

    // ========================================================================
    // CÁC PHƯƠNG THỨC HELPER (Giữ nguyên)
    // ========================================================================
    private String getResponseBody(Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            if (responseBody != null) { return responseBody.string(); }
            else if (!response.isSuccessful()) { throw new IOException("Request failed: " + response.code() + " and response body is null."); }
            else { System.out.println("API response successful (" + response.code() + ") but body is null."); return ""; }
        }
    }

    private void parseAndThrowError(Response response, String responseData, String baseErrorMessage) throws IOException {
        String specificErrorMessage = null;
        if (!responseData.isEmpty()) {
            try {
                // Ưu tiên parse cấu trúc lỗi có trường 'message'
                ApiSingleResponse<?> errorResponse = gson.fromJson(responseData, ApiSingleResponse.class); // Hoặc ApiErrorResponse
                if (errorResponse != null && errorResponse.getMessage() != null && !errorResponse.getMessage().isEmpty()) {
                    specificErrorMessage = errorResponse.getMessage();
                }
            } catch (JsonSyntaxException ignored) {
                System.err.println("Could not parse error response body into known error structure: " + responseData.substring(0, Math.min(responseData.length(), 200)));
            }
        }
        String finalMessage = baseErrorMessage + ": " + response.code();
        if (specificErrorMessage != null) {
            finalMessage += " - " + specificErrorMessage;
        } else if (!responseData.isEmpty() && !responseData.startsWith("<") && responseData.length() < 1000) { // Avoid adding large HTML bodies
            finalMessage += ". Body: " + responseData.substring(0, Math.min(responseData.length(), 200)) + "...";
        }
        throw new IOException(finalMessage);
    }

    private void handleJsonParsingError(JsonSyntaxException e, String jsonData, String apiName) {
        System.err.println("JSON parsing error from " + apiName + ": " + e.getMessage());
        System.err.println("Raw JSON causing parsing error: " + jsonData.substring(0, Math.min(jsonData.length(), 500)) + "...");
    }

    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) { throw new IOException("Authentication token not found. Please login again."); }
        return new Request.Builder().url(url).header("Authorization", "Bearer " + token).get().build();
    }

    private Request buildAuthenticatedRequestWithBody(String method, String url, RequestBody body) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) { throw new IOException("Authentication token not found. Please login again."); }
        Request.Builder builder = new Request.Builder().url(url).header("Authorization", "Bearer " + token);
        if ("PUT".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            builder.header("Content-Type", JSON.toString()); // Ensure Content-Type for methods with body
        }
        switch (method.toUpperCase()) {
            case "POST": builder.post(body); break;
            case "PUT": builder.put(body); break;
            case "PATCH": builder.patch(body); break;
            case "DELETE": if (body != null) builder.delete(body); else builder.delete(); break;
            default: throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        return builder.build();
    }
}