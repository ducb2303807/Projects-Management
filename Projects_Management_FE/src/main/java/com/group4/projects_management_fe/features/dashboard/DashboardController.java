package com.group4.projects_management_fe.features.dashboard;

import com.group4.common.dto.ProjectResponseDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import com.group4.projects_management_fe.features.task.TaskDetailFormController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DashboardController {

    @FXML
    private VBox calendarBox;

    @FXML
    private TableView<TaskResponseDTO> todayTaskTable;

    @FXML
    private TableColumn<TaskResponseDTO, String> colTaskName;

    @FXML
    private TableColumn<TaskResponseDTO, String> colTaskDeadline;

    @FXML
    private TableColumn<TaskResponseDTO, String> colTaskStatus;

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

        projectApi.getMyProjects(false)
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
                .map(p -> projectApi.getTasksByProjectId(p.getId(),false)) //
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

    private void setupTodayTaskTable() {

        todayTaskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colTaskName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName())
        );

        colTaskDeadline.setCellValueFactory(data -> {
            if (data.getValue().getDeadline() != null) {
                return new SimpleStringProperty(
                        data.getValue().getDeadline().toLocalDate().toString()
                );
            }
            return new SimpleStringProperty("No date");
        });

        colTaskStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatusName())
        );

        colTaskStatus.setCellFactory(column -> new TableCell<>() {
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
                    case "done":
                        badge.getStyleClass().add("status-completed");
                        break;
                    case "on going":
                        badge.getStyleClass().add("status-on-going");
                        break;
                    case "under review":
                        badge.getStyleClass().add("status-in-review");
                        break;
                    case "cancelled":
                        badge.getStyleClass().add("status-cancelled");
                        break;
                    case "delayed":
                        badge.getStyleClass().add("status-delayed");
                        break;
                    default:
                        badge.getStyleClass().add("status-planning");
                        break;
                }

                setGraphic(badge);
                setText(null);
            }
        });

        todayTaskTable.setRowFactory(tv -> {
            TableRow<TaskResponseDTO> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    TaskResponseDTO clickedTask = row.getItem();
                    openTaskDetail(clickedTask);
                }
            });

            return row;
        });
    }

    @FXML
    private void handleResetTodayTasks() {
        loadProjects();
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

                badge.setMaxWidth(Double.MAX_VALUE);
                badge.setAlignment(Pos.CENTER);

                setAlignment(Pos.CENTER);

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
            todayTaskTable.setItems(FXCollections.observableArrayList(taskItems));

            if (taskItems.isEmpty()) {
                todayTaskTable.setPlaceholder(new Label("No tasks for today"));
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

            Long projectId = task.getProjectId();

            controller.initData(task, null, projectId);

            controller.setProjectId(projectId);

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

        loadProjects();
        setupProjectTable();
        setupTodayTaskTable();

        DatePicker datePicker = new DatePicker();

        DatePickerSkin skin = new DatePickerSkin(datePicker);
        Node calendar = skin.getPopupContent();

        calendarBox.getChildren().add(calendar);
    }
}