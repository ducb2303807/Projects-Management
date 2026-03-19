package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.LookupDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.enums.LookupType;
import com.group4.projects_management_fe.core.api.LookupApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TasksViewController {

    @FXML private VBox taskCategoryVBox;

//    private Long projectId;
    private TaskApi taskApi;
    private LookupApi lookupApi;          // === THÊM ===
    private AuthSessionProvider sessionProvider;

//    public void setProjectId(Long projectId) {
//        this.projectId = projectId;
//        loadTasksData();
//    }
    private Long currentUserId;

    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;

        // Khởi tạo các API
        this.taskApi = new TaskApi(sessionProvider);
        this.lookupApi = new LookupApi(sessionProvider);

        if (this.sessionProvider != null) {
            /* * 2. Lấy userId từ SessionProvider.
             * LƯU Ý: Tùy vào cách bạn viết interface AuthSessionProvider,
             * hàm này có thể là .getUserId() hoặc .getCurrentUser().getId()
             * Bạn hãy tự điều chỉnh tên hàm cho đúng với code của bạn nhé!
             */

//            this.currentUserId = this.sessionProvider.getUserId(); // <--- CHỈNH LẠI TÊN HÀM NẾU CẦN

            // 3. Gọi hàm tải dữ liệu sau khi đã có userId
            loadTasksData();
        }
    }

    private void loadTasksData() {
        if (currentUserId == null) return;

        taskApi = new TaskApi(sessionProvider);
        lookupApi = new LookupApi(sessionProvider);

        // === SỬA: Dùng LookupApi + TaskApi đúng ===
        lookupApi.getAll(LookupType.TASK_STATUS)
                .thenCombine(taskApi.getTasksByProject(currentUserId), (statuses, tasks) -> {
                    Platform.runLater(() -> renderDynamicGroups(statuses, tasks));
                    return null;
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void renderDynamicGroups(List<LookupDTO> statuses, List<TaskResponseDTO> tasks) {
        taskCategoryVBox.getChildren().clear();

        Map<String, List<TaskResponseDTO>> groupedTasks = tasks.stream()
                .collect(Collectors.groupingBy(t -> t.getStatusName() != null ? t.getStatusName() : "UNKNOWN"));

        for (LookupDTO status : statuses) {
            String systemName = status.getName();
            String displayName = getDisplayName(systemName);

            List<TaskResponseDTO> groupTasks = groupedTasks.getOrDefault(systemName, List.of());

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/group4/projects_management_fe/features/task/TaskGroup.fxml"));
                VBox groupRoot = loader.load();

                TaskGroupController groupCtrl = loader.getController();
                groupCtrl.setGroupName(displayName);
                groupCtrl.loadTasks(groupTasks);

                taskCategoryVBox.getChildren().add(groupRoot);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getDisplayName(String systemName) {
        return switch (systemName.toUpperCase()) {
            case "TODO"       -> "To do";
            case "IN_PROGRESS"-> "On going";
            case "REVIEW"     -> "In review";
            case "DONE"       -> "Done";
            case "CANCELLED"  -> "Cancelled";
            default           -> systemName;
        };
    }

    @FXML
    private void openPopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/group4/projects_management_fe/features/task/NewTaskForm.fxml")
            );
            Parent root = loader.load();

            NewTaskFormController formController = loader.getController();
            formController.setSessionProvider(sessionProvider);

            Window ownerWindow = ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(ownerWindow);
            // TRANSPARENT: Scene fill trong suốt, chỉ .popup-background có màu overlay mờ
            popup.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            popup.setScene(scene);

            // Phủ toàn cửa sổ chủ để overlay mờ che hết nền
            popup.setWidth(ownerWindow.getWidth());
            popup.setHeight(ownerWindow.getHeight());
            popup.setX(ownerWindow.getX());
            popup.setY(ownerWindow.getY());

            formController.setPopupStage(popup);

            // Click vào rootPane (vùng mờ ngoài form card) → đóng
            root.setOnMouseClicked(e -> {
                if (e.getTarget() == root) popup.close();
            });

            popup.showAndWait();

        } catch (IOException e) {
            System.err.println("[TasksViewController] Lỗi mở NewTaskForm: " + e.getMessage());
            e.printStackTrace();
        }
    }
}