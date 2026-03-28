package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.*;
import com.group4.common.enums.LookupType;
import com.group4.projects_management_fe.core.api.LookupApi;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import com.group4.projects_management_fe.core.api.CommentApi;
import com.group4.projects_management_fe.core.exception.GlobalExceptionHandler;
import com.group4.projects_management_fe.core.session.AppSessionManager;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TaskDetailFormController {

    // ── FXML bindings ─────────────────────────────────────────────────────────
    @FXML private StackPane  rootPane;
    @FXML private TextField  taskNameInput;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<LookupDTO> statusComboBox;
    @FXML private ComboBox<LookupDTO> priorityComboBox;

    @FXML private FlowPane  assigneeChipsPane;
    @FXML private Button    addAssigneeBtn;   // nút "+" từ FXML – sẽ được style lại nhỏ gọn
    @FXML private TextField assigneeInput;    // legacy, giữ để không lỗi FXML

    @FXML private Button    showDetails;
    @FXML private TextArea  descriptionInput;
    @FXML private VBox      commentsContainer;
    @FXML private TextField commentField;
    @FXML private Label     myAvatarLabel;
    @FXML private Label     lastEditedAvatarLabel;
    @FXML private Label     usernameLabel;
    @FXML private Label     editedAtLabel;
    @FXML private Button    saveBtn;

    // ── State ─────────────────────────────────────────────────────────────────
    private Stage      popupStage;
    private ProjectApi projectApi;
    private TaskApi    taskApi;
    private LookupApi  lookupApi;
    private CommentApi commentApi;
    private Runnable   onSaveSuccessCallback;

    private TaskResponseDTO currentTask;
    private LookupDTO       currentMemberLookup;
    private Long            projectId;

    /**
     * true  → mở từ ProjectTasksController → được thêm / xóa assignee
     * false → mở từ TaskGroupController    → chỉ xem
     */
    private boolean assigneeManagementEnabled = false;

    /** Cache toàn bộ member của project, dùng cho popup search */
    private List<ProjectMemberDTO> projectMembersCache = new ArrayList<>();

    /**
     * Danh sách assignee đang làm việc trên UI (có thể khác DB).
     * Mọi thay đổi thêm / xóa chỉ cập nhật list này; API chỉ gọi khi Save.
     */
    private List<TaskAssigneeDTO> workingAssignees = new ArrayList<>();

    /**
     * IDs của assignee lúc mới mở form (dùng để tính diff khi Save).
     */
    private Set<Long> originalAssigneeIds = new HashSet<>();

    // ── Public setters ────────────────────────────────────────────────────────

    public void setAssigneeManagementEnabled(boolean enabled) {
        this.assigneeManagementEnabled = enabled;
    }

    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        AuthSessionProvider provider = AppSessionManager.getInstance();
        this.projectApi = new ProjectApi(provider);
        this.taskApi    = new TaskApi(provider);
        this.lookupApi  = new LookupApi(provider);
        this.commentApi = new CommentApi(provider);
        loadLookups();
    }

    public void setPopupStage(Stage stage)           { this.popupStage = stage; }
    public void setOnSaveSuccessCallback(Runnable r) { this.onSaveSuccessCallback = r; }
    public void setProjectId(Long id)                { this.projectId = id; }

    public void setProjectContext(Long projectId) {
        this.projectId = projectId;

        if (this.projectApi == null) {
            this.projectApi = new ProjectApi(AppSessionManager.getInstance());
        }

        if (projectId != null) {
            // Chủ động tải danh sách member ngay từ đầu (cache lại)
            projectApi.getMembersOfProject(projectId)
                    .thenAccept(members -> {
                        this.projectMembersCache = members != null ? members : new ArrayList<>();
                        System.out.println("[TaskDetail] Đã tải xong " + this.projectMembersCache.size() + " members.");
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> GlobalExceptionHandler.handleException(ex));
                        return null;
                    });
        } else {
            System.err.println("[TaskDetail] CẢNH BÁO: Màn hình cha truyền projectId bị NULL vào setProjectContext!");
        }
    }

    // ── initData ──────────────────────────────────────────────────────────────

    public void initData(TaskResponseDTO task, LookupDTO memberLookup, Long projectId) {
        this.currentTask         = task;
        this.currentMemberLookup = memberLookup;
        this.projectId           = projectId;

        // ── Avatar ────────────────────────────────────────────────────────────
        if (memberLookup != null && memberLookup.getName() != null
                && !memberLookup.getName().isEmpty()) {
            myAvatarLabel.setText(memberLookup.getName().substring(0, 1).toUpperCase());
        }

        // ── Điền các field ────────────────────────────────────────────────────
        taskNameInput.setText(task.getName() != null ? task.getName() : "");
        descriptionInput.setText(task.getDescription() != null ? task.getDescription() : "");
        if (task.getDeadline() != null) {
            dueDatePicker.setValue(task.getDeadline().toLocalDate());
        }

        // ── Khởi tạo working list từ dữ liệu gốc ────────────────────────────
        workingAssignees = task.getAssignees() != null
                ? new ArrayList<>(task.getAssignees())
                : new ArrayList<>();
        originalAssigneeIds = workingAssignees.stream()
                .map(TaskAssigneeDTO::getProjectMemberId)
                .filter(id -> id != null)
                .collect(Collectors.toCollection(HashSet::new));

        // ── Assignee section ──────────────────────────────────────────────────
        addAssigneeBtn.getStyleClass().add("add-assignee-btn");
//        styleAddButton(); // thu nhỏ nút "+" FXML thành hình tròn nhỏ gọn

        if (assigneeManagementEnabled) {
            addAssigneeBtn.setVisible(true);
            addAssigneeBtn.setManaged(true);
            renderAssigneeChips();

            // Load cache member background
            if (projectId != null) {
                projectApi.getMembersOfProject(projectId)
                        .thenAccept(members ->
                                this.projectMembersCache = members != null ? members : new ArrayList<>())
                        .exceptionally(ex -> {
                            Platform.runLater(() -> GlobalExceptionHandler.handleException(ex));
                            return null;
                        });
            }
        } else {
            addAssigneeBtn.setVisible(false);
            addAssigneeBtn.setManaged(false);
            renderAssigneeChips();
        }

        setupStatusComboBox();
        setupPriorityComboBox();
        loadTaskHistory();
        loadTaskComments();
        setupChangeListeners();
    }

    // ── Render Chips (đọc từ workingAssignees) ────────────────────────────────

    private void renderAssigneeChips() {
        assigneeChipsPane.getChildren().clear();

        if (workingAssignees.isEmpty()) {
            Label unassigned = new Label("Unassigned");
            unassigned.setStyle("-fx-text-fill:#999; -fx-font-style:italic; -fx-font-size: 14px");
            assigneeChipsPane.getChildren().add(unassigned);
        } else {
            for (TaskAssigneeDTO assignee : workingAssignees) {
                String name = resolveAssigneeName(assignee);

                HBox chip = new HBox(4);
                chip.setAlignment(Pos.CENTER_LEFT);
                chip.setStyle("-fx-background-color:#E2E8F0;"
                        + "-fx-background-radius:12;"
                        + "-fx-padding:3 8 3 10;");

                Label nameLabel = new Label(name);
                nameLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#333;");
                chip.getChildren().add(nameLabel);

                // Nút "×" chỉ hiện khi có quyền quản lý
                if (assigneeManagementEnabled && assignee.getProjectMemberId() != null) {
                    Button removeBtn = new Button("×");
                    removeBtn.setStyle(
                            "-fx-background-color: transparent;"
                                    + "-fx-text-fill: #888;"
                                    + "-fx-font-size: 12px;"
                                    + "-fx-cursor: hand;"
                                    + "-fx-padding: 0 2 0 2;"
                                    + "-fx-min-width: 16px;"
                                    + "-fx-max-width: 16px;"
                    );
                    Long pmId = assignee.getProjectMemberId();
                    // Chỉ cập nhật UI — KHÔNG gọi API
                    removeBtn.setOnAction(e -> removeAssigneeLocally(pmId));
                    chip.getChildren().add(removeBtn);
                }

                assigneeChipsPane.getChildren().add(chip);
            }
        }

        // Nút "+" luôn ở cuối khi đang ở chế độ quản lý
        if (assigneeManagementEnabled) {
            assigneeChipsPane.getChildren().add(addAssigneeBtn);
        }

        // Bất cứ thay đổi assignee nào cũng enable nút Save
        if (assigneeManagementEnabled) checkChanges();
    }

    private String resolveAssigneeName(TaskAssigneeDTO a) {
        if (a.getFullName() != null && !a.getFullName().isBlank()) return a.getFullName();
        if (a.getUsername()  != null && !a.getUsername().isBlank())  return a.getUsername();
        return "Unknown";
    }

    // ── Thêm assignee vào local list (KHÔNG gọi API) ─────────────────────────

    private void addAssigneeLocally(ProjectMemberDTO member) {
        // Tránh duplicate
        boolean exists = workingAssignees.stream()
                .anyMatch(a -> member.getProjectMemberId().equals(a.getProjectMemberId()));
        if (exists) return;

        TaskAssigneeDTO dto = new TaskAssigneeDTO();
        dto.setProjectMemberId(member.getProjectMemberId());
        dto.setUserId(member.getUserId());
        dto.setFullName(member.getFullName());
        dto.setUsername(member.getUsername());

        workingAssignees.add(dto);
        renderAssigneeChips();
    }

    // ── Xóa assignee khỏi local list (KHÔNG gọi API) ─────────────────────────

    private void removeAssigneeLocally(Long projectMemberId) {
        workingAssignees.removeIf(a -> projectMemberId.equals(a.getProjectMemberId()));
        renderAssigneeChips();
    }

    // ── Add Assignee Popup ────────────────────────────────────────────────────

    @FXML
    private void handleAddAssigneeClick(ActionEvent event) {
        if (!assigneeManagementEnabled) return;

        // Nếu cache chưa có data, thử kích hoạt tải lại
        if (projectMembersCache == null || projectMembersCache.isEmpty()) {
            System.out.println("[TaskDetail] Cache trống, đang thử load lại...");
            if (projectId != null) {
                setProjectContext(projectId);
            } else {
                System.err.println("[TaskDetail] LỖI: projectId vẫn null, không thể lấy danh sách. Hãy check lại Controller cha.");
            }
        }

        // Luôn gọi hàm hiển thị popup
        showMemberSearchPopup();
    }

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

        Runnable filter = () -> {
            resultsBox.getChildren().clear();
            String query = searchField.getText() != null
                    ? searchField.getText().toLowerCase().trim() : "";

            // IDs đang có trong working list → loại khỏi kết quả search
            List <Long> currentIds = workingAssignees.stream()
                    .map(TaskAssigneeDTO::getProjectMemberId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());

            List<ProjectMemberDTO> filtered = projectMembersCache.stream()
                    .filter(m -> {
                        if (currentIds.contains(m.getProjectMemberId())) return false;
                        String fn = m.getFullName()  != null ? m.getFullName().toLowerCase()  : "";
                        String un = m.getUsername() != null ? m.getUsername().toLowerCase() : "";
                        return fn.contains(query) || un.contains(query);
                    })
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                Label lbl = new Label(query.isEmpty()
                        ? "All members already assigned." : "No member found.");
                lbl.setStyle("-fx-padding:4 8; -fx-text-fill:#888;");
                resultsBox.getChildren().add(lbl);
            } else {
                for (ProjectMemberDTO member : filtered) {
                    String display = (member.getFullName() != null
                            && !member.getFullName().isBlank())
                            ? member.getFullName() : member.getUsername();

                    Button btn = new Button(display);
                    btn.getStyleClass().add("member-item-btn");
                    btn.setOnAction(e -> {
                        popup.hide();
                        addAssigneeLocally(member); // cập nhật UI, KHÔNG gọi API
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

    // ── Assignee Input (legacy FXML handler) ──────────────────────────────────
    @FXML
    private void handleAssigneeSubmit(ActionEvent event) {
        if (assigneeInput != null) {
            assigneeInput.clear();
            assigneeInput.setVisible(false);
            assigneeInput.setManaged(false);
        }
    }

    // ── ComboBox helpers ──────────────────────────────────────────────────────

    private void setupStatusComboBox() {
        statusComboBox.setConverter(lookupConverter());
        statusComboBox.setCellFactory(coloredCell("status"));
        statusComboBox.setButtonCell(coloredCell("status").call(null));
    }

    private void setupPriorityComboBox() {
        priorityComboBox.setConverter(lookupConverter());
        priorityComboBox.setCellFactory(coloredCell("priority"));
        priorityComboBox.setButtonCell(coloredCell("priority").call(null));
    }

    private StringConverter<LookupDTO> lookupConverter() {
        return new StringConverter<>() {
            @Override public String toString(LookupDTO dto) {
                return dto == null || dto.getName() == null ? "" : dto.getName();
            }
            @Override public LookupDTO fromString(String s) { return null; }
        };
    }

    private Callback<ListView<LookupDTO>, ListCell<LookupDTO>> coloredCell(String type) {
        return listView -> new ListCell<>() {
            @Override
            protected void updateItem(LookupDTO dto, boolean empty) {
                super.updateItem(dto, empty);
                if (empty || dto == null || dto.getName() == null) {
                    setText(null); setGraphic(null);
                } else {
                    Label badge = new Label(dto.getName());
                    badge.getStyleClass().addAll("lookup-badge",
                            type + "-" + slugify(dto.getName()));
                    HBox container = new HBox(badge);
                    container.setAlignment(Pos.CENTER_LEFT);
                    setText(null); setGraphic(container);
                }
            }
        };
    }

    private String slugify(String name) {
        return name.toLowerCase().replace("_", "-").replace(" ", "-");
    }

    // ── Change Detection ──────────────────────────────────────────────────────

    private void setupChangeListeners() {
        saveBtn.setDisable(true);
        taskNameInput.textProperty()    .addListener((o, v, n) -> checkChanges());
        descriptionInput.textProperty() .addListener((o, v, n) -> checkChanges());
        dueDatePicker.valueProperty()   .addListener((o, v, n) -> checkChanges());
        statusComboBox.valueProperty()  .addListener((o, v, n) -> checkChanges());
        priorityComboBox.valueProperty().addListener((o, v, n) -> checkChanges());
    }

    private void checkChanges() {
        if (currentTask == null) return;
        boolean changed = false;

        // Tên
        String curName = currentTask.getName() != null ? currentTask.getName() : "";
        if (!curName.equals(taskNameInput.getText() != null ? taskNameInput.getText() : ""))
            changed = true;

        // Mô tả
        String curDesc = currentTask.getDescription() != null ? currentTask.getDescription() : "";
        if (!curDesc.equals(descriptionInput.getText() != null ? descriptionInput.getText() : ""))
            changed = true;

        // Ngày
        java.time.LocalDate curDate = currentTask.getDeadline() != null
                ? currentTask.getDeadline().toLocalDate() : null;
        java.time.LocalDate newDate = dueDatePicker.getValue();
        if ((curDate == null && newDate != null) || (curDate != null && !curDate.equals(newDate)))
            changed = true;

        // Status
        LookupDTO ns = statusComboBox.getValue();
        if (ns != null && currentTask.getStatusName() != null
                && !ns.getName().equalsIgnoreCase(currentTask.getStatusName()))
            changed = true;
        else if (ns != null && currentTask.getStatusName() == null)
            changed = true;

        // Priority
        LookupDTO np = priorityComboBox.getValue();
        if (np != null && currentTask.getPriorityName() != null
                && !np.getName().equalsIgnoreCase(currentTask.getPriorityName()))
            changed = true;
        else if (np != null && currentTask.getPriorityName() == null)
            changed = true;

        // Assignee thay đổi?
        Set<Long> currentIds = workingAssignees.stream()
                .map(TaskAssigneeDTO::getProjectMemberId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (!currentIds.equals(originalAssigneeIds)) changed = true;

        saveBtn.setDisable(!changed);
    }

    // ── Load Lookups ──────────────────────────────────────────────────────────

    private void loadLookups() {
        lookupApi.getAll(LookupType.TASK_STATUS).thenAccept(statuses ->
                Platform.runLater(() -> {
                    statusComboBox.getItems().setAll(statuses);
                    if (currentTask != null) {
                        statuses.stream()
                                .filter(s -> s.getName().equalsIgnoreCase(currentTask.getStatusName()))
                                .findFirst()
                                .ifPresent(statusComboBox.getSelectionModel()::select);
                    }
                }));

        lookupApi.getAll(LookupType.PRIORITY).thenAccept(priorities ->
                Platform.runLater(() -> {
                    priorityComboBox.getItems().setAll(priorities);
                    if (currentTask != null) {
                        priorities.stream()
                                .filter(p -> p.getName().equalsIgnoreCase(currentTask.getPriorityName()))
                                .findFirst()
                                .ifPresent(priorityComboBox.getSelectionModel()::select);
                    }
                }));
    }

    // ── History ──────────────────────────────────────────────────────────────

    private void loadTaskHistory() {
        if (currentTask == null || currentTask.getTaskId() == null) return;

        // Giả sử taskApi có hàm getTaskHistories trả về List<TaskHistoryDTO>
        taskApi.getTaskHistory(currentTask.getTaskId()).thenAccept(histories ->
                Platform.runLater(() -> {
                    if (histories != null && !histories.isEmpty()) {
                        // Giả sử API trả về list mới nhất xếp đầu tiên (descending).
                        // Nếu API trả về list tăng dần, hãy dùng histories.get(histories.size() - 1)
                        var latestHistory = histories.get(0);

                        // Lấy tên hiển thị
                        String displayName = latestHistory.getChangedByFullName();
                        if (displayName == null || displayName.isBlank()) {
                            displayName = latestHistory.getChangedByFullName();
                        }
                        if (displayName == null || displayName.isBlank()) {
                            displayName = "Unknown";
                        }

                        // Cập nhật Avatar và Tên
                        usernameLabel.setText(displayName);
                        lastEditedAvatarLabel.setText(displayName.substring(0, 1).toUpperCase());

                        // Cập nhật thời gian (Giả định getCreatedAt() trả về LocalDateTime)
                        if (latestHistory.getChangedAt() != null) {
                            java.time.format.DateTimeFormatter formatter =
                                    java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");
                            editedAtLabel.setText(latestHistory.getChangedAt().format(formatter));
                        } else {
                            editedAtLabel.setText("Unknown time");
                        }
                    } else {
                        // Trạng thái mặc định nếu chưa có lịch sử nào
                        usernameLabel.setText("No edits yet");
                        editedAtLabel.setText("");
                        lastEditedAvatarLabel.setText("-");
                    }
                })
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                System.err.println("[TaskDetail] Lỗi load history: " + ex.getMessage());
            });
            return null;
        });
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    private void loadTaskComments() {
        taskApi.getTaskComments(currentTask.getTaskId()).thenAccept(comments ->
                Platform.runLater(() -> {
                    commentsContainer.getChildren().clear();
                    for (CommentDTO c : comments) addCommentToUI(c);
                })
        ).exceptionally(ex -> {
            System.err.println("[TaskDetail] Lỗi load comments: " + ex.getMessage());
            return null;
        });
    }

    @FXML
    private void handleCommentSubmit(ActionEvent event) {
        String content = commentField.getText();
        if (content == null || content.trim().isEmpty() || currentMemberLookup == null) return;

        Long memberId;
        try {
            memberId = Long.valueOf(currentMemberLookup.getId());
        } catch (NumberFormatException e) {
            System.err.println("[TaskDetail] Member ID không hợp lệ.");
            return;
        }

        CommentCreateRequestDTO request = CommentCreateRequestDTO.builder()
                .taskId(currentTask.getTaskId())
                .projectMemberId(memberId)
                .content(content.trim())
                .parentId(null)
                .build();

        commentField.setDisable(true);
        commentApi.createComment(request).thenAccept(newComment ->
                Platform.runLater(() -> {
                    addCommentToUI(newComment);
                    commentField.clear();
                    commentField.setDisable(false);
                    commentField.requestFocus();
                })
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                commentField.setDisable(false);
                System.err.println("[TaskDetail] Lỗi thêm comment: " + ex.getMessage());
            });
            return null;
        });
    }

    private void addCommentToUI(CommentDTO comment) {
        HBox row = new HBox(10);
        row.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        String displayName = comment.getFullName() != null
                ? comment.getFullName() : comment.getUserName();
        if (displayName == null || displayName.isEmpty()) displayName = "Unknown";

        Label avatar = new Label(displayName.substring(0, 1).toUpperCase());
        avatar.setStyle("-fx-background-color:#5B3E9E; -fx-text-fill:white;"
                + "-fx-min-width:32; -fx-min-height:32; -fx-alignment:center;"
                + "-fx-background-radius:16; -fx-font-weight:bold;");

        VBox contentBox = new VBox(4);
        Label nameLabel = new Label(displayName);
        nameLabel.setStyle("-fx-font-weight:bold; -fx-text-fill:#333333; -fx-font-size:13px;");
        Label commentText = new Label(comment.getContent());
        commentText.setWrapText(true);
        commentText.setStyle("-fx-background-color:white; -fx-padding:8 12;"
                + "-fx-background-radius:0 10 10 10;"
                + "-fx-border-color:#EEE; -fx-border-radius:0 10 10 10;");

        contentBox.getChildren().addAll(nameLabel, commentText);
        row.getChildren().addAll(avatar, contentBox);
        commentsContainer.getChildren().add(row);
    }

    // ── Save (gọi tất cả API tại đây) ────────────────────────────────────────

    @FXML
    private void handleSave(ActionEvent event) {
        if (currentTask == null) { closeForm(); return; }
        saveBtn.setDisable(true);

        // ── Tính diff assignee ────────────────────────────────────────────────
        Set<Long> currentIds = workingAssignees.stream()
                .map(TaskAssigneeDTO::getProjectMemberId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        List<Long> toAdd = currentIds.stream()
                .filter(id -> !originalAssigneeIds.contains(id))
                .collect(Collectors.toList());

        List<Long> toRemove = originalAssigneeIds.stream()
                .filter(id -> !currentIds.contains(id))
                .collect(Collectors.toList());

        // Update thông tin task
        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setName(taskNameInput.getText().trim());
        dto.setDescription(descriptionInput.getText().trim());

        if (dueDatePicker.getValue() != null)
            dto.setDeadline(dueDatePicker.getValue().atStartOfDay());
        if (statusComboBox.getValue() != null)
            dto.setStatusId(Long.valueOf(statusComboBox.getValue().getId()));
        if (priorityComboBox.getValue() != null)
            dto.setPriorityId(Long.valueOf(priorityComboBox.getValue().getId()));

        CompletableFuture<Void> updateFuture = taskApi.updateTask(currentTask.getTaskId(), dto)
                .thenAccept(updated -> {});

        // Gán member mới
        CompletableFuture<Void> assignFuture = toAdd.isEmpty()
                ? CompletableFuture.completedFuture(null)
                : taskApi.assignMember(currentTask.getTaskId(), toAdd)
                .thenAccept(v -> {});

        // Xóa member
        CompletableFuture<Void> removeFuture = toRemove.isEmpty()
                ? CompletableFuture.completedFuture(null)
                : taskApi.removeMemberFromTask(currentTask.getTaskId(), toRemove)
                .thenAccept(v -> {});

        // Đóng form + reload màn hình cha
        CompletableFuture.allOf(updateFuture, assignFuture, removeFuture)
                .thenAccept(v -> Platform.runLater(() -> {
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

    // ── Close ─────────────────────────────────────────────────────────────────

    private void closeForm() {
        if (popupStage != null) popupStage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }
}