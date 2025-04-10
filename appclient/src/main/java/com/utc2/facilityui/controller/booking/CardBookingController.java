package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardBooking;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.text.Text;

public class CardBookingController {
    @FXML
    private Button btnCancel;

    @FXML
    private Text dateBooking;

    @FXML
    private Label nameBooking;

    @FXML
    private Text purposeBooking;

    @FXML
    private Text requestBooking;

    @FXML
    private Text statusBooking;

    @FXML
    private Text timeBooking;
    public void setData(CardBooking cardbooking) {
        nameBooking.setText(cardbooking.getNameBooking());
        purposeBooking.setText(cardbooking.getPurposeBooking());
        dateBooking.setText(cardbooking.getDateBooking());
        timeBooking.setText(cardbooking.getTimeBooking());
        requestBooking.setText(cardbooking.getRequestBooking());
        statusBooking.setText(cardbooking.getStatusBooking());

    }
}
