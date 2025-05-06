package com.utc2.facilityui.controller.booking; // Hoặc package đúng của bạn

import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.service.BookingService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window; // Cần để hiển thị Alert

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ResourceBundle;

public class MyBookingsController implements Initializable {

    // --- FXML Components (Đảm bảo fx:id khớp FXML của bạn) ---
    @FXML private TableView<BookingResponse> myBookingsTable;
    @FXML private TableColumn<BookingResponse, String> myRoomNameColumn;
    @FXML private TableColumn<BookingResponse, String> myPurposeColumn;
    @FXML private TableColumn<BookingResponse, String> myStatusColumn;
    @FXML private TableColumn<BookingResponse, LocalDateTime> myDateColumn;
    @FXML private TableColumn<BookingResponse, String> myTimeSlotColumn;
    @FXML private TableColumn<BookingResponse, LocalDateTime> myCreatedAtColumn;
    // Thêm cột Actions nếu cần (ví dụ: nút Cancel)
    // @FXML private TableColumn<BookingResponse, Void> myActionsColumn;

    @FXML private ComboBox<String> myRowsPerPageComboBox;
    @FXML private Label myPageInfoLabel;
    @FXML private Button myPreviousButton;
    @FXML private Button myNextButton;

    // --- Data List (Chỉ hiển thị trang hiện tại) ---
    private final ObservableList<BookingResponse> displayedMyBookings = FXCollections.observableArrayList();

    // --- Service ---
    private BookingService bookingService;

    // --- State Variables for Pagination ---
    private int currentPage = 0; // Backend dùng 0-based indexing
    private int currentPageSize = 10; // Default (nên khớp với giá trị mặc định của ComboBox)
    private int totalPages = 0;
    private long totalElements = 0;

    // --- Formatters ---
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookingService = new BookingService();

