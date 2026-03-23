package com.group4.projects_management_fe.features.mainlayout;

import com.group4.common.dto.InvitationRequestDTO;
import com.group4.common.dto.NotificationDTO;
import com.group4.common.enums.InvitationAction;
import com.group4.projects_management_fe.core.api.InvitationApi;
import com.group4.projects_management_fe.core.api.NotificationApi;
import com.group4.projects_management_fe.core.exception.GlobalExceptionHandler;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.ocpsoft.prettytime.PrettyTime;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class NotificationsController {
    @FXML
    private ListView<NotificationDTO> notificationList;

    private final NotificationApi notificationApi;
    private final InvitationApi invitationApi;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotificationsController() {
        this.notificationApi = new NotificationApi(AppSessionManager.getInstance());
        this.invitationApi = new InvitationApi(AppSessionManager.getInstance());
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

//    private void handleNotificationClick(NotificationDTO item) {
//        if ("PROJECT_INVITATION".equals(item.getType())) {
//            String action = (item.getMetadata() != null) ? item.getMetadata().getResponseAction() : null;
//
//            if ("DECLINE".equalsIgnoreCase(action)) {
//                Alert info = new Alert(Alert.AlertType.INFORMATION);
//                info.setHeaderText("You have already declined this project invitation.");
//                info.show();
//                return;
//            }
//
//            if ("ACCEPT".equalsIgnoreCase(action)) {
//                if (MainLayoutController.getInstance() != null && item.getMetadata() != null) {
//                    Long projectId = item.getMetadata().getProjectId();
//                    String projectName = item.getMetadata().getProjectName();
//
//                    MainLayoutController.getInstance().openProjectTasksWindow(projectId, projectName);
//                }
//                return;
//            }
//
//            showInvitationPopup(item);
//        }

    /// /        else if ("MEMBER_JOINED".equals(item.getType())) {
    /// /            if (!item.isRead()) {
    /// /                notificationApi.markAsRead(item.getId());
    /// /                item.setRead(true);
    /// /                notificationList.refresh();
    /// /                if (MainLayoutController.getInstance() != null) {
    /// /                    MainLayoutController.getInstance().decrementBadgeCount();
    /// /                }
    /// /            }
    /// /
    /// /            if (MainLayoutController.getInstance() != null && item.getMetadata() != null) {
    /// /                Long projectId = item.getMetadata().getProjectId();
    /// /                String projectName = item.getMetadata().getProjectName();
    /// /
    /// /                if (projectName == null) {
    /// /                    projectName = "Project Details";
    /// /                }
    /// /                System.out.println("Opening project: " + projectId + " - " + projectName); // log debug
    /// /                MainLayoutController.getInstance().openProjectTasksWindow(projectId, projectName);
    /// /            }
    /// /        }
//    }
    private void handleNotificationClick(NotificationDTO item) {
        if (item.isRead()) return;
        if ("PROJECT_INVITATION".equals(item.getType())) {
            Map<String, Object> metaMap = item.getMetadata();

            // Thêm check null an toàn
            if (metaMap == null) {
                metaMap = new HashMap<>();
            }

            String action = (String) metaMap.get("responseAction");

            if ("DECLINE".equalsIgnoreCase(action)) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText("You have already declined this project invitation.");
                info.show();
                return;
            }

            if ("ACCEPT".equalsIgnoreCase(action)) {
                if (MainLayoutController.getInstance() != null) {
                    Long projectId = getLong(metaMap, "projectId");
                    String projectName = (String) metaMap.get("projectName");

                    if (projectId != null) {
                        MainLayoutController.getInstance().openProjectTasksWindow(projectId, projectName);
                    }
                }
                return;
            }

            showInvitationPopup(item, metaMap);
        }

        notificationApi.markAsRead(item.getId());
        item.setRead(true);
        notificationList.refresh();
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().decrementBadgeCount();
        }
    }

    private void showInvitationPopup(NotificationDTO item, Map<String, Object> metaMap) {
        String inviterName = (String) metaMap.getOrDefault("inviterName", "Unknown");
        String projectName = (String) metaMap.getOrDefault("projectName", "Project");
        String roleName = (String) metaMap.getOrDefault("roleName", "Member");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Project Invitation");
        alert.setHeaderText("You are invited by " + inviterName +
                " to join project " + projectName +
                " as a " + roleName);

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
        var enumAction = InvitationAction.valueOf(action);
        InvitationRequestDTO dto = new InvitationRequestDTO(enumAction);

        invitationApi.handleInvitation(projectMemberId, dto)
                .thenAccept(res -> Platform.runLater(() -> {

                    item.setRead(true);

                    // Cập nhật trạng thái trực tiếp vào Map (Không cần Serialize/Deserialize nữa)
                    if (item.getMetadata() == null) {
                        item.setMetadata(new HashMap<>());
                    }
                    item.getMetadata().put("responseAction", action);

                    notificationList.refresh();
                    notificationApi.markAsRead(item.getId());

                    if (MainLayoutController.getInstance() != null) {
                        MainLayoutController.getInstance().decrementBadgeCount();
                    }

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("ACCEPT".equals(action) ? "Successfully joined the project!" : "Invitation declined successfully.");
                    info.show();

                }))
                .exceptionally(ex -> {
                    GlobalExceptionHandler.handleException(ex);
                    return null;
                });
    }

