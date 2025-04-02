package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardInfo;
import javafx.fxml.FXML;
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

    public void setData(CardInfo cardinfo) {
       Image image = new Image(getClass().getResourceAsStream(cardinfo.getImgSrc()));
        roomImg.setImage(image);
        nameRoom.setText(cardinfo.getNameCard());
        roleManager.setText(cardinfo.getRoleManager());
        nameManager.setText(cardinfo.getNameManager());
    }
}
