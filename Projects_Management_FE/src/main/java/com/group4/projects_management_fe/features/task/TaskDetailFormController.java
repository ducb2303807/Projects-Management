package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.*;
import com.group4.common.enums.LookupType;
import com.group4.projects_management_fe.core.api.LookupApi;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import com.group4.projects_management_fe.core.api.CommentApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TaskDetailFormController {
    // ── FXML bindings ─────────────────────────────────────────────────────────
    @FXML private StackPane  rootPane;
    @FXML private TextField  taskNameInput;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<LookupDTO> statusComboBox;
    @FXML private ComboBox<LookupDTO> priorityComboBox;

    @FXML private FlowPane assigneeChipsPane;   // hiển thị tên assignee hoặc "Unassigned"
    @FXML private Button    addAssigneeBtn;   // nút "+", ẩn với MEMBER
    @FXML private TextField assigneeInput;    // ẩn mặc định (visible=false, managed=false)

    @FXML private TextArea  descriptionInput;
    @FXML private VBox      commentsContainer;
    @FXML private TextField commentField;
    @FXML private Label     myAvatarLabel;
    @FXML private Button    saveBtn;

    // ── State ─────────────────────────────────────────────────────────────────
    private Stage popupStage;
    private ProjectApi projectApi;
    private TaskApi taskApi;
    private LookupApi lookupApi;
    private CommentApi commentApi;
    private Runnable onSaveSuccessCallback;

    private TaskResponseDTO currentTask;
    private LookupDTO currentMemberLookup;
    private Long projectId;

    // True nếu user hiện tại có role PM hoặc CO_PM trong project này
    private boolean canManageAssignees = false;
    private boolean includeCancelled = true;

    // Cache danh sách member của project (lazy load lần đầu nhấn "+")
    private List<ProjectMemberDTO> projectMembersCache = null;

    // ── Public API ────────────────────────────────────────────────────────────

    // 1. Cấu hình các thiết lập ban đầu
    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.projectApi = new ProjectApi(sessionProvider);
        this.taskApi = new TaskApi(sessionProvider);
        this.lookupApi = new LookupApi(sessionProvider);
        this.commentApi = new CommentApi(sessionProvider);

        loadLookups();
    }

    public void setPopupStage(Stage popupStage) { this.popupStage = popupStage; }

    // Callback được gọi SAU khi save thành công VÀ popup đã đóng
    public void setOnSaveSuccessCallback(Runnable callback) { this.onSaveSuccessCallback = callback; }

    // Setter projectId (truyền từ TaskItemController)
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    // 2. Nhận dữ liệu truyền vào và Load giao diện
    public void initData(TaskResponseDTO task, LookupDTO currentMemberLookup, Long projectId) {
        this.currentTask = task;
        this.currentMemberLookup = currentMemberLookup;
        this.projectId = projectId;

        // Avatar của user hiện tại ở ô nhập comment
        if (currentMemberLookup != null && currentMemberLookup.getName() != null) {
            myAvatarLabel.setText(currentMemberLookup.getName().substring(0, 1).toUpperCase());
        }

        // Điền các field
        taskNameInput.setText(task.getName() != null ? task.getName() : "");
        descriptionInput.setText(task.getDescription() != null ? task.getDescription() : "");
        if (task.getDeadline() != null) {
            dueDatePicker.setValue(task.getDeadline().toLocalDate());
        }

        // Hiển thị assignee theo role
        renderAssigneeChips(false);

        // Ẩn nút "+" mặc định, load project members để xác định role
        addAssigneeBtn.setVisible(false);
        addAssigneeBtn.setManaged(false);
//        loadProjectMembersAndDetermineRole();

        // GỌI API LẤY DANH SÁCH MEMBER & KIỂM TRA QUYỀN TRỰC TIẾP
        projectApi.getMembersOfProject(projectId).thenAccept(members -> {
            this.projectMembersCache = members; // Lưu cache danh sách member cho thanh search

            Platform.runLater(() -> {
                // Tìm xem user hiện tại đang là ai trong danh sách project members
                ProjectMemberDTO currentMember = members.stream()
                        .filter(m -> {
                            // Tùy theo currentMemberLookup của bạn đang giữ ID của ProjectMember hay User
                            return String.valueOf(m.getProjectMemberId()).equals(currentMemberLookup.getId()) ||
                                    (m.getUserId() != null && String.valueOf(m.getUserId()).equals(currentMemberLookup.getId()));
                        })
                        .findFirst()
                        .orElse(null);

                if (currentMember != null) {
                    // Lấy tên Role (Lưu ý: Bạn điều chỉnh lại hàm getRoleName() cho đúng với ProjectMemberDTO của bạn)
                    String roleName = currentMember.getRoleName() != null ? currentMember.getRoleName() : "";

                    // Nếu role là Project Manager hoặc Co-Project Manager thì hiện nút "+"
                    if (roleName.equalsIgnoreCase("Project Manager") ||
                            roleName.equalsIgnoreCase("Co-Project Manager") ||
                            roleName.toLowerCase().contains("PM")) {

                        addAssigneeBtn.setVisible(true);
                        addAssigneeBtn.setManaged(true);
                    }
                }
            });
        }).exceptionally(ex -> {
            System.err.println("Lỗi tải danh sách member: " + ex.getMessage());
            return null;
        });

        // Đổ dữ liệu khác
        taskNameInput.setText(task.getName());
        descriptionInput.setText(task.getDescription() != null ? task.getDescription() : "");

        if (task.getDeadline() != null) {
            dueDatePicker.setValue(task.getDeadline().toLocalDate());
        }

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

//    private void loadProjectMembersAndDetermineRole() {
//        if (projectId == null) return;
//
//        projectApi.getMembersOfProject(projectId)
//                .thenAccept(members -> {
//                    this.projectMembersCache = members;
//
//                    // Tìm user hiện tại trong danh sách member
//                    boolean isManager = false;
//                    if (currentMemberLookup != null && currentMemberLookup.getId() != null) {
//                        String currentMemberId = currentMemberLookup.getId();
//                        isManager = members.stream().anyMatch(m -> {
//                            // So sánh theo projectMemberId
//                            String memberId = m.getProjectMemberId() != null ? m.getProjectMemberId().toString() : "";
//                            if (memberId.equals(currentMemberId)) {
//                                // Kiểm tra role: MANAGER hoặc CO-MANAGER
//                                String role = m.getRoleName() != null
//                                        ? m.getRoleName().toUpperCase() : "";
//                                return role.contains("Project Manager") || role.contains("Co-Project Manager");
//                            }
//                            return false;
//                        });
//                    }
//
//                    this.canManageAssignees = isManager;
//                    final boolean canManage = isManager;
//                    Platform.runLater(() -> {
//                        addAssigneeBtn.setVisible(canManage);
//                        addAssigneeBtn.setManaged(canManage);
//                    });
//                })
//                .exceptionally(ex -> {
//                    System.err.println("[TaskDetail] Lỗi load members: " + ex.getMessage());
//                    return null;
//                });
//    }

    private void renderAssigneeChips(boolean withRemoveButton) {
        assigneeChipsPane.getChildren().clear();

        List<TaskAssigneeDTO> assignees = currentTask.getAssignees();
        if (assignees == null || assignees.isEmpty()) {
            Label unassigned = new Label("Unassigned");
            unassigned.setStyle("-fx-text-fill:#999; -fx-font-style:italic;");
            assigneeChipsPane.getChildren().add(unassigned);
            return;
        }

        for (TaskAssigneeDTO assignee : assignees) {
            String name = (assignee.getFullName() != null && !assignee.getFullName().isBlank())
                    ? assignee.getFullName()
                    : (assignee.getUsername() != null ? assignee.getUsername() : "Unknown");

            HBox chip = new HBox(4);
            chip.setAlignment(Pos.CENTER_LEFT);
            chip.setStyle("-fx-background-color:#E2E8F0;"
                    + "-fx-background-radius:12;"
                    + "-fx-padding:3 10 3 10;");

            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#333;");
            chip.getChildren().add(nameLabel);

            if (withRemoveButton) {
                // Nút "×" chỉ hiện với PM/CO_PM
                Button removeBtn = new Button("×");
                removeBtn.setStyle("-fx-background-color:transparent;"
                        + "-fx-text-fill:#888;"
                        + "-fx-font-size:12px;"
                        + "-fx-cursor:hand;"
                        + "-fx-padding:0 0 0 4;");
                removeBtn.setOnAction(e -> {
                    // projectMemberId từ TaskAssigneeDTO
                    if (assignee.getProjectMemberId() != null) {
                        removeAssigneeFromTask(assignee.getProjectMemberId(), name);
                    }
                });
                chip.getChildren().add(removeBtn);
            }

            assigneeChipsPane.getChildren().add(chip);
        }
    }

    private void removeAssigneeFromTask(Long projectMemberId, String displayName) {
        // Tắt tất cả nút × trong lúc đang gọi API để chống double-click
        assigneeChipsPane.getChildren().forEach(node -> node.setDisable(true));

        taskApi.removeMemberFromTask(currentTask.getTaskId(), Collections.singletonList(projectMemberId))
                .thenCompose(v -> taskApi.getMyTasks(includeCancelled))
                .thenAccept(tasks -> {
                    tasks.stream()
                            .filter(t -> t.getTaskId().equals(currentTask.getTaskId()))
                            .findFirst()
                            .ifPresent(updated -> {
                                this.currentTask = updated;
                                Platform.runLater(() -> {
                                    renderAssigneeChips(canManageAssignees);
                                    if (onSaveSuccessCallback != null)
                                        onSaveSuccessCallback.run();
                                });
                            });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        assigneeChipsPane.getChildren().forEach(n -> n.setDisable(false));
                        System.err.println("[TaskDetail] Lỗi xóa assignee: " + ex.getMessage());
                    });
                    return null;
                });
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

    // Bấm "+" → load danh sách member của project
    @FXML
    private void handleAddAssigneeClick(ActionEvent event) {
        // Vì danh sách member đã được tải và cache sẵn ở initData() nên chỉ việc show luôn
        if (this.projectMembersCache != null && !this.projectMembersCache.isEmpty()) {
            showMemberSearchPopup();
        } else {
            System.err.println("Chưa tải được danh sách thành viên dự án!");
        }
    }

