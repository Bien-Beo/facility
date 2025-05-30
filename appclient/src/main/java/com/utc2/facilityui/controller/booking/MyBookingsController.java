package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.*;
import com.utc2.facilityui.model.BookedEquipmentItem; // Đảm bảo import này tồn tại nếu BookedEquipmentItem ở package khác
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.service.BookingService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // Thêm import
import java.util.List;    // Thêm import
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MyBookingsController implements Initializable {

    @FXML private VBox cardsContainerVBox;
    @FXML private Label statusLabel; // Dùng để hiển thị thông báo lỗi chung nếu cần
    @FXML private ComboBox<String> myRowsPerPageComboBox;
    @FXML private Label myPageInfoLabel;
    @FXML private Button myPreviousButton;
    @FXML private Button myNextButton;

    private static MyBookingsController instance; // Giữ nguyên: Để truy cập tĩnh

    private BookingService bookingService;
    private int currentPage = 0;
    private int currentPageSize = 5; // Giá trị mặc định, sẽ được cập nhật từ ComboBox
    private int totalPages = 0;
    private long totalElements = 0;

    // Danh sách để lưu trữ các controller của card đang hiển thị
    private List<Object> activeCardControllers = new ArrayList<>();


    private final Locale vietnameseLocale = new Locale("vi", "VN");
    private final DateTimeFormatter VNF_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy", vietnameseLocale);
    private final DateTimeFormatter VNF_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", vietnameseLocale);
    private final DateTimeFormatter VNF_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy", vietnameseLocale);

    // Các hằng số trạng thái (giữ nguyên như file gốc của bạn)
    private static final String PENDING_APPROVAL_STATUS_FROM_API = "PENDING_APPROVAL";
    private static final String CONFIRMED_STATUS_FROM_API = "CONFIRMED";
    private static final String CANCELLED_STATUS_FROM_API = "CANCELLED";
    private static final String REJECTED_STATUS_FROM_API = "REJECTED";
    private static final String COMPLETED_STATUS_FROM_API = "COMPLETED";
    private static final String IN_PROGRESS_STATUS_FROM_API = "IN_PROGRESS";
    private static final String OVERDUE_STATUS_FROM_API = "OVERDUE"; // Quan trọng nếu server cũng có thể trả về trạng thái này


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // System.out.println("MyBookingsController: initialize() - Bắt đầu. Instance hiện tại (trước khi gán): " + MyBookingsController.instance);
        bookingService = new BookingService();
        instance = this; // Gán instance khi controller được khởi tạo
        // System.out.println("MyBookingsController: initialize() - Instance ĐÃ ĐƯỢC GÁN: " + instance);
        setupControls();
        loadMyBookingsData(); // Tải dữ liệu lần đầu
    }

    public static MyBookingsController getInstance() { // Phương thức để lấy instance
        if (instance == null) {
            System.err.println("MyBookingsController.getInstance(): LỖI - instance đang là null! Controller này có thể chưa được khởi tạo hoặc đã bị hủy.");
        }
        return instance;
    }

    public void refreshBookingsListPublic() { // Phương thức công khai để các controller khác có thể gọi làm mới
        // System.out.println("MyBookingsController: refreshBookingsListPublic() ĐƯỢC GỌI.");
        Platform.runLater(() -> {
            // System.out.println("MyBookingsController: refreshBookingsListPublic() - Bên trong Platform.runLater. Resetting currentPage về 0.");
            this.currentPage = 0; // Reset về trang đầu tiên khi làm mới
            loadMyBookingsData();  // Gọi lại hàm tải dữ liệu chính
        });
    }

    private void setupControls() {
        myRowsPerPageComboBox.setItems(FXCollections.observableArrayList("3", "5", "10", "20"));
        myRowsPerPageComboBox.setValue(String.valueOf(currentPageSize));
    }

    public void refreshBookingsList() { // Có thể được gọi bởi refreshBookingsListPublic hoặc các hành động nội bộ
        // System.out.println("MyBookingsController: refreshBookingsList() được gọi (có thể là nội bộ). Reset currentPage về 0.");
        this.currentPage = 0;
        loadMyBookingsData();
    }

    private void loadMyBookingsData() {
        try {
            currentPageSize = Integer.parseInt(myRowsPerPageComboBox.getValue());
        } catch (NumberFormatException e) {
            // System.err.println("MyBookingsController: loadMyBookingsData - Lỗi parse currentPageSize từ ComboBox, sử dụng giá trị hiện tại: " + currentPageSize);
            // Giữ nguyên currentPageSize hiện tại hoặc đặt giá trị mặc định
            currentPageSize = 5; // Hoặc giá trị mặc định bạn muốn
            myRowsPerPageComboBox.setValue(String.valueOf(currentPageSize)); // Cập nhật lại ComboBox
        }

        // System.out.println("MyBookingsController: loadMyBookingsData() ĐƯỢC GỌI. Trang hiện tại (0-based): " + currentPage + ", Kích thước trang: " + currentPageSize);
        Platform.runLater(() -> {
            // DỌN DẸP CÁC CONTROLLER CŨ TRƯỚC KHI XÓA CARD
            // System.out.println("Chuẩn bị dọn dẹp " + activeCardControllers.size() + " active card controllers cũ.");
            for (Object controller : activeCardControllers) {
                if (controller instanceof CardAcceptBookingController) {
                    // System.out.println("Dừng checker cho CardAcceptBookingController cũ.");
                    ((CardAcceptBookingController) controller).stopPotentialChecker();
                }
                // Thêm các instanceof khác nếu các loại card khác cũng có Timeline
            }
            activeCardControllers.clear(); // Xóa danh sách controller cũ

            cardsContainerVBox.getChildren().clear(); // Xóa các card UI cũ
            Label loadingLabel = new Label("Đang tải danh sách đặt phòng...");
            loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: grey; -fx-padding: 10px;");
            cardsContainerVBox.getChildren().add(loadingLabel);
            myPageInfoLabel.setText("Đang tải...");
            myPreviousButton.setDisable(true);
            myNextButton.setDisable(true);
            if (statusLabel != null) statusLabel.setVisible(false); // Ẩn status label cũ nếu có
        });

        Task<Page<BookingResponse>> fetchTask = new Task<>() {
            @Override
            protected Page<BookingResponse> call() throws Exception {
                // System.out.printf("[FETCH MY CARDS] Requesting Page: %d, Size: %d%n", currentPage, currentPageSize);
                Page<BookingResponse> response = bookingService.getMyBookingsPaged(currentPage, currentPageSize);
                // ... (log kết quả nếu cần) ...
                return response;
            }
        };

        fetchTask.setOnSucceeded(event -> {
            Page<BookingResponse> resultPage = fetchTask.getValue();
            // ... (log debug API response nếu cần) ...
            updateMyBookingsUI(resultPage);
        });

        fetchTask.setOnFailed(event -> {
            Throwable exception = fetchTask.getException();
            // System.err.println("[FETCH MY CARDS] Task Failed: " + exception.getMessage());
            // exception.printStackTrace();
            Platform.runLater(() -> {
                // Dọn dẹp controllers nếu task lỗi trước khi kịp cập nhật UI
                for (Object controller : activeCardControllers) {
                    if (controller instanceof CardAcceptBookingController) {
                        ((CardAcceptBookingController) controller).stopPotentialChecker();
                    }
                }
                activeCardControllers.clear();

                if(cardsContainerVBox != null) cardsContainerVBox.getChildren().clear(); // Xóa loading label
                Label errorLabel = new Label("Lỗi tải danh sách đặt phòng: " + exception.getMessage());
                errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 10px;");
                if(cardsContainerVBox != null) cardsContainerVBox.getChildren().add(errorLabel);
                if(myPageInfoLabel != null) myPageInfoLabel.setText("Lỗi tải dữ liệu");
                handleEmptyOrErrorResult(null);
                showErrorAlert("Tải thất bại", "Không thể tải danh sách đặt phòng của bạn: " + exception.getMessage());
            });
        });
        new Thread(fetchTask).start();
    }

    private void updateMyBookingsUI(Page<BookingResponse> resultPage) {
        Platform.runLater(() -> {
            // System.out.println("MyBookingsController: updateMyBookingsUI() ĐƯỢC GỌI.");
            // cardsContainerVBox đã được clear trong loadMyBookingsData() trên Platform.runLater trước đó
            // Chỉ cần xóa loading label nếu nó vẫn còn (trường hợp hiếm)
            if (!cardsContainerVBox.getChildren().isEmpty() && cardsContainerVBox.getChildren().get(0) instanceof Label) {
                Label firstChild = (Label) cardsContainerVBox.getChildren().get(0);
                if (firstChild.getText().startsWith("Đang tải")) {
                    cardsContainerVBox.getChildren().clear();
                }
            }

            if (resultPage != null && resultPage.getContent() != null && !resultPage.getContent().isEmpty()) {
                // System.out.printf("MyBookingsController: Sẽ hiển thị %d booking(s) từ resultPage (Page: %d, TotalElements: %d)%n",
                //         resultPage.getContent().size(), resultPage.getNumber(), resultPage.getTotalElements());

                for (BookingResponse bookingRes : resultPage.getContent()) {
                    if (bookingRes == null) {
                        System.err.println("WARNING: Gặp đối tượng BookingResponse null trong danh sách. Bỏ qua.");
                        continue;
                    }

                    String fxmlPath = null;
                    Object loadedController = null; // Biến để lưu controller đã load
                    try {
                        FXMLLoader loader;
                        Node cardNode;
                        String bookingStatus = bookingRes.getStatus();
                        // System.out.println("Processing Booking ID: " + bookingRes.getId() + ", API Status: '" + bookingStatus + "'");

                        URL cardFxmlUrl;

                        if (CONFIRMED_STATUS_FROM_API.equalsIgnoreCase(bookingStatus) ||
                                IN_PROGRESS_STATUS_FROM_API.equalsIgnoreCase(bookingStatus) ||
                                OVERDUE_STATUS_FROM_API.equalsIgnoreCase(bookingStatus) ) { // Bao gồm OVERDUE từ server
                            fxmlPath = "/com/utc2/facilityui/component/cardAcceptBooking.fxml";
                            cardFxmlUrl = getClass().getResource(fxmlPath);
                            if (cardFxmlUrl == null) { handleFxmlNotFoundError(fxmlPath, bookingRes.getId()); continue; }
                            loader = new FXMLLoader(cardFxmlUrl);
                            cardNode = loader.load();
                            CardAcceptBookingController controller = loader.getController();
                            CardAcceptBooking model = convertToCardAcceptBookingModel(bookingRes);
                            controller.setAcceptBooking(model);
                            loadedController = controller;
                        } else if (PENDING_APPROVAL_STATUS_FROM_API.equalsIgnoreCase(bookingStatus)) {
                            fxmlPath = "/com/utc2/facilityui/component/cardBooking.fxml";
                            cardFxmlUrl = getClass().getResource(fxmlPath);
                            if (cardFxmlUrl == null) { handleFxmlNotFoundError(fxmlPath, bookingRes.getId()); continue; }
                            loader = new FXMLLoader(cardFxmlUrl);
                            cardNode = loader.load();
                            CardBookingController controller = loader.getController();
                            CardBooking model = convertToCardBookingModel(bookingRes);
                            controller.setBooking(model);
                            controller.setMyBookingsController(this);
                            loadedController = controller;
                        } else if (CANCELLED_STATUS_FROM_API.equalsIgnoreCase(bookingStatus)) {
                            fxmlPath = "/com/utc2/facilityui/component/cardCancelBooking.fxml";
                            cardFxmlUrl = getClass().getResource(fxmlPath);
                            if (cardFxmlUrl == null) { handleFxmlNotFoundError(fxmlPath, bookingRes.getId()); continue; }
                            loader = new FXMLLoader(cardFxmlUrl);
                            cardNode = loader.load();
                            CardCancelBookingController controller = loader.getController();
                            CardCancelBooking model = convertToCardCancelBookingModel(bookingRes);
                            controller.setCancelBooking(model);
                            loadedController = controller;
                        } else if (REJECTED_STATUS_FROM_API.equalsIgnoreCase(bookingStatus)) {
                            fxmlPath = "/com/utc2/facilityui/component/cardRejectBooking.fxml";
                            cardFxmlUrl = getClass().getResource(fxmlPath);
                            if (cardFxmlUrl == null) { handleFxmlNotFoundError(fxmlPath, bookingRes.getId()); continue; }
                            loader = new FXMLLoader(cardFxmlUrl);
                            cardNode = loader.load();
                            CardRejectBookingController controller = loader.getController();
                            CardRejectBooking model = convertToCardRejectBookingModel(bookingRes);
                            controller.setRejectBooking(model);
                            loadedController = controller;
                        } else if (COMPLETED_STATUS_FROM_API.equalsIgnoreCase(bookingStatus)) {
                            fxmlPath = "/com/utc2/facilityui/component/cardCompleteBooking.fxml";
                            cardFxmlUrl = getClass().getResource(fxmlPath);
                            if (cardFxmlUrl == null) { handleFxmlNotFoundError(fxmlPath, bookingRes.getId()); continue; }
                            loader = new FXMLLoader(cardFxmlUrl);
                            cardNode = loader.load();
                            CardCompleteBookingController controller = loader.getController();
                            CardCompleteBooking model = convertToCardCompleteBookingModel(bookingRes);
                            controller.setCompleteBookingData(model);
                            loadedController = controller;
                        } else {
                            // System.out.println("Thông báo: Trạng thái '" + bookingStatus + "' của booking ID " + bookingRes.getId() + " sẽ dùng CardDefaultStatus.fxml.");
                            fxmlPath = "/com/utc2/facilityui/component/cardDefaultStatus.fxml";
                            cardFxmlUrl = getClass().getResource(fxmlPath);
                            if (cardFxmlUrl == null) {
                                // ... (handle FXML not found)
                                handleFxmlNotFoundError(fxmlPath, bookingRes.getId() + " (Card mặc định CardDefaultStatus.fxml bị thiếu)");
                                continue;
                            }
                            loader = new FXMLLoader(cardFxmlUrl);
                            cardNode = loader.load();
                            CardDefaultStatusController controller = loader.getController();
                            CardDefaultStatus model = convertToCardDefaultStatusModel(bookingRes);
                            controller.setData(model);
                            loadedController = controller;
                        }

                        if (loadedController != null) {
                            activeCardControllers.add(loadedController); // Thêm controller mới vào danh sách quản lý
                        }
                        cardsContainerVBox.getChildren().add(cardNode);

                    } catch (IOException e) {
                        handleCardLoadError("IOException", e, bookingRes.getId(), fxmlPath);
                    } catch (Exception e) {
                        handleCardLoadError("Exception (General)", e, bookingRes.getId(), fxmlPath);
                    }
                }
                updatePaginationControls(resultPage);
            } else {
                // System.out.println("MyBookingsController: updateMyBookingsUI - Không có booking nào để hiển thị hoặc resultPage/content là null/rỗng.");
                handleEmptyOrErrorResult(resultPage);
            }
        });
    }

    // --- CÁC PHƯƠNG THỨC CONVERT MODEL (GIỮ NGUYÊN NHƯ TRONG FILE GỐC CỦA BẠN) ---
    // (convertToCardBookingModel, convertToCardAcceptBookingModel, etc.)
    private CardBooking convertToCardBookingModel(BookingResponse bookingRes) {
        CardBooking cardModel = new CardBooking();
        cardModel.setBookingId(bookingRes.getId());
        cardModel.setNameBooking(bookingRes.getRoomName() != null ? bookingRes.getRoomName() : "Phòng không xác định");
        cardModel.setUserName(bookingRes.getUserName() != null ? bookingRes.getUserName() : "Không rõ người đặt");
        cardModel.setPurposeBooking(bookingRes.getPurpose());

        String startTimeStr = "N/A";
        String endTimeStr = "N/A";
        String dateStr = "N/A";
        if (bookingRes.getPlannedStartTime() != null) {
            startTimeStr = bookingRes.getPlannedStartTime().format(VNF_TIME_FORMATTER);
            dateStr = bookingRes.getPlannedStartTime().format(VNF_DATE_FORMATTER);
        }
        if (bookingRes.getPlannedEndTime() != null) {
            if (bookingRes.getPlannedStartTime() != null &&
                    bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_TIME_FORMATTER);
            } else {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_DATE_TIME_FORMATTER);
            }
        }
        if (bookingRes.getPlannedStartTime() != null && bookingRes.getPlannedEndTime() != null &&
                !bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
            cardModel.setTimeRangeDisplay(String.format("%s, %s - %s", startTimeStr, dateStr, endTimeStr));
        } else {
            cardModel.setTimeRangeDisplay(String.format("%s - %s, %s", startTimeStr, endTimeStr, dateStr));
        }


        if (bookingRes.getCreatedAt() != null) {
            cardModel.setRequestBooking(bookingRes.getCreatedAt().format(VNF_DATE_TIME_FORMATTER));
        } else {
            cardModel.setRequestBooking("N/A");
        }

        cardModel.setStatusBooking(bookingRes.getStatus());
        cardModel.setEquipmentsDisplay(formatEquipments(bookingRes.getBookedEquipments()));
        return cardModel;
    }

    private CardAcceptBooking convertToCardAcceptBookingModel(BookingResponse bookingRes) {
        CardAcceptBooking cardModel = new CardAcceptBooking();
        cardModel.setBookingId(bookingRes.getId());
        cardModel.setNameBooking(bookingRes.getRoomName() != null ? bookingRes.getRoomName() : "Phòng không xác định");
        cardModel.setUserName(bookingRes.getUserName() != null ? bookingRes.getUserName() : "Không rõ người đặt");
        cardModel.setPurposeBooking(bookingRes.getPurpose());

        String startTimeStr = "N/A";
        String endTimeStr = "N/A";
        String dateStr = "N/A";
        if (bookingRes.getPlannedStartTime() != null) {
            startTimeStr = bookingRes.getPlannedStartTime().format(VNF_TIME_FORMATTER);
            dateStr = bookingRes.getPlannedStartTime().format(VNF_DATE_FORMATTER);
        }
        if (bookingRes.getPlannedEndTime() != null) {
            if (bookingRes.getPlannedStartTime() != null &&
                    bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_TIME_FORMATTER);
            } else {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_DATE_TIME_FORMATTER);
            }
        }
        if (bookingRes.getPlannedStartTime() != null && bookingRes.getPlannedEndTime() != null &&
                !bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
            cardModel.setTimeRangeDisplay(String.format("%s, %s - %s", startTimeStr, dateStr, endTimeStr));
        } else {
            cardModel.setTimeRangeDisplay(String.format("%s - %s, %s", startTimeStr, endTimeStr, dateStr));
        }

        if (bookingRes.getCreatedAt() != null) {
            cardModel.setRequestBooking(bookingRes.getCreatedAt().format(VNF_DATE_TIME_FORMATTER));
        } else {
            cardModel.setRequestBooking("N/A");
        }
        cardModel.setApprovedByUserName(bookingRes.getApprovedByUserName());
        cardModel.setStatusBooking(bookingRes.getStatus());
        cardModel.setEquipmentsDisplay(formatEquipments(bookingRes.getBookedEquipments()));

        cardModel.setPlannedStartTime(bookingRes.getPlannedStartTime());
        cardModel.setPlannedEndTime(bookingRes.getPlannedEndTime());
        cardModel.setActualCheckInTime(bookingRes.getActualCheckInTime());

        return cardModel;
    }

    private CardRejectBooking convertToCardRejectBookingModel(BookingResponse bookingRes) {
        CardRejectBooking cardModel = new CardRejectBooking();
        cardModel.setBookingId(bookingRes.getId());
        cardModel.setNameBooking(bookingRes.getRoomName() != null ? bookingRes.getRoomName() : "N/A");
        cardModel.setUserName(bookingRes.getUserName() != null ? bookingRes.getUserName() : "Không rõ");
        cardModel.setPurposeBooking(bookingRes.getPurpose() != null && !bookingRes.getPurpose().isEmpty() ? bookingRes.getPurpose() : "");

        String startTimeStr = "N/A";
        String endTimeStr = "N/A";
        String dateStr = "N/A";
        if (bookingRes.getPlannedStartTime() != null) {
            startTimeStr = bookingRes.getPlannedStartTime().format(VNF_TIME_FORMATTER);
            dateStr = bookingRes.getPlannedStartTime().format(VNF_DATE_FORMATTER);
        }
        if (bookingRes.getPlannedEndTime() != null) {
            if (bookingRes.getPlannedStartTime() != null &&
                    bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_TIME_FORMATTER);
            } else {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_DATE_TIME_FORMATTER);
            }
        }
        if (bookingRes.getPlannedStartTime() != null && bookingRes.getPlannedEndTime() != null &&
                !bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
            cardModel.setTimeRangeDisplay(String.format("%s, %s - %s", startTimeStr, dateStr, endTimeStr));
        } else {
            cardModel.setTimeRangeDisplay(String.format("%s - %s, %s", startTimeStr, endTimeStr, dateStr));
        }

        if (bookingRes.getCreatedAt() != null) {
            cardModel.setRequestBooking(bookingRes.getCreatedAt().format(VNF_DATE_TIME_FORMATTER));
        } else {
            cardModel.setRequestBooking("N/A");
        }

        cardModel.setStatusBooking(bookingRes.getStatus());

        if (REJECTED_STATUS_FROM_API.equalsIgnoreCase(bookingRes.getStatus())) {
            String rejector = bookingRes.getApprovedByUserName();
            cardModel.setApprovedByUserName(rejector != null && !rejector.isEmpty() ? rejector : "Không rõ");
            String reason = bookingRes.getNote();
            if (reason == null || reason.isEmpty()) {
                reason = "Không có lý do được cung cấp.";
            }
            cardModel.setCancellationReasonDisplay(reason);

        } else {
            // System.err.println("CẢNH BÁO: convertToCardRejectBookingModel được gọi cho booking ID " + bookingRes.getId() + " với trạng thái không mong đợi: " + bookingRes.getStatus());
            cardModel.setApprovedByUserName("N/A");
            cardModel.setCancellationReasonDisplay("N/A");
        }
        cardModel.setEquipmentsDisplay(formatEquipments(bookingRes.getBookedEquipments()));
        return cardModel;
    }

    private CardCancelBooking convertToCardCancelBookingModel(BookingResponse bookingRes) {
        CardCancelBooking cardModel = new CardCancelBooking();
        cardModel.setBookingId(bookingRes.getId());
        cardModel.setNameBooking(bookingRes.getRoomName() != null ? bookingRes.getRoomName() : "N/A");
        cardModel.setUserName(bookingRes.getUserName() != null ? bookingRes.getUserName() : "Không rõ");
        cardModel.setPurposeBooking(bookingRes.getPurpose() != null ? bookingRes.getPurpose() : "");

        String startTimeStr = "N/A";
        String endTimeStr = "N/A";
        String dateStr = "N/A";
        if (bookingRes.getPlannedStartTime() != null) {
            startTimeStr = bookingRes.getPlannedStartTime().format(VNF_TIME_FORMATTER);
            dateStr = bookingRes.getPlannedStartTime().format(VNF_DATE_FORMATTER);
        }
        if (bookingRes.getPlannedEndTime() != null) {
            if (bookingRes.getPlannedStartTime() != null &&
                    bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_TIME_FORMATTER);
            } else {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_DATE_TIME_FORMATTER);
            }
        }
        if (bookingRes.getPlannedStartTime() != null && bookingRes.getPlannedEndTime() != null &&
                !bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
            cardModel.setTimeRangeDisplay(String.format("%s, %s - %s", startTimeStr, dateStr, endTimeStr));
        } else {
            cardModel.setTimeRangeDisplay(String.format("%s - %s, %s", startTimeStr, endTimeStr, dateStr));
        }

        if (bookingRes.getCreatedAt() != null) {
            cardModel.setRequestBooking(bookingRes.getCreatedAt().format(VNF_DATE_TIME_FORMATTER));
        } else {
            cardModel.setRequestBooking("N/A");
        }
        cardModel.setStatusBooking(bookingRes.getStatus());

        if (CANCELLED_STATUS_FROM_API.equalsIgnoreCase(bookingRes.getStatus())) {
            String canceller = bookingRes.getCancelledByUserName();
            if (canceller == null || canceller.isEmpty()) {
                // System.err.println("WARNING: API response for CANCELLED booking ID " + bookingRes.getId() + " is missing 'cancelledByUserName'. Falling back.");
                canceller = bookingRes.getUserName(); // Hiển thị người đặt nếu không có người hủy cụ thể
            }
            cardModel.setCancelledByUserName(canceller);

            String reason = bookingRes.getCancellationReason();
            if (reason == null || reason.isEmpty()) {
                reason = bookingRes.getNote();
            }
            if (reason == null || reason.isEmpty()) {
                reason = "Không có lý do được cung cấp.";
            }
            cardModel.setCancellationReason(reason);
        } else {
            cardModel.setCancelledByUserName("N/A");
            cardModel.setCancellationReason("N/A");
        }
        cardModel.setEquipmentsDisplay(formatEquipments(bookingRes.getBookedEquipments()));
        return cardModel;
    }

    private CardDefaultStatus convertToCardDefaultStatusModel(BookingResponse bookingRes) {
        CardDefaultStatus cardModel = new CardDefaultStatus();
        cardModel.setBookingId(bookingRes.getId());
        cardModel.setNameBooking(bookingRes.getRoomName() != null ? bookingRes.getRoomName() : "Phòng không xác định");
        cardModel.setUserName(bookingRes.getUserName() != null ? bookingRes.getUserName() : "Không rõ người đặt");
        cardModel.setPurposeBooking(bookingRes.getPurpose());

        String startTimeStr = "N/A";
        String endTimeStr = "N/A";
        String dateStr = "N/A";
        if (bookingRes.getPlannedStartTime() != null) {
            startTimeStr = bookingRes.getPlannedStartTime().format(VNF_TIME_FORMATTER);
            dateStr = bookingRes.getPlannedStartTime().format(VNF_DATE_FORMATTER);
        }
        if (bookingRes.getPlannedEndTime() != null) {
            if (bookingRes.getPlannedStartTime() != null &&
                    bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_TIME_FORMATTER);
            } else {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_DATE_TIME_FORMATTER);
            }
        }
        if (bookingRes.getPlannedStartTime() != null && bookingRes.getPlannedEndTime() != null &&
                !bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
            cardModel.setTimeRangeDisplay(String.format("%s, %s - %s", startTimeStr, dateStr, endTimeStr));
        } else {
            cardModel.setTimeRangeDisplay(String.format("%s - %s, %s", startTimeStr, endTimeStr, dateStr));
        }

        if (bookingRes.getCreatedAt() != null) {
            cardModel.setRequestBooking(bookingRes.getCreatedAt().format(VNF_DATE_TIME_FORMATTER));
        } else {
            cardModel.setRequestBooking("N/A");
        }
        cardModel.setStatusBooking(bookingRes.getStatus());
        cardModel.setEquipmentsDisplay(formatEquipments(bookingRes.getBookedEquipments()));
        return cardModel;
    }
    private CardCompleteBooking convertToCardCompleteBookingModel(BookingResponse bookingRes) {
        CardCompleteBooking cardModel = new CardCompleteBooking();
        cardModel.setBookingId(bookingRes.getId());
        cardModel.setNameBooking(bookingRes.getRoomName() != null ? bookingRes.getRoomName() : "Phòng không xác định");
        cardModel.setUserName(bookingRes.getUserName() != null ? bookingRes.getUserName() : "Không rõ người đặt");
        cardModel.setPurposeBooking(bookingRes.getPurpose() != null ? bookingRes.getPurpose() : "");

        String startTimeStr = "N/A";
        String endTimeStr = "N/A";
        String dateStr = "N/A";
        if (bookingRes.getPlannedStartTime() != null) {
            startTimeStr = bookingRes.getPlannedStartTime().format(VNF_TIME_FORMATTER);
            dateStr = bookingRes.getPlannedStartTime().format(VNF_DATE_FORMATTER);
        }
        if (bookingRes.getPlannedEndTime() != null) {
            if (bookingRes.getPlannedStartTime() != null &&
                    bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_TIME_FORMATTER);
            } else {
                endTimeStr = bookingRes.getPlannedEndTime().format(VNF_DATE_TIME_FORMATTER);
            }
        }

        if (bookingRes.getPlannedStartTime() != null && bookingRes.getPlannedEndTime() != null &&
                !bookingRes.getPlannedStartTime().toLocalDate().isEqual(bookingRes.getPlannedEndTime().toLocalDate())) {
            cardModel.setTimeRangeDisplay(String.format("%s, %s - %s", startTimeStr, dateStr, endTimeStr));
        } else {
            cardModel.setTimeRangeDisplay(String.format("%s - %s, %s", startTimeStr, endTimeStr, dateStr));
        }

        if (bookingRes.getCreatedAt() != null) {
            cardModel.setRequestBooking(bookingRes.getCreatedAt().format(VNF_DATE_TIME_FORMATTER));
        } else {
            cardModel.setRequestBooking("N/A");
        }
        cardModel.setActualCheckInTime(bookingRes.getActualCheckInTime());
        cardModel.setActualCheckOutTime(bookingRes.getActualCheckOutTime());
        cardModel.setStatusBooking(bookingRes.getStatus());
        cardModel.setEquipmentsDisplay(formatEquipments(bookingRes.getBookedEquipments()));
        return cardModel;
    }

    private String formatEquipments(List<BookedEquipmentItem> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            return null; // Hoặc trả về chuỗi rỗng "" nếu bạn muốn hiển thị thay vì ẩn HBox
        }
        return equipments.stream()
                .map(eq -> eq.getEquipmentModelName() + (eq.getQuantity() > 1 ? " (SL: " + eq.getQuantity() + ")" : ""))
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.joining(", "));
    }

    private void handleFxmlNotFoundError(String fxmlPath, String bookingId) {
        System.err.println("LỖI NGHIÊM TRỌNG: Không tìm thấy tệp FXML: " + fxmlPath + " cho booking ID: " + bookingId);
        Platform.runLater(() -> {
            Label errorCardLabel = new Label("Lỗi: FXML card (" + fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1) + ") không tìm thấy cho ID: " + bookingId);
            errorCardLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-padding: 10px;");
            cardsContainerVBox.getChildren().add(errorCardLabel);
        });
    }

    private void handleCardLoadError(String errorType, Exception e, String bookingId, String fxmlPathAttempted) {
        String pathInfo = (fxmlPathAttempted != null) ? fxmlPathAttempted : "Không rõ FXML";
        System.err.println("Lỗi " + errorType + " khi tải card (FXML: " + pathInfo + ") cho booking ID " + bookingId + ": " + e.getMessage());
        e.printStackTrace();
        Platform.runLater(() -> {
            Label cardErrorLabel = new Label("Lỗi " + errorType + " tải card cho ID: " + bookingId + ". Xem console log.");
            cardErrorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-padding: 10px;");
            cardsContainerVBox.getChildren().add(cardErrorLabel);
        });
    }

    private void updatePaginationControls(Page<BookingResponse> resultPage) {
        if (resultPage == null) {
            // System.err.println("updatePaginationControls: LỖI - resultPage là null. Không thể cập nhật phân trang.");
            if(myPageInfoLabel != null) myPageInfoLabel.setText("Lỗi tải thông tin trang");
            if(myPreviousButton != null) myPreviousButton.setDisable(true);
            if(myNextButton != null) myNextButton.setDisable(true);
            totalPages = 0;
            totalElements = 0;
            return;
        }
        // ... (log debug nếu cần) ...

        totalPages = resultPage.getTotalPages();
        totalElements = resultPage.getTotalElements();
        currentPage = resultPage.getNumber();
        currentPageSize = resultPage.getSize();

        String pageText;
        if (totalElements == 0) {
            pageText = "Trang 0 / 0 (Tổng: 0 mục)";
        } else {
            pageText = String.format("Trang %d / %d (Tổng: %d mục)",
                    currentPage + 1,
                    totalPages,
                    totalElements);
        }
        if(myPageInfoLabel != null) myPageInfoLabel.setText(pageText);
        if(myPreviousButton != null) myPreviousButton.setDisable(resultPage.isFirst());
        if(myNextButton != null) myNextButton.setDisable(resultPage.isLast() || totalPages == 0);
    }

    private void handleEmptyOrErrorResult(Page<BookingResponse> resultPage) {
        // System.out.println("[DEBUG EMPTY/ERROR] handleEmptyOrErrorResult ĐƯỢC GỌI.");
        String message;
        if (resultPage == null) {
            message = "Lỗi tải dữ liệu từ server.";
        } else if (resultPage.getContent() == null && resultPage.getTotalElements() > 0) {
            message = "Lỗi dữ liệu: API báo có mục nhưng không có nội dung.";
        } else if (resultPage.getContent() != null && resultPage.getContent().isEmpty()) {
            if (resultPage.getTotalElements() == 0) {
                message = "Bạn không có đặt phòng nào.";
            } else {
                message = "Không có đặt phòng nào trên trang này.";
            }
        } else {
            // Trường hợp này không nên xảy ra nếu đã có check resultPage.getContent().isEmpty() ở trên
            // nhưng để phòng hờ
            if (resultPage != null && resultPage.getContent() != null && !resultPage.getContent().isEmpty()){
                // Đã có nội dung, không cần hiển thị thông báo "không có..."
                updatePaginationControls(resultPage); // Chỉ cập nhật phân trang
                return;
            }
            message = "Không có đặt phòng nào được tìm thấy hoặc có lỗi xảy ra.";
        }

        // Chỉ thêm label nếu VBox rỗng hoặc không chứa label lỗi/đang tải
        if (cardsContainerVBox.getChildren().isEmpty() ||
                !(cardsContainerVBox.getChildren().get(0) instanceof Label &&
                        (((Label)cardsContainerVBox.getChildren().get(0)).getText().startsWith("Lỗi") ||
                                ((Label)cardsContainerVBox.getChildren().get(0)).getText().startsWith("Đang tải"))
                )) {
            Label noBookingsLabel = new Label(message);
            noBookingsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: grey; -fx-padding: 10px;");
            // Xóa các con cũ trước khi thêm label này để tránh trùng lặp label "Không có đặt phòng"
            cardsContainerVBox.getChildren().clear();
            cardsContainerVBox.getChildren().add(noBookingsLabel);
        }

        // Cập nhật phân trang ngay cả khi rỗng/lỗi
        // Nếu resultPage là null, tạo một Page rỗng để updatePaginationControls không bị lỗi
        Page<BookingResponse> pageToUpdateControls = resultPage != null ? resultPage : new Page<>();
        if (resultPage == null) { // Đặt lại các giá trị nếu resultPage hoàn toàn null
            pageToUpdateControls.setTotalElements(0);
            pageToUpdateControls.setTotalPages(0);
            pageToUpdateControls.setNumber(0);
            pageToUpdateControls.setSize(currentPageSize); // Giữ nguyên size đang chọn
            pageToUpdateControls.setFirst(true);
            pageToUpdateControls.setLast(true);
            pageToUpdateControls.setContent(java.util.Collections.emptyList());
        }
        updatePaginationControls(pageToUpdateControls);
    }


    @FXML void handleMyRowsPerPageChange(ActionEvent event) {
        // System.out.println("MyBookingsController: handleMyRowsPerPageChange - Giá trị mới: " + myRowsPerPageComboBox.getValue());
        try {
            int selectedRows = Integer.parseInt(myRowsPerPageComboBox.getValue());
            currentPageSize = (selectedRows > 0) ? selectedRows : 5;
        } catch (NumberFormatException | NullPointerException e) {
            // System.err.println("Lỗi khi parse số dòng/trang từ ComboBox: '" + myRowsPerPageComboBox.getValue() + "'. Đặt về mặc định là 5. Lỗi: " + e.getMessage());
            currentPageSize = 5;
            myRowsPerPageComboBox.setValue("5");
        }
        currentPage = 0;
        // System.out.println("MyBookingsController: handleMyRowsPerPageChange - Sẽ load lại dữ liệu với currentPageSize=" + currentPageSize + ", currentPage=0");
        loadMyBookingsData();
    }

    @FXML void handleMyPreviousPage(ActionEvent event) {
        // System.out.println("MyBookingsController: handleMyPreviousPage - Trang hiện tại (trước khi giảm): " + currentPage);
        if (currentPage > 0) {
            currentPage--;
            // System.out.println("MyBookingsController: handleMyPreviousPage - Sẽ load lại dữ liệu cho trang: " + currentPage);
            loadMyBookingsData();
        } else {
            // System.out.println("MyBookingsController: handleMyPreviousPage - Đã ở trang đầu, không làm gì.");
        }
    }

    @FXML void handleMyNextPage(ActionEvent event) {
        // System.out.println("MyBookingsController: handleMyNextPage - Trang hiện tại (trước khi tăng): " + currentPage + ", Tổng số trang: " + totalPages);
        if (currentPage < totalPages - 1) {
            currentPage++;
            // System.out.println("MyBookingsController: handleMyNextPage - Sẽ load lại dữ liệu cho trang: " + currentPage);
            loadMyBookingsData();
        } else {
            // System.out.println("MyBookingsController: handleMyNextPage - Đã ở trang cuối, không làm gì.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(title);
            TextArea textArea = new TextArea(message);
            textArea.setEditable(false); textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE); textArea.setMaxHeight(Double.MAX_VALUE);
            alert.getDialogPane().setContent(textArea); alert.setResizable(true);
            Window owner = getWindow();
            if (owner != null) {
                alert.initOwner(owner);
            }
            alert.showAndWait();
        });
    }

    private Window getWindow() {
        try {
            if (cardsContainerVBox != null && cardsContainerVBox.getScene() != null && cardsContainerVBox.getScene().getWindow() != null) {
                return cardsContainerVBox.getScene().getWindow();
            }
            if (myPageInfoLabel != null && myPageInfoLabel.getScene() != null && myPageInfoLabel.getScene().getWindow() != null) {
                return myPageInfoLabel.getScene().getWindow();
            }
            if (statusLabel != null && statusLabel.getScene() != null && statusLabel.getScene().getWindow() != null) {
                return statusLabel.getScene().getWindow();
            }
        } catch (Exception e) {
            // System.err.println("Không thể xác định Window owner một cách đáng tin cậy: " + e.getMessage());
        }
        return null;
    }
}