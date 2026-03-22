package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.TaskResponseDTO;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import com.group4.projects_management_fe.core.api.LookupApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import javafx.stage.Modality;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.paint.Color;
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

    private final ProjectTasksViewModel viewModel = new ProjectTasksViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private TaskApi taskApi;
    private LookupApi lookupApi;
    private AuthSessionProvider sessionProvider;

    private Long projectId;

    // Gọi hàm này khi khởi tạo màn hình từ bên ngoài
    public void initData(Long projectId, String projectName) {
        this.projectId = projectId;

        if (projectNameLabel != null) {
            projectNameLabel.setText(projectName);
        }
        viewModel.loadTasksForProject(projectId);
    }

    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
        if (sessionProvider != null) {
            this.taskApi = new TaskApi(sessionProvider);
            this.lookupApi = new LookupApi(sessionProvider);
        }
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
    private void openPopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/group4/projects_management_fe/features/task/NewTaskForm.fxml")
            );
            Parent root = loader.load();

            NewTaskFormController formController = loader.getController();
            formController.setSessionProvider(sessionProvider);
            formController.setProjectId(this.projectId);   // ← truyền projectId

            Window ownerWindow = ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(ownerWindow);
            popup.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popup.setScene(scene);

            popup.setWidth(ownerWindow.getWidth());
            popup.setHeight(ownerWindow.getHeight());
            popup.setX(ownerWindow.getX());
            popup.setY(ownerWindow.getY());

            formController.setPopupStage(popup);

            // Click ra ngoài vùng xám (rootPane) → đóng form
            root.setOnMouseClicked(e -> {
                if (e.getTarget() == root) popup.close();
            });

            popup.showAndWait();

        } catch (IOException e) {
            System.err.println("[ProjectTasksController] Lỗi mở NewTaskForm: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