        setupTableColumns(); // Cấu hình cột
        setupControls();     // Cấu hình ComboBox phân trang
        loadMyBookingsData(); // Tải dữ liệu lần đầu
    }

    /**
     * Cấu hình các cột TableView
     */
    private void setupTableColumns() {
        // Đảm bảo các chuỗi trong PropertyValueFactory khớp tên getter trong BookingResponse
        myRoomNameColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        myPurposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        myStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Cột Ngày (Planned Start)
        myDateColumn.setCellValueFactory(new PropertyValueFactory<>("plannedStartTime"));
        myDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dateFormatter.format(item));
            }
        });
        myDateColumn.setStyle("-fx-alignment: CENTER;");

        // Cột Time Slot (Planned Start - End)
        myTimeSlotColumn.setCellValueFactory(cellData -> {
            BookingResponse booking = cellData.getValue();
            if (booking != null && booking.getPlannedStartTime() != null && booking.getPlannedEndTime() != null) {
                String start = timeFormatter.format(booking.getPlannedStartTime());
                String end = timeFormatter.format(booking.getPlannedEndTime());
                return new SimpleStringProperty(start + " - " + end);
            }
            return new SimpleStringProperty("");
        });
        myTimeSlotColumn.setStyle("-fx-alignment: CENTER;");

        // Cột Requested At (Created At)
        myCreatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        myCreatedAtColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dateTimeFormatter.format(item));
            }
        });

        // TODO: Thêm CellFactory cho cột Actions nếu có (ví dụ: nút Cancel)

        // Gán danh sách hiển thị và placeholder
        myBookingsTable.setItems(displayedMyBookings);
        myBookingsTable.setPlaceholder(new Label("Loading your bookings..."));
    }

    /**
     * Cấu hình trạng thái ban đầu cho các control (ComboBox)
     */
    private void setupControls() {
        myRowsPerPageComboBox.setItems(FXCollections.observableArrayList("5", "10", "20"));
        myRowsPerPageComboBox.setValue(String.valueOf(currentPageSize)); // Set giá trị mặc định

        // Vô hiệu hóa nút phân trang ban đầu
        myPreviousButton.setDisable(true);
        myNextButton.setDisable(true);
    }

    /**
     * Tải dữ liệu booking của người dùng hiện tại từ backend (chạy trên luồng nền)
     */
    private void loadMyBookingsData() {
        // Hiển thị trạng thái đang tải
        Platform.runLater(() -> {
            displayedMyBookings.clear(); // Xóa bảng khi đang tải
            myBookingsTable.setPlaceholder(new Label("Loading your bookings..."));
            myPageInfoLabel.setText("Loading...");
            myPreviousButton.setDisable(true);
            myNextButton.setDisable(true);
        });

        // Tạo Task để gọi API trên luồng nền
        Task<Page<BookingResponse>> fetchTask = new Task<>() {
            @Override
            protected Page<BookingResponse> call() throws Exception {
                System.out.printf("[FETCH MY] Requesting Page: %d, Size: %d%n", currentPage, currentPageSize);
                // *** GỌI PHƯƠNG THỨC ĐÚNG ***
                return bookingService.getMyBookingsPaged(currentPage, currentPageSize);
            }
        };

        // Xử lý khi Task thành công
        fetchTask.setOnSucceeded(event -> {
            Page<BookingResponse> resultPage = fetchTask.getValue();
            if (resultPage != null) {
                System.out.println("[FETCH MY] Success. Received page " + resultPage.getNumber() + "/" + (resultPage.getTotalPages()-1) + ", Elements: " + resultPage.getNumberOfElements() + "/" + resultPage.getTotalElements());
                updateMyBookingsUI(resultPage); // Cập nhật UI trên luồng JavaFX
            } else {
                // Xử lý trường hợp trả về null (bất thường)
                System.err.println("[FETCH MY] Succeeded but received null Page object.");
                updateMyBookingsUI(null);
            }
        });

        // Xử lý khi Task thất bại
        fetchTask.setOnFailed(event -> {
            Throwable exception = fetchTask.getException();
            System.err.println("[FETCH MY] Failed: " + exception.getMessage());
            exception.printStackTrace();
            Platform.runLater(() -> {
                myBookingsTable.setPlaceholder(new Label("Error loading your bookings."));
                myPageInfoLabel.setText("Error");
                showErrorAlert("Load Failed", "Could not load your bookings: " + exception.getMessage());
            });
        });

        // Chạy Task
        new Thread(fetchTask).start();
    }

    /**
     * Cập nhật TableView và các control phân trang sau khi nhận dữ liệu từ API.
     * Phải được gọi trên luồng JavaFX Application Thread.
     */
    private void updateMyBookingsUI(Page<BookingResponse> resultPage) {
        Platform.runLater(() -> { // Đảm bảo chạy trên luồng UI
            if (resultPage != null) {
                displayedMyBookings.setAll(resultPage.getContent() != null ? resultPage.getContent() : Collections.emptyList());
                totalPages = resultPage.getTotalPages();
                totalElements = resultPage.getTotalElements();
                currentPage = resultPage.getNumber(); // Cập nhật trang hiện tại (0-based)
                currentPageSize = resultPage.getSize(); // Cập nhật kích thước trang

                // Cập nhật Label thông tin trang (hiển thị 1-based)
                String pageText = String.format("Page %d of %d (%d items)",
                        currentPage + 1,
                        totalPages,
                        totalElements);
                myPageInfoLabel.setText(pageText);

                // Cập nhật trạng thái nút Previous/Next
                myPreviousButton.setDisable(resultPage.isFirst());
                myNextButton.setDisable(resultPage.isLast());

                // Cập nhật placeholder nếu danh sách rỗng
                myBookingsTable.setPlaceholder(new Label(resultPage.isEmpty() ? "You have no bookings." : null));

            } else {
                // Xử lý nếu resultPage là null
                displayedMyBookings.clear();
                totalPages = 0;
                totalElements = 0;
                currentPage = 0;
                myPageInfoLabel.setText("Page 1 of 1 (0 items)");
                myPreviousButton.setDisable(true);
                myNextButton.setDisable(true);
                myBookingsTable.setPlaceholder(new Label("Error loading data."));
            }
        });
    }

    // --- Event Handlers cho phân trang ---

    @FXML
    void handleMyRowsPerPageChange(ActionEvent event) {
        System.out.println("[PAGINATION MY] Rows per page changed.");
        try {
            int selectedRows = Integer.parseInt(myRowsPerPageComboBox.getValue());
            currentPageSize = (selectedRows > 0) ? selectedRows : 10; // Cập nhật biến state
        } catch (NumberFormatException | NullPointerException e) {
            currentPageSize = 10; // Về giá trị mặc định
            myRowsPerPageComboBox.setValue("10"); // Sửa lại UI nếu nhập sai
        }
        currentPage = 0; // Luôn về trang đầu khi đổi kích thước trang
        loadMyBookingsData(); // Tải lại dữ liệu
    }

    @FXML
    void handleMyPreviousPage(ActionEvent event) {
        System.out.println("[PAGINATION MY] Previous page requested.");
        if (currentPage > 0) {
            currentPage--; // Giảm chỉ số trang (0-based)
            loadMyBookingsData(); // Tải lại dữ liệu
        }
    }

    @FXML
    void handleMyNextPage(ActionEvent event) {
        System.out.println("[PAGINATION MY] Next page requested.");
        if (currentPage < totalPages - 1) {
            currentPage++; // Tăng chỉ số trang (0-based)
            loadMyBookingsData(); // Tải lại dữ liệu
        }
    }

    // --- Phương thức tiện ích ---
    private void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            TextArea textArea = new TextArea(message);
            textArea.setEditable(false); textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE); textArea.setMaxHeight(Double.MAX_VALUE);
            alert.getDialogPane().setContent(textArea); alert.setResizable(true);
            Window owner = getWindow(); // Lấy cửa sổ chủ
            if (owner != null) alert.initOwner(owner);
            alert.showAndWait();
        });
    }
    private Window getWindow() {
        try {
            if (myBookingsTable != null && myBookingsTable.getScene() != null) return myBookingsTable.getScene().getWindow();
            // Thêm các control khác nếu cần
        } catch (Exception e) {
            System.err.println("Could not reliably determine the window owner: " + e.getMessage());
        }
        return null;
    }
}