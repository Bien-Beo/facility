package com.utc2.facilityui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import com.utc2.facilityui.model.Booking;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.TreeSet;

public class ManageBookingsController {

    // Các ComboBox và các thành phần giao diện
    @FXML
    private ComboBox<String> filterByRoomComboBox;

    @FXML
    private ComboBox<String> filterByMonthComboBox;

    @FXML
    private TextField yearTextField;

    @FXML
    private ComboBox<String> filterByUserComboBox;

    @FXML
    private ComboBox<String> rowsPerPageComboBox;

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, String> titleFacilityColumn;
    @FXML
    private TableColumn<Booking, String> purposeColumn;
    @FXML
    private TableColumn<Booking, String> timeSlotColumn;
    @FXML
    private TableColumn<Booking, String> equipmentColumn;
    @FXML
    private TableColumn<Booking, String> requestedAtColumn;
    @FXML
    private TableColumn<Booking, String> statusColumn;
    @FXML
    private TableColumn<Booking, String> handledByColumn;
    @FXML
    private TableColumn<Booking, String> reasonNoteColumn;
    @FXML
    private TableColumn<Booking, String> actionColumn;

    @FXML
    private Label currentPageLabel;

    // Khởi tạo các thành phần trong TableView và ComboBox
    @FXML
    private void initialize() {
        setupTableColumns();
        setupFilters();
        addSampleBookings();
    }

    // Thiết lập các cột trong TableView
    private void setupTableColumns() {
        titleFacilityColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Booking booking = (Booking) getTableRow().getItem();

                    Label requestedByLabel = new Label(booking.getRequestedBy());
                    requestedByLabel.setStyle("-fx-font-weight: bold;");

                    Label facilityLabel = new Label(booking.getTitleFacilityOriginal());
                    VBox vbox = new VBox(requestedByLabel, facilityLabel);
                    vbox.setSpacing(2);
                    setGraphic(vbox);
                }
            }
        });
        purposeColumn.setCellValueFactory(cellData -> cellData.getValue().purposeProperty());
        timeSlotColumn.setCellValueFactory(cellData -> cellData.getValue().timeSlotProperty());
        requestedAtColumn.setCellValueFactory(cellData -> cellData.getValue().requestedAtProperty());
        equipmentColumn.setCellValueFactory(cellData -> cellData.getValue().equipmentProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        handledByColumn.setCellValueFactory(cellData -> cellData.getValue().handledByProperty());
        reasonNoteColumn.setCellValueFactory(cellData -> cellData.getValue().reasonNoteProperty());
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Action"));

        enableTextWrapping(purposeColumn);
        enableTextWrapping(timeSlotColumn);
        enableTextWrapping(requestedAtColumn);
        enableTextWrapping(equipmentColumn);
        enableTextWrapping(statusColumn);
        enableTextWrapping(handledByColumn);
        enableTextWrapping(reasonNoteColumn);
    }

    private void addSampleBookings() {
        ObservableList<Booking> samples = FXCollections.observableArrayList(
                new Booking("Phòng họp A1", "Nguyễn Văn A", "Họp nhóm",
                        LocalDateTime.of(2025, 5, 10, 0, 0),
                        "08:00 - 10:00",
                        LocalDateTime.of(2025, 5, 8, 14, 30),
                        "Trưởng phòng Kỹ thuật", "Quản lý CSVC", "Máy chiếu", "false"),

                new Booking("Phòng máy B2", "Trần Thị B", "Thuyết trình đề tài",
                        LocalDateTime.of(2025, 5, 11, 0, 0),
                        "13:00 - 15:00",
                        LocalDateTime.of(2025, 5, 9, 9, 45),
                        null, null, "Máy chiếu, bảng trắng", "false"),

                new Booking("Phòng hội trường", "Lê Văn C", "Hội thảo chuyên đề",
                        LocalDateTime.of(2025, 5, 12, 0, 0),
                        "08:00 - 11:30",
                        LocalDateTime.of(2025, 5, 10, 10, 0),
                        "Phó giám đốc đào tạo", null, "Âm thanh, micro không dây", "true") // Đã hủy
        );

        bookingsTable.setItems(samples);
    }


    private void enableTextWrapping(TableColumn<Booking, String> column) {
        column.setCellFactory(col -> {
            TableCell<Booking, String> cell = new TableCell<>() {
                private final Text text = new Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(col.widthProperty().subtract(10)); // trừ padding
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });
    }

    // Thiết lập dữ liệu cho các ComboBox
    private void setupFilters() {
        ObservableList<String> roomOptions = FXCollections.observableArrayList("Room 1", "Room 2", "Room 3");
        filterByRoomComboBox.setItems(roomOptions);

        ObservableList<String> monthOptions = FXCollections.observableArrayList("January", "February", "March");
        filterByMonthComboBox.setItems(monthOptions);

        ObservableList<String> userOptions = FXCollections.observableArrayList("User 1", "User 2", "User 3");
        filterByUserComboBox.setItems(userOptions);

        ObservableList<String> rowsOptions = FXCollections.observableArrayList("5", "10", "15", "20");
        rowsPerPageComboBox.setItems(rowsOptions);
    }

    private void populateRoomFilterFromBookings() {
        ObservableList<Booking> bookings = bookingsTable.getItems();

        Set<String> roomSet = bookings.stream()
                .map(booking -> {
                    String full = booking.getTitleFacility(); // VD: "Nguyễn Văn A / Phòng họp A1"
                    String[] parts = full.split(" / ");
                    return parts.length == 2 ? parts[1] : full; // Lấy "Phòng họp A1"
                })
                .collect(Collectors.toCollection(TreeSet::new)); // Tự động loại trùng và sắp xếp

        filterByRoomComboBox.setItems(FXCollections.observableArrayList(roomSet));
    }

    // Hàm xử lý xuất dữ liệu (PDF/Excel)
    @FXML
    private void handleExportBookings(ActionEvent event) {
        // Logic xuất dữ liệu (có thể là PDF hoặc Excel)
        System.out.println("Export bookings...");
    }

    // Hàm reset bộ lọc
    @FXML
    private void handleResetFilters(ActionEvent event) {
        filterByRoomComboBox.getSelectionModel().clearSelection();
        filterByMonthComboBox.getSelectionModel().clearSelection();
        yearTextField.clear();
        filterByUserComboBox.getSelectionModel().clearSelection();
    }

    // Hàm xử lý thay đổi số dòng mỗi trang trong TableView
    @FXML
    private void handleRowsPerPageChange(ActionEvent event) {
        String selectedRows = rowsPerPageComboBox.getSelectionModel().getSelectedItem();
        System.out.println("Rows per page: " + selectedRows);
        // Logic phân trang
    }

    // Hàm xử lý quay lại trang trước
    @FXML
    private void handlePreviousPage(ActionEvent event) {
        // Logic quay lại trang trước
        System.out.println("Previous page");
    }

    // Hàm xử lý chuyển sang trang kế tiếp
    @FXML
    private void handleNextPage(ActionEvent event) {
        // Logic chuyển sang trang tiếp theo
        System.out.println("Next page");
    }

    // Các sự kiện khác có thể cần thiết, ví dụ, cho hành động trong các cột TableView
    @FXML
    private void handleTableRowClick(MouseEvent event) {
        // Logic xử lý khi người dùng nhấp vào một dòng trong TableView
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            System.out.println("Selected booking: " + selectedBooking.getTitleFacility());
        }
    }
}
