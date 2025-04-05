package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ButtonNav;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStream;

public class ButtonNavController {

    @FXML private AnchorPane btn;
    @FXML public Button buttonNav;
    @FXML private ImageView imgButtonNav;

    public void setData(ButtonNav bntNav) {
        if (bntNav.getImageSrc() != null) {
            InputStream imgStream = getClass().getResourceAsStream(bntNav.getImageSrc());
            if (imgStream != null) {
                Image image = new Image(imgStream);
                imgButtonNav.setImage(image);
            } else {
                System.err.println("Không tìm thấy ảnh: " + bntNav.getImageSrc());
            }
        }
        buttonNav.setText(bntNav.getName());
        // Thêm sự kiện click để chuyển trang
        buttonNav.setOnAction(event -> loadPage(bntNav.getName()));
    }
    private void loadPage(String pageName) {
        try {
            // Tìm mainCenter từ scene graph
            AnchorPane mainCenter = (AnchorPane) btn.getScene().lookup("#mainCenter");
            if (mainCenter == null) {
                System.err.println("Không tìm thấy mainCenter");
                return;
            }

            // Load trang mới
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/" + getPageFile(pageName)));
            AnchorPane newPage = loader.load();

            // Xóa nội dung cũ và thêm trang mới vào mainCenter
            mainCenter.getChildren().clear();
            mainCenter.getChildren().add(newPage);

            // Set anchors để trang mới lấp đầy mainCenter
            AnchorPane.setTopAnchor(newPage, 0.0);
            AnchorPane.setBottomAnchor(newPage, 0.0);
            AnchorPane.setLeftAnchor(newPage, 0.0);
            AnchorPane.setRightAnchor(newPage, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi chuyển trang: " + pageName);
        }
    }

    private String getPageFile(String pageName) {
        return switch (pageName.toLowerCase()) {
            case "rooms" -> "rooms.fxml";
            case "equipments" -> "equipments.fxml";
            case "my bookings" -> "myBookings.fxml";
            case "reset password" -> "resetpassword.fxml";
            case "logout" -> "login.fxml";
            default -> "home.fxml";
        };
    }

}


