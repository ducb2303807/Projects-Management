package com.group4.projects_management_fe.core.navigation;

import com.group4.projects_management_fe.MainWindow;
import com.group4.projects_management_fe.core.exception.GlobalExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppStageManager {
    private static AppStageManager instance;
    private Stage stage;

    private AppStageManager() {}

    public synchronized static AppStageManager getInstance() {
        if (instance == null) {
            instance = new AppStageManager();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void navigateToLogin() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::navigateToLogin);
            return;
        }

        try {
            // 2. Load giao diện
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

        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e);
        }
    }
}
