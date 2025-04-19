package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private ComboBox<String> facilityComboBox;

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
                private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(dateFormatter.format(item));
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
        ).sorted());
        facilityComboBox.setItems(FXCollections.observableArrayList("Facility A", "Facility B", "Facility C").sorted());
        rowsPerPageComboBox.setItems(FXCollections.observableArrayList("10", "25", "50", "100").sorted());
        rowsPerPageComboBox.setValue(String.valueOf(rowsPerPage));
        updateCurrentPageLabel();
    }

    private void loadBookingDataForPage(int pageNumber, int rowsPerPage) {
        bookingData.clear();
        System.out.println("loadBookingDataForPage() called for page: " + pageNumber + ", rows per page: " + rowsPerPage);

        // Your data loading logic (example hardcoded data):
        bookingData.addAll(
                new Booking("Lunch Courtyard", "Sam Bergnaum", "A lunch party", LocalDateTime.now(), "12:30 PM - 02:00 PM", LocalDateTime.now(), "Not approved", "Not approved"),
                new Booking("Celebration Courtyard", "Sam Bergnaum", "a celebration party", LocalDateTime.now().plusDays(1), "10:00 AM - 12:00 PM", LocalDateTime.now(), "Grace Cummerata", "Grady Turco")
                // Add more test data
        );

        System.out.println("bookingData size after loading: " + bookingData.size());
        for (Booking booking : bookingData) {
            System.out.println("Loaded Booking: " + booking); // Make sure toString() in Booking is helpful
        }
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
    void handleFilterByMonth(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        String selectedMonth = monthComboBox.getValue();
        bookingData.clear();
        if (selectedMonth != null && !selectedMonth.isEmpty()) {
            bookingData.addAll(allBookingData.stream().filter(booking -> {
                boolean dateMatch = booking.getDate() != null && booking.getDate().getMonth().toString().equalsIgnoreCase(selectedMonth.toUpperCase());
                return dateMatch;
            }).collect(Collectors.toList()));
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage);
        }
        updateCurrentPageLabel();
    }

    @FXML
    void handleFilterByYear(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        String yearText = yearTextField.getText();
        if (!yearText.isEmpty()) {
            try {
                int yearValue = Integer.parseInt(yearText);
                bookingData.clear();
                bookingData.addAll(allBookingData.stream().filter(booking -> booking.getDate().getYear() == yearValue).collect(Collectors.toList()));
            } catch (NumberFormatException e) {
                // Handle invalid year format
            }
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage);
        }
        updateCurrentPageLabel();
    }

    @FXML
    void handleFilterByFacility(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        String selectedFacility = facilityComboBox.getValue();
        if (selectedFacility != null) {
            bookingData.clear();
            bookingData.addAll(allBookingData.stream().filter(booking -> booking.getTitleFacility().contains(selectedFacility)).collect(Collectors.toList()));
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage);
        }
        updateCurrentPageLabel();
    }

    @FXML
    void handleFilterByEmployeeId(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        String employeeIdText = employeeIdTextField.getText();
        if (!employeeIdText.isEmpty()) {
            bookingData.clear();
            bookingData.addAll(allBookingData.stream()
                    .filter(booking -> booking.getRequestedBy().toLowerCase().contains(employeeIdText.toLowerCase()))
                    .collect(Collectors.toList()));
        } else {
            loadBookingDataForPage(currentPage, rowsPerPage);
        }
        updateCurrentPageLabel();
    }

    @FXML
    void handleResetFilters(ActionEvent event) {
        // Implement logic
        currentPage = 1;
        rowsPerPage = 10;
        loadBookingDataForPage(currentPage, rowsPerPage);
        updateCurrentPageLabel();
        monthComboBox.setValue(null);
        yearTextField.clear();
        facilityComboBox.setValue(null);
        employeeIdTextField.clear();
    }

    @FXML
    void handleApplyFilters(ActionEvent event) {
        // Implement logic
        System.out.println("Apply Filters clicked");
        currentPage = 1;
        bookingData.clear();

        // Apply filters
        String selectedMonth = monthComboBox.getValue();
        String yearText = yearTextField.getText();
        String selectedFacility = facilityComboBox.getValue();
        String employeeIdText = employeeIdTextField.getText();

        ObservableList<Booking> filteredList = allBookingData;

        if (selectedMonth != null) {
            int monthValue = LocalDateTime.parse("2023-" + selectedMonth + "-01T00:00:00").getMonthValue();
            filteredList = filteredList.stream()
                    .filter(booking -> booking.getDate().getMonthValue() == monthValue)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        if (!yearText.isEmpty()) {
            try {
                int yearValue = Integer.parseInt(yearText);
                filteredList = filteredList.stream()
                        .filter(booking -> booking.getDate().getYear() == yearValue)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
            } catch (NumberFormatException e) {
                // Handle invalid year format (optional: show error to user)
                System.err.println("Invalid year format: " + yearText);
            }
        }

        if (selectedFacility != null) {
            filteredList = filteredList.stream()
                    .filter(booking -> booking.getTitleFacility().toLowerCase().contains(selectedFacility.toLowerCase()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        if (!employeeIdText.isEmpty()) {
            filteredList = filteredList.stream()
                    .filter(booking -> booking.getRequestedBy().toLowerCase().contains(employeeIdText.toLowerCase()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        // Load the filtered data for the current page
        int startIndex = (currentPage - 1) * rowsPerPage;
        int endIndex = Math.min(startIndex + rowsPerPage, filteredList.size());
        bookingData.addAll(filteredList.subList(startIndex, endIndex));

        updateCurrentPageLabel(filteredList.size());
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

        // Save to a file (you'll need to handle file selection, error handling, etc.)
        // For a simple example, print to console:
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