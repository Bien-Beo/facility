package com.utc2.facilityui.controller.nav;

import com.utc2.facilityui.model.ButtonNav;
import com.utc2.facilityui.model.User; // << THÊM IMPORT CHO USER MODEL
import com.utc2.facilityui.service.UserServices;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text; // Hoặc Label, tùy FXML

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class InfoPersonController implements Initializable {

    @FXML private VBox putbtn;
    @FXML private ImageView avatar;
    @FXML private Text namePerson; // Hoặc Label
    @FXML private Text idPerson;   // Hoặc Label

    private List<ButtonNav> navigationButtonModels;
    private static final String DEFAULT_AVATAR_PATH = "/com/utc2/facilityui/images/man.png";
    private static final String BUTTON_NAV_FXML_PATH = "/com/utc2/facilityui/component/buttonNav.fxml";
    // Bỏ CLASSPATH_AVATAR_BASE_PREFIX nếu avatar từ API luôn là URL đầy đủ hoặc tên file đơn giản
    // Hoặc giữ lại nếu bạn có quy ước đường dẫn phức tạp hơn cho avatar lưu trữ nội bộ.
    // private static final String CLASSPATH_AVATAR_BASE_PREFIX = "/com/utc2/facilityui/images/";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing InfoPersonController...");

        // Bo tròn avatar
        if (avatar != null) { // Kiểm tra null trước khi dùng
            double fitWidth = avatar.getFitWidth(); // Lấy kích thước thực tế từ FXML hoặc default
            double fitHeight = avatar.getFitHeight();
            if (fitWidth <= 0) fitWidth = 80.0; // Giá trị mặc định nếu FXML không set
            if (fitHeight <= 0) fitHeight = 73.0; // Giá trị mặc định

            double radius = Math.min(fitWidth, fitHeight) / 2.0;
            Circle clip = new Circle(fitWidth / 2.0, fitHeight / 2.0, radius);
            avatar.setClip(clip);
        }

        setUIToLoadingState();
        loadNavigationButtons();
        loadUserInfo();
    }

    private void setUIToLoadingState() {
        if (namePerson != null) namePerson.setText("Đang tải...");
        if (idPerson != null) idPerson.setText("ID: Đang tải...");
        updateAvatarImage(null); // Tải avatar mặc định
    }

    private void loadUserInfo() {
        System.out.println("InfoPersonController: Starting to load user info task...");
        // THAY ĐỔI KIỂU CỦA TASK THÀNH User
        Task<User> loadUserTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                // Giả định UserServices.getMyInfo() giờ đây trả về User object
                return UserServices.getMyInfo();
            }
        };

        loadUserTask.setOnSucceeded(event -> {
            // THAY ĐỔI KIỂU CỦA BIẾN NHẬN KẾT QUẢ
            User user = loadUserTask.getValue(); // DÒNG 82 SẼ LÀ ĐÂY (hoặc gần đó)
            System.out.println("InfoPersonController: User info task succeeded.");
            Platform.runLater(() -> {
                if (user != null && user.getUserId() != null && !user.getUserId().trim().isEmpty()) { // Kiểm tra user và userId
                    System.out.println("InfoPersonController: Updating UI with user: " + user.getUsername());
                    updateUserInfoUI(user); // << TRUYỀN ĐỐI TƯỢNG User
                } else {
                    System.out.println("InfoPersonController: User object or critical fields (like userId) are null or empty.");
                    setUIToDefaultOrError("Không có dữ liệu", "ID: Lỗi", true);
                }
            });
        });

        loadUserTask.setOnFailed(event -> {
            Throwable exception = loadUserTask.getException();
            System.err.println("InfoPersonController: User info task failed: " + exception.getMessage());
            exception.printStackTrace();
            Platform.runLater(() -> {
                setUIToDefaultOrError("Lỗi tải dữ liệu", "ID: Lỗi", true);
                String errorType = (exception instanceof java.net.ConnectException) ? "Không thể kết nối server." :
                        (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("parse")) ? "Dữ liệu không hợp lệ từ server." :
                                "Lỗi mạng hoặc hệ thống.";
                showErrorAlert("Lỗi Tải Thông Tin Người Dùng", errorType + "\nChi tiết: " + exception.getMessage());
            });
        });

        System.out.println("InfoPersonController: Starting user info loading thread...");
        Thread backgroundThread = new Thread(loadUserTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    /**
     * Cập nhật các thành phần UI với dữ liệu từ đối tượng User.
     * @param user Đối tượng User chứa dữ liệu (đã kiểm tra không null và có userId).
     */
    private void updateUserInfoUI(User user) {
        // Lớp User của bạn có getUsername().
        // Nếu bạn muốn hiển thị "fullName" và nó có trong User object (sau khi UserServices.getMyInfo được cập nhật),
        // bạn có thể dùng user.getFullName(). Hiện tại dùng getUsername().
        String displayName = user.getUsername() != null ? user.getUsername() : "N/A";
        if (namePerson != null) namePerson.setText(displayName);

        // Model User của bạn có getUserId() và getId().
        // Hãy dùng trường ID phù hợp mà bạn muốn hiển thị.
        String displayId = user.getUserId() != null ? user.getUserId() : "N/A";
        if (idPerson != null) idPerson.setText("ID: " + displayId);

        // Lấy avatar từ đối tượng User
        String avatarIdentifier = user.getAvatar(); // avatarIdentifier có thể là URL hoặc tên file
        System.out.println("InfoPersonController: Định danh avatar nhận được từ User object: " + avatarIdentifier);
        updateAvatarImage(avatarIdentifier);
    }

    private void updateAvatarImage(String avatarIdentifier) {
        if (avatar == null) return; // Đảm bảo ImageView avatar đã được inject

        Image imageToSet = null;
        if (avatarIdentifier != null && !avatarIdentifier.trim().isEmpty()) {
            String path = avatarIdentifier.trim();
            if (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")) {
                System.out.println("InfoPersonController: Avatar is an HTTP URL: " + path + ". Loading from network.");
                loadAvatarFromHttpURL_Internal(path); // Phương thức này sẽ tự set image hoặc default
                return; // Việc set image sẽ được xử lý bởi loadAvatarFromHttpURL_Internal
            } else {
                // Giả sử avatarIdentifier là tên file trong một thư mục cố định trên classpath
                // Hoặc nếu nó là đường dẫn đầy đủ trên classpath (bắt đầu bằng "/")
                String classpathResourcePath = path.startsWith("/") ? path : "/com/utc2/facilityui/images/" + path; // Điều chỉnh base path nếu cần
                System.out.println("InfoPersonController: Attempting to load avatar from classpath: " + classpathResourcePath);
                imageToSet = loadImageFromClasspath(classpathResourcePath);
                if (imageToSet != null) {
                    System.out.println("InfoPersonController: Successfully loaded avatar from classpath: " + classpathResourcePath);
                } else {
                    System.err.println("InfoPersonController: Failed to load avatar from classpath: " + classpathResourcePath);
                }
            }
        }

        if (imageToSet == null) { // Nếu không tải được từ avatarIdentifier (hoặc nó null/empty)
            System.out.println("InfoPersonController: Avatar from API failed or not provided. Loading default avatar.");
            imageToSet = loadImageFromClasspath(DEFAULT_AVATAR_PATH);
            if (imageToSet != null) {
                System.out.println("InfoPersonController: Successfully loaded default avatar: " + DEFAULT_AVATAR_PATH);
            } else {
                System.err.println("InfoPersonController CRITICAL: Failed to load default avatar from: " + DEFAULT_AVATAR_PATH);
            }
        }

        avatar.setImage(imageToSet); // Set ảnh (có thể là null nếu cả hai đều thất bại)
        if (imageToSet == null) {
            System.err.println("InfoPersonController: Could not load any avatar (API or default). ImageView will be empty or show previous image if not cleared.");
        }
    }

    private Image loadImageFromClasspath(String classpathPath) {
        if (classpathPath == null || classpathPath.trim().isEmpty()) {
            System.err.println("InfoPersonController: Classpath for image is null or empty.");
            return null;
        }
        try (InputStream imageStream = getClass().getResourceAsStream(classpathPath)) {
            if (imageStream != null) {
                Image image = new Image(imageStream);
                if (image.isError()) {
                    System.err.println("InfoPersonController: Error creating Image object from classpath: " + classpathPath + (image.getException() != null ? " - " + image.getException().getMessage() : ""));
                    return null;
                }
                return image;
            } else {
                System.err.println("InfoPersonController: Cannot find resource on classpath: " + classpathPath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("InfoPersonController: Exception while loading image from classpath: " + classpathPath);
            e.printStackTrace();
            return null;
        }
    }

    private void loadAvatarFromHttpURL_Internal(String avatarUrl) {
        if (avatar == null) return;
        try {
            // true để tải nền, false để tải đồng bộ (có thể treo UI nếu mạng chậm)
            Image networkImage = new Image(avatarUrl, true);
            avatar.setImage(networkImage); // Set ngay, Image sẽ tự cập nhật khi tải xong hoặc lỗi

            networkImage.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    System.err.println("InfoPersonController: Error loading avatar from HTTP URL: " + avatarUrl +
                            (networkImage.getException() != null ? ". Reason: " + networkImage.getException().getMessage() : ". Unknown error."));
                    Platform.runLater(() -> updateAvatarImage(null)); // Thử tải lại default avatar nếu lỗi
                }
            });

            // Không cần progress listener nếu Image tự xử lý, nhưng nếu muốn có thể thêm:
            // networkImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
            //     if (newProgress.doubleValue() == 1.0 && !networkImage.isError()) {
            //         System.out.println("InfoPersonController: Successfully loaded avatar from HTTP URL: " + avatarUrl);
            //         // avatar.setImage(networkImage); // Đã set ở trên
            //     }
            // });

            // Kiểm tra lỗi ngay sau khi tạo đối tượng Image (cho các URL không hợp lệ tức thì)
            if (networkImage.isError()) {
                System.err.println("InfoPersonController: Immediate error after creating Image from HTTP URL: " + avatarUrl +
                        (networkImage.getException() != null ? ". Reason: " + networkImage.getException().getMessage() : ". Unknown error."));
                Platform.runLater(() -> updateAvatarImage(null));
            }
        } catch (IllegalArgumentException e) { // Nếu URL sai định dạng ngay từ đầu
            System.err.println("InfoPersonController: Invalid HTTP URL format for avatar: " + avatarUrl + " - " + e.getMessage());
            Platform.runLater(() -> updateAvatarImage(null));
        } catch (Exception e) { // Các lỗi không mong muốn khác
            System.err.println("InfoPersonController: Unexpected error creating Image from HTTP URL: " + avatarUrl);
            e.printStackTrace();
            Platform.runLater(() -> updateAvatarImage(null));
        }
    }

    // Bỏ getStringValueFromMap vì không dùng trực tiếp nữa khi đã có User object
    // private String getStringValueFromMap(Map<String, Object> map, String key, String defaultValue) { ... }

    private void setUIToDefaultOrError(String nameText, String idText, boolean tryLoadDefaultAvatar) {
        if (namePerson != null) namePerson.setText(nameText);
        if (idPerson != null) idPerson.setText(idText);
        if (tryLoadDefaultAvatar) {
            updateAvatarImage(null); // Sẽ tự động tải default avatar
        }
        System.out.println("InfoPersonController: UI set to default/error state - Name: " + nameText + ", ID: " + idText);
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadNavigationButtons() {
        System.out.println("InfoPersonController: Loading navigation buttons...");
        navigationButtonModels = createNavigationButtonModels();
        if (putbtn == null) {
            System.err.println("InfoPersonController CRITICAL: VBox 'putbtn' is null. Cannot add navigation buttons.");
            return;
        }
        putbtn.getChildren().clear();
        try {
            for (ButtonNav btnModel : navigationButtonModels) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(BUTTON_NAV_FXML_PATH));
                if (fxmlLoader.getLocation() == null) {
                    System.err.println("InfoPersonController CRITICAL: Cannot find FXML for navigation button: " + BUTTON_NAV_FXML_PATH);
                    continue;
                }
                AnchorPane buttonPane = fxmlLoader.load();
                ButtonNavController controller = fxmlLoader.getController();
                if (controller != null) {
                    controller.setData(btnModel); // setData của ButtonNavController
                    putbtn.getChildren().add(buttonPane);
                } else {
                    System.err.println("InfoPersonController CRITICAL: ButtonNavController is null for FXML: " + BUTTON_NAV_FXML_PATH);
                }
            }
            System.out.println("InfoPersonController: Navigation buttons loaded.");
        } catch (Exception e) {
            System.err.println("InfoPersonController: Unexpected error during navigation button loading: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Lỗi Giao Diện Nghiêm Trọng", "Không thể tải các thành phần điều hướng.");
        }
    }

    private List<ButtonNav> createNavigationButtonModels() {
        List<ButtonNav> ls = new ArrayList<>();
        ls.add(new ButtonNav("Phòng", "/com/utc2/facilityui/images/medal-outline-icon.png"));
        ls.add(new ButtonNav("Đặt phòng của tôi", "/com/utc2/facilityui/images/List-Check-icon.png"));
        ls.add(new ButtonNav("Thông báo", "/com/utc2/facilityui/images/notification.png"));
        ls.add(new ButtonNav("Đặt lại mật khẩu", "/com/utc2/facilityui/images/rotation-lock.png"));
        ls.add(new ButtonNav("Đăng xuất", "/com/utc2/facilityui/images/logout-icon.png"));
        return ls;
    }
}