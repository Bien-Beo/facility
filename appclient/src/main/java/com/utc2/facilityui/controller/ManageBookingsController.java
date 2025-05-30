package com.utc2.facilityui.controller;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.utc2.facilityui.model.User;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.response.EquipmentResponse;
import com.utc2.facilityui.service.BookingService;
import com.utc2.facilityui.service.UserServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class ManageBookingsController implements Initializable {

    @FXML private TableView<BookingResponse> bookingsTable;
    private final Map<String, String> userIdToUsername = new HashMap<>();

    @FXML private TableColumn<BookingResponse, String> userIdColumn;
    @FXML private TableColumn<BookingResponse, String> roomIdColumn;
    @FXML private TableColumn<BookingResponse, String> purposeColumn;
    @FXML private TableColumn<BookingResponse, String> plannedTimeColumn;
    @FXML private TableColumn<BookingResponse, String> equipmentColumn;
    @FXML private TableColumn<BookingResponse, String> actualTimeColumn;
    @FXML private TableColumn<BookingResponse, String> statusColumn;
    @FXML private TableColumn<BookingResponse, String> approvedByColumn;
    @FXML private TableColumn<BookingResponse, String> cancellationReasonColumn;
    @FXML private TableColumn<BookingResponse, String> noteColumn;
    @FXML private TableColumn<BookingResponse, String> actionColumn;

    @FXML private ComboBox<String> filterByRoomComboBox;
    @FXML private ComboBox<String> filterByMonthComboBox;
    @FXML private ComboBox<String> filterByUserComboBox;
    @FXML
    private TextField filterByUserTextField;
    @FXML private ComboBox<Integer> rowsPerPageComboBox;
    @FXML private Label pageInfoLabel;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;

    private final BookingService bookingService = new BookingService();
    private ObservableList<BookingResponse> masterData = FXCollections.observableArrayList();
    private FilteredList<BookingResponse> filteredData;
    private int currentPage = 1;
    private int rowsPerPage = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserMap();
        initTableColumns();
        // Gọi API lấy danh sách bookings
        List<BookingResponse> listFromApi = null;
        try {
            listFromApi = bookingService.getAllBookings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        masterData.setAll(listFromApi);

        filteredData = new FilteredList<>(masterData, b -> true);
        SortedList<BookingResponse> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(bookingsTable.comparatorProperty());
        bookingsTable.setItems(sortedData);
        // --- ComboBox lọc phòng ---
        Set<String> uniqueRooms = masterData.stream()
                .map(BookingResponse::getRoomName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
        filterByRoomComboBox.getItems().add("Tất cả phòng");
        filterByRoomComboBox.getItems().addAll(uniqueRooms);
        filterByRoomComboBox.getSelectionModel().selectFirst();

        filterByRoomComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilters());

        // --- ComboBox lọc tháng ---
        List<String> monthNames = Arrays.stream(Month.values())
                .map(m -> m.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .collect(Collectors.toList());
        filterByMonthComboBox.getItems().add("Tất cả tháng");
        filterByMonthComboBox.getItems().addAll(monthNames);
        filterByMonthComboBox.getSelectionModel().selectFirst();
        filterByMonthComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilters());

        // --- ComboBox lọc người đặt ---
        Set<String> uniqueUsers = masterData.stream()
                .map(BookingResponse::getUserName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
        filterByUserComboBox.getItems().add("Tất cả người đặt");
        filterByUserComboBox.getItems().addAll(uniqueUsers);
        filterByUserComboBox.getSelectionModel().selectFirst();
        filterByUserComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilters());
        // Khởi tạo combo box rows per page
        rowsPerPageComboBox.getItems().addAll(10, 20, 50, 100);
        rowsPerPageComboBox.setValue(rowsPerPage);
        rowsPerPageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            rowsPerPage = newVal;
            currentPage = 0;
            updatePagination();
        });

        prevPageButton.setOnAction(e -> handlePreviousPage());
        nextPageButton.setOnAction(e -> handleNextPage());
    }

    private void updatePagination() {
        int totalItems = filteredData.size();
        int totalPages = (int) Math.ceil((double) totalItems / rowsPerPage);

        if (currentPage < 0) currentPage = 0;
        if (currentPage >= totalPages) currentPage = totalPages - 1;

        int fromIndex = currentPage * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, totalItems);

        List<BookingResponse> pageItems = filteredData.subList(fromIndex, toIndex);
        bookingsTable.setItems(FXCollections.observableArrayList(pageItems));

        pageInfoLabel.setText("Page " + (currentPage + 1) + " of " + (totalPages == 0 ? 1 : totalPages));

        prevPageButton.setDisable(currentPage <= 0);
        nextPageButton.setDisable(currentPage >= totalPages - 1);
    }


    // Hàm cập nhật bộ lọc tổng hợp
    private void updateFilters() {
        String selectedRoom = filterByRoomComboBox.getValue();
        String selectedMonth = filterByMonthComboBox.getValue();
        String selectedUser = filterByUserComboBox.getValue();

        filteredData.setPredicate(booking -> {
            // Lọc theo phòng
            if (selectedRoom != null && !selectedRoom.equals("Tất cả phòng") &&
                    (booking.getRoomName() == null || !booking.getRoomName().equals(selectedRoom))) {
                return false;
            }
            // Lọc theo tháng (theo plannedStartTime)
            if (selectedMonth != null && !selectedMonth.equals("Tất cả tháng")) {
                if (booking.getPlannedStartTime() == null) return false;
                Month bookingMonth = booking.getPlannedStartTime().getMonth();
                if (!bookingMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(selectedMonth)) {
                    return false;
                }
            }
            // Lọc theo người đặt
            if (selectedUser != null && !selectedUser.equals("Tất cả người đặt") && !selectedUser.equals(booking.getUserName())) {
                return false;
            }
            return true;
        });
    }

    private Integer getMonthFromBooking(BookingResponse booking) {
        if (booking.getCreatedAt() == null) return null;
        return booking.getCreatedAt().getMonthValue(); // Nếu dùng LocalDate/LocalDateTime
    }

    private void loadUserMap() {
        try {
            List<User> users = UserServices.getAllUsers();
            for (User user : users) {
                userIdToUsername.put(user.getId(), user.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTableColumns() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userName")); // Hiển thị tên người đặt
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        purposeColumn.setCellFactory(column -> new TableCell<BookingResponse, String>() {
            private final Text text = new Text();

            {
                text.setStyle("-fx-font-size: 12px;");
                text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
                setGraphic(text);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                text.setText(empty || item == null ? null : item);
            }
        });

        plannedTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        formatTimeRange(cellData.getValue().getPlannedStartTime(), cellData.getValue().getPlannedEndTime())
                )
        );
        plannedTimeColumn.setCellFactory(column -> new TableCell<BookingResponse, String>() {
            private final Text text = new Text();

            {
                text.setStyle("-fx-font-size: 12px;"); // hoặc bạn chỉnh theo style riêng
                text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
                setGraphic(text);
                setPrefHeight(Control.USE_COMPUTED_SIZE); // Cho phép tự động điều chỉnh chiều cao
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    text.setText(null);
                } else {
                    text.setText(item);
                }
            }
        });

        equipmentColumn.setCellValueFactory(cellData -> {
            List<String> ids = cellData.getValue().getEquipmentItemIds();
            String display = (ids != null && !ids.isEmpty()) ? String.join(", ", ids) : "";
            return new SimpleStringProperty(display);
        });
        actualTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime createdAt = cellData.getValue().getCreatedAt();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
            String formatted = createdAt != null ? formatter.format(createdAt) : "";
            return new SimpleStringProperty(formatted);
        });
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(generateNotification(cellData.getValue())));
        approvedByColumn.setCellValueFactory(new PropertyValueFactory<>("approvedByUserName"));
        cancellationReasonColumn.setCellValueFactory(new PropertyValueFactory<>("cancellationReason"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action")); // Chưa sử dụng
    }

    private String generateNotification(BookingResponse booking) {
        if ("REJECTED".equals(booking.getStatus())) return "Đã bị từ chối";
        if ("PENDING_APPROVAL".equals(booking.getStatus())) return "Chờ duyệt";
        if ("COMPLETED".equals(booking.getStatus())) return "Đã hoàn thành";
        if ("CONFIRMED".equals(booking.getStatus())) return "Đang sử dụng";
        if ("CANCELLED".equals(booking.getStatus())) return "Đã hủy";
        if ("OVERDUE".equals(booking.getStatus())) return "Quá hạn";
        return "";
    }

    private void loadBookings() {
        try {
            List<BookingResponse> list = bookingService.getAllBookings();
            enrichWithEquipments(list);
            bookingsTable.setItems(FXCollections.observableArrayList(list));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể tải danh sách đặt phòng: " + e.getMessage()).showAndWait();
        }
    }

    private String formatTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return "";
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy", Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        String datePart = dateFormatter.format(start);
        String startTime = timeFormatter.format(start);
        String endTime = timeFormatter.format(end);

        return datePart + " " + startTime + " - " + endTime;
    }

    private String formatTimeRange(LocalDateTime createdAt) {
        if (createdAt == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        return formatter.format(createdAt);
    }

    private void enrichWithEquipments(List<BookingResponse> bookings) {
        for (BookingResponse booking : bookings) {
            try {
                List<EquipmentResponse> equipmentList = bookingService.getEquipmentsByBookingId(booking.getId());

                List<String> itemIds = equipmentList.stream()
                        .map(EquipmentResponse::getItemId)
                        .collect(Collectors.toList());

                booking.setEquipmentItemIds(itemIds);
            } catch (IOException e) {
                System.err.println("Không thể tải thiết bị cho booking ID: " + booking.getId());
                e.printStackTrace();
                booking.setEquipmentItemIds(Collections.singletonList("Lỗi tải thiết bị"));
            }
        }
    }

    @FXML
    private void handleResetFilters() {
        filterByRoomComboBox.getSelectionModel().select("Tất cả phòng");
        filterByMonthComboBox.getSelectionModel().select("Tất cả tháng");
        filterByUserComboBox.getSelectionModel().select("Tất cả người đặt");
        updateFilters();
    }

    @FXML
    private void handleExportBookingsPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(bookingsTable.getScene().getWindow());

        if (file != null) {
            try {
                // 1. Tạo writer và document
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // 2. Load font Unicode từ resources
                InputStream fontStream = getClass().getResourceAsStream("/com/utc2/facilityui/fonts/Roboto-Regular.ttf");
                byte[] fontBytes = fontStream.readAllBytes();
                PdfFont unicodeFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, true);
                document.setFont(unicodeFont);

                // 3. Thêm tiêu đề
                Paragraph title = new Paragraph("DANH SÁCH ĐẶT PHÒNG")
                        .setFont(unicodeFont)
                        .setBold()
                        .setFontSize(16)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(15);
                document.add(title);

                // 4. Tạo bảng
                float[] columnWidths = {3, 2, 3, 3, 3, 3, 2, 2, 3, 3};
                Table table = new Table(columnWidths);
                table.setWidth(UnitValue.createPercentValue(100));

                // 5. Header
                String[] headers = {
                        "Người đặt", "Phòng", "Mục đích", "Thời gian dự kiến", "Thiết bị",
                        "Yêu cầu lúc", "Trạng thái", "Người xử lý", "Lý do hủy", "Ghi chú"
                };
                for (String header : headers) {
                    table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(unicodeFont).setBold()));
                }

                // 6. Dữ liệu từ TableView
                for (BookingResponse booking : bookingsTable.getItems()) {
                    table.addCell(new Cell().add(new Paragraph(safeText(booking.getUserName())).setFont(unicodeFont)));
                    table.addCell(new Cell().add(new Paragraph(safeText(booking.getRoomName())).setFont(unicodeFont)));
                    table.addCell(new Cell().add(new Paragraph(safeText(booking.getPurpose())).setFont(unicodeFont)));
                    table.addCell(new Cell().add(new Paragraph(safeText(formatTimeRange(booking.getPlannedStartTime(), booking.getPlannedEndTime()))).setFont(unicodeFont)));
                    table.addCell(new Cell().add(new Paragraph(safeText(String.join(", ", booking.getEquipmentItemIds()))).setFont(unicodeFont)));
                    table.addCell(new Cell().add(new Paragraph(safeText(formatTimeRange(booking.getCreatedAt()))).setFont(unicodeFont)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(safeText(translateStatus(booking.getStatus()))).setFont(unicodeFont)));
                    table.addCell(new Cell().add(new Paragraph(safeText(booking.getApprovedByUserName())).setFont(unicodeFont)));
                    table.addCell(new Cell().add(new Paragraph(safeText(booking.getCancellationReason())).setFont(unicodeFont)));
                    table.addCell(new Cell().add(new Paragraph(safeText(booking.getNote())).setFont(unicodeFont)));
                }

                // 7. Thêm bảng và đóng tài liệu
                document.add(table);
                document.close();

                new Alert(Alert.AlertType.INFORMATION, "Xuất PDF thành công!").showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Lỗi khi xuất PDF: " + e.getMessage()).showAndWait();
            }
        }
    }

    private String safeText(String value) {
        return value != null ? value : "";
    }

    private String translateStatus(String status) {
        if (status == null) return "";
        return switch (status.toUpperCase()) {
            case "COMPLETED" -> "Đã hoàn thành";
            case "PENDING_APPROVAL" -> "Chờ duyệt";
            case "CANCELLED" -> "Đã hủy";
            case "REJECTED" -> "Đã từ chối";
            case "CONFIRMED" -> "Đang sử dụng";
            case "OVERDUE" -> "Quá hạn";
            default -> status; // giữ nguyên nếu không có trong danh sách
        };
    }

    @FXML
    private void handleExportBookingsExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
        File file = fileChooser.showSaveDialog(bookingsTable.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Bookings");
                String[] headers = {
                        "Người đặt", "Phòng", "Mục đích", "Thời gian dự kiến", "Thiết bị",
                        "Yêu cầu lúc", "Trạng thái", "Người xử lý", "Lý do hủy", "Ghi chú"
                };

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }

                int rowIndex = 1;
                for (BookingResponse booking : bookingsTable.getItems()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(safeText(booking.getUserName()));
                    row.createCell(1).setCellValue(safeText(booking.getRoomName()));
                    row.createCell(2).setCellValue(safeText(booking.getPurpose()));
                    row.createCell(3).setCellValue(safeText(formatTimeRange(booking.getPlannedStartTime(), booking.getPlannedEndTime())));
                    row.createCell(4).setCellValue(safeText(String.join(", ", booking.getEquipmentItemIds())));
                    row.createCell(5).setCellValue(safeText(formatTimeRange(booking.getCreatedAt())));
                    row.createCell(6).setCellValue(safeText(translateStatus(booking.getStatus())));
                    row.createCell(7).setCellValue(safeText(booking.getCancelledByUserName()));
                    row.createCell(8).setCellValue(safeText(booking.getCancellationReason()));
                    row.createCell(9).setCellValue(safeText(booking.getNote()));
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }

                new Alert(Alert.AlertType.INFORMATION, "Xuất Excel thành công!").showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Lỗi khi xuất Excel: " + e.getMessage()).showAndWait();
            }
        }
    }


    @FXML
    private void handleRowsPerPageChange() {
        Integer selected = rowsPerPageComboBox.getValue();
        if (selected != null) {
            rowsPerPage = selected;
            currentPage = 0;
            updatePagination();
        }
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePagination();
        }    }

    @FXML
    private void handleNextPage() {
        int totalPages = (int) Math.ceil((double) filteredData.size() / rowsPerPage);
        if (currentPage < totalPages - 1) {
            currentPage++;
            updatePagination();
        }    }
}
