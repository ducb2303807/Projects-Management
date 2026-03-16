package com.group4.projects_management_fe.features.mainlayout;

import com.group4.projects_management_fe.MainWindow;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;

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
    private HBox userBox;

    @FXML
    private StackPane profileOverlay;

    @FXML
    private StackPane overlayBackground;

    @FXML
    private VBox profilePanel;

    @FXML
    public void initialize() {
        showDashboard();
        setActive(dashboardBtn);

        Circle clip = new Circle(20, 20, 20);
        avatarImage.setClip(clip);
        userBox.setOnMouseClicked(e -> showProfile());
        overlayBackground.setOnMouseClicked(e -> closeProfile());
    }

    @FXML
    private void showDashboard() {
        setActive(dashboardBtn);
        loadView("/com/group4/projects_management_fe/features/dashboard/DashboardView.fxml");
    }

    @FXML
    private void showProjects() {
        setActive(projectsBtn);
        loadView("/com/group4/projects_management_fe/features/project/ProjectsView.fxml");
    }

    @FXML
    private void showTasks() {
        setActive(tasksBtn);
        loadView("/com/group4/projects_management_fe/features/task/TasksView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            VBox wrapper = new VBox(view);
            wrapper.setMaxWidth(1200);

            VBox.setVgrow(view, Priority.ALWAYS);

            contentPane.getChildren().setAll(wrapper);

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

    private void showProfile() {

        profileOverlay.setVisible(true);
        profileOverlay.setManaged(true);

        profileOverlay.setOpacity(0);
        profilePanel.setScaleX(0.8);
        profilePanel.setScaleY(0.8);

        FadeTransition fade = new FadeTransition(Duration.millis(200), profileOverlay);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), profilePanel);
        scale.setToX(1);
        scale.setToY(1);

        fade.play();
        scale.play();
    }

    @FXML
    private void hideProfile(ActionEvent event) {
        closeProfile();
    }

    private void closeProfile() {

        FadeTransition fade = new FadeTransition(Duration.millis(200), profileOverlay);
        fade.setToValue(0);

        fade.setOnFinished(e -> {
            profileOverlay.setVisible(false);
            profileOverlay.setManaged(false);
        });

        fade.play();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/group4/projects_management_fe/features/auth/AuthView.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            Scene scene = new Scene(root, 900, 650);
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}