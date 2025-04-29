package com.utc2.facilityui.controller;

// JavaFX Imports
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
import javafx.stage.FileChooser; // Keep FileChooser

// Project Specific Imports
import com.utc2.facilityui.controller.room.addRoomController; // Keep if needed
import com.utc2.facilityui.model.Facility;
import com.utc2.facilityui.model.OperationsTableCell;
import com.utc2.facilityui.model.OperationsTableCellFactory;
import com.utc2.facilityui.service.RoomService;

// iText PDF Imports (Added)
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

// Java Standard Imports
import java.io.File; // Keep File
import java.io.FileNotFoundException; // Added
import java.io.FileOutputStream; // Added
import java.io.IOException; // Keep IOException
import java.net.URL;
import java.time.LocalDateTime; // Added
import java.time.format.DateTimeFormatter; // Added
import java.util.ResourceBundle;
import java.util.Objects; // Added (if not already present)


/**
 * Controller for the Facility Management screen.
 * Includes functionality for displaying, adding, editing, deleting,
 * and exporting facility data to PDF.
 */
public class FacilityController implements Initializable, OperationsTableCell.OperationsEventHandler<Facility> {

    // --- FXML Components ---
    @FXML private TableView<Facility> facilityTable;
    @FXML private TableColumn<Facility, String> nameColumn;
    @FXML private TableColumn<Facility, String> descriptionColumn;
    @FXML private TableColumn<Facility, String> statusColumn;
    @FXML private TableColumn<Facility, String> createdAtColumn;
    @FXML private TableColumn<Facility, String> updatedAtColumn;
    @FXML private TableColumn<Facility, String> deletedAtColumn; // This column might not be needed for export
    @FXML private TableColumn<Facility, String> managerColumn; // Column to display manager info
    @FXML private TableColumn<Facility, Void> operationsColumn;
    // Ensure the Export PDF button in FXML has this fx:id and onAction="#handleExportToPdf"
    @FXML private Button btnExport;

    // --- Data and Services ---
    private ObservableList<Facility> facilityDataList = FXCollections.observableArrayList();
    private RoomService roomService;

