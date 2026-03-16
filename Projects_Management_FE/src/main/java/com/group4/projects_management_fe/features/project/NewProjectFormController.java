package com.group4.projects_management_fe.features.project;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

public class NewProjectFormController {

    @FXML private TextField projectNameInput;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionInput;

    @FXML private Label memberCountLabel;
    @FXML private Button addCoManagerBtn;
    @FXML private TextField coManagerInput;
    @FXML private FlowPane coManagerTagsContainer;

    // Mặc định luôn có 1 member (người tạo/owner)
    private int totalMembers = 1;

    @FXML
    public void initialize() {
        // 1. Khởi tạo list Status
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Đang diễn ra",
                "Đã hoàn thành"
        ));

        // 2. Ràng buộc DatePicker: End Date phải >= Start Date
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                java.time.LocalDate startDate = startDatePicker.getValue();
                // Disable những ngày trước Start Date
                if (startDate != null && date.isBefore(startDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Màu nhạt báo hiệu không chọn được
                }
            }
        });

        // Nếu người dùng chọn lại Start Date, reset hoặc check lại End Date
        startDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (endDatePicker.getValue() != null && newValue != null && endDatePicker.getValue().isBefore(newValue)) {
                endDatePicker.setValue(null); // Reset End Date nếu nó bị lùi về trước Start Date
            }
        });
    }

    // Xử lý khi bấm nút "+"
    @FXML
    private void handleAddCoManagerClick(ActionEvent event) {
        coManagerInput.setVisible(true);
        coManagerInput.setManaged(true);
        coManagerInput.requestFocus(); // Focus thẳng vào ô để gõ luôn
    }

    // Xử lý khi nhập xong username và bấm Enter (trong ô text)
    @FXML
    private void handleCoManagerSubmit(ActionEvent event) {
        String username = coManagerInput.getText().trim();
        if (!username.isEmpty()) {
            addCoManagerTag(username);

            // Ẩn ô nhập và reset text
            coManagerInput.setText("");
            coManagerInput.setVisible(false);
            coManagerInput.setManaged(false);
        }
    }

    // Hàm tạo giao diện cái "Tag" (VD: hunny02 x)
    private void addCoManagerTag(String username) {
        HBox tagBox = new HBox();
        tagBox.getStyleClass().add("user-tag");

        Label nameLabel = new Label(username);
        nameLabel.getStyleClass().add("user-tag-text");

        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("user-tag-close");

        // Sự kiện khi bấm 'x' để xóa tag
        closeBtn.setOnAction(e -> {
            coManagerTagsContainer.getChildren().remove(tagBox);
            updateMemberCount(-1); // Giảm member
        });

        tagBox.getChildren().addAll(nameLabel, closeBtn);
        coManagerTagsContainer.getChildren().add(tagBox);

        updateMemberCount(1); // Tăng member
    }

    // Cập nhật text hiển thị số lượng member
    private void updateMemberCount(int change) {
        totalMembers += change;
        if (totalMembers <= 1) {
            memberCountLabel.setText("1 member");
        } else {
            memberCountLabel.setText(totalMembers + " members");
        }
    }

    @FXML
    private void handleCreate(ActionEvent event) {
        // Lấy dữ liệu test thử
        String projectName = projectNameInput.getText();
        System.out.println("Tạo Project mới: " + projectName);
        System.out.println("Total Members: " + totalMembers);

        // TODO: Viết logic lưu vào Database hoặc List ở đây

        // Tạo xong thì đóng Form
        closeForm();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Hủy bỏ thì chỉ cần đóng form
        closeForm();
    }

    // Hàm tiện ích để đóng cửa sổ Popup hiện tại
    private void closeForm() {
        javafx.stage.Stage stage = (javafx.stage.Stage) projectNameInput.getScene().getWindow();
        stage.close();
    }
}