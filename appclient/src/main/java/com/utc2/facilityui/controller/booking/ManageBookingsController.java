package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.BookedEquipmentItem;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.response.Page;
import com.utc2.facilityui.service.BookingService;
// Ví dụ: import com.utc2.facilityui.service.RoomService;
// import com.utc2.facilityui.service.UserService;
// import com.utc2.facilityui.dto.SimpleIdNamePair; // Một DTO đơn giản để giữ ID và Tên

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

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
import org.apache.poi.ss.usermodel.Row; // Apache POI Row
import org.apache.poi.ss.usermodel.Sheet; // Apache POI Sheet
import org.apache.poi.ss.usermodel.Workbook; // Apache POI Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Apache POI XSSFWorkbook

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class ManageBookingsController implements Initializable {

    @FXML private TableView<BookingResponse> bookingsTable;
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
    @FXML private ComboBox<Integer> filterByYearComboBox;
    @FXML private ComboBox<String> filterByUserComboBox;

    @FXML private Button resetButton;
    // @FXML private Button searchButton;

    @FXML private ComboBox<Integer> rowsPerPageComboBox;
    @FXML private Label pageInfoLabel;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;

    private final BookingService bookingService = new BookingService();
    // private final RoomService roomService = new RoomService();
    // private final UserService userService = new UserService();

    private ObservableList<BookingResponse> currentTableData = FXCollections.observableArrayList();

    private int currentPageNumber = 0;
    private int currentPageSize = 10;
    private int totalPages = 1;
    private long totalElements = 0;

    private Map<String, String> roomDisplayToIdMap = new HashMap<>();
    private Map<String, String> userDisplayToIdMap = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTableColumns();
        initFilterControls();

        if (resetButton != null) {
            resetButton.setOnAction(e -> handleResetFilters());
        }
        loadBookingsFromServer();
    }

    private void initTableColumns() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        purposeColumn.setCellFactory(column -> new TableCell<>() {
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
                        formatDateTimeRange(cellData.getValue().getPlannedStartTime(), cellData.getValue().getPlannedEndTime())
                )
        );
        plannedTimeColumn.setCellFactory(column -> new TableCell<>() {
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

        equipmentColumn.setCellValueFactory(cellData -> {
            List<BookedEquipmentItem> equipments = cellData.getValue().getBookedEquipments();
            String display;
            if (equipments != null && !equipments.isEmpty()) {
                display = equipments.stream()
                        .map(BookedEquipmentItem::getEquipmentModelName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));
                if (display.isEmpty()) display = "Không có";
            } else {
                display = "Không có";
            }
            return new SimpleStringProperty(display);
        });

        actualTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        formatSingleDateTime(cellData.getValue().getCreatedAt())
                )
        );

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(translateBookingStatus(cellData.getValue().getStatus()))
        );
        approvedByColumn.setCellValueFactory(new PropertyValueFactory<>("approvedByUserName"));
        cancellationReasonColumn.setCellValueFactory(new PropertyValueFactory<>("cancellationReason"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        // TODO: actionColumn.setCellValueFactory(...);
    }

    private void initFilterControls() {
        rowsPerPageComboBox.getItems().addAll(5, 10, 20, 50, 100);
        rowsPerPageComboBox.setValue(currentPageSize);
        rowsPerPageComboBox.setOnAction(e -> {
            Integer selectedSize = rowsPerPageComboBox.getValue();
            if (selectedSize != null && selectedSize != currentPageSize) {
                currentPageSize = selectedSize;
                currentPageNumber = 0;
                loadBookingsFromServer();
            }
        });

        filterByMonthComboBox.getItems().add("Tất cả tháng");
        for (int i = 1; i <= 12; i++) {
            filterByMonthComboBox.getItems().add("Tháng " + i);
        }
        filterByMonthComboBox.getSelectionModel().selectFirst();
        filterByMonthComboBox.setOnAction(e -> { currentPageNumber = 0; loadBookingsFromServer(); });

        filterByYearComboBox.getItems().add(null);
        int currentYear = Year.now().getValue();
        for (int i = currentYear + 1; i >= currentYear - 5; i--) {
            filterByYearComboBox.getItems().add(i);
        }
        filterByYearComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Integer year) {
                return year == null ? "Tất cả năm" : year.toString();
            }
            @Override public Integer fromString(String string) {
                return (string == null || string.isEmpty() || string.equals("Tất cả năm")) ? null : Integer.parseInt(string);
            }
        });
        filterByYearComboBox.getSelectionModel().select(null);
        filterByYearComboBox.setOnAction(e -> { currentPageNumber = 0; loadBookingsFromServer(); });

        filterByRoomComboBox.getItems().add("Tất cả phòng");
        // TODO: Load room names and IDs into roomDisplayToIdMap and filterByRoomComboBox.getItems()
        filterByRoomComboBox.getSelectionModel().selectFirst();
        filterByRoomComboBox.setOnAction(e -> { currentPageNumber = 0; loadBookingsFromServer(); });

        filterByUserComboBox.getItems().add("Tất cả người dùng");
        // TODO: Load user full names and UUIDs into userDisplayToIdMap and filterByUserComboBox.getItems()
        filterByUserComboBox.getSelectionModel().selectFirst();
        filterByUserComboBox.setOnAction(e -> { currentPageNumber = 0; loadBookingsFromServer(); });

        prevPageButton.setOnAction(e -> handlePreviousPage());
        nextPageButton.setOnAction(e -> handleNextPage());
    }

    @FXML
    private void handleResetFilters() {
        filterByRoomComboBox.getSelectionModel().selectFirst();
        filterByMonthComboBox.getSelectionModel().selectFirst();
        filterByYearComboBox.getSelectionModel().select(null);
        filterByUserComboBox.getSelectionModel().selectFirst();

        currentPageNumber = 0;
        loadBookingsFromServer();
    }

    private void loadBookingsFromServer() {
        // Lấy giá trị từ các ComboBox lọc (code này đã có)
        String selectedRoomName = filterByRoomComboBox.getValue();
        String currentSearchRoomId = null; // Đổi tên biến để tránh nhầm lẫn nếu có biến instance cùng tên
        if (selectedRoomName != null && !"Tất cả phòng".equals(selectedRoomName)) {
            currentSearchRoomId = roomDisplayToIdMap.get(selectedRoomName);
            if (currentSearchRoomId == null) {
                System.err.println("ManageBookingsController: Không tìm thấy ID cho phòng đã chọn: " + selectedRoomName + ". Sẽ không lọc theo phòng.");
            }
        }

        String selectedMonthStr = filterByMonthComboBox.getValue();
        Integer currentSearchMonth = null; // Đổi tên biến
        if (selectedMonthStr != null && !"Tất cả tháng".equals(selectedMonthStr)) {
            try {
                currentSearchMonth = Integer.parseInt(selectedMonthStr.replace("Tháng ", ""));
            } catch (NumberFormatException e) {
                System.err.println("ManageBookingsController: Lỗi parse tháng: " + selectedMonthStr);
            }
        }

        Integer currentSearchYear = filterByYearComboBox.getValue(); // Đổi tên biến

        String selectedUserName = filterByUserComboBox.getValue();
        String currentSearchUserId = null; // Đổi tên biến
        if (selectedUserName != null && !"Tất cả người dùng".equals(selectedUserName)) {
            currentSearchUserId = userDisplayToIdMap.get(selectedUserName);
            if (currentSearchUserId == null) {
                System.err.println("ManageBookingsController: Không tìm thấy ID cho người dùng đã chọn: " + selectedUserName + ". Sẽ không lọc theo người dùng.");
            }
        }

        System.out.println(String.format("ManageBookingsController: Loading bookings - RoomId: %s, Month: %s, Year: %s, UserId: %s, Page: %d, Size: %d",
                currentSearchRoomId, currentSearchMonth, currentSearchYear, currentSearchUserId, currentPageNumber, currentPageSize));

        // Tạo các biến effectively final cho lambda
        final String finalRoomId = currentSearchRoomId;
        final Integer finalMonth = currentSearchMonth;
        final Integer finalYear = currentSearchYear;
        final String finalUserId = currentSearchUserId;

        try {
            Page<BookingResponse> pagedResponse = bookingService.getAllBookings(
                    finalRoomId, finalMonth, finalYear, finalUserId, currentPageNumber, currentPageSize // Sử dụng các biến final
            );

            Platform.runLater(() -> { // Đây là lambda expression
                if (pagedResponse != null) {
                    currentTableData.setAll(pagedResponse.getContent() != null ? pagedResponse.getContent() : Collections.emptyList());
                    bookingsTable.setItems(currentTableData);

                    this.currentPageNumber = pagedResponse.getNumber();
                    this.totalPages = pagedResponse.getTotalPages();
                    this.totalElements = pagedResponse.getTotalElements();
                    updatePaginationUI();

                    if (pagedResponse.getContent() == null || pagedResponse.getContent().isEmpty()) {
                        // Dòng gây lỗi trước đó - giờ sử dụng các biến effectively final
                        if (this.totalElements == 0 && (finalRoomId != null || finalMonth != null || finalYear != null || finalUserId != null) ) {
                            new Alert(Alert.AlertType.INFORMATION, "Không có đặt phòng nào phù hợp với tiêu chí tìm kiếm.").showAndWait();
                        } else if (this.totalElements == 0) {
                            // Không hiển thị gì nếu không có dữ liệu ban đầu và không có filter
                        }
                    }
                } else {
                    currentTableData.clear();
                    bookingsTable.setItems(currentTableData);
                    new Alert(Alert.AlertType.ERROR, "Không nhận được phản hồi dữ liệu đặt phòng từ server.").showAndWait();
                    this.totalPages = 0;
                    this.totalElements = 0;
                    updatePaginationUI();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "Không thể tải danh sách đặt phòng: " + e.getMessage()).showAndWait();
                currentTableData.clear();
                bookingsTable.setItems(currentTableData);
                this.totalPages = 0;
                this.totalElements = 0;
                updatePaginationUI();
            });
        }
    }

    private void updatePaginationUI() {
        if (totalElements == 0) {
            pageInfoLabel.setText("Không có dữ liệu");
            prevPageButton.setDisable(true);
            nextPageButton.setDisable(true);
        } else {
            pageInfoLabel.setText("Trang " + (currentPageNumber + 1) + " / " + totalPages + " (Tổng: " + totalElements + " mục)");
            prevPageButton.setDisable(currentPageNumber <= 0);
            nextPageButton.setDisable(currentPageNumber >= totalPages - 1);
        }
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPageNumber > 0) {
            currentPageNumber--;
            loadBookingsFromServer();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPageNumber < totalPages - 1) {
            currentPageNumber++;
            loadBookingsFromServer();
        }
    }
    @FXML // Hoặc public
    private void handleRowsPerPageChange(ActionEvent event) { // Thêm ActionEvent nếu onAction được dùng bởi Button, ComboBox onAction thường không cần
        Integer selected = rowsPerPageComboBox.getValue();
        if (selected != null && selected != currentPageSize) { // Sử dụng currentPageSize
            currentPageSize = selected;
            currentPageNumber = 0; // Reset về trang đầu
            loadBookingsFromServer(); // Gọi hàm tải dữ liệu từ server
        }
    }
    private String formatDateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy", new Locale("vi", "VN"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("vi", "VN"));

        if (start.toLocalDate().equals(end.toLocalDate())) {
            return String.format("%s (%s - %s)", start.format(dateFormatter), start.format(timeFormatter), end.format(timeFormatter));
        } else {
            DateTimeFormatter fullRangeFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", new Locale("vi", "VN"));
            return String.format("%s - %s", start.format(fullRangeFormatter), end.format(fullRangeFormatter));
        }
    }

    private String formatSingleDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", new Locale("vi", "VN"));
        return dateTime.format(formatter);
    }

    private String translateBookingStatus(String status) {
        if (status == null) return "";
        return switch (status.toUpperCase()) {
            case "COMPLETED" -> "Đã hoàn thành";
            case "PENDING_APPROVAL" -> "Chờ duyệt";
            case "CANCELLED" -> "Đã hủy";
            case "REJECTED" -> "Đã từ chối";
            case "CONFIRMED" -> "Đã duyệt";
            case "IN_PROGRESS" -> "Đang mượn";
            case "OVERDUE" -> "Quá hạn";
            default -> status;
        };
    }

    @FXML
    private void handleExportBookingsPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(bookingsTable.getScene().getWindow());

        if (file != null) {
            try (InputStream fontStream = getClass().getResourceAsStream("/com/utc2/facilityui/fonts/Roboto-Regular.ttf")) {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                if (fontStream == null) {
                    throw new IOException("Không tìm thấy file font Roboto-Regular.ttf trong resources");
                }
                byte[] fontBytes = fontStream.readAllBytes();
                final PdfFont unicodeFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, true);
                document.setFont(unicodeFont);

                Paragraph titlePara = new Paragraph("DANH SÁCH ĐẶT PHÒNG")
                        .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER).setMarginBottom(15);
                document.add(titlePara);

                float[] columnWidths = {2.5f, 1.5f, 2.5f, 3.5f, 3f, 2.5f, 2f, 2f, 2.5f, 3f};
                Table pdfTable = new Table(UnitValue.createPercentArray(columnWidths));
                pdfTable.setWidth(UnitValue.createPercentValue(100));

                String[] headers = {
                        "Người đặt", "Phòng", "Mục đích", "Thời gian dự kiến", "Thiết bị",
                        "Yêu cầu lúc", "Trạng thái", "Người duyệt", "Lý do hủy", "Ghi chú"
                };
                for (String header : headers) {
                    pdfTable.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
                }

                final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", new Locale("vi","VN"));
                ObservableList<BookingResponse> itemsToExport = currentTableData;

                for (BookingResponse booking : itemsToExport) {
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getUserName()))));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getRoomName()))));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getPurpose()))));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(formatDateTimeRange(booking.getPlannedStartTime(), booking.getPlannedEndTime())))));

                    List<BookedEquipmentItem> equipments = booking.getBookedEquipments();
                    String equipmentDisplay = "Không có";
                    if (equipments != null && !equipments.isEmpty()) {
                        equipmentDisplay = equipments.stream()
                                .map(BookedEquipmentItem::getEquipmentModelName)
                                .filter(Objects::nonNull)
                                .collect(Collectors.joining(", "));
                        if (equipmentDisplay.isEmpty()) equipmentDisplay = "Không có";
                    }
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(equipmentDisplay))));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getCreatedAt() !=null ? booking.getCreatedAt().format(timeFormatter) : ""))));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(translateBookingStatus(booking.getStatus())))));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getApprovedByUserName()))));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getCancellationReason()))));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getNote()))));
                }

                document.add(pdfTable);
                document.close();
                new Alert(Alert.AlertType.INFORMATION, "Xuất file PDF thành công!").showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Đã xảy ra lỗi khi xuất PDF: " + e.getMessage()).showAndWait();
            }
        }
    }

    private String safeText(String value) {
        return value != null ? value : "";
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
                        "Người đặt", "Phòng", "Mục đích", "Thời gian bắt đầu dự kiến", "Thời gian kết thúc dự kiến",
                        "Thiết bị", "Yêu cầu lúc", "Trạng thái", "Người duyệt/hủy", "Lý do hủy", "Ghi chú"
                };

                Row headerRowExcel = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRowExcel.createCell(i);
                    cell.setCellValue(headers[i]);
                }
                // KHAI BÁO excelDateTimeFormatter LÀ FINAL
                final DateTimeFormatter excelDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                ObservableList<BookingResponse> itemsToExport = currentTableData;

                int rowIndex = 1;
                for (BookingResponse booking : itemsToExport) {
                    Row rowData = sheet.createRow(rowIndex++);
                    rowData.createCell(0).setCellValue(safeText(booking.getUserName()));
                    rowData.createCell(1).setCellValue(safeText(booking.getRoomName()));
                    rowData.createCell(2).setCellValue(safeText(booking.getPurpose()));
                    rowData.createCell(3).setCellValue(booking.getPlannedStartTime() != null ? booking.getPlannedStartTime().format(excelDateTimeFormatter) : "");
                    rowData.createCell(4).setCellValue(booking.getPlannedEndTime() != null ? booking.getPlannedEndTime().format(excelDateTimeFormatter) : "");

                    List<BookedEquipmentItem> equipments = booking.getBookedEquipments();
                    String equipmentDisplay = "";
                    if (equipments != null && !equipments.isEmpty()) {
                        equipmentDisplay = equipments.stream()
                                .map(BookedEquipmentItem::getEquipmentModelName)
                                .filter(Objects::nonNull)
                                .collect(Collectors.joining(", "));
                    }
                    rowData.createCell(5).setCellValue(safeText(equipmentDisplay.isEmpty() ? "Không có" : equipmentDisplay));
                    // Dòng 307 của bạn (hoặc gần đó)
                    rowData.createCell(6).setCellValue(booking.getCreatedAt() != null ? booking.getCreatedAt().format(excelDateTimeFormatter) : "");
                    rowData.createCell(7).setCellValue(safeText(translateBookingStatus(booking.getStatus())));
                    rowData.createCell(8).setCellValue(safeText(booking.getApprovedByUserName() != null ? booking.getApprovedByUserName() : booking.getCancelledByUserName()));
                    rowData.createCell(9).setCellValue(safeText(booking.getCancellationReason()));
                    rowData.createCell(10).setCellValue(safeText(booking.getNote()));
                }

                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                new Alert(Alert.AlertType.INFORMATION, "Xuất file Excel thành công!").showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Đã xảy ra lỗi khi xuất Excel: " + e.getMessage()).showAndWait();
            }
        }
    }

    // Các hàm Alert đã được loại bỏ theo yêu cầu trước và thay bằng new Alert(...) trực tiếp
}