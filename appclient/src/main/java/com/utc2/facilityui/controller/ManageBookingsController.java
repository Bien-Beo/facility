package com.utc2.facilityui.controller;

// === CÁC IMPORT CẦN THIẾT ===
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.service.BookingService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent; // *** CHO onAction ***
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent; // *** CHO onKeyReleased ***
import javafx.stage.FileChooser;
import javafx.stage.Window;

// iText PDF Imports (Nếu giữ lại export)
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

// Java Standard Imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
// ===========================

public class ManageBookingsController implements Initializable {

    // --- FXML Components (Đảm bảo fx:id khớp FXML) ---
    @FXML private TableView<BookingResponse> bookingsTable;
    @FXML private TableColumn<BookingResponse, String> roomNameColumn;
    @FXML private TableColumn<BookingResponse, String> userNameColumn;
    @FXML private TableColumn<BookingResponse, String> purposeColumn;
    @FXML private TableColumn<BookingResponse, String> statusColumn;
    @FXML private TableColumn<BookingResponse, LocalDateTime> dateColumn;
    @FXML private TableColumn<BookingResponse, String> timeSlotColumn;
    @FXML private TableColumn<BookingResponse, LocalDateTime> createdAtColumn;
    @FXML private TableColumn<BookingResponse, String> approvedByColumn;

    @FXML private ComboBox<String> monthComboBox;
    @FXML private TextField yearTextField;
    @FXML private TextField roomSearchField;
    @FXML private TextField userSearchField;

    @FXML private ComboBox<String> rowsPerPageComboBox;
    @FXML private Label pageInfoLabel;
    @FXML private Button previousButton;
    @FXML private Button nextButton;

    // --- Data List (Chỉ hiển thị trang hiện tại) ---
    private final ObservableList<BookingResponse> displayedBookings = FXCollections.observableArrayList();

    // --- Service ---
    private BookingService bookingService;

    // --- State Variables ---
    private int currentPage = 0; // 0-based for API
    private int currentPageSize = 6; // Default (PHẢI > 0)
    private int totalPages = 0;
    private long totalElements = 0;

