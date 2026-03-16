package com.group4.projects_management_fe.features.project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProjectController implements Initializable {

    @FXML
    private HBox recentCardsContainer;

    @FXML
    private FlowPane mainCardsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRecentProjects();
        loadMainProjects();
    }

    private void loadRecentProjects() {
        try {
            for (int i = 0; i < 6; i++) {
                // Sửa lại đường dẫn nạp FXML cho đúng với cấu trúc thư mục của project bạn
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/RecentProjectCard.fxml"));
                Node recentCard = loader.load();
                recentCardsContainer.getChildren().add(recentCard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainProjects() {
        try {
            for (int i = 0; i < 5; i++) {
                // Sửa lại đường dẫn nạp FXML cho đúng với cấu trúc thư mục của project bạn
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/ProjectCard.fxml"));
                Node projectCard = loader.load();
                mainCardsContainer.getChildren().add(projectCard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @FXML
//    void openNewProjectPopup(ActionEvent event) {
//        // Tạm thời để rỗng hoặc in ra một dòng log để test giao diện
//        System.out.println("Nút + New đã được bấm! Giao diện load thành công!");
//    }

    @FXML
    public void openNewProjectPopup(ActionEvent event) {
        try {
            // Đã đổi tên file thành NewProjectForm.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewProjectForm.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("New Project");

            // Làm nền popup trong suốt để thấy được drop shadow
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            // Cấu hình cửa sổ dạng Popup chuẩn (không viền, khóa màn hình phía sau)
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}