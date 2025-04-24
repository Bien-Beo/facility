package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
import com.utc2.facilityui.service.ApprovalRequestService; // Giả sử bạn có service này
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SidebarMenuController implements Initializable {

    @FXML
    private Button manageFacilitiesButton;

    @FXML
    private Button manageBookingsButton;

    @FXML
    private Button approvalRequestsButton;

    @FXML
    private Label approvalCountLabel; // Đã thêm

    @FXML
    private Button cancellationRequestsButton;

    @FXML
    private Button reportButton;

    @FXML
    private Button resetPasswordButton;

    @FXML
    private Button logoutButton;

    private BorderPane mainBorderPane;
    private ApprovalRequestService approvalRequestService; // Giả sử bạn có service này để lấy dữ liệu

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        approvalRequestService = new ApprovalRequestService(); // Khởi tạo service
        updatePendingApprovalRequestCount(); // Lấy và đặt số lượng khi khởi tạo
    }

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    @FXML
    void handleManageFacilities(ActionEvent event) {
        System.out.println("Nút Manage Facilities được nhấp");
        loadView("/com/utc2/facilityui/view/manageFacility.fxml");
    }

    @FXML
    void handleManageBookings(ActionEvent event) {
        System.out.println("Nút Manage Bookings được nhấp");
        loadView("/com/utc2/facilityui/view/manageBookings.fxml");
    }

    @FXML
    void handleApprovalRequests(ActionEvent event) {
        System.out.println("Nút Approval Requests được nhấp");
        loadView("/com/utc2/facilityui/view/approvalrequests.fxml");
    }

    @FXML
    void handleCancellationRequests(ActionEvent event) {
        System.out.println("Nút Cancellation Requests được nhấp");
        loadView("/com/utc2/facilityui/view/cancellationRequests.fxml");
    }

    @FXML
    void handleReport(ActionEvent event) {
        System.out.println("Nút Report được nhấp");
        loadView("/com/utc2/facilityui/view/report.fxml");
    }

    @FXML
    void handleResetPassword(ActionEvent event) {
        System.out.println("Nút Reset Password được nhấp");
        loadView("/com/utc2/facilityui/view/resetPassword.fxml");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("Nút Logout được nhấp");
        // Xử lý logic đăng xuất (ví dụ: đóng ứng dụng)
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ
        }
    }

    public void setApprovalRequestCount(int count) {
        approvalCountLabel.setText(String.valueOf(count));
    }

    private void updatePendingApprovalRequestCount() {
        int pendingCount = getPendingApprovalRequestCountForCurrentUser();
        setApprovalRequestCount(pendingCount);
    }

    private int getPendingApprovalRequestCountForCurrentUser() {
        // Thay thế đoạn này bằng logic thực tế để lấy số lượng yêu cầu phê duyệt đang chờ
        // cho người dùng hiện tại.

        // 1. Lấy ID hoặc định danh của người dùng hiện tại.
        String currentUserId = getCurrentUserId(); // Bạn cần triển khai phương thức này

        // 2. Gọi ApprovalRequestService (hoặc phương thức truy cập dữ liệu của bạn) để lấy số lượng.
        try {
            if (approvalRequestService != null) {
                return approvalRequestService.getPendingApprovalCountForUser(currentUserId);
            } else {
                System.err.println("ApprovalRequestService chưa được khởi tạo.");
                return 0; // Trả về 0 nếu service không khả dụng
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi lấy số lượng yêu cầu phê duyệt đang chờ: " + e.getMessage());
            e.printStackTrace();
            return 0; // Trả về 0 trong trường hợp có lỗi
        }
    }

    // Placeholder cho phương thức lấy ID người dùng hiện tại
    private String getCurrentUserId() {
        // Thay thế đoạn này bằng triển khai thực tế để lấy ID người dùng hiện tại
        // Điều này có thể liên quan đến việc truy cập session, context người dùng, v.v.
        return "user123"; // ID người dùng ví dụ
    }
}