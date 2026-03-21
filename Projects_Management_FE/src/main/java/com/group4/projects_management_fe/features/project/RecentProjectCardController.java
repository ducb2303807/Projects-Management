package com.group4.projects_management_fe.features.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RecentProjectCardController {

    @FXML
    private Label lblProjectName;

    public void bindData(String projectName) {
        if (lblProjectName != null) {
            lblProjectName.setText(projectName);
        }
    }
}