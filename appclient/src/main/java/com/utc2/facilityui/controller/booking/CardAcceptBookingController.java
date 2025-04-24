package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardAcceptBooking;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class CardAcceptBookingController {
    @FXML
    private Text approvalByFM;
//
    @FXML
    private Text approvalByGD;

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
    public void setData(CardAcceptBooking cardAcceptBooking) {
        nameBooking.setText(cardAcceptBooking.getNameBooking());
        purposeBooking.setText(cardAcceptBooking.getPurposeBooking());
        dateBooking.setText(cardAcceptBooking.getDateBooking());
        timeBooking.setText(cardAcceptBooking.getTimeBooking());
        requestBooking.setText(cardAcceptBooking.getRequestBooking());
        statusBooking.setText(cardAcceptBooking.getStatusBooking());
        approvalByGD.setText(cardAcceptBooking.getApprovalByGD());
        approvalByFM.setText(cardAcceptBooking.getApprovalByFM());
    }
}
