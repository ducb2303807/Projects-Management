package com.group4.projects_management_fe.features.auth;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    private static final String eRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final String pRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox checkBox;

    @FXML
    public void initialize() {
        // chạy khi view load
        System.out.println("Login view loaded");
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        boolean checkbox = checkBox.isSelected();

        if (email.isBlank() || password.isBlank()) {
            showAlert("Validation error", "Email and password cannot be empty");
            return;
        }

        if (!email.matches(eRegex)) {
            showAlert("Email Validation error", "Invalid email format");
            return;
        }

        if (!password.matches(pRegex)) {
            showAlert(
                    "Password Validation error",
                    "Password must be at least 8 characters and contain letters and numbers"
            );
            return;
        }

        System.out.println("Email: " + email);
        System.out.println("CheckBox: " + checkbox);
        System.out.println("Password: " + password);

        //goToProjectList();
    }


    @FXML
    private void handleSwitchToRegister() {
        System.out.println("Switch to Register view");
    }

    @FXML
    private void handleSwitchToLogin() {
        // đang ở login không cần làm gì
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
