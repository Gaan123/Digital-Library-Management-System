module org.app.dlms {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;

    opens org.app.dlms to javafx.fxml;
    opens org.app.dlms.FrontEnd.Views.Auth to javafx.fxml;
    exports org.app.dlms.FrontEnd.Views.Auth to javafx.graphics;
    opens org.app.dlms.FrontEnd.Views.Dashboard to javafx.fxml;
    exports org.app.dlms.FrontEnd.Views.Dashboard to javafx.graphics;
    exports org.app.dlms;
    opens org.app.dlms.Backend.Model to javafx.base;
}