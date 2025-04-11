package com.utc2.facilityui.controller.equipment;

import com.utc2.facilityui.model.CardInfoEquipment;
import com.utc2.facilityui.model.Equipment; // Sử dụng model Equipment đã sửa
import com.utc2.facilityui.service.EquipmentService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EquipmentsController implements Initializable {
    @FXML private HBox cardElectronics;
    @FXML private HBox cardAudioEquipment;
    @FXML private VBox mainEquipmentsContainer;

    private EquipmentService equipmentService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.equipmentService = new EquipmentService();
        loadAndRenderEquipments();
    }

    private void loadAndRenderEquipments() {
        // Xóa card cũ và lỗi cũ
        cardElectronics.getChildren().clear();
        cardAudioEquipment.getChildren().clear();
        if (mainEquipmentsContainer != null) {
            mainEquipmentsContainer.getChildren().removeIf(node -> node.getStyleClass().contains("error-label"));
        } else {
            // Lỗi này không nên xảy ra nữa nếu FXML đã được sửa
            System.err.println("Lỗi: mainEquipmentsContainer chưa được inject từ FXML!");
        }

        new Thread(() -> {
            try {
                List<Equipment> allEquipments = equipmentService.getAllEquipments();

                Platform.runLater(() -> {
                    if (allEquipments == null || allEquipments.isEmpty()) {
                        showError("Không tải được danh sách thiết bị hoặc danh sách trống.");
                        return;
                    }

                    for (Equipment equipment : allEquipments) {
                        // Bỏ qua nếu equipment bị null trong list (đề phòng)
                        if (equipment == null) continue;

                        try {
                            CardInfoEquipment cardInfo = mapEquipmentToCardInfo(equipment);
                            // Bỏ qua nếu không map được (ví dụ thiếu thông tin cơ bản)
                            if (cardInfo == null) continue;

                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardEquipment.fxml"));
                            AnchorPane cardNode = fxmlLoader.load();

                            CardEquipmentController cardController = fxmlLoader.getController();
                            if (cardController != null) {
                                cardController.setData(cardInfo, equipment);
                                // cardController.setSourceView("equipments"); // Set nếu cần
                            } else {
                                System.err.println("Lỗi: Không lấy được CardEquipmentController.");
                                continue;
                            }

                            addCardToCorrectHBox(equipment, cardNode);

                        } catch (IOException e) {
                            System.err.println("Lỗi khi load card FXML cho thiết bị: " + equipment.getModelName() + " - " + e.getMessage());
                            e.printStackTrace();
                        } catch (Exception e) {
                            System.err.println("Lỗi không xác định khi xử lý thiết bị: " + equipment.getModelName() + " - " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

            } catch (IOException e) {
                System.err.println("Lỗi khi gọi API lấy danh sách thiết bị: " + e.getMessage());
                e.printStackTrace();
                Platform.runLater(() -> showError("Lỗi kết nối hoặc xử lý dữ liệu thiết bị."));
            }
        }).start();
    }

    // Hàm ánh xạ từ Equipment sang CardInfoEquipment (ĐÃ CẬP NHẬT)
    private CardInfoEquipment mapEquipmentToCardInfo(Equipment equipment) {
        if (equipment == null) return null;
        CardInfoEquipment cardInfo = new CardInfoEquipment();
        cardInfo.setId(equipment.getId());
        cardInfo.setNameEquip(equipment.getModelName()); // Dùng modelName

        // Lấy đường dẫn ảnh từ imgModel
        String imgPath = equipment.getImgModel();
        if (imgPath != null && !imgPath.startsWith("/") && !imgPath.startsWith("http")) {
            // Giả định đường dẫn tương đối cần tiền tố, điều chỉnh nếu cần
            imgPath = "/com/utc2/facilityui/" + imgPath; // Ví dụ: /com/utc2/facilityui/images/models/epson_ebs41.jpg
        } else if (imgPath == null || imgPath.trim().isEmpty()) {
            imgPath = "/com/utc2/facilityui/images/default_equipment.png"; // Ảnh mặc định
        }
        cardInfo.setImgSrc(imgPath);

        // Lấy loại thiết bị làm vai trò
        cardInfo.setRoleManager(equipment.getTypeName() != null ? equipment.getTypeName() : "Chưa rõ loại");
        // API không trả về tên người quản lý cho thiết bị này
        cardInfo.setNameManager("N/A");

        return cardInfo;
    }

    // Hàm phân loại thiết bị vào HBox tương ứng (ĐÃ CẬP NHẬT)
    private void addCardToCorrectHBox(Equipment equipment, AnchorPane cardNode) {
        // Sử dụng typeName để phân loại
        String type = (equipment.getTypeName() != null) ? equipment.getTypeName().toLowerCase() : "";

        // --- CẬP NHẬT CÁC TỪ KHÓA PHÂN LOẠI CHO PHÙ HỢP ---
        // Ví dụ: Dựa trên JSON thì có "Máy chiếu"
        if (type.contains("điện tử") || type.contains("electronic") || type.contains("máy chiếu")) {
            cardElectronics.getChildren().add(cardNode);
        } else if (type.contains("âm thanh") || type.contains("audio") || type.contains("loa")|| type.contains("microphone")) {
            cardAudioEquipment.getChildren().add(cardNode);
        } else {
            // Nếu không khớp, thêm vào một mục mặc định (ví dụ Electronics)
            System.out.println("Loại thiết bị không xác định hoặc chưa xử lý: '" + equipment.getTypeName() + "'. Thêm vào Electronics.");
            cardElectronics.getChildren().add(cardNode);
        }
    }

    // Hàm hiển thị lỗi
    private void showError(String message) {
        if (mainEquipmentsContainer == null) {
            System.err.println("UI Error (main container null): " + message);
            return;
        }
        // Xóa lỗi cũ trước khi thêm lỗi mới
        mainEquipmentsContainer.getChildren().removeIf(node -> node.getStyleClass().contains("error-label"));
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 10px;");
        errorLabel.getStyleClass().add("error-label");
        mainEquipmentsContainer.getChildren().add(0, errorLabel); // Thêm vào đầu VBox
    }
}