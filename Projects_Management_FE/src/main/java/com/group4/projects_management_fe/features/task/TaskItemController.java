package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.TaskAssigneeDTO;
import com.group4.common.dto.TaskResponseDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TaskItemController implements Initializable {

    @FXML private CheckBox taskCheckBox;
    @FXML private Label taskNameLabel;
    @FXML private Label commentCountLabel;
    @FXML private Label dueDateLabel;
    @FXML private Label assigneeAvatar;
    @FXML private Label assigneeOverflow;
    @FXML private Label priorityLabel;
    @FXML private Button moreButton;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    public void bindTask(TaskResponseDTO task) {
        taskNameLabel.setText(task.getName());

        // Comment count (hiện tại DTO chưa có → tạm 0, sau thêm vào DTO)
        commentCountLabel.setText("💬 " + (task.getCommentCount() != null ? task.getCommentCount() : 0));

        // Due date → deadline (từ TaskBaseDTO)
        if (task.getDeadline() != null) {
            dueDateLabel.setText(task.getDeadline().format(DATE_FORMAT));
        }

        // Assignee (List<TaskAssigneeDTO>)
        List<TaskAssigneeDTO> assignees = task.getAssignees();
        if (!assignees.isEmpty()) {
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

        // Priority
        String prio = task.getPriorityName() != null ? task.getPriorityName().toLowerCase() : "medium";
        priorityLabel.setText(task.getPriorityName());
        priorityLabel.getStyleClass().setAll("priority-badge", "priority-" + prio);

        taskCheckBox.setSelected("DONE".equalsIgnoreCase(task.getStatusName()));
    }

    @FXML
    private void onMoreOptionsClicked() {
        System.out.println("Open detail for task: " + taskNameLabel.getText());
    }
}