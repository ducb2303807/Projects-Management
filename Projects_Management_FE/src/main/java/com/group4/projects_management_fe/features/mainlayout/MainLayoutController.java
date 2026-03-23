package com.group4.projects_management_fe.features.mainlayout;

import com.group4.common.dto.ChangePasswordRequestDTO;
import com.group4.common.dto.SseNotificationDTO;
import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserUpdateDTO;
import com.group4.projects_management_fe.core.api.RxSseManager;
import com.group4.projects_management_fe.core.api.UserApi;
import com.group4.projects_management_fe.core.api.base.SseClientManager;
import com.group4.projects_management_fe.core.extension.SseRxBridge;
import com.group4.projects_management_fe.core.navigation.AppStageManager;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import com.group4.projects_management_fe.features.toast.Toast;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField fullnameField;

    @FXML
    private TextField emailField;

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

//    @FXML
//    private Label roleLabel;

    @FXML private VBox profileContent;
    @FXML private VBox passwordContent;

    @FXML private Label profileTab;
    @FXML private Label passwordTab;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private UserDTO currentUser;
    private final UserApi userApi = new UserApi(AppSessionManager.getInstance());
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
        loadCurrentUser();

        Stage stage = AppStageManager.getInstance().getStage();
        /// SSE
        sseClientManager.connect();
        disposables.add(SseRxBridge.toObservable(sseClientManager)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(sseNotificationDTO ->  {
                            Toast.showToast(stage, sseNotificationDTO);
                        }
                        , RxJavaPlugins::onError));
    }

    private void bindUserToUI(UserDTO user) {
        usernameLabel.setText(user.getUsername());

        usernameField.setText(user.getUsername());
        fullnameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
//        roleLabel.setText(user.getSystemRoleName());
    }

    private void loadCurrentUser() {
        currentUser = AppSessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            bindUserToUI(currentUser);
        }
    }

    @FXML
    private void showProfileTab() {
        profileContent.setVisible(true);
        profileContent.setManaged(true);

        passwordContent.setVisible(false);
        passwordContent.setManaged(false);

        profileTab.getStyleClass().setAll("tab-active");
        passwordTab.getStyleClass().setAll("tab");
    }

    @FXML
    private void showPasswordTab() {
        profileContent.setVisible(false);
        profileContent.setManaged(false);

        passwordContent.setVisible(true);
        passwordContent.setManaged(true);

        profileTab.getStyleClass().setAll("tab");
        passwordTab.getStyleClass().setAll("tab-active");
    }

    @FXML
    private void handleSaveProfile() {
        if (currentUser == null) return;

        UserUpdateDTO request = new UserUpdateDTO();
        request.setFullName(fullnameField.getText());
        request.setEmail(emailField.getText());

        userApi.updateProfile(currentUser.getId(), request)
                .thenAccept(updatedUser -> {
                    currentUser = updatedUser;

                    javafx.application.Platform.runLater(() -> {
                        bindUserToUI(updatedUser);
                        closeProfile();
                    });
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
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

            // Lấy controller của màn hình vừa load
            Object controller = loader.getController();

            // Kiểm tra xem nếu màn hình đang load là TasksViewController
            if (controller instanceof com.group4.projects_management_fe.features.task.TasksViewController) {
                com.group4.projects_management_fe.features.task.TasksViewController tasksController =
                        (com.group4.projects_management_fe.features.task.TasksViewController) controller;

                // Truyền AppSessionManager vào (vì nó implements AuthSessionProvider)
                tasksController.setSessionProvider(AppSessionManager.getInstance());

//                tasksController.setProjectId(null);
            }

            VBox wrapper = new VBox(view);
            wrapper.setMaxWidth(Double.MAX_VALUE);

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

        if (currentUser != null) {
            bindUserToUI(currentUser);
        }

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
    private void handleChangePassword() {
        if (currentUser == null) return;

        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!newPass.equals(confirm)) {
            System.out.println("Confirm password error");
            return;
        }

        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setOldPassword(current);
        request.setNewPassword(newPass);

        userApi.changePassword(currentUser.getId(), request)
                .thenRun(() -> {
                    javafx.application.Platform.runLater(() -> {
                        clearPasswordFields();
                        closeProfile();
                        System.out.println("Password change successfully");
                    });
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
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


}