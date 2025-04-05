package com.utc2.facilityui.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.utc2.facilityui.auth.TokenStorage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import okhttp3.*;
import com.utc2.facilityui.model.Facility;
import java.io.IOException;

public class FacilityController {
    @FXML private TableView<Facility> facilityTable;
    @FXML private TableColumn<Facility, String> nameColumn;
    @FXML private TableColumn<Facility, Integer> capacityColumn;
    @FXML private TableColumn<Facility, String> typeColumn;
    @FXML private TableColumn<Facility, String> statusColumn;
    @FXML private TableColumn<Facility, String> createdAtColumn;
    @FXML private TableColumn<Facility, String> updatedAtColumn;
    @FXML private TableColumn<Facility, String> deletedAtColumn;
    @FXML private TableColumn<Facility, String> managerColumn;
    @FXML private Label lbMessage;
    @FXML private Button addButton;
    @FXML private Button exportButton;

    private static final String FACILITIES_URL = "http://localhost:8080/facility/list";
    private final OkHttpClient client = new OkHttpClient();

    @FXML
    private void initialize() {
        setupTableColumns();
        loadFacilities();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        deletedAtColumn.setCellValueFactory(new PropertyValueFactory<>("deletedAt"));
        managerColumn.setCellValueFactory(new PropertyValueFactory<>("manager"));
    }

    private void loadFacilities() {
        Request request = new Request.Builder()
                .url(FACILITIES_URL)
                .addHeader("Authorization", "Bearer " + TokenStorage.getToken())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> lbMessage.setText("Lỗi kết nối server!"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();

                    Platform.runLater(() -> {
                        facilityTable.getItems().clear();
                        for (var element : jsonArray) {
                            JsonObject obj = element.getAsJsonObject();
                            facilityTable.getItems().add(new Facility(
                                    obj.get("name").getAsString(),
                                    obj.get("capacity").getAsInt(),
                                    obj.get("type").getAsString(),
                                    obj.get("status").getAsString(),
                                    obj.get("createdAt").getAsString(),
                                    obj.get("updatedAt").getAsString(),
                                    obj.has("deletedAt") ? obj.get("deletedAt").getAsString() : "",
                                    obj.get("manager").getAsString()
                            ));
                        }
                    });
                } else {
                    Platform.runLater(() -> lbMessage.setText("Lỗi tải danh sách cơ sở vật chất!"));
                }
            }
        });
    }
}
