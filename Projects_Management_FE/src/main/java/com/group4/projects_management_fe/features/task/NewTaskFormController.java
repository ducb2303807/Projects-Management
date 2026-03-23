package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.LookupDTO;
import com.group4.common.dto.ProjectMemberDTO;
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

    @FXML private StackPane  rootPane;
    @FXML private TextField  taskNameInput;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<LookupDTO> statusComboBox;
    @FXML private ComboBox<LookupDTO> priorityComboBox;

    @FXML private FlowPane   assigneeChipsPane;

    @FXML private Button     addAssigneeBtn;

    @FXML private TextArea   descriptionInput;
    @FXML private Button     saveBtn;


    @Getter
    private NewTaskViewModel viewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Stage popupStage;

    private Long projectId;
    private Long currentMemberId;

    // Gọi từ TasksViewController sau khi tạo Stage, trước showAndWait()
    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    public void setProjectContext(Long projectId, Long currentMemberId) {
        this.projectId       = projectId;
        this.currentMemberId = currentMemberId;
        if (viewModel != null) {
            viewModel.setProjectId(projectId);
            viewModel.loadProjectMembers(currentMemberId);
        }
    }

    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.viewModel = new NewTaskViewModel(sessionProvider);
        if (projectId != null) {
            viewModel.setProjectId(projectId);
        }
        setupBindings();
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        renderAssigneeChips(List.of(), false); // Hiện "Unassigned" mặc định
        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> rootPane.requestFocus());
    }

    // -----------------------------------------------------------------------
    // Setup ComboBox: StringConverter + CellFactory tô màu
    // -----------------------------------------------------------------------

    private void setupComboBoxes() {
        StringConverter<LookupDTO> converter = new StringConverter<>() {
            @Override public String toString(LookupDTO dto) {
                return dto == null || dto.getName() == null ? "" : dto.getName();
            }
            @Override public LookupDTO fromString(String s) { return null; }
        };

        Callback<ListView<LookupDTO>, ListCell<LookupDTO>> cellFactory =
                lv -> new ListCell<>() {
                    @Override protected void updateItem(LookupDTO dto, boolean empty) {
                        super.updateItem(dto, empty);
                        getStyleClass().removeIf(s ->
                                s.startsWith("lookup-") || s.startsWith("status-") || s.startsWith("priority-"));
                        if (empty || dto == null || dto.getName() == null) {
                            setText(null); setGraphic(null);
                        } else {
                            setText(dto.getName());
                            getStyleClass().addAll("lookup-badge",
                                    slugify(dto.getName()));
                            setStyle(getColorStyle(dto));
                        }
                    }
                };

        statusComboBox.setConverter(converter);
        statusComboBox.setCellFactory(cellFactory);
        statusComboBox.setButtonCell(cellFactory.call(null));

        priorityComboBox.setConverter(converter);
        priorityComboBox.setCellFactory(cellFactory);
        priorityComboBox.setButtonCell(cellFactory.call(null));
    }

    private String slugify(String name) {
        return name.toLowerCase().replace("_", "-").replace(" ", "-");
    }

    private String getColorStyle(LookupDTO item) {
        if (item == null || item.getName() == null) return "";
        return switch (item.getName().toUpperCase()) {
            case "CẦN LÀM", "TO DO"               -> "-fx-text-fill:#757575; -fx-font-weight:bold;";
            case "ĐANG LÀM", "IN PROGRESS"         -> "-fx-text-fill:#FCAB10; -fx-font-weight:bold;";
            case "ĐANG KIỂM TRA", "UNDER REVIEW"   -> "-fx-text-fill:#7B68EE; -fx-font-weight:bold;";
            case "HOÀN THÀNH", "DONE"              -> "-fx-text-fill:#2E7D32; -fx-font-weight:bold;";
            case "ĐÃ HỦY", "CANCELLED"             -> "-fx-text-fill:#C62828; -fx-font-weight:bold;";
            case "KHẨN CẤP", "URGENT"              -> "-fx-text-fill:#C62828; -fx-font-weight:bold;";
            case "CAO", "HIGH"                     -> "-fx-text-fill:#EF6C00; -fx-font-weight:bold;";
            case "TRUNG BÌNH", "MEDIUM"             -> "-fx-text-fill:#FCAB10; -fx-font-weight:bold;";
            case "THẤP", "LOW"                     -> "-fx-text-fill:#2E7D32; -fx-font-weight:bold;";
            default                                -> "-fx-text-fill:#333333;";
        };
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

        // ViewModel → View: hiện/ẩn nút "+" theo role
        // Chỉ Manager mới thấy nút "+"
        disposables.add(
                viewModel.canManageAssigneesObservable()
                        .subscribe(canManage -> Platform.runLater(() -> {
                            addAssigneeBtn.setVisible(canManage);
                            addAssigneeBtn.setManaged(canManage);
                        }))
        );

        // ViewModel → View: render lại chip khi selectedAssignees thay đổi
        // (mỗi lần add/remove member)
        disposables.add(
                viewModel.selectedAssigneesObservable()
                        .subscribe(selected -> Platform.runLater(() ->
                                renderAssigneeChips(
                                        selected,
                                        Boolean.TRUE.equals(
                                                viewModel.canManageAssigneesObservable()
                                                        .blockingFirst()))))
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

    private void renderAssigneeChips(List<ProjectMemberDTO> members, boolean withRemoveBtn) {
        assigneeChipsPane.getChildren().clear();

        if (members == null || members.isEmpty()) {
            Label unassigned = new Label("Unassigned");
            unassigned.setStyle("-fx-text-fill:#999; -fx-font-style:italic;");
            assigneeChipsPane.getChildren().add(unassigned);
            return;
        }

        for (ProjectMemberDTO member : members) {
            String name = (member.getFullName() != null && !member.getFullName().isBlank())
                    ? member.getFullName()
                    : (member.getUsername() != null ? member.getUsername() : "Unknown");

            HBox chip = new HBox(4);
            chip.setAlignment(Pos.CENTER_LEFT);
            chip.setStyle("-fx-background-color:#E2E8F0;"
                    + "-fx-background-radius:12;"
                    + "-fx-padding:3 10 3 10;");

            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#333;");
            chip.getChildren().add(nameLabel);

            if (withRemoveBtn) {
                Button removeBtn = new Button("×");
                removeBtn.setStyle("-fx-background-color:transparent;"
                        + "-fx-text-fill:#888;"
                        + "-fx-font-size:12px;"
                        + "-fx-cursor:hand;"
                        + "-fx-padding:0 0 0 4;");
                removeBtn.setOnAction(e ->
                        viewModel.removeSelectedAssignee(member.getProjectMemberId()));
                chip.getChildren().add(removeBtn);
            }

            assigneeChipsPane.getChildren().add(chip);
        }
    }

    // -----------------------------------------------------------------------
    // Action handlers
    // -----------------------------------------------------------------------

    @FXML
    private void handleAddAssigneeClick(ActionEvent event) {
        // Lấy snapshot từ ViewModel (đã được cache từ lần load trước)
        List<ProjectMemberDTO> allMembers = viewModel.getProjectMembersSnapshot();

        if (allMembers.isEmpty()) {
            // Chưa load xong hoặc project không có member → không làm gì
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
                    btn.setStyle("-fx-background-color:transparent;"
                            + "-fx-alignment:CENTER_LEFT;"
                            + "-fx-pref-width:220;"
                            + "-fx-padding:6 10;");
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
        viewModel.setOnSuccess(this::closeForm);
        viewModel.submitTask();
        // Đóng popup ngay, onSuccess callback sẽ reload list ở TasksViewController
        closeForm();
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