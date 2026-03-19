package com.group4.projects_management_fe.features.mainlayout;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NotificationsController {
    @FXML
    private ListView<String> notificationList;

    public void initialize() {
        notificationList.getItems().add("No new notifications");
    }
}
