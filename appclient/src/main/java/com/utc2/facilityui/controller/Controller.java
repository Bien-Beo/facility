package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ButtonNav;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private VBox putbtn;
    private List<ButtonNav> recentLyAdded;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recentLyAdded = recentLyAdded();  // Fix lỗi NullPointerException
        try {
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