    // --- Formatters ---
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Map<String, Integer> monthMap = Stream.of(Month.values())
            .collect(Collectors.toMap(
                    m -> m.getDisplayName(TextStyle.FULL, Locale.ENGLISH), // Dùng English để khớp giá trị ComboBox
                    Month::getValue
            ));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookingService = new BookingService();
        setupTableColumns();
        setupControls();
        fetchAndDisplayBookings(); // Tải dữ liệu ban đầu
    }

    private void setupTableColumns() {
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        approvedByColumn.setCellValueFactory(new PropertyValueFactory<>("approvedByUserName"));

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("plannedStartTime"));
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dateFormatter.format(item));
            }
        });
        dateColumn.setStyle("-fx-alignment: CENTER;");

        timeSlotColumn.setCellValueFactory(cellData -> {
            BookingResponse booking = cellData.getValue();
            if (booking != null && booking.getPlannedStartTime() != null && booking.getPlannedEndTime() != null) {
                return new SimpleStringProperty(timeFormatter.format(booking.getPlannedStartTime()) + " - " + timeFormatter.format(booking.getPlannedEndTime()));
            }
            return new SimpleStringProperty("");
        });
        timeSlotColumn.setStyle("-fx-alignment: CENTER;");

        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dateTimeFormatter.format(item));
            }
        });

        bookingsTable.setItems(displayedBookings);
        bookingsTable.setPlaceholder(new Label("Loading bookings..."));
    }

    private void setupControls() {
        // Lấy danh sách tên tháng từ Map để đưa vào ComboBox
        List<String> monthNames = new ArrayList<>(monthMap.keySet());
        // Sắp xếp tên tháng theo thứ tự lịch (tùy chọn)
        monthNames.sort(Comparator.comparingInt(monthName -> monthMap.getOrDefault(monthName, 0)));
        monthComboBox.setItems(FXCollections.observableArrayList(monthNames));
        monthComboBox.setPromptText("Select Month");

        rowsPerPageComboBox.setItems(FXCollections.observableArrayList("6", "10", "20", "50"));
        // Đặt giá trị mặc định cho ComboBox và biến state
        currentPageSize = 6; // Đặt giá trị mặc định > 0
        rowsPerPageComboBox.setValue(String.valueOf(currentPageSize));

        previousButton.setDisable(true);
        nextButton.setDisable(true);
    }

    // ==============================================================
    // === PHƯƠNG THỨC QUAN TRỌNG ĐỂ TẢI DỮ LIỆU (ĐÃ SỬA LỖI size=0) ===
    // ==============================================================
    private void fetchAndDisplayBookings() {
        // +++ KIỂM TRA VÀ ĐẢM BẢO currentPageSize LUÔN > 0 +++
        if (this.currentPageSize <= 0) {
            System.err.println("[WARN] currentPageSize was invalid (" + this.currentPageSize + "). Resetting to default (6).");
            this.currentPageSize = 6; // Đặt lại về giá trị mặc định an toàn
            // Cập nhật lại ComboBox nếu cần
            if (rowsPerPageComboBox != null) {
                rowsPerPageComboBox.setValue(String.valueOf(this.currentPageSize));
            }
        }
        // +++ KẾT THÚC KIỂM TRA +++

        // Hiển thị trạng thái đang tải
        Platform.runLater(() -> {
            displayedBookings.clear();
            bookingsTable.setPlaceholder(new Label("Loading bookings..."));
            pageInfoLabel.setText("Loading...");
            previousButton.setDisable(true);
            nextButton.setDisable(true);
        });

        // Lấy giá trị bộ lọc (khai báo final để dùng trong Task)
        final String roomIdFilter = roomSearchField.getText().trim();
        final String userFilter = userSearchField.getText().trim();
        final Integer monthFilter = monthMap.get(monthComboBox.getValue()); // Lấy số tháng từ tên tháng đã chọn
        Integer tempYearFilter = null;
        try {
            if (yearTextField.getText() != null && !yearTextField.getText().trim().isEmpty()) {
                tempYearFilter = Integer.parseInt(yearTextField.getText().trim());
            }
        } catch (NumberFormatException e) { System.err.println("Invalid year input: " + yearTextField.getText()); }
        final Integer yearFilter = tempYearFilter;
        final int pageToFetch = this.currentPage; // Sử dụng biến final cho Task
        final int sizeToFetch = this.currentPageSize; // Sử dụng biến final cho Task (đã được kiểm tra > 0)


        // Tạo Task chạy nền
        Task<Page<BookingResponse>> fetchTask = new Task<>() {
            @Override
            protected Page<BookingResponse> call() throws Exception {
                System.out.printf("[FETCH] Requesting Page: %d, Size: %d, Room: '%s', User: '%s', Month: %s, Year: %s%n",
                        pageToFetch, sizeToFetch, roomIdFilter, userFilter, monthFilter, yearFilter);
                // Gọi service để lấy dữ liệu
                return bookingService.getAllBookings(
                        roomIdFilter.isEmpty() ? null : roomIdFilter,
                        monthFilter,
                        yearFilter,
                        userFilter.isEmpty() ? null : userFilter,
                        pageToFetch,
                        sizeToFetch // Truyền size đã được kiểm tra
                );
            }
        };

        // Xử lý khi Task thành công
        fetchTask.setOnSucceeded(event -> {
            Page<BookingResponse> resultPage = fetchTask.getValue();
            if (resultPage != null) {
                System.out.println("[FETCH] Success. Received page " + resultPage.getNumber() + "/" + (resultPage.getTotalPages() > 0 ? resultPage.getTotalPages()-1 : 0) + ", Elements: " + resultPage.getNumberOfElements() + "/" + resultPage.getTotalElements());
                updateUIAfterFetch(resultPage); // Cập nhật UI
            } else {
                System.err.println("[FETCH] Succeeded but received null Page object.");
                updateUIAfterFetch(null); // Xử lý trường hợp null
            }
        });

        // Xử lý khi Task thất bại
        fetchTask.setOnFailed(event -> {
            Throwable exception = fetchTask.getException();
            System.err.println("[FETCH] Failed: " + exception.getMessage());
            exception.printStackTrace();
            Platform.runLater(() -> {
                bookingsTable.setPlaceholder(new Label("Error loading bookings."));
                pageInfoLabel.setText("Error");
                showErrorAlert("Load Failed", "Could not load bookings: " + exception.getMessage());
                previousButton.setDisable(true); // Vô hiệu hóa nút khi lỗi
                nextButton.setDisable(true);
            });
        });

        new Thread(fetchTask).start(); // Bắt đầu Task
    }

    // Cập nhật UI sau khi fetch dữ liệu
    private void updateUIAfterFetch(Page<BookingResponse> resultPage) {
        Platform.runLater(() -> {
            if (resultPage != null) {
                displayedBookings.setAll(resultPage.getContent() != null ? resultPage.getContent() : Collections.emptyList());
                totalPages = resultPage.getTotalPages();
                totalElements = resultPage.getTotalElements();
                currentPage = resultPage.getNumber(); // Cập nhật trang hiện tại (0-based)
                currentPageSize = resultPage.getSize(); // Cập nhật kích thước trang thực tế từ phản hồi

                // Cập nhật label thông tin trang (hiển thị 1-based)
                String pageText = String.format("Page %d of %d (%d items)",
                        (totalElements == 0 ? 0 : currentPage + 1), // Hiển thị trang 0 nếu không có item nào
                        totalPages,
                        totalElements);
                pageInfoLabel.setText(pageText);

                // Cập nhật trạng thái nút Previous/Next
                previousButton.setDisable(resultPage.isFirst());
                nextButton.setDisable(resultPage.isLast());

                // Cập nhật placeholder nếu trang trống
                bookingsTable.setPlaceholder(new Label(resultPage.isEmpty() ? "No bookings match filters." : null));

            } else {
                // Xử lý nếu resultPage là null
                displayedBookings.clear();
                totalPages = 0; totalElements = 0; currentPage = 0;
                pageInfoLabel.setText("Page 0 of 0 (0 items)"); // Cập nhật label
                previousButton.setDisable(true); nextButton.setDisable(true);
                bookingsTable.setPlaceholder(new Label("Error loading data."));
            }
        });
    }


    // ==========================================================
    // === CÁC PHƯƠNG THỨC XỬ LÝ SỰ KIỆN (ĐÃ SỬA LỖI THAM SỐ) ===
    // ==========================================================

    // --- Dùng cho onKeyReleased từ TextFields ---
    @FXML
    void handleFilterChange(KeyEvent event) {
        System.out.println("[FILTER] Key event triggered filter change.");
        currentPage = 0; // Reset về trang đầu khi lọc
        fetchAndDisplayBookings(); // Gọi hàm tải lại dữ liệu
    }

    // --- Dùng cho onAction từ ComboBox và Year TextField ---
    @FXML
    void handleFilterChange(ActionEvent event) {
        System.out.println("[FILTER] Action event triggered filter change.");
        currentPage = 0; // Reset về trang đầu khi lọc
        fetchAndDisplayBookings(); // Gọi hàm tải lại dữ liệu
    }
    // ----------------------------------------------------------


    @FXML
    void handleResetFilters(ActionEvent event) {
        System.out.println("[FILTER] Resetting filters.");
        // Xóa các control lọc
        monthComboBox.setValue(null);
        yearTextField.clear();
        roomSearchField.clear();
        userSearchField.clear();

        // Đặt lại kích thước trang về mặc định
        currentPageSize = 6; // Hoặc giá trị mặc định của bạn
        rowsPerPageComboBox.setValue(String.valueOf(currentPageSize));

        // Đặt lại trang và tải lại
        currentPage = 0;
        fetchAndDisplayBookings();
    }

    @FXML
    void handleRowsPerPageChange(ActionEvent event) {
        System.out.println("[PAGINATION] Rows per page changed.");
        try {
            int selectedRows = Integer.parseInt(rowsPerPageComboBox.getValue());
            // Đảm bảo size luôn > 0
            currentPageSize = (selectedRows > 0) ? selectedRows : 6; // Nếu <=0 thì về mặc định
        } catch (NumberFormatException | NullPointerException e) {
            currentPageSize = 6; // Mặc định nếu lỗi
        }
        // Không cần setValue lại ở đây vì giá trị đã được lấy và kiểm tra
        // rowsPerPageComboBox.setValue(String.valueOf(currentPageSize)); // Có thể bỏ dòng này
        currentPage = 0; // Về trang đầu
        fetchAndDisplayBookings();
    }

    @FXML
    void handlePreviousPage(ActionEvent event) {
        System.out.println("[PAGINATION] Previous page requested.");
        if (currentPage > 0) {
            currentPage--;
            fetchAndDisplayBookings();
        }
    }

    @FXML
    void handleNextPage(ActionEvent event) {
        System.out.println("[PAGINATION] Next page requested.");
        if (currentPage < totalPages - 1) {
            currentPage++;
            fetchAndDisplayBookings();
        }
    }

    // --- Export Functionality (Giữ nguyên logic export trang hiện tại) ---
    @FXML
    public void handleExportBookings(ActionEvent actionEvent) {
        if (displayedBookings.isEmpty()) {
            showInfoAlert("No Data", "There is no data currently displayed to export.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Current Page as PDF");
        fileChooser.setInitialFileName("Bookings_Page_" + (currentPage + 1) + "_" + java.time.LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        Window stage = getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            System.out.println("[EXPORT] Exporting current page data to PDF: " + filePath);
            // Tạo bản sao của danh sách hiện tại để truyền vào luồng nền
            List<BookingResponse> dataToExport = new ArrayList<>(displayedBookings);
            new Thread(() -> createPdf(filePath, dataToExport)).start();
        } else {
            System.out.println("[EXPORT] PDF Export cancelled by user.");
        }
    }

    private void createPdf(String filePath, List<BookingResponse> dataToExport) {
        System.out.println("[EXPORT] Starting PDF creation with " + dataToExport.size() + " items...");
        try (FileOutputStream fos = new FileOutputStream(filePath);
             PdfWriter writer = new PdfWriter(fos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Bookings Export (Current View)").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Generated on: " + dateTimeFormatter.format(LocalDateTime.now())).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            float[] columnWidths = {2f, 1.5f, 2f, 1f, 1.5f, 1.5f, 2f, 1.5f};
            Table pdfTable = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            addPdfHeaderCell(pdfTable, "Room Name"); addPdfHeaderCell(pdfTable, "Requested By"); addPdfHeaderCell(pdfTable, "Purpose"); addPdfHeaderCell(pdfTable, "Status"); addPdfHeaderCell(pdfTable, "Date"); addPdfHeaderCell(pdfTable, "Time Slot"); addPdfHeaderCell(pdfTable, "Requested At"); addPdfHeaderCell(pdfTable, "Processed By");

            if (dataToExport.isEmpty()) {
                document.add(new Paragraph("No booking data to export in this view.").setTextAlignment(TextAlignment.CENTER));
            } else {
                for (BookingResponse booking : dataToExport) {
                    if (booking == null) continue;
                    pdfTable.addCell(getStringOrEmpty(booking.getRoomName()));
                    pdfTable.addCell(getStringOrEmpty(booking.getUserName()));
                    pdfTable.addCell(getStringOrEmpty(booking.getPurpose()));
                    pdfTable.addCell(getStringOrEmpty(booking.getStatus()));
                    pdfTable.addCell(booking.getPlannedStartTime() != null ? dateFormatter.format(booking.getPlannedStartTime()) : "");
                    String timeSlotStr = "";
                    if (booking.getPlannedStartTime() != null && booking.getPlannedEndTime() != null) {
                        timeSlotStr = timeFormatter.format(booking.getPlannedStartTime()) + " - " + timeFormatter.format(booking.getPlannedEndTime());
                    }
                    pdfTable.addCell(timeSlotStr);
                    pdfTable.addCell(booking.getCreatedAt() != null ? dateTimeFormatter.format(booking.getCreatedAt()) : "");
                    pdfTable.addCell(getStringOrEmpty(booking.getApprovedByUserName()));
                }
                document.add(pdfTable);
            }
            System.out.println("[EXPORT] PDF created successfully at: " + filePath);
            Platform.runLater(() -> showInfoAlert("Export Successful", "Current page data exported successfully to:\n" + filePath));
        } catch (Exception e) {
            System.err.println("[EXPORT] Error during PDF export: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> showErrorAlert("Export Failed", "An error occurred during export:\n" + e.getMessage()));
        }
        System.out.println("[EXPORT] createPdf() finished.");
    }


    private void addPdfHeaderCell(Table table, String text) {
        table.addHeaderCell(new Paragraph(text).setBold().setFontSize(10));
    }

    // --- Utility Methods ---
    private String getStringOrEmpty(String str) { return str != null ? str : ""; }
    private void showInfoAlert(String title, String message) { showAlert(Alert.AlertType.INFORMATION, title, message); }
    private void showErrorAlert(String title, String message) { showAlert(Alert.AlertType.ERROR, title, message); }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showAlert(alertType, title, message));
            return;
        }
        Alert alert = new Alert(alertType);
        alert.setTitle(title); alert.setHeaderText(null);
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false); textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE); textArea.setMaxHeight(Double.MAX_VALUE);
        alert.getDialogPane().setContent(textArea); alert.setResizable(true);
        Window owner = getWindow();
        if (owner != null) alert.initOwner(owner);
        alert.showAndWait();
    }
    private Window getWindow() {
        try {
            if (bookingsTable != null && bookingsTable.getScene() != null) return bookingsTable.getScene().getWindow();
            if (monthComboBox != null && monthComboBox.getScene() != null) return monthComboBox.getScene().getWindow();
        } catch (Exception e) { System.err.println("Could not reliably determine the window owner: " + e.getMessage()); }
        return null;
    }
}