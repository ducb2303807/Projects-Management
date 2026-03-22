package com.group4.projects_management_fe.features.mainlayout;

import com.group4.common.dto.ChangePasswordRequestDTO;
import com.group4.common.dto.SseNotificationDTO;
import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserUpdateDTO;
import com.group4.projects_management_fe.core.api.NotificationApi;
import com.group4.projects_management_fe.core.api.RxSseManager;
import com.group4.projects_management_fe.core.api.UserApi;
import com.group4.projects_management_fe.core.api.base.SseClientManager;
import com.group4.projects_management_fe.core.extension.SseRxBridge;
import com.group4.projects_management_fe.core.navigation.AppStageManager;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import com.group4.projects_management_fe.features.project.ProjectTasksController;
import com.group4.projects_management_fe.features.toast.Toast;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

import java.io.IOException;

public class MainLayoutController {
    @Getter
    private static MainLayoutController instance;

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

    @FXML
    private Button notifBtn;

    private Popup notificationsPopup;

    @FXML
    private Label roleLabel;

    @FXML private VBox profileContent;
    @FXML private VBox passwordContent;

    @FXML private Label profileTab;
    @FXML private Label passwordTab;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label notifBadge;

    private UserDTO currentUser;
    private final UserApi userApi = new UserApi(AppSessionManager.getInstance());
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final SseClientManager<SseNotificationDTO> sseClientManager = new RxSseManager(AppSessionManager.getInstance());
    private final NotificationApi notificationApi = new NotificationApi(AppSessionManager.getInstance());

    @FXML
    public void initialize() {
        instance = this;

        showDashboard();
        setActive(dashboardBtn);
        notifBtn.setOnAction(e -> showNotificationsPopup());

        Circle clip = new Circle(20, 20, 20);
        avatarImage.setClip(clip);
        userBox.setOnMouseClicked(e -> showProfile());
        overlayBackground.setOnMouseClicked(e -> closeProfile());
        loadCurrentUser();

        updateNotificationBadge();

        Stage stage = AppStageManager.getInstance().getStage();
        /// SSE
        sseClientManager.connect();
        disposables.add(SseRxBridge.toObservable(sseClientManager)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(sseNotificationDTO -> {
                    javafx.application.Platform.runLater(() -> {
                        Toast.showToast(stage, sseNotificationDTO);

                        incrementBadgeCount();

                        new java.util.Timer().schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                javafx.application.Platform.runLater(() -> updateNotificationBadge());
                            }
                        }, 1500);
                    });
                }, RxJavaPlugins::onError));
    }

    private void bindUserToUI(UserDTO user) {
        usernameLabel.setText(user.getUsername());

        usernameField.setText(user.getUsername());
        fullnameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        roleLabel.setText(user.getSystemRoleName());
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

    private void showNotificationsPopup() {
        try {
            if (notificationsPopup != null && notificationsPopup.isShowing()) {
                notificationsPopup.hide();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/group4/projects_management_fe/features/mainlayout/NotificationsView.fxml"
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

    public void updateNotificationBadge() {
        notificationApi.getUnreadCount().thenAccept(count -> {
            javafx.application.Platform.runLater(() -> {
                applyBadgeCount(count);
            });
        });
    }

    public void decrementBadgeCount() {
        Platform.runLater(() -> {
            try {
                String currentText = notifBadge.getText();
                if (currentText == null || currentText.isEmpty() || currentText.equals("0")) {
                    applyBadgeCount(0);
                    return;
                }

                if (!currentText.contains("+")) {
                    int count = Integer.parseInt(currentText);
                    applyBadgeCount(Math.max(0, count - 1));
                } else {
                    updateNotificationBadge();
                }
            } catch (Exception e) {
                updateNotificationBadge();
            }
        });
    }

    public void incrementBadgeCount() {
        Platform.runLater(() -> {
            try {
                // Kiểm tra xem label có null không (tránh lỗi crash app)
                if (notifBadge == null) return;

                int newCount = 1; // Mặc định nếu chưa có số thì là 1

                // Nếu badge đang hiển thị và có nội dung số
                if (notifBadge.isVisible() && notifBadge.getText() != null && !notifBadge.getText().isEmpty()) {
                    String text = notifBadge.getText().replace("+", "").trim();
                    newCount = Integer.parseInt(text) + 1;
                }

                // Cập nhật lên giao diện
                applyBadgeCount(newCount);

                System.out.println("SSE debug: Đã tăng badge lên " + newCount);
            } catch (Exception e) {
                System.err.println("Lỗi khi tăng badge: " + e.getMessage());
                updateNotificationBadge(); // Fallback gọi API nếu tính toán lỗi
            }
        });
    }

    public void applyBadgeCount(int count) {
        if (count > 0) {
            notifBadge.setText(count > 99 ? "99+" : String.valueOf(count));
            notifBadge.setVisible(true);
            notifBadge.setManaged(true);
            notifBadge.applyCss();
        } else {
            notifBadge.setVisible(false);
            notifBadge.setManaged(false);
        }
    }

    public void openProjectTasksWindow(Long projectId, String projectName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/ProjectTasks.fxml"));
            Parent root = loader.load();

            ProjectTasksController controller = loader.getController();
            controller.initData(projectId, projectName);

            Stage newStage = new Stage();
            newStage.setTitle("Project Tasks: " + projectName);
            newStage.setScene(new javafx.scene.Scene(root));

            Platform.runLater(() -> newStage.show());

        } catch (IOException e) {
            System.err.println("Không thể mở cửa sổ Project Tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }
}