package com.utc2.facilityui.controller.nav;

import com.utc2.facilityui.controller.room.InfoRoomController;
import com.utc2.facilityui.model.CardInfo;
// Giả sử bạn có lớp InfoRoomController trong package này hoặc import nó
// import com.utc2.facilityui.controller.room.InfoRoomController;
import com.utc2.facilityui.model.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.InputStream; // Cần import InputStream

public class Controller { // Có thể đổi tên thành CardController cho rõ nghĩa

    @FXML private Label nameManager;
    @FXML private Label roleManager;
    @FXML private ImageView roomImg;
    @FXML private Label nameRoom;
    @FXML private Button showInfo;
    @FXML private AnchorPane card; // Root pane của card

    private String currentView; // Tên của view chứa card này (ví dụ: "RoomsView")
    private CardInfo currentCardInfo; // Lưu lại thông tin card để có thể truyền đi
    private Room currentRoom;
    /**
     * Phương thức khởi tạo tự động khi FXML load.
     * Gán hành động cho button "showInfo".
     */
    @FXML
    public void initialize() {
        showInfo.setOnAction(event -> showInfoRoom());
    }

    /**Admin
     * Nhận dữ liệu CardInfo và cập nhật UI của card.
     * @param cardinfo Dữ liệu để hiển thị.
     */
    public void setData(CardInfo cardinfo, Room roomData) { // <<<--- SỬA SIGNATURE
        if (cardinfo == null || roomData == null) {
            System.err.println("setData nhận được CardInfo hoặc Room null!");
            card.setVisible(false);
            return;
        }
        card.setVisible(true);
        this.currentCardInfo = cardinfo;
        this.currentRoom = roomData; // <<<--- LƯU LẠI ROOM

        // Load ảnh từ CardInfo (giả định CardInfo có imgSrc)
        loadImage(cardinfo.getImgSrc());

        // Cập nhật các Label từ CardInfo
        nameRoom.setText(cardinfo.getNameCard() != null ? cardinfo.getNameCard() : "N/A");
        roleManager.setText(cardinfo.getRoleManager() != null ? cardinfo.getRoleManager() : "N/A");
        nameManager.setText(cardinfo.getNameManager() != null ? cardinfo.getNameManager() : "N/A");
    }
    /**
     * Lưu lại tên của view đã tạo ra card này.
     * Thông tin này dùng để quay lại từ màn hình chi tiết.
     * @param view Tên của view nguồn (ví dụ: "RoomsView").
     */
    public void setCurrentView(String view) {
        this.currentView = view;
    }

