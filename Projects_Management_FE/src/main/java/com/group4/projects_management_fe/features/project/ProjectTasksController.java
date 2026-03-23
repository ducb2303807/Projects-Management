package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import com.group4.projects_management_fe.features.task.NewTaskFormController;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class ProjectTasksController {

    @FXML private Label projectNameLabel;
    @FXML private TextField searchInput;
    @FXML private ComboBox<String> sortTaskComboBox;

    // Đây là VBox nằm bên trong TaskListInProject.fxml
    @FXML private VBox taskItemsContainer;

    @FXML private Button cancelBtn;

    private Long currentProjectId;

    private final ProjectTasksViewModel viewModel = new ProjectTasksViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();

    // Gọi hàm này khi khởi tạo màn hình từ bên ngoài
    public void initData(Long projectId, String projectName) {
        this.currentProjectId = projectId;
        if (projectNameLabel != null) {
            projectNameLabel.setText(projectName);
        }
        viewModel.loadTasksForProject(projectId);
    }

    @FXML
    public void initialize() {
        // Setup Sort ComboBox
        if (sortTaskComboBox != null) {
            // Thay thế dòng addAll cũ bằng dòng này:
            sortTaskComboBox.getItems().addAll(
                    "Newest", "Oldest", "Deadline (Earliest)",
                    "Status A-Z", "Priority A-Z", "Name A-Z", "Name Z-A"
            );

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

    @FXML
    private void handleCancel(ActionEvent event) {
        // Dọn dẹp rác, ngắt kết nối API
        cleanup();

        // Ép kiểu và đóng popup hiện tại (Code rất ngắn gọn)
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    // Đừng quên clear memory khi đóng màn hình
    public void cleanup() {
        disposables.clear();
    }

    @FXML
    public void handleSortClick(ActionEvent event) {
        if (sortTaskComboBox != null) {
            boolean isVisible = sortTaskComboBox.isVisible();

            // Đảo ngược trạng thái hiển thị
            sortTaskComboBox.setVisible(!isVisible);
            sortTaskComboBox.setManaged(!isVisible);

            // Nếu vừa được bật lên -> Xổ dropdown ra luôn
            if (!isVisible) {
                sortTaskComboBox.show();
            }
        }
    }

    @FXML
    private void handleCreateNewTask(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/task/NewTaskForm.fxml"));
            Parent root = loader.load();

            NewTaskFormController controller = loader.getController();
            controller.setSessionProvider(AppSessionManager.getInstance());
            controller.getViewModel().setProjectId(this.currentProjectId);

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
            popup.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            popup.setScene(scene);

            // === PHẦN QUAN TRỌNG: DÙNG KÍCH THƯỚC TỪ FXML ===
            popup.setResizable(false);  // không cho resize
            popup.setWidth(780);
            popup.setHeight(450);

            // Căn giữa popup trên cửa sổ cha
            Window owner = popup.getOwner();
            popup.setX(owner.getX() + (owner.getWidth() - 780) / 2);
            popup.setY(owner.getY() + (owner.getHeight() - 450) / 2);

            controller.setPopupStage(popup);
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot open Task create form!");
        }
    }

    private void loadProjectTasks() {
        if (this.currentProjectId != null) {
            viewModel.loadTasksForProject(this.currentProjectId);
        }
    }
}
