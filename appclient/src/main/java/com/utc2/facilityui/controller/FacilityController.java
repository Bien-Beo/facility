package com.utc2.facilityui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.utc2.facilityui.model.Facility;

public class FacilityController {

    @FXML
    private TableView<Facility> facilityTable;
    @FXML
    private TableColumn<Facility, String> nameColumn;
    @FXML
    private TableColumn<Facility, String> capacityColumn;
    @FXML
    private TableColumn<Facility, String> typeRoomColumn;
    @FXML
    private TableColumn<Facility, String> statusColumn;
    @FXML
    private TableColumn<Facility, String> createdAtColumn;
    @FXML
    private TableColumn<Facility, String> updatedAtColumn;
    @FXML
    private TableColumn<Facility, String> deletedAtColumn;
    @FXML
    private TableColumn<Facility, String> managerColumn;
    @FXML
    private Button addButton;

    private ObservableList<Facility> facilities;

    @FXML
    private void initialize() {
        facilities = FXCollections.observableArrayList(
                new Facility(
                        "Courtyard / Harmony Plaza",
                        "80",
                        "Outdoor Area",
                        "Active",
                        "09:02 PM\nWed Dec 20 2023",
                        "09:07 PM\nWed Dec 20 2023",
                        "N/A",
                        "Grady Turcotte",
                        "395003"
                ),
                new Facility(
                        "Meeting Rooms / SkyRise Tower",
                        "50",
                        "Meeting Room",
                        "Active",
                        "11:16 AM\nSun Oct 06 2024",
                        "09:08 PM\nWed Dec 20 2023",
                        "01:24 PM\nSun Jun 16 2024",
                        "Roland Miller",
                        "374546"
                )
        );

        // Mapping các cột trong TableView
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        capacityColumn.setCellValueFactory(cell -> cell.getValue().capacityProperty());
        typeRoomColumn.setCellValueFactory(cell -> cell.getValue().typeRoomProperty());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());
        createdAtColumn.setCellValueFactory(cell -> cell.getValue().createdAtProperty());
        updatedAtColumn.setCellValueFactory(cell -> cell.getValue().updatedAtProperty());
        deletedAtColumn.setCellValueFactory(cell -> cell.getValue().deletedAtProperty());

        managerColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getManagerName() + "\nId:" + cell.getValue().getManagerId()
                )
        );

        facilityTable.setItems(facilities);
    }
}
