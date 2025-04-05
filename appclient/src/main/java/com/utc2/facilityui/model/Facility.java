package com.utc2.facilityui.model;

import javafx.beans.property.SimpleStringProperty;

public class Facility {
    private final SimpleStringProperty name;
    private final SimpleStringProperty capacity;
    private final SimpleStringProperty typeRoom;
    private final SimpleStringProperty status;
    private final SimpleStringProperty createdAt;
    private final SimpleStringProperty updatedAt;
    private final SimpleStringProperty deletedAt;
    private final SimpleStringProperty manager;

    public Facility(String name, int capacity, String typeRoom, String status,
                    String createdAt, String updatedAt, String deletedAt, String manager) {
        this.name = new SimpleStringProperty(name);
        this.capacity = new SimpleStringProperty(String.valueOf(capacity));
        this.typeRoom = new SimpleStringProperty(typeRoom);
        this.status = new SimpleStringProperty(status);
        this.createdAt = new SimpleStringProperty(createdAt);
        this.updatedAt = new SimpleStringProperty(updatedAt);
        this.deletedAt = new SimpleStringProperty(deletedAt);
        this.manager = new SimpleStringProperty(manager);
    }

    public String getName() { return name.get(); }
    public String getCapacity() { return capacity.get(); }
    public String getTypeRoom() { return typeRoom.get(); }
    public String getStatus() { return status.get(); }
    public String getCreatedAt() { return createdAt.get(); }
    public String getUpdatedAt() { return updatedAt.get(); }
    public String getDeletedAt() { return deletedAt.get(); }
    public String getManager() { return manager.get(); }
}
