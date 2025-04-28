package com.utc2.facilityui.controller.room;

import com.utc2.facilityui.controller.booking.AddBookingController;
import com.utc2.facilityui.model.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        // Giả định this.currentRoomData chứa thông tin phòng đã chọn và không null
        if (this.currentRoomData == null) {
            System.err.println("Lỗi: Dữ liệu phòng hiện tại (currentRoomData) là null.");
            // Có thể hiển thị Alert cho người dùng ở đây
            // showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa chọn phòng để đặt.");
            return;
        }

        try {
            // 1. Load FXML
            // Đảm bảo đường dẫn FXML là chính xác
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/addBooking.fxml"));
            Parent root = loader.load(); // Dùng Parent thay vì AnchorPane để linh hoạt hơn

            // 2. Lấy Controller SAU KHI load FXML
            AddBookingController addBookingController = loader.getController();

            // 3. !! BƯỚC QUAN TRỌNG: Gọi setTargetInfo để truyền ID và Tên phòng !!
            // Lấy ID và Tên từ dữ liệu phòng đã chọn (this.currentRoomData)
            String selectedRoomId = this.currentRoomData.getId(); // Giả định có phương thức getId()
            String selectedRoomName = this.currentRoomData.getName(); // Giả định có phương thức getName()

            // Kiểm tra lại ID trước khi truyền
            if (selectedRoomId == null || selectedRoomId.trim().isEmpty()) {
                System.err.println("Lỗi: ID phòng từ currentRoomData là null hoặc rỗng.");
                // showAlert(Alert.AlertType.ERROR, "Lỗi Dữ Liệu", "Không thể lấy ID phòng hợp lệ.");
                return;
            }
            addBookingController.setTargetInfo(selectedRoomId, selectedRoomName);

            // 4. Tạo và cấu hình Stage (Cửa sổ mới)
            Stage stage = new Stage();
            stage.setTitle("Add New Booking for " + selectedRoomName); // Hiển thị tên phòng trên tiêu đề
            // Cấu hình icon nếu cần
            try {
                Image icon = new Image(getClass().getResourceAsStream("/com/utc2/facilityui/images/logo-icon-UTC2.png"));
                stage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Không thể load icon: " + e.getMessage());
            }
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ cha
            stage.setResizable(false); // Có thể không cho thay đổi kích thước

            // 5. !! KHÔNG GHI ĐÈ setOnAction ở đây !!
            // Để FXML tự động liên kết nút bấm với các phương thức handleAddBooking và handleCancel
            // trong AddBookingController thông qua thuộc tính onAction="#methodName"

            // 6. Hiển thị cửa sổ và đợi người dùng tương tác
            stage.showAndWait();

            // Code ở đây sẽ chạy sau khi cửa sổ Add Booking đóng lại
            System.out.println("Cửa sổ Add Booking đã đóng.");
            // TODO: Có thể cần làm mới (refresh) danh sách booking hoặc trạng thái phòng ở đây

        } catch (IOException e) {
            e.printStackTrace();
            // Hiển thị lỗi load FXML cho người dùng
            // showAlert(Alert.AlertType.ERROR, "Lỗi Giao Diện", "Không thể mở cửa sổ đặt phòng: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            // Lỗi này có thể xảy ra nếu currentRoomData hoặc các phương thức getId/getName trả về null
            // showAlert(Alert.AlertType.ERROR, "Lỗi Dữ Liệu", "Dữ liệu phòng không hợp lệ: " + e.getMessage());
        } catch (Exception e) { // Bắt các lỗi khác
            e.printStackTrace();
            // showAlert(Alert.AlertType.ERROR, "Lỗi Không Mong Muốn", "Đã xảy ra lỗi: " + e.getMessage());
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