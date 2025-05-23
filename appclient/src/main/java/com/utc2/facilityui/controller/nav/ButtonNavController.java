package com.utc2.facilityui.controller.nav;

import com.utc2.facilityui.auth.TokenStorage; // Import TokenStorage
import com.utc2.facilityui.model.ButtonNav;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ButtonNavController {

    @FXML private AnchorPane btn;
    @FXML public Button buttonNav;
    @FXML private ImageView imgButtonNav;

    public void setData(ButtonNav bntNav) {
        // ... (phần set image và text giữ nguyên) ...
        if (bntNav.getImageSrc() != null) {
            try (InputStream imgStream = getClass().getResourceAsStream(bntNav.getImageSrc())) {
                if (imgStream != null) {
                    Image image = new Image(imgStream);
                    imgButtonNav.setImage(image);
                } else {
                    System.err.println("Không tìm thấy ảnh: " + bntNav.getImageSrc());
                }
            } catch (IOException e) {
                System.err.println("Lỗi khi đọc ảnh: " + bntNav.getImageSrc());
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.err.println("getResourceAsStream trả về null cho: " + bntNav.getImageSrc());
            }
        }
        buttonNav.setText(bntNav.getName());
        buttonNav.setOnAction(event -> loadPage(bntNav.getName()));
    }

    private void loadPage(String pageName) {
        try {
            if ("Đăng xuất".equalsIgnoreCase(pageName)) {
                // SỬ DỤNG PHƯƠNG THỨC LOGOUT MỚI CỦA TokenStorage
                TokenStorage.logout();

                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/utc2/facilityui/view/login2.fxml")));
                AnchorPane loginRoot = loader.load();
                Scene loginScene = new Scene(loginRoot);
                Stage stage = (Stage) btn.getScene().getWindow();
                stage.setScene(loginScene);
                stage.setTitle("Login");
                // stage.sizeToScene(); // Cân nhắc việc thay đổi kích thước cửa sổ
                // stage.centerOnScreen();
                stage.show();
            } else {
                // ... (phần chuyển trang khác giữ nguyên) ...
                AnchorPane mainCenter = (AnchorPane) btn.getScene().lookup("#mainCenter");
                if (mainCenter == null) {
                    System.err.println("Không tìm thấy mainCenter");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/utc2/facilityui/view/" + getPageFile(pageName))));
                AnchorPane newPage = loader.load();
                mainCenter.getChildren().clear();
                mainCenter.getChildren().add(newPage);
                AnchorPane.setTopAnchor(newPage, 0.0);
                AnchorPane.setBottomAnchor(newPage, 0.0);
                AnchorPane.setLeftAnchor(newPage, 0.0);
                AnchorPane.setRightAnchor(newPage, 0.0);
            }
        } catch (IOException | NullPointerException e) { // Bắt cả hai ngoại lệ
            e.printStackTrace();
            System.err.println("Lỗi khi xử lý trang '" + pageName + "': " + e.getMessage());
            // Hiển thị lỗi cho người dùng nếu cần
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Lỗi điều hướng");
            errorAlert.setHeaderText("Không thể tải trang yêu cầu.");
            errorAlert.setContentText("Đã xảy ra lỗi khi cố gắng tải trang '" + pageName + "'. Vui lòng thử lại hoặc liên hệ quản trị viên.");
            errorAlert.showAndWait();
        }
    }

    private String getPageFile(String pageName) {
        return switch (pageName.toLowerCase()) {
            case "phòng" -> "rooms.fxml";
            case "thông báo" -> "myNotification.fxml";
            case "đặt phòng của tôi" -> "myBookings.fxml";
            case "đặt lại mật khẩu" -> "resetpassword.fxml";
            // Thêm các case khác nếu cần
            default -> "home.fxml"; // Hoặc một trang lỗi/trang không tìm thấy
        };
    }
}