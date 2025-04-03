package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardInfo;
import com.utc2.facilityui.model.InfoRoom;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class Controller {
    @FXML
    private Label nameManager;

    @FXML
    private Label roleManager;

    @FXML
    private ImageView roomImg;

    @FXML
    private Label nameRoom;

    @FXML
    private Button showInfo;
    @FXML
    private AnchorPane card;

    private String currentView; // Để lưu tên view hiện tại

    @FXML
    public void initialize() {
        showInfo.setOnAction(event -> showInfoRoom());
    }

    public void setData(CardInfo cardinfo) {
        Image image = new Image(getClass().getResourceAsStream(cardinfo.getImgSrc()));
        roomImg.setImage(image);
        nameRoom.setText(cardinfo.getNameCard());
        roleManager.setText(cardinfo.getRoleManager());
        nameManager.setText(cardinfo.getNameManager());
    }

    public void setCurrentView(String view) {
        this.currentView = view;
    }

    private void showInfoRoom() {
        try {
            AnchorPane mainCenter = (AnchorPane) card.getScene().lookup("#mainCenter");
            if (mainCenter == null) {
                System.err.println("Không tìm thấy mainCenter");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/infoRoom.fxml"));
            AnchorPane infoRoom = loader.load();
            InfoRoomController infoRoomController = loader.getController();

            // Chỉ set trang nguồn
            infoRoomController.setSourceView(currentView);

            // Xóa nội dung cũ và thêm infoRoom
            mainCenter.getChildren().clear();
            mainCenter.getChildren().add(infoRoom);

            // Set anchors
            AnchorPane.setTopAnchor(infoRoom, 0.0);
            AnchorPane.setBottomAnchor(infoRoom, 0.0);
            AnchorPane.setLeftAnchor(infoRoom, 0.0);
            AnchorPane.setRightAnchor(infoRoom, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi hiển thị thông tin phòng");
        }
    }
}