    /**
     * Tải và hiển thị ảnh lên ImageView.
     * Sử dụng ảnh mặc định nếu có lỗi.
     * @param imagePath Đường dẫn resource của ảnh.
     */
    private void loadImage(String imagePath) {
        Image image = null;
        // Xác định đường dẫn hiệu lực (dùng ảnh mặc định nếu path gốc null/rỗng)
        String effectivePath = (imagePath != null && !imagePath.trim().isEmpty()) ? imagePath : getDefaultImagePath();
        InputStream imageStream = null;

        try {
            imageStream = getClass().getResourceAsStream(effectivePath);
            if (imageStream != null) {
                image = new Image(imageStream);
                if (image.isError()) {
                    System.err.println("Lỗi khi tạo Image từ resource: " + effectivePath + " - " + image.getException());
                    image = null; // Đặt lại để thử tải ảnh mặc định
                }
            } else {
                System.err.println("Không tìm thấy resource ảnh: " + effectivePath);
                // image vẫn là null
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + effectivePath + " - " + e.getMessage());
            // image vẫn là null
        } finally {
            // Đảm bảo đóng stream nếu nó đã được mở
            if (imageStream != null) {
                try { imageStream.close(); } catch (Exception e) { System.err.println("Lỗi khi đóng imageStream: " + e.getMessage()); }
            }
        }

        // Nếu không tải được ảnh gốc (image vẫn null), thử tải ảnh mặc định
        if (image == null) {
            System.out.println("Đang tải ảnh mặc định thay cho: " + effectivePath);
            InputStream defaultStream = null;
            try {
                defaultStream = getClass().getResourceAsStream(getDefaultImagePath());
                if (defaultStream != null) {
                    image = new Image(defaultStream);
                    if(image.isError()){
                        System.err.println("Lỗi khi tạo Image mặc định: " + getDefaultImagePath() + " - " + image.getException());
                        image = null; // Không thể tải cả ảnh mặc định
                    }
                } else {
                    System.err.println("Không tìm thấy resource ảnh mặc định: " + getDefaultImagePath());
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải ảnh mặc định: " + getDefaultImagePath() + " - " + e.getMessage());
            } finally {
                // Đóng defaultStream
                if (defaultStream != null) {
                    try { defaultStream.close(); } catch (Exception e) { System.err.println("Lỗi khi đóng defaultStream: " + e.getMessage()); }
                }
            }
        }

        // Đặt ảnh cuối cùng lên ImageView (có thể là null nếu cả hai đều lỗi)
        roomImg.setImage(image);
        if (image == null) {
            System.err.println("Không thể hiển thị bất kỳ ảnh nào cho card.");
            // Có thể đặt một ảnh placeholder đơn giản hoặc màu nền cho roomImg ở đây
        }
    }

    /**
     * Trả về đường dẫn resource của ảnh mặc định.
     * @return Đường dẫn resource ảnh mặc định.
     */
    private String getDefaultImagePath() {
        // Đảm bảo đường dẫn này đúng trong cấu trúc project của bạn
        return "/com/utc2/facilityui/images/default_room.png";
    }


    /**
     * Xử lý sự kiện khi nhấn nút "Show Info".
     * Tải view infoRoom.fxml và hiển thị nó vào vùng mainCenter.
     */
    private void showInfoRoom() {
        try {
            // Tìm AnchorPane chính trong Scene chứa card này
            AnchorPane mainCenter = (AnchorPane) card.getScene().lookup("#mainCenter");
            if (mainCenter == null) {
                System.err.println("Không tìm thấy #mainCenter trong Scene.");
                // Có thể hiển thị thông báo lỗi cho người dùng
                return;
            }

            // Load FXML của màn hình thông tin chi tiết
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/form/infoRoom.fxml"));
            AnchorPane infoRoomView = loader.load(); // Tên biến rõ ràng hơn

            // Lấy controller của màn hình thông tin chi tiết
            InfoRoomController infoRoomController = loader.getController();
            if(infoRoomController == null) {
                System.err.println("Không thể lấy InfoRoomController từ infoRoom.fxml.");
                return;
            }
            // --- GỌI PHƯƠNG THỨC MỚI ĐỂ TRUYỀN DỮ LIỆU ---
            infoRoomController.setSourceView(this.currentView); // Vẫn truyền view nguồn
            infoRoomController.loadRoomDetails(this.currentRoom); // <<<--- TRUYỀN ĐỐI TƯỢNG ROOM

//            // --- TRUYỀN DỮ LIỆU CHO InfoRoomController ---
//
//            // 1. Truyền tên view nguồn để quay lại
//            infoRoomController.setSourceView(this.currentView);
//
//            // 2. Truyền dữ liệu của phòng hiện tại
//            if (this.currentCardInfo != null) {
//                // Giả sử InfoRoomController có phương thức setInitialData hoặc tương tự
//                // infoRoomController.setInitialData(this.currentCardInfo); // Truyền CardInfo
//                // Hoặc nếu CardInfo có ID và InfoRoomController cần ID để tự load:
//                // String roomId = this.currentCardInfo.getId(); // Giả sử CardInfo có getId()
//                // if (roomId != null) {
//                //      infoRoomController.loadRoomDetailsById(roomId);
//                // } else {
//                //      System.err.println("CardInfo không có ID để truyền.");
//                //      return; // Không chuyển màn hình nếu thiếu ID
//                // }
//                System.out.println(">>> Cần gọi phương thức trong InfoRoomController để truyền dữ liệu phòng: " + this.currentCardInfo.getNameCard());
//                // !!! BẠN CẦN THÊM VÀ GỌI PHƯƠNG THỨC PHÙ HỢP CỦA InfoRoomController Ở ĐÂY !!!
//
//            } else {
//                System.err.println("Không có thông tin CardInfo (currentCardInfo) để truyền cho InfoRoomController.");
//                return; // Không nên chuyển màn hình nếu không có dữ liệu
//            }
//            // --- KẾT THÚC TRUYỀN DỮ LIỆU ---


            // Thay thế nội dung của mainCenter bằng view mới
            mainCenter.getChildren().clear();
            mainCenter.getChildren().add(infoRoomView);

            // Đặt anchors để view mới fill mainCenter
            AnchorPane.setTopAnchor(infoRoomView, 0.0);
            AnchorPane.setBottomAnchor(infoRoomView, 0.0);
            AnchorPane.setLeftAnchor(infoRoomView, 0.0);
            AnchorPane.setRightAnchor(infoRoomView, 0.0);

        } catch (Exception e) {
            e.printStackTrace(); // In chi tiết lỗi ra console
            System.err.println("Lỗi khi tải hoặc hiển thị infoRoom.fxml: " + e.getMessage());
            // Hiển thị thông báo lỗi thân thiện cho người dùng nếu cần
        }
    }
}