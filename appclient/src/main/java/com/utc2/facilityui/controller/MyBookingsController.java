package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CardAcceptBooking;
import com.utc2.facilityui.model.CardBooking;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class MyBookingsController implements Initializable {
    @FXML
    private VBox myBookings;
    private List<CardBooking> cardBooking;
    private List<CardAcceptBooking> cardAcceptBooking;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        
        cardBooking = new ArrayList<>(bookingLyAdded());
        cardAcceptBooking = new ArrayList<>(acceptBookingLyAdded());
        
        try {
            // Thêm cardBooking
            for (CardBooking bookings : cardBooking) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/cardBooking.fxml"));
                AnchorPane cardNode = fxmlLoader.load();
                
                // Set kích thước cho card
                cardNode.setPrefWidth(762.0);
                cardNode.setMaxWidth(Double.MAX_VALUE);
                
                CardBookingController controller = fxmlLoader.getController();
                controller.setData(bookings);
                myBookings.getChildren().add(cardNode);
            }

     
            for (CardAcceptBooking acceptBookings : cardAcceptBooking) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/utc2/facilityui/component/cardAcceptBooking.fxml"));
                AnchorPane cardNode = fxmlLoader.load();
                
                // Set kích thước cho card
                cardNode.setPrefWidth(762.0);
                cardNode.setMaxWidth(Double.MAX_VALUE);
                
                CardAcceptBookingController controller = fxmlLoader.getController();
                controller.setData(acceptBookings);
                myBookings.getChildren().add(cardNode);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<CardBooking> bookingLyAdded() {
        List<CardBooking> ls = new ArrayList<>();
        CardBooking cardBookings = new CardBooking();
        cardBookings.setNameBooking("Borrow Room 503DN");
        cardBookings.setPurposeBooking("Study");
        cardBookings.setDateBooking("Thu Apr 10 2025");
        cardBookings.setTimeBooking("13:00 PM - 05:30 PM");
        cardBookings.setRequestBooking("Wed Apr 2 2025, 11:58 AM");
        cardBookings.setStatusBooking("Pending Approval");
        ls.add(cardBookings);
        return ls;
    }

    private List<CardAcceptBooking> acceptBookingLyAdded() {
        List<CardAcceptBooking> ls = new ArrayList<>();
        CardAcceptBooking acceptBookings = new CardAcceptBooking();

        acceptBookings.setNameBooking("Borrow Room 501DN");
        acceptBookings.setPurposeBooking("Study");
        acceptBookings.setDateBooking("Thu Apr 10 2025");
        acceptBookings.setTimeBooking("13:00 PM - 05:30 PM");
        acceptBookings.setRequestBooking("Wed Apr 2 2025, 11:58 AM");
        acceptBookings.setStatusBooking("Pending Approval");
        acceptBookings.addApprovalByManager("Ngoc Bien at Tue Apr 1 2025, 12:06 PM"); // FM approval
        acceptBookings.addApprovalByManager("Nhat Tan at Tue Apr 1 2025, 10:06 PM"); // GD approval
        ls.add(acceptBookings);


        acceptBookings = new CardAcceptBooking();
        acceptBookings.setNameBooking("Borrow Room 501DN");
        acceptBookings.setPurposeBooking("Study");
        acceptBookings.setDateBooking("Thu Apr 10 2025");
        acceptBookings.setTimeBooking("13:00 PM - 05:30 PM");
        acceptBookings.setRequestBooking("Wed Apr 2 2025, 11:58 AM");
        acceptBookings.setStatusBooking("Pending Approval");
        acceptBookings.addApprovalByManager("Ngoc Bien at Tue Apr 1 2025, 12:06 PM");
        acceptBookings.addApprovalByManager("Nhat Tan at Tue Apr 1 2025, 10:06 PM");
        ls.add(acceptBookings);

        acceptBookings = new CardAcceptBooking();
        acceptBookings.setNameBooking("Borrow Room 501DN");
        acceptBookings.setPurposeBooking("Study");
        acceptBookings.setDateBooking("Thu Apr 10 2025");
        acceptBookings.setTimeBooking("13:00 PM - 05:30 PM");
        acceptBookings.setRequestBooking("Wed Apr 2 2025, 11:58 AM");
        acceptBookings.setStatusBooking("Pending Approval");
        acceptBookings.addApprovalByManager("Ngoc Bien at Tue Apr 1 2025, 12:06 PM");
        acceptBookings.addApprovalByManager("Nhat Tan at Tue Apr 1 2025, 10:06 PM");
        ls.add(acceptBookings);
        return ls;
    }
}
