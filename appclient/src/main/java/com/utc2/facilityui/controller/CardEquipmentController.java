package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardInfo;
import com.utc2.facilityui.model.CardInfoEquipment;
import com.utc2.facilityui.model.InfoEquipment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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
    private ImageView roomImg;

    @FXML
    private Button showInfo;
    private String currentView;
    @FXML
    public void initialize() {
        showInfo.setOnAction(event -> showInfoEquipment());
    }
    public void setData(CardInfoEquipment cardEquipment) {
        Image image = new Image(getClass().getResourceAsStream(cardEquipment.getImgSrc()));
        roomImg.setImage(image);
        nameEquipment.setText(cardEquipment.getNameCard());
        roleManager.setText(cardEquipment.getRoleManager());
        nameManager.setText(cardEquipment.getNameManager());
    }

    public void setCurrentView(String view) {
        this.currentView = view;
    }
    private void showInfoEquipment() {
        try {
            AnchorPane mainCenter = (AnchorPane) card.getScene().lookup("#mainCenter");
            if (mainCenter == null) {
                System.err.println("Không tìm thấy mainCenter");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/infoEquipment.fxml"));
            AnchorPane infoEquipment = loader.load();
            InfoEquipmentController infoEquipmentController = loader.getController();

            // Chỉ set trang nguồn
            infoEquipmentController.setSourceView(currentView);

            // Xóa nội dung cũ và thêm infoRoom
            mainCenter.getChildren().clear();
            mainCenter.getChildren().add(infoEquipment);

            // Set anchors
            AnchorPane.setTopAnchor(infoEquipment, 0.0);
            AnchorPane.setBottomAnchor(infoEquipment, 0.0);
            AnchorPane.setLeftAnchor(infoEquipment, 0.0);
            AnchorPane.setRightAnchor(infoEquipment, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi hiển thị thông tin thiết bị");
        }
    }

}
