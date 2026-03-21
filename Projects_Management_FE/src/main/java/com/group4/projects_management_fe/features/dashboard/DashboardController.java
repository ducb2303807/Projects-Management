package com.group4.projects_management_fe.features.dashboard;

import com.group4.common.dto.ProjectResponseDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management_fe.features.task.TaskDetailFormController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.*;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private VBox calendarBox;

    @FXML
    private ListView<TaskResponseDTO> taskListView;

    /* -------- POPUP -------- */

    @FXML
    private Pane overlay;

    @FXML
    private HBox createProjectPopup;

    @FXML
    private Button statusBtn;

    private ContextMenu statusMenu;

    //service
    private ProjectApi projectApi;

    private TaskApi taskApi;

    @FXML
    private Label totalProjectLabel;

    @FXML
    private Label totalTaskLabel;

    @FXML
    private Label assignedTaskLabel;

    @FXML
    private Label completedTaskLabel;

    @FXML
    private TableView<ProjectResponseDTO> projectTable;

    @FXML
    private TableColumn<ProjectResponseDTO, String> colName;

    @FXML
    private TableColumn<ProjectResponseDTO, String> colManager;

    @FXML
    private TableColumn<ProjectResponseDTO, String> colDueDate;

    @FXML
    private TableColumn<ProjectResponseDTO, String> colStatus;

    private void loadProjects() {

        projectApi.getMyProjects()
                .thenAccept(projects -> {

                    Platform.runLater(() -> {
                        projectTable.setItems(FXCollections.observableArrayList(projects));
                        totalProjectLabel.setText(String.valueOf(projects.size()));
                    });

                    loadTasksFromProjects(projects);

                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void loadTasksFromProjects(List<ProjectResponseDTO> projects) {

        if (projects == null || projects.isEmpty()) {
            Platform.runLater(() -> {
                totalTaskLabel.setText("0");
                assignedTaskLabel.setText("0");
                completedTaskLabel.setText("0");
            });
            return;
        }

        List<CompletableFuture<List<TaskResponseDTO>>> futures = projects.stream()
                .map(p -> projectApi.getTasksByProjectId(p.getId())) //
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {

                    List<TaskResponseDTO> allTasks = futures.stream()
                            .flatMap(f -> f.join().stream())
                            .toList();

                    int totalTasks = allTasks.size();

                    Long currentUserId = AppSessionManager
                            .getInstance()
                            .getCurrentUser()
                            .getId();

                    List<TaskResponseDTO> myTasks = allTasks.stream()
                            .filter(task ->
                                    task.getAssignees() != null &&
                                            task.getAssignees().stream()
                                                    .anyMatch(a -> a.getUserId().equals(currentUserId))
                            )
                            .toList();

                    int assignedTasks = myTasks.size();

                    int completedTasks = (int) myTasks.stream()
                            .filter(task ->
                                    task.getStatusName() != null &&
                                            task.getStatusName().equalsIgnoreCase("done")
                            )
                            .count();

                    Platform.runLater(() -> {
                        totalTaskLabel.setText(String.valueOf(totalTasks));
                        assignedTaskLabel.setText(String.valueOf(assignedTasks));
                        completedTaskLabel.setText(String.valueOf(completedTasks));
                    });
                    loadTodayTasks(allTasks);
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private HBox createTaskItem(TaskResponseDTO task) {

        HBox container = new HBox();
        container.setSpacing(10);
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("task-item");

        Label name = new Label(task.getName());
        name.getStyleClass().add("task-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(task.getStatusName());
        status.getStyleClass().add("status-badge");

        String normalized = task.getStatusName().toLowerCase();

        switch (normalized) {

            case "done":
                status.getStyleClass().add("status-completed");
                break;

            case "in_progress":
                status.getStyleClass().add("status-active");
                break;

            case "under_review":
                status.getStyleClass().add("status-on-hold");
                break;

            case "cancelled":
                status.getStyleClass().add("status-cancelled");
                break;

            case "todo":
            default:
                status.getStyleClass().add("status-planning");
                break;
        }

        container.getChildren().addAll(name, spacer, status);

        return container;
    }

    private void setupProjectTable() {

        projectTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProjectName())
        );

        colManager.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUserCreatedFullName())
        );

        colDueDate.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() != null) {
                return new SimpleStringProperty(
                        data.getValue().getCreatedAt().toLocalDate().toString()
                );
            }
            return new SimpleStringProperty("");
        });

        // 🟢 Status text
        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatusName())
        );

        colStatus.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(status);
                badge.getStyleClass().add("status-badge");

                String normalized = status.toLowerCase();

                switch (normalized) {

                    case "completed":
                        badge.getStyleClass().add("status-completed");
                        break;

                    case "active":
                        badge.getStyleClass().add("status-active");
                        break;

                    case "on_hold":
                        badge.getStyleClass().add("status-on-hold");
                        break;

                    case "cancelled":
                        badge.getStyleClass().add("status-cancelled");
                        break;

                    case "planning":
                    default:
                        badge.getStyleClass().add("status-planning");
                        break;
                }
                setGraphic(badge);
                setText(null);
            }
        });
    }

    private void loadTodayTasks(List<TaskResponseDTO> allTasks) {
        if (allTasks == null) return;

        Long currentUserId = AppSessionManager
                .getInstance()
                .getCurrentUser()
                .getId();

        LocalDate today = LocalDate.now();

        List<TaskResponseDTO> taskItems = allTasks.stream()
                .filter(task -> task.getAssignees() != null)
                .filter(task -> task.getAssignees().stream()
                        .anyMatch(a -> a.getUserId() != null && a.getUserId().equals(currentUserId)))
                .filter(task -> task.getDeadline() != null)
                .filter(task -> task.getDeadline().toLocalDate().isEqual(today))
                .toList();

        Platform.runLater(() -> {
            taskListView.setItems(FXCollections.observableArrayList(taskItems));
        });
    }

    private void setupTaskListView() {

        // render UI cho từng item
        taskListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(TaskResponseDTO task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createTaskItem(task));
                }
            }
        });

        // bắt sự kiện click
        taskListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TaskResponseDTO selectedTask =
                        taskListView.getSelectionModel().getSelectedItem();

                if (selectedTask != null) {
                    openTaskDetail(selectedTask);
                }
            }
        });
    }

    private void openTaskDetail(TaskResponseDTO task) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/group4/projects_management_fe/features/task/TaskDetailForm.fxml")
            );

            Parent root = loader.load();

            TaskDetailFormController controller = loader.getController();

            controller.setSessionProvider(AppSessionManager.getInstance());

            controller.initData(task, null);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Task Detail");

            controller.setPopupStage(stage);

            stage.showAndWait();
            loadProjects();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //service

    @FXML
    private void showStatusMenu() {
        statusMenu.show(statusBtn, Side.BOTTOM, 0, 5);
    }

    @FXML
    public void initialize() {
        AuthSessionProvider sessionProvider = AppSessionManager.getInstance();
        projectApi = new ProjectApi(sessionProvider);
        taskApi = new TaskApi(sessionProvider);

        setupTaskListView();
        loadProjects();
        setupProjectTable();

        DatePicker datePicker = new DatePicker();

        DatePickerSkin skin = new DatePickerSkin(datePicker);
        Node calendar = skin.getPopupContent();

        calendarBox.getChildren().add(calendar);
    }
}