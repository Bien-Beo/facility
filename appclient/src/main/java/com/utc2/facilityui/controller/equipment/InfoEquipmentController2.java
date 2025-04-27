
package com.utc2.facilityui.controller.equipment;

import com.utc2.facilityui.controller.booking.AddBookingController;
import com.utc2.facilityui.model.Equipment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class InfoEquipmentController2 implements Initializable {

    @FXML private Button bntReport;
    @FXML private Button buttonAddBooking;
    @FXML private Button buttonBack;
    @FXML private Text creationDate;
    @FXML private Label description;
    @FXML private Text equipmentTypeName;
    @FXML private ImageView img;
    @FXML private Label name;
    @FXML private Text nameFManager; // Field này sẽ không có dữ liệu từ API hiện tại
    @FXML private Text status;
    @FXML private Text updateDate;

    // --- Biến thành viên ---
    private String sourceView = "equipments";
    private Equipment currentEquipmentData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonBack.setOnAction(event -> goBack());
        buttonAddBooking.setOnAction(event -> showAddBookingDialog());
        bntReport.setOnAction(event -> showReportEquipmentDialog());
        clearDetails();
    }

    public void setSourceView(String source) {
        if (source != null && !source.trim().isEmpty()) {
            this.sourceView = source.trim();
        }
    }

    /**
     * Nhận dữ liệu Equipment và hiển thị lên các thành phần UI (ĐÃ CẬP NHẬT).
     * @param equipmentData Dữ liệu chi tiết của thiết bị.
     */
    public void loadEquipmentDetails(Equipment equipmentData) {
        this.currentEquipmentData = equipmentData;

        if (equipmentData == null) {
            System.err.println("InfoEquipmentController nhận equipmentData null!");
            clearDetails();
            name.setText("Không có dữ liệu");
            buttonAddBooking.setDisable(true);
            bntReport.setDisable(true);
            return;
        }

        buttonAddBooking.setDisable(false);
        bntReport.setDisable(false);

        // --- Cập nhật UI với dữ liệu từ equipmentData (ĐÃ CẬP NHẬT) ---
        name.setText(getValueOrDefault(equipmentData.getModelName(), "N/A")); // Dùng modelName
        description.setText(getValueOrDefault(equipmentData.getNotes(), "")); // Dùng notes làm description
        equipmentTypeName.setText(getValueOrDefault(equipmentData.getTypeName(), "N/A")); // Dùng typeName
        status.setText(getValueOrDefault(equipmentData.getStatus(), "N/A"));
        nameFManager.setText("N/A"); // <<<--- Không có dữ liệu từ API này
        creationDate.setText(formatDate(equipmentData.getCreatedAt()));
        updateDate.setText(formatDate(equipmentData.getUpdatedAt()));

        // Hiển thị thêm thông tin nếu có UI tương ứng
        // Ví dụ: serialNumberText.setText(getValueOrDefault(equipmentData.getSerialNumber(), "N/A"));
        //        purchaseDateText.setText(formatDate(equipmentData.getPurchaseDate()));
        //        warrantyDateText.setText(formatDate(equipmentData.getWarrantyExpiryDate()));
        //        defaultRoomText.setText(getValueOrDefault(equipmentData.getDefaultRoomName(), "N/A"));

        loadImage(equipmentData.getImgModel()); // Dùng imgModel
    }

    // ... (Các phương thức clearDetails, getValueOrDefault, formatDate, loadImage, getDefaultImagePath, closeStream, goBack, setupDialogStage giữ nguyên như trước) ...
    private void clearDetails() {
        name.setText("");
        description.setText("");
        img.setImage(null);
        equipmentTypeName.setText("");
        status.setText("");
        nameFManager.setText("");
        creationDate.setText("");
        updateDate.setText("");
    }

    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    private String formatDate(String dateString) {
        if (dateString == null) return "N/A";
        try {
            return dateString.substring(0, 10); // Lấy YYYY-MM-DD
        } catch (Exception e) {
            return dateString;
        }
    }

    private void loadImage(String imagePath) {
        Image image = null;
        String effectivePath = (imagePath != null && !imagePath.trim().isEmpty()) ? imagePath : getDefaultImagePath();
        InputStream imageStream = null;
        try {
            if (!effectivePath.startsWith("/") && !effectivePath.startsWith("http")) {
                // Điều chỉnh đường dẫn cho phù hợp với cấu trúc resource và giá trị từ API
                effectivePath = "/com/utc2/facilityui/" + effectivePath; // Ví dụ: /com/utc2/facilityui/images/models/epson_ebs41.jpg
            }
            imageStream = getClass().getResourceAsStream(effectivePath);
            if (imageStream != null) {
                image = new Image(imageStream);
                if (image.isError()) {
                    System.err.println("Lỗi tạo Image: " + effectivePath + " - " + image.getException());
                    image = null;
                }
            } else {
                System.err.println("Không tìm thấy resource ảnh: " + effectivePath);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh: " + effectivePath + " - " + e.getMessage());
        } finally {
            closeStream(imageStream);
        }

        if (image == null) {
            InputStream defaultStream = null;
            try {
                defaultStream = getClass().getResourceAsStream(getDefaultImagePath());
                if (defaultStream != null) image = new Image(defaultStream);
                else System.err.println("Không tìm thấy ảnh mặc định: " + getDefaultImagePath());
            } catch (Exception e) {
                System.err.println("Lỗi tải ảnh mặc định: " + e.getMessage());
            } finally {
                closeStream(defaultStream);
            }
        }
        img.setImage(image);
    }

    private void closeStream(InputStream stream) {
        if (stream != null) {
            try { stream.close(); } catch (IOException e) { /* ignore */ }
        }
    }

    private String getDefaultImagePath() {
        return "/com/utc2/facilityui/images/default_equipment.png";
    }

    private void goBack() {
        try {
            if (buttonBack == null || buttonBack.getScene() == null) return;
            AnchorPane mainCenter = (AnchorPane) buttonBack.getScene().lookup("#mainCenter");
            if (mainCenter == null) return;
            String viewPath = "/com/utc2/facilityui/view/" + sourceView + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            AnchorPane page = loader.load();
            mainCenter.getChildren().setAll(page); // Dùng setAll để thay thế
            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật hàm dialog để dùng currentEquipmentData
    private void showAddBookingDialog() {
        System.out.println("Chức năng Add Booking cho Equipment cần được xem xét lại.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/addBooking.fxml"));
            AnchorPane addBookingPane = loader.load();
            Scene scene = new Scene(addBookingPane);
            Stage stage = new Stage();
            stage.setTitle("Add Booking");
            stage.setScene(scene);
            setupDialogStage(stage);

            AddBookingController controller = loader.getController();
            if(controller == null) return;

            if (this.currentEquipmentData != null) {
                controller.getName().setText(this.currentEquipmentData.getModelName()); // Dùng modelName
            } else {
                controller.getName().setText("Thiết bị không xác định");
            }
            controller.getBntCancel().setOnAction(e -> stage.close());
            controller.getBntAddBooking().setOnAction(e -> stage.close()); // Chỉ đóng lại
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật hàm dialog để dùng currentEquipmentData
    private void showReportEquipmentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/reportEquipment.fxml"));
            AnchorPane reportPane = loader.load();
            Scene scene = new Scene(reportPane);
            Stage stage = new Stage();
            stage.setTitle("Báo cáo sự cố Thiết bị");
            stage.setScene(scene);
            setupDialogStage(stage);

            ReportEquipmentController controller = loader.getController();
            if(controller == null) return;

            if (this.currentEquipmentData != null) {
                controller.getName().setText(this.currentEquipmentData.getModelName()); // Dùng modelName
                // controller.setTargetId(this.currentEquipmentData.getId()); // Truyền ID nếu cần
            } else {
                controller.getName().setText("Thiết bị không xác định");
            }
            controller.getBntCancel().setOnAction(e -> stage.close());
            controller.getBntAdd().setOnAction(e -> {
                controller.handleAdd();
                stage.close();
            });
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupDialogStage(Stage stage) {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/com/utc2/facilityui/images/logo-icon-UTC2.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
                iconStream.close();
            }
        } catch (Exception e) { /* ignore */ }
        stage.initModality(Modality.APPLICATION_MODAL);
    }
}
