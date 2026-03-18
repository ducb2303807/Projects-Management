package com.group4.projects_management_fe.features.auth;

import com.group4.common.dto.LoginRequest;
import com.group4.common.dto.UserExistsRequestDTO;
import com.group4.common.dto.UserRegistrationDTO;
import com.group4.projects_management_fe.core.api.AuthApi;
import com.group4.projects_management_fe.core.api.UserApi;
import com.group4.projects_management_fe.core.navigation.AppStageManager;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

import java.util.concurrent.TimeUnit;
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

    private static final Pattern eReg = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern nameReg =
            Pattern.compile("^[A-Za-zÀ-ỹ\\s]{2,50}$");

    private static final double FORM_WIDTH = 450;
    private static final Duration ANIMATION_TIME = Duration.millis(400);

    private final AuthApi authApi = new AuthApi();


    private UserApi userApi;

    @FXML
    private void initialize() {
        AuthSessionProvider authSessionProvider = AppSessionManager.getInstance();
        this.userApi = new UserApi(authSessionProvider);
        bgArea.setTranslateX(0);

        signInForm.setVisible(true);
        signInForm.setManaged(true);
        signInForm.setOpacity(1);

        signUpForm.setVisible(false);
        signUpForm.setManaged(false);
        signUpForm.setOpacity(0);

        var stage = AppStageManager.getInstance().getStage();
        keyBindingInitialize(stage);
        UsernameValidationInitialize(stage);
        disposeInitialize(stage);
    }

    private void UsernameValidationInitialize(Stage stage) {
        var usernameValidation = JavaFxObservable.valuesOf(signUpForm.visibleProperty())
                .switchMap(isVisible -> isVisible
                        ? Observable.combineLatest(
                        JavaFxObservable.valuesOf(registerName.textProperty()),
                        JavaFxObservable.valuesOf(registerEmail.textProperty()),
                        UserExistsRequestDTO::new)
                        : Observable.empty()
                )
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMap(dto -> {
                    boolean isValidLocal = true;

                    if (!dto.getEmail().isEmpty() && !eReg.matcher(dto.getEmail()).matches()) {
                        registerEmail.setStyle("-fx-border-color: red");
                        isValidLocal = false;
                    }

                    if (!dto.getUsername().isEmpty() && dto.getUsername().length() < 5) {
                        registerName.setStyle("-fx-border-color: red");
                        isValidLocal = false;
                    }

                    if (!isValidLocal)
                        return Observable.empty();

                    return Observable.just(dto);
                })
                .observeOn(Schedulers.io())
                .switchMap(dto -> Observable.fromCompletionStage(userApi.existsByUsernameOrEmail(dto))
                        .onErrorResumeNext(ex -> {
                            System.err.println("validation error: " + ex.getMessage());
                            return Observable.empty();
                        }))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(
                        response -> {
                            updateValidationStyle(registerName, response.isUsernameExists());
                            updateValidationStyle(registerEmail, response.isEmailExists());
                        },
                        ex -> System.err.println("CHẾT LUỒNG usernameValidation " + ex.getMessage())
                );

        disposables.add(usernameValidation);
    }

    private void updateValidationStyle(TextField field, boolean isExists) {
        if (field.getText().isEmpty()) {
            field.setStyle("");
            return;
        }

        field.setStyle(isExists ? "-fx-border-color: red" : "-fx-border-color: green");
    }

    private void keyBindingInitialize(Stage stage) {
        var enterBinding = JavaFxObservable.valuesOf(stage.sceneProperty())
                .filter(scene -> scene != null)
                .switchMap(scene -> JavaFxObservable.eventsOf(scene, KeyEvent.KEY_PRESSED))
                .filter(e -> e.getCode() == KeyCode.ENTER)
                .subscribe(e -> {
                    if (signInForm.isVisible()) {
                        handleLogin();
                    } else if (signUpForm.isVisible()) {
                        handleRegister();
                    } else {
                        System.out.println("No form is visible");
                    }
                });
        disposables.add(enterBinding);
    }

    private void disposeInitialize(Stage stage) {
        Platform.runLater(() -> {
            JavaFxObservable.valuesOf(stage.sceneProperty())
                    .filter(scene -> scene != null)
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

        if (password.isEmpty()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return;
        }

        LoginRequest loginRequest = LoginRequest.builder()
                .username(email)
                .password(password).build();

        authApi.login(loginRequest).thenAccept(response -> {
            Platform.runLater(() -> {
                AppSessionManager.getInstance().createSession(
                        response.getToken(),
                        response.getUser()
                );
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


        if (username.isEmpty()) {
            showAlert("Validation Error", "Name cannot be empty.");
            return;
        }

        if (email.isEmpty()) {
            showAlert("Validation Error", "Email cannot be empty.");
            return;
        }

        if (password.isEmpty()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return;
        }

        UserRegistrationDTO registerRequest = UserRegistrationDTO
                .builder()
                .username(username)
                .password(password)
                .fullName(fullName)
                .email(email)
                .build();

        authApi.register(registerRequest).thenAccept(response -> {
            Platform.runLater(() -> {
                showAlert("Register successful!", "Your account has been created. Now, please login.");
                showSignIn();
            });
        }).exceptionally(ex -> {
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

            Stage stage = AppStageManager.getInstance().getStage();

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