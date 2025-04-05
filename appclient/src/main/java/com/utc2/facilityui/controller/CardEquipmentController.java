package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardInfo;
import com.utc2.facilityui.model.CardInfoEquipment;
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
//    @FXML
//    public void initialize() {
//        showInfo.setOnAction(event -> showInfoRoom());
//    }
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


}
