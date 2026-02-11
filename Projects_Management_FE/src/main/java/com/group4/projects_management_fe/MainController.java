package com.group4.projects_management_fe;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label welcomeText;

    // mỏng hơn
    // UI có gì đó -> gửi thông báo thay đổi
    // Khi mà có yêu cầu thay đổi gì đó trên UI -> Controller tự nhận thông báo và đưa lên UI

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
