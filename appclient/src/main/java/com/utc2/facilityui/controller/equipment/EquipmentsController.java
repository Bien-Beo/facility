package com.utc2.facilityui.controller.equipment;

import com.utc2.facilityui.model.CardInfoEquipment;
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

    private List<CardInfoEquipment> electronics;
    private List<CardInfoEquipment> audioEquipment;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        electronics = new ArrayList<>(electronicsLyAdded());
        audioEquipment = new ArrayList<>(audioLyAdded());
        try {
            for (CardInfoEquipment cards : electronics) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/cardEquipment.fxml"));

                AnchorPane btn = fxmlLoader.load();
                CardEquipmentController controller = fxmlLoader.getController();
                controller.setData(cards);

                cardElectronics.getChildren().add(btn);
            }
            for (CardInfoEquipment value : audioEquipment) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/cardEquipment.fxml"));

                AnchorPane btn = fxmlLoader.load();
                CardEquipmentController controller = fxmlLoader.getController();
                controller.setData(value);

                cardAudioEquipment.getChildren().add(btn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private List<CardInfoEquipment> electronicsLyAdded() {
        List<CardInfoEquipment> ls = new ArrayList<>();
        CardInfoEquipment cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Bulb");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Bulb");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Bulb");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Bulb");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Bulb");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Bulb");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("a");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);
        return ls;
    }
    private List<CardInfoEquipment> audioLyAdded(){
        List<CardInfoEquipment> ls = new ArrayList<>();
        CardInfoEquipment cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Micro");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Micro");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Micro");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Micro");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Micro");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Micro");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        cardEquip = new CardInfoEquipment();
        cardEquip.setImgSrc("/com/utc2/facilityui/images/background.jpg");
        cardEquip.setNameCard("Micro");
        cardEquip.setRoleManager("Facility Manager");
        cardEquip.setNameManager("Bien");
        ls.add(cardEquip);

        return ls;
    }

}
