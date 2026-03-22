package com.group4.projects_management_fe.features.project;

import com.group4.projects_management_fe.features.project.ProjectTasksController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class RecentProjectCardController {

    // Khớp với fx:id trong file RecentProjectCard.fxml của bạn
    @FXML private Label lblProjectName;

    // Biến ngầm để lưu ID dự án (phục vụ cho việc click)
    private String currentProjectId;

    // Nạp dữ liệu vào thẻ: Chỉ cần ID và Tên
    public void bindData(String id, String projectName) {
        this.currentProjectId = id;

        // Vì thẻ này khá nhỏ, nếu tên dài quá thì cắt bớt bằng "..."
        int MAX_LENGTH = 25;
        String displayName = (projectName != null && projectName.length() > MAX_LENGTH)
                ? projectName.substring(0, MAX_LENGTH) + "..."
                : projectName;

        if (lblProjectName != null) {
            lblProjectName.setText(displayName);
        }
    }

    // Xử lý sự kiện khi user click vào thẻ
    @FXML
    private void handleCardClick(MouseEvent event) {
        if (currentProjectId == null || currentProjectId.trim().isEmpty()) {
            System.err.println("Lỗi: Không tìm thấy Project ID khi click vào thẻ Recent Project!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/ProjectTasks.fxml"));
            Parent root = loader.load();

            // Truyền dữ liệu sang Controller
            ProjectTasksController controller = loader.getController();
            controller.initData(Long.valueOf(this.currentProjectId), this.lblProjectName.getText());

            Stage popupStage = new Stage();
            Scene scene = new Scene(root);

            // Nếu thiết kế form của bạn có bo góc, cần set Transparent để không bị viền đen
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            popupStage.setScene(scene);

            // ====================================================================
            // 1. Ẩn thanh Title (thanh chứa nút X, -, phóng to) và không cho resize
            popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT); // Hoặc dùng StageStyle.UNDECORATED

            // 2. Chặn tương tác với phần bên ngoài màn hình (Modality)
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // 3. Set Owner là cửa sổ chính (để popup luôn nằm trên cùng)
            Stage mainStage = com.group4.projects_management_fe.core.navigation.AppStageManager.getInstance().getStage();
            if (mainStage != null) {
                popupStage.initOwner(mainStage);
            }
            // ====================================================================

            popupStage.showAndWait(); // Thay show() bằng showAndWait() nếu muốn chặn hoàn toàn luồng

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}