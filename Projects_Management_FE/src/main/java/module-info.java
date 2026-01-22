module com.group.projects_management_fe {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.group4.projects_management_fe to javafx.fxml;
    exports com.group4.projects_management_fe;
}