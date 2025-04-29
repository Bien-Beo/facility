package com.utc2.facilityui.controller.nav;

import com.utc2.facilityui.model.ButtonNav;
import com.utc2.facilityui.controller.nav.ButtonNavController;
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
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class InfoPersonController implements Initializable {

    @FXML private VBox putbtn;
    @FXML private ImageView imgPerson;
    @FXML private Text namePerson;
    @FXML private Text idPerson;

    private List<ButtonNav> navigationButtonModels;
    private static final String DEFAULT_AVATAR_PATH = "/com/utc2/facilityui/images/man.png";
    private static final String BUTTON_NAV_FXML_PATH = "/com/utc2/facilityui/component/buttonNav.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing InfoPersonController...");
        setUIToLoadingState();
        loadNavigationButtons();
        loadUserInfo();
    }

    private void setUIToLoadingState() {
        namePerson.setText("Loading...");
        idPerson.setText("ID: Loading...");
        setDefaultAvatar();
    }

    private void loadUserInfo() {
        System.out.println("Starting to load user info task...");
        Task<Map<String, Object>> loadUserTask = new Task<>() {
            @Override
            protected Map<String, Object> call() throws Exception {
                // Service trả về Map rỗng nếu lỗi logic/token/API-biz-error,
                // ném IOException nếu lỗi mạng/parse JSON
                return UserServices.getMyInfo();
            }
        };

        loadUserTask.setOnSucceeded(event -> {
            Map<String, Object> userMap = loadUserTask.getValue();
            System.out.println("User info task succeeded.");
            Platform.runLater(() -> {
                if (userMap != null && !userMap.isEmpty()) {
                    System.out.println("Updating UI with user info: " + userMap.keySet());
                    updateUserInfoUI(userMap);
                } else {
                    // Service đã trả về map rỗng, không cần báo lỗi lớn ở đây
                    System.out.println("User info map is null or empty after task completion (check previous logs for reason).");
                    setUIToDefaultOrError("No Data", "ID: N/A", true);
                }
            });
        });

        loadUserTask.setOnFailed(event -> {
            Throwable exception = loadUserTask.getException(); // Lỗi IOException từ service
            System.err.println("User info task failed with exception: " + exception.getMessage());
            exception.printStackTrace();
            Platform.runLater(() -> {
                setUIToDefaultOrError("Error", "ID: Error", true);
                // Hiển thị lỗi rõ ràng hơn cho người dùng
                String errorType = (exception instanceof java.net.ConnectException) ? "Cannot connect to server." :
                        (exception.getMessage().contains("parse")) ? "Invalid data from server." :
                                "Network or system error.";
                showErrorAlert("Load User Info Error", errorType + "\nDetails: " + exception.getMessage());
            });
        });

        System.out.println("Starting user info loading thread...");
        Thread backgroundThread = new Thread(loadUserTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    private void updateUserInfoUI(Map<String, Object> userMap) {
        String fullName = getStringValueFromMap(userMap, "fullName", "N/A");
        namePerson.setText(fullName);

        String userId = "N/A";
        if (userMap.containsKey("userId") && userMap.get("userId") != null) {
            Object userIdObj = userMap.get("userId");
            if (userIdObj instanceof String) {
                userId = (String) userIdObj;
            } else if (userIdObj instanceof Number) {
                userId = String.format("%.0f", ((Number)userIdObj).doubleValue());
            } else {
                userId = userIdObj.toString();
                System.out.println("WARN: updateUserInfoUI - 'userId' was not String or Number, used toString().");
            }
        } else {
            System.out.println("WARN: updateUserInfoUI - 'userId' key missing or null.");
        }
        idPerson.setText("ID: " + userId);

        String avatarUrl = getStringValueFromMap(userMap, "avatar", null);
        updateAvatarImage(avatarUrl);
    }

    private void updateAvatarImage(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            System.out.println("Attempting to load avatar from URL: " + avatarUrl);
            try {
                Image avatarImage = new Image(avatarUrl, true);
                avatarImage.errorProperty().addListener((obs, wasError, isError) -> {
                    if (isError) {
                        System.err.println("Failed to load avatar image from URL: " + avatarUrl + ". Reason: " + avatarImage.getException());
                        Platform.runLater(this::setDefaultAvatar);
                    }
                });
                avatarImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() == 1.0 && !avatarImage.isError()) {
                        System.out.println("Avatar loaded successfully: " + avatarUrl);
                    }
                });
                imgPerson.setImage(avatarImage);
            } catch (Exception e) {
                System.err.println("Error loading avatar image: " + e.getMessage() + " URL: " + avatarUrl);
                setDefaultAvatar();
            }
        } else {
            System.out.println("Avatar URL is missing or empty, setting default avatar.");
            setDefaultAvatar();
        }
    }

    private String getStringValueFromMap(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        if (value != null) {
            System.out.println("WARN: getStringValueFromMap - Key '" + key + "' exists but is not a String (Type: " + value.getClass().getName() + ").");
        }
        return defaultValue;
    }

    private void setUIToDefaultOrError(String nameText, String idText, boolean useDefaultAvatar) {
        namePerson.setText(nameText);
        idPerson.setText(idText);
        if (useDefaultAvatar) {
            setDefaultAvatar();
        }
        System.out.println("UI set to default/error state - Name: " + nameText + ", ID: " + idText);
    }

    private void setDefaultAvatar() {
        try (InputStream stream = getClass().getResourceAsStream(DEFAULT_AVATAR_PATH)) {
            if (stream == null) {
                System.err.println("CRITICAL: Cannot find default avatar resource: " + DEFAULT_AVATAR_PATH);
                imgPerson.setImage(null); return;
            }
            Image defaultImage = new Image(stream);
            imgPerson.setImage(defaultImage);
            System.out.println("Default avatar set.");
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to load default avatar image: " + e.getMessage());
            imgPerson.setImage(null); e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadNavigationButtons() {
        System.out.println("Loading navigation buttons...");
        navigationButtonModels = createNavigationButtonModels();
        putbtn.getChildren().clear();
        try {
            for (ButtonNav btnModel : navigationButtonModels) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL buttonFxmlUrl = getClass().getResource(BUTTON_NAV_FXML_PATH);
                if (buttonFxmlUrl == null) {
                    System.err.println("CRITICAL: Cannot find FXML for navigation button: " + BUTTON_NAV_FXML_PATH); continue;
                }
                fxmlLoader.setLocation(buttonFxmlUrl);
                try {
                    AnchorPane buttonPane = fxmlLoader.load();
                    ButtonNavController controller = fxmlLoader.getController();
                    if (controller != null) {
                        controller.setData(btnModel);
                        putbtn.getChildren().add(buttonPane);
                    } else {
                        System.err.println("CRITICAL: ButtonNavController is null for FXML: " + BUTTON_NAV_FXML_PATH);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading FXML for button '" + btnModel.getName() + "': " + e.getMessage());
                }
            }
            System.out.println("Navigation buttons loaded.");
        } catch (Exception e) {
            System.err.println("Unexpected error during navigation button loading: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Critical UI Error", "Failed to load navigation components.");
        }
    }

    private List<ButtonNav> createNavigationButtonModels() {
        List<ButtonNav> ls = new ArrayList<>();
        ls.add(new ButtonNav("Rooms", "/com/utc2/facilityui/images/medal-outline-icon.png"));
        ls.add(new ButtonNav("Equipments", "/com/utc2/facilityui/images/light-bulb.png"));
        ls.add(new ButtonNav("My Bookings", "/com/utc2/facilityui/images/List-Check-icon.png"));
        ls.add(new ButtonNav("Reset Password", "/com/utc2/facilityui/images/rotation-lock.png"));
        ls.add(new ButtonNav("Logout", "/com/utc2/facilityui/images/logout-icon.png"));
        return ls;
    }
}