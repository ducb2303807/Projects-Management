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
import java.util.List;

public class ProjectDetailsFormController {

    @FXML private StackPane rootPane;

    @FXML private TextField projectNameInput;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionInput;
    @FXML private Label memberCountLabel;

    // Nút điều khiển chính
    @FXML private Button editBtn;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    // Co-manager
    @FXML private TextField coManagerInput;
    @FXML private Button addCoManagerBtn;
    @FXML private FlowPane coManagerTagsContainer;

    // Members (MỚI THÊM)
    @FXML private TextField memberInput;
    @FXML private Button addMemberBtn;
    @FXML private FlowPane memberTagsContainer;

    private final ProjectDetailsViewModel viewModel = new ProjectDetailsViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("NEW", "IN_PROGRESS", "DONE");

        setupUIDateLogic();
        setupBindings();

        // Chặn auto-focus vào ô nhập Tên dự án lúc vừa mở lên
        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> rootPane.requestFocus());

        // MÔ PHỎNG DỮ LIỆU
        viewModel.setProjectName("Hệ thống quản lý nhóm 4");
//        viewModel.setStatus("IN_PROGRESS");
//        viewModel.addCoManager("Thanh");
        viewModel.addMember("Nam");
        viewModel.addMember("Binh");

        viewModel.cancelEditMode(); // Bắt đầu ở chế độ chỉ xem
    }

    private void setupUIDateLogic() {
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = startDatePicker.getValue();
                if (startDate != null && date.isBefore(startDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate endDate = endDatePicker.getValue();
                if (endDate != null && date.isAfter(endDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        startDatePicker.valueProperty().addListener((ov, oldVal, newVal) -> {
            if (endDatePicker.getValue() != null && newVal != null && endDatePicker.getValue().isBefore(newVal)) {
                endDatePicker.setValue(null);
            }
        });

        endDatePicker.valueProperty().addListener((ov, oldVal, newVal) -> {
            if (startDatePicker.getValue() != null && newVal != null && newVal.isBefore(startDatePicker.getValue())) {
                startDatePicker.setValue(null);
            }
        });

        startDatePicker.setEditable(false);
        endDatePicker.setEditable(false);
    }

    private void setupBindings() {
        // ... (Bạn tự thêm binding cho input như cũ nếu cần) ...

        disposables.add(viewModel.isEditingObservable().subscribe(isEditing -> {
            Platform.runLater(() -> updateUIForEditMode(isEditing));
        }));

//        disposables.add(viewModel.coManagersObservable().subscribe(coManagers -> {
//            Platform.runLater(() -> renderCoManagerTags(coManagers));
//        }));

        // BINDING CHO MEMBERS
        disposables.add(viewModel.membersObservable().subscribe(members -> {
            Platform.runLater(() -> renderMemberTags(members));
        }));
    }

    private void updateUIForEditMode(boolean isEditing) {
        projectNameInput.setEditable(isEditing);
        descriptionInput.setEditable(isEditing);

        startDatePicker.setMouseTransparent(!isEditing);
        endDatePicker.setMouseTransparent(!isEditing);
        statusComboBox.setMouseTransparent(!isEditing);

        // Bật tắt các nút thêm người
        addCoManagerBtn.setVisible(isEditing);
        addMemberBtn.setVisible(isEditing); // MỚI: Chỉ hiện nút thêm Member khi đang Edit

        editBtn.setVisible(!isEditing);
        saveBtn.setVisible(isEditing);
        saveBtn.setManaged(isEditing);
        cancelBtn.setText(isEditing ? "Cancel Edit" : "Close");

        if (isEditing) {
            rootPane.getStyleClass().remove("read-only-mode");
            projectNameInput.requestFocus();
        } else {
            if (!rootPane.getStyleClass().contains("read-only-mode")) {
                rootPane.getStyleClass().add("read-only-mode");
            }
            // Ẩn thanh input đi lỡ người dùng đang gõ dở mà bấm Cancel Edit
            coManagerInput.setVisible(false); coManagerInput.setManaged(false);
            memberInput.setVisible(false); memberInput.setManaged(false);
        }
    }

    // ==========================================
    // ON ACTION NÚT CHÍNH
    // ==========================================
    @FXML private void handleEditMode(ActionEvent event) { viewModel.enableEditMode(); }
    @FXML private void handleSave(ActionEvent event) { viewModel.saveChanges(); }
    @FXML private void handleCancel(ActionEvent event) {
        if (saveBtn.isVisible()) viewModel.cancelEditMode();
        else closeForm();
    }

    // ==========================================
    // ON ACTION CO-MANAGER
    // ==========================================
    @FXML private void handleAddCoManagerClick(ActionEvent event) {
        coManagerInput.setVisible(true); coManagerInput.setManaged(true); coManagerInput.requestFocus();
    }
    @FXML private void handleCoManagerSubmit(ActionEvent event) {
//        viewModel.addCoManager(coManagerInput.getText());
        coManagerInput.clear(); coManagerInput.setVisible(false); coManagerInput.setManaged(false);
    }

    // ==========================================
    // ON ACTION MEMBERS (MỚI)
    // ==========================================
    @FXML private void handleAddMemberClick(ActionEvent event) {
        memberInput.setVisible(true); memberInput.setManaged(true); memberInput.requestFocus();
    }
    @FXML private void handleMemberSubmit(ActionEvent event) {
        viewModel.addMember(memberInput.getText());
        memberInput.clear(); memberInput.setVisible(false); memberInput.setManaged(false);
    }

    // ==========================================
    // RENDER TAGS
    // ==========================================
    private void renderCoManagerTags(List<String> coManagers) {
        coManagerTagsContainer.getChildren().clear();
        for (String username : coManagers) {
            Label tag = new Label(username + "  ✕");
            tag.getStyleClass().add("co-manager-tag");
            tag.setOnMouseClicked(e -> {
//                if (saveBtn.isVisible()) viewModel.removeCoManager(username); // Chỉ xóa khi đang Edit
            });
            coManagerTagsContainer.getChildren().add(tag);
        }
    }

    private void renderMemberTags(List<String> membersList) {
        memberTagsContainer.getChildren().clear();
        for (String username : membersList) {
            Label tag = new Label(username + "  ✕");
            // Tái sử dụng CSS class của Co-manager để giao diện đồng bộ
            tag.getStyleClass().add("co-manager-tag");
            tag.setOnMouseClicked(e -> {
                if (saveBtn.isVisible()) viewModel.removeMember(username); // Chỉ xóa khi đang Edit
            });
            memberTagsContainer.getChildren().add(tag);
        }

        // Tổng số thành viên (Bao gồm cả Co-manager và Member)
        if (memberCountLabel != null) {
            int total = membersList.size() + coManagerTagsContainer.getChildren().size() + 1; // +1 là chủ dự án
            memberCountLabel.setText(total <= 1 ? "1 member" : total + " members");
        }
    }

    private void closeForm() {
        disposables.clear();
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}