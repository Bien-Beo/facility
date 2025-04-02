package com.utc2.facilityui.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utc2.facilityui.model.ButtonNav;
import com.utc2.facilityui.model.User;
import com.utc2.facilityui.response.ApiResponse;
import com.utc2.facilityui.service.UserServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private VBox putbtn;
    private List<ButtonNav> recentLyAdded;

    @FXML private Text namePerson;
    @FXML private Text idPerson;

    private final Gson gson = new Gson();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recentLyAdded = recentLyAdded();  // Fix lỗi NullPointerException
        try {
            // Load dữ liệu người dùng
            initDataUser();

            // Load các nút điều hướng
            for (ButtonNav btnNav : recentLyAdded) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/buttonNav.fxml"));

                AnchorPane btn = fxmlLoader.load();
                InfoPersonController controller = fxmlLoader.getController();
                controller.setData(btnNav);

                putbtn.getChildren().add(btn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDataUser() {
        try {
            String data = UserServices.getMyInfo();
            Type userType = new TypeToken<ApiResponse<User>>() {
            }.getType();
            ApiResponse<User> apiResponse = gson.fromJson(data, userType);
            User user = apiResponse.getResult();

            if (user == null || user.getUsername() == null) {
                System.out.println("Dữ liệu User không hợp lệ!");
                return;
            }

            namePerson.setText(user.getUsername());
            idPerson.setText(user.getUserId());
        } catch (IOException e) {
            System.out.println("Lỗi khi gọi API hoặc parse dữ liệu!");
            e.printStackTrace();
        }
    }

    private List<ButtonNav> recentLyAdded(){
        List<ButtonNav> ls = new ArrayList<>();
        ButtonNav btn = new ButtonNav();
        btn.setName("Rooms");
        btn.setImageSrc("/com/utc2/facilityui/images/medal.png");
        ls.add(btn);

        btn = new ButtonNav();
        btn.setName("Equipments");
        btn.setImageSrc("/com/utc2/facilityui/images/equipment.png");
        ls.add(btn);

        btn = new ButtonNav();
        btn.setName("My Bookings");
        btn.setImageSrc("/com/utc2/facilityui/images/booking.png");
        ls.add(btn);

        btn = new ButtonNav();
        btn.setName("Maintenance");
        btn.setImageSrc("/com/utc2/facilityui/images/myRequest.png");
        ls.add(btn);

        btn = new ButtonNav();
        btn.setName("Reset Password");
        btn.setImageSrc("/com/utc2/facilityui/images/password.png");
        ls.add(btn);

        btn = new ButtonNav();
        btn.setName("Logout");
        btn.setImageSrc("/com/utc2/facilityui/images/logout.png");
        ls.add(btn);
        return ls;
    }
}
