package com.group4.projects_management_fe.features.mainlayout;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management_fe.core.api.NotificationApi;
import com.group4.projects_management_fe.core.session.AppSessionManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationsController {
    @FXML
    private ListView<NotificationDTO> notificationList;

    private final NotificationApi notificationApi;

    public NotificationsController() {
        this.notificationApi = new NotificationApi(AppSessionManager.getInstance());
    }

    public void initialize() {
        // Gọi API lấy danh sách thông báo
        notificationApi.getNotificationsForUser().thenAccept(notifications -> {
            Platform.runLater(() -> {
                if (notifications == null || notifications.isEmpty()) {
                    // Tạo một item giả để hiển thị "No new notifications"
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
                error.setTitle("Lỗi tải thông báo");
                error.setRead(true);
                notificationList.getItems().add(error);
            });
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            return null;
        });

        // Gắn cell factory để custom UI cho từng item
        notificationList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(NotificationDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Format thời gian
                    String timeText = item.getCreatedDate() != null
                            ? formatDate(item.getCreatedDate())
                            : "";

                    // Hiển thị title + thời gian
                    setText(item.getTitle() + (timeText.isEmpty() ? "" : " - " + timeText));

                    // CSS theo trạng thái read
                    if (!item.isRead()) {
                        setStyle("-fx-font-weight: bold; -fx-background-color: #e6f7ff; -fx-text-fill: black;");
                    } else {
                        setStyle("-fx-font-weight: normal; -fx-text-fill: gray;");
                    }

                    // Sự kiện click
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
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa xong";
        if (duration.toMinutes() < 60) return duration.toMinutes() + " phút trước";
        if (duration.toHours() < 24) return "Hôm nay lúc " + createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void handleNotificationClick(NotificationDTO item) {
        // Ví dụ xử lý theo type
        if ("PROJECT_INVITATION".equals(item.getType())) {
            if (!item.isRead()) {
                notificationApi.markAsRead(item.getId());
                // TODO: hiện popup mời tham gia dự án, dùng metadata.roleName + metadata.projectName
            } else {
                // TODO: điều hướng sang trang dự án, lấy projectId từ metadata
            }
        }
        // Có thể thêm các type khác ở đây
    }
}
