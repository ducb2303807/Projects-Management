module com.group4.projects_management_fe {

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.fontawesome5;
    opens com.group4.projects_management_fe.features.auth to javafx.fxml;
    //opens com.group4.projects_management_fe.features.project_list to javafx.fxml;

    exports com.group4.projects_management_fe;
}
