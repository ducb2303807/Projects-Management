package com.group4.projects_management_fe.features.auth;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.regex.Pattern;

public class AuthController {

    @FXML
    private VBox signInForm;

    @FXML
    private VBox signUpForm;

    @FXML
    private TextField loginEmail;

    @FXML
    private PasswordField loginPassword;

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


    private static final double FORM_WIDTH = 450;
    private static final Duration ANIMATION_TIME = Duration.millis(400);

    @FXML
    private void initialize() {

        bgArea.setTranslateX(0);

        signInForm.setVisible(true);
        signInForm.setManaged(true);
        signInForm.setOpacity(1);

        signUpForm.setVisible(false);
        signUpForm.setManaged(false);
        signUpForm.setOpacity(0);
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

        String email = loginEmail.getText();
        String password = loginPassword.getText();

        if (email == null || email.isBlank()) {
            showAlert("Validation error", "Email cannot be empty");
            return;
        }

        if (!eReg.matcher(email).matches()) {
            showAlert("Validation error", "Email format is invalid");
            return;
        }

        if (password == null || password.isBlank()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return;
        }

        if (!pReg.matcher(password).matches()) {
            showAlert("Validation Error",
                    "Password must be at least 8 characters and include uppercase, lowercase and a number.");
            return;
        }

        showAlert("Login successfull", "Welcome back!");
        openMainLayout();
    }

    @FXML
    private void handleRegister() {

        String name = registerName.getText();
        String email = registerEmail.getText();
        String password = registerPassword.getText();

        if (name == null || name.isBlank()) {
            showAlert("Validation Error", "Name cannot be empty.");
            return;
        }

        if (email == null || email.isBlank()) {
            showAlert("Validation Error", "Email cannot be empty.");
            return;
        }

        if (!eReg.matcher(email).matches()) {
            showAlert("Validation Error", "Email format is invalid.");
            return;
        }

        if (password == null || password.isBlank()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return;
        }

        if (!pReg.matcher(password).matches()) {
            showAlert("Validation Error",
                    "Password must be at least 8 characters and include uppercase, lowercase and a number.");
            return;
        }

        showAlert("Register successful!", "Now, please login.");
        showSignIn();
    }

    private void openMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/group4/projects_management_fe/features/auth/MainLayout.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) signInForm.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
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