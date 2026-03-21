package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.LookupDTO;
import com.group4.common.dto.TaskAssigneeDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TaskItemController implements Initializable {

    @FXML private CheckBox taskCheckBox;
    @FXML private Label taskNameLabel;
    @FXML private Label dueDateLabel;
    @FXML private Label assigneeAvatar;
    @FXML private Label assigneeOverflow;
    @FXML private Label priorityLabel;
    @FXML private Button moreButton;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private AuthSessionProvider sessionProvider;
    private TaskResponseDTO currentTask;

//    Callback được truyền từ TasksViewController → TaskGroupController → TaskItemController
    private Runnable reloadCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public void setCurrentTask(TaskResponseDTO task) {
        this.currentTask = task;
    }

    public void setReloadCallback(Runnable reloadCallback) {
        this.reloadCallback = reloadCallback;
    }

    public void bindTask(TaskResponseDTO task) {
        this.currentTask = task;

        taskNameLabel.setText(task.getName());

        if (task.getDeadline() != null) {
            dueDateLabel.setText(task.getDeadline().format(DATE_FORMAT));
        } else {
            dueDateLabel.setText("--/--/----");
        }

        List<TaskAssigneeDTO> assignees = task.getAssignees();
        if (assignees != null && !assignees.isEmpty()) {
            String name = assignees.get(0).getFullName() != null ? assignees.get(0).getFullName() : "U";
            assigneeAvatar.setText(name.substring(0, 1).toUpperCase());
            assigneeAvatar.setVisible(true);

            int extra = assignees.size() - 1;
            assigneeOverflow.setText("+" + extra);
            assigneeOverflow.setVisible(extra > 0);
        } else {
            assigneeAvatar.setVisible(false);
            assigneeOverflow.setVisible(false);
        }

        String prio = task.getPriorityName() != null ? task.getPriorityName().toLowerCase() : "medium";
        priorityLabel.setText(task.getPriorityName() != null ? task.getPriorityName() : "None");
        priorityLabel.getStyleClass().setAll("priority-badge", "priority-" + prio);

        taskCheckBox.setSelected("DONE".equalsIgnoreCase(task.getStatusName()));
    }

    // ===================================================================
    // BẤM "⋯" → MỞ CHI TIẾT TASK
    // ===================================================================
    @FXML
    private void onMoreOptionsClicked() {
        String projectRole = "PM";
        Long projectId = currentTask.getProjectId();

        if (currentTask == null || sessionProvider == null) {
            System.err.println("Missing task or sessionProvider");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/group4/projects_management_fe/features/task/TaskDetailForm.fxml")
            );
            Parent root = loader.load();

            TaskDetailFormController controller = loader.getController();

            // 1. Truyền session
            controller.setSessionProvider(sessionProvider);

            // 2. Tạo LookupDTO cho status
            LookupDTO memberLookup = new LookupDTO();
            memberLookup.setId(currentTask.getTaskId() != null
                    ? currentTask.getTaskId().toString() : null);
            memberLookup.setName(currentTask.getStatusName());

            // 3. Truyền dữ liệu task vào form
            controller.initData(currentTask, memberLookup, projectId);

            controller.setProjectId(currentTask.getProjectId());

            // 4. Callback reload sau khi save
            controller.setOnSaveSuccessCallback(reloadCallback);

            // === TẠO STAGE TRONG SUỐT (giống NewTaskForm) ===
            Window ownerWindow = moreButton.getScene().getWindow();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(ownerWindow);
            popup.initStyle(StageStyle.TRANSPARENT);

            controller.setPopupStage(popup);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            popup.setScene(scene);
            popup.setWidth(ownerWindow.getWidth());
            popup.setHeight(ownerWindow.getHeight());
            popup.setX(ownerWindow.getX());
            popup.setY(ownerWindow.getY());

            root.setOnMouseClicked(e -> {
                if (e.getTarget() == root) popup.close();
            });

            popup.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}