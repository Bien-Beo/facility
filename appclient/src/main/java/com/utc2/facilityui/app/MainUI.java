package com.utc2.facilityui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/utc2/facilityui/view/home.fxml"));
        primaryStage.setTitle("Facility UI");
        primaryStage.setScene(new Scene(root, 1065, 497));
        primaryStage.show();
    }

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/chatbot/chatbot.fxml"));
//        VBox root = loader.load();
//
//        primaryStage.setTitle("Chatbot Hỗ Trợ");
//        primaryStage.setScene(new Scene(root, 450, 400));
//        primaryStage.show();
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
