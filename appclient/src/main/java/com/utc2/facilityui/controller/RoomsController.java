package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ButtonNav;
import com.utc2.facilityui.model.CardInfo;
import com.utc2.facilityui.model.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RoomsController implements Initializable {
    @FXML
    private HBox cardClassRoom;

    @FXML
    private HBox cardLabRoom;

    @FXML
    private HBox cardMeetingRoom;
    private List<CardInfo> classroom;
    private List<CardInfo> meetingroom;
    private List<CardInfo> labroom;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        classroom = new ArrayList<>(classroomLyAdded());
        meetingroom = new ArrayList<>(meetingLyAdded());
        labroom = new ArrayList<>(labLyAdded());
        try {
            for (CardInfo cards : classroom) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/card.fxml"));

                AnchorPane btn = fxmlLoader.load();
                CardController controller = fxmlLoader.getController();
                controller.setData(cards);

                cardClassRoom.getChildren().add(btn);
            }
            for (CardInfo value : meetingroom) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/card.fxml"));

                AnchorPane btn = fxmlLoader.load();
                CardController controller = fxmlLoader.getController();
                controller.setData(value);

                cardLabRoom.getChildren().add(btn);
            }
            for (CardInfo values : labroom) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/card.fxml"));

                AnchorPane btn = fxmlLoader.load();
                CardController controller = fxmlLoader.getController();
                controller.setData(values);

                cardMeetingRoom.getChildren().add(btn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private List<CardInfo> classroomLyAdded() {
        List<CardInfo> ls = new ArrayList<>();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);
        return ls;
    }
    private List<CardInfo> meetingLyAdded(){
        List<CardInfo> ls = new ArrayList<>();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);
        return ls;
    }
    private List<CardInfo> labLyAdded(){
        List<CardInfo> ls = new ArrayList<>();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("503DN");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);
        return ls;
    }
}
