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
    requires java.desktop;

    exports com.utc2.facilityui.controller;
    opens com.utc2.facilityui.controller to javafx.fxml;
    opens com.utc2.facilityui.chatbot to javafx.fxml;
    exports com.utc2.facilityui.app;
    opens com.utc2.facilityui.app to javafx.fxml;
    opens com.utc2.facilityui.service to javafx.fxml;
    opens com.utc2.facilityui.view to javafx.fxml;
    opens com.utc2.facilityui.model to com.google.gson;
    opens com.utc2.facilityui.response to com.google.gson;
    exports com.utc2.facilityui.controller.auth;
    opens com.utc2.facilityui.controller.auth to javafx.fxml;
    exports com.utc2.facilityui.controller.booking;
    opens com.utc2.facilityui.controller.booking to javafx.fxml;
    exports com.utc2.facilityui.controller.equipment;
    opens com.utc2.facilityui.controller.equipment to javafx.fxml;
    exports com.utc2.facilityui.controller.room;
    opens com.utc2.facilityui.controller.room to javafx.fxml;
    exports com.utc2.facilityui.controller.nav;
    opens com.utc2.facilityui.controller.nav to javafx.fxml;
}