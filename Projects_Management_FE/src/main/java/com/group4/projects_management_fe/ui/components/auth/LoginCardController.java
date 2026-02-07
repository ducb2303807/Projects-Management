package com.group4.projects_management_fe.ui.components.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginCardController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        // Logic đăng nhập giả lập
        System.out.println("User: " + usernameField.getText());
        showAlert("Thông báo", "Đang xử lý đăng nhập...");
    }

    @FXML
    private void handleSwitchToSignup(ActionEvent event) {
        try {
            // Đường dẫn tuyệt đối từ classpath để tránh lỗi
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/ui/components/auth/SignupCard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không tìm thấy file SignupCard.fxml");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}