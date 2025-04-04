package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        buttonAddBooking.setOnAction(event -> showAddBookingDialog());
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

    private void showAddBookingDialog() {
        try {
            // Load file FXML của AddBooking
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/addBooking.fxml"));
            AnchorPane addBookingPane = loader.load();
            
            // Tạo scene mới
            Scene scene = new Scene(addBookingPane);
            
            // Tạo stage mới
            Stage stage = new Stage();
            stage.setTitle("Add Booking");
            stage.setScene(scene);
            
            // Thiết lập modal window (không cho click vào cửa sổ chính)
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Lấy controller của AddBooking
            AddBookingController controller = loader.getController();
            
            // Xử lý nút Cancel
            controller.getBntCancel().setOnAction(e -> stage.close());
            //Xử lý nút Add
            controller.getBntAddBooking().setOnAction(e -> stage.showAndWait());
            // Hiển thị cửa sổ
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}