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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
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
            showInvitationPopup(item);
        }
        // TODO: thêm các type khác như TASK_ASSIGNED, COMMENT_ADDED...
    }

    private void showInvitationPopup(NotificationDTO item) {
        NotificationDTO.Metadata meta = item.getMetadata();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Thư mời tham gia dự án");
        alert.setHeaderText("Bạn được " + meta.getInviterName() +
                " mời tham gia dự án " + meta.getProjectName() +
                " với vai trò " + meta.getRoleName());

        ButtonType acceptBtn = new ButtonType("Chấp nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType declineBtn = new ButtonType("Từ chối", ButtonBar.ButtonData.NO);

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
            // Nếu là ButtonType.CANCEL (do bấm X hoặc ESC) -> Tự động rơi vào đây và thoát, không làm gì cả
        }
        // Nếu result không present (trường hợp hiếm) -> cũng không làm gì cả
    }

    private void handleInvitationAction(Long projectMemberId, String action, NotificationDTO item) {
        notificationApi.respondToInvitation(projectMemberId, action)
                .thenAccept(success -> Platform.runLater(() -> {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    if (success) {
                        info.setHeaderText(action.equals("ACCEPT")
                                ? "Bạn đã tham gia dự án thành công!"
                                : "Bạn đã từ chối lời mời.");
                        notificationApi.markAsRead(item.getId());
                        item.setRead(true);
                        notificationList.refresh();
                    } else {
                        info.setHeaderText("Có lỗi xảy ra khi xử lý lời mời.");
                    }
                    info.show();
                }));
    }

}
