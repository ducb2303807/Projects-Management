package com.group4.projects_management_fe.features.project;

import com.group4.projects_management_fe.core.navigation.AppStageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public class ProjectCardController {

    @FXML private Label statusLabel;
    @FXML private Label projectTitleLabel;
    @FXML private Label moreOptionsLabel;
    @FXML private Label dateLabel;
    @FXML private Label creatorNameLabel;
    @FXML private AnchorPane rootCardPane;

    private String currentProjectId;

    private Runnable onProjectUpdatedCallback;

    @FXML
    public void initialize() {
        if (rootCardPane != null) {
            rootCardPane.setOnMouseClicked(event -> {
                handleCardClick(event);
            });
        }
        System.out.println("Tải thành công 1 dự án");
    }

    private Runnable onAddToRecentCallback;
    public void setOnProjectUpdatedCallback(Runnable callback) {
        this.onProjectUpdatedCallback = callback;
    }
    public void setOnAddToRecentCallback(Runnable callback) {
        this.onAddToRecentCallback = callback;
    }

    public void bindData(String id, String title, String status, String creator, String date) {
        this.currentProjectId = id;
        int MAX_LENGTH = 40;
        if (title != null && title.length() > MAX_LENGTH) {
            title = title.substring(0, MAX_LENGTH) + "...";
        }
        this.projectTitleLabel.setText(title);
        this.statusLabel.setText(status);
        this.creatorNameLabel.setText(creator);
        this.dateLabel.setText(date);
    }

    @FXML
    private void handleOpenDetails(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/ProjectDetailsForm.fxml"));
            Parent root = loader.load();

            if (onAddToRecentCallback != null) {
                onAddToRecentCallback.run();
            }

            // LẤY CONTROLLER VÀ TRUYỀN ID SANG
            ProjectDetailsFormController detailsController = loader.getController();
            if (this.currentProjectId != null && !this.currentProjectId.trim().isEmpty()) {
                detailsController.initData(Long.valueOf(this.currentProjectId));
            } else {
                System.err.println("LỖI: currentProjectId bị null hoặc trống!");
                return;
            }

            Stage popupStage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Stage mainStage = AppStageManager.getInstance().getStage();
            if (mainStage != null) {
                popupStage.initOwner(mainStage);
            }
            popupStage.showAndWait();
            if (onProjectUpdatedCallback != null) {
                onProjectUpdatedCallback.run(); // Báo cho ProjectController load lại data
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCardClick(MouseEvent event) { // Đổi thành public

        // 1. Lấy phần tử thực sự bị click (thường là cái chữ "•••")
        javafx.scene.Node target = (javafx.scene.Node) event.getTarget();

        // 2. Dò ngược lên các thẻ cha để kiểm tra
        while (target != null) {
            if (target == moreOptionsLabel || target instanceof javafx.scene.control.Button) {
                return; // Bắt trúng nút 3 chấm -> Quay xe, KHÔNG mở danh sách Task
            }
            target = target.getParent();
        }

        System.out.println("====== ĐÃ CLICK VÀO CARD! ID: " + this.currentProjectId + " ======");

        // 3. THÊM GỌI CALLBACK TẠI ĐÂY (trước hoặc sau khi showAndWait đều được)
        if (onAddToRecentCallback != null) {
            onAddToRecentCallback.run();
        }

        // Tránh bị đè sự kiện click khi bấm vào nút 3 chấm Options
//        if (event.getTarget() == moreOptionsLabel || event.getTarget() instanceof Button) {
//            return;
//        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/ProjectTasks.fxml"));
            Parent root = loader.load();

            // Truyền dữ liệu sang Controller
            ProjectTasksController controller = loader.getController();
            controller.initData(Long.valueOf(this.currentProjectId), this.projectTitleLabel.getText());

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