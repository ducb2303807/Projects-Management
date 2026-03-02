package com.group4.projects_management_fe.features.auth;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button projectsBtn;

    @FXML
    private Button tasksBtn;

    @FXML
    private ImageView avatarImage;

    @FXML
    public void initialize() {
        showDashboard();
        setActive(dashboardBtn);

        dashboardBtn.setOnAction(e -> setActive(dashboardBtn));
        projectsBtn.setOnAction(e -> setActive(projectsBtn));
        tasksBtn.setOnAction(e -> setActive(tasksBtn));

        Circle clip = new Circle(20, 20, 20);
        avatarImage.setClip(clip);
    }

    @FXML
    private void showDashboard() {
        loadView("/com/group4/projects_management_fe/features/auth/DashboardView.fxml");
    }

    @FXML
    private void showProjects() {
        loadView("/com/group4/projects_management_fe/features/auth/ProjectsView.fxml");
    }

    @FXML
    private void showTasks() {
        loadView("/com/group4/projects_management_fe/features/auth/TasksView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            contentPane.getChildren().setAll(view);

            FadeTransition fade = new FadeTransition(Duration.millis(250), view);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button selected) {
        dashboardBtn.getStyleClass().remove("active");
        projectsBtn.getStyleClass().remove("active");
        tasksBtn.getStyleClass().remove("active");

        selected.getStyleClass().add("active");
    }
}