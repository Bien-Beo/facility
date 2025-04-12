package com.utc2.facilityui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
//
public abstract class BaseReportController {
    @FXML protected Button bntAdd;
    @FXML protected Button bntCancel;
    @FXML protected TextArea description;
    @FXML protected Label name;
    @FXML protected Label userID;

    protected abstract void handleAdd();

    public Button getBntCancel() {
        return bntCancel;
    }

    public Button getBntAdd() {
        return bntAdd;
    }

    public Label getName() {
        return name;
    }

    public TextArea getDescription() {
        return description;
    }

    public Label getUserID() {
        return userID;
    }

    protected void closeDialog() {
        Stage stage = (Stage) bntCancel.getScene().getWindow();
        stage.close();
    }
    protected void addDiaLog(){
        Stage stage = (Stage) bntAdd.getScene().getWindow();
        stage.close();
    }
}