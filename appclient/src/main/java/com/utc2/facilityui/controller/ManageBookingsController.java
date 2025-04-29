package com.utc2.facilityui.controller;

// JavaFX Imports
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

// iText PDF Imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

// Project Specific Imports
import com.utc2.facilityui.response.BookingResponse; // DTO
import com.utc2.facilityui.service.BookingService; // Service

// Java Standard Imports
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month; // Cần import Month
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle; // Cần FormatStyle
import java.time.format.TextStyle;
import java.util.Collections; // Thêm import này
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.Objects;


/**
 * Controller for the Manage Bookings screen.
 * This implementation loads ALL bookings using BookingService.getAllBookings(),
 * suitable for an administrator view.
 */
public class ManageBookingsController implements Initializable {

    // --- FXML Components ---
    @FXML private TableView<BookingResponse> bookingsTable;
    @FXML private TableColumn<BookingResponse, String> roomNameColumn;
    @FXML private TableColumn<BookingResponse, String> userNameColumn;
    @FXML private TableColumn<BookingResponse, String> purposeColumn;
    @FXML private TableColumn<BookingResponse, LocalDateTime> dateColumn;
    @FXML private TableColumn<BookingResponse, String> timeSlotColumn;
    @FXML private TableColumn<BookingResponse, LocalDateTime> createdAtColumn;
    @FXML private TableColumn<BookingResponse, String> approvedByColumn;
    @FXML private TableColumn<BookingResponse, String> statusColumn;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private TextField yearTextField;
    @FXML private TextField facilityTextField; // Searches Room Name
    @FXML private TextField employeeIdTextField; // Searches User Name
    @FXML private ComboBox<String> rowsPerPageComboBox;
    @FXML private Label currentPageLabel;

    // --- Data Lists ---
    private ObservableList<BookingResponse> allBookingData = FXCollections.observableArrayList();
    private ObservableList<BookingResponse> filteredBookingData = FXCollections.observableArrayList();
    private ObservableList<BookingResponse> currentViewData = FXCollections.observableArrayList();

    // --- Service ---
    private BookingService bookingService;

    // --- State Variables ---
    private int currentPage = 1;
    private int rowsPerPage = 6; // Default rows per page

