package com.group4.projects_management_fe.features.project;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ProjectDetailsFormController {

    @FXML private StackPane rootPane;
    @FXML private TextField projectNameInput;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionInput;
    @FXML private Label memberCountLabel;

    @FXML private Button editBtn;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @FXML private TextField coManagerInput;
    @FXML private Button addCoManagerBtn;
    @FXML private FlowPane coManagerTagsContainer;

    @FXML private TextField memberInput;
    @FXML private Button addMemberBtn;
    @FXML private FlowPane memberTagsContainer;

    @FXML private Label createdByLabel;
    @FXML private Label createdDateLabel;
    @FXML private Label lastUpdatedByLabel;
    @FXML private Label lastUpdatedDateLabel;

    private final ProjectDetailsViewModel viewModel = new ProjectDetailsViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();

    @FXML
    public void initialize() {
        setupBindings();
        if (rootPane != null) Platform.runLater(() -> rootPane.requestFocus());
    }

    public void initData(Long projectId) {
        if (projectId != null) {
            viewModel.loadProjectDetails(projectId);
        }
    }

    private void setupBindings() {
        // --- 1. TEXT BINDINGS ---
        // --- BINDING TỪ VIEWMODEL XUỐNG VIEW ---

        // 1. Sửa cho ô Project Name
        disposables.add(viewModel.projectNameObservable().subscribe(val -> {
            Platform.runLater(() -> {
                // THÊM ĐIỀU KIỆN CHẶN LOOP Ở ĐÂY:
                if (projectNameInput != null && val != null && !val.equals(projectNameInput.getText())) {
                    projectNameInput.setText(val);
                }
            });
        }));

        // 2. Sửa cho ô Description
        disposables.add(viewModel.descriptionObservable().subscribe(val -> {
            Platform.runLater(() -> {
                // THÊM ĐIỀU KIỆN CHẶN LOOP Ở ĐÂY:
                if (descriptionInput != null && val != null && !val.equals(descriptionInput.getText())) {
                    descriptionInput.setText(val);
                }
            });
        }));

        // --- 2. DATE BINDINGS (Lọc bỏ LocalDate.MIN và chống loop tuyệt đối) ---
        disposables.add(viewModel.startDateObservable()
                .filter(val -> val != null && !val.equals(LocalDate.MIN))
                .distinctUntilChanged()
                .subscribe(val -> Platform.runLater(() -> {
                    if (startDatePicker != null && !val.equals(startDatePicker.getValue())) {
                        startDatePicker.setValue(val);
                    }
                })));

        disposables.add(viewModel.endDateObservable()
                .filter(val -> val != null && !val.equals(LocalDate.MIN))
                .distinctUntilChanged()
                .subscribe(val -> Platform.runLater(() -> {
                    if (endDatePicker != null && !val.equals(endDatePicker.getValue())) {
                        endDatePicker.setValue(val);
                    }
                })));

        disposables.add(viewModel.createdByObservable().subscribe(name ->
                Platform.runLater(() -> { if (createdByLabel != null) createdByLabel.setText(name); })
        ));

        disposables.add(viewModel.createdDateObservable().subscribe(date ->
                Platform.runLater(() -> { if (createdDateLabel != null) createdDateLabel.setText(date); })
        ));

//        disposables.add(viewModel.lastUpdatedByObservable().subscribe(name ->
//                Platform.runLater(() -> { if (lastUpdatedByLabel != null) lastUpdatedByLabel.setText(name); })
//        ));

        disposables.add(viewModel.lastUpdatedDateObservable().subscribe(date ->
                Platform.runLater(() -> { if (lastUpdatedDateLabel != null) lastUpdatedDateLabel.setText(date); })
        ));

        // --- UI -> VIEWMODEL ---
        if (projectNameInput != null) {
            projectNameInput.textProperty().addListener((obs, oldV, newV) -> viewModel.setProjectName(newV));
        }
        if (descriptionInput != null) {
            descriptionInput.textProperty().addListener((obs, oldV, newV) -> viewModel.setDescription(newV));
        }
        if (startDatePicker != null) {
            startDatePicker.valueProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && !newV.equals(oldV)) viewModel.setStartDate(newV);
            });
        }
        if (endDatePicker != null) {
            endDatePicker.valueProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && !newV.equals(oldV)) viewModel.setEndDate(newV);
            });
        }

        // --- 3. QUẢN LÝ TRẠNG THÁI EDIT/VIEW (AN TOÀN NULL-CHECK) ---
        disposables.add(viewModel.isEditingObservable().subscribe(isEditing -> {
            Platform.runLater(() -> {
                if (projectNameInput != null) projectNameInput.setEditable(isEditing);
                if (descriptionInput != null) descriptionInput.setEditable(isEditing);
                if (startDatePicker != null) startDatePicker.setDisable(!isEditing);
                if (endDatePicker != null) endDatePicker.setDisable(!isEditing);
                if (statusComboBox != null) statusComboBox.setDisable(!isEditing);

                if (editBtn != null) {
                    editBtn.setVisible(!isEditing);
                    editBtn.setManaged(!isEditing);
                }
                if (saveBtn != null) {
                    saveBtn.setVisible(isEditing);
                    saveBtn.setManaged(isEditing);
                }
                if (cancelBtn != null) {
//                    cancelBtn.setVisible(isEditing);
//                    cancelBtn.setManaged(isEditing);
                    cancelBtn.setText(isEditing ? "Cancel" : "Close");
                }

                if (coManagerInput != null) coManagerInput.setDisable(!isEditing);
                if (addCoManagerBtn != null) addCoManagerBtn.setDisable(!isEditing);
                if (memberInput != null) memberInput.setDisable(!isEditing);
                if (addMemberBtn != null) addMemberBtn.setDisable(!isEditing);

                if (projectNameInput != null) {
                    projectNameInput.getStyleClass().removeAll("view-mode", "edit-mode");
                    projectNameInput.getStyleClass().add(isEditing ? "edit-mode" : "view-mode");
                }
            });
        }));

        // --- 4. LẮNG NGHE KẾT QUẢ SAVE API ---
        disposables.add(viewModel.onSaveSuccess().subscribe(success -> {
            Platform.runLater(() -> {
                if (saveBtn != null) {
                    saveBtn.setText("Save");
                    saveBtn.setDisable(false);
                }
                // (Tùy chọn) Hiện thông báo Alert thành công ở đây nếu muốn
            });
        }));

        disposables.add(viewModel.onSaveError().subscribe(errorMsg -> {
            Platform.runLater(() -> {
                if (saveBtn != null) {
                    saveBtn.setText("Save");
                    saveBtn.setDisable(false);
                }
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể cập nhật dự án");
                alert.setContentText(errorMsg);
                alert.showAndWait();
            });
        }));
    }

    @FXML private void handleEditMode(ActionEvent event) { viewModel.enableEditMode(); }
    @FXML
    private void handleCancel(ActionEvent event) {
        if ("Close".equals(cancelBtn.getText())) {
            closeForm(); // Nếu đang ở chế độ xem -> Đóng popup
        } else {
            viewModel.cancelEditMode(); // Nếu đang sửa -> Hủy sửa, quay về chế độ xem
        }
    }
    @FXML
    private void handleSave(ActionEvent event) {
        if (saveBtn != null) {
            saveBtn.setText("Saving...");
            saveBtn.setDisable(true); // Disable nút tránh bấm 2 lần
        }
        viewModel.saveChanges();
    }

    @FXML private void handleAddCoManagerClick(ActionEvent event) {}
    @FXML private void handleCoManagerSubmit(ActionEvent event) {}
    @FXML private void handleAddMemberClick(ActionEvent event) {}
    @FXML private void handleMemberSubmit(ActionEvent event) {}

    public void closeForm() {
        disposables.clear();
        if (rootPane != null && rootPane.getScene() != null) {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
    }
}