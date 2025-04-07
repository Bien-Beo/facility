package com.utc2.facilityui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Facility {
    private final SimpleStringProperty name;
    private final SimpleStringProperty capacity;
    private final SimpleStringProperty typeRoom;
    private final SimpleStringProperty status;
    private final SimpleStringProperty createdAt;
    private final SimpleStringProperty updatedAt;
    private final SimpleStringProperty deletedAt;
    private final SimpleStringProperty managerName;
    private final StringProperty managerId;

    public Facility(String name, String capacity, String typeRoom, String status,
                    String createdAt, String updatedAt, String deletedAt, String managerName, String managerId) {
        this.name = new SimpleStringProperty(name);
        this.capacity = new SimpleStringProperty(String.valueOf(capacity));
        this.typeRoom = new SimpleStringProperty(typeRoom);
        this.status = new SimpleStringProperty(status);
        this.createdAt = new SimpleStringProperty(createdAt);
        this.updatedAt = new SimpleStringProperty(updatedAt);
        this.deletedAt = new SimpleStringProperty(deletedAt);
        this.managerName = new SimpleStringProperty(managerName);
        this.managerId = new SimpleStringProperty(managerId);
    }

    public String getName() { return name.get(); }
    public String getCapacity() { return capacity.get(); }
    public String getTypeRoom() { return typeRoom.get(); }
    public String getStatus() { return status.get(); }
    public String getCreatedAt() { return createdAt.get(); }
    public String getUpdatedAt() { return updatedAt.get(); }
    public String getDeletedAt() { return deletedAt.get(); }
    public String getManagerName() { return managerName.get(); }
    public String getManagerId() { return managerId.get(); }

    public StringProperty nameProperty() { return name; }
    public StringProperty capacityProperty() { return capacity; }
    public StringProperty typeRoomProperty() { return typeRoom; }
    public StringProperty statusProperty() { return status; }
    public StringProperty createdAtProperty() { return createdAt; }
    public StringProperty updatedAtProperty() { return updatedAt; }
    public StringProperty deletedAtProperty() { return deletedAt; }
    public StringProperty managerNameProperty() { return managerName; }
    public StringProperty managerIdProperty() { return managerId; }
}
