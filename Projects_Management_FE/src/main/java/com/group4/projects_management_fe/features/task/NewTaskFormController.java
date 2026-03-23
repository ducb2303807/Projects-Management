package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.LookupDTO;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import lombok.Getter;

import java.util.List;

public class NewTaskFormController {

    @FXML private StackPane rootPane;
    @FXML private TextField taskNameInput;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<LookupDTO> statusComboBox;
    @FXML private ComboBox<LookupDTO> priorityComboBox;
    @FXML private Label     assigneeLabel;
    @FXML private Button    addAssigneeBtn;
    @FXML private TextField assigneeInput;
    @FXML private TextArea  descriptionInput;
    @FXML private Button    saveBtn;

    @Getter
    private NewTaskViewModel viewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Stage popupStage;

    // Gọi từ TasksViewController sau khi tạo Stage, trước showAndWait()
    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    @FXML
    public void initialize() {
        System.out.println(">>> FORM ĐÃ MỞ!"); // Kiểm tra xem Controller có chạy không
        setupStatusComboBox();
        setupPriorityComboBox();
        setupComboBoxStyling();

        assigneeLabel.setText("Unassigned");
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

    // -----------------------------------------------------------------------
    // Session & Bindings
    // -----------------------------------------------------------------------

    public void setSessionProvider(AuthSessionProvider sessionProvider) {
        this.viewModel = new NewTaskViewModel(sessionProvider);
        setupBindings();
    }

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

        // ViewModel → View: data từ DB populate vào ComboBox
        disposables.add(
                viewModel.taskStatusesObservable()
                        .subscribe(list -> Platform.runLater(() ->
                                statusComboBox.getItems().setAll(list)))
        );

        disposables.add(
                viewModel.prioritiesObservable()
                        .subscribe(list -> Platform.runLater(() ->
                                priorityComboBox.getItems().setAll(list)))
        );

        disposables.add(
                viewModel.isFormValidObservable()
                        .subscribe(valid -> Platform.runLater(() ->
                                saveBtn.setDisable(!valid)))
        );

        disposables.add(
                viewModel.assigneeObservable()
                        .subscribe(name -> Platform.runLater(() ->
                                assigneeLabel.setText(
                                        name == null || name.isEmpty() ? "Unassigned" : name)))
        );
    }

    // -----------------------------------------------------------------------
    // Action handlers
    // -----------------------------------------------------------------------

    @FXML
    private void handleAddAssigneeClick(ActionEvent event) {
        assigneeInput.setVisible(true);
        assigneeInput.setManaged(true);
        assigneeInput.requestFocus();
    }

    @FXML
    private void handleAssigneeSubmit(ActionEvent event) {
        String username = assigneeInput.getText().trim();
        if (!username.isEmpty()) viewModel.setAssignee(username);
        assigneeInput.clear();
        assigneeInput.setVisible(false);
        assigneeInput.setManaged(false);
    }

    @FXML
    private void handleSave(ActionEvent event) {
        viewModel.submitTask();
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