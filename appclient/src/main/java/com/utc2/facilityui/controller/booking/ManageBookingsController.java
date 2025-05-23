package com.utc2.facilityui.controller.booking;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.utc2.facilityui.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import com.itextpdf.layout.property.TextAlignment;



import com.itextpdf.layout.Document;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ManageBookingsController implements Initializable {

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, String> titleFacilityColumn;

    @FXML
    private TableColumn<Booking, String> requestedByColumn;

    @FXML
    private TableColumn<Booking, String> purposeColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Booking, String> timeSlotColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> requestedAtColumn;

    @FXML
    private TableColumn<Booking, String> groupDirectorColumn;

    @FXML
    private TableColumn<Booking, String> facilityManColumn;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private TextField yearTextField;

    @FXML
    private TextField facilityTextField;

    @FXML
    private TextField employeeIdTextField;

    @FXML
    private ComboBox<String> rowsPerPageComboBox;

    @FXML
    private Label currentPageLabel;

    private ObservableList<Booking> allBookingData = FXCollections.observableArrayList();
    private ObservableList<Booking> bookingData = FXCollections.observableArrayList();
    private int currentPage = 1;
    private int rowsPerPage = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize table columns
        titleFacilityColumn.setCellValueFactory(new PropertyValueFactory<>("titleFacility"));
        requestedByColumn.setCellValueFactory(new PropertyValueFactory<>("requestedBy"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        // Format for Date column
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(column -> {
            return new TableCell<Booking, LocalDateTime>() {
                private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"); // Định dạng đầy đủ

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(dateFormatter.format(item)); // Hiển thị theo định dạng đầy đủ
                    }
                }
            };
        });



        timeSlotColumn.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));

        // Format for Requested At column
        requestedAtColumn.setCellValueFactory(new PropertyValueFactory<>("requestedAt"));
        requestedAtColumn.setCellFactory(column -> {
            return new TableCell<Booking, LocalDateTime>() {
                private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a EEE MMM dd yyyy");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(dateTimeFormatter.format(item));
                    }
                }
            };
        });

        groupDirectorColumn.setCellValueFactory(new PropertyValueFactory<>("groupDirector"));
        facilityManColumn.setCellValueFactory(new PropertyValueFactory<>("facilityMan"));

        // Initialize table data
        loadBookingDataForPage(currentPage, rowsPerPage);
        bookingsTable.setItems(bookingData);

        // Initialize combo boxes
        monthComboBox.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        rowsPerPageComboBox.setItems(FXCollections.observableArrayList("6", "10", "20").sorted());
        rowsPerPageComboBox.setValue(String.valueOf(6));
        updateCurrentPageLabel();
    }

    private void loadBookingDataForPage(int pageNumber, int rowsPerPage) {
        bookingData.clear();

        // Nếu dữ liệu gốc chưa có, load 1 lần và giữ lại
        if (allBookingData.isEmpty()) {
            List<Booking> loadedData = List.of(
                    new Booking("Lunch Courtyard", "Sam Bergnaum", "A lunch party",
                            LocalDateTime.of(2025, 4, 22, 12, 30), // April
                            "12:30 PM - 02:00 PM",
                            LocalDateTime.now(),
                            "Not approved",
                            "Not approved"),

                    new Booking("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Booking("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Booking("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Booking("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Booking("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Booking("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco")
            );

            allBookingData.setAll(loadedData);
        }

        int startIndex = (pageNumber - 1) * rowsPerPage;
        int endIndex = Math.min(startIndex + rowsPerPage, allBookingData.size());

        // Chỉ tải dữ liệu nằm trong khoảng startIndex đến endIndex
        for (int i = startIndex; i < endIndex; i++) {
            bookingData.add(allBookingData.get(i));
        }

        // Debug log
        bookingData.forEach(booking -> {
            System.out.println("Loaded booking: " + booking);
        });

        updateCurrentPageLabel(allBookingData.size());
    }


    private int calculateTotalPages(int rowsPerPage) {
        if (rowsPerPage <= 0) return 1;
        return (int) Math.ceil((double) allBookingData.size() / rowsPerPage);
    }

    private void updateCurrentPageLabel() {
        int totalPages = calculateTotalPages(rowsPerPage);
        currentPageLabel.setText("Page " + currentPage + " of " + totalPages);
    }

    @FXML
    void handleShowAllBookings(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        loadBookingDataForPage(currentPage, Integer.MAX_VALUE);
        updateCurrentPageLabel();
    }

    @FXML
    void handleShowThisMonthBookings(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        int currentMonth = LocalDateTime.now().getMonthValue();
        bookingData.clear();
        bookingData.addAll(allBookingData.stream().filter(booking -> booking.getDate().getMonthValue() == currentMonth).collect(Collectors.toList()));
        updateCurrentPageLabel();
    }

    @FXML
    void handleSearchByMonth(ActionEvent event) {
        for (Booking b : allBookingData) {
            if (b.getDate() != null) {
                System.out.println("Booking date: " + b.getDate() +
                        " | Month: " + b.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            }
        }
        String selectedMonth = monthComboBox.getValue();
        bookingData.clear();

        if (selectedMonth != null && !selectedMonth.isEmpty()) {
            // In ra tháng đã chọn để debug
            System.out.println("Selected Month (Search): " + selectedMonth);

            // Lọc dữ liệu theo tên tháng đầy đủ (ví dụ: "April")
            List<Booking> filtered = allBookingData.stream()
                    .filter(booking -> {
                        if (booking.getDate() != null) {
                            String bookingMonth = booking.getDate()
                                    .getMonth()
                                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                            return bookingMonth.equalsIgnoreCase(selectedMonth);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            bookingData.addAll(filtered);
            System.out.println("Filtered Booking Data Size (Selected Month): " + filtered.size());
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage);
        }

        updateCurrentPageLabel(bookingData.size());
    }

    @FXML
    void handleFilterByYear(ActionEvent event) {
        currentPage = 1;
        String yearText = yearTextField.getText().trim(); // Xóa khoảng trắng nếu có

        if (!yearText.isEmpty()) {
            try {
                int yearValue = Integer.parseInt(yearText);
                bookingData.clear();

                List<Booking> filteredByYear = allBookingData.stream()
                        .filter(booking -> booking.getDate() != null && booking.getDate().getYear() == yearValue)
                        .collect(Collectors.toList());

                bookingData.addAll(filteredByYear);
                System.out.println("Filtered Booking Data Size (Selected Year): " + filteredByYear.size());

            } catch (NumberFormatException e) {
                System.err.println("Invalid year format: " + yearText);
                // Optionally show alert to user here
            }
        } else {
            // Nếu để trống thì load lại toàn bộ dữ liệu cho trang đầu
            loadBookingDataForPage(currentPage, rowsPerPage);
        }

        updateCurrentPageLabel(bookingData.size());
    }

    @FXML
    public void handleLiveSearchFacility(KeyEvent event) {
        String input = facilityTextField.getText().trim().toLowerCase();
        bookingData.clear();

        if (!input.isEmpty()) {
            List<Booking> filtered = allBookingData.stream()
                    .filter(b -> b.getTitleFacility().toLowerCase().contains(input))
                    .collect(Collectors.toList());
            bookingData.addAll(filtered);
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage); // hoặc bookingData.addAll(allBookingData);
        }

        updateCurrentPageLabel(bookingData.size());
    }

    @FXML
    void handleLiveSearchByFacilityMan(KeyEvent event) {
        String input = employeeIdTextField.getText().trim().toLowerCase();
        bookingData.clear();

        if (!input.isEmpty()) {
            bookingData.addAll(allBookingData.stream()
                    .filter(booking -> booking.getFacilityMan() != null &&
                            booking.getFacilityMan().toLowerCase().contains(input))
                    .collect(Collectors.toList()));
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage);
        }

        updateCurrentPageLabel(bookingData.size());
    }

    @FXML
    void handleResetFilters(ActionEvent event) {
        // Reset current page and rows per page
        currentPage = 1;
        rowsPerPage = 6;

        // Clear all TextFields
        monthComboBox.setValue(null); // Reset ComboBox (Month)
        yearTextField.clear(); // Clear Year TextField
        employeeIdTextField.clear(); // Clear Employee ID TextField

        // If you have more TextFields to reset, add them here
        facilityTextField.clear(); // Example TextField for Facility Name

        // Reload all data without any filters
        loadBookingDataForPage(currentPage, rowsPerPage);

        // Update the pagination label
        updateCurrentPageLabel();
    }

    private void updateCurrentPageLabel(int totalItems) {
        int totalPages = calculateTotalPages(rowsPerPage, totalItems);

        int startItem = (currentPage - 1) * rowsPerPage + 1;
        int endItem = Math.min(currentPage * rowsPerPage, totalItems);

        if (totalItems == 0) {
            currentPageLabel.setText("0-0 of 0");
        } else {
            currentPageLabel.setText(startItem + "-" + endItem + " of " + totalItems);
        }
    }

    private int calculateTotalPages(int rowsPerPage, int totalItems) {
        if (rowsPerPage <= 0) {
            return 1; // Tránh chia cho 0
        }
        return (int) Math.ceil((double) totalItems / rowsPerPage);
    }

    public void handleExportBookings(ActionEvent actionEvent) {
        System.out.println("Export Bookings clicked");

        String dest = "bookings_export.pdf"; // Output file path

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(dest));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Booking Export")
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2, 2, 2, 2}))
                    .useAllAvailableWidth();

            // Header row
            table.addHeaderCell("Title/Facility");
            table.addHeaderCell("Requested By");
            table.addHeaderCell("Purpose");
            table.addHeaderCell("Date");
            table.addHeaderCell("Time Slot");
            table.addHeaderCell("Requested At");
            table.addHeaderCell("Group Director");
            table.addHeaderCell("Facility Man");

            // Data rows
            for (Booking booking : allBookingData) {
                table.addCell(booking.getTitleFacility());
                table.addCell(booking.getRequestedBy());
                table.addCell(booking.getPurpose());
                table.addCell(booking.getDate().toString());
                table.addCell(booking.getTimeSlot());
                table.addCell(booking.getRequestedAt().toString());
                table.addCell(booking.getGroupDirector());
                table.addCell(booking.getFacilityMan());
            }

            document.add(table);
            document.close();

            System.out.println("PDF exported successfully to: " + dest);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void handleRowsPerPageChange(ActionEvent actionEvent) {
        rowsPerPage = 6;
        currentPage = 1; // Bắt đầu từ trang đầu tiên
        loadBookingDataForPage(currentPage, rowsPerPage);
        updateCurrentPageLabel(allBookingData.size());
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        if (currentPage > 1) {
            currentPage--;
            loadBookingDataForPage(currentPage, rowsPerPage);
            updateCurrentPageLabel(allBookingData.size());
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        int totalPages = calculateTotalPages(rowsPerPage, allBookingData.size());
        if (currentPage < totalPages) {
            currentPage++;
            loadBookingDataForPage(currentPage, rowsPerPage);
            updateCurrentPageLabel(allBookingData.size());
        }
    }
}