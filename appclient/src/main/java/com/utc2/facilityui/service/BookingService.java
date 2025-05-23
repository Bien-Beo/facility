package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.model.BookingCreationRequest;
import com.utc2.facilityui.model.CancelBookingRequestData;
import com.utc2.facilityui.response.ApiContainerForPagedData;
import com.utc2.facilityui.response.ResultWithNestedPage;
import com.utc2.facilityui.response.PageMetadata;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.response.ApiSingleResponse;
import com.utc2.facilityui.utils.LocalDateTimeAdapter;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BookingService {

    private final OkHttpClient client;
    private final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/facility";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public BookingService() {
        client = new OkHttpClient.Builder().build();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    private Page<BookingResponse> createEmptyPage() {
        Page<BookingResponse> emptyPage = new Page<>();
        emptyPage.setContent(Collections.emptyList());
        emptyPage.setTotalElements(0);
        emptyPage.setTotalPages(0);
        emptyPage.setNumber(0);
        emptyPage.setSize(0);
        emptyPage.setFirst(true);
        emptyPage.setLast(true);
        emptyPage.setEmpty(true);
        emptyPage.setNumberOfElements(0);
        return emptyPage;
    }

    public BookingResponse createBooking(BookingCreationRequest bookingRequest) throws IOException {
        if (bookingRequest == null) {
            throw new IllegalArgumentException("Dữ liệu yêu cầu đặt phòng không được để trống.");
        }
        if (bookingRequest.getRoomId() == null || bookingRequest.getPlannedStartTime() == null || bookingRequest.getPlannedEndTime() == null) {
            throw new IllegalArgumentException("Dữ liệu yêu cầu đặt phòng không hợp lệ (thiếu ID phòng hoặc thời gian).");
        }

        String url = BASE_URL + "/booking";
        String jsonBody = gson.toJson(bookingRequest);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request apiRequest = buildAuthenticatedRequestWithBody("POST", url, body);

        try (Response response = client.newCall(apiRequest).execute()) {
            String jsonData = getResponseBody(response);
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Tạo đặt phòng thất bại");
            }
            try {
                Type apiResponseType = new TypeToken<ApiSingleResponse<BookingResponse>>() {}.getType();
                ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getResult() != null) {
                    if (apiResponse.getCode() != 0 && apiResponse.getMessage() != null) {
                        throw new IOException("API tạo đặt phòng trả về lỗi logic " + apiResponse.getCode() + ": " + apiResponse.getMessage());
                    }
                    return apiResponse.getResult();
                } else {
                    throw new IOException("Không thể parse cấu trúc booking mong đợi từ phản hồi API sau khi tạo.");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking (POST) - createBooking"); // Giữ lại log lỗi parse
                throw new IOException("Lỗi parse JSON phản hồi thành công khi tạo đặt phòng: " + e.getMessage(), e);
            }
        }
    }

    public Page<BookingResponse> getMyBookingsPaged(int page, int size) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/booking/my")).newBuilder();
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("size", String.valueOf(size));
        String url = urlBuilder.build().toString();
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            String jsonData = getResponseBody(response);
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Lấy danh sách đặt phòng của tôi thất bại");
            }
            try {
                Type apiContainerType = new TypeToken<ApiContainerForPagedData<BookingResponse>>() {}.getType();
                ApiContainerForPagedData<BookingResponse> parsedContainer = gson.fromJson(jsonData, apiContainerType);

                if (parsedContainer != null && parsedContainer.getResult() != null) {
                    ResultWithNestedPage<BookingResponse> resultData = parsedContainer.getResult();
                    if (resultData.getContent() != null && resultData.getPage() != null) {
                        PageMetadata metadata = resultData.getPage();
                        List<BookingResponse> content = resultData.getContent();

                        Page<BookingResponse> finalPageObject = new Page<>();
                        finalPageObject.setContent(content);
                        finalPageObject.setTotalElements(metadata.getTotalElements());
                        finalPageObject.setTotalPages(metadata.getTotalPages());
                        finalPageObject.setNumber(metadata.getNumber());
                        finalPageObject.setSize(metadata.getSize());
                        finalPageObject.setNumberOfElements(content.size());
                        finalPageObject.setFirst(metadata.getNumber() == 0 && metadata.getTotalElements() > 0);
                        finalPageObject.setLast( (metadata.getTotalElements() > 0 && metadata.getNumber() >= metadata.getTotalPages() - 1) || (metadata.getTotalElements() == 0 && metadata.getTotalPages() <= 1 && metadata.getNumber() == 0) );
                        finalPageObject.setEmpty(content.isEmpty());
                        return finalPageObject;
                    } else {
                        System.err.println("[CLIENT BookingService] getMyBookingsPaged - LỖI: resultData.getContent() hoặc resultData.getPage() là null."); // Giữ lại log lỗi quan trọng
                        return createEmptyPage();
                    }
                } else {
                    System.err.println("[CLIENT BookingService] getMyBookingsPaged - LỖI: parsedContainer hoặc parsedContainer.getResult() là null."); // Giữ lại log lỗi quan trọng
                    return createEmptyPage();
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking/my (paged)"); // Giữ lại log lỗi parse
                return createEmptyPage();
            }
        }
    }

    public Page<BookingResponse> getAllBookings(String roomId, Integer month, Integer year, String userId, int page, int size) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/booking")).newBuilder();
        if (roomId != null && !roomId.trim().isEmpty()) urlBuilder.addQueryParameter("roomId", roomId.trim());
        if (month != null && month >= 1 && month <= 12) urlBuilder.addQueryParameter("month", String.valueOf(month));
        if (year != null && year > 1900) urlBuilder.addQueryParameter("year", String.valueOf(year));
        if (userId != null && !userId.trim().isEmpty()) urlBuilder.addQueryParameter("userId", userId.trim());
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("size", String.valueOf(size));
        String url = urlBuilder.build().toString();
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            String jsonData = getResponseBody(response);
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Lấy danh sách tất cả đặt phòng thất bại");
            }
            try {
                Type apiContainerType = new TypeToken<ApiContainerForPagedData<BookingResponse>>() {}.getType();
                ApiContainerForPagedData<BookingResponse> parsedContainer = gson.fromJson(jsonData, apiContainerType);
                if (parsedContainer != null && parsedContainer.getResult() != null) {
                    ResultWithNestedPage<BookingResponse> resultData = parsedContainer.getResult();
                    if (resultData.getContent() != null && resultData.getPage() != null) {
                        PageMetadata metadata = resultData.getPage();
                        List<BookingResponse> content = resultData.getContent();
                        Page<BookingResponse> finalPageObject = new Page<>();
                        finalPageObject.setContent(content);
                        finalPageObject.setTotalElements(metadata.getTotalElements());
                        finalPageObject.setTotalPages(metadata.getTotalPages());
                        finalPageObject.setNumber(metadata.getNumber());
                        finalPageObject.setSize(metadata.getSize());
                        finalPageObject.setNumberOfElements(content.size());
                        finalPageObject.setFirst(metadata.getNumber() == 0 && metadata.getTotalElements() > 0);
                        finalPageObject.setLast( (metadata.getTotalElements() > 0 && metadata.getNumber() >= metadata.getTotalPages() - 1) || (metadata.getTotalElements() == 0 && metadata.getTotalPages() <= 1 && metadata.getNumber() == 0) );
                        finalPageObject.setEmpty(content.isEmpty());
                        return finalPageObject;
                    } else {
                        System.err.println("[CLIENT BookingService] getAllBookings - LỖI: resultData.getContent() hoặc resultData.getPage() là null."); // Giữ lại
                        return createEmptyPage();
                    }
                } else {
                    System.err.println("[CLIENT BookingService] getAllBookings - LỖI: parsedContainer hoặc getResult() là null."); // Giữ lại
                    return createEmptyPage();
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking (paged getAllBookings)"); // Giữ lại
                return createEmptyPage();
            }
        }
    }

    private BookingResponse handleSingleBookingResponseAction(String bookingId, String actionPath, String errorMessageBase, String logContextForAction) throws IOException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID không được để trống cho hành động: " + actionPath);
        }
        String url = BASE_URL + "/booking/" + bookingId + "/" + actionPath;
        RequestBody body = RequestBody.create(new byte[0]);
        Request request = buildAuthenticatedRequestWithBody("PUT", url, body);

        try (Response response = client.newCall(request).execute()) {
            String jsonData = getResponseBody(response);
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, errorMessageBase);
            }
            try {
                Type apiResponseType = new TypeToken<ApiSingleResponse<BookingResponse>>() {}.getType();
                ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);
                if (apiResponse != null && apiResponse.getResult() != null) {
                    if (apiResponse.getCode() != 0 && apiResponse.getMessage() != null) {
                        throw new IOException("API " + logContextForAction + " trả về lỗi logic " + apiResponse.getCode() + ": " + apiResponse.getMessage());
                    }
                    return apiResponse.getResult();
                } else {
                    System.err.println("[CLIENT BookingService] " + logContextForAction + " - LỖI: Phản hồi API không hợp lệ hoặc kết quả null."); // Giữ lại
                    throw new IOException("Phản hồi API " + logContextForAction + " không hợp lệ hoặc kết quả null.");
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API " + actionPath + " cho " + logContextForAction); // Giữ lại
                throw new IOException("Lỗi parse JSON khi " + actionPath.toLowerCase() + ": " + e.getMessage(), e);
            }
        }
    }

    public BookingResponse checkInBooking(String bookingId) throws IOException {
        return handleSingleBookingResponseAction(bookingId, "checkin", "Check-in thất bại", "Check-in Booking");
    }

    public BookingResponse checkOutBooking(String bookingId) throws IOException {
        return handleSingleBookingResponseAction(bookingId, "checkout", "Check-out thất bại", "Check-out Booking");
    }

    public BookingResponse approveBooking(String bookingId) throws IOException {
        return handleSingleBookingResponseAction(bookingId, "approve", "Duyệt đặt phòng thất bại", "Approve Booking");
    }

    public BookingResponse rejectBooking(String bookingId) throws IOException {
        return handleSingleBookingResponseAction(bookingId, "reject", "Từ chối đặt phòng thất bại", "Reject Booking");
    }

    public void cancelBookingByUser(String bookingId, CancelBookingRequestData cancelRequestData) throws IOException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID không được để trống để hủy.");
        }
        String url = BASE_URL + "/booking/" + bookingId + "/cancel";
        String jsonBody = gson.toJson(cancelRequestData != null ? cancelRequestData : new CancelBookingRequestData(null));
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request apiRequest = buildAuthenticatedRequestWithBody("POST", url, body);

        try (Response response = client.newCall(apiRequest).execute()) {
            String jsonData = getResponseBody(response);
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Hủy đặt phòng thất bại");
            }
            try {
                if (jsonData != null && !jsonData.trim().isEmpty() && !jsonData.trim().equals("{}")) {
                    Type apiResponseType = new TypeToken<ApiSingleResponse<Void>>() {}.getType();
                    ApiSingleResponse<Void> apiResponse = gson.fromJson(jsonData, apiResponseType);
                    if (apiResponse != null && apiResponse.getCode() != 0 ) {
                        String errorMessage = "API hủy đặt phòng trả về lỗi logic " + apiResponse.getCode();
                        if(apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()){
                            errorMessage += ": " + apiResponse.getMessage();
                        }
                        throw new IOException(errorMessage);
                    }
                }
                // Nếu không có lỗi, hoặc body rỗng (HTTP 200/204) thì coi là thành công
            } catch (JsonSyntaxException e) {
                // Không làm gì nếu parse lỗi nhưng HTTP thành công, vì có thể body rỗng
                System.out.println("[CLIENT BookingService] Yêu cầu hủy đặt phòng đã gửi, HTTP: " + response.code() + ". Body không phải JSON chuẩn hoặc rỗng.");
            }
        }
    }

    // --- Helper Methods ---
    private String getResponseBody(Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            if (responseBody != null) {
                return responseBody.string();
            } else if (!response.isSuccessful()) {
                System.err.println("[CLIENT BookingService GET_RESPONSE_BODY] Yêu cầu thất bại: " + response.code() + " và body phản hồi là null.");
                throw new IOException("Yêu cầu thất bại: " + response.code() + " và body phản hồi là null.");
            } else {
                return "{}";
            }
        }
    }

    private void parseAndThrowError(Response response, String responseData, String baseErrorMessage) throws IOException {
        // Giữ lại System.err cho lỗi nghiêm trọng này
        System.err.println("[CLIENT BookingService PARSE_AND_THROW_ERROR] " + baseErrorMessage + ". HTTP Status: " + response.code());
        if (responseData != null && !responseData.isEmpty()) {
            System.err.println("[CLIENT BookingService RAW_JSON_ERROR_CONTEXT] JSON (first 700): " + responseData.substring(0, Math.min(responseData.length(), 700)) + (responseData.length() > 700 ? "..." : ""));
        }

        String specificErrorMessage = "Không có thông tin lỗi chi tiết từ server.";
        int httpStatusCode = response.code();
        Integer apiErrorCode = null;
        String apiMessage = null;

        if (responseData != null && !responseData.isEmpty() && !responseData.trim().equals("{}")) {
            try {
                Type errorResponseType = new TypeToken<ApiSingleResponse<Object>>() {}.getType();
                ApiSingleResponse<?> errorDetails = gson.fromJson(responseData, errorResponseType);
                if (errorDetails != null) {
                    apiMessage = errorDetails.getMessage();
                    if (errorDetails.getCode() != 0) {
                        apiErrorCode = errorDetails.getCode();
                    }
                    if (apiMessage != null && !apiMessage.isEmpty()) {
                        specificErrorMessage = apiMessage;
                    }
                }
            } catch (JsonSyntaxException ignored) {
                // Đã log ở trên, không cần log lại
            }
        }

        String finalMessage = baseErrorMessage + ". Mã HTTP: " + httpStatusCode;
        if (apiErrorCode != null) {
            finalMessage += ". Mã lỗi API: " + apiErrorCode;
        }
        if (!"Không có thông tin lỗi chi tiết từ server.".equals(specificErrorMessage) && !specificErrorMessage.trim().isEmpty()) {
            finalMessage += ". Chi tiết: " + specificErrorMessage;
        } else if (response.message() != null && !response.message().isEmpty()){
            finalMessage += ". Thông điệp HTTP: " + response.message();
        }
        throw new IOException(finalMessage);
    }

    private void handleJsonParsingError(JsonSyntaxException e, String jsonData, String apiName) {
        // Giữ lại System.err cho lỗi nghiêm trọng này
        System.err.println("[CLIENT BookingService JSON_PARSE_ERROR] Lỗi parse JSON từ " + apiName + ": " + e.getMessage());
        if (jsonData != null) {
            System.err.println("[CLIENT BookingService JSON_PARSE_ERROR] JSON thô (first 700): " + jsonData.substring(0, Math.min(jsonData.length(), 700)) + (jsonData.length() > 700 ? "..." : ""));
        }
    }

    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("[CLIENT BookingService AUTH_GET] Lỗi: Token không tìm thấy hoặc rỗng."); // Giữ lại log lỗi xác thực
            throw new IOException("Không tìm thấy token xác thực. Vui lòng đăng nhập lại.");
        }
        return new Request.Builder().url(url).header("Authorization", "Bearer " + token).get().build();
    }

    private Request buildAuthenticatedRequestWithBody(String method, String url, RequestBody body) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("[CLIENT BookingService AUTH_BODY_REQ] Lỗi: Token không tìm thấy hoặc rỗng."); // Giữ lại log lỗi xác thực
            throw new IOException("Không tìm thấy token xác thực. Vui lòng đăng nhập lại.");
        }
        Request.Builder builder = new Request.Builder().url(url).header("Authorization", "Bearer " + token);
        switch (method.toUpperCase()) {
            case "POST": builder.post(body); break;
            case "PUT": builder.put(body); break;
            case "PATCH": builder.patch(body); break;
            case "DELETE": if (body != null) builder.delete(body); else builder.delete(); break;
            default: throw new IllegalArgumentException("Phương thức HTTP không được hỗ trợ: " + method);
        }
        return builder.build();
    }

}