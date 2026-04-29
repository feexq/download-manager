module com.project.downloadmanager {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires static lombok;
    requires jdk.compiler;
    requires jakarta.persistence;
    requires org.xerial.sqlitejdbc;
    requires jdk.httpserver;
    requires java.rmi;
    requires java.desktop;

    opens com.project.downloadmanager to javafx.fxml;
    opens com.project.downloadmanager.model.entity to javafx.base;
    exports com.project.downloadmanager;
}