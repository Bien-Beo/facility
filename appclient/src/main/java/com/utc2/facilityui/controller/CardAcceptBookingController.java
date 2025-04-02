package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardAcceptBooking;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.List;

public class CardAcceptBookingController {
    @FXML
    private Text approvalByFM;

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
        
        List<String> approvals = cardAcceptBooking.getApprovalsByManager();
        if (approvals != null && !approvals.isEmpty()) {

            approvalByFM.setText(approvals.get(0));

            if (approvals.size() > 1) {
                approvalByGD.setText(approvals.get(1));
            }
        }
    }
}
