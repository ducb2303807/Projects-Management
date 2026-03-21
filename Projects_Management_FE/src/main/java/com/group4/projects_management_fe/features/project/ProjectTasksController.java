package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.TaskResponseDTO;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ProjectTasksController {

    @FXML private Label projectNameLabel;
    @FXML private TextField searchInput;
    @FXML private ComboBox<String> sortTaskComboBox;

    // Đây là VBox nằm bên trong TaskListInProject.fxml
    @FXML private VBox taskItemsContainer;

    private final ProjectTasksViewModel viewModel = new ProjectTasksViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();

    // Gọi hàm này khi khởi tạo màn hình từ bên ngoài
    public void initData(Long projectId, String projectName) {
        if (projectNameLabel != null) {
            projectNameLabel.setText(projectName);
        }
        viewModel.loadTasksForProject(projectId);
    }

    @FXML
    public void initialize() {
        // Setup Sort ComboBox
        if (sortTaskComboBox != null) {
            sortTaskComboBox.getItems().addAll("Newest", "Oldest", "Deadline (Earliest)");
            sortTaskComboBox.setValue("Newest");
            sortTaskComboBox.valueProperty().addListener((obs, oldV, newV) -> viewModel.setSortType(newV));
        }

        // Setup Search Input
        if (searchInput != null) {
            searchInput.textProperty().addListener((obs, oldV, newV) -> viewModel.setSearchKeyword(newV));
        }

        // Lắng nghe dữ liệu lọc trả về và vẽ giao diện
        disposables.add(viewModel.filteredTasksObservable().subscribe(tasks -> {
            Platform.runLater(() -> {
                if (taskItemsContainer == null) return;

                // Xóa danh sách cũ
                taskItemsContainer.getChildren().clear();

                // Sinh ra các dòng FXML mới
                for (TaskResponseDTO task : tasks) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/task/TaskItemInProject.fxml"));
                        Parent taskItemNode = loader.load();

                        TaskItemController itemController = loader.getController();
                        itemController.bindData(task);

                        taskItemsContainer.getChildren().add(taskItemNode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }));
    }

    // Đừng quên clear memory khi đóng màn hình
    public void cleanup() {
        disposables.clear();
    }
}
