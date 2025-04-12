package com.utc2.facilityui.controller.nav;

import com.utc2.facilityui.auth.TokenStorage; // <- Thêm import này
import com.utc2.facilityui.model.ButtonNav;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;             // <- Thêm import này
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;             // <- Thêm import này

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects; // <- Thêm import này nếu dùng Objects.requireNonNull

public class ButtonNavController {

    @FXML private AnchorPane btn;
    @FXML public Button buttonNav;
    @FXML private ImageView imgButtonNav;

    public void setData(ButtonNav bntNav) {
        if (bntNav.getImageSrc() != null) {
            // Sử dụng try-with-resources để đảm bảo InputStream được đóng
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
        // Thêm sự kiện click để chuyển trang hoặc đăng xuất
        buttonNav.setOnAction(event -> loadPage(bntNav.getName()));
    }

    private void loadPage(String pageName) {
        try {
            // --- XỬ LÝ ĐĂNG XUẤT ---
            if ("Logout".equalsIgnoreCase(pageName)) { // Dùng equalsIgnoreCase để không phân biệt hoa thường
                // 1. (Optional) Xóa token đã lưu
                TokenStorage.setToken(null); // Giả sử bạn có phương thức này hoặc setToken(null) để xóa

                // 2. Tải FXML của trang login
                // Đảm bảo đường dẫn chính xác
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/utc2/facilityui/view/login2.fxml")));
                AnchorPane loginRoot = loader.load(); // login2.fxml có root là AnchorPane

                // 3. Tạo Scene mới
                Scene loginScene = new Scene(loginRoot);

                // 4. Lấy Stage hiện tại (cửa sổ) từ button hoặc pane chứa button
                Stage stage = (Stage) btn.getScene().getWindow();

                // 5. Đặt Scene mới cho Stage
                stage.setScene(loginScene);
                stage.setTitle("Login"); // Cập nhật lại tiêu đề cửa sổ
                // Optional: Điều chỉnh kích thước nếu cần hoặc căn giữa
                // stage.sizeToScene();
                // stage.centerOnScreen();
                stage.show();

            } else { // --- XỬ LÝ CHUYỂN TRANG BÊN TRONG GIAO DIỆN CHÍNH ---
                // Tìm mainCenter từ scene graph
                AnchorPane mainCenter = (AnchorPane) btn.getScene().lookup("#mainCenter");
                if (mainCenter == null) {
                    System.err.println("Không tìm thấy mainCenter");
                    return;
                }

                // Load trang mới
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/utc2/facilityui/view/" + getPageFile(pageName))));
                AnchorPane newPage = loader.load();

                // Xóa nội dung cũ và thêm trang mới vào mainCenter
                mainCenter.getChildren().clear();
                mainCenter.getChildren().add(newPage);

                // Set anchors để trang mới lấp đầy mainCenter
                AnchorPane.setTopAnchor(newPage, 0.0);
                AnchorPane.setBottomAnchor(newPage, 0.0);
                AnchorPane.setLeftAnchor(newPage, 0.0);
                AnchorPane.setRightAnchor(newPage, 0.0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi chuyển trang hoặc đăng xuất: " + pageName);
            // Có thể hiển thị thông báo lỗi cho người dùng ở đây
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Lỗi NullPointerException, kiểm tra đường dẫn FXML hoặc lookup #mainCenter: " + pageName);
        }
    }

    // Giữ nguyên phương thức getPageFile
    private String getPageFile(String pageName) {
        // Chuyển về chữ thường để switch hoạt động ổn định
        return switch (pageName.toLowerCase()) {
            case "rooms" -> "rooms.fxml";
            case "equipments" -> "equipments.fxml";
            case "my bookings" -> "myBookings.fxml";
            case "reset password" -> "resetpassword.fxml";
            default -> "home.fxml"; // Trang mặc định nếu không khớp
        };
    }
}