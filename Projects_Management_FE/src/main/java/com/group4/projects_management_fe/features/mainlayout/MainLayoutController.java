package com.group4.projects_management_fe.features.mainlayout;

import com.group4.common.dto.SseNotificationDTO;
import com.group4.projects_management_fe.core.api.RxSseManager;
import com.group4.projects_management_fe.core.api.base.SseClientManager;
import com.group4.projects_management_fe.core.navigation.AppStageManager;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
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
    private HBox userBox;

    @FXML
    private StackPane profileOverlay;

    @FXML
    private StackPane overlayBackground;

    @FXML
    private VBox profilePanel;

    @FXML
    private Button notifBtn;

    private Popup notificationsPopup;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final SseClientManager<SseNotificationDTO> sseClientManager = new RxSseManager(AppSessionManager.getInstance());

    @FXML
    public void initialize() {
        showDashboard();
        setActive(dashboardBtn);

        Circle clip = new Circle(20, 20, 20);
        avatarImage.setClip(clip);
        userBox.setOnMouseClicked(e -> showProfile());
        overlayBackground.setOnMouseClicked(e -> closeProfile());

        notifBtn.setOnAction(e -> showNotificationsPopup());
        // SSE test
//        sseClientManager.connect();
//        disposables.add(SseRxBridge.toObservable(sseClientManager)
//                .subscribe(System.out::println
//                        , RxJavaPlugins::onError));
//
//        var projectApi = new ProjectApi(AppSessionManager.getInstance());
//        disposables.add(
//                Observable.interval(5, TimeUnit.SECONDS)
//                        .switchMap(i -> Observable.fromCompletionStage(projectApi.getMyProjects()))
//                        .subscribe(System.out::println, RxJavaPlugins::onError)
//        );
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
        AppStageManager.getInstance().navigateToLogin();
        AppSessionManager.getInstance().destroySession();
        if (!disposables.isDisposed()) disposables.dispose();
        sseClientManager.shutdown();
        System.out.println("Logged out");
    }

    private void showNotificationsPopup() {
        try {
            if (notificationsPopup != null && notificationsPopup.isShowing()) {
                notificationsPopup.hide();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/group4/projects_management_fe/features/dashboard/NotificationsView.fxml"
            ));
            Parent root = loader.load();

            double popupWidth = ((Region) root).prefWidth(-1);

            notificationsPopup = new Popup();
            notificationsPopup.getContent().add(root);
            notificationsPopup.setAutoHide(true);

            Stage stage = (Stage) notifBtn.getScene().getWindow();
            Bounds bounds = notifBtn.localToScreen(notifBtn.getBoundsInLocal());

            double x = bounds.getMinX() + (notifBtn.getWidth() / 2) - (popupWidth / 2);
            double y = bounds.getMaxY();

            notificationsPopup.show(stage, x, y);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}