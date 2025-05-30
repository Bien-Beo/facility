package com.utc2.facilityui.controller;

import com.utc2.facilityui.controller.nav.SidebarMenuController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private SidebarMenuController sidebarMenuController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Tải sidebar menu
        loadSidebarMenu();

        // Tải giao diện ban đầu (ví dụ: dashboard hoặc quản lý cơ sở vật chất)
        loadInitialView(); // Đổi tên phương thức cho rõ ràng
    }

    private void loadSidebarMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/view/sidebarmenu.fxml"));
            Parent sidebar = loader.load();
            sidebarMenuController = loader.getController();
            sidebarMenuController.setMainBorderPane(mainBorderPane);
            mainBorderPane.setLeft(sidebar);
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ (ví dụ: hiển thị thông báo lỗi)
        }
    }

    private void loadInitialView() { // Đổi tên phương thức
        try {
            Parent initialView = FXMLLoader.load(getClass().getResource("/com/utc2/facilityui/view/manageFacility.fxml")); // Thay thế bằng giao diện ban đầu mong muốn
            mainBorderPane.setCenter(initialView);
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ
        }
    }
}