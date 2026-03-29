package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.LookupDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import com.group4.projects_management_fe.features.task.NewTaskFormController;
import com.group4.projects_management_fe.features.task.TaskDetailFormController;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class ProjectTasksController {

    @FXML private Label projectNameLabel;
    @FXML private TextField searchInput;
    @FXML private ComboBox<String> sortTaskComboBox;
    @FXML private Button newTaskBtn;

    // Đây là VBox nằm bên trong TaskListInProject.fxml
    @FXML private VBox taskItemsContainer;
    @FXML private Button cancelBtn;

    private Long currentProjectId;

    private final ProjectTasksViewModel viewModel = new ProjectTasksViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public void initData(Long projectId, String projectName) {
        this.currentProjectId = projectId;
        if (projectNameLabel != null) {
            projectNameLabel.setText(projectName);
        }
        viewModel.loadTasksForProject(projectId);
        viewModel.checkUserRole(projectId);
    }

    @FXML
    public void initialize() {
        // Setup Sort ComboBox
        if (sortTaskComboBox != null) {
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

        disposables.add(viewModel.getCanCreateTask().subscribe(canCreate -> {
            Platform.runLater(() -> {
                if (newTaskBtn != null) {
                    newTaskBtn.setVisible(canCreate);
                    newTaskBtn.setManaged(canCreate);
                }
            });
        }));

        // Lắng nghe dữ liệu lọc trả về và vẽ giao diện
        disposables.add(viewModel.filteredTasksObservable().subscribe(tasks -> {
            Platform.runLater(() -> {
                if (taskItemsContainer == null) return;
                taskItemsContainer.getChildren().clear();

                for (TaskResponseDTO task : tasks) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                                "/com/group4/projects_management_fe/features/task/TaskItemInProject.fxml"));
                        Parent taskItemNode = loader.load();

                        TaskItemController itemController = loader.getController();
                        itemController.bindData(task);

                        // ── Hover effect bằng CSS class ──────────────────────────────
                        // Thêm class "task-row" (định nghĩa trong project-tasks.css)
                        // Khi hover: JavaFX tự áp dụng selector ".task-row:hover"
                        taskItemNode.getStyleClass().add("task-row");

                        // ── Click → mở Task Detail với full assignee management ───────
                        taskItemNode.setOnMouseClicked(e -> openTaskDetail(task, taskItemNode));

                        taskItemsContainer.getChildren().add(taskItemNode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }));
    }

    // ── Mở Task Detail từ ProjectTasks (CÓ thêm/xóa assignee) ──────────────
    private void openTaskDetail(TaskResponseDTO task, Node sourceNode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/group4/projects_management_fe/features/task/TaskDetailForm.fxml"));
            Parent root = loader.load();

            TaskDetailFormController controller = loader.getController();
            controller.setSessionProvider(AppSessionManager.getInstance());
            controller.setAssigneeManagementEnabled(true);

            LookupDTO memberLookup = buildCurrentMemberLookup();

            controller.initData(task, memberLookup, currentProjectId);
            controller.setOnSaveSuccessCallback(this::loadProjectTasks);
            controller.setProjectContext(currentProjectId);

            // Mở popup phủ toàn màn hình
            Window currentWindow = sourceNode.getScene().getWindow();
            Window mainWindow = getRootWindow(currentWindow);

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(currentWindow);
            popup.initStyle(StageStyle.TRANSPARENT);

            controller.setPopupStage(popup);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popup.setScene(scene);

            popup.setWidth(mainWindow.getWidth());
            popup.setHeight(mainWindow.getHeight());
            popup.setX(mainWindow.getX());
            popup.setY(mainWindow.getY());

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể mở Task Detail!");
        }
    }

    /**
     * Xây dựng LookupDTO đại diện cho user hiện đang đăng nhập.
     * ID = userId của session, Name = fullName hoặc username (dùng làm avatar chữ đầu).
     */
    private LookupDTO buildCurrentMemberLookup() {
        LookupDTO lookup = new LookupDTO();
        try {
            var currentUser = AppSessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                lookup.setId(String.valueOf(currentUser.getId()));
                String name = (currentUser.getFullName() != null && !currentUser.getFullName().isBlank())
                        ? currentUser.getFullName()
                        : currentUser.getUsername();
                lookup.setName(name != null ? name : "U");
            }
        } catch (Exception ex) {
            // Fallback nếu session bị lỗi
            lookup.setId("0");
            lookup.setName("User");
        }
        return lookup;
    }

    /**
     * Truy ngược lên cửa sổ gốc (Main Window) để lấy kích thước đúng cho popup.
     */
    private Window getRootWindow(Window window) {
        Window root = window;
        while (root instanceof Stage && ((Stage) root).getOwner() != null) {
            root = ((Stage) root).getOwner();
        }
        return root;
    }

    // ── Các handler có sẵn ───────────────────────────────────────────────────

    @FXML
    private void handleCancel(ActionEvent event) {
        cleanup();
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void cleanup() {
        disposables.clear();
    }

    @FXML
    public void handleSortClick(ActionEvent event) {
        if (sortTaskComboBox != null) {
            boolean isVisible = sortTaskComboBox.isVisible();
            sortTaskComboBox.setVisible(!isVisible);
            sortTaskComboBox.setManaged(!isVisible);
            if (!isVisible) {
                sortTaskComboBox.show();
            }
        }
    }

    @FXML
    private void handleCreateNewTask(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/group4/projects_management_fe/features/task/NewTaskForm.fxml"));
            Parent root = loader.load();

            NewTaskFormController controller = loader.getController();
            controller.setSessionProvider(AppSessionManager.getInstance());
            controller.setProjectContext(this.currentProjectId);
            controller.getViewModel().setProjectId(this.currentProjectId);
            controller.setOnSaveSuccessCallback(this::loadProjectTasks);

            Window currentWindow = ((Node) event.getSource()).getScene().getWindow();
            Window mainWindow = getRootWindow(currentWindow);

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(currentWindow);
            popup.initStyle(StageStyle.TRANSPARENT);

            controller.setPopupStage(popup);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popup.setScene(scene);

            popup.setWidth(mainWindow.getWidth());
            popup.setHeight(mainWindow.getHeight());
            popup.setX(mainWindow.getX());
            popup.setY(mainWindow.getY());

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