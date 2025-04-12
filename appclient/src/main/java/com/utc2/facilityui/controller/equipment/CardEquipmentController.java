package com.utc2.facilityui.controller.equipment;

import com.utc2.facilityui.model.CardInfoEquipment;
import com.utc2.facilityui.model.Equipment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStream;
//
public class CardEquipmentController {
    @FXML
    private AnchorPane card;

    @FXML
    private Label nameEquipment;

    @FXML
    private Label nameManager;

    @FXML
    private Label roleManager;

    @FXML
    private ImageView equipmentImg;

    @FXML
    private Button showInfo;
    private String currentView;
    private String sourceView = "equipments";
    private Equipment currentEquipment;
    public void setData(CardInfoEquipment cardInfo, Equipment equipment) {
        if (cardInfo == null || equipment == null) {
            System.err.println("CardEquipmentController nhận dữ liệu null");
            if (card != null) card.setVisible(false);
            return;
        }
        if (card != null) card.setVisible(true);
        this.currentEquipment = equipment; // Lưu lại equipment

        // Hiển thị dữ liệu từ cardInfo
        nameEquipment.setText(cardInfo.getNameEquip() != null ? cardInfo.getNameEquip() : "N/A");
        roleManager.setText(cardInfo.getRoleManager() != null ? cardInfo.getRoleManager() : "N/A");
        nameManager.setText(cardInfo.getNameManager() != null ? cardInfo.getNameManager() : "N/A");
        loadImage(cardInfo.getImgSrc());
    }

    public void setCurrentView(String view) {
        this.currentView = view;
    }
    @FXML
    public void initialize() {
        if(showInfo != null) {
                 showInfo.setOnAction(event -> handleCardClick(null)); // Truyền null hoặc tạo MouseEvent giả nếu cần
             }
    }

    // Hàm xử lý sự kiện click vào card
    @FXML
    private void handleCardClick(javafx.event.ActionEvent event) { // Đổi thành ActionEvent nếu gọi từ onAction
        System.out.println("Button clicked! Equipment: " + (currentEquipment != null ? currentEquipment.getModelName() : "null"));
        showInfoEquipment();
    }

    // Hàm tải ảnh (tương tự các controller khác)
    private void loadImage(String imagePath) {
        Image image = null;
        String effectivePath = (imagePath != null && !imagePath.trim().isEmpty()) ? imagePath : getDefaultImagePath();
        InputStream imageStream = null;
        try {
            if (!effectivePath.startsWith("/") && !effectivePath.startsWith("http")) {
                effectivePath = "/com/utc2/facilityui/images/equipment/" + effectivePath; // Điều chỉnh nếu cần
            }
            imageStream = getClass().getResourceAsStream(effectivePath);
            if (imageStream != null) {
                image = new Image(imageStream);
                if (image.isError()) image = null;
            }
        } catch (Exception e) { /* ignore */ }
        finally { if (imageStream != null) { try { imageStream.close(); } catch (IOException e) { /*ignore*/ } } }

        if (image == null) { // Thử ảnh mặc định
            InputStream defaultStream = null;
            try {
                defaultStream = getClass().getResourceAsStream(getDefaultImagePath());
                if (defaultStream != null) image = new Image(defaultStream);
            } catch (Exception e) { /* ignore */ }
            finally { if (defaultStream != null) { try { defaultStream.close(); } catch (IOException e) { /*ignore*/ } } }
        }
        equipmentImg.setImage(image);
    }

    private String getDefaultImagePath() {
        return "/com/utc2/facilityui/images/default_equipment.png"; // Ảnh mặc định
    }

    // Hàm điều hướng sang màn hình chi tiết
    private void showInfoEquipment() {
        if (this.currentEquipment == null) {
            System.err.println("Không có dữ liệu Equipment để hiển thị chi tiết.");
            return;
        }
        try {
            // Tìm mainCenter từ Scene của card
            if (card == null || card.getScene() == null) {
                System.err.println("Không thể tìm thấy Scene để tra cứu #mainCenter.");
                return;
            }
            AnchorPane mainCenter = (AnchorPane) card.getScene().lookup("#mainCenter");
            if (mainCenter == null) {
                System.err.println("Không tìm thấy #mainCenter trong Scene.");
                return;
            }

            // Load FXML của màn hình chi tiết
            // *** ĐẢM BẢO ĐÚNG ĐƯỜNG DẪN VÀ TÊN FILE FXML ***
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/infoEquipment.fxml"));
            AnchorPane infoEquipmentView = loader.load();

            // Lấy controller của màn hình chi tiết
            InfoEquipmentController infoController = loader.getController();
            if (infoController == null) {
                System.err.println("Không thể lấy InfoEquipmentController.");
                return;
            }

            // Truyền dữ liệu và view nguồn
            infoController.setSourceView(this.sourceView);
            infoController.loadEquipmentDetails(this.currentEquipment); // <<<--- GỌI HÀM TRUYỀN DATA

            // Hiển thị view mới
            mainCenter.getChildren().clear();
            mainCenter.getChildren().add(infoEquipmentView);
            AnchorPane.setTopAnchor(infoEquipmentView, 0.0);
            AnchorPane.setBottomAnchor(infoEquipmentView, 0.0);
            AnchorPane.setLeftAnchor(infoEquipmentView, 0.0);
            AnchorPane.setRightAnchor(infoEquipmentView, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi chuyển sang màn hình chi tiết thiết bị: " + e.getMessage());
        }
    }
}