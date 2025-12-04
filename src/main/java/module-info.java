module com.adanali.javafx.asynchronousimageprocessor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.graphics;

    exports com.adanali.javafx.asynchronousimageprocessor.app;
    opens com.adanali.javafx.asynchronousimageprocessor.app to javafx.fxml;
    opens com.adanali.javafx.asynchronousimageprocessor.controller to javafx.fxml;
}