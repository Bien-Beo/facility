package com.utc2.facilityui.controller.room;

import com.utc2.facilityui.controller.nav.Controller;
import com.utc2.facilityui.model.CardInfo;
import com.utc2.facilityui.model.Room; // Import model Room
import com.utc2.facilityui.service.RoomService; // Import RoomService
import javafx.application.Platform; // Import Platform
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label; // Import Label để hiển thị lỗi
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; // Có thể dùng VBox để chứa Label lỗi

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RoomsController implements Initializable {
    @FXML private HBox cardClassRoom;
    @FXML private HBox cardLabRoom;
    @FXML private HBox cardMeetingRoom;
    @FXML private VBox mainContainer; // Container chính để có thể thêm thông báo lỗi

    private RoomService roomService;

    // Giả sử card.fxml có controller tên là CardController (thay vì Controller)
    // và có phương thức setData(CardInfo info)
    // import com.utc2.facilityui.controller.CardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roomService = new RoomService(); // Khởi tạo service
        loadAndRenderRooms(); // Gọi phương thức tải và hiển thị dữ liệu
    }

    private void loadAndRenderRooms() {
        // Xóa các card cũ trước khi tải mới
        cardClassRoom.getChildren().clear();
        cardLabRoom.getChildren().clear();
        cardMeetingRoom.getChildren().clear();
        // Xóa các thông báo lỗi cũ (nếu có)
        mainContainer.getChildren().removeIf(node -> node.getStyleClass().contains("error-label"));


        // Thực hiện gọi API và cập nhật UI trên luồng JavaFX Application Thread
        // nếu RoomService thực hiện công việc trong một luồng khác (hiện tại nó đồng bộ)
        // Nếu RoomService là đồng bộ và chạy trên luồng chính, không cần Platform.runLater
        // Nhưng để an toàn và chuẩn bị cho các thao tác bất đồng bộ sau này, nên dùng:
        // new Thread(() -> { // Chạy việc gọi mạng ở luồng riêng để không block UI
        try {
            List<Room> rooms = roomService.getRooms(); // Gọi service để lấy dữ liệu

            // Cập nhật UI phải được thực hiện trên JavaFX Application Thread
            Platform.runLater(() -> {
                if (rooms == null || rooms.isEmpty()) {
                    showError("Không tải được danh sách phòng hoặc danh sách trống.");
                    return;
                }

                for (Room room : rooms) {
                    try {
                        // 1. Ánh xạ Room sang CardInfo (vẫn cần nếu card dùng CardInfo)
                        CardInfo cardInfo = mapRoomToCardInfo(room); // Giả sử hàm này vẫn tồn tại

                        // 2. Load FXML card
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/card.fxml"));
                        AnchorPane cardNode = fxmlLoader.load();

                        // 3. Lấy controller của card và set data
                        Controller cardController = fxmlLoader.getController(); // Dùng tên Controller.java
                        if (cardController != null) {
                            // <<<--- TRUYỀN CẢ cardInfo VÀ room VÀO setData --->>>
                            cardController.setData(cardInfo, room);
                            // <<<--- Đừng quên gọi setCurrentView nếu cần --->>>
                            cardController.setCurrentView("rooms"); // Hoặc tên view hiện tại của bạn
                        } else {
                            System.err.println("Không thể lấy được controller cho card.fxml");
                            continue;
                        }

                        // 4. Phân loại và thêm card vào HBox phù hợp
                        addCardToCorrectHBox(room.getRoomTypeName(), cardNode);

                    } catch (IOException e) {
                        System.err.println("Lỗi khi load card FXML cho phòng: " + room.getName() + " - " + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("Lỗi không xác định khi xử lý phòng: " + room.getName() + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            // Lỗi kết nối hoặc đọc dữ liệu từ API
            System.err.println("Lỗi khi gọi API lấy danh sách phòng: " + e.getMessage());
            e.printStackTrace();
            // Hiển thị lỗi trên UI (phải dùng Platform.runLater)
            Platform.runLater(() -> showError("Lỗi kết nối tới máy chủ hoặc xử lý dữ liệu: " + e.getMessage()));
        }
        // }).start(); // Bắt đầu luồng nếu bạn chạy gọi mạng bất đồng bộ

    }

    // Hàm ánh xạ dữ liệu từ Room sang CardInfo
    private CardInfo mapRoomToCardInfo(Room room) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setNameCard(room.getName()); // Tên phòng

        // Lấy tên quản lý, nếu null thì hiển thị "Chưa có"
        cardInfo.setNameManager(room.getNameFacilityManager() != null ? room.getNameFacilityManager() : "Chưa có");

        // Vai trò có thể lấy từ roomTypeName hoặc giữ cố định
        cardInfo.setRoleManager(room.getRoomTypeName() != null ? room.getRoomTypeName() : "Phòng");
        // Hoặc giữ cố định: cardInfo.setRoleManager("Facility Manager");

        // Xử lý đường dẫn ảnh
        String imagePathApi = room.getImg();
        String finalImagePath;
        if (imagePathApi != null && !imagePathApi.trim().isEmpty()) {
            // Giả sử API trả về đường dẫn tương đối như "rooms/304dn.png"
            // và bạn muốn load từ resources trong project
            // Đường dẫn tuyệt đối trong resources sẽ là "/com/utc2/facilityui/images/rooms/304dn.png"
            // Cần điều chỉnh tiền tố "/com/utc2/facilityui/images/" cho phù hợp cấu trúc project
            if (!imagePathApi.startsWith("/")) { // Nếu không phải đường dẫn tuyệt đối resource
                finalImagePath = "/com/utc2/facilityui/images/" + imagePathApi; // Thêm tiền tố resource
            } else {
                finalImagePath = imagePathApi; // Giữ nguyên nếu đã là đường dẫn resource tuyệt đối
            }

            // Kiểm tra xem resource có tồn tại không (tuỳ chọn, có thể làm chậm)
            // URL checkUrl = getClass().getResource(finalImagePath);
            // if (checkUrl == null) {
            //     System.err.println("Ảnh không tồn tại: " + finalImagePath);
            //     finalImagePath = getDefaultImagePath(); // Dùng ảnh mặc định nếu không tìm thấy
            // }
        } else {
            // Nếu API không trả về ảnh, dùng ảnh mặc định
            finalImagePath = getDefaultImagePath();
        }
        cardInfo.setImgSrc(finalImagePath);

        // Bạn có thể thêm các thông tin khác từ Room vào CardInfo nếu cần
        // cardInfo.setSomeOtherField(room.getSomeOtherField());

        return cardInfo;
    }

    // Hàm trả về đường dẫn ảnh mặc định
    private String getDefaultImagePath() {
        // Đặt đường dẫn đến ảnh mặc định trong resources của bạn
        return "/com/utc2/facilityui/images/default_room.png";
    }

    // Hàm thêm card vào HBox phù hợp dựa trên loại phòng
    private void addCardToCorrectHBox(String roomTypeName, AnchorPane cardNode) {
        String type = (roomTypeName != null) ? roomTypeName.trim().toLowerCase() : "";

        if (type.contains("phòng học") ||type.contains("giảng đường")) {
            cardClassRoom.getChildren().add(cardNode);
        } else if (type.contains("lab") || type.contains("thí nghiệm") || type.contains("phòng thực hành")) {
            cardLabRoom.getChildren().add(cardNode);
        } else if (type.contains("họp") || type.contains("meeting") || type.contains("hội trường")) {
            cardMeetingRoom.getChildren().add(cardNode);
        } else {
            // Nếu không khớp loại nào, có thể thêm vào một HBox "Khác" hoặc mặc định vào phòng học
            System.out.println("Loại phòng không xác định: '" + roomTypeName + "'. Thêm vào Phòng học.");
            cardClassRoom.getChildren().add(cardNode);
        }
    }

    // Hàm hiển thị thông báo lỗi trên UI
    private void showError(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 10px;"); // CSS cho label lỗi
        errorLabel.getStyleClass().add("error-label"); // Thêm style class để dễ dàng xóa đi sau này
        // Thêm vào đầu container chính hoặc một vị trí thích hợp khác
        if (mainContainer != null) {
            mainContainer.getChildren().add(0, errorLabel); // Thêm vào đầu VBox
        } else {
            // Nếu không có mainContainer, tìm cách khác để hiển thị lỗi
            System.err.println("UI Error: " + message);
        }
    }


}