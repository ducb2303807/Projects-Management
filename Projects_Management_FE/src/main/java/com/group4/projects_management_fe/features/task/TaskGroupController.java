package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.TaskResponseDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TaskGroupController implements Initializable {

    @FXML private TitledPane groupTitledPane;
    @FXML private VBox taskItemsVBox;           // VBox bên trong ScrollPane (đúng với FXML)

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // selectAll logic (giữ nếu bạn cần)
    }

    public void setGroupName(String name) {
        groupTitledPane.setText(name);
    }

    // === CHỈ GIỮ 1 METHOD NÀY (xóa cái cũ trùng lặp) ===
    public void loadTasks(List<TaskResponseDTO> tasks) {
        taskItemsVBox.getChildren().clear();
        for (TaskResponseDTO task : tasks) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/group4/projects_management_fe/features/task/TaskItem.fxml"));
                HBox itemNode = loader.load();

                TaskItemController itemCtrl = loader.getController();
                itemCtrl.bindTask(task);

                taskItemsVBox.getChildren().add(itemNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}