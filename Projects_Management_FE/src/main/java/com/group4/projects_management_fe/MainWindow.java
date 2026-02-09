package com.group4.projects_management_fe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {
//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("main-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//    }
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                MainWindow.class.getResource(
                        "/com/group4/projects_management_fe/features/login/login-view.fxml"
                )
        );
        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
