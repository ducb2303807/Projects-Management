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
    @FXML private VBox taskGroupContentVBox; // Vùng chứa list task bên trong ScrollPane

    // === THÊM 2 BIẾN NÀY ĐỂ TRUYỀN DỮ LIỆU ===
    private AuthSessionProvider sessionProvider;
    private TaskResponseDTO currentTask;
    private Runnable reloadCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // selectAll logic
    }

    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public void setGroupName(String name) {
        groupTitledPane.setText(name);
    }

    public void setReloadCallback(Runnable reloadCallback) {
        this.reloadCallback = reloadCallback;
    }

    public void loadTasks(List<TaskResponseDTO> tasks) {
        taskGroupContentVBox.getChildren().clear();

        for (TaskResponseDTO task : tasks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/task/TaskItem.fxml"));
                HBox itemNode = loader.load();

                TaskItemController itemCtrl = loader.getController();
                itemCtrl.bindTask(task);
                itemCtrl.setSessionProvider(sessionProvider);
                itemCtrl.setCurrentTask(task);
                // Truyền reload callback xuống TaskItemController
                // để sau khi save TaskDetail, list tự reload
                itemCtrl.setReloadCallback(reloadCallback);

                taskGroupContentVBox.getChildren().add(itemNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}