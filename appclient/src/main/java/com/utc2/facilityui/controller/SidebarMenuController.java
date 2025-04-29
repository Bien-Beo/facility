package com.utc2.facilityui.controller;

import com.utc2.facilityui.auth.TokenStorage; // Đảm bảo import đúng
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable; // Import Initializable nếu bạn cần phương thức initialize
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL; // Import URL nếu dùng Initializable
import java.util.ResourceBundle; // Import ResourceBundle nếu dùng Initializable

public class SidebarMenuController /* implements Initializable */ { // Bạn có thể thêm Initializable nếu cần

    // Tham chiếu đến BorderPane chính để thay đổi nội dung trung tâm
    private BorderPane mainBorderPane;

    // Các thành phần FXML được inject từ sidebarmenu.fxml
    @FXML private Button manageFacilitiesButton;
    @FXML private Button manageBookingsButton;
    @FXML private Button approvalRequestsButton;
    @FXML private Label approvalCountLabel; // Label để hiển thị số lượng yêu cầu chờ duyệt
    @FXML private Button cancellationRequestsButton;
    @FXML private Button reportButton;
    @FXML private Button resetPasswordButton;
    @FXML private Button logoutButton;

    /**
     * Phương thức này được gọi bởi MainScreenController để truyền vào
     * tham chiếu của BorderPane chính.
     * @param mainBorderPane BorderPane chính của ứng dụng.
     */
    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    /**
     * (Tùy chọn) Phương thức khởi tạo FXML.
     * Được gọi sau khi tất cả các thành phần @FXML đã được inject.
     * Bạn có thể dùng nó để cập nhật trạng thái ban đầu (ví dụ: lấy số lượng approval).
     */
    /*
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ví dụ: Cập nhật số lượng approval ban đầu (cần logic lấy dữ liệu thực tế)
        // updateApprovalCount(5); // Gọi hàm cập nhật với số lượng giả định
        System.out.println("SidebarMenuController initialized.");
    }
    */

    // --- Các phương thức xử lý sự kiện cho các nút menu ---

    @FXML
    private void handleManageFacilities(ActionEvent event) {
        System.out.println("Manage Facilities button clicked.");
        loadView("/com/utc2/facilityui/view/manageFacility.fxml"); // << THAY THẾ BẰNG ĐƯỜNG DẪN ĐÚNG
    }

    @FXML
    private void handleManageBookings(ActionEvent event) {
        System.out.println("Manage Bookings button clicked.");
        loadView("/com/utc2/facilityui/view/manageBookings.fxml"); // << THAY THẾ BẰNG ĐƯỜNG DẪN ĐÚNG
    }

    @FXML
    private void handleApprovalRequests(ActionEvent event) {
        System.out.println("Approval Requests button clicked.");
        loadView("/com/utc2/facilityui/view/approvalRequests.fxml"); // << THAY THẾ BẰNG ĐƯỜNG DẪN ĐÚNG
    }

    @FXML
    private void handleCancellationRequests(ActionEvent event) {
        System.out.println("Cancellation Requests button clicked.");
        loadView("/com/utc2/facilityui/view/cancellationRequests.fxml"); // << THAY THẾ BẰNG ĐƯỜNG DẪN ĐÚNG
    }

    @FXML
    private void handleReport(ActionEvent event) {
        System.out.println("Report button clicked.");
        loadView("/com/utc2/facilityui/view/report.fxml"); // << THAY THẾ BẰNG ĐƯỜNG DẪN ĐÚNG
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        System.out.println("Reset Password button clicked.");
        loadView("/com/utc2/facilityui/view/resetPassword.fxml"); // << THAY THẾ BẰNG ĐƯỜNG DẪN ĐÚNG
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked.");

        // 1. Xóa token đã lưu trữ
        TokenStorage.clearToken(); // Gọi phương thức clearToken từ lớp TokenStorage
        System.out.println("Token cleared via TokenStorage.");

        // 2. Chuyển hướng về màn hình đăng nhập (Login2.fxml)
        try {
            // Lấy Stage (cửa sổ) hiện tại từ nút Logout
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            if (stage == null) {
                System.err.println("SidebarMenuController: Critical error - Could not get the current stage.");
                showErrorAlert("Lỗi Đăng Xuất", "Không thể truy cập cửa sổ ứng dụng để đăng xuất.");
                return;
            }

            // Tải file FXML của màn hình đăng nhập
            // *** ĐẢM BẢO ĐƯỜNG DẪN NÀY LÀ CHÍNH XÁC ***
            String loginFxmlPath = "/com/utc2/facilityui/view/Login2.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(loginFxmlPath));

            // Kiểm tra xem có tìm thấy file FXML không
            if (loader.getLocation() == null) {
                throw new IOException("Không thể tìm thấy file FXML tại đường dẫn: " + loginFxmlPath);
            }

            Parent loginRoot = loader.load(); // Tải giao diện đăng nhập

            // Tạo một Scene mới với giao diện đăng nhập
            Scene loginScene = new Scene(loginRoot);

            // Đặt Scene mới làm Scene hiện tại cho Stage
            stage.setScene(loginScene);
            stage.setTitle("Login"); // Cập nhật lại tiêu đề cửa sổ
            stage.centerOnScreen(); // Căn giữa lại cửa sổ trên màn hình
            stage.show(); // Hiển thị lại stage với scene mới

            System.out.println("Successfully navigated back to Login screen.");

        } catch (IOException e) {
            System.err.println("SidebarMenuController: Error loading Login screen (" + "/com/utc2/facilityui/view/Login2.fxml" + "): " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Lỗi Đăng Xuất", "Đã xảy ra lỗi khi cố gắng tải màn hình đăng nhập: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("SidebarMenuController: Null pointer exception during logout. Check if logoutButton, scene, or window is null.");
            e.printStackTrace();
            showErrorAlert("Lỗi Đăng Xuất", "Có lỗi xảy ra trong quá trình đăng xuất (tham chiếu null). Vui lòng thử lại.");
        }
    }

    // --- Phương thức tiện ích ---

    /**
     * Tải một view FXML vào vùng trung tâm của mainBorderPane.
     * @param fxmlPath Đường dẫn đến file FXML cần tải (ví dụ: "/com/utc2/facilityui/view/dashboard.fxml").
     */
    private void loadView(String fxmlPath) {
        if (mainBorderPane == null) {
            System.err.println("SidebarMenuController: Cannot load view. Main BorderPane is not set.");
            showErrorAlert("Lỗi Tải Giao Diện", "Không thể tải giao diện do cấu trúc layout chính bị lỗi.");
            return;
        }
        try {
            System.out.println("Loading view: " + fxmlPath);
            // *** ĐẢM BẢO ĐƯỜNG DẪN NÀY LÀ CHÍNH XÁC ***
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("Không thể tìm thấy file FXML tại đường dẫn: " + fxmlPath);
            }
            Parent view = loader.load();
            mainBorderPane.setCenter(view); // Đặt view mới vào trung tâm
            System.out.println("View loaded successfully: " + fxmlPath);
        } catch (IOException e) {
            System.err.println("SidebarMenuController: Failed to load view " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Lỗi Tải Giao Diện", "Không thể tải giao diện: " + fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1) + "\nLỗi: " + e.getMessage());
        }
    }

    /**
     * (Ví dụ) Cập nhật số lượng hiển thị trên label approvalCountLabel.
     * @param count Số lượng cần hiển thị.
     */
    public void updateApprovalCount(int count) {
        if (count > 0) {
            approvalCountLabel.setText(String.valueOf(count));
            approvalCountLabel.setVisible(true); // Hiển thị nếu có số > 0
        } else {
            approvalCountLabel.setVisible(false); // Ẩn đi nếu không có hoặc bằng 0
        }
    }


    /**
     * Hiển thị một hộp thoại thông báo lỗi đơn giản.
     * @param title Tiêu đề của hộp thoại lỗi.
     * @param message Nội dung thông báo lỗi.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // Không cần header text
        alert.setContentText(message);
        alert.showAndWait(); // Hiển thị và đợi người dùng đóng
    }
}