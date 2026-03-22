package com.group4.projects_management_fe.features.mainlayout;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management_fe.core.api.NotificationApi;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class NotificationsController {
    @FXML
    private ListView<NotificationDTO> notificationList;

    private final NotificationApi notificationApi;

    public NotificationsController() {
        this.notificationApi = new NotificationApi(AppSessionManager.getInstance());
    }

    public void initialize() {
        notificationApi.getNotificationsForUser().thenAccept(notifications -> {
            Platform.runLater(() -> {
                if (notifications == null || notifications.isEmpty()) {
                    NotificationDTO empty = new NotificationDTO();
                    empty.setTitle("No new notifications");
                    empty.setRead(true);
                    notificationList.getItems().add(empty);
                } else {
                    notificationList.getItems().addAll(notifications);
                }
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                NotificationDTO error = new NotificationDTO();
                error.setTitle("Error loading notifications");
                error.setRead(true);
                notificationList.getItems().add(error);
            });
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            return null;
        });

        notificationList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(NotificationDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String timeText = item.getCreatedDate() != null
                            ? formatDate(item.getCreatedDate())
                            : "";

                    setText(item.getTitle() + (timeText.isEmpty() ? "" : " - " + timeText));

                    if (!item.isRead()) {
                        setStyle("-fx-font-weight: bold; -fx-background-color: cyan; -fx-text-fill: black;");
                    } else {
                        setStyle("-fx-font-weight: normal; -fx-text-fill: gray;");
                    }

                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 1) {
                            handleNotificationClick(item);
                        }
                    });
                }
            }
        });
    }

    private String formatDate(LocalDateTime createdAt) {
        if (createdAt == null) return "";
        Date date = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        PrettyTime prettyTime = new PrettyTime(Locale.ENGLISH);

        return prettyTime.format(date);
    }

    private void handleNotificationClick(NotificationDTO item) {
        if ("PROJECT_INVITATION".equals(item.getType())) {
            String action = (item.getMetadata() != null) ? item.getMetadata().getResponseAction() : null;

            if ("DECLINE".equalsIgnoreCase(action)) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText("You have already declined this project invitation.");
                info.show();
                return;
            }

            if ("ACCEPT".equalsIgnoreCase(action)) {
                if (MainLayoutController.getInstance() != null && item.getMetadata() != null) {
                    Long projectId = item.getMetadata().getProjectId();
                    String projectName = item.getMetadata().getProjectName();

                    MainLayoutController.getInstance().openProjectTasksWindow(projectId, projectName);
                }
                return;
            }

            showInvitationPopup(item);
        }
    }

    private void showInvitationPopup(NotificationDTO item) {
        NotificationDTO.Metadata meta = item.getMetadata();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Project Invitation");
        alert.setHeaderText("You are invited by " + meta.getInviterName() +
                " to join project " + meta.getProjectName() +
                " as a " + meta.getRoleName());

        ButtonType acceptBtn = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
        ButtonType declineBtn = new ButtonType("Decline", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(acceptBtn, declineBtn, ButtonType.CANCEL);

        Node cancelButton = alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.setVisible(false);
            cancelButton.setManaged(false);
        }

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            ButtonType chosen = result.get();
            if (chosen.equals(acceptBtn)) {
                handleInvitationAction(item.getReferenceId(), "ACCEPT", item);
            } else if (chosen.equals(declineBtn)) {
                handleInvitationAction(item.getReferenceId(), "DECLINE", item);
            }
        }
    }

    private void handleInvitationAction(Long projectMemberId, String action, NotificationDTO item) {
        notificationApi.respondToInvitation(projectMemberId, action)
                .thenAccept(success -> Platform.runLater(() -> {
                    if (success) {
                        item.setRead(true);
                        if (item.getMetadata() != null) {
                            item.getMetadata().setResponseAction(action);
                        }
                        notificationList.refresh();

                        notificationApi.markAsRead(item.getId());

                        if (MainLayoutController.getInstance() != null) {
                            MainLayoutController.getInstance().decrementBadgeCount();
                        }

                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText("ACCEPT".equals(action) ? "Successfully joined the project!" : "Invitation declined successfully.");
                        info.show();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setHeaderText("An error occurred while processing the invitation.");
                        error.show();
                    }
                }));
    }

}
