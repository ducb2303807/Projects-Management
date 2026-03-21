package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.*;
import com.group4.common.enums.LookupType;
import com.group4.projects_management_fe.core.api.LookupApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDateTime;

public class TaskDetailFormController {

    @FXML private StackPane rootPane;
    @FXML private TextField taskNameInput;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<LookupDTO> statusComboBox;
    @FXML private ComboBox<LookupDTO> priorityComboBox;
    @FXML private TextField assigneeInput;
    @FXML private Label      assigneeLabel;   // fx:id="assigneeLabel"
    @FXML private Button     addAssigneeBtn;
    @FXML private TextArea descriptionInput;
    @FXML private VBox commentsContainer;
    @FXML private TextField commentField;
    @FXML private Label myAvatarLabel;
    @FXML private Button saveBtn;

    private Stage popupStage;
    private TaskApi taskApi;
    private LookupApi lookupApi;
    private AuthSessionProvider sessionProvider;
    private Runnable onSaveSuccessCallback;

    private TaskResponseDTO currentTask;
    private LookupDTO currentMemberLookup;

    // 1. Cấu hình các thiết lập ban đầu
    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
        this.taskApi = new TaskApi(sessionProvider);
        this.lookupApi = new LookupApi(sessionProvider);

        loadLookups();
    }

    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }

//     Callback được gọi SAU khi save thành công VÀ popup đã đóng.
//     TasksViewController.reloadData() được truyền vào đây.
    public void setOnSaveSuccessCallback(Runnable callback) {
        this.onSaveSuccessCallback = callback;
    }

    // 2. Nhận dữ liệu truyền vào và Load giao diện
    public void initData(TaskResponseDTO task, LookupDTO currentMemberLookup) {
        this.currentTask = task;
        this.currentMemberLookup = currentMemberLookup;

        setupStatusComboBox();
        setupPriorityComboBox();
        setupComboBoxStyling();

        // Set Avatar người dùng hiện tại ở ô nhập comment
        if (currentMemberLookup != null && currentMemberLookup.getName() != null) {
            myAvatarLabel.setText(currentMemberLookup.getName().substring(0, 1).toUpperCase());
        }

        // Đổ dữ liệu task
        taskNameInput.setText(task.getName());
        descriptionInput.setText(task.getDescription() != null ? task.getDescription() : "");

        if (task.getDeadline() != null) {
            dueDatePicker.setValue(task.getDeadline().toLocalDate());
        }

        // Hiển thị tên Assignee (nếu có)
        if (task.getAssignees() != null && !task.getAssignees().isEmpty()) {
            StringBuilder assigneesStr = new StringBuilder();
            for (TaskAssigneeDTO assignee : task.getAssignees()) {
                assigneesStr.append(assignee.getFullName() != null ? assignee.getFullName() : assignee.getUsername()).append(", ");
            }
            assigneeInput.setText(assigneesStr.substring(0, assigneesStr.length() - 2));
        }

        // Load comments từ server
        loadTaskComments();

        // Lắng nghe thay đổi của task
        setupChangeListeners();
    }

    // -----------------------------------------------------------------------
    // Setup ComboBox: StringConverter + CellFactory tô màu
    // -----------------------------------------------------------------------

    private void setupStatusComboBox() {
        statusComboBox.setConverter(lookupConverter());
        statusComboBox.setCellFactory(coloredCell("status"));
        // ButtonCell: hiển thị item đang chọn trên nút ComboBox cũng có màu
        statusComboBox.setButtonCell(coloredCell("status").call(null));
    }

    private void setupPriorityComboBox() {
        priorityComboBox.setConverter(lookupConverter());
        priorityComboBox.setCellFactory(coloredCell("priority"));
        priorityComboBox.setButtonCell(coloredCell("priority").call(null));
    }

    /**
     * StringConverter dùng chung: hiển thị dto.getName()
     */
    private StringConverter<LookupDTO> lookupConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(LookupDTO dto) {
                // Trả về Name để hiển thị trên Dropdown
                return dto == null || dto.getName() == null ? "" : dto.getName();
            }
            @Override
            public LookupDTO fromString(String s) { return null; }
        };
    }

    /**
     * CellFactory tô màu dựa trên tên item và loại combobox ("status" / "priority").
     * CSS class được thêm vào Label bên trong cell → định nghĩa màu trong lookup-colors.css
     */
    private Callback<ListView<LookupDTO>, ListCell<LookupDTO>> coloredCell(String type) {
        return listView -> new ListCell<>() {
            @Override
            protected void updateItem(LookupDTO dto, boolean empty) {
                super.updateItem(dto, empty);

                // Xóa hết style cũ trước khi set mới (tránh style bị giữ lại khi cell tái sử dụng)
                getStyleClass().removeIf(s -> s.startsWith("lookup-") || s.startsWith("status-") || s.startsWith("priority-"));

                if (empty || dto == null || dto.getName() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(dto.getName());
                    // Thêm CSS class dạng: status-new, status-on-going, priority-high, ...
                    String cssClass = type + "-" + slugify(dto.getName());
                    getStyleClass().add("lookup-badge");
                    getStyleClass().add(cssClass);
                }
            }
        };
    }

    private String slugify(String name) {
        return name.toLowerCase().replace("_", "-").replace(" ", "-");
    }

    private void setupComboBoxStyling() {
        // Tạo Factory để tùy chỉnh từng dòng (Cell)
        Callback<ListView<LookupDTO>, ListCell<LookupDTO>> cellFactory = listView -> new ListCell<>() {
            @Override
            protected void updateItem(LookupDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Xóa style nếu dòng trống
                } else {
                    setText(item.getName()); // Hiển thị tên
                    setStyle(getColorStyleForLookup(item)); // Áp dụng màu sắc đã định nghĩa ở Bước 1
                }
            }
        };

        // 1. Áp dụng cho danh sách xổ xuống (Dropdown List)
        statusComboBox.setCellFactory(cellFactory);
        priorityComboBox.setCellFactory(cellFactory);

        // 2. Áp dụng cho ô hiển thị sau khi đã chọn xong (Button Cell)
        statusComboBox.setButtonCell(cellFactory.call(null));
        priorityComboBox.setButtonCell(cellFactory.call(null));
    }

    private void setupChangeListeners() {
        // Mặc định disable nút Save ban đầu
        saveBtn.setDisable(true);

        // Bắt sự kiện mỗi khi người dùng gõ hoặc chọn giá trị mới
        taskNameInput.textProperty().addListener((observable, oldValue, newValue) -> checkChanges());
        descriptionInput.textProperty().addListener((observable, oldValue, newValue) -> checkChanges());
        dueDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkChanges());
        statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkChanges());
        priorityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkChanges());
    }

    private void checkChanges() {
        if (currentTask == null) return;

        boolean isChanged = false;

        // So sánh Tên task
        String currentName = currentTask.getName() != null ? currentTask.getName() : "";
        String newName = taskNameInput.getText() != null ? taskNameInput.getText() : "";
        if (!currentName.equals(newName)) isChanged = true;

        // So sánh Description
        String currentDesc = currentTask.getDescription() != null ? currentTask.getDescription() : "";
        String newDesc = descriptionInput.getText() != null ? descriptionInput.getText() : "";
        if (!currentDesc.equals(newDesc)) isChanged = true;

        // So sánh Due Date
        java.time.LocalDate currentDate = currentTask.getDeadline() != null ? currentTask.getDeadline().toLocalDate() : null;
        java.time.LocalDate newDate = dueDatePicker.getValue();
        if ((currentDate == null && newDate != null) || (currentDate != null && !currentDate.equals(newDate))) {
            isChanged = true;
        }

        // So sánh Status
        String currentStatus = currentTask.getStatusName();
        LookupDTO newStatus = statusComboBox.getValue();
        if (newStatus != null && currentStatus != null && !newStatus.getName().equalsIgnoreCase(currentStatus)) {
            isChanged = true;
        } else if (newStatus != null && currentStatus == null) {
            isChanged = true;
        }

        // So sánh Priority
        String currentPriority = currentTask.getPriorityName();
        LookupDTO newPriority = priorityComboBox.getValue();
        if (newPriority != null && currentPriority != null && !newPriority.getName().equalsIgnoreCase(currentPriority)) {
            isChanged = true;
        } else if (newPriority != null && currentPriority == null) {
            isChanged = true;
        }

        // Nếu có thay đổi -> Enable (Bật) nút Save. Nếu không -> Disable (Tắt)
        saveBtn.setDisable(!isChanged);
    }

    /**
     * Chuyển tên thành CSS class slug: "ON_GOING" → "on-going", "HIGH" → "high"
     */
    private String getColorStyleForLookup(LookupDTO item) {
        if (item == null || item.getName() == null) return "";

        return switch (item.getName().toUpperCase()) {
            // Task Status
            case "CẦN LÀM", "TO DO" -> "-fx-text-fill: #757575; -fx-font-weight: bold;"; // Xám
            case "ĐANG LÀM", "IN PROGRESS" -> "-fx-text-fill: #FCAB10; -fx-font-weight: bold;"; // Vàng
            case "ĐANG KIỂM TRA", "UNDER REVIEW" -> "-fx-text-fill: #7B68EE; -fx-font-weight: bold;"; // Tím
            case "HOÀN THÀNH", "DONE" -> "-fx-text-fill: #2E7D32; -fx-font-weight: bold;"; // Xanh lá
            case "ĐÃ HỦY", "CANCELLED" -> "-fx-text-fill: #C62828; -fx-font-weight: bold;"; // Đỏ

            // Priority
            case "KHẨN CẤP", "URGENT" -> "-fx-text-fill: #C62828; -fx-font-weight: bold;"; // Đỏ
            case "CAO", "HIGH" -> "-fx-text-fill: #EF6C00; -fx-font-weight: bold;"; // Cam
            case "TRUNG BÌNH", "MEDIUM" -> "-fx-text-fill: #FCAB10; -fx-font-weight: bold;"; // Vàng
            case "THẤP", "LOW" -> "-fx-text-fill: #2E7D32; -fx-font-weight: bold;"; // Xanh lá

            default -> "-fx-text-fill: #333333;"; // Màu mặc định
        };
    }

    // 3. Gọi API Load Lookups (Trạng thái và Mức độ ưu tiên)
    private void loadLookups() {
        lookupApi.getAll(LookupType.TASK_STATUS).thenAccept(statuses -> {
            Platform.runLater(() -> {
                statusComboBox.getItems().setAll(statuses);
                if (currentTask != null) {
                    statuses.stream()
                            .filter(s -> s.getName().equalsIgnoreCase(currentTask.getStatusName()))
                            .findFirst()
                            .ifPresent(statusComboBox.getSelectionModel()::select);
                }
            });
        });

        lookupApi.getAll(LookupType.PRIORITY).thenAccept(priorities -> {
            Platform.runLater(() -> {
                priorityComboBox.getItems().setAll(priorities);
                if (currentTask != null) {
                    priorities.stream()
                            .filter(p -> p.getName().equalsIgnoreCase(currentTask.getPriorityName()))
                            .findFirst()
                            .ifPresent(priorityComboBox.getSelectionModel()::select);
                }
            });
        });
    }

    @FXML
    private void handleAddAssigneeClick(ActionEvent event) {
        assigneeInput.setVisible(true);
        assigneeInput.setManaged(true);
        assigneeInput.requestFocus();
    }

    /**
     * Enter trong TextField assignee: cập nhật label và ẩn input.
     * FXML dòng 65: onAction="#handleAssigneeSubmit"
     */
    @FXML
    private void handleAssigneeSubmit(ActionEvent event) {
        String username = assigneeInput.getText().trim();
        if (!username.isEmpty()) {
            // Hiện tên vừa nhập lên label
            String current = assigneeLabel.getText();
            if ("Unassigned".equals(current) || current.isBlank()) {
                assigneeLabel.setText(username);
            } else {
                assigneeLabel.setText(current + ", " + username);
            }
            // TODO: gọi TaskApi.assignMembers() với projectMemberId tương ứng
        }
        assigneeInput.clear();
        assigneeInput.setVisible(false);
        assigneeInput.setManaged(false);
    }

    // 4. Gọi API Cập Nhật Task (PUT /api/tasks/{taskId})
    @FXML
    private void handleSave(ActionEvent event) {
        if (currentTask == null) {
            closeForm();
            return;
        }

        saveBtn.setDisable(true);

        TaskUpdateDTO updateRequest = new TaskUpdateDTO();
        updateRequest.setName(taskNameInput.getText());
        updateRequest.setDescription(descriptionInput.getText());

        if (dueDatePicker.getValue() != null) {
            updateRequest.setDeadline(dueDatePicker.getValue().atStartOfDay());
        }
        if (statusComboBox.getValue() != null) {
            updateRequest.setStatusId(Long.valueOf(statusComboBox.getValue().getId()));
        }
        if (priorityComboBox.getValue() != null) {
            updateRequest.setPriorityId(Long.valueOf(priorityComboBox.getValue().getId()));
        }

        taskApi.updateTask(currentTask.getTaskId(), updateRequest)
                .thenAccept(updated -> {
                    Platform.runLater(() -> {
                        // ĐÓNG POPUP TRƯỚC, sau đó mới reload
                        closeForm();
                        if (onSaveSuccessCallback != null) {
                            onSaveSuccessCallback.run();
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveBtn.setDisable(false);
                        System.err.println("Lỗi khi lưu task: " + ex.getMessage());
                    });
                    return null;
                });
    }

    // 5. Quản lý Comments (Load & Thêm mới)
    private void loadTaskComments() {
        taskApi.getTaskComments(currentTask.getTaskId()).thenAccept(comments -> {
            Platform.runLater(() -> {
                commentsContainer.getChildren().clear();
                for (CommentDTO comment : comments) {
                    addCommentToUI(comment);
                }
            });
        }).exceptionally(ex -> {
            System.err.println("Lỗi load comments: " + ex.getMessage());
            return null;
        });
    }

    @FXML
    private void handleCommentSubmit(ActionEvent event) {
        String content = commentField.getText();

        if (content == null || content.trim().isEmpty() || currentMemberLookup == null) {
            return;
        }

        Long memberId;
        try {
            memberId = Long.valueOf(currentMemberLookup.getId());
        } catch (NumberFormatException e) {
            System.err.println("Lỗi: Project Member ID không hợp lệ.");
            return;
        }

        // Dùng SuperBuilder khởi tạo DTO gửi xuống backend
        CommentCreateRequestDTO request = CommentCreateRequestDTO.builder()
                .taskId(currentTask.getTaskId())
                .projectMemberId(memberId)
                .content(content.trim())
                .parentId(null)
                .build();

        commentField.setDisable(true);

        taskApi.createComment(request).thenAccept(newComment -> {
            Platform.runLater(() -> {
                addCommentToUI(newComment);
                commentField.clear();
                commentField.setDisable(false);
                commentField.requestFocus();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                commentField.setDisable(false);
                System.err.println("Lỗi khi thêm comment: " + ex.getMessage());
            });
            return null;
        });
    }

    // Giao diện cho 1 dòng Comment
    private void addCommentToUI(CommentDTO comment) {
        HBox row = new HBox(10);
        row.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        String displayName = comment.getFullName() != null ? comment.getFullName() : comment.getUserName();
        if (displayName == null || displayName.isEmpty()) displayName = "Unknown";

        String avatarChar = displayName.substring(0, 1).toUpperCase();
        Label avatar = new Label(avatarChar);
        avatar.setStyle("-fx-background-color:#5B3E9E; -fx-text-fill:white; -fx-min-width:32; -fx-min-height:32; -fx-alignment:center; -fx-background-radius:16; -fx-font-weight:bold;");

        VBox contentBox = new VBox(4);

        // Dòng header comment (Tên người gửi)
        Label nameLabel = new Label(displayName);
        nameLabel.setStyle("-fx-font-weight:bold; -fx-text-fill: #333333; -fx-font-size: 13px;");

        // Nội dung comment có bo góc
        Label commentText = new Label(comment.getContent());
        commentText.setWrapText(true);
        commentText.setStyle("-fx-background-color: white; -fx-padding: 8 12; -fx-background-radius: 0 10 10 10; -fx-border-color: #EEE; -fx-border-radius: 0 10 10 10;");

        contentBox.getChildren().addAll(nameLabel, commentText);
        row.getChildren().addAll(avatar, contentBox);

        commentsContainer.getChildren().add(row);
    }

    private void closeForm() {
        if (popupStage != null) {
            popupStage.close();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }
}