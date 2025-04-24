package com.utc2.facilityui.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.model.User;
import com.utc2.facilityui.response.ApiResponse;
import com.utc2.facilityui.service.UserServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
//
public class HomeController {
    @FXML
    private ImageView imgAvatar;
    @FXML
    private Label lbUsername;
    @FXML
    private Label lbUserId;
    @FXML
    private AnchorPane nav;
    @FXML
    private AnchorPane mainCenter;
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        loadDefaultPage();
    }



    private void loadDefaultPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/rooms.fxml"));
            AnchorPane roomsPage = loader.load();
            mainCenter.getChildren().setAll(roomsPage);

            // Set anchors
            AnchorPane.setTopAnchor(roomsPage, 0.0);
            AnchorPane.setBottomAnchor(roomsPage, 0.0);
            AnchorPane.setLeftAnchor(roomsPage, 0.0);
            AnchorPane.setRightAnchor(roomsPage, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}