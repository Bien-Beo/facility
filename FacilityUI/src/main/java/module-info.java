module com.utc2.facilityui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires okhttp3;
    requires com.google.gson;

    exports com.utc2.facilityui.controller;
    opens com.utc2.facilityui.controller to javafx.fxml;
    exports com.utc2.facilityui.app;
    opens com.utc2.facilityui.app to javafx.fxml;
}