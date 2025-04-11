package com.utc2.facilityui.controller.room;

import com.utc2.facilityui.controller.booking.AddBookingController;
import com.utc2.facilityui.model.Room;
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
import java.io.InputStream;

public class InfoRoomController {
    @FXML
    private Button bntReport;

    @FXML
    private Text building;

    @FXML
    private Button buttonAddBooking;

    @FXML
    private Button buttonBack;

    @FXML
    private Text capacity;

    @FXML
    private Text date;

    @FXML
    private Label description;

    @FXML
    private ImageView img;

    @FXML
    private Label info;

    @FXML
    private Label name;

    @FXML
    private Text nameFManager;

    @FXML
    private Label nameFacilityManager;

    @FXML
    private Text status;

    @FXML
    private Text typeRoom;

    private String sourceView; // Lưu trang nguồn
    private Room currentRoomData;
    public void setSourceView(String source) {
        this.sourceView = source;
    }

    @FXML
    public void initialize() {
        buttonBack.setOnAction(event -> goBack());
        buttonAddBooking.setOnAction(event -> showAddBookingDialog());
        bntReport.setOnAction(event -> showReportRooomDialog());
    }
    public void loadRoomDetails(Room roomData) {
        if (roomData == null) {
            System.err.println("InfoRoomController nhận được roomData null!");
            // Hiển thị thông báo lỗi hoặc trạng thái không có dữ liệu
            name.setText("Không có dữ liệu phòng");
            description.setText("");
            // ... xóa hoặc ẩn các thông tin khác ...
            return;
        }

        this.currentRoomData = roomData; // Lưu lại để có thể dùng ở chỗ khác nếu cần

        // --- Cập nhật UI với dữ liệu từ roomData ---
        name.setText(roomData.getName() != null ? roomData.getName() : "N/A");
        description.setText(roomData.getDescription() != null ? roomData.getDescription() : "Không có mô tả");
        typeRoom.setText(roomData.getRoomTypeName() != null ? roomData.getRoomTypeName() : "N/A");
        building.setText(roomData.getBuildingName() != null ? roomData.getBuildingName() : "N/A");
        capacity.setText(String.valueOf(roomData.getCapacity())); // Chuyển int sang String
        status.setText(roomData.getStatus() != null ? roomData.getStatus() : "N/A");
        nameFManager.setText(roomData.getNameFacilityManager() != null ? roomData.getNameFacilityManager() : "Chưa có");
        // nameFacilityManager.setText(...); // Nếu Label này khác với nameFManager

        // date.setText(...); // Cần xác định ngày gì? Ví dụ: roomData.getCreatedAt() hoặc updatedAt?
        // info.setText(...); // Cần xác định thông tin gì?

        // Tải ảnh
        loadImage(roomData.getImg());
    }
    // Hàm tải ảnh (tương tự như trong Card Controller, có thể tạo lớp tiện ích dùng chung)
    private void loadImage(String imagePath) {
        Image image = null;
        String effectivePath = (imagePath != null && !imagePath.trim().isEmpty()) ? imagePath : getDefaultImagePath();
        InputStream imageStream = null;
        try {
            // Ưu tiên load từ resources nếu đường dẫn là tương đối
            if (!effectivePath.startsWith("/") && !effectivePath.startsWith("http")) {
                effectivePath = "/com/utc2/facilityui/images/" + effectivePath; // Giả sử ảnh nằm trong thư mục này
            }
            imageStream = getClass().getResourceAsStream(effectivePath);
            if (imageStream != null) {
                image = new Image(imageStream);
                if (image.isError()) {
                    System.err.println("Lỗi tạo Image: " + effectivePath + " - " + image.getException());
                    image = null;
                }
            } else {
                System.err.println("Không tìm thấy resource: " + effectivePath);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh: " + effectivePath + " - " + e.getMessage());
        } finally {
            if (imageStream != null) { try { imageStream.close(); } catch (IOException e) { /* ignore */ } }
        }

        // Thử ảnh mặc định nếu lỗi
        if (image == null) {
            InputStream defaultStream = null;
            try {
                defaultStream = getClass().getResourceAsStream(getDefaultImagePath());
                if (defaultStream != null) image = new Image(defaultStream);
                else System.err.println("Không tìm thấy ảnh mặc định: " + getDefaultImagePath());
            } catch (Exception e) {
                System.err.println("Lỗi tải ảnh mặc định: " + e.getMessage());
            } finally {
                if (defaultStream != null) { try { defaultStream.close(); } catch (IOException e) { /* ignore */ } }
            }
        }
        img.setImage(image); // Đặt ảnh (có thể là null nếu cả 2 lỗi)
    }
    private String getDefaultImagePath() {
        return "/com/utc2/facilityui/images/default_room.png";
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/addBooking.fxml"));
            AnchorPane addBookingPane = loader.load();
            Scene scene = new Scene(addBookingPane);
            Stage stage = new Stage();
            // ... (set title, icon, modality) ...
            stage.setTitle("Add Booking");
            stage.setScene(scene);
            Image icon = new Image(getClass().getResourceAsStream("/com/utc2/facilityui/images/logo-icon-UTC2.png"));
            stage.getIcons().add(icon);
            stage.initModality(Modality.APPLICATION_MODAL);


            AddBookingController controller = loader.getController();
            // Lấy tên phòng từ dữ liệu đã lưu thay vì từ Label
            if (this.currentRoomData != null) {
                controller.getName().setText(this.currentRoomData.getName());
                // Bạn có thể truyền thêm ID phòng hoặc thông tin khác nếu AddBookingController cần
                // controller.setRoomId(this.currentRoomData.getId());
            } else {
                controller.getName().setText("N/A"); // Hoặc xử lý lỗi
            }

            controller.getBntCancel().setOnAction(e -> stage.close());
            controller.getBntAddBooking().setOnAction(e -> {
                // controller.handleAddBooking(); // Gọi hàm xử lý logic add booking
                stage.close(); // Đóng sau khi xử lý xong
            });
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // InfoRoomController.java
    private void showReportRooomDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/reportRoom.fxml"));
            AnchorPane reportPane = loader.load();
            Scene scene = new Scene(reportPane);
            Stage stage = new Stage();
            // ... (set title, icon, modality) ...
            stage.setTitle("Report Room");
            stage.setScene(scene);
            Image icon = new Image(getClass().getResourceAsStream("/com/utc2/facilityui/images/logo-icon-UTC2.png"));
            stage.getIcons().add(icon);
            stage.initModality(Modality.APPLICATION_MODAL);


            ReportRoomController controller = loader.getController();
            // Lấy tên phòng từ dữ liệu đã lưu thay vì từ Label
            if (this.currentRoomData != null) {
                controller.getName().setText(this.currentRoomData.getName());
                // Truyền thêm ID phòng nếu ReportRoomController cần để gửi report
                // controller.setRoomId(this.currentRoomData.getId());
            } else {
                controller.getName().setText("N/A"); // Hoặc xử lý lỗi
            }

            controller.getBntCancel().setOnAction(e -> stage.close());
            controller.getBntAdd().setOnAction(e -> {
                controller.handleAdd(); // Gọi hàm xử lý logic add report
                stage.close(); // Đóng sau khi xử lý xong
            });
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}