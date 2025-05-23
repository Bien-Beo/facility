module com.utc2.facilityui {
    // --- Phần Requires ---
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires okhttp3; // Giữ lại nếu bạn vẫn sử dụng OkHttp ở đâu đó
    requires com.google.gson; // Giữ lại nếu bạn vẫn sử dụng Gson ở đâu đó
    requires de.jensd.fx.glyphs.fontawesome;
    requires javafx.controls;
    requires javafx.graphics;
    requires com.gluonhq.attach.util; // Kiểm tra xem có bị trùng lặp khai báo không (có 2 dòng)
    requires com.gluonhq.charm.glisten;
    requires java.desktop;
    requires kernel; // iText
    requires layout; // iText
    requires java.net.http; // Cho NotificationApiService và các HTTP client khác của Java
    requires javafx.base;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310; // Cho việc xử lý Java Time API (LocalDateTime,...)

    // --- Phần Opens ---
    opens com.utc2.facilityui.controller to javafx.fxml;
    opens com.utc2.facilityui.chatbot to javafx.fxml;
    opens com.utc2.facilityui.app to javafx.fxml;
    opens com.utc2.facilityui.service to javafx.fxml; // Mở nếu có inject FXML vào service, thường không cần thiết
    opens com.utc2.facilityui.view to javafx.fxml;   // Thường không cần thiết
    opens com.utc2.facilityui.controller.auth to javafx.fxml;
    opens com.utc2.facilityui.controller.booking to javafx.fxml;
    opens com.utc2.facilityui.controller.equipment to javafx.fxml;
    opens com.utc2.facilityui.controller.room to javafx.fxml;
    opens com.utc2.facilityui.controller.nav to javafx.fxml;

    opens com.utc2.facilityui.model to com.google.gson, javafx.base;
    opens com.utc2.facilityui.controller.Notification to javafx.fxml;

    opens com.utc2.facilityui.response to com.google.gson, javafx.base, com.fasterxml.jackson.databind;


    // --- Phần Exports ---
    exports com.utc2.facilityui.controller;
    exports com.utc2.facilityui.app;
    exports com.utc2.facilityui.controller.auth;
    exports com.utc2.facilityui.controller.booking;
    exports com.utc2.facilityui.controller.equipment;
    exports com.utc2.facilityui.controller.room;
    exports com.utc2.facilityui.controller.nav;

    // Bạn có thể cần export cả response và model nếu các module khác (nếu có) cần dùng trực tiếp các lớp này
    // exports com.utc2.facilityui.response;
    // exports com.utc2.facilityui.model;
}