    // --- Formatters (Added) ---
    // Used for formatting the timestamp in the generated PDF file name/content
    private final DateTimeFormatter pdfTimestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- Initialize, Setup Columns, Load Data ---
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("FacilityController initialize() called!");
        this.roomService = new RoomService();
        setupTableColumns();
        facilityTable.setItems(facilityDataList);
        loadFacilitiesData();
    }

    private void setupTableColumns() {
        System.out.println("setupTableColumns() called!");
        // Bind columns to properties in the Facility model
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Assumes Facility has getName()
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); // Assumes Facility has getDescription()
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status")); // Assumes Facility has getStatus()
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt")); // Assumes Facility has getCreatedAt() returning formatted String
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt")); // Assumes Facility has getUpdatedAt() returning formatted String
        deletedAtColumn.setCellValueFactory(new PropertyValueFactory<>("deletedAt")); // Assumes Facility has getDeletedAt() returning formatted String
        // Bind to the correct property/getter for manager info
        managerColumn.setCellValueFactory(new PropertyValueFactory<>("facilityManagerId")); // Use the correct property name from Facility model

        // Cell factory for description to wrap text
        descriptionColumn.setCellFactory(tc -> {
            TableCell<Facility, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionColumn.widthProperty().subtract(15)); // Adjust padding if needed
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        // Cell factory for Edit/Delete buttons
        operationsColumn.setCellFactory(new OperationsTableCellFactory<>(this));
        facilityTable.setPlaceholder(new Label("Loading data..."));
    }

    private void loadFacilitiesData() {
        System.out.println("loadFacilitiesData() called!");
        facilityTable.setPlaceholder(new Label("Loading data..."));
        new Thread(() -> { // Load data in a background thread
            try {
                // Fetch data using the service
                final ObservableList<Facility> fetchedFacilities = roomService.getDashboardFacilities();
                // Update the UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    // Ensure fetchedFacilities is not null before setting
                    facilityDataList.setAll(fetchedFacilities != null ? fetchedFacilities : FXCollections.observableArrayList());
                    facilityTable.setPlaceholder(new Label(facilityDataList.isEmpty() ? "No facility data available." : null));
                    System.out.println("Facility Table updated with " + facilityDataList.size() + " items.");
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showErrorAlert("Load Data Error", "Could not load facility data: " + e.getMessage());
                    facilityTable.setPlaceholder(new Label("Error loading data. Please try again."));
                });
                System.err.println("Error loading facilities: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) { // Catch other unexpected errors
                Platform.runLater(() -> {
                    showErrorAlert("Unknown Error", "An unknown error occurred while loading data: " + e.getMessage());
                    facilityTable.setPlaceholder(new Label("Unknown error."));
                });
                System.err.println("Unexpected error loading facilities: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // --- Edit, Delete, Add Facility Handlers ---
    @Override
    public void onEdit(Facility facility) {
        System.out.println("onEdit() called in FacilityController!");
        if (facility == null) {
            showErrorAlert("Operation Error", "No facility selected for editing.");
            return;
        }
        System.out.println("FacilityController: Edit action for facility ID: " + facility.getId() + ", Name: " + facility.getName());
        try {
            // Load the edit dialog FXML
            String fxmlPath = "/com/utc2/facilityui/view/EditFacilityDialog.fxml"; // Ensure path is correct
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent parent = loader.load(); // Load the FXML, this also initializes the controller

            // Get the controller of the dialog
            EditFacilityController editController = loader.getController();
            if (editController == null) {
                showErrorAlert("Controller Error", "Could not initialize edit dialog controller.");
                return;
            }
            // Pass the selected facility and the list to the dialog controller
            editController.setFacility(facility);
            editController.setFacilityList(this.facilityDataList);

            // Create and show the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Facility: " + facility.getName());
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with main window
            Window ownerWindow = getWindow(); // Use helper method to get window
            if (ownerWindow != null) { dialogStage.initOwner(ownerWindow); }
            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);
            dialogStage.showAndWait(); // Wait for the dialog to close

            System.out.println("FacilityController: Edit dialog closed.");
            // Optionally reload data if needed after edit
            // loadFacilitiesData();

        } catch (IOException | NullPointerException e) { // Catch potential FXML loading errors
            System.err.println("Error loading or showing EditFacilityDialog: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Load UI Error", "Could not load edit interface. Details: " + e.getMessage());
        } catch (Exception e) { // Catch other unexpected errors
            System.err.println("Unexpected error in onEdit: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Unexpected Error", "An unexpected error occurred. Details: " + e.getMessage());
        }
    }

    @Override
    public void onDelete(Facility facility) {
        System.out.println("onDelete() called!");
        if (facility == null) {
            showErrorAlert("Operation Error", "No facility selected for deletion.");
            return;
        }
        String facilityId = facility.getId();
        if (facilityId == null || facilityId.trim().isEmpty()) {
            showErrorAlert("Data Error", "Cannot delete facility due to missing ID.");
            return;
        }
        System.out.println("Controller: Delete action for facility ID: " + facilityId + ", Name: " + facility.getName());

        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete facility '" + facility.getName() + "' (ID: " + facilityId + ")?");
        confirmAlert.setContentText("This action might not be reversible.");
        Window ownerWindow = getWindow(); // Use helper method
        if (ownerWindow != null) confirmAlert.initOwner(ownerWindow);

        // Process the user's response
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Perform deletion in a background thread
                new Thread(() -> {
                    try {
                        System.out.println("Attempting to delete facility with ID: " + facilityId);
                        boolean deleted = roomService.deleteFacilityById(facilityId);
                        if (deleted) {
                            // Update UI on JavaFX Application Thread
                            Platform.runLater(() -> {
                                facilityDataList.remove(facility); // Remove from the list
                                System.out.println("Facility '" + facility.getName() + "' removed from table.");
                                if (facilityDataList.isEmpty()) { // Update placeholder if list becomes empty
                                    facilityTable.setPlaceholder(new Label("No facility data available."));
                                }
                            });
                        } // else: Handle case where service returns false (optional)
                    } catch (IOException e) { // Handle network/API errors
                        Platform.runLater(() -> showErrorAlert("Deletion Error", "Failed to delete facility '" + facility.getName() + "'. Error: " + e.getMessage()));
                        System.err.println("Error deleting facility (IOException): " + e.getMessage());
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) { // Handle invalid arguments
                        Platform.runLater(() -> showErrorAlert("Data Error", "Deletion failed: " + e.getMessage()));
                        System.err.println("Error deleting facility (IllegalArgumentException): " + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) { // Catch other unexpected errors
                        Platform.runLater(() -> showErrorAlert("Unknown Error", "An unexpected error occurred while deleting facility '" + facility.getName() + "': " + e.getMessage()));
                        System.err.println("Error deleting facility (Exception): " + e.getMessage());
                        e.printStackTrace();
                    }
                }).start();
            } else {
                System.out.println("Delete cancelled by user for facility: " + facility.getName());
            }
        });
    }

    @FXML
    void handleAddFacility(ActionEvent event) {
        System.out.println("handleAddFacility() called!");
        try {
            // Load the add facility dialog
            String fxmlPath = "/com/utc2/facilityui/view/addRoom.fxml"; // Ensure path is correct
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent addRoomRoot = loader.load();
            // addRoomController addRoomCtrl = loader.getController(); // Get controller if needed

            // Create and show the dialog stage
            Stage addRoomStage = new Stage();
            addRoomStage.setTitle("Add New Facility");
            addRoomStage.setScene(new Scene(addRoomRoot));
            addRoomStage.initModality(Modality.APPLICATION_MODAL);
            Window ownerWindow = getWindow(); // Use helper method
            if (ownerWindow != null) addRoomStage.initOwner(ownerWindow);
            addRoomStage.showAndWait(); // Wait for dialog to close

            System.out.println("Add dialog closed. Reloading data...");
            loadFacilitiesData(); // Reload data to see the new entry

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            showErrorAlert("Load FXML Error", "Could not open add window: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Unknown Error", "Unexpected error opening add window: " + e.getMessage());
        }
    }


    // --- PDF EXPORT FUNCTIONALITY (Replaces CSV) ---

    /**
     * Handles the action event when the Export PDF button is clicked.
     * Opens a FileChooser for the user to select a save location and initiates PDF creation.
     * @param event The action event.
     */
    @FXML
    void handleExportToPdf(ActionEvent event) { // Renamed method
        System.out.println("handleExportToPdf() called!");

        if (facilityDataList.isEmpty()) {
            showInfoAlert("No Data", "The table is empty, nothing to export to PDF.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.setInitialFileName("FacilityList_" + java.time.LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));

        Window stage = getWindow(); // Use helper method to get window

        File file = fileChooser.showSaveDialog(stage);

        // If the user selected a file, proceed with PDF creation
        if (file != null) {
            String filePath = file.getAbsolutePath();
            System.out.println("Exporting data to PDF: " + filePath);
            // Run PDF creation in a background thread
            new Thread(() -> createPdf(filePath)).start();
        } else {
            System.out.println("PDF Export cancelled by user.");
        }
    }

    /**
     * Creates a PDF document containing the facility data from the table.
     * @param filePath The full path where the PDF file will be saved.
     */
    private void createPdf(String filePath) {
        System.out.println("Starting PDF creation...");

        // Use try-with-resources for automatic resource management
        try (FileOutputStream fos = new FileOutputStream(filePath);
             PdfWriter writer = new PdfWriter(fos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Add document title and generation timestamp
            document.add(new Paragraph("Facility List Export")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            // Use the formatter for the timestamp
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(pdfTimestampFormatter))
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n")); // Add spacing

            // Define table columns and widths (adjust as needed)
            // Match the columns visible in the TableView (excluding Operations, Deleted At)
            float[] columnWidths = {2f, 3f, 1f, 1.5f, 1.5f, 1.5f}; // Example: Name, Desc, Status, Created, Updated, Manager
            Table pdfTable = new Table(UnitValue.createPercentArray(columnWidths))
                    .useAllAvailableWidth();

            // Add table header row
            addPdfHeaderCell(pdfTable, "Name/Building");
            addPdfHeaderCell(pdfTable, "Description");
            addPdfHeaderCell(pdfTable, "Status");
            addPdfHeaderCell(pdfTable, "Created At");
            addPdfHeaderCell(pdfTable, "Updated At");
            addPdfHeaderCell(pdfTable, "Manager"); // Header for manager info

            // Add data rows from the facilityDataList (the data currently in memory)
            if (facilityDataList.isEmpty()) {
                document.add(new Paragraph("No facility data to export.").setTextAlignment(TextAlignment.CENTER));
            } else {
                for (Facility facility : facilityDataList) {
                    if (facility == null) continue; // Skip null entries
                    pdfTable.addCell(getStringOrEmpty(facility.getName()));
                    pdfTable.addCell(getStringOrEmpty(facility.getDescription()));
                    pdfTable.addCell(getStringOrEmpty(facility.getStatus()));
                    // Assuming createdAt/updatedAt in Facility model are already formatted Strings
                    pdfTable.addCell(getStringOrEmpty(facility.getCreatedAt()));
                    pdfTable.addCell(getStringOrEmpty(facility.getUpdatedAt()));
                    // Use the correct getter for manager info
                    pdfTable.addCell(getStringOrEmpty(facility.getFacilityManagerId()));
                }
                document.add(pdfTable); // Add the completed table to the document
            }

            System.out.println("PDF created successfully at: " + filePath);
            // Show success message on the UI thread
            Platform.runLater(() -> showInfoAlert("Export Successful", "Data exported successfully to:\n" + filePath));

        } catch (FileNotFoundException e) {
            System.err.println("Error creating PDF file (file not found or permissions): " + e.getMessage());
            Platform.runLater(() -> showErrorAlert("Export Failed", "Could not create PDF file. Check permissions or path.\nError: " + e.getMessage()));
        } catch (IOException e) {
            System.err.println("Error writing to PDF file: " + e.getMessage());
            Platform.runLater(() -> showErrorAlert("Export Failed", "An error occurred while writing to the PDF file:\n" + e.getMessage()));
        } catch (Exception e) { // Catch any other unexpected errors during PDF creation
            System.err.println("Unexpected error creating PDF: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> showErrorAlert("Export Failed", "An unexpected error occurred during export:\n" + e.getMessage()));
        }
    }

    /**
     * Helper method to add a styled header cell to the PDF table.
     * @param table The iText Table object.
     * @param text The header text.
     */
    private void addPdfHeaderCell(Table table, String text) {
        table.addHeaderCell(new Paragraph(text).setBold().setFontSize(10));
    }


    // --- Utility Methods (Alerts, getStringOrEmpty, getWindow) ---

    /** Displays an error alert dialog. */
    private void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    /** Displays an information alert dialog. */
    private void showInfoAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    /** General helper method to show alerts on the UI thread. */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // Ensure UI updates happen on the JavaFX Application Thread
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            // Use TextArea for potentially long messages
            TextArea textArea = new TextArea(message);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            alert.getDialogPane().setContent(textArea);
            alert.setResizable(true);

            // Attempt to set the owner window
            Window owner = getWindow();
            if (owner != null) alert.initOwner(owner);

            alert.showAndWait();
        } else {
            Platform.runLater(() -> showAlert(alertType, title, message));
        }
    }

    /** Returns the input string or an empty string if the input is null. */
    private String getStringOrEmpty(String str) {
        return str != null ? str : "";
    }

    /** Helper method to get the current window, trying the button first, then the table. */
    private Window getWindow() {
        try {
            if (btnExport != null && btnExport.getScene() != null) {
                return btnExport.getScene().getWindow();
            } else if (facilityTable != null && facilityTable.getScene() != null) {
                return facilityTable.getScene().getWindow();
            }
        } catch (Exception e) {
            System.err.println("Could not reliably determine the window owner: " + e.getMessage());
        }
        return null; // Return null if window cannot be determined
    }

}
