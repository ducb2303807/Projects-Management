package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
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

    // === THÊM 2 BIẾN NÀY ĐỂ TRUYỀN DỮ LIỆU ===
    private AuthSessionProvider sessionProvider;
    private TaskResponseDTO currentTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // selectAll logic (giữ nếu bạn cần)
    }

    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public void setGroupName(String name) {
        groupTitledPane.setText(name);
    }

    @FXML private VBox taskGroupContentVBox; // Vùng chứa list task bên trong ScrollPane

    public void loadTasks(List<TaskResponseDTO> tasks) {
        taskGroupContentVBox.getChildren().clear();   // dùng fx:id đúng

        for (TaskResponseDTO task : tasks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/task/TaskItem.fxml"));
                HBox itemNode = loader.load();

                TaskItemController itemCtrl = loader.getController();
                itemCtrl.bindTask(task);
                itemCtrl.setSessionProvider(this.sessionProvider);
                itemCtrl.setCurrentTask(task);

                taskGroupContentVBox.getChildren().add(itemNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}