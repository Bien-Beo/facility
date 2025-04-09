package com.utc2.facilityui.model;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.io.InputStream;

public class OperationsTableCell<S> extends TableCell<S, Void> {

    private final ImageView editButton = new ImageView();
    private final ImageView deleteButton = new ImageView();
    private final HBox actionGroup = new HBox(10);
    private final ObjectProperty<OperationsEventHandler<S>> eventHandler = new SimpleObjectProperty<>();

    public OperationsTableCell() {
        // Load hình ảnh
        InputStream editStream = getClass().getResourceAsStream("/com/utc2/facilityui/images/pencil.png");
        InputStream deleteStream = getClass().getResourceAsStream("/com/utc2/facilityui/images/delete.png");
        if (editStream != null && deleteStream != null) {
            editButton.setImage(new Image(editStream));
            deleteButton.setImage(new Image(deleteStream));
        } else {
            System.err.println("Không tìm thấy hình ảnh biểu tượng chỉnh sửa hoặc xóa.");
        }

        actionGroup.getChildren().addAll(editButton, deleteButton);
        actionGroup.setAlignment(Pos.CENTER);

        // Thông báo hành động cho Controller thông qua EventHandler
        editButton.setOnMouseClicked(event -> {
            S facility = getTableRow().getItem();
            if (facility != null && eventHandler.get() != null) {
                eventHandler.get().onEdit(facility);
            }
        });

        deleteButton.setOnMouseClicked(event -> {
            S facility = getTableRow().getItem();
            if (facility != null && eventHandler.get() != null) {
                eventHandler.get().onDelete(facility);
            }
        });

        setGraphic(actionGroup);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionGroup);
        }
    }

    public ObjectProperty<OperationsEventHandler<S>> eventHandlerProperty() {
        return eventHandler;
    }

    public void setEventHandler(OperationsEventHandler<S> handler) {
        this.eventHandler.set(handler);
    }

    public interface OperationsEventHandler<T> {
        void onEdit(T item);
        void onDelete(T item);
    }
}
