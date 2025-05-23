package com.utc2.facilityui.controller.room;

import com.utc2.facilityui.controller.booking.AddBookingController;
import com.utc2.facilityui.model.Room;
// Đảm bảo bạn có ReportRoomController và nó được import đúng nếu được sử dụng
// import com.utc2.facilityui.controller.room.ReportRoomController;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class InfoRoomController {
    // --- Các trường @FXML được cập nhật theo yêu cầu ---
    @FXML
    private Button bntReport;

    @FXML
    private Text buildingName;

    @FXML
    private Button buttonAddBooking;

    @FXML
    private Button buttonBack;

    @FXML
    private Text capacity;

    @FXML
    private Label description;

    @FXML
    private ImageView img;

    @FXML
    private Label info; // Label tiêu đề "Information Room"

    @FXML
    private Label name; // Label tên phòng

    @FXML
    private Text nameFacilityManager; // Khớp FXML Text fx:id="nameFacilityManager"

    @FXML
    private Text status;

    @FXML
    private Text roomTypeName;

    @FXML
    private Text createdAt;

    @FXML
    private Text updatedAt;

    @FXML
    private Text defaultEquipments;

    private String sourceView;
    private Room currentRoomData;

    // Locale cho định dạng ngày giờ Việt Nam
    private final Locale vietnameseLocale = new Locale("vi", "VN");
    // Đã cập nhật định dạng có dấu phẩy
    private final DateTimeFormatter vietnameseDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", vietnameseLocale);


    // --- CÁC PHƯƠNG THỨC SAU ĐƯỢC GIỮ NGUYÊN TỪ CODE GỐC BẠN CUNG CẤP ---
    public void setSourceView(String source) {
        this.sourceView = source;
    }

    @FXML
    public void initialize() {
        buttonBack.setOnAction(event -> goBack());
        buttonAddBooking.setOnAction(event -> showAddBookingDialog());
        bntReport.setOnAction(event -> showReportRooomDialog());
    }

    // --- PHƯƠNG THỨC LOADROOMDETAILS ĐƯỢC CẬP NHẬT ---
    public void loadRoomDetails(Room roomData) {
        if (roomData == null) {
            System.err.println("InfoRoomController nhận được roomData null!");
            if (name != null) name.setText("Không có dữ liệu phòng");
            if (description != null) description.setText("");
            if (roomTypeName != null) roomTypeName.setText("N/A");
            if (buildingName != null) buildingName.setText("N/A");
            if (capacity != null) capacity.setText("N/A");
            if (status != null) status.setText("N/A");
            if (nameFacilityManager != null) nameFacilityManager.setText("N/A");
            if (createdAt != null) createdAt.setText("N/A");
            if (updatedAt != null) updatedAt.setText("N/A");
            if (defaultEquipments != null) defaultEquipments.setText("");
            if (img != null) img.setImage(null);
            return;
        }

        this.currentRoomData = roomData;

        if (name != null) name.setText(roomData.getName() != null ? roomData.getName() : "N/A");
        if (description != null) description.setText(roomData.getDescription() != null ? roomData.getDescription() : "Không có mô tả");

        if (roomTypeName != null) {
            roomTypeName.setText(roomData.getRoomTypeName() != null ? roomData.getRoomTypeName() : "N/A");
        }
        if (buildingName != null) {
            buildingName.setText(roomData.getBuildingName() != null ? roomData.getBuildingName() : "N/A");
        }
        if (capacity != null) {
            capacity.setText(String.valueOf(roomData.getCapacity()) + " người");
        }
        if (status != null) {
            status.setText(roomData.getStatus() != null ? roomData.getStatus() : "N/A");
        }
        if (nameFacilityManager != null) {
            nameFacilityManager.setText(roomData.getNameFacilityManager() != null ? roomData.getNameFacilityManager() : "Chưa có");
        }

        if (createdAt != null) {
            createdAt.setText(formatToVietnameseDateTime(roomData.getCreatedAt()));
        }
        if (updatedAt != null) {
            updatedAt.setText(formatToVietnameseDateTime(roomData.getUpdatedAt()));
        }

        // Hiển thị defaultEquipments - LẤY THEO 'modelName'
        if (defaultEquipments != null) {
            if (roomData.getDefaultEquipments() != null && !roomData.getDefaultEquipments().isEmpty()) {
                List<Object> eqs = roomData.getDefaultEquipments();
                String equipmentModelNames = eqs.stream()
                        .map(obj -> {
                            if (obj instanceof Map) {
                                Map<?, ?> map = (Map<?, ?>) obj;
                                // Lấy 'modelName' thay vì 'name'
                                Object modelNameObj = map.get("modelName");
                                return modelNameObj != null ? modelNameObj.toString() : "Model không xác định";
                            }
                            return obj.toString(); // Fallback nếu đối tượng không phải là Map
                        })
                        .collect(Collectors.joining(", "));
                defaultEquipments.setText(equipmentModelNames);
            } else {
                defaultEquipments.setText(""); // Để trống nếu không có thiết bị
            }
        }

        if (img != null) {
            loadImage(roomData.getImg());
        }
    }

    private String formatToVietnameseDateTime(String isoDateTimeString) {
        if (isoDateTimeString == null || isoDateTimeString.isEmpty() || isoDateTimeString.equalsIgnoreCase("N/A")) {
            return "N/A";
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTimeString);
            return dateTime.format(vietnameseDateFormatter); // Sử dụng formatter đã cập nhật
        } catch (DateTimeParseException e1) {
            try {
                LocalDate date = LocalDate.parse(isoDateTimeString);
                return date.format(vietnameseDateFormatter);
            } catch (DateTimeParseException e2) {
                System.err.println("Không thể phân tích cú pháp ngày giờ hoặc ngày: " + isoDateTimeString + " - " + e2.getMessage());
                return isoDateTimeString;
            }
        }
    }
    // --- KẾT THÚC PHƯƠNG THỨC LOADROOMDETAILS CẬP NHẬT ---


    // --- CÁC PHƯƠNG THỨC SAU ĐƯỢC GIỮ NGUYÊN ---
    private void loadImage(String imagePath) {
        Image image = null;
        String effectivePath = (imagePath != null && !imagePath.trim().isEmpty()) ? imagePath : getDefaultImagePath();
        InputStream imageStream = null;
        try {
            if (!effectivePath.startsWith("/") && !effectivePath.startsWith("http")) {
                effectivePath = "/com/utc2/facilityui/images/" + effectivePath;
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
        if (img != null) {
            img.setImage(image);
        } else {
            System.err.println("ImageView 'img' chưa được inject!");
        }
    }

    private String getDefaultImagePath() {
        return "/com/utc2/facilityui/images/default_room.png";
    }

    private void goBack() {
        try {
            AnchorPane mainCenter = (AnchorPane) buttonBack.getScene().lookup("#mainCenter");
            if (mainCenter == null) return;
            String viewPath = "/com/utc2/facilityui/view/" +
                    (sourceView != null ? sourceView : "rooms") + ".fxml";
            System.out.println("Quay về trang: " + viewPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            AnchorPane page = loader.load();
            mainCenter.getChildren().clear();
            mainCenter.getChildren().add(page);
            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAddBookingDialog() {
        if (this.currentRoomData == null) {
            System.err.println("Lỗi: Dữ liệu phòng hiện tại (currentRoomData) là null.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/addBooking.fxml"));
            Parent root = loader.load();
            AddBookingController addBookingController = loader.getController();
            String selectedRoomId = this.currentRoomData.getId();
            String selectedRoomName = this.currentRoomData.getName();
            if (selectedRoomId == null || selectedRoomId.trim().isEmpty()) {
                System.err.println("Lỗi: ID phòng từ currentRoomData là null hoặc rỗng.");
                return;
            }
            addBookingController.setTargetInfo(selectedRoomId, selectedRoomName);
            Stage stage = new Stage();
            stage.setTitle("Add New Booking for " + selectedRoomName);
            try {
                Image icon = new Image(getClass().getResourceAsStream("/com/utc2/facilityui/images/logo-icon-UTC2.png"));
                stage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Không thể load icon: " + e.getMessage());
            }
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            System.out.println("Cửa sổ Add Booking đã đóng.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showReportRooomDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/reportRoom.fxml"));
            AnchorPane reportPane = loader.load();
            Scene scene = new Scene(reportPane);
            Stage stage = new Stage();
            stage.setTitle("Report Room");
            stage.setScene(scene);
            Image icon = new Image(getClass().getResourceAsStream("/com/utc2/facilityui/images/logo-icon-UTC2.png"));
            stage.getIcons().add(icon);
            stage.initModality(Modality.APPLICATION_MODAL);

            com.utc2.facilityui.controller.room.ReportRoomController controller = loader.getController();

            if (controller != null) {
                if (this.currentRoomData != null) {
                    if (controller.getName() != null) {
                        controller.getName().setText(this.currentRoomData.getName());
                    }
                } else {
                    if (controller.getName() != null) {
                        controller.getName().setText("N/A");
                    }
                }
                if (controller.getBntCancel() != null) {
                    controller.getBntCancel().setOnAction(e -> stage.close());
                }
                if (controller.getBntAdd() != null) {
                    controller.getBntAdd().setOnAction(e -> {
                        controller.handleAdd();
                        stage.close();
                    });
                }
            } else {
                System.err.println("ReportRoomController không được tải từ FXML!");
            }
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không mong muốn trong showReportRooomDialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}