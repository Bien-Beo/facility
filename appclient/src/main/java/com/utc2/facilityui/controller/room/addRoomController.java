package com.utc2.facilityui.controller.room;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class addRoomController implements Initializable {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField locationTextField;

    @FXML
    private TextField capacityTextField;

    @FXML
    private TextField imageURLTextField;

    @FXML
    private TextField buildingTextField; // Changed from ComboBox to TextField

    @FXML
    private TextField roomTypeTextField; // Changed from ComboBox to TextField

    @FXML
    private TextField managerTextField; // Changed from ComboBox to TextField

    @FXML
    private Button cancelButton;

    @FXML
    private Button addButton;

    public addRoomController() {
        System.out.println("AddRoomController constructor called!");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("AddRoomController initialize() called!");
        // No need for initializeComboBoxes() anymore
    }

    @FXML
    void handleAddRoom(ActionEvent event) {
        System.out.println("handleAddRoom() called!");

        String name = nameTextField.getText();
        String description = descriptionTextField.getText();
        String location = locationTextField.getText();
        String capacityText = capacityTextField.getText();
        String imageURL = imageURLTextField.getText();
        String building = buildingTextField.getText(); // Get text from TextField
        String roomType = roomTypeTextField.getText(); // Get text from TextField
        String manager = managerTextField.getText(); // Get text from TextField

        try {
            int capacity = Integer.parseInt(capacityText);

            // *** Example: Creating a Room object (replace with your actual object if needed) ***
            // Room newRoom = new Room(name, description, location, capacity, imageURL, building, roomType, manager);

            System.out.println("Adding room with details:");
            System.out.println("  Name: " + name);
            System.out.println("  Description: " + description);
            System.out.println("  Capacity: " + capacity);
            System.out.println("  Building: " + building);
            System.out.println("  Room Type: " + roomType);
            System.out.println("  Manager: " + manager);

            // *** Call your service to add the room to the database ***
            // roomService.addRoom(newRoom);

            closeStage();

        } catch (NumberFormatException e) {
            System.err.println("Invalid capacity: " + capacityText);
            // *** Consider showing an alert to the user ***
        } catch (Exception e) {
            System.err.println("Error adding room: " + e.getMessage());
            e.printStackTrace();
            // *** Consider showing an alert to the user ***
        }
    }

    @FXML
    void handleCancelAddRoom(ActionEvent event) {
        System.out.println("handleCancelAddRoom() called!");
        closeStage();
    }

    private void closeStage() {
        System.out.println("closeStage() called!");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}