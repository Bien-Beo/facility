package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;

public class InfoRoomController {
    @FXML
    private Text building;

    @FXML
    private Button buttonAddBooking;

    @FXML
    private Button buttonBack;

    @FXML
    private Text capacity;

    @FXML
    private Text dateCreate;

    @FXML
    private Text dateUpdate;

    @FXML
    private Label description;

    @FXML
    private ImageView img;

    @FXML
    private Label name;

    @FXML
    private Text nameFManager;

    @FXML
    private Text status;

    @FXML
    private Text typeRoom;

    @FXML
    private Label info;

    private String sourceView; // Lưu trang nguồn

    public void setSourceView(String source) {
        this.sourceView = source;
    }

    @FXML
    public void initialize() {
        buttonBack.setOnAction(event -> goBack());

    }



    private void goBack() {
        try {
            // Tìm mainCenter
            AnchorPane mainCenter = (AnchorPane) buttonBack.getScene().lookup("#mainCenter");
            if (mainCenter == null) return;

            // Xác định đường dẫn trang cần quay về
            String viewPath = "/com/utc2/facilityui/view/" + 
                            (sourceView != null ? sourceView : "rooms") + ".fxml";
            
            System.out.println("Quay về trang: " + viewPath); // Debug log

            // Load trang tương ứng
            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            AnchorPane page = loader.load();

            // Thay thế nội dung hiện tại
            mainCenter.getChildren().clear();
            mainCenter.getChildren().add(page);

            // Set anchors
            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}