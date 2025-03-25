module com.utc2.facilityui {
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires okhttp3;
    requires com.google.gson;
    requires de.jensd.fx.glyphs.fontawesome;
    requires javafx.controls;
    requires javafx.graphics;
    requires com.gluonhq.attach.util;
    requires com.gluonhq.charm.glisten;

    exports com.utc2.facilityui.controller;
    opens com.utc2.facilityui.controller to javafx.fxml;
    exports com.utc2.facilityui.app;
    opens com.utc2.facilityui.app to javafx.fxml;
    opens com.utc2.facilityui.service to javafx.fxml;
    opens com.utc2.facilityui.view to javafx.fxml;
}