package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage;
import com.utc2.facilityui.helper.Config; // Đảm bảo bạn đã tạo lớp này
import com.utc2.facilityui.model.BookingCreationRequest;
import com.utc2.facilityui.model.CancelBookingRequestData;
import com.utc2.facilityui.response.*;
import com.utc2.facilityui.utils.LocalDateTimeAdapter;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BookingService {

    private final OkHttpClient client;
    private final Gson gson;
    private static final String BASE_URL = Config.getOrDefault("BASE_URL", "http://localhost:8080/facility");
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public BookingService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    // createEmptyPage giờ nhận pageSize
    private Page<BookingResponse> createEmptyPage(int pageSize) {
        Page<BookingResponse> emptyPage = new Page<>();
        emptyPage.setContent(Collections.emptyList());
        emptyPage.setTotalElements(0);
        emptyPage.setTotalPages(0);
        emptyPage.setNumber(0);
        emptyPage.setSize(pageSize); // Sử dụng pageSize được truyền vào
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

        String url = BASE_URL + "/booking";
        String jsonBody = gson.toJson(bookingRequest);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request apiRequest = buildAuthenticatedRequestWithBody("POST", url, body);
        String jsonData = null;

        try (Response response = client.newCall(apiRequest).execute()) {
            jsonData = getResponseBody(response, "API /booking (POST) - createBooking");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Tạo đặt phòng thất bại");
            }
            try {
                Type apiResponseType = new TypeToken<ApiSingleResponse<BookingResponse>>() {}.getType();
                ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    return apiResponse.getResult();
                } else {
                    String errorMsg = "API tạo đặt phòng không hợp lệ hoặc kết quả null.";
                    if (apiResponse != null) errorMsg += " Code: " + apiResponse.getCode() + ", Message: " + apiResponse.getMessage();
                    handleJsonParsingError(null, jsonData, "API /booking (POST) - createBooking (result is null or structure mismatch) " + errorMsg);
                    throw new IOException(errorMsg);
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking (POST) - createBooking");
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
        String jsonData = null;
        System.out.println("Requesting My Bookings from: " + url);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code (My Bookings): " + response.code());
            jsonData = getResponseBody(response, "API /booking/my (paged)");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Lấy danh sách đặt phòng của tôi thất bại");
            }
            try {
                Type apiContainerType = new TypeToken<ApiContainerForPagedData<BookingResponse>>() {}.getType();
                ApiContainerForPagedData<BookingResponse> parsedContainer = gson.fromJson(jsonData, apiContainerType);

                if (parsedContainer != null && parsedContainer.getCode() == 0 && parsedContainer.getResult() != null) {
                    ResultWithNestedPage<BookingResponse> resultData = parsedContainer.getResult();
                    if (resultData.getContent() != null && resultData.getPage() != null) {
                        PageMetadata metadata = resultData.getPage();
                        List<BookingResponse> content = resultData.getContent() != null ? resultData.getContent() : Collections.emptyList();

                        Page<BookingResponse> finalPageObject = new Page<>();
                        finalPageObject.setContent(content);
                        finalPageObject.setTotalElements(metadata.getTotalElements());
                        finalPageObject.setTotalPages(metadata.getTotalPages());
                        finalPageObject.setNumber(metadata.getNumber());
                        finalPageObject.setSize(metadata.getSize());
                        finalPageObject.setNumberOfElements(content.size());
                        finalPageObject.setFirst(metadata.getNumber() == 0);
                        if (metadata.getTotalPages() == 0) {
                            finalPageObject.setLast(true);
                        } else {
                            finalPageObject.setLast(metadata.getNumber() >= metadata.getTotalPages() - 1);
                        }
                        finalPageObject.setEmpty(content.isEmpty());
                        return finalPageObject;
                    }
                }
                System.err.println("[CLIENT BookingService] getMyBookingsPaged - LỖI: Cấu trúc phản hồi API không như mong đợi."+ (parsedContainer != null ? " Code: " + parsedContainer.getCode() : ""));
                return createEmptyPage(size); // <-- Cập nhật: Truyền size
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking/my (paged)");
                return createEmptyPage(size); // <-- Cập nhật: Truyền size
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
        String jsonData = null;
        System.out.println("Requesting All Bookings from: " + url);

        try (Response response = client.newCall(request).execute()) {
            jsonData = getResponseBody(response, "API /booking (paged getAllBookings)");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Lấy danh sách tất cả đặt phòng thất bại");
            }
            try {
                // Parse đúng vào ApiContainerForPagedData
                Type apiContainerType = new TypeToken<ApiContainerForPagedData<BookingResponse>>() {}.getType();
                ApiContainerForPagedData<BookingResponse> parsedContainer = gson.fromJson(jsonData, apiContainerType);

                if (parsedContainer != null && parsedContainer.getCode() == 0 && parsedContainer.getResult() != null) {
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
                        finalPageObject.setFirst(metadata.getNumber() == 0);
                        if (metadata.getTotalPages() == 0) {
                            finalPageObject.setLast(true);
                        } else {
                            finalPageObject.setLast(metadata.getNumber() >= metadata.getTotalPages() - 1);
                        }
                        finalPageObject.setEmpty(content.isEmpty());
                        System.out.println("[CLIENT BookingService] getAllBookings - Successfully parsed. TotalElements: " + metadata.getTotalElements() + ", TotalPages: " + metadata.getTotalPages());
                        return finalPageObject;
                    } else {
                        System.err.println("[CLIENT BookingService] getAllBookings - LỖI: content hoặc page metadata trong result là null. JSON: " + (jsonData != null ? jsonData.substring(0, Math.min(jsonData.length(), 700)) : "null"));
                        return createEmptyPage(size); // <-- Cập nhật: Truyền size
                    }
                } else {
                    System.err.println("[CLIENT BookingService] getAllBookings - LỖI: Phản hồi API không hợp lệ hoặc result null. Code: " + (parsedContainer != null ? parsedContainer.getCode() : "N/A"));
                    return createEmptyPage(size); // <-- Cập nhật: Truyền size
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking (paged getAllBookings)");
                return createEmptyPage(size); // <-- Cập nhật: Truyền size
            }
        }
    }

    public Page<BookingResponse> getPendingApprovalBookings(int page, int size) throws IOException {
        String url = BASE_URL + "/booking/approval-request?page=" + page + "&size=" + size + "&status=PENDING_APPROVAL&sort=plannedStartTime,asc";
        Request request = buildAuthenticatedGetRequest(url);
        String jsonData = null;
        System.out.println("BookingService: Fetching PENDING_APPROVAL bookings from: " + url);

        try (Response response = client.newCall(request).execute()) {
            jsonData = getResponseBody(response, "API /booking/approval-request");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Lấy danh sách yêu cầu chờ duyệt thất bại");
            }
            // Đã sửa ở trên getAllBookings, bây giờ getPendingApprovalBookings cũng cần parse theo cách đúng
            // Giả sử /approval-request cũng trả về cấu trúc ApiContainerForPagedData
            try {
                Type apiContainerType = new TypeToken<ApiContainerForPagedData<BookingResponse>>() {}.getType();
                ApiContainerForPagedData<BookingResponse> parsedContainer = gson.fromJson(jsonData, apiContainerType);

                if (parsedContainer != null && parsedContainer.getCode() == 0 && parsedContainer.getResult() != null) {
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
                        finalPageObject.setFirst(metadata.getNumber() == 0);
                        if (metadata.getTotalPages() == 0) {
                            finalPageObject.setLast(true);
                        } else {
                            finalPageObject.setLast(metadata.getNumber() >= metadata.getTotalPages() - 1);
                        }
                        finalPageObject.setEmpty(content.isEmpty());
                        System.out.println("BookingService: Successfully fetched " + content.size() +
                                " PENDING_APPROVAL bookings for page " + page);
                        return finalPageObject;
                    } else {
                        System.err.println("[CLIENT BookingService] getPendingApprovalBookings - LỖI: content hoặc page metadata trong result là null. JSON: " + (jsonData != null ? jsonData.substring(0, Math.min(jsonData.length(), 700)) : "null"));
                        return createEmptyPage(size); // <-- Cập nhật: Truyền size
                    }
                } else {
                    String errorMsg = "API response (pending approvals) không hợp lệ hoặc báo lỗi.";
                    if (parsedContainer != null) {
                        errorMsg += " Code: " + parsedContainer.getCode() + ", Message: " + parsedContainer.getMessage();
                    }
                    System.err.println(errorMsg + (jsonData != null ? ". JSON: " + jsonData : ""));
                    return createEmptyPage(size); // <-- Cập nhật: Truyền size
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking/approval-request");
                return createEmptyPage(size); // <-- Cập nhật: Truyền size
            }
        }
    }


    public Page<BookingResponse> getOverdueBookings(int page, int size) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/booking/overdue")).newBuilder();
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("size", String.valueOf(size));
        String url = urlBuilder.build().toString();
        Request request = buildAuthenticatedGetRequest(url);
        String jsonData = null;
        System.out.println("BookingService: Fetching OVERDUE bookings from: " + url);

        try (Response response = client.newCall(request).execute()) {
            jsonData = getResponseBody(response, "API /booking/overdue");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Lấy danh sách đặt phòng quá hạn thất bại");
            }
            // Giả sử /overdue cũng trả về cấu trúc ApiContainerForPagedData
            try {
                Type apiContainerType = new TypeToken<ApiContainerForPagedData<BookingResponse>>() {}.getType();
                ApiContainerForPagedData<BookingResponse> parsedContainer = gson.fromJson(jsonData, apiContainerType);

                if (parsedContainer != null && parsedContainer.getCode() == 0 && parsedContainer.getResult() != null) {
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
                        finalPageObject.setFirst(metadata.getNumber() == 0);
                        if (metadata.getTotalPages() == 0) {
                            finalPageObject.setLast(true);
                        } else {
                            finalPageObject.setLast(metadata.getNumber() >= metadata.getTotalPages() - 1);
                        }
                        finalPageObject.setEmpty(content.isEmpty());
                        System.out.println("BookingService: Successfully fetched " + content.size() +
                                " OVERDUE bookings for page " + page);
                        return finalPageObject;
                    } else {
                        System.err.println("[CLIENT BookingService] getOverdueBookings - LỖI: content hoặc page metadata trong result là null. JSON: " + (jsonData != null ? jsonData.substring(0, Math.min(jsonData.length(), 700)) : "null"));
                        return createEmptyPage(size); // <-- Cập nhật: Truyền size
                    }
                } else {
                    String errorMsg = "API response (overdue bookings) không hợp lệ hoặc báo lỗi.";
                    if (parsedContainer != null) {
                        errorMsg += " Code: " + parsedContainer.getCode() + ", Message: " + parsedContainer.getMessage();
                    }
                    System.err.println(errorMsg + (jsonData != null ? ". JSON: " + jsonData : ""));
                    return createEmptyPage(size); // <-- Cập nhật: Truyền size
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking/overdue");
                return createEmptyPage(size); // <-- Cập nhật: Truyền size
            }
        }
    }

    private BookingResponse handleSingleBookingResponseAction(
            String bookingId,
            String actionPath,
            String failureMessageBase,
            String logContextForAction,
            RequestBody body) throws IOException {

        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID không được để trống cho hành động: " + actionPath);
        }
        String url = BASE_URL + "/booking/" + bookingId + "/" + actionPath;
        Request request = buildAuthenticatedRequestWithBody("PUT", url, body);
        String jsonData = null;

        System.out.println("BookingService: Performing action " + actionPath + " for booking ID: " + bookingId + " at " + url);

        try (Response response = client.newCall(request).execute()) {
            jsonData = getResponseBody(response, "API /booking/" + bookingId + "/" + actionPath + " for " + logContextForAction);
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, failureMessageBase);
            }
            try {
                Type apiResponseType = new TypeToken<com.utc2.facilityui.response.ApiSingleResponse<BookingResponse>>() {}.getType();
                com.utc2.facilityui.response.ApiSingleResponse<BookingResponse> apiResponse = gson.fromJson(jsonData, apiResponseType);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    System.out.println("BookingService: Action " + actionPath + " for booking " + bookingId + " succeeded.");
                    return apiResponse.getResult();
                } else {
                    String errorMsg = failureMessageBase + ": Phản hồi API không hợp lệ hoặc kết quả null.";
                    if (apiResponse != null) errorMsg += " Code: " + apiResponse.getCode() + ", Message: " + apiResponse.getMessage();
                    System.err.println(errorMsg + (jsonData != null ? ". JSON: " + jsonData : ""));
                    throw new IOException(errorMsg);
                }
            } catch (JsonSyntaxException e) {
                handleJsonParsingError(e, jsonData, "API /booking/" + bookingId + "/" + actionPath);
                throw new IOException("Lỗi parse JSON khi " + actionPath.toLowerCase() + " booking: " + e.getMessage(), e);
            }
        }
    }

    public BookingResponse checkInBooking(String bookingId) throws IOException {
        return handleSingleBookingResponseAction(bookingId, "checkin", "Check-in thất bại", "Check-in Booking", RequestBody.create(new byte[0]));
    }

    public BookingResponse checkOutBooking(String bookingId) throws IOException {
        return handleSingleBookingResponseAction(bookingId, "checkout", "Check-out thất bại", "Check-out Booking", RequestBody.create(new byte[0]));
    }

    public BookingResponse approveBooking(String bookingId) throws IOException {
        return handleSingleBookingResponseAction(bookingId, "approve", "Duyệt đặt phòng thất bại", "Approve Booking", RequestBody.create(new byte[0]));
    }

    public BookingResponse rejectBooking(String bookingId, String reason) throws IOException {
        Map<String, String> bodyMap = new HashMap<>();
        if (reason != null && !reason.trim().isEmpty()) {
            bodyMap.put("reason", reason);
        } else {
            // Quan trọng: API server có thể yêu cầu field reason, dù là rỗng.
            // Nếu server không cho phép reason là null hoặc body rỗng cho reject,
            // bạn cần gửi một đối tượng JSON hợp lệ, ví dụ: {"reason": ""}
            bodyMap.put("reason", ""); // Gửi reason rỗng nếu không có lý do cụ thể
        }
        RequestBody requestBody = RequestBody.create(gson.toJson(bodyMap), JSON);
        return handleSingleBookingResponseAction(bookingId, "reject", "Từ chối đặt phòng thất bại", "Reject Booking with Reason", requestBody);
    }

    public void cancelBookingByUser(String bookingId, CancelBookingRequestData cancelRequestData) throws IOException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID không được để trống để hủy.");
        }
        String url = BASE_URL + "/booking/" + bookingId + "/cancel";
        // Đảm bảo cancelRequestData không null để gson.toJson không lỗi
        String jsonBody = gson.toJson(cancelRequestData != null ? cancelRequestData : new CancelBookingRequestData("")); // Gửi reason rỗng nếu null
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request apiRequest = buildAuthenticatedRequestWithBody("POST", url, body); // Thường cancel là POST hoặc PUT/PATCH
        String jsonData = null;
        System.out.println("BookingService: Cancelling booking ID: " + bookingId + " with body: " + jsonBody);

        try (Response response = client.newCall(apiRequest).execute()) {
            jsonData = getResponseBody(response, "API /booking/.../cancel");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Hủy đặt phòng thất bại");
            }
            if (jsonData != null && !jsonData.trim().isEmpty() && !jsonData.trim().equals("{}")) {
                try {
                    Type apiResponseType = new TypeToken<com.utc2.facilityui.response.ApiSingleResponse<Void>>() {}.getType();
                    com.utc2.facilityui.response.ApiSingleResponse<Void> apiResponse = gson.fromJson(jsonData, apiResponseType);
                    if (apiResponse != null && apiResponse.getCode() != 0 ) {
                        String errorMessage = "API hủy đặt phòng trả về lỗi logic " + apiResponse.getCode();
                        if(apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()){
                            errorMessage += ": " + apiResponse.getMessage();
                        }
                        throw new IOException(errorMessage);
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println("[CLIENT BookingService] Yêu cầu hủy đặt phòng đã gửi, HTTP: " + response.code() + ". Body không phải JSON chuẩn hoặc rỗng: " + jsonData);
                }
            }
        }
    }

    public List<EquipmentResponse> getEquipmentsByBookingId(String bookingId) throws IOException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID không được rỗng.");
        }
        String url = BASE_URL + "/booking/" + bookingId + "/equipments";
        Request request = buildAuthenticatedGetRequest(url);
        String jsonData = null;
        System.out.println("BookingService: Fetching equipments for booking ID: " + bookingId + " from " + url);

        try (Response response = client.newCall(request).execute()) {
            jsonData = getResponseBody(response, "API /booking/" + bookingId + "/equipments");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Lỗi lấy thiết bị theo booking");
            }
            Type apiResponseType = new TypeToken<com.utc2.facilityui.response.ApiSingleResponse<List<EquipmentResponse>>>(){}.getType();
            com.utc2.facilityui.response.ApiSingleResponse<List<EquipmentResponse>> apiResponse = gson.fromJson(jsonData, apiResponseType);

            if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                return apiResponse.getResult();
            } else {
                String errorMsg = "API response (thiết bị theo booking) không hợp lệ hoặc báo lỗi.";
                if(apiResponse != null) errorMsg += " Code: " + apiResponse.getCode() + ", Message: " + apiResponse.getMessage();
                System.err.println(errorMsg + (jsonData != null ? ". JSON: " + jsonData : ""));
                return Collections.emptyList();
            }
        } catch (JsonSyntaxException e) {
            handleJsonParsingError(e, jsonData, "API /booking/" + bookingId + "/equipments");
            throw new IOException("Lỗi parse JSON khi lấy thiết bị theo booking: " + e.getMessage(), e);
        }
    }

    public void sendOverdueReminder(String bookingId) throws IOException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID không được để trống để gửi nhắc nhở.");
        }
        String url = BASE_URL + "/api/notifications/overdue-reminder?bookingId=" + bookingId;
        Request request = buildAuthenticatedGetRequest(url);
        String jsonData = null;
        System.out.println("BookingService: Sending overdue reminder for booking ID: " + bookingId + " to " + url);

        try (Response response = client.newCall(request).execute()) {
            jsonData = getResponseBody(response, "API /api/notifications/overdue-reminder");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Gửi nhắc nhở quá hạn thất bại");
            }
            if (jsonData != null && !jsonData.trim().isEmpty() && !jsonData.trim().equals("{}")) {
                try {
                    Type apiResponseType = new TypeToken<ApiSingleResponse<Void>>() {}.getType();
                    ApiSingleResponse<Void> apiResponse = gson.fromJson(jsonData, apiResponseType);
                    if (apiResponse != null && apiResponse.getCode() != 0) {
                        String errorMessage = "API gửi nhắc nhở trả về lỗi logic " + apiResponse.getCode();
                        if (apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()) {
                            errorMessage += ": " + apiResponse.getMessage();
                        }
                        throw new IOException(errorMessage);
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println("[CLIENT BookingService] Yêu cầu gửi nhắc nhở đã gửi, HTTP: " + response.code() + ". Body không phải JSON chuẩn hoặc rỗng: " + jsonData);
                }
            }
            System.out.println("BookingService: Overdue reminder sent successfully for booking ID: " + bookingId);
        }
    }

    public void revokeBooking(String bookingId, String reason) throws IOException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID không được để trống để thu hồi.");
        }
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/booking/revoke")).newBuilder();
        urlBuilder.addQueryParameter("bookingId", bookingId);
        if (reason != null && !reason.trim().isEmpty()) {
            urlBuilder.addQueryParameter("reason", reason);
        }
        String url = urlBuilder.build().toString();

        RequestBody emptyBody = RequestBody.create(new byte[0]);
        Request request = buildAuthenticatedRequestWithBody("PUT", url, emptyBody);
        String jsonData = null;
        System.out.println("BookingService: Revoking booking ID: " + bookingId + " with reason: '" + reason + "' at " + url);

        try (Response response = client.newCall(request).execute()) {
            jsonData = getResponseBody(response, "API /booking/revoke");
            if (!response.isSuccessful()) {
                parseAndThrowError(response, jsonData, "Thu hồi đặt phòng thất bại");
            }
            if (jsonData != null && !jsonData.trim().isEmpty() && !jsonData.trim().equals("{}")) {
                try {
                    Type apiResponseType = new TypeToken<ApiSingleResponse<Void>>() {}.getType();
                    ApiSingleResponse<Void> apiResponse = gson.fromJson(jsonData, apiResponseType);
                    if (apiResponse != null && apiResponse.getCode() != 0) {
                        String errorMessage = "API thu hồi đặt phòng trả về lỗi logic " + apiResponse.getCode();
                        if (apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()) {
                            errorMessage += ": " + apiResponse.getMessage();
                        }
                        throw new IOException(errorMessage);
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println("[CLIENT BookingService] Yêu cầu thu hồi đã gửi, HTTP: " + response.code() + ". Body không phải JSON chuẩn hoặc rỗng: " + jsonData);
                }
            }
            System.out.println("BookingService: Booking " + bookingId + " revoked successfully.");
        }
    }

    private String getResponseBody(Response response, String context) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            if (responseBody != null) {
                return responseBody.string();
            } else if (!response.isSuccessful()) {
                System.err.println("[CLIENT BookingService GET_RESPONSE_BODY] ("+context+") Yêu cầu thất bại: " + response.code() + " và body phản hồi là null.");
                throw new IOException("("+context+") Yêu cầu thất bại: " + response.code() + " và body phản hồi là null.");
            } else {
                return "";
            }
        }
    }

    private void parseAndThrowError(Response response, String responseData, String baseErrorMessage) throws IOException {
        System.err.println("[CLIENT BookingService PARSE_AND_THROW_ERROR] " + baseErrorMessage + ". HTTP Status: " + response.code());
        String specificErrorMessage = "Không có thông tin lỗi chi tiết từ server.";
        Integer apiErrorCode = null;
        String apiMessage = null;

        if (responseData != null && !responseData.isEmpty() && !responseData.trim().equals("{}")) {
            System.err.println("[CLIENT BookingService RAW_JSON_ERROR_CONTEXT] JSON (first 700): " + responseData.substring(0, Math.min(responseData.length(), 700)) + (responseData.length() > 700 ? "..." : ""));
            try {
                Type errorResponseType = new TypeToken<com.utc2.facilityui.response.ApiSingleResponse<Object>>() {}.getType();
                com.utc2.facilityui.response.ApiSingleResponse<?> errorDetails = gson.fromJson(responseData, errorResponseType);
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
                System.err.println("[CLIENT BookingService PARSE_ERROR_DETAIL] Không thể parse chi tiết lỗi từ JSON: " + ignored.getMessage());
            }
        }
        String finalMessage = baseErrorMessage + ". Mã HTTP: " + response.code();
        if (apiErrorCode != null) { finalMessage += ". Mã lỗi API: " + apiErrorCode; }
        if (!specificErrorMessage.equals("Không có thông tin lỗi chi tiết từ server.") && !specificErrorMessage.trim().isEmpty()) {
            finalMessage += ". Chi tiết: " + specificErrorMessage;
        } else if (response.message() != null && !response.message().isEmpty()){
            finalMessage += ". Thông điệp HTTP: " + response.message();
        }
        throw new IOException(finalMessage);
    }

    private void handleJsonParsingError(JsonSyntaxException e, String jsonData, String apiName) {
        System.err.println("[CLIENT BookingService JSON_PARSE_ERROR] Lỗi parse JSON từ " + apiName + ": " + e.getMessage());
        if (jsonData != null) {
            System.err.println("[CLIENT BookingService JSON_PARSE_ERROR] JSON thô (first 700): " + jsonData.substring(0, Math.min(jsonData.length(), 700)) + (jsonData.length() > 700 ? "..." : ""));
        }
    }

    private Request buildAuthenticatedGetRequest(String url) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("[CLIENT BookingService AUTH_GET] Lỗi: Token không tìm thấy hoặc rỗng.");
            throw new IOException("Không tìm thấy token xác thực. Vui lòng đăng nhập lại.");
        }
        return new Request.Builder().url(url).header("Authorization", "Bearer " + token).get().build();
    }

    private Request buildAuthenticatedRequestWithBody(String method, String url, RequestBody body) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("[CLIENT BookingService AUTH_BODY_REQ] Lỗi: Token không tìm thấy hoặc rỗng.");
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