package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardInfo;
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

public class EquipmentsController implements Initializable {
    @FXML
    private HBox cardElectronics;

    @FXML
    private HBox cardAudioEquipment;

    private List<CardInfo> electronics;
    private List<CardInfo> audioEquipment;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        electronics = new ArrayList<>(electronicsLyAdded());
        audioEquipment = new ArrayList<>(audioLyAdded());
        try {
            for (CardInfo cards : electronics) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/card.fxml"));

                AnchorPane btn = fxmlLoader.load();
                CardController controller = fxmlLoader.getController();
                controller.setData(cards);

                cardElectronics.getChildren().add(btn);
            }
            for (CardInfo value : audioEquipment) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/card.fxml"));

                AnchorPane btn = fxmlLoader.load();
                CardController controller = fxmlLoader.getController();
                controller.setData(value);

                cardAudioEquipment.getChildren().add(btn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private List<CardInfo> electronicsLyAdded() {
        List<CardInfo> ls = new ArrayList<>();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Bulb");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Bulb");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Bulb");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Bulb");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Bulb");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Bulb");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Bulb");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);
        return ls;
    }
    private List<CardInfo> audioLyAdded(){
        List<CardInfo> ls = new ArrayList<>();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Micro");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Micro");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Micro");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Micro");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Micro");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Micro");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);

        cardInfo = new CardInfo();
        cardInfo.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardInfo.setNameCard("Micro");
        cardInfo.setRoleManager("Facility Manager");
        cardInfo.setNameManager("Bien");
        ls.add(cardInfo);
        return ls;
    }

}
