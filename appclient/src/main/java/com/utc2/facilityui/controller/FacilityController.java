package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.BuildingItem;
import com.utc2.facilityui.model.Facility;
import com.utc2.facilityui.model.RoomTypeItem;
import com.utc2.facilityui.model.UserItem;
import com.utc2.facilityui.model.OperationsTableCell;
import com.utc2.facilityui.model.OperationsTableCellFactory;
import com.utc2.facilityui.service.BuildingClientService;
import com.utc2.facilityui.service.RoomService;
import com.utc2.facilityui.service.RoomTypeClientService;
import com.utc2.facilityui.service.UserClientService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

// Đảm bảo import EditFacilityController
import com.utc2.facilityui.controller.EditFacilityController; // <<< THÊM IMPORT NÀY

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table; // iText Table
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class FacilityController implements Initializable, OperationsTableCell.OperationsEventHandler<Facility> {

    @FXML private TableView<Facility> facilityTable;
    @FXML private TableColumn<Facility, String> nameColumn;
    @FXML private TableColumn<Facility, String> descriptionColumn;
    @FXML private TableColumn<Facility, String> statusColumn;
    @FXML private TableColumn<Facility, String> createdAtColumn;
    @FXML private TableColumn<Facility, String> updatedAtColumn;
    @FXML private TableColumn<Facility, String> managerColumn;
    @FXML private TableColumn<Facility, Void> operationsColumn;
    @FXML private Button btnExport;
    @FXML private Button btnAddFacility;

    @FXML private ComboBox<BuildingItem> filterBuildingComboBox;
    @FXML private ComboBox<RoomTypeItem> filterRoomTypeComboBox;
    @FXML private TextField filterYearTextField;
    @FXML private ComboBox<UserItem> filterManagerComboBox;
    @FXML private Button btnResetFilters;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;
    @FXML private ComboBox<Integer> rowsPerPageComboBox;

    private ObservableList<Facility> facilityDataList = FXCollections.observableArrayList();
    private RoomService roomService;
    private BuildingClientService buildingClientService;
    private RoomTypeClientService roomTypeClientService;
    private UserClientService userClientService;

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;
    private long totalElements = 0;

    private final DateTimeFormatter pdfTimestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("FacilityController initialize() called!");
        this.roomService = new RoomService();
        this.buildingClientService = new BuildingClientService();
        this.roomTypeClientService = new RoomTypeClientService();
        this.userClientService = new UserClientService();

        setupTableColumns();
        facilityTable.setItems(facilityDataList);

        if (rowsPerPageComboBox != null) {
            rowsPerPageComboBox.setItems(FXCollections.observableArrayList(10, 15, 20, 25, 50));
            rowsPerPageComboBox.setValue(pageSize);
            rowsPerPageComboBox.setOnAction(event -> {
                pageSize = rowsPerPageComboBox.getValue();
                currentPage = 0;
                applyFiltersAndReloadData();
            });
        }

        setupFilterComboBoxes();

        filterBuildingComboBox.setOnAction(event -> applyFiltersAndReloadData());
        filterRoomTypeComboBox.setOnAction(event -> applyFiltersAndReloadData());
        filterManagerComboBox.setOnAction(event -> applyFiltersAndReloadData());
        filterYearTextField.setOnAction(event -> applyFiltersAndReloadData());

        loadFacilitiesData();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        managerColumn.setCellValueFactory(new PropertyValueFactory<>("nameFacilityManager"));

        descriptionColumn.setCellFactory(tc -> {
            TableCell<Facility, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionColumn.widthProperty().subtract(15));
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        operationsColumn.setCellFactory(new OperationsTableCellFactory<>(this)); // 'this' là FacilityController
        facilityTable.setPlaceholder(new Label("Đang tải dữ liệu..."));
    }

    private void setupFilterComboBoxes() {
        // Setup filterBuildingComboBox
        filterBuildingComboBox.setConverter(new StringConverter<BuildingItem>() {
            @Override public String toString(BuildingItem object) { return object == null || object.getId() == null ? "Tất cả tòa nhà" : object.getName(); }
            @Override public BuildingItem fromString(String string) { return null; }
        });
        filterBuildingComboBox.setPlaceholder(new Label("Tải tòa nhà..."));
        new Thread(() -> {
            try {
                List<BuildingItem> buildings = buildingClientService.getAllBuildings();
                Platform.runLater(() -> {
                    ObservableList<BuildingItem> buildingItems = FXCollections.observableArrayList();
                    buildingItems.add(new BuildingItem(null, "Tất cả tòa nhà"));
                    buildingItems.addAll(buildings);
                    filterBuildingComboBox.setItems(buildingItems);
                    if (buildingItems.size() <= 1 && buildings.isEmpty()) {
                        filterBuildingComboBox.setPlaceholder(new Label("Không có tòa nhà"));
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    filterBuildingComboBox.setPlaceholder(new Label("Lỗi tải tòa nhà"));
                    showErrorAlert("Lỗi Dữ Liệu", "Không thể tải danh sách tòa nhà: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();

        // Setup filterRoomTypeComboBox
        filterRoomTypeComboBox.setConverter(new StringConverter<RoomTypeItem>() {
            @Override public String toString(RoomTypeItem object) { return object == null || object.getId() == null ? "Tất cả loại phòng" : object.getName(); }
            @Override public RoomTypeItem fromString(String string) { return null; }
        });
        filterRoomTypeComboBox.setPlaceholder(new Label("Tải loại phòng..."));
        new Thread(() -> {
            try {
                List<RoomTypeItem> roomTypes = roomTypeClientService.getAllRoomTypes();
                Platform.runLater(() -> {
                    ObservableList<RoomTypeItem> roomTypeItems = FXCollections.observableArrayList();
                    roomTypeItems.add(new RoomTypeItem(null, "Tất cả loại phòng"));
                    roomTypeItems.addAll(roomTypes);
                    filterRoomTypeComboBox.setItems(roomTypeItems);
                    if (roomTypeItems.size() <= 1 && roomTypes.isEmpty()) {
                        filterRoomTypeComboBox.setPlaceholder(new Label("Không có loại phòng"));
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    filterRoomTypeComboBox.setPlaceholder(new Label("Lỗi tải loại phòng"));
                    showErrorAlert("Lỗi Dữ Liệu", "Không thể tải danh sách loại phòng: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();

        // Setup filterManagerComboBox
        filterManagerComboBox.setConverter(new StringConverter<UserItem>() {
            @Override public String toString(UserItem object) { return object == null || object.getId() == null ? "Tất cả quản lý" : object.getDisplayName(); }
            @Override public UserItem fromString(String string) { return null; }
        });
        filterManagerComboBox.setPlaceholder(new Label("Tải quản lý..."));
        new Thread(() -> {
            try {
                List<UserItem> managers = userClientService.getAllFacilityManagers();
                Platform.runLater(() -> {
                    ObservableList<UserItem> managerItems = FXCollections.observableArrayList();
                    managerItems.add(new UserItem(null, "Tất cả quản lý", null));
                    managerItems.addAll(managers);
                    filterManagerComboBox.setItems(managerItems);
                    if (managerItems.size() <= 1 && managers.isEmpty()) {
                        filterManagerComboBox.setPlaceholder(new Label("Không có người quản lý"));
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    filterManagerComboBox.setPlaceholder(new Label("Lỗi tải quản lý"));
                    showErrorAlert("Lỗi Dữ Liệu", "Không thể tải danh sách người quản lý: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void handleResetFilters(ActionEvent event) {
        if (filterBuildingComboBox.getItems() != null && !filterBuildingComboBox.getItems().isEmpty()) {
            filterBuildingComboBox.getSelectionModel().selectFirst();
        } else {
            filterBuildingComboBox.getSelectionModel().clearSelection();
        }
        if (filterRoomTypeComboBox.getItems() != null && !filterRoomTypeComboBox.getItems().isEmpty()) {
            filterRoomTypeComboBox.getSelectionModel().selectFirst();
        } else {
            filterRoomTypeComboBox.getSelectionModel().clearSelection();
        }
        filterYearTextField.clear();
        if (filterManagerComboBox.getItems() != null && !filterManagerComboBox.getItems().isEmpty()) {
            filterManagerComboBox.getSelectionModel().selectFirst();
        } else {
            filterManagerComboBox.getSelectionModel().clearSelection();
        }
        applyFiltersAndReloadData();
    }

    private void applyFiltersAndReloadData() {
        currentPage = 0;
        loadFacilitiesData();
    }

    private void loadFacilitiesData() {
        BuildingItem selectedBuilding = filterBuildingComboBox.getSelectionModel().getSelectedItem();
        RoomTypeItem selectedRoomType = filterRoomTypeComboBox.getSelectionModel().getSelectedItem();
        String yearText = filterYearTextField.getText();
        UserItem selectedManager = filterManagerComboBox.getSelectionModel().getSelectedItem();

        final String buildingIdFilter = (selectedBuilding != null && selectedBuilding.getId() != null) ? selectedBuilding.getId() : null;
        final String roomTypeIdFilter = (selectedRoomType != null && selectedRoomType.getId() != null) ? selectedRoomType.getId() : null;
        Integer tempYearFilter = null;
        if (yearText != null && !yearText.trim().isEmpty()) {
            try {
                tempYearFilter = Integer.parseInt(yearText.trim());
                if (tempYearFilter < 1900 || tempYearFilter > LocalDateTime.now().getYear() + 10) {
                    System.err.println("Năm lọc không hợp lệ: " + tempYearFilter + ". Bỏ qua lọc theo năm.");
                    tempYearFilter = null;
                }
            } catch (NumberFormatException e) {
                System.err.println("Năm nhập vào không phải là số hợp lệ: '" + yearText + "'. Bỏ qua lọc theo năm.");
            }
        }
        final Integer yearFilter = tempYearFilter;
        final String managerIdFilter = (selectedManager != null && selectedManager.getId() != null) ? selectedManager.getId() : null;

        System.out.println("loadFacilitiesData() for page: " + currentPage + ", size: " + pageSize +
                ", buildingId: " + buildingIdFilter + ", roomTypeId: " + roomTypeIdFilter +
                ", year: " + yearFilter + ", managerId: " + managerIdFilter);

        facilityTable.setPlaceholder(new Label("Đang tải dữ liệu..."));
        facilityDataList.clear();

        new Thread(() -> {
            try {
                RoomService.PaginatedFacilitiesResponse response = roomService.getFacilitiesPaginated(
                        currentPage, pageSize,
                        buildingIdFilter, roomTypeIdFilter, yearFilter, managerIdFilter
                );
                Platform.runLater(() -> {
                    if (response != null && response.getFacilities() != null) {
                        facilityDataList.setAll(response.getFacilities());
                        totalPages = response.getTotalPages();
                        totalElements = response.getTotalElements();
                        currentPage = response.getCurrentPageNumber();
                    } else {
                        facilityDataList.clear();
                        totalPages = 0;
                        totalElements = 0;
                    }

                    if (facilityDataList.isEmpty() && currentPage == 0 && totalElements == 0) {
                        facilityTable.setPlaceholder(new Label("Không có dữ liệu phù hợp với bộ lọc."));
                    } else if (facilityDataList.isEmpty() && currentPage > 0) {
                        facilityTable.setPlaceholder(new Label("Không có dữ liệu cho trang này."));
                        if(this.currentPage > 0) {
                            this.currentPage--;
                            loadFacilitiesData();
                            return;
                        }
                    } else {
                        facilityTable.setPlaceholder(null);
                    }
                    updatePaginationControls();
                });

            } catch (IOException e) {
                final String errorMessage = e.getMessage();
                Platform.runLater(() -> {
                    showErrorAlert("Lỗi Tải Dữ Liệu", "Không thể tải dữ liệu: " + errorMessage);
                    facilityTable.setPlaceholder(new Label("Lỗi tải dữ liệu."));
                    totalPages = 0; totalElements = 0; updatePaginationControls();
                });
                e.printStackTrace();
            } catch (Exception e) {
                final String generalErrorMessage = e.getMessage();
                Platform.runLater(() -> {
                    showErrorAlert("Lỗi Không Xác Định", "Đã xảy ra lỗi không mong muốn: " + generalErrorMessage);
                    facilityTable.setPlaceholder(new Label("Lỗi không xác định."));
                    totalPages = 0; totalElements = 0; updatePaginationControls();
                });
                e.printStackTrace();
            }
        }).start();
    }


    @FXML
    private void handlePrevPage(ActionEvent event) {
        if (currentPage > 0) {
            currentPage--;
            loadFacilitiesData();
        }
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadFacilitiesData();
        }
    }

    private void updatePaginationControls() {
        Platform.runLater(() -> {
            if (pageInfoLabel != null) {
                if (totalElements == 0) {
                    pageInfoLabel.setText("Không có dữ liệu");
                } else if (totalPages > 0) {
                    pageInfoLabel.setText("Trang " + (currentPage + 1) + " / " + totalPages);
                } else {
                    pageInfoLabel.setText("Trang " + (currentPage + 1));
                }
            }
            if (prevPageButton != null) {
                prevPageButton.setDisable(currentPage == 0 || totalElements == 0);
            }
            if (nextPageButton != null) {
                nextPageButton.setDisable(currentPage >= totalPages - 1 || totalElements == 0);
            }
            if (rowsPerPageComboBox != null) {
                rowsPerPageComboBox.setDisable(totalElements == 0);
            }
        });
    }

    // --- CẬP NHẬT PHƯƠNG THỨC onEdit ---
    @Override
    public void onEdit(Facility facility) {
        System.out.println("FacilityController: Edit action for facility ID: " + facility.getId() + ", Name: " + facility.getName());
        try {
            // Đường dẫn đến file FXML của dialog chỉnh sửa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/EditFacilityDialog.fxml"));
            Parent parent = loader.load();

            // Lấy controller của dialog chỉnh sửa
            EditFacilityController editController = loader.getController();
            // Truyền đối tượng Facility hiện tại và danh sách (nếu EditFacilityController cần cập nhật lại danh sách)
            editController.setFacilityToEdit(facility); // Đổi tên phương thức trong EditFacilityController nếu cần
            // editController.setFacilityObservableList(facilityDataList); // Nếu dialog cần sửa trực tiếp list

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chỉnh sửa thông tin phòng: " + facility.getName());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Window ownerWindow = getWindow();
            if (ownerWindow != null) {
                dialogStage.initOwner(ownerWindow);
            }
            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);

            // Hiển thị dialog và chờ cho đến khi nó đóng
            dialogStage.showAndWait();

            // Sau khi dialog đóng (có thể đã lưu hoặc hủy), tải lại dữ liệu cho TableView
            // để phản ánh bất kỳ thay đổi nào.
            System.out.println("EditFacilityDialog closed. Reloading data in FacilityController.");
            loadFacilitiesData();

        } catch (IOException e) {
            System.err.println("Lỗi tải FXML cho EditFacilityDialog: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Lỗi Giao Diện", "Không thể mở giao diện chỉnh sửa phòng.\nChi tiết: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi không mong muốn khi mở dialog sửa: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Lỗi Không Xác Định", "Đã xảy ra lỗi không mong muốn.\nChi tiết: " + e.getMessage());
        }
    }

    @Override
    public void onDelete(Facility facility) {
        if (facility == null) {
            showErrorAlert("Lỗi Thao Tác", "Không có cơ sở vật chất nào được chọn để xóa.");
            return;
        }
        String facilityId = facility.getId();
        if (facilityId == null || facilityId.trim().isEmpty()) {
            showErrorAlert("Lỗi Dữ Liệu", "Không thể xóa: ID cơ sở vật chất không hợp lệ.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận Xóa");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa '" + facility.getName() + "' (ID: " + facilityId + ")?");
        confirmAlert.setContentText("Hành động này không thể hoàn tác.");
        Window ownerWindow = getWindow();
        if (ownerWindow != null) confirmAlert.initOwner(ownerWindow);

        confirmAlert.showAndWait().ifPresent(buttonResponse -> {
            if (buttonResponse == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        boolean deleted = roomService.deleteFacilityById(facilityId);
                        if (deleted) {
                            Platform.runLater(() -> {
                                // Tải lại dữ liệu để cập nhật bảng sau khi xóa thành công
                                loadFacilitiesData();
                                showInfoAlert("Thành công", "Đã xóa phòng '" + facility.getName() + "' thành công.");
                            });
                        } else {
                            Platform.runLater(() -> showErrorAlert("Xóa Thất Bại", "Không thể xóa '" + facility.getName() + "' từ server."));
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> showErrorAlert("Lỗi Xóa", "Lỗi mạng hoặc API khi xóa: " + e.getMessage()));
                        e.printStackTrace();
                    } catch (Exception e) {
                        Platform.runLater(() -> showErrorAlert("Lỗi Không Xác Định", "Lỗi không mong muốn khi xóa: " + e.getMessage()));
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }

    @FXML
    void handleAddFacility(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/addRoom.fxml"));
            Parent addRoomRoot = loader.load();
            // addRoomController addCtrl = loader.getController(); // Không cần nếu không tương tác sâu

            Stage addRoomStage = new Stage();
            addRoomStage.setTitle("Thêm Cơ Sở Vật Chất Mới");
            addRoomStage.setScene(new Scene(addRoomRoot));
            addRoomStage.initModality(Modality.APPLICATION_MODAL);
            Window ownerWindow = getWindow();
            if (ownerWindow != null) addRoomStage.initOwner(ownerWindow);
            addRoomStage.showAndWait();

            currentPage = 0;
            loadFacilitiesData();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Lỗi Giao Diện", "Không thể mở cửa sổ thêm. Chi tiết: " + e.getMessage());
        }
    }

    @FXML
    void handleExportToPdf(ActionEvent event) {
        if (facilityDataList.isEmpty() && totalElements == 0) {
            showInfoAlert("Không có dữ liệu", "Bảng trống, không có gì để xuất ra PDF.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu File PDF");
        fileChooser.setInitialFileName("DanhSachCoSoVatChat_" + java.time.LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        Window stage = getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            new Thread(() -> createPdf(file.getAbsolutePath())).start();
        }
    }

    private void createPdf(String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             PdfWriter writer = new PdfWriter(fos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Danh Sách Cơ Sở Vật Chất").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Tạo ngày: " + LocalDateTime.now().format(pdfTimestampFormatter)).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            if (facilityDataList.isEmpty()) {
                document.add(new Paragraph("Không có dữ liệu.").setTextAlignment(TextAlignment.CENTER));
            } else {
                // Cần 6 cột: Tên, Mô tả, Trạng thái, Ngày tạo, Ngày cập nhật, Quản lý
                float[] columnWidths = {2.5f, 3f, 1f, 1.5f, 1.5f, 2f};
                Table pdfTable = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
                addPdfHeaderCell(pdfTable, "Tên/Tòa nhà");
                addPdfHeaderCell(pdfTable, "Mô tả");
                addPdfHeaderCell(pdfTable, "Trạng thái");
                addPdfHeaderCell(pdfTable, "Ngày tạo");
                addPdfHeaderCell(pdfTable, "Ngày cập nhật");
                addPdfHeaderCell(pdfTable, "Quản lý");

                for (Facility facility : facilityDataList) {
                    if (facility == null) continue;
                    pdfTable.addCell(getStringOrEmpty(facility.getName()));
                    pdfTable.addCell(getStringOrEmpty(facility.getDescription()));
                    pdfTable.addCell(getStringOrEmpty(facility.getStatus()));
                    pdfTable.addCell(getStringOrEmpty(facility.getCreatedAt()));
                    pdfTable.addCell(getStringOrEmpty(facility.getUpdatedAt()));
                    pdfTable.addCell(getStringOrEmpty(facility.getNameFacilityManager()));
                }
                document.add(pdfTable);
            }
            Platform.runLater(() -> showInfoAlert("Xuất Thành Công", "Dữ liệu đã xuất: " + filePath));
        } catch (Exception e) {
            Platform.runLater(() -> showErrorAlert("Xuất Thất Bại", "Lỗi khi xuất PDF: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private void addPdfHeaderCell(Table table, String text) {
        table.addHeaderCell(new Paragraph(text).setBold().setFontSize(10));
    }
    private void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }
    private void showInfoAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            TextArea textArea = new TextArea(message);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            alert.getDialogPane().setContent(textArea);
            alert.setResizable(true);
            Window owner = getWindow();
            if (owner != null) alert.initOwner(owner);
            alert.showAndWait();
        } else {
            Platform.runLater(() -> showAlert(alertType, title, message));
        }
    }
    private String getStringOrEmpty(String str) {
        return str != null ? str : "";
    }
    private Window getWindow() {
        try {
            if (facilityTable != null && facilityTable.getScene() != null && facilityTable.getScene().getWindow() != null) {
                return facilityTable.getScene().getWindow();
            }
            if (btnExport != null && btnExport.getScene() != null && btnExport.getScene().getWindow() != null) {
                return btnExport.getScene().getWindow();
            }
            if (btnAddFacility != null && btnAddFacility.getScene() != null && btnAddFacility.getScene().getWindow() != null) {
                return btnAddFacility.getScene().getWindow();
            }
        } catch (Exception e) { /* ... */ }
        return null;
    }
}