    // --- Formatters ---
    // Sử dụng Cách 1: Định dạng chuẩn hóa
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL); //.withLocale(Locale.ENGLISH);
    // Các formatter khác
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookingService = new BookingService(); // Initialize the service

        setupTableColumns(); // Configure table columns and cell factories
        setupControls(); // Configure initial state of ComboBoxes etc.

        loadBookingData(); // Load initial data (all bookings) from the service
    }

    /**
     * Configures the TableView columns, setting cell value factories and cell factories.
     */
    private void setupTableColumns() {
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        approvedByColumn.setCellValueFactory(new PropertyValueFactory<>("approvedByUserName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Date column (Planned Start Time - Date only)
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("plannedStartTime"));
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || dateFormatter == null) {
                    setText(null);
                } else {
                    try { setText(dateFormatter.format(item)); }
                    catch (Exception e) { setText("Invalid Date"); System.err.println("Error formatting date in cell factory: " + e.getMessage()); }
                }
            }
        });

        // Time Slot column (Planned Start Time - Planned End Time)
        timeSlotColumn.setCellValueFactory(cellData -> {
            BookingResponse booking = cellData.getValue();
            if (booking == null) return new SimpleStringProperty("");
            LocalDateTime start = booking.getPlannedStartTime();
            LocalDateTime end = booking.getPlannedEndTime();
            String timeSlot = "N/A";
            if (start != null && end != null) {
                if (timeFormatter != null) {
                    try { timeSlot = timeFormatter.format(start) + " - " + timeFormatter.format(end); }
                    catch (Exception e) { timeSlot = "Invalid Time"; }
                } else { timeSlot = "Formatter Error"; }
            } else if (start != null) {
                if (timeFormatter != null) {
                    try { timeSlot = timeFormatter.format(start) + " - ?"; } catch (Exception e) { timeSlot = "Invalid Time"; }
                } else { timeSlot = "Formatter Error"; }
            } else if (end != null) {
                if (timeFormatter != null) {
                    try { timeSlot = "? - " + timeFormatter.format(end); } catch (Exception e) { timeSlot = "Invalid Time"; }
                } else { timeSlot = "Formatter Error"; }
            }
            return new SimpleStringProperty(timeSlot);
        });
        timeSlotColumn.setStyle("-fx-alignment: CENTER;");

        // Created At column
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || dateTimeFormatter == null) {
                    setText(null);
                } else {
                    try { setText(dateTimeFormatter.format(item)); }
                    catch (Exception e) { setText("Invalid DateTime"); System.err.println("Error formatting datetime in cell factory: " + e.getMessage()); }
                }
            }
        });

        bookingsTable.setPlaceholder(new Label("No bookings found."));
    }

    /**
     * Sets up initial values and items for controls like ComboBoxes.
     */
    private void setupControls() {
        monthComboBox.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        rowsPerPageComboBox.setItems(FXCollections.observableArrayList("6", "10", "20"));
        rowsPerPageComboBox.setValue(String.valueOf(rowsPerPage));
    }

    /**
     * Loads ALL booking data from the BookingService using getAllBookings().
     * Handles potential IOExceptions and updates the UI accordingly.
     */
    private void loadBookingData() {
        try {
            System.out.println("Attempting to fetch all bookings..."); // Log bắt đầu
            List<BookingResponse> fetchedData = bookingService.getAllBookings(); // Gọi hàm getAllBookings

            // *** ĐÃ XÓA PHẦN DEBUG CONSOLE Ở ĐÂY ***

            // Cập nhật UI trên JavaFX Application Thread
            final List<BookingResponse> finalFetchedData = fetchedData;
            Platform.runLater(() -> {
                allBookingData.setAll(finalFetchedData != null ? finalFetchedData : Collections.emptyList());
                System.out.println("Loaded " + allBookingData.size() + " total bookings (Admin) from service into ObservableList.");
                applyFiltersAndPagination(); // Áp dụng filter và hiển thị trang đầu
            });

        } catch (IOException e) {
            System.err.println("Error loading ALL booking data from service: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                allBookingData.clear();
                applyFiltersAndPagination();
                showErrorAlert("Load Data Failed", "Could not load booking data from server: \n" + e.getMessage());
            });
        } catch (Exception e) {
            System.err.println("Unexpected error loading ALL bookings: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                allBookingData.clear();
                applyFiltersAndPagination();
                showErrorAlert("Unexpected Error", "An unexpected error occurred while loading bookings: \n" + e.getClass().getSimpleName());
            });
        }
    }

    // --- Filtering and Pagination Logic ---
    private void applyFiltersAndPagination() {
        String selectedMonth = monthComboBox.getValue();
        String yearText = yearTextField.getText().trim();
        String roomSearch = facilityTextField.getText().trim().toLowerCase();
        String userSearch = employeeIdTextField.getText().trim().toLowerCase();

        List<BookingResponse> newlyFilteredData = allBookingData.stream()
                .filter(Objects::nonNull)
                .filter(b -> filterByMonth(b, selectedMonth))
                .filter(b -> filterByYear(b, yearText))
                .filter(b -> filterByRoom(b, roomSearch))
                .filter(b -> filterByUser(b, userSearch))
                .collect(Collectors.toList());

        filteredBookingData.setAll(newlyFilteredData);
        currentPage = 1; // Luôn về trang 1 khi filter thay đổi
        updateTableViewPage(); // Cập nhật hiển thị bảng
    }

    // Helper methods for filtering conditions
    private boolean filterByMonth(BookingResponse booking, String selectedMonth) {
        if (booking.getPlannedStartTime() == null) return selectedMonth == null || selectedMonth.isEmpty();
        if (selectedMonth != null && !selectedMonth.isEmpty()) {
            String bookingMonth = booking.getPlannedStartTime().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            return bookingMonth.equalsIgnoreCase(selectedMonth);
        }
        return true;
    }

    private boolean filterByYear(BookingResponse booking, String yearText) {
        if (booking.getPlannedStartTime() == null) return yearText == null || yearText.isEmpty();
        if (!yearText.isEmpty()) {
            try { return booking.getPlannedStartTime().getYear() == Integer.parseInt(yearText); }
            catch (NumberFormatException e) { return true; }
        }
        return true;
    }

    private boolean filterByRoom(BookingResponse booking, String roomSearch) {
        if (!roomSearch.isEmpty()) {
            return booking.getRoomName() != null && booking.getRoomName().toLowerCase().contains(roomSearch);
        }
        return true;
    }

    private boolean filterByUser(BookingResponse booking, String userSearch) {
        if (!userSearch.isEmpty()) {
            return booking.getUserName() != null && booking.getUserName().toLowerCase().contains(userSearch);
        }
        return true;
    }

    private void updateTableViewPage() {
        currentViewData.clear();
        int startIndex = (currentPage - 1) * rowsPerPage;
        int endIndex = Math.min(startIndex + rowsPerPage, filteredBookingData.size());
        if (startIndex >= 0 && startIndex < filteredBookingData.size()) {
            currentViewData.addAll(filteredBookingData.subList(startIndex, endIndex));
        }
        bookingsTable.setItems(currentViewData);
        updateCurrentPageLabel(filteredBookingData.size());
    }

    private void updateCurrentPageLabel(int totalItems) {
        int totalPages = calculateTotalPages(rowsPerPage, totalItems);
        if (totalPages == 0) { currentPage = 1; }
        else if (currentPage > totalPages) { currentPage = totalPages; }
        else if (currentPage < 1) { currentPage = 1; }

        int startItem = totalItems == 0 ? 0 : (currentPage - 1) * rowsPerPage + 1;
        int endItem = Math.min(currentPage * rowsPerPage, totalItems);
        currentPageLabel.setText(totalItems == 0 ? "0-0 of 0" : startItem + "-" + endItem + " of " + totalItems);
    }

    private int calculateTotalPages(int rowsPerPage, int totalItems) {
        if (rowsPerPage <= 0 || totalItems <= 0) { return 0; }
        return (int) Math.ceil((double) totalItems / rowsPerPage);
    }

    // --- Event Handlers for UI Controls ---
    @FXML void handleShowAllBookings(ActionEvent event) { resetFiltersInternal(); applyFiltersAndPagination(); }

    @FXML
    void handleShowThisMonthBookings(ActionEvent event) {
        resetFiltersInternal();
        LocalDateTime now = LocalDateTime.now();
        Month currentMonthEnum = now.getMonth();
        String currentMonthName = currentMonthEnum.getDisplayName(TextStyle.FULL, Locale.ENGLISH); // Dùng English để khớp ComboBox
        monthComboBox.setValue(currentMonthName);
        applyFiltersAndPagination();
    }

    @FXML void handleSearchByMonth(ActionEvent event) { applyFiltersAndPagination(); }
    @FXML void handleFilterByYear(ActionEvent event) { applyFiltersAndPagination(); }
    @FXML void handleLiveSearchFacility(KeyEvent event) { applyFiltersAndPagination(); }
    @FXML void handleLiveSearchByUser(KeyEvent event) { applyFiltersAndPagination(); }
    @FXML void handleResetFilters(ActionEvent event) {
        resetFiltersInternal();
        rowsPerPage = 6;
        rowsPerPageComboBox.setValue(String.valueOf(rowsPerPage));
        applyFiltersAndPagination();
    }
    private void resetFiltersInternal() {
        monthComboBox.setValue(null);
        yearTextField.clear();
        facilityTextField.clear();
        employeeIdTextField.clear();
    }
    @FXML void handleRowsPerPageChange(ActionEvent event) {
        try {
            int selectedRows = Integer.parseInt(rowsPerPageComboBox.getValue());
            rowsPerPage = (selectedRows > 0) ? selectedRows : 6;
        } catch (NumberFormatException | NullPointerException e) {
            rowsPerPage = 6;
        }
        rowsPerPageComboBox.setValue(String.valueOf(rowsPerPage));
        currentPage = 1;
        updateTableViewPage();
    }
    @FXML void handlePreviousPage(ActionEvent event) {
        if (currentPage > 1) { currentPage--; updateTableViewPage(); }
    }
    @FXML void handleNextPage(ActionEvent event) {
        int totalPages = calculateTotalPages(rowsPerPage, filteredBookingData.size());
        if (totalPages > 0 && currentPage < totalPages) { currentPage++; updateTableViewPage(); }
    }

    // --- Export Functionality ---
    @FXML
    public void handleExportBookings(ActionEvent actionEvent) {
        System.out.println("Exporting All Bookings...");
        String dest = "all_bookings_export_" + System.currentTimeMillis() + ".pdf";
        try (FileOutputStream fos = new FileOutputStream(dest);
             PdfWriter writer = new PdfWriter(fos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("All Bookings Export").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Generated on: " + (dateTimeFormatter != null ? LocalDateTime.now().format(dateTimeFormatter) : LocalDateTime.now().toString())).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            Table table = new Table(UnitValue.createPercentArray(new float[]{2f, 1.5f, 2f, 2f, 2f, 2f, 1.5f, 1f})).useAllAvailableWidth();
            addPdfHeaderCell(table, "Room Name"); addPdfHeaderCell(table, "User Name"); addPdfHeaderCell(table, "Purpose"); addPdfHeaderCell(table, "Date"); addPdfHeaderCell(table, "Time Slot"); addPdfHeaderCell(table, "Created At"); addPdfHeaderCell(table, "Approved By"); addPdfHeaderCell(table, "Status");

            if (filteredBookingData.isEmpty()) {
                document.add(new Paragraph("No bookings to export.").setTextAlignment(TextAlignment.CENTER));
            } else {
                for (BookingResponse booking : filteredBookingData) {
                    if (booking == null) continue;
                    table.addCell(getStringOrEmpty(booking.getRoomName()));
                    table.addCell(getStringOrEmpty(booking.getUserName()));
                    table.addCell(getStringOrEmpty(booking.getPurpose()));
                    table.addCell(booking.getPlannedStartTime() != null && dateFormatter != null ? dateFormatter.format(booking.getPlannedStartTime()) : (booking.getPlannedStartTime() != null ? booking.getPlannedStartTime().toLocalDate().toString() : ""));

                    String timeSlotStr = "";
                    if (booking.getPlannedStartTime() != null && booking.getPlannedEndTime() != null && timeFormatter != null) {
                        try { timeSlotStr = timeFormatter.format(booking.getPlannedStartTime()) + " - " + timeFormatter.format(booking.getPlannedEndTime()); } catch(Exception e) { timeSlotStr = "Invalid Time"; }
                    }
                    table.addCell(timeSlotStr);

                    table.addCell(booking.getCreatedAt() != null && dateTimeFormatter != null ? dateTimeFormatter.format(booking.getCreatedAt()) : (booking.getCreatedAt() != null ? booking.getCreatedAt().toString() : ""));
                    table.addCell(getStringOrEmpty(booking.getApprovedByUserName()));
                    table.addCell(getStringOrEmpty(booking.getStatus()));
                }
                document.add(table);
            }

            System.out.println("PDF exported successfully to: " + dest);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("All bookings exported successfully to:\n" + dest);
                alert.showAndWait();
            });
        } catch (Exception e) {
            System.err.println("Error during PDF export: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> showErrorAlert("Export Failed", "An error occurred during export:\n" + e.getMessage()));
        }
    }

    /** Helper method to add a styled header cell to the PDF table. */
    private void addPdfHeaderCell(Table table, String text) {
        table.addHeaderCell(new Paragraph(text).setBold().setFontSize(10));
    }

    // --- Utility Methods ---
    /** Returns the input string or an empty string if the input is null. */
    private String getStringOrEmpty(String str) { return str != null ? str : ""; }
    /** Displays an error alert dialog to the user. */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false); textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE); textArea.setMaxHeight(Double.MAX_VALUE);
        alert.getDialogPane().setContent(textArea); alert.setResizable(true);
        alert.showAndWait();
    }
}
