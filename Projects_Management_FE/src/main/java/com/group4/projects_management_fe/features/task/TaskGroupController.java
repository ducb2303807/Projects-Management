package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.LookupDTO;
import com.group4.common.dto.TaskAssigneeDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TaskGroupController implements Initializable {

    @FXML private TitledPane groupTitledPane;
    @FXML private TableView<TaskResponseDTO> taskGroupContent;   // TableView chính
    @FXML private Button moreButton;

    private TaskResponseDTO currentTask;
    private AuthSessionProvider sessionProvider;
    private Runnable reloadCallback;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
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

    // ===================================================================
    // SETUP TABLE (giống DashboardController)
    // ===================================================================
    private void setupTableColumns() {
        // Task Name
        TableColumn<TaskResponseDTO, String> colName = new TableColumn<>("Task name");
        colName.getStyleClass().add("task-name-col");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colName.setPrefWidth(520);

        // Due Date
        TableColumn<TaskResponseDTO, String> colDue = new TableColumn<>("Due date");
        colDue.setCellValueFactory(data -> {
            if (data.getValue().getDeadline() != null) {
                return new SimpleStringProperty(data.getValue().getDeadline().format(DATE_FORMAT));
            }
            return new SimpleStringProperty("--/--/----");
        });
        colDue.setPrefWidth(150);

        // Assignee
        TableColumn<TaskResponseDTO, List<TaskAssigneeDTO>> colAssignee = new TableColumn<>("Assignee");
        colAssignee.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAssignees()));
        colAssignee.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(List<TaskAssigneeDTO> assignees, boolean empty) {
                super.updateItem(assignees, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(6);
                container.setAlignment(javafx.geometry.Pos.CENTER);

                if (assignees == null || assignees.isEmpty()) {
                    Label unassigned = new Label("Unassigned");
                    unassigned.setStyle("-fx-text-fill: #A0AEC0;");
                    container.getChildren().add(unassigned);
                } else {
                    // Tên người đầu tiên
                    String firstName = assignees.get(0).getFullName() != null
                            ? assignees.get(0).getFullName()
                            : assignees.get(0).getUsername();
                    Label nameLabel = new Label(firstName);
                    nameLabel.setStyle("-fx-text-fill: #333333;");
                    container.getChildren().add(nameLabel);

                    // Badge +N nếu có nhiều hơn 1 người
                    if (assignees.size() > 1) {
                        int extra = assignees.size() - 1;
                        Label badge = new Label("+" + extra);
                        badge.getStyleClass().add("assignee-extra-badge");
                        container.getChildren().add(badge);
                    }
                }

                setGraphic(container);
                setText(null);
            }
        });
        colAssignee.setPrefWidth(180);

        // Priority (có badge màu)
        TableColumn<TaskResponseDTO, String> colPriority = new TableColumn<>("Priority");
        colPriority.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPriorityName()));
        colPriority.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(priority);
                badge.getStyleClass().add("priority-badge");

                String p = priority.toLowerCase();
                if (p.contains("urgent")) badge.getStyleClass().add("priority-urgent");
                else if (p.contains("high")) badge.getStyleClass().add("priority-high");
                else if (p.contains("medium")) badge.getStyleClass().add("priority-medium");
                else badge.getStyleClass().add("priority-low");

                setGraphic(badge);
                setText(null);
            }
        });
        colPriority.setPrefWidth(150);

        // More button
        TableColumn<TaskResponseDTO, Void> colMore = new TableColumn<>("");
        colMore.setPrefWidth(50);
        colMore.setCellFactory(col -> new TableCell<>() {
            private final Button moreButton = new Button("•••");

            {
                moreButton.getStyleClass().add("more-button");
                moreButton.setOnAction(e -> {
                    TaskResponseDTO task = getTableView().getItems().get(getIndex());
                    openTaskDetail(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : moreButton);
            }
        });

        // Thêm cột vào TableView
        taskGroupContent.getColumns().setAll(colName, colDue, colAssignee, colPriority, colMore);
        taskGroupContent.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ===================================================================
    // LOAD TASKS
    // ===================================================================
    public void loadTasks(List<TaskResponseDTO> tasks) {
        taskGroupContent.getItems().clear();
        if (tasks != null) {
            taskGroupContent.getItems().addAll(tasks);
        }
    }

    private void openTaskDetail(TaskResponseDTO taskToOpen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/task/TaskDetailForm.fxml"));
            Parent root = loader.load();

            TaskDetailFormController controller = loader.getController();
            controller.setSessionProvider(this.sessionProvider);

            // Gán dữ liệu tạm để đẩy vào Form
            LookupDTO memberLookup = new LookupDTO();
            memberLookup.setId(taskToOpen.getTaskId() != null ? taskToOpen.getTaskId().toString() : null);
            memberLookup.setName(taskToOpen.getStatusName());

            Long projectId = taskToOpen.getProjectId();

            // Khởi tạo dữ liệu form
            controller.initData(taskToOpen, memberLookup, projectId);
            controller.setOnSaveSuccessCallback(this.reloadCallback);
            controller.setAssigneeManagementEnabled(false);

            // Mở cửa sổ dạng Popup mờ nền
            Window ownerWindow = taskGroupContent.getScene().getWindow();
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(ownerWindow);
            popup.initStyle(StageStyle.TRANSPARENT);

            controller.setPopupStage(popup);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popup.setScene(scene);

            popup.setWidth(ownerWindow.getWidth());
            popup.setHeight(ownerWindow.getHeight());
            popup.setX(ownerWindow.getX());
            popup.setY(ownerWindow.getY());

            popup.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}