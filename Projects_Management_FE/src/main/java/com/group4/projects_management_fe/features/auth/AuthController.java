package com.group4.projects_management_fe.features.auth;

import com.group4.common.dto.LoginRequest;
import com.group4.common.dto.UserRegistrationDTO;
import com.group4.projects_management_fe.core.api.AuthApi;
import com.group4.projects_management_fe.core.navigation.AppStageManager;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.pdfsam.rxjavafx.observables.JavaFxObservable;

import java.util.regex.Pattern;

public class AuthController {
    private final CompositeDisposable disposables = new CompositeDisposable();

    @FXML
    private VBox signInForm;

    @FXML
    private VBox signUpForm;

    @FXML
    private TextField loginUsername;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField registerFullName;

    @FXML
    private TextField registerName;

    @FXML
    private TextField registerEmail;

    @FXML
    private PasswordField registerPassword;

    @FXML
    private VBox bgArea;

    private static final Pattern eReg =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern pReg =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    private static final Pattern nameReg =
            Pattern.compile("^[A-Za-zÀ-ỹ\\s]{2,50}$");

    private static final double FORM_WIDTH = 450;
    private static final Duration ANIMATION_TIME = Duration.millis(400);

    private final AuthApi authApi = new AuthApi();


    @FXML
    private void initialize() {
        bgArea.setTranslateX(0);

        signInForm.setVisible(true);
        signInForm.setManaged(true);
        signInForm.setOpacity(1);

        signUpForm.setVisible(false);
        signUpForm.setManaged(false);
        signUpForm.setOpacity(0);

        keyBindingInitialize();
    }

    private void keyBindingInitialize() {
        var stage = AppStageManager.getInstance().getStage();

        Platform.runLater(() -> {
            var enterBinding = JavaFxObservable.eventsOf(stage.getScene(), KeyEvent.KEY_PRESSED)
                    .filter(e -> e.getCode() == KeyCode.ENTER)
                    .subscribe(e -> {
                        System.out.println("Enter key pressed");
                        if (signInForm.isVisible()) {
                            handleLogin();
                        } else if (signUpForm.isVisible()) {
                            handleRegister();
                        } else {
                            System.out.println("No form is visible");
                        }
                    });

            disposables.add(enterBinding);

            JavaFxObservable.valuesOf(stage.sceneProperty())
                    .skip(1)
                    .take(1)
                    .subscribe(scene -> {
                        if (!disposables.isDisposed())
                            disposables.dispose();
                        System.out.println("Auth controller Disposed");
                    });
        });
    }


    @FXML
    private void showSignUp() {

        FadeTransition fadeOut =
                new FadeTransition(Duration.millis(300), signInForm);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {

            signInForm.setVisible(false);
            signInForm.setManaged(false);

            TranslateTransition move =
                    new TranslateTransition(ANIMATION_TIME, bgArea);
            move.setToX(-FORM_WIDTH);
            move.setInterpolator(Interpolator.EASE_BOTH);

            move.setOnFinished(ev -> {

                signUpForm.setVisible(true);
                signUpForm.setManaged(true);

                FadeTransition fadeIn =
                        new FadeTransition(Duration.millis(300), signUpForm);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });

            move.play();
        });

        fadeOut.play();
    }

    @FXML
    private void showSignIn() {

        FadeTransition fadeOut =
                new FadeTransition(Duration.millis(300), signUpForm);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {

            signUpForm.setVisible(false);
            signUpForm.setManaged(false);

            TranslateTransition move =
                    new TranslateTransition(ANIMATION_TIME, bgArea);
            move.setToX(0);
            move.setInterpolator(Interpolator.EASE_BOTH);

            move.setOnFinished(ev -> {

                signInForm.setVisible(true);
                signInForm.setManaged(true);

                FadeTransition fadeIn =
                        new FadeTransition(Duration.millis(300), signInForm);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });

            move.play();
        });

        fadeOut.play();
    }

    @FXML
    private void handleLogin() {

        String email = loginUsername.getText().trim();
        String password = loginPassword.getText().trim();

        if (email.isEmpty()) {
            showAlert("Validation Error", "Email cannot be empty.");
            return;
        }

//        if (!eReg.matcher(email).matches()) {
//            showAlert("Validation Error", "Email format is invalid.");
//            return;
//        }

        if (password.isEmpty()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return;
        }
//
//        if (!pReg.matcher(password).matches()) {
//            showAlert("Validation Error",
//                    "Password must be at least 8 characters and include uppercase, lowercase and a number.");
//            return;
//        }

        LoginRequest loginRequest = LoginRequest.builder()
                .username(email)
                .password(password).build();

        authApi.login(loginRequest).thenAccept(response -> {
            Platform.runLater(() -> {
                showAlert("Login successful", "Welcome back!");
                openMainLayout();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                String cleanMessage = cause.getMessage();

                if (cleanMessage != null && cleanMessage.contains(": ")) {
                    cleanMessage = cleanMessage.substring(cleanMessage.indexOf(": ") + 2);
                }

                showAlert("Login failed", cleanMessage);
            });
            return null;
        });
    }

    @FXML
    private void handleRegister() {

        String fullName = registerFullName.getText().trim();
        String username = registerName.getText().trim();
        String email = registerEmail.getText().trim();
        String password = registerPassword.getText().trim();

        if (fullName.isEmpty()) {
            showAlert("Validation Error", "Full username cannot be empty.");
            return;
        }

        if (!nameReg.matcher(fullName).matches()) {
            showAlert("Validation Error",
                    "Full username must contain only letters and spaces (2-50 characters).");
            return;
        }

        if (username.isEmpty()) {
            showAlert("Validation Error", "Name cannot be empty.");
            return;
        }

        if (email.isEmpty()) {
            showAlert("Validation Error", "Email cannot be empty.");
            return;
        }

        if (!eReg.matcher(email).matches()) {
            showAlert("Validation Error", "Email format is invalid.");
            return;
        }

        if (password.isEmpty()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return;
        }

        if (!pReg.matcher(password).matches()) {
            showAlert("Validation Error",
                    "Password must be at least 8 characters and include uppercase, lowercase and a number.");
            return;
        }

        UserRegistrationDTO registerRequest = UserRegistrationDTO
                .builder()
                .username(username)
                .password(password)
                .fullName(fullName)
                .email(email)
                .build();

        // 2. Gọi API thông qua authApi
        authApi.register(registerRequest).thenAccept(response -> {
            // Khi thành công dùng Platform.runLater để update UI
            Platform.runLater(() -> {
                showAlert("Register successful!", "Your account has been created. Now, please login.");

                // (Tuỳ chọn) Xóa trắng các ô nhập liệu sau khi đăng ký thành công
//                registerFullName.clear();
//                registerName.clear();
//                registerEmail.clear();
//                registerPassword.clear();

                // Chuyển về màn hình đăng nhập
                showSignIn();
            });
        }).exceptionally(ex -> {
            // Xử lý lỗi giống handleLogin
            Platform.runLater(() -> {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                String cleanMessage = cause.getMessage();

                if (cleanMessage != null && cleanMessage.contains(": ")) {
                    cleanMessage = cleanMessage.substring(cleanMessage.indexOf(": ") + 2);
                }

                showAlert("Registration failed", cleanMessage);
            });
            return null;
        });
    }

    private void openMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/group4/projects_management_fe/features/mainlayout/MainLayoutView.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) signInForm.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setResizable(true);
            stage.setTitle("Nexus");
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}