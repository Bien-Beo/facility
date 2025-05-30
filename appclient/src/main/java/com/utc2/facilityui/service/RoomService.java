package com.utc2.facilityui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.auth.TokenStorage; // Đảm bảo lớp này tồn tại và hoạt động
import com.utc2.facilityui.model.Facility;   // Model bạn dùng cho TableView
// Import các DTOs bạn đã cung cấp từ package response
import com.utc2.facilityui.model.Room;
import com.utc2.facilityui.model.RoomItem;
import com.utc2.facilityui.response.*;
// Các DTOs khác nếu vẫn được dùng


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
// import java.util.stream.Collectors; // Bỏ comment nếu dùng cho mapping

public class RoomService {
    private final OkHttpClient client;
    private final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/facility"; // URL API server
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Lớp nội tĩnh để chứa kết quả trả về từ API phân trang.
     * Bao gồm danh sách các Facility cho trang hiện tại và thông tin metadata của trang.
     */
    public static class PaginatedFacilitiesResponse {
        private List<Facility> facilities;
        private PageMetadata pageMetadata; // Sử dụng PageMetadata DTO bạn đã cung cấp

        public PaginatedFacilitiesResponse(List<Facility> facilities, PageMetadata pageMetadata) {
            this.facilities = facilities;
            this.pageMetadata = pageMetadata;
        }

        public List<Facility> getFacilities() {
            return facilities;
        }

        public PageMetadata getPageMetadata() {
            return pageMetadata;
        }

        // Các helper method để dễ dàng truy cập thông tin từ PageMetadata
        public int getTotalPages() {
            return pageMetadata != null ? pageMetadata.getTotalPages() : 0;
        }

        public long getTotalElements() {
            return pageMetadata != null ? pageMetadata.getTotalElements() : 0;
        }

        public int getCurrentPageNumber() { // Server trả về 0-indexed
            return pageMetadata != null ? pageMetadata.getNumber() : 0;
        }

        public int getPageSize() {
            return pageMetadata != null ? pageMetadata.getSize() : 0;
        }
    }

    public RoomService() {
        client = new OkHttpClient();
        gson = new GsonBuilder().create();
    }

    /**
     * Lấy danh sách Facility theo trang từ server.
     * Sử dụng cấu trúc DTO: ApiContainerForPagedData -> ResultWithNestedPage -> PageMetadata.
     *
     * @param page Số trang yêu cầu (0-indexed).
     * @param size Số lượng mục trên mỗi trang.
     * @return PaginatedFacilitiesResponse chứa danh sách Facility và thông tin phân trang.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi từ API.
     */
    public PaginatedFacilitiesResponse getFacilitiesPaginated(
            int page,
            int size,
            String buildingIdFilter,
            String roomTypeIdFilter,
            Integer yearFilter,
            String managerIdFilter) throws IOException {

        // Xây dựng URL với các tham số lọc (chỉ thêm nếu chúng không null/rỗng)
        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/rooms?page=" + page + "&size=" + size);

        if (buildingIdFilter != null && !buildingIdFilter.isEmpty()) {
            urlBuilder.append("&buildingId=").append(buildingIdFilter);
        }
        if (roomTypeIdFilter != null && !roomTypeIdFilter.isEmpty()) {
            urlBuilder.append("&roomTypeId=").append(roomTypeIdFilter);
        }
        if (yearFilter != null) { // Integer có thể là null
            urlBuilder.append("&year=").append(yearFilter);
        }
        if (managerIdFilter != null && !managerIdFilter.isEmpty()) {
            urlBuilder.append("&managerId=").append(managerIdFilter); // Hoặc tên param server mong đợi
        }

        String url = urlBuilder.toString();
        Request request = buildAuthenticatedGetRequest(url);
        String jsonData = null;

        try (Response response = client.newCall(request).execute()) {
            // ... (phần còn lại của phương thức để parse JSON và trả về PaginatedFacilitiesResponse
            //      giống như phiên bản tôi đã cung cấp, sử dụng ApiContainerForPagedData<Facility>) ...
            // Ví dụ:
            ResponseBody responseBody = response.body();
            // ... (đọc jsonData, đóng body) ...
            if (!response.isSuccessful()) { /* xử lý lỗi */ }
            // ... (parse jsonData vào ApiContainerForPagedData<Facility>) ...
            // ... (trả về new PaginatedFacilitiesResponse(...)) ...

            // ĐOẠN CODE MẪU ĐẦY ĐỦ CHO PHẦN NÀY ĐÃ CÓ TRONG PHIÊN BẢN RoomService.java
            // MÀ TÔI GỬI Ở LƯỢT "vậy tôi dùng cách 1 bạn hãy code lại đầy đủ giúp tôi file service"
            // BẠN CHỈ CẦN ĐẢM BẢO PHẦN ĐẦU PHƯƠNG THỨC (SIGNATURE) NHẬN ĐỦ 6 THAM SỐ.

            // === BẮT ĐẦU PHẦN LOGIC BÊN TRONG TRY (SAU KHI CÓ URL) ===
            try { // Khối try này cho responseBody.string() và gson.fromJson()
                if (responseBody == null) {
                    throw new IOException("Response body rỗng từ API phân trang cơ sở vật chất.");
                }
                jsonData = responseBody.string();
            } finally {
                if (responseBody != null) responseBody.close();
            }

            System.out.println("Paginated Facilities Raw JSON from " + url + ": " + jsonData);

            Type apiContainerType = new TypeToken<ApiContainerForPagedData<Facility>>() {}.getType();
            ApiContainerForPagedData<Facility> apiContainer = gson.fromJson(jsonData, apiContainerType);

            if (apiContainer != null && apiContainer.getCode() == 0 && apiContainer.getResult() != null && apiContainer.getResult().getPage() != null) {
                ResultWithNestedPage<Facility> resultWithNestedPage = apiContainer.getResult();
                List<Facility> facilities = resultWithNestedPage.getContent();
                PageMetadata pageMetadata = resultWithNestedPage.getPage();

                if (facilities == null) {
                    facilities = Collections.emptyList();
                }
                return new PaginatedFacilitiesResponse(facilities, pageMetadata);
            } else {
                String apiCode = (apiContainer != null) ? String.valueOf(apiContainer.getCode()) : "N/A";
                String apiMessage = (apiContainer != null && apiContainer.getMessage() != null) ? apiContainer.getMessage() : "Unknown API error or malformed response";
                System.err.println("API phân trang cơ sở vật chất response không hợp lệ hoặc báo lỗi. Code: " + apiCode + ", Message: " + apiMessage + (jsonData != null ? ". JSON: " + jsonData : ""));
                PageMetadata errorMetadata = new PageMetadata();
                errorMetadata.setNumber(page);
                errorMetadata.setSize(size);
                errorMetadata.setTotalPages(0);
                errorMetadata.setTotalElements(0);
                return new PaginatedFacilitiesResponse(Collections.emptyList(), errorMetadata);
            }
            // === KẾT THÚC PHẦN LOGIC BÊN TRONG TRY ===

        } catch (JsonSyntaxException e) {
            System.err.println("Lỗi parse JSON phân trang cơ sở vật chất: " + e.getMessage() + (jsonData != null ? ". JSON: " + jsonData : ". Không thể đọc JSON body."));
            throw new IOException("Không thể parse response phân trang: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Lỗi không mong muốn khi lấy dữ liệu phân trang: " + e.getMessage());
            throw new IOException("Lỗi không mong muốn khi lấy dữ liệu phân trang: " + e.getMessage(), e);
        }
        }

    /**
     * Lấy dữ liệu Facility cho Dashboard (Nếu vẫn được sử dụng cho mục đích khác).
     * Endpoint: GET /facility/dashboard/room
     */
    public ObservableList<Facility> getDashboardFacilities() throws IOException {
        String url = BASE_URL + "/dashboard/room";
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                String errorBodyStr = (responseBody != null) ? responseBody.string() : "N/A";
                if(responseBody != null) responseBody.close();
                throw new IOException("Yêu cầu API Dashboard thất bại: " + response.code() + ". Body: " + errorBodyStr);
            }
            if (responseBody == null) throw new IOException("Response body rỗng từ API Dashboard.");

            String jsonData;
            try { jsonData = responseBody.string(); } finally { responseBody.close(); }
            // System.out.println("Dashboard Raw JSON: " + jsonData);

