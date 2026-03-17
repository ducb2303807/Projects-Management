package com.group4.projects_management_fe.features.project;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane; // Đảm bảo rootPane của bạn trong FXML là StackPane (hoặc thay bằng VBox/HBox tương ứng)
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ProjectDetailsFormController {

    // Nút gốc bọc toàn bộ form, dùng để gán CSS class (Nhớ thêm fx:id="rootPane" ở thẻ ngoài cùng FXML)
    @FXML private StackPane rootPane;

    @FXML private TextField projectNameInput;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionInput;
    @FXML private Label memberCountLabel;

    // Các nút chức năng
    @FXML private Button editBtn;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    // Co-manager
    @FXML private TextField coManagerInput;
    @FXML private Button addCoManagerBtn;
    @FXML private FlowPane coManagerTagsContainer;

    // ViewModel & RxJava
    private final ProjectDetailsViewModel viewModel = new ProjectDetailsViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();

    @FXML
    public void initialize() {
        // 1. Setup ComboBox
        statusComboBox.getItems().addAll("NEW", "IN_PROGRESS", "DONE");

        // 2. Setup Logic khóa ngày tháng
        setupUIDateLogic();

        // 3. Setup kết nối dữ liệu
        setupBindings();

        // 4. MÔ PHỎNG: Load dữ liệu từ Database lên khi vừa mở Popup
        viewModel.setProjectName("Hệ thống quản lý nhóm 4");
        viewModel.setStatus("IN_PROGRESS");
        viewModel.addCoManager("Thanh");

        // Khởi động ở Phase 1 (Chỉ xem)
        viewModel.cancelEditMode();

        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> {
            rootPane.requestFocus();
        });
    }

    // ==========================================
    // CÁC LOGIC RÀNG BUỘC (UI & DATA)
    // ==========================================

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

        // ==========================================
        // 3. KHÓA BÀN PHÍM (BẢO MẬT GIAO DIỆN)
        // ==========================================

        // Không cho phép gõ ngày tháng bằng tay để tránh việc người dùng gõ lách luật
        startDatePicker.setEditable(false);
        endDatePicker.setEditable(false);
    }

    private void setupBindings() {
        // 1. BINDING VIEW -> VIEWMODEL (Input)
        projectNameInput.textProperty().addListener((obs, oldVal, newVal) -> viewModel.setProjectName(newVal));
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.setStartDate(newVal));
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.setEndDate(newVal));
        descriptionInput.textProperty().addListener((obs, oldVal, newVal) -> viewModel.setDescription(newVal));
        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.setStatus(newVal));

        // 2. BINDING VIEWMODEL -> VIEW (Output)

        // Lắng nghe thay đổi Phase (View <-> Edit)
        disposables.add(
                viewModel.isEditingObservable()
                        .subscribe(isEditing -> {
                            Platform.runLater(() -> updateUIForEditMode(isEditing));
                        })
        );

        // Lắng nghe danh sách Co-Manager để vẽ Tag
        disposables.add(
                viewModel.coManagersObservable()
                        .subscribe(coManagers -> {
                            Platform.runLater(() -> renderCoManagerTags(coManagers));
                        })
        );
    }

    // ==========================================
    // LOGIC ĐỔI GIAO DIỆN (PHASE 1 <-> PHASE 2)
    // ==========================================

    private void updateUIForEditMode(boolean isEditing) {
        // 1. Bật/Tắt tính năng chỉnh sửa của Input Text
        projectNameInput.setEditable(isEditing);
        descriptionInput.setEditable(isEditing);

        // 2. Bật/Tắt các component đặc biệt (Dùng mouseTransparent để không làm mờ UI)
        startDatePicker.setMouseTransparent(!isEditing);
        endDatePicker.setMouseTransparent(!isEditing);
        statusComboBox.setMouseTransparent(!isEditing);

        // 3. Ẩn/Hiện các nút chức năng
        addCoManagerBtn.setVisible(isEditing);
        editBtn.setVisible(!isEditing); // Đang sửa thì ẩn nút Cây bút

        saveBtn.setVisible(isEditing);
        saveBtn.setManaged(isEditing); // Bật nút Save, cho nó chiếm không gian
        cancelBtn.setText(isEditing ? "Cancel Edit" : "Close");

        // 4. Áp dụng/Gỡ CSS ngụy trang "Read-only"
        if (isEditing) {
            rootPane.getStyleClass().remove("read-only-mode");
            projectNameInput.requestFocus();
        } else {
            if (!rootPane.getStyleClass().contains("read-only-mode")) {
                rootPane.getStyleClass().add("read-only-mode");
            }
        }
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ SỰ KIỆN NÚT BẤM (ON ACTION)
    // ==========================================

    @FXML
    private void handleEditMode(ActionEvent event) {
        viewModel.enableEditMode(); // Chuyển sang Phase 2
    }

    @FXML
    private void handleSave(ActionEvent event) {
        viewModel.saveChanges(); // Lưu và tự động về lại Phase 1
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        if (saveBtn.isVisible()) {
            viewModel.cancelEditMode(); // Đang sửa -> Hủy sửa, về Phase 1
        } else {
            closeForm(); // Đang xem -> Đóng cửa sổ
        }
    }

    @FXML
    private void handleAddCoManagerClick(ActionEvent event) {
        coManagerInput.setVisible(true);
        coManagerInput.setManaged(true);
        coManagerInput.requestFocus();
    }

    @FXML
    private void handleCoManagerSubmit(ActionEvent event) {
        String username = coManagerInput.getText();
        viewModel.addCoManager(username);

        coManagerInput.clear();
        coManagerInput.setVisible(false);
        coManagerInput.setManaged(false);
    }

    // ==========================================
    // CÁC HÀM TIỆN ÍCH (HELPER)
    // ==========================================

    private void renderCoManagerTags(List<String> coManagers) {
        coManagerTagsContainer.getChildren().clear();

        for (String username : coManagers) {
            Label tag = new Label(username + "  ✕");
            tag.getStyleClass().add("co-manager-tag");

            // CHỈ CHO PHÉP XÓA KHI ĐANG Ở CHẾ ĐỘ SỬA
            tag.setOnMouseClicked(e -> {
                if (saveBtn.isVisible()) {
                    viewModel.removeCoManager(username);
                }
            });

            coManagerTagsContainer.getChildren().add(tag);
        }

        // Cập nhật Label hiển thị số member
        if (memberCountLabel != null) {
            int totalMembers = coManagers.size() + 1;
            if (totalMembers <= 1) {
                memberCountLabel.setText("1 member");
            } else {
                memberCountLabel.setText(totalMembers + " members");
            }
        }
    }

    private void closeForm() {
        disposables.clear(); // Xóa sạch bộ nhớ RxJava
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}