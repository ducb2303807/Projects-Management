package com.group4.projects_management_fe.core.navigation;

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
}
