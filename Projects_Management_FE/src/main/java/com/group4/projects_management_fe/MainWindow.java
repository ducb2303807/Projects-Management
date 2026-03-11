package com.group4.projects_management_fe;

import com.group4.projects_management_fe.core.api.base.BaseApi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {

    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                MainWindow.class.getResource(
                        "/com/group4/projects_management_fe/features/auth/AuthView.fxml"
                )
        );
        Scene scene = new Scene(loader.load(), 900, 650);
        scene.getStylesheets().add(
                MainWindow.class.getResource(
                        "/com/group4/projects_management_fe/features/assets/css/auth.css"
                ).toExternalForm()
        );

        Image logo = new Image(
                getClass().getResourceAsStream("/com/group4/projects_management_fe/features/assets/image/app_icon_v6.png")
        );

        stage.getIcons().add(logo);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        BaseApi.shutdown();
        super.stop();
    }
}