            try {
                DashboardRoomApiResponse apiResponse = gson.fromJson(jsonData, DashboardRoomApiResponse.class);

                if (apiResponse != null && apiResponse.getCode() == 0 && apiResponse.getResult() != null) {
                    ObservableList<Facility> allFacilities = FXCollections.observableArrayList();
                    for (RoomGroupResponse group : apiResponse.getResult()) {
                        if (group != null && group.getRooms() != null) {
                            group.getRooms().stream()
                                    .filter(Objects::nonNull)
                                    .forEach(allFacilities::add);
                        }
                    }
                    return allFacilities;
                } else {
                    String apiCode = (apiResponse != null) ? String.valueOf(apiResponse.getCode()) : "N/A";
                    System.err.println("API Dashboard response không hợp lệ hoặc báo lỗi. Code: " + apiCode);
                    return FXCollections.observableArrayList();
                }
            } catch (JsonSyntaxException e) {
                System.err.println("Lỗi parse JSON Dashboard: " + e.getMessage());
                throw new IOException("Không thể parse response dashboard: " + e.getMessage(), e);
            }
        }
    }
    /**
     * Lấy danh sách rút gọn các phòng (ID và Tên) để dùng cho ComboBox lọc.
     * API server có thể là một endpoint mới /rooms/list hoặc /rooms không phân trang,
     * hoặc fetch tất cả các trang từ API /rooms hiện tại.
     * @return Danh sách RoomItem.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi từ API.
     */
    public List<RoomItem> getAllRoomsForFilter() throws IOException {
        List<RoomItem> allRoomItems = new ArrayList<>();
        // Cách 1: Gọi một endpoint mới (ví dụ /rooms/simple-list) nếu server có
        // String url = BASE_URL + "/rooms/simple-list";
        // Request request = buildAuthenticatedGetRequest(url);
        // // ... xử lý response tương tự, parse vào ApiResponse<List<RoomItem>> ...

        // Cách 2: Fetch tất cả các trang từ endpoint /rooms hiện tại
        // (Cẩn thận nếu số lượng phòng rất lớn)
        int currentPage = 0;
        int totalPages = 1; // Giả định ban đầu
        int pageSizeForFilter = 200; // Lấy nhiều item một lúc để giảm số lần gọi API

        while (currentPage < totalPages) {
            // Xây dựng URL với các tham số lọc (chỉ thêm nếu chúng không null/rỗng)
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/rooms").newBuilder();
            urlBuilder.addQueryParameter("page", String.valueOf(currentPage));
            urlBuilder.addQueryParameter("size", String.valueOf(pageSizeForFilter));
            // KHÔNG thêm các tham số lọc khác (buildingId, roomTypeId, etc.)
            // vì chúng ta muốn lấy TẤT CẢ các phòng cho ComboBox

            String url = urlBuilder.build().toString();
            Request request = buildAuthenticatedGetRequest(url);
            String jsonData = null;
            System.out.println("Fetching all rooms for filter (page " + currentPage + ") from: " + url);

            try (Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                try {
                    if (responseBody == null) throw new IOException("Response body rỗng (getAllRoomsForFilter page " + currentPage + ")");
                    jsonData = responseBody.string();
                } finally {
                    if (responseBody != null) responseBody.close();
                }

                if (!response.isSuccessful()) {
                    throw new IOException("Lấy danh sách phòng cho filter thất bại (trang " + currentPage + "). Mã lỗi: " + response.code() + ". Phản hồi: " + jsonData);
                }

                Type apiContainerType = new TypeToken<ApiContainerForPagedData<Facility>>() {}.getType(); // Vẫn dùng Facility vì API /rooms trả về Facility
                ApiContainerForPagedData<Facility> apiContainer = gson.fromJson(jsonData, apiContainerType);

                if (apiContainer != null && apiContainer.getCode() == 0 && apiContainer.getResult() != null && apiContainer.getResult().getPage() != null) {
                    ResultWithNestedPage<Facility> resultWithNestedPage = apiContainer.getResult();
                    List<Facility> facilitiesOnPage = resultWithNestedPage.getContent();
                    PageMetadata pageMetadata = resultWithNestedPage.getPage();

                    if (facilitiesOnPage != null) {
                        for (Facility facility : facilitiesOnPage) {
                            // Chuyển đổi từ Facility sang RoomItem (chỉ lấy ID và Name)
                            allRoomItems.add(new RoomItem(facility.getId(), facility.getName()));
                        }
                    }
                    totalPages = pageMetadata.getTotalPages();
                    currentPage++;
                    if (totalPages == 0 && !allRoomItems.isEmpty()) break;
                    if (totalPages == 0 && allRoomItems.isEmpty()) break;

                } else {
                    System.err.println("API response phòng (cho filter) không hợp lệ hoặc báo lỗi (trang " + currentPage + ")");
                    break;
                }
            } catch (JsonSyntaxException e) {
                System.err.println("Lỗi parse JSON phòng (cho filter): " + e.getMessage() + ". JSON: " + jsonData);
                throw new IOException("Lỗi parse JSON phòng (cho filter): " + e.getMessage(), e);
            }
        }
        System.out.println("Fetched " + allRoomItems.size() + " room items for filter.");
        return allRoomItems;
    }

    /**
     * Gửi yêu cầu POST để tạo phòng mới sử dụng dữ liệu từ Map.
     * Endpoint: POST /facility/rooms
     * @param roomData Map chứa thông tin phòng cần tạo.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi từ API.
     */
    public void addRoomFromMap(Map<String, Object> roomData) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại. Không thể thêm phòng.");
        }

        String url = BASE_URL + "/rooms";
        String jsonRequestBody = gson.toJson(roomData);
        System.out.println("Sending request (from map) to add room: " + jsonRequestBody);

        RequestBody body = RequestBody.create(jsonRequestBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body(); // Lấy body để xử lý và đóng
            if (!response.isSuccessful()) {
                String errorBodyString = (responseBody != null) ? responseBody.string() : "N/A";
                if(responseBody != null) responseBody.close();
                throw new IOException("Thêm phòng thất bại. Mã lỗi: " + response.code() + ". Phản hồi: " + errorBodyString);
            } else {
                System.out.println("Thêm phòng thành công! Code: " + response.code());
                if(responseBody != null) responseBody.close();
            }
        }
    }
    public List<Room> getRooms() throws IOException {
        String url = BASE_URL + "/rooms";
        Request request = buildAuthenticatedGetRequest(url);

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();

            if (!response.isSuccessful()) {
                String errorBody = (responseBody != null) ? responseBody.string() : "N/A";
                if(responseBody != null) responseBody.close();
                throw new IOException("Lấy danh sách phòng thất bại. Mã lỗi: " + response.code() + ". Phản hồi: " + errorBody);
            }
            if (responseBody == null) throw new IOException("Response body rỗng khi lấy danh sách phòng.");

            String jsonData;
            try { jsonData = responseBody.string(); } finally { responseBody.close(); }

            Type apiResponseType = new TypeToken<ApiResponse<Room>>() {}.getType();
            ApiResponse<Room> apiResponse = gson.fromJson(jsonData, apiResponseType);

            if (apiResponse == null || apiResponse.getCode() != 0 || apiResponse.getResult() == null || apiResponse.getResult().getContent() == null) {
                System.err.println("API response không hợp lệ hoặc báo lỗi. Code: " + (apiResponse != null ? apiResponse.getCode() : "N/A"));
                return Collections.emptyList();
            }
            return apiResponse.getResult().getContent();

        } catch (JsonSyntaxException e) {
            throw new IOException("Lỗi parse JSON khi lấy danh sách phòng: " + e.getMessage(), e);
        }
    }

    /**
     * Gửi yêu cầu DELETE để xóa một facility/room dựa trên ID.
     * Endpoint: DELETE /facility/rooms/{facilityId}
     * @param facilityId ID của facility/room cần xóa.
     * @return true nếu xóa thành công trên server.
     * @throws IOException Nếu có lỗi mạng hoặc lỗi từ API.
     */
    public boolean deleteFacilityById(String facilityId) throws IOException {
        String token = TokenStorage.getToken();
        if (token == null || token.isEmpty()) {
            throw new IOException("Token xác thực không tồn tại. Không thể xóa.");
        }
        if (facilityId == null || facilityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Facility ID không được để trống khi yêu cầu xóa.");
        }

        String url = BASE_URL + "/rooms/" + facilityId.trim();
        System.out.println("Sending DELETE request for facility ID: " + facilityId + " to URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body(); // Lấy body để xử lý và đóng
            if (!response.isSuccessful()) {
                String errorBodyString = (responseBody != null) ? responseBody.string() : "N/A";
                if(responseBody != null) responseBody.close();
                System.err.println("Xóa thất bại từ server. Mã lỗi: " + response.code() + ". Phản hồi: " + errorBodyString);
                throw new IOException("Xóa thất bại. Mã lỗi server: " + response.code() + ". Phản hồi: " + errorBodyString);
            } else {
                System.out.println("Xóa facility/room thành công! Code: " + response.code());
                if(responseBody != null) responseBody.close();
                return true;
            }
        }
    }

    /**
     * Helper method để xây dựng yêu cầu GET có xác thực.
     */
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


}