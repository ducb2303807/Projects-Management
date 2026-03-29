package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.LookupDTO;
import com.group4.common.dto.ProjectMemberDTO;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.exception.GlobalExceptionHandler;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;


public class NewTaskFormController {

    @FXML
    private StackPane rootPane;
    @FXML
    private TextField taskNameInput;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private ComboBox<LookupDTO> statusComboBox;
    @FXML
    private ComboBox<LookupDTO> priorityComboBox;

    @FXML
    private FlowPane assigneeChipsPane;

    @FXML
    private Button addAssigneeBtn;

    @FXML
    private TextArea descriptionInput;
    @FXML
    private Button saveBtn;


    @Getter
    private NewTaskViewModel viewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Stage popupStage;

    private Long projectId;
    private Long currentMemberId;
    private ProjectApi projectApi;
    private Runnable onSaveSuccessCallback;

    // Gọi từ TasksViewController sau khi tạo Stage, trước showAndWait()
    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    public void setOnSaveSuccessCallback(Runnable callback) {
        this.onSaveSuccessCallback = callback;
    }

    public void setProjectContext(Long projectId) {
        this.projectId = projectId;

        var userId = AppSessionManager.getInstance().getCurrentUser().getId();
        projectApi = new ProjectApi(AppSessionManager.getInstance());

        projectApi.getMembersOfProject(projectId)
                .thenAccept(list -> {
                    var projectMember = list.stream()
                            .filter(projectMemberDTO -> projectMemberDTO.getUserId() == userId)
                            .findFirst()
                            .orElse(null);


                    this.currentMemberId = projectMember.getProjectMemberId();
                })
                .exceptionally(ex -> {
                    GlobalExceptionHandler.handleException(ex);
                    return null;
                });
        if (viewModel != null) {
            viewModel.setProjectId(projectId);
            viewModel.loadProjectMembers(currentMemberId);
        }
    }


    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.viewModel = new NewTaskViewModel(sessionProvider);
        if (projectId != null) {
            viewModel.setProjectId(projectId);
            viewModel.loadProjectMembers(currentMemberId);
        }
        setupBindings();
    }

    @FXML
    public void initialize() {
        setupStatusComboBox();
        setupPriorityComboBox();

        addAssigneeBtn.setVisible(true);
        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> rootPane.requestFocus());
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
     * Tạo Label badge bên trong HBox để CSS class không bị override bởi hover/selection state của ListCell.
     */
    private Callback<ListView<LookupDTO>, ListCell<LookupDTO>> coloredCell(String type) {
        return listView -> new ListCell<>() {
            @Override
            protected void updateItem(LookupDTO dto, boolean empty) {
                super.updateItem(dto, empty);

                if (empty || dto == null || dto.getName() == null) {
                    setText(null);
                    setGraphic(null);
//                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Tạo Label badge và gắn CSS class: lookup-badge + status-new / priority-high ...
                    Label badge = new Label(dto.getName());
                    String cssClass = type + "-" + slugify(dto.getName());
                    badge.getStyleClass().add("lookup-badge");
                    badge.getStyleClass().add(cssClass);

                    HBox container = new HBox(badge);
                    container.setAlignment(Pos.CENTER_LEFT);
//                    container.setStyle("-fx-background-color: transparent;");

                    setText(null);
                    setGraphic(container);
                }
            }
        };
    }

    private String slugify(String name) {
        return name.toLowerCase().replace("_", "-").replace(" ", "-");
    }


    // -----------------------------------------------------------------------
    // Session & Bindings
    // -----------------------------------------------------------------------

    private void setupBindings() {
        // Input → ViewModel
        taskNameInput.textProperty().addListener(
                (obs, old, val) -> viewModel.setTaskName(val));
        dueDatePicker.valueProperty().addListener(
                (obs, old, val) -> viewModel.setDueDate(val));
        statusComboBox.valueProperty().addListener(
                (obs, old, val) -> viewModel.setStatus(val));
        priorityComboBox.valueProperty().addListener(
                (obs, old, val) -> viewModel.setPriority(val));
        descriptionInput.textProperty().addListener(
                (obs, old, val) -> viewModel.setDescription(val));

        addAssigneeBtn.setDisable(true);
        // ViewModel → View: populate ComboBox status
        disposables.add(
                viewModel.taskStatusesObservable()
                        .subscribe(list -> Platform.runLater(() ->
                                statusComboBox.getItems().setAll(list)))
        );

        // ViewModel → View: populate ComboBox priority
        disposables.add(
                viewModel.prioritiesObservable()
                        .subscribe(list -> Platform.runLater(() ->
                                priorityComboBox.getItems().setAll(list)))
        );

        // ViewModel → View: enable/disable nút Save
        disposables.add(
                viewModel.isFormValidObservable()
                        .subscribe(valid -> Platform.runLater(() ->
                                saveBtn.setDisable(!valid)))
        );
        disposables.add(
                viewModel.projectMembersObservable()
                        .subscribe(list -> Platform.runLater(() -> {
                            addAssigneeBtn.setDisable(list == null || list.isEmpty());
                        }))
        );
        // ViewModel → View: hiện/ẩn nút "+" theo role
        // Chỉ Manager mới thấy nút "+"
        disposables.add(
                viewModel.selectedAssigneesObservable()
                        .subscribe(selected -> Platform.runLater(() -> renderAssigneeChips(selected)))
        );

        // Load project members ngay nếu đã có projectId
        if (projectId != null) {
            viewModel.setProjectId(projectId);
            viewModel.loadProjectMembers(currentMemberId);
        }
    }

    // -----------------------------------------------------------------------
    // Render Assignee Chips
    // -----------------------------------------------------------------------

    private void renderAssigneeChips(List<ProjectMemberDTO> members) {
        assigneeChipsPane.getChildren().clear();

        if (members == null || members.isEmpty()) {
            // Nếu chưa chọn ai, hiện lại chữ Unassigned
            Label unassigned = new Label("Unassigned");
            unassigned.getStyleClass().add("field-value"); // Giữ style cũ
            unassigned.setStyle("-fx-text-fill:#999; -fx-font-style:italic;");
            assigneeChipsPane.getChildren().add(unassigned);
        } else {
            // Nếu đã chọn, duyệt list để tạo các "Chip" tên
            for (ProjectMemberDTO member : members) {
                String name = (member.getFullName() != null && !member.getFullName().isBlank())
                        ? member.getFullName() : member.getUsername();

                HBox chip = new HBox(5);
                chip.setAlignment(Pos.CENTER_LEFT);
                chip.setStyle("-fx-background-color:#E2E8F0; -fx-background-radius:15; -fx-padding:2 10;");

                Label nameLabel = new Label(name);
                nameLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#1E293B;");

                // Nút xóa (X) nếu bạn muốn cho phép bỏ chọn
                Button removeBtn = new Button("×");
                removeBtn.setStyle("-fx-background-color:transparent; -fx-padding:0; -fx-cursor:hand;");
                removeBtn.setOnAction(e -> viewModel.removeSelectedAssignee(member.getProjectMemberId()));

                chip.getChildren().addAll(nameLabel, removeBtn);
                assigneeChipsPane.getChildren().add(chip);
            }
        }

        // Luôn add lại nút "+" ở cuối cùng
        assigneeChipsPane.getChildren().add(addAssigneeBtn);
    }

    // -----------------------------------------------------------------------
    // Action handlers
    // -----------------------------------------------------------------------

    @FXML
    private void handleAddAssigneeClick(ActionEvent event) {
        // Lấy snapshot từ ViewModel (đã được cache từ lần load trước)
        List<ProjectMemberDTO> allMembers = viewModel.getProjectMembersSnapshot();

        if (allMembers.isEmpty()) {
            System.out.println("Members size = " + allMembers.size());
            System.out.println("[NewTaskForm] Project members chưa được load.");
            return;
        }

        showMemberSearchPopup(allMembers);
    }

    private void showMemberSearchPopup(List<ProjectMemberDTO> allMembers) {
        ContextMenu popup = new ContextMenu();

        TextField searchField = new TextField();
        searchField.setPromptText("Search members...");
        searchField.setPrefWidth(220);

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

            // IDs đã được chọn → loại khỏi danh sách search
            List<Long> selectedIds = viewModel.getSelectedAssigneesSnapshot()
                    .stream().map(ProjectMemberDTO::getProjectMemberId).collect(Collectors.toList());

            List<ProjectMemberDTO> filtered = allMembers.stream()
                    .filter(m -> {
                        // Loại member đã chọn
                        if (selectedIds.contains(m.getProjectMemberId())) return false;
                        String fn = m.getFullName() != null ? m.getFullName().toLowerCase() : "";
                        String un = m.getUsername() != null ? m.getUsername().toLowerCase() : "";
                        return fn.contains(query) || un.contains(query);
                    })
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                Label noResult = new Label(query.isEmpty()
                        ? "All members selected." : "No member found.");
                noResult.setStyle("-fx-padding:4 8; -fx-text-fill:#888;");
                resultsBox.getChildren().add(noResult);
            } else {
                for (ProjectMemberDTO member : filtered) {
                    String display = (member.getFullName() != null
                            && !member.getFullName().isBlank())
                            ? member.getFullName() : member.getUsername();

                    Button btn = new Button(display);
                    btn.getStyleClass().add("member-item-btn");
                    btn.setOnAction(e -> {
                        popup.hide();
                        // Thêm vào selectedAssignees → trigger chip render qua Observable
                        viewModel.addSelectedAssignee(member);
                    });
                    resultsBox.getChildren().add(btn);
                }
            }
        };

        searchField.textProperty().addListener((obs, old, val) -> filter.run());
        filter.run(); // Hiện full list ngay khi mở

        popup.show(addAssigneeBtn, Side.BOTTOM, 0, 4);
        Platform.runLater(searchField::requestFocus);
    }

    @FXML
    private void handleSave(ActionEvent event) {
        saveBtn.setDisable(true);

        viewModel.setOnSuccess(() -> {
            Platform.runLater(() -> {
                // Gọi hàm reload list của màn hình cha
                if (onSaveSuccessCallback != null) {
                    onSaveSuccessCallback.run();
                }
                closeForm();
            });
        });

        // Gửi request lên server
        viewModel.submitTask();
    }

    // -----------------------------------------------------------------------
    // Close
    // -----------------------------------------------------------------------

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    private void closeForm() {
        disposables.clear();
        if (popupStage != null) {
            popupStage.close();
        }
    }
}