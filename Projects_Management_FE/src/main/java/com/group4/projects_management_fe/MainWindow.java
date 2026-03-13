package com.group4.projects_management_fe;

import com.group4.projects_management_fe.core.api.base.BaseApi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {

    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                MainWindow.class.getResource(
                        "/com/group4/projects_management_fe/features/auth/AuthView.fxml"
//                        "/com/group4/projects_management_fe/features/dashboard/DashboardView.fxml"
                )
        );
        Scene scene = new Scene(loader.load(), 900, 600);
        scene.getStylesheets().add(
                getClass().getResource(
                        "/com/group4/projects_management_fe/features/assets/css/auth.css"
                ).toExternalForm()
        );

        scene.getStylesheets().add(
                getClass().getResource(
                        "/com/group4/projects_management_fe/features/assets/css/dashboard.css"
                ).toExternalForm()
        );
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        BaseApi.shutdown();
        super.stop();
    }
}
