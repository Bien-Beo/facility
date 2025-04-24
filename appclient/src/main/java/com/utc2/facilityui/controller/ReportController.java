package com.utc2.facilityui.controller;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.utc2.facilityui.model.Report;
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

public class ReportController implements Initializable {

    @FXML
    private TableView<Report> reportTable;

    @FXML
    private TableColumn<Report, String> titleFacilityColumn;

    @FXML
    private TableColumn<Report, String> requestedByColumn;

    @FXML
    private TableColumn<Report, String> purposeColumn;

    @FXML
    private TableColumn<Report, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Report, String> timeSlotColumn;

    @FXML
    private TableColumn<Report, LocalDateTime> requestedAtColumn;

    @FXML
    private TableColumn<Report, String> groupDirectorColumn;

    @FXML
    private TableColumn<Report, String> facilityManColumn;

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

    private ObservableList<Report> allReportData = FXCollections.observableArrayList();
    private ObservableList<Report> reportData = FXCollections.observableArrayList();
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
            return new TableCell<Report, LocalDateTime>() {
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
            return new TableCell<Report, LocalDateTime>() {
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
        reportTable.setItems(reportData);

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
        reportData.clear();

        // Nếu dữ liệu gốc chưa có, load 1 lần và giữ lại
        if (allReportData.isEmpty()) {
            List<Report> loadedData = List.of(
                    new Report("Lunch Courtyard", "Sam Bergnaum", "A lunch party",
                            LocalDateTime.of(2025, 4, 22, 12, 30), // April
                            "12:30 PM - 02:00 PM",
                            LocalDateTime.now(),
                            "Not approved",
                            "Not approved"),

                    new Report("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Report("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Report("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Report("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Report("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco"),

                    new Report("Celebration Courtyard", "Sam Bergnaum", "a celebration party",
                            LocalDateTime.of(2025, 5, 15, 10, 0), // May
                            "10:00 AM - 12:00 PM",
                            LocalDateTime.now(),
                            "Grace Cummerata",
                            "Grady Turco")
            );

            allReportData.setAll(loadedData);
        }

        int startIndex = (pageNumber - 1) * rowsPerPage;
        int endIndex = Math.min(startIndex + rowsPerPage, allReportData.size());

        // Chỉ tải dữ liệu nằm trong khoảng startIndex đến endIndex
        for (int i = startIndex; i < endIndex; i++) {
            reportData.add(allReportData.get(i));
        }

        // Debug log
        reportData.forEach(booking -> {
            System.out.println("Loaded booking: " + booking);
        });

        updateCurrentPageLabel(allReportData.size());
    }


    private int calculateTotalPages(int rowsPerPage) {
        if (rowsPerPage <= 0) return 1;
        return (int) Math.ceil((double) allReportData.size() / rowsPerPage);
    }

    private void updateCurrentPageLabel() {
        int totalPages = calculateTotalPages(rowsPerPage);
        currentPageLabel.setText("Page " + currentPage + " of " + totalPages);
    }

    @FXML
    void handleShowAllReports(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        loadBookingDataForPage(currentPage, Integer.MAX_VALUE);
        updateCurrentPageLabel();
    }

    @FXML
    void handleShowThisMonthReports(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        int currentMonth = LocalDateTime.now().getMonthValue();
        reportData.clear();
        reportData.addAll(allReportData.stream().filter(report -> report.getDate().getMonthValue() == currentMonth).collect(Collectors.toList()));
        updateCurrentPageLabel();
    }

    @FXML
    void handleSearchByMonth(ActionEvent event) {
        for (Report b : allReportData) {
            if (b.getDate() != null) {
                System.out.println("Booking date: " + b.getDate() +
                        " | Month: " + b.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            }
        }
        String selectedMonth = monthComboBox.getValue();
        reportData.clear();

        if (selectedMonth != null && !selectedMonth.isEmpty()) {
            // In ra tháng đã chọn để debug
            System.out.println("Selected Month (Search): " + selectedMonth);

            // Lọc dữ liệu theo tên tháng đầy đủ (ví dụ: "April")
            List<Report> filtered = allReportData.stream()
                    .filter(report -> {
                        if (report.getDate() != null) {
                            String bookingMonth = report.getDate()
                                    .getMonth()
                                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                            return bookingMonth.equalsIgnoreCase(selectedMonth);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            reportData.addAll(filtered);
            System.out.println("Filtered Booking Data Size (Selected Month): " + filtered.size());
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage);
        }

        updateCurrentPageLabel(reportData.size());
    }

    @FXML
    void handleFilterByYear(ActionEvent event) {
        currentPage = 1;
        String yearText = yearTextField.getText().trim(); // Xóa khoảng trắng nếu có

        if (!yearText.isEmpty()) {
            try {
                int yearValue = Integer.parseInt(yearText);
                reportData.clear();

                List<Report> filteredByYear = allReportData.stream()
                        .filter(booking -> booking.getDate() != null && booking.getDate().getYear() == yearValue)
                        .collect(Collectors.toList());

                reportData.addAll(filteredByYear);
                System.out.println("Filtered Booking Data Size (Selected Year): " + filteredByYear.size());

            } catch (NumberFormatException e) {
                System.err.println("Invalid year format: " + yearText);
                // Optionally show alert to user here
            }
        } else {
            // Nếu để trống thì load lại toàn bộ dữ liệu cho trang đầu
            loadBookingDataForPage(currentPage, rowsPerPage);
        }

        updateCurrentPageLabel(reportData.size());
    }

    @FXML
    public void handleLiveSearchFacility(KeyEvent event) {
        String input = facilityTextField.getText().trim().toLowerCase();
        reportData.clear();

        if (!input.isEmpty()) {
            List<Report> filtered = allReportData.stream()
                    .filter(b -> b.getTitleFacility().toLowerCase().contains(input))
                    .collect(Collectors.toList());
            reportData.addAll(filtered);
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage); // hoặc bookingData.addAll(allBookingData);
        }

        updateCurrentPageLabel(reportData.size());
    }

    @FXML
    void handleLiveSearchByFacilityMan(KeyEvent event) {
        String input = employeeIdTextField.getText().trim().toLowerCase();
        reportData.clear();

        if (!input.isEmpty()) {
            reportData.addAll(allReportData.stream()
                    .filter(booking -> booking.getFacilityMan() != null &&
                            booking.getFacilityMan().toLowerCase().contains(input))
                    .collect(Collectors.toList()));
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage);
        }

        updateCurrentPageLabel(reportData.size());
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

    public void handleExportReport(ActionEvent actionEvent) {
        System.out.println("Export Report clicked");

        String dest = "report_export.pdf"; // Output file path

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(dest));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Report Export")
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
            for (Report booking : allReportData) {
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
        updateCurrentPageLabel(allReportData.size());
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        if (currentPage > 1) {
            currentPage--;
            loadBookingDataForPage(currentPage, rowsPerPage);
            updateCurrentPageLabel(allReportData.size());
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        int totalPages = calculateTotalPages(rowsPerPage, allReportData.size());
        if (currentPage < totalPages) {
            currentPage++;
            loadBookingDataForPage(currentPage, rowsPerPage);
            updateCurrentPageLabel(allReportData.size());
        }
    }
}