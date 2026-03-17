package com.group4.projects_management_fe.features.project;

import com.group4.projects_management_fe.core.navigation.AppStageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ProjectCardController {

    @FXML private Label statusLabel;
    @FXML private Label projectTitleLabel;
    @FXML private Label moreOptionsLabel;
    @FXML private Label dateLabel;
    @FXML private Label creatorNameLabel;

    // Biến lưu trữ ID của project để truyền sang màn hình Detail
    private String currentProjectId;

    @FXML
    public void initialize() {
        // Có thể setup hiệu ứng hover cho dấu ••• ở đây nếu muốn
    }

    /**
     * Hàm này sẽ được gọi từ màn hình Danh sách (ProjectListController)
     * Mục đích: Bơm dữ liệu thật vào Card
     */
    public void bindData(String id, String title, String status, String creator, String date) {
        this.currentProjectId = id;
        int MAX_LENGTH = 40; // Bạn có thể chỉnh con số này cho phù hợp với độ rộng của Card
        if (title != null && title.length() > MAX_LENGTH) {
            title = title.substring(0, MAX_LENGTH) + "...";
        }
        this.projectTitleLabel.setText(title);
        this.statusLabel.setText(status);
        this.creatorNameLabel.setText(creator);
        this.dateLabel.setText(date);
    }
    /**
     * Xử lý sự kiện khi bấm vào dấu •••
     */
    @FXML
    private void handleOpenDetails(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/ProjectDetailsForm.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();

            // 1. Tạo Scene và set nền trong suốt (để bo góc CSS hoạt động được)
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);

            // 2. ẨN THANH TIÊU ĐỀ CỦA HỆ ĐIỀU HÀNH
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Stage mainStage = AppStageManager.getInstance().getStage();
            if (mainStage != null) {
                popupStage.initOwner(mainStage);
            }

            popupStage.initModality(Modality.WINDOW_MODAL);

            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}