//    private void showInvitationPopup(NotificationDTO item) {
//        NotificationDTO.Metadata meta = item.getMetadata();
//
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Project Invitation");
//        alert.setHeaderText("You are invited by " + meta.getInviterName() +
//                " to join project " + meta.getProjectName() +
//                " as a " + meta.getRoleName());
//
//        ButtonType acceptBtn = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
//        ButtonType declineBtn = new ButtonType("Decline", ButtonBar.ButtonData.NO);
//
//        alert.getButtonTypes().setAll(acceptBtn, declineBtn, ButtonType.CANCEL);
//
//        Node cancelButton = alert.getDialogPane().lookupButton(ButtonType.CANCEL);
//        if (cancelButton != null) {
//            cancelButton.setVisible(false);
//            cancelButton.setManaged(false);
//        }
//
//        Optional<ButtonType> result = alert.showAndWait();
//
//        if (result.isPresent()) {
//            ButtonType chosen = result.get();
//            if (chosen.equals(acceptBtn)) {
//                handleInvitationAction(item.getReferenceId(), "ACCEPT", item);
//            } else if (chosen.equals(declineBtn)) {
//                handleInvitationAction(item.getReferenceId(), "DECLINE", item);
//            }
//        }
//    }

//    private void handleInvitationAction(Long projectMemberId, String action, NotificationDTO item) {
//
//        var enumAction = InvitationAction.valueOf(action);
//        InvitationRequestDTO dto = new InvitationRequestDTO(enumAction);
//
//        invitationApi.handleInvitation(projectMemberId, dto)
//                .thenAccept(() -> Platform.runLater(() -> {
//
//
//
//                        item.setRead(true);
//                        if (item.getMetadata() != null) {
//                            item.getMetadata().setResponseAction(action);
//                        }
//                        notificationList.refresh();
//
//                        notificationApi.markAsRead(item.getId());
//
//                        if (MainLayoutController.getInstance() != null) {
//                            MainLayoutController.getInstance().decrementBadgeCount();
//                        }
//
//                        Alert info = new Alert(Alert.AlertType.INFORMATION);
//                        info.setHeaderText("ACCEPT".equals(action) ? "Successfully joined the project!" : "Invitation declined successfully.");
//                        info.show();
//

    /// /                        Alert error = new Alert(Alert.AlertType.ERROR);
    /// /                        error.setHeaderText("An error occurred while processing the invitation.");
    /// /                        error.show();
//
//                }));
//    }
    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

}
