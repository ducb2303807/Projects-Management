package com.group4.projects_management_fe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {

    public void start(Stage stage) throws IOException {

//        FXMLLoader loader = new FXMLLoader(
//                MainWindow.class.getResource(
//                        "/com/group4/projects_management_fe/features/auth/MainLayout.fxml"
//                )
//        );

        FXMLLoader loader = new FXMLLoader(
                MainWindow.class.getResource(
                        "/com/group4/projects_management_fe/features/auth/AuthView.fxml"
                )
        );
        Scene scene = new Scene(loader.load(), 900, 600);
        scene.getStylesheets().add(
                MainWindow.class.getResource(
                        "/com/group4/projects_management_fe/features/assets/css/auth.css"
                ).toExternalForm()
        );
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
