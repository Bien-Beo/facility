package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.BookedEquipmentItem;
import com.utc2.facilityui.model.RoomItem;
import com.utc2.facilityui.model.UserItem;
import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.response.Page; // Client-side Page DTO
import com.utc2.facilityui.service.BookingService;
import com.utc2.facilityui.service.RoomService;
import com.utc2.facilityui.service.UserClientService;

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
import javafx.stage.Window;
import javafx.util.StringConverter;

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
import java.time.format.DateTimeFormatter;
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
    @FXML private TableColumn<BookingResponse, Void> actionColumn;

    @FXML private ComboBox<RoomItem> filterByRoomComboBox;
    @FXML private ComboBox<String> filterByMonthComboBox;
    @FXML private ComboBox<Integer> filterByYearComboBox;
    @FXML private ComboBox<UserItem> filterByUserComboBox;

    @FXML private Button resetButton;

    @FXML private ComboBox<Integer> rowsPerPageComboBox;
    @FXML private Label pageInfoLabel; // Đã sửa lại từ myPageInfoLabel
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;

    private final BookingService bookingService = new BookingService();
    private final RoomService roomListService = new RoomService();
    private final UserClientService userListService = new UserClientService();

    private ObservableList<BookingResponse> currentTableData = FXCollections.observableArrayList();

    private int currentPageNumber = 0;
    private int currentPageSize = 10;
    private int totalPages = 0;
    private long totalElements = 0;

    private boolean initializingFilters = true; // Cờ kiểm soát cho initFilterControls

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ManageBookingsController: initialize()");
        initTableColumns();

        initializingFilters = true;
        initFilterControls();
        initializingFilters = false;

        if (resetButton != null) {
            resetButton.setOnAction(e -> handleResetFilters());
        }
        loadBookingsFromServer(); // Tải dữ liệu lần đầu
    }

    private void initTableColumns() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        purposeColumn.setCellFactory(column -> createWrappingTextCell());

        plannedTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        formatDateTimeRange(cellData.getValue().getPlannedStartTime(), cellData.getValue().getPlannedEndTime())
                )
        );
        plannedTimeColumn.setCellFactory(column -> createWrappingTextCell());

        equipmentColumn.setCellValueFactory(cellData -> {
            List<BookedEquipmentItem> equipments = cellData.getValue().getBookedEquipments();
            String display = "Không có";
            if (equipments != null && !equipments.isEmpty()) {
                display = equipments.stream()
                        .map(BookedEquipmentItem::getEquipmentModelName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));
                if (display.isEmpty()) display = "Không có";
            }
            return new SimpleStringProperty(display);
        });
        equipmentColumn.setCellFactory(column -> createWrappingTextCell());


        actualTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        formatSingleDateTime(cellData.getValue().getCreatedAt())
                )
        );
        actualTimeColumn.setCellFactory(column -> createWrappingTextCell());


        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(translateBookingStatus(cellData.getValue().getStatus()))
        );
        approvedByColumn.setCellValueFactory(new PropertyValueFactory<>("approvedByUserName"));
        cancellationReasonColumn.setCellValueFactory(new PropertyValueFactory<>("cancellationReason"));
        cancellationReasonColumn.setCellFactory(column -> createWrappingTextCell());
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        noteColumn.setCellFactory(column -> createWrappingTextCell());

        // TODO: actionColumn.setCellFactory(...);
    }

    private TableCell<BookingResponse, String> createWrappingTextCell() {
        return new TableCell<>() {
            private final Text text = new Text();
            {
                text.setStyle("-fx-font-size: 12px;");
                text.wrappingWidthProperty().bind(this.widthProperty().subtract(10));
                setGraphic(text);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                text.setText(empty || item == null ? null : item);
            }
        };
    }


    private void initFilterControls() {
        rowsPerPageComboBox.setItems(FXCollections.observableArrayList(5, 10, 20, 50, 100));
        rowsPerPageComboBox.setValue(currentPageSize);
        rowsPerPageComboBox.setOnAction(e -> {
            if (initializingFilters) return;
            handleRowsPerPageChange(e);
        });

        ObservableList<String> monthItems = FXCollections.observableArrayList();
        monthItems.add("Tất cả tháng");
        for (int i = 1; i <= 12; i++) {
            monthItems.add("Tháng " + i);
        }
        filterByMonthComboBox.setItems(monthItems);
        filterByMonthComboBox.getSelectionModel().selectFirst();
        filterByMonthComboBox.setOnAction(e -> {
            if (initializingFilters) return;
            currentPageNumber = 0; loadBookingsFromServer();
        });

        ObservableList<Integer> yearItems = FXCollections.observableArrayList();
        yearItems.add(null);
        int currentSystemYear = java.time.Year.now().getValue();
        for (int i = currentSystemYear + 1; i >= currentSystemYear - 5; i--) {
            yearItems.add(i);
        }
        filterByYearComboBox.setItems(yearItems);
        filterByYearComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Integer year) {
                return year == null ? "Tất cả năm" : year.toString();
            }
            @Override public Integer fromString(String string) {
                return (string == null || string.isEmpty() || string.equals("Tất cả năm")) ? null : Integer.parseInt(string);
            }
        });
        filterByYearComboBox.getSelectionModel().select(null);
        filterByYearComboBox.setOnAction(e -> {
            if (initializingFilters) return;
            currentPageNumber = 0; loadBookingsFromServer();
        });

        final RoomItem allRoomsPlaceholder = new RoomItem(null, "Tất cả phòng");
        filterByRoomComboBox.setConverter(new StringConverter<RoomItem>() {
            @Override public String toString(RoomItem room) { return room == null ? "Tất cả phòng" : room.getName(); }
            @Override public RoomItem fromString(String string) { return null; }
        });
        filterByRoomComboBox.setPlaceholder(new Label("Đang tải phòng..."));
        filterByRoomComboBox.getItems().add(allRoomsPlaceholder);
        filterByRoomComboBox.getSelectionModel().selectFirst();
        new Thread(() -> {
            try {
                List<RoomItem> rooms = roomListService.getAllRoomsForFilter();
                Platform.runLater(() -> {
                    ObservableList<RoomItem> roomItems = FXCollections.observableArrayList();
                    roomItems.add(allRoomsPlaceholder);
                    roomItems.addAll(rooms);
                    filterByRoomComboBox.setItems(roomItems);
                    if (!roomItems.isEmpty()) {
                        filterByRoomComboBox.getSelectionModel().selectFirst();
                    }
                    if (rooms.isEmpty()) filterByRoomComboBox.setPlaceholder(new Label("Không có phòng"));
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    filterByRoomComboBox.setPlaceholder(new Label("Lỗi tải phòng"));
                    System.err.println("Lỗi tải phòng cho ComboBox: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
        filterByRoomComboBox.setOnAction(e -> {
            if (initializingFilters) return;
            currentPageNumber = 0; loadBookingsFromServer();
        });

        final UserItem allUsersPlaceholder = new UserItem(null, "Tất cả người dùng", null);
        filterByUserComboBox.setConverter(new StringConverter<UserItem>() {
            @Override public String toString(UserItem user) { return user == null ? "Tất cả người dùng" : user.getDisplayName(); }
            @Override public UserItem fromString(String string) { return null; }
        });
        filterByUserComboBox.setPlaceholder(new Label("Đang tải người dùng..."));
        filterByUserComboBox.getItems().add(allUsersPlaceholder);
        filterByUserComboBox.getSelectionModel().selectFirst();
        new Thread(() -> {
            try {
                List<UserItem> users = userListService.getAllUsersForFilter();
                Platform.runLater(() -> {
                    ObservableList<UserItem> userItems = FXCollections.observableArrayList();
                    userItems.add(allUsersPlaceholder);
                    userItems.addAll(users);
                    filterByUserComboBox.setItems(userItems);
                    if (!userItems.isEmpty()) {
                        filterByUserComboBox.getSelectionModel().selectFirst();
                    }
                    if (users.isEmpty()) filterByUserComboBox.setPlaceholder(new Label("Không có người dùng"));
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    filterByUserComboBox.setPlaceholder(new Label("Lỗi tải người dùng"));
                    System.err.println("Lỗi tải người dùng cho ComboBox: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
        filterByUserComboBox.setOnAction(e -> {
            if (initializingFilters) return;
            currentPageNumber = 0; loadBookingsFromServer();
        });

        prevPageButton.setOnAction(e -> handlePreviousPage());
        nextPageButton.setOnAction(e -> handleNextPage());
    }

    @FXML
    private void handleResetFilters() {
        initializingFilters = true; // Tạm thời bật cờ để tránh load nhiều lần khi reset
        if (filterByRoomComboBox.getItems() != null && !filterByRoomComboBox.getItems().isEmpty()) filterByRoomComboBox.getSelectionModel().selectFirst();
        if (filterByMonthComboBox.getItems() != null && !filterByMonthComboBox.getItems().isEmpty()) filterByMonthComboBox.getSelectionModel().selectFirst();
        if (filterByYearComboBox.getItems() != null && !filterByYearComboBox.getItems().isEmpty()) filterByYearComboBox.getSelectionModel().selectFirst();
        if (filterByUserComboBox.getItems() != null && !filterByUserComboBox.getItems().isEmpty()) filterByUserComboBox.getSelectionModel().selectFirst();
        initializingFilters = false;

        currentPageNumber = 0;
        loadBookingsFromServer();
    }

    private void loadBookingsFromServer() {
        RoomItem selectedRoom = filterByRoomComboBox.getSelectionModel().getSelectedItem();
        final String finalRoomId = (selectedRoom != null && selectedRoom.getId() != null) ? selectedRoom.getId() : null;

        String selectedMonthStr = filterByMonthComboBox.getValue();
        Integer tempSelectedMonth = null;
        if (selectedMonthStr != null && !selectedMonthStr.equals("Tất cả tháng")) {
            try {
                tempSelectedMonth = Integer.parseInt(selectedMonthStr.replace("Tháng ", ""));
            } catch (NumberFormatException e) { System.err.println("Lỗi parse tháng: " + selectedMonthStr); }
        }
        final Integer finalMonth = tempSelectedMonth;
        final Integer finalYear = filterByYearComboBox.getValue();

        UserItem selectedUser = filterByUserComboBox.getSelectionModel().getSelectedItem();
        final String finalUserId = (selectedUser != null && selectedUser.getId() != null) ? selectedUser.getId() : null;

        System.out.printf("ManageBookings: Loading - RoomId: %s, Month: %s, Year: %s, UserId: %s, Page: %d, Size: %d%n",
                finalRoomId, finalMonth, finalYear, finalUserId, currentPageNumber, currentPageSize);

        bookingsTable.setPlaceholder(new Label("Đang tải danh sách đặt phòng..."));

        new Thread(() -> {
            try {
                Page<BookingResponse> pagedResponse = bookingService.getAllBookings(
                        finalRoomId, finalMonth, finalYear, finalUserId, currentPageNumber, currentPageSize
                );

                Platform.runLater(() -> {
                    if (pagedResponse != null) {
                        List<BookingResponse> content = pagedResponse.getContent() != null ? pagedResponse.getContent() : Collections.emptyList();
                        currentTableData.setAll(content);
                        bookingsTable.setItems(currentTableData);

                        this.currentPageNumber = pagedResponse.getNumber();
                        long serverReportedTotalElements = pagedResponse.getTotalElements();
                        int serverReportedTotalPages = pagedResponse.getTotalPages();

                        if (serverReportedTotalElements == 0 && !content.isEmpty()) {
                            // BUG SERVER: Server báo totalElements = 0, nhưng lại gửi dữ liệu.
                            // Client workaround: Sử dụng giá trị đặc biệt để đánh dấu tình trạng này.
                            this.totalElements = -1; // Đánh dấu: có dữ liệu nhưng không rõ tổng số
                            this.totalPages = -1;    // Đánh dấu: không rõ tổng số trang
                            System.out.println("ManageBookings: Server reported 0 total elements but sent content. Using workaround values for pagination state.");
                        } else {
                            // Server báo cáo bình thường (hoặc 0 total và content cũng rỗng)
                            this.totalElements = serverReportedTotalElements;
                            this.totalPages = serverReportedTotalPages;
                        }

                        updatePaginationUI(); // Gọi sau khi cập nhật các biến

                        // Cập nhật placeholder cho bảng dựa trên content thực tế của trang này
                        if (content.isEmpty()) {
                            if (this.totalElements == 0 && this.totalPages == 0 && serverReportedTotalElements == 0) {
                                // Thực sự không có dữ liệu nào theo server và client
                                bookingsTable.setPlaceholder(new Label("Không có đặt phòng nào phù hợp với tiêu chí."));
                            } else if (this.totalElements == -1) {
                                // Có thể có dữ liệu ở đâu đó, nhưng trang này rỗng (ít khả năng xảy ra với bug server kiểu này)
                                // Hoặc đây là trang cuối cùng khi server báo sai
                                bookingsTable.setPlaceholder(new Label("Không có thêm dữ liệu ở trang này."));
                            } else if (this.currentPageNumber >= this.totalPages && this.totalPages > 0) {
                                // Yêu cầu trang vượt quá tổng số trang server báo (khi server báo đúng)
                                bookingsTable.setPlaceholder(new Label("Không có dữ liệu ở trang này."));
                            } else {
                                // Trường hợp chung khi content rỗng nhưng có thể có dữ liệu ở trang khác
                                bookingsTable.setPlaceholder(new Label("Không có đặt phòng nào phù hợp với tiêu chí ở trang này."));
                            }
                        } else {
                            bookingsTable.setPlaceholder(null); // Có content, không cần placeholder
                        }
                    } else {
                        System.err.println("ManageBookings: pagedResponse từ service là null.");
                        currentTableData.clear();
                        bookingsTable.setItems(currentTableData);
                        bookingsTable.setPlaceholder(new Label("Không nhận được phản hồi từ server."));
                        this.totalPages = 0;
                        this.totalElements = 0;
                        this.currentPageNumber = 0;
                        updatePaginationUI();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR,"Lỗi Tải Dữ Liệu", "Không thể tải danh sách đặt phòng: " + e.getMessage());
                    currentTableData.clear();
                    bookingsTable.setItems(currentTableData);
                    bookingsTable.setPlaceholder(new Label("Lỗi tải dữ liệu."));
                    this.totalPages = 0;
                    this.totalElements = 0;
                    this.currentPageNumber = 0;
                    updatePaginationUI();
                });
            }
        }).start();
    }

    private void updatePaginationUI() {
        System.out.println("ManageBookings: Updating pagination UI - currentPage: " + this.currentPageNumber +
                ", totalPages: " + this.totalPages + ", totalElements: " + this.totalElements +
                ", currentTableDataSize: " + currentTableData.size());

        boolean hasContentButUnknownTotal = (this.totalElements == -1 && this.totalPages == -1);

        if (pageInfoLabel != null) {
            if (hasContentButUnknownTotal) {
                // Server báo sai, nhưng có dữ liệu hiển thị
                pageInfoLabel.setText(String.format("Trang %d (hiển thị %d mục, tổng số không xác định)",
                        this.currentPageNumber + 1, // Hiển thị 1-based
                        currentTableData.size()));
            } else if (this.totalElements == 0 && currentTableData.isEmpty()) {
                // Thực sự không có dữ liệu
                pageInfoLabel.setText("Không có dữ liệu");
            } else if (this.totalElements == 0 && !currentTableData.isEmpty()){
                // Trường hợp này gần như không xảy ra nếu logic ở loadBookingsFromServer đúng,
                // vì nếu content không empty, totalElements sẽ là -1 (unknown) hoặc > 0.
                // Nếu server trả về totalElements = 0, totalPages > 0, và content không rỗng (1 kịch bản lạ khác)
                pageInfoLabel.setText(String.format("Trang %d (hiển thị %d mục, server báo không có tổng)",
                        this.currentPageNumber + 1,
                        currentTableData.size()));
            }
            else {
                // Server báo cáo bình thường và có dữ liệu (hoặc totalElements > 0)
                pageInfoLabel.setText(String.format("Trang %d / %d (Tổng: %d)",
                        this.currentPageNumber + 1, // Hiển thị 1-based
                        this.totalPages,
                        this.totalElements));
            }
        }

        boolean isFirstPage = (this.currentPageNumber == 0);
        boolean isEffectivelyLastPage;

        if (hasContentButUnknownTotal) {
            // Nếu không rõ tổng, nút Next sẽ được bật nếu trang hiện tại đầy.
            isEffectivelyLastPage = (currentTableData.size() < this.currentPageSize);
        } else if (this.totalElements == 0) { // Và không phải trường hợp hasContentButUnknownTotal
            isEffectivelyLastPage = true;
        } else { // Trường hợp server báo cáo bình thường (totalElements > 0)
            isEffectivelyLastPage = (this.currentPageNumber >= this.totalPages - 1);
        }

        // Nếu không có dữ liệu nào cả (dựa trên totalElements đã được điều chỉnh hoặc từ server)
        // và không phải trường hợp "có content nhưng không rõ tổng"
        boolean noDataAtAllForControls = (this.totalElements == 0 && !hasContentButUnknownTotal && currentTableData.isEmpty());


        if (prevPageButton != null) {
            prevPageButton.setDisable(isFirstPage || noDataAtAllForControls);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(isEffectivelyLastPage || noDataAtAllForControls);
        }
        if (rowsPerPageComboBox != null) {
            // Chỉ vô hiệu hóa nếu thực sự không có dữ liệu theo cách hiểu của client
            rowsPerPageComboBox.setDisable(noDataAtAllForControls && !hasContentButUnknownTotal);
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
        // Nếu totalPages là -1 (không rõ), vẫn cho phép next nếu isEffectivelyLastPage là false
        boolean canGoNext = (this.totalPages == -1 && ! (currentTableData.size() < this.currentPageSize) ) ||
                (this.totalPages > 0 && currentPageNumber < totalPages - 1);

        if (canGoNext) {
            currentPageNumber++;
            loadBookingsFromServer();
        }
    }

    @FXML
    private void handleRowsPerPageChange(ActionEvent event) {
        Integer selected = rowsPerPageComboBox.getValue();
        if (selected != null && selected != currentPageSize) {
            currentPageSize = selected;
            currentPageNumber = 0; // Reset về trang đầu khi thay đổi số dòng
            loadBookingsFromServer();
        }
    }

    private String formatDateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "N/A";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy", new Locale("vi", "VN"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("vi", "VN"));
        DateTimeFormatter fullRangeFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", new Locale("vi", "VN"));

        if (start.toLocalDate().equals(end.toLocalDate())) {
            return String.format("%s (%s - %s)", start.format(dateFormatter), start.format(timeFormatter), end.format(timeFormatter));
        } else {
            return String.format("%s đến %s", start.format(fullRangeFormatter), end.format(fullRangeFormatter));
        }
    }

    private String formatSingleDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy", new Locale("vi", "VN"));
        return dateTime.format(formatter);
    }

    private String translateBookingStatus(String status) {
        if (status == null) return "N/A";
        return switch (status.toUpperCase()) {
            case "COMPLETED" -> "Đã hoàn thành";
            case "PENDING_APPROVAL" -> "Chờ duyệt";
            case "CANCELLED" -> "Đã hủy";
            case "REJECTED" -> "Đã từ chối";
            case "CONFIRMED" -> "Đã duyệt";
            case "IN_PROGRESS" -> "Đang sử dụng";
            case "OVERDUE" -> "Quá hạn";
            default -> status;
        };
    }

    private String safeText(String value) {
        return value != null ? value : "";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        TextArea contentArea = new TextArea(message);
        contentArea.setWrapText(true);
        contentArea.setEditable(false);
        contentArea.setPrefHeight(100);
        alert.getDialogPane().setContent(contentArea);
        alert.setResizable(true);

        Window owner = getWindow();
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.showAndWait();
    }

    private Window getWindow() {
        try {
            if (bookingsTable != null && bookingsTable.getScene() != null && bookingsTable.getScene().getWindow() != null) {
                return bookingsTable.getScene().getWindow();
            }
            if (resetButton != null && resetButton.getScene() != null && resetButton.getScene().getWindow() != null) {
                return resetButton.getScene().getWindow();
            }
        } catch (Exception e) {
            System.err.println("Không thể xác định cửa sổ chủ cho Alert: " + e.getMessage());
        }
        return null;
    }


    @FXML
    private void handleExportBookingsPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        Window ownerWindow = getWindow();
        File file = fileChooser.showSaveDialog(ownerWindow);

        if (file != null) {
            try (InputStream fontStream = getClass().getResourceAsStream("/com/utc2/facilityui/fonts/Roboto-Regular.ttf")) {
                if (fontStream == null) {
                    throw new IOException("Không tìm thấy file font Roboto-Regular.ttf trong resources");
                }
                byte[] fontBytes = fontStream.readAllBytes();
                final PdfFont unicodeFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, true);

                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                document.setFont(unicodeFont);

                Paragraph titlePara = new Paragraph("DANH SÁCH ĐẶT PHÒNG")
                        .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER).setMarginBottom(15);
                document.add(titlePara);

                float[] columnWidths = {2.5f, 1.8f, 3f, 3.8f, 3f, 2.8f, 2.2f, 2.5f, 2.5f, 3f};
                Table pdfTable = new Table(UnitValue.createPercentArray(columnWidths));
                pdfTable.setWidth(UnitValue.createPercentValue(100));

                String[] headers = {
                        "Người đặt", "Phòng", "Mục đích", "Thời gian dự kiến", "Thiết bị",
                        "Yêu cầu lúc", "Trạng thái", "Người xử lý", "Lý do hủy", "Ghi chú"
                };
                for (String header : headers) {
                    pdfTable.addHeaderCell(new Cell().add(new Paragraph(header).setBold().setFontSize(10)));
                }

                ObservableList<BookingResponse> itemsToExport = currentTableData;

                for (BookingResponse booking : itemsToExport) {
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getUserName())).setFontSize(9)));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getRoomName())).setFontSize(9)));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getPurpose())).setFontSize(9)));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(formatDateTimeRange(booking.getPlannedStartTime(), booking.getPlannedEndTime()))).setFontSize(9)));

                    List<BookedEquipmentItem> equipments = booking.getBookedEquipments();
                    String equipmentDisplay = "Không có";
                    if (equipments != null && !equipments.isEmpty()) {
                        equipmentDisplay = equipments.stream()
                                .map(BookedEquipmentItem::getEquipmentModelName)
                                .filter(Objects::nonNull)
                                .collect(Collectors.joining(", "));
                        if (equipmentDisplay.isEmpty()) equipmentDisplay = "Không có";
                    }
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(equipmentDisplay)).setFontSize(9)));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(formatSingleDateTime(booking.getCreatedAt()))).setFontSize(9)));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(translateBookingStatus(booking.getStatus()))).setFontSize(9)));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getApprovedByUserName())).setFontSize(9)));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getCancellationReason())).setFontSize(9)));
                    pdfTable.addCell(new Cell().add(new Paragraph(safeText(booking.getNote())).setFontSize(9)));
                }

                document.add(pdfTable);
                document.close();
                showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Xuất file PDF thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi Xuất PDF", "Đã xảy ra lỗi khi xuất PDF: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportBookingsExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
        Window ownerWindow = getWindow();
        File file = fileChooser.showSaveDialog(ownerWindow);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Bookings");
                String[] headers = {
                        "Người đặt", "Phòng", "Mục đích", "Thời gian bắt đầu dự kiến", "Thời gian kết thúc dự kiến",
                        "Thiết bị", "Yêu cầu lúc", "Trạng thái", "Người xử lý", "Lý do hủy", "Ghi chú"
                };

                Row headerRowExcel = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRowExcel.createCell(i);
                    cell.setCellValue(headers[i]);
                }
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
                showAlert(Alert.AlertType.INFORMATION,"Thành Công", "Xuất file Excel thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR,"Lỗi Xuất Excel", "Đã xảy ra lỗi khi xuất Excel: " + e.getMessage());
            }
        }
    }
}