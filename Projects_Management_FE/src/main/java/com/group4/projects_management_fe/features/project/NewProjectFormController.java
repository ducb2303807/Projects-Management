package com.group4.projects_management_fe.features.project;

import com.group4.projects_management_fe.core.session.AppSessionManager;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class NewProjectFormController {

    @FXML private StackPane rootPane;
    @FXML private TextField projectNameInput;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea descriptionInput;
    @FXML private Button createBtn;
    @FXML private Label creatorUsernameLabel;

    private final NewProjectViewModel viewModel = new NewProjectViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Runnable onSuccessCallback;

//    public void setOnSuccessCallback(Runnable onSuccessCallback) {
//        this.onSuccessCallback = onSuccessCallback;
//    }

    @FXML
    public void initialize() {
        // 1. Setup logic giao diện cho DatePicker
        setupUIDateLogic();

        // 2. Chống auto-focus vào ô nhập liệu đầu tiên
        Platform.runLater(() -> rootPane.requestFocus());

        // 3. Lấy Username của người đăng nhập hiển thị lên form
        if (AppSessionManager.getInstance().isLoggedIn() && AppSessionManager.getInstance().getCurrentUser() != null) {
            String currentUsername = AppSessionManager.getInstance().getCurrentUser().getUsername();
            creatorUsernameLabel.setText(currentUsername);
        } else {
            creatorUsernameLabel.setText("System");
        }

        // ==========================================
        // 4. RÀNG BUỘC DỮ LIỆU TỪ UI XUỐNG VIEW_MODEL
        // ==========================================
        projectNameInput.textProperty().addListener((obs, oldV, newV) -> viewModel.setProjectName(newV));
        startDatePicker.valueProperty().addListener((obs, oldV, newV) -> viewModel.setStartDate(newV));
        endDatePicker.valueProperty().addListener((obs, oldV, newV) -> viewModel.setEndDate(newV));
        descriptionInput.textProperty().addListener((obs, oldV, newV) -> viewModel.setDescription(newV));

        // 5. Ràng buộc trạng thái nút Create (Chỉ bật khi Tên và Start Date hợp lệ)
        disposables.add(viewModel.isFormValidObservable().subscribe(isValid -> {
            Platform.runLater(() -> createBtn.setDisable(!isValid));
        }));

        // 6. Lắng nghe kết quả API thành công
        disposables.add(viewModel.onCreateSuccess().subscribe(response -> {
            Platform.runLater(() -> {
                if (onSuccessCallback != null) onSuccessCallback.run();
                closeForm();
            });
        }));

        // 7. Lắng nghe kết quả API thất bại
        disposables.add(viewModel.onCreateError().subscribe(errorMessage -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
                alert.showAndWait();
                createBtn.setDisable(false);
                createBtn.setText("Create");
            });
        }));
    }

    private void setupUIDateLogic() {
        // ==========================================
        // 1. RÀNG BUỘC HIỂN THỊ TRÊN LỊCH (UI RENDER)
        // ==========================================

        // Ràng buộc End Date: Không được phép chọn ngày TRƯỚC Start Date
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = startDatePicker.getValue();

                if (startDate != null && date.isBefore(startDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Màu hồng nhạt báo hiệu không chọn được
                }
            }
        });

        // Ràng buộc Start Date: Không được phép chọn ngày SAU End Date
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate endDate = endDatePicker.getValue();

                if (endDate != null && date.isAfter(endDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Màu hồng nhạt báo hiệu không chọn được
                }
            }
        });

        // ==========================================
        // 2. RÀNG BUỘC KHI DỮ LIỆU THAY ĐỔI (LISTENERS)
        // ==========================================

        // Nếu người dùng chọn lại Start Date, reset lại End Date nếu nó bị lùi về trước Start Date
        startDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (endDatePicker.getValue() != null && newValue != null && endDatePicker.getValue().isBefore(newValue)) {
                endDatePicker.setValue(null);
            }
        });

        // Nếu người dùng chọn lại End Date, reset lại Start Date nếu nó bị vượt quá End Date
        endDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (startDatePicker.getValue() != null && newValue != null && newValue.isBefore(startDatePicker.getValue())) {
                startDatePicker.setValue(null);
            }
        });
    }

    @FXML
    private void handleCreate(ActionEvent event) {
        createBtn.setDisable(true);
        createBtn.setText("Creating...");
        viewModel.submitProject();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    private void closeForm() {
        disposables.clear();
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}