package com.group4.projects_management_fe.features.project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;

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

    @FXML
    void openNewProjectPopup(ActionEvent event) {
        // Tạm thời để rỗng hoặc in ra một dòng log để test giao diện
        System.out.println("Nút + New đã được bấm! Giao diện load thành công!");
    }
}