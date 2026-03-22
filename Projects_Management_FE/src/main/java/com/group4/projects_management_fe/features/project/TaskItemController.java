package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.TaskAssigneeDTO;
import com.group4.common.dto.TaskResponseDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskItemController {
    @FXML private Label taskNameLabel;
    @FXML private Label dueDateLabel; // maps to deadline

    // ĐỔI fx:id CŨ SANG 2 LÀM MỚI TỪ FXML BƯỚC 1
    @FXML private Label assigneeNameLabel;
    @FXML private Label assigneeExtraCountBadge;

    @FXML private Label priorityLabel;
    @FXML private Label statusLabel;

    public void bindData(TaskResponseDTO task) {
        // =========================================================
        // LOGIC 1: RÚT GỌN TÊN TASK (<= 35 ký tự + ...)
        // =========================================================
        String originalName = task.getName() != null ? task.getName() : "Untitled Task";
        String displayName = originalName;

        if (originalName.length() > 35) {
            displayName = originalName.substring(0, 35).stripTrailing() + "...";
            // Thêm Tooltip để khi user di chuột vào sẽ thấy tên đầy đủ
            Tooltip.install(taskNameLabel, new Tooltip(originalName));
        } else {
            // Xóa Tooltip nếu tên task ngắn (để đảm bảo tái sử dụng view không bị lỗi)
            Tooltip.install(taskNameLabel, null);
        }

        if (taskNameLabel != null) taskNameLabel.setText(displayName);

        // =========================================================
        // LOGIC 2: DEADLINE (Due Date)
        // =========================================================
        if (dueDateLabel != null) {
            if (task.getDeadline() != null) {
                // Định dạng giống ảnh mẫu: MMM dd, yyyy (ví dụ: Mar 15, 2026)
                dueDateLabel.setText(task.getDeadline().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            } else {
                dueDateLabel.setText("No Deadline");
            }
        }

        // =========================================================
        // LOGIC 3: ASSIGNEE (Hunny +2, Circle 22px)
        // =========================================================
        List<TaskAssigneeDTO> assignees = task.getAssignees();

        if (assigneeNameLabel == null || assigneeExtraCountBadge == null) return;

        if (assignees == null || assignees.isEmpty()) {
            // TRƯỜNG HỢP 0: Unassigned
            assigneeNameLabel.setText("Unassigned");
            assigneeNameLabel.setStyle("-fx-text-fill: #A0AEC0;"); // Màu xám nhạt hơn

            assigneeExtraCountBadge.setVisible(false);
            assigneeExtraCountBadge.setManaged(false); // Xóa khỏi bố cục

        } else {
            // TRƯỜNG HỢP CÓ ASSIGNEE:
            assigneeNameLabel.setStyle("-fx-text-fill: #333333;"); // Reset màu text

            // 3.1. Hiển thị Tên Assignee đầu tiên
            String firstAssigneeFullName = assignees.get(0).getFullName();
            assigneeNameLabel.setText(firstAssigneeFullName);

            // 3.2. Kiểm tra nếu có nhiều hơn 1 người
            if (assignees.size() > 1) {
                // TRƯỜNG HỢP > 1: Hunny +2
                int extraCount = assignees.size() - 1;

                assigneeExtraCountBadge.setText("+" + extraCount);
                assigneeExtraCountBadge.setVisible(true);
                assigneeExtraCountBadge.setManaged(true); // Nhét vào bố cục

            } else {
                // TRƯỜNG HỢP CHỈ CÓ 1: Chẩn đoán tên của 1 người
                assigneeExtraCountBadge.setVisible(false);
                assigneeExtraCountBadge.setManaged(false);
            }
        }

        // =========================================================
        // MÁY TRẠNG THÁI & ƯU TIÊN (Giữ nguyên)
        // =========================================================
        if (priorityLabel != null) priorityLabel.setText(task.getPriorityName());

        if (statusLabel != null) {
            statusLabel.setText(task.getStatusName());
            // Bạn có thể thêm logic tô màu cho statusLabel dựa vào StatusId/Name ở đây nếu muốn giống design Figma.
        }
    }
}