//    ContextMenu search: CHỈ lọc trong projectMembersCache
    private void showMemberSearchPopup() {
        ContextMenu popup = new ContextMenu();

        TextField searchField = new TextField();
        searchField.setPromptText("Search members...");
        searchField.setPrefWidth(230);

        CustomMenuItem searchItem = new CustomMenuItem(searchField);
        searchItem.setHideOnClick(false);
        popup.getItems().add(searchItem);

        VBox resultsBox = new VBox(2);
        CustomMenuItem resultsItem = new CustomMenuItem(resultsBox);
        resultsItem.setHideOnClick(false);
        popup.getItems().add(resultsItem);

        // Lấy danh sách projectMemberId đang assign để loại khỏi kết quả search
        List<Long> alreadyAssignedIds = currentTask.getAssignees() != null
                ? currentTask.getAssignees().stream()
                .map(TaskAssigneeDTO::getProjectMemberId)
                .filter(id -> id != null)
                .collect(Collectors.toList())
                : new ArrayList<>();

        Runnable filter = () -> {
            resultsBox.getChildren().clear();
            String query = searchField.getText() != null
                    ? searchField.getText().toLowerCase().trim() : "";

            List<ProjectMemberDTO> filtered = projectMembersCache.stream()
                    .filter(m -> {
                        // Loại member đã được assign
                        if (alreadyAssignedIds.contains(m.getProjectMemberId())) return false;
                        String fn = m.getFullName() != null ? m.getFullName().toLowerCase() : "";
                        String un = m.getUsername() != null ? m.getUsername().toLowerCase() : "";
                        return fn.contains(query) || un.contains(query);
                    })
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                Label noResult = new Label(query.isEmpty()
                        ? "All members already assigned." : "No member found.");
                noResult.setStyle("-fx-padding:4 8; -fx-text-fill:#888;");
                resultsBox.getChildren().add(noResult);
            } else {
                for (ProjectMemberDTO member : filtered) {
                    String display = (member.getFullName() != null
                            && !member.getFullName().isBlank())
                            ? member.getFullName() : member.getUsername();
                    Button btn = new Button(display);
                    btn.setStyle("-fx-background-color:transparent;"
                            + "-fx-alignment:CENTER_LEFT;"
                            + "-fx-pref-width:230;"
                            + "-fx-padding:6 10;");
                    btn.setOnAction(e -> {
                        popup.hide();
                        assignMemberToTask(member.getProjectMemberId());
                    });
                    resultsBox.getChildren().add(btn);
                }
            }
        };

        searchField.textProperty().addListener((obs, old, val) -> filter.run());
        filter.run();

        popup.show(addAssigneeBtn, Side.BOTTOM, 0, 4);
        Platform.runLater(searchField::requestFocus);
    }

    /**
     * Gọi POST /tasks/{taskId}/members sau khi chọn member.
     * TaskApi.assignMember() gửi List.of(projectMemberId) làm body.
     * Sau khi gán: reload task từ server để cập nhật assignee label.
     */
    private void assignMemberToTask(Long projectMemberId) {
        addAssigneeBtn.setDisable(true);

        taskApi.assignMember(currentTask.getProjectId(), Collections.singletonList(projectMemberId)).thenAccept(v -> {
            Platform.runLater(() -> {
                System.out.println("Gán member thành công!");
                if (onSaveSuccessCallback != null) onSaveSuccessCallback.run(); // Load lại list view ngoài
                closeForm(); // Tắt popup để người dùng thấy giao diện ngoài cập nhật
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                addAssigneeBtn.setDisable(false);
                System.err.println("Lỗi gán member: " + ex.getMessage());
            });
            return null;
        });
    }

    /**
     * Enter trong TextField assignee: cập nhật label và ẩn input.
     * FXML dòng 65: onAction="#handleAssigneeSubmit"
     */
    @FXML
    private void handleAssigneeSubmit(ActionEvent event) {
        assigneeInput.clear();
        assigneeInput.setVisible(false);
        assigneeInput.setManaged(false);
    }

    private void selectCurrentLookupValues() {
        if (currentTask == null) return;

        if (!statusComboBox.getItems().isEmpty() && currentTask.getStatusName() != null) {
            statusComboBox.getItems().stream()
                    .filter(s -> s.getName().equalsIgnoreCase(currentTask.getStatusName()))
                    .findFirst()
                    .ifPresent(statusComboBox.getSelectionModel()::select);
        }

        if (!priorityComboBox.getItems().isEmpty() && currentTask.getPriorityName() != null) {
            priorityComboBox.getItems().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(currentTask.getPriorityName()))
                    .findFirst()
                    .ifPresent(priorityComboBox.getSelectionModel()::select);
        }
    }

    // 4. Gọi API Cập Nhật Task (PUT /api/tasks/{taskId})
    @FXML
    private void handleSave(ActionEvent event) {
        if (currentTask == null) { closeForm(); return; }

        saveBtn.setDisable(true);

        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setName(taskNameInput.getText().trim());
        dto.setDescription(descriptionInput.getText().trim());

        if (dueDatePicker.getValue() != null) {
            dto.setDeadline(dueDatePicker.getValue().atStartOfDay());
        }
        if (statusComboBox.getValue() != null) {
            dto.setStatusId(Long.valueOf(statusComboBox.getValue().getId()));
        }
        if (priorityComboBox.getValue() != null) {
            dto.setPriorityId(Long.valueOf(priorityComboBox.getValue().getId()));
        }

        taskApi.updateTask(currentTask.getTaskId(), dto)
                .thenAccept(updated -> Platform.runLater(() -> {
                    closeForm();
                    if (onSaveSuccessCallback != null) onSaveSuccessCallback.run();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveBtn.setDisable(false);
                        System.err.println("[TaskDetail] Lỗi save: " + ex.getMessage());
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

        commentApi.createComment(request).thenAccept(newComment -> {
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