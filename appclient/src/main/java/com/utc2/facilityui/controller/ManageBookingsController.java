package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;


import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
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
        rowsPerPageComboBox.setItems(FXCollections.observableArrayList("10", "25", "50", "100").sorted());
        rowsPerPageComboBox.setValue(String.valueOf(rowsPerPage));
        updateCurrentPageLabel();
    }

    private void loadBookingDataForPage(int pageNumber, int rowsPerPage) {
        bookingData.clear();
        allBookingData.clear();

        System.out.println("loadBookingDataForPage() called for page: " + pageNumber + ", rows per page: " + rowsPerPage);

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
                        "Grady Turco")
        );

        bookingData.addAll(loadedData);
        allBookingData.addAll(loadedData);

        // Debug log
        loadedData.forEach(booking -> {
            System.out.println("Loaded booking: " + booking);
        });

        updateCurrentPageLabel(bookingData.size());
    }


    private int calculateTotalPages(int rowsPerPage) {
        // Implement logic to calculate total pages
        return (int) Math.ceil((double) bookingData.size() / rowsPerPage);
    }

    private void updateCurrentPageLabel() {
        int totalPages = calculateTotalPages(rowsPerPage);
        currentPageLabel.setText(currentPage + "-" + totalPages + " of " + totalPages);
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
        rowsPerPage = 10;

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
        currentPageLabel.setText(currentPage + "-" + Math.min(currentPage * rowsPerPage, totalItems) + " of " + totalItems);
    }

    private int calculateTotalPages(int rowsPerPage, int totalItems) {
        if (rowsPerPage <= 0) {
            return 1; // Avoid division by zero
        }
        return (int) Math.ceil((double) totalItems / rowsPerPage);
    }

    public void handleExportBookings(ActionEvent actionEvent) {
        // Implement logic
        System.out.println("Export Bookings clicked");
        // Implement logic to export booking data (e.g., to CSV, Excel)

        StringBuilder sb = new StringBuilder();
        // Add headers
        sb.append("Title/Facility,Requested By,Purpose,Date,Time Slot,Requested At,Group Director,Facility Man\n");
        // Add data rows
        for (Booking booking : allBookingData) { // Export all data, not just the current page
            sb.append(booking.getTitleFacility()).append(",");
            sb.append(booking.getRequestedBy()).append(",");
            sb.append(booking.getPurpose()).append(",");
            sb.append(booking.getDate()).append(",");
            sb.append(booking.getTimeSlot()).append(",");
            sb.append(booking.getRequestedAt()).append(",");
            sb.append(booking.getGroupDirector()).append(",");
            sb.append(booking.getFacilityMan()).append("\n");
        }
        System.out.println("Exported Data:\n" + sb.toString());
    }

    public void handleRowsPerPageChange(ActionEvent actionEvent) {
        // Implement logic
        String selectedRowsPerPage = rowsPerPageComboBox.getValue();
        rowsPerPage = Integer.parseInt(selectedRowsPerPage);
        currentPage = 1;
        loadBookingDataForPage(currentPage, rowsPerPage);
        updateCurrentPageLabel();
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        // Implement logic
        if (currentPage > 1) {
            currentPage--;
            loadBookingDataForPage(currentPage, rowsPerPage);
            updateCurrentPageLabel();
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        int totalPages = calculateTotalPages(rowsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            loadBookingDataForPage(currentPage, rowsPerPage);
            updateCurrentPageLabel();
        }
    }
}