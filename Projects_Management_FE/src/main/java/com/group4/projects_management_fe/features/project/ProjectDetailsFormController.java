package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.UserDTO;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.group4.common.dto.ProjectMemberDTO;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.geometry.Side;

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
    @FXML private Button deleteBtn;

    @FXML private TextField coManagerInput;
    @FXML private Button addCoManagerBtn;
    @FXML private FlowPane coManagerTagsContainer;

    @FXML private TextField memberInput;
    @FXML private Button addMemberBtn;
    @FXML private FlowPane memberTagsContainer;

    @FXML private Label createdByLabel;
    @FXML private Label createdDateLabel;
    @FXML private Label lastUpdatedDateLabel;

    private ContextMenu coManagerDropdown = new ContextMenu();
    private ContextMenu memberDropdown = new ContextMenu();

    private final ProjectDetailsViewModel viewModel = new ProjectDetailsViewModel();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private boolean isUpdatingStatus = false;

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
                if (projectNameInput != null && val != null && !val.equals(projectNameInput.getText())) {
                    projectNameInput.setText(val);
                }
            });
        }));

        // 2. Sửa cho ô Description
        disposables.add(viewModel.descriptionObservable().subscribe(val -> {
            Platform.runLater(() -> {
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
                // 1. Các ô TextField bình thường
                if (projectNameInput != null) projectNameInput.setEditable(isEditing);
                if (descriptionInput != null) descriptionInput.setEditable(isEditing);

                // 2. CÁCH MỚI CHO DATEPICKER & COMBOBOX (CHỐNG LÀM MỜ)
                boolean isReadOnly = !isEditing;

                if (startDatePicker != null) {
                    startDatePicker.setMouseTransparent(isReadOnly);
                    startDatePicker.setFocusTraversable(isEditing);
                }
                if (endDatePicker != null) {
                    endDatePicker.setMouseTransparent(isReadOnly);
                    endDatePicker.setFocusTraversable(isEditing);
                }
                if (statusComboBox != null) {
                    statusComboBox.setMouseTransparent(isReadOnly);
                    statusComboBox.setFocusTraversable(isEditing);
                }

                // 3. Xử lý ẩn/hiện các nút bấm
                if (editBtn != null) {
                    editBtn.setVisible(!isEditing);
                    editBtn.setManaged(!isEditing);
                }
                if (saveBtn != null) {
                    saveBtn.setVisible(isEditing);
                    saveBtn.setManaged(isEditing);
                }
                if (cancelBtn != null) {
                    cancelBtn.setText(isEditing ? "Cancel" : "Close");
                }

                // --- CẬP NHẬT: Ẩn/Hiện nút Delete ---
                if (deleteBtn != null) {
                    deleteBtn.setVisible(isEditing);
                    deleteBtn.setManaged(isEditing);
                }

                // 4. Các trường nhập liệu phụ
                if (coManagerInput != null) coManagerInput.setDisable(!isEditing);
                if (addCoManagerBtn != null) addCoManagerBtn.setDisable(!isEditing);
                if (memberInput != null) memberInput.setDisable(!isEditing);
                if (addMemberBtn != null) addMemberBtn.setDisable(!isEditing);

                // 5. Cập nhật class CSS để đổi giao diện
                if (projectNameInput != null) {
                    projectNameInput.getStyleClass().removeAll("view-mode", "edit-mode");
                    projectNameInput.getStyleClass().add(isEditing ? "edit-mode" : "view-mode");
                }
            });
        }));

        // --- 4. LẮNG NGHE KẾT QUẢ SAVE/DELETE API ---
        disposables.add(viewModel.onSaveSuccess().subscribe(success -> {
            Platform.runLater(() -> {
                if (saveBtn != null) {
                    saveBtn.setText("Save");
                    saveBtn.setDisable(false);
                }
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

        // --- BỔ SUNG: Lắng nghe sự kiện Delete thành công ---
        disposables.add(viewModel.onDeleteSuccess().subscribe(success -> {
            Platform.runLater(this::closeForm); // Tự động đóng form Details khi xóa mềm xong
        }));

        // --- BỔ SUNG: Gắn Action cho nút Delete ---
        if (deleteBtn != null) {
            deleteBtn.setOnAction(e -> showDeleteConfirmation());
        }

        // ==========================================================
        // KHU VỰC BINDING STATUS
        // ==========================================================

        // --- 1. ĐỔ DANH SÁCH OPTION TỪ API VÀO COMBOBOX ---
        disposables.add(viewModel.statusListObservable()
                .filter(list -> list != null && !list.isEmpty())
                .subscribe(list -> {
                    Platform.runLater(() -> {
                        if (statusComboBox != null) {
                            if (!statusComboBox.getItems().equals(list)) {
                                isUpdatingStatus = true;

                                String currentVal = statusComboBox.getValue();
                                statusComboBox.getItems().setAll(list);

                                if (currentVal != null && list.contains(currentVal)) {
                                    statusComboBox.setValue(currentVal);
                                }

                                isUpdatingStatus = false;
                            }
                        }
                    });
                }));

        // --- 2. BINDING: VIEWMODEL -> GIAO DIỆN ---
        disposables.add(viewModel.statusNameObservable()
                .distinctUntilChanged()
                .subscribe(val -> {
                    Platform.runLater(() -> {
                        if (statusComboBox != null && val != null && !val.equals(statusComboBox.getValue())) {
                            isUpdatingStatus = true;
                            statusComboBox.setValue(val);
                            isUpdatingStatus = false;
                        }
                    });
                }));

        // --- 3. BINDING: GIAO DIỆN -> VIEWMODEL ---
        if (statusComboBox != null) {
            statusComboBox.valueProperty().addListener((obs, oldV, newV) -> {
                if (isUpdatingStatus) return;

                if (newV != null && !newV.equals(oldV)) {
                    viewModel.setStatusName(newV);

                    statusComboBox.getStyleClass().removeAll("status-1", "status-2", "status-3", "status-4", "status-5");

                    Long statusId = viewModel.getStatusNameToIdMap().get(newV);

                    if (statusId != null) {
                        if (statusId == 1L) statusComboBox.getStyleClass().add("status-1");
                        else if (statusId == 2L) statusComboBox.getStyleClass().add("status-2");
                        else if (statusId == 3L) statusComboBox.getStyleClass().add("status-3");
                        else if (statusId == 4L) statusComboBox.getStyleClass().add("status-4");
                        else statusComboBox.getStyleClass().add("status-5");
                    }
                }
            });
        }

        // ==========================================================
        // TÔ MÀU CHO TỪNG DÒNG TRONG DANH SÁCH XỔ XUỐNG CỦA COMBOBOX
        // ==========================================================
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            setStyle("-fx-background-color: transparent;");
                        } else {
                            Label badge = new Label(item);
                            badge.setStyle(getBadgeStyle(item));

                            badge.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                            badge.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

                            HBox container = new HBox(badge);
                            container.setAlignment(Pos.CENTER_LEFT);
                            container.setStyle("-fx-background-color: transparent;");

                            setText(null);
                            setGraphic(container);
                            setStyle("-fx-background-color: transparent; -fx-padding: 5px 10px;");
                        }
                    }
                };
            }
        };

        disposables.add(
                viewModel.getProjectMembersObservable()
                        .subscribe(members -> {
                            // Đảm bảo thao tác UI chạy trên luồng JavaFX
                            Platform.runLater(() -> renderMembers(members));
                        }, error -> error.printStackTrace())
        );

        statusComboBox.setCellFactory(cellFactory);
        statusComboBox.setButtonCell(cellFactory.call(null));

        setupSearchBindings();
    }

    @FXML private void handleEditMode(ActionEvent event) { viewModel.enableEditMode(); }

    @FXML
    private void handleCancel(ActionEvent event) {
        if ("Close".equals(cancelBtn.getText())) {
            closeForm();
        } else {
            viewModel.cancelEditMode();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (saveBtn != null) {
            saveBtn.setText("Saving...");
            saveBtn.setDisable(true);
        }
        viewModel.saveChanges();
    }

    public void closeForm() {
        disposables.clear();
        if (rootPane != null && rootPane.getScene() != null) {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
    }

    // --- BỔ SUNG: HÀM HIỂN THỊ POPUP XÁC NHẬN XÓA ---
    private void showDeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete project?");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this project?");

        // Tạo 2 nút Custom
        ButtonType btnDelete = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnDelete, btnCancel);

        // Lấy object Button thật ra để cấu hình Highlight
        Node deleteButtonNode = alert.getDialogPane().lookupButton(btnDelete);
        Node cancelButtonNode = alert.getDialogPane().lookupButton(btnCancel);

        if (cancelButtonNode instanceof Button) {
            ((Button) cancelButtonNode).setDefaultButton(true);  // Highlight Cancel (Nút mặc định khi nhấn Enter)
        }
        if (deleteButtonNode instanceof Button) {
            ((Button) deleteButtonNode).setDefaultButton(false); // Không highlight Delete
        }

        // Hiển thị và chờ user phản hồi
        alert.showAndWait().ifPresent(type -> {
            if (type == btnDelete) {
                // Nếu bấm Delete -> Gọi ViewModel xử lý đổi Status
                viewModel.deleteProject();
            }
            // Nếu bấm Cancel -> Popup tự tắt, giữ nguyên trạng thái Edit Mode
        });
    }

    private String getBadgeStyle(String statusName) {
        if (statusName == null) return "-fx-background-color: transparent;";

        String bgColor;
        String textColor;

        switch (statusName.trim()) {
            case "Planning":
                bgColor = "#E1F0FF"; textColor = "#0052CC";
                break;
            case "Active":
                bgColor = "#FFF0B3"; textColor = "#FF991F";
                break;
            case "Completed":
                bgColor = "#E3FCEF"; textColor = "#006644";
                break;
            case "On Hold":
                bgColor = "#FFEBE6"; textColor = "#BF2600";
                break;
            case "Cancelled":
                bgColor = "#EBECF0"; textColor = "#42526E";
                break;
            default:
                bgColor = "#F4F5F7"; textColor = "#091E42";
                break;
        }

        return String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 4px 12px; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 12px;",
                bgColor, textColor
        );
    }

    // ==========================================
    // XỬ LÝ ẨN/HIỆN Ô TÌM KIẾM CO-MANAGER VÀ MEMBER
    // ==========================================

    @FXML
    private void handleAddCoManagerClick(ActionEvent event) {
        if (coManagerInput != null) {
            boolean isCurrentlyVisible = coManagerInput.isVisible();

            // Đảo ngược trạng thái bật/tắt
            coManagerInput.setVisible(!isCurrentlyVisible);
            coManagerInput.setManaged(!isCurrentlyVisible);

            if (!isCurrentlyVisible) {
                // Nếu vừa được bật lên -> Đưa con trỏ chuột vào để gõ ngay
                coManagerInput.requestFocus();
            } else {
                // Nếu bị tắt đi -> Xóa sạch text bên trong
                coManagerInput.clear();
            }
        }
    }

    @FXML
    private void handleAddMemberClick(ActionEvent event) {
        if (memberInput != null) {
            boolean isCurrentlyVisible = memberInput.isVisible();

            // Đảo ngược trạng thái bật/tắt
            memberInput.setVisible(!isCurrentlyVisible);
            memberInput.setManaged(!isCurrentlyVisible);

            if (!isCurrentlyVisible) {
                // Nếu vừa được bật lên -> Đưa con trỏ chuột vào để gõ ngay
                memberInput.requestFocus();
            } else {
                // Nếu bị tắt đi -> Xóa sạch text bên trong
                memberInput.clear();
            }
        }
    }

    private void renderMembers(List<ProjectMemberDTO> members) {
        // Xóa sạch Node cũ trước khi vẽ lại
        coManagerTagsContainer.getChildren().clear();
        memberTagsContainer.getChildren().clear();

        // Thêm một biến đếm tổng số thành viên Active
        int totalActiveMembers = 0;

        if (members == null || members.isEmpty()) {
            if (memberCountLabel != null) memberCountLabel.setText("(0)");
            return;
        }

        for (ProjectMemberDTO member : members) {
            // 1. Dọn dẹp khoảng trắng
            String status = member.getStatusName() != null ? member.getStatusName().trim() : "";
            String role = member.getRoleName() != null ? member.getRoleName().trim() : "";

            // 2. Chỉ lấy thành viên Active
            if (!"Active".equalsIgnoreCase(status)) {
                continue;
            }

            // 3. Tăng biến đếm tổng (Vì đã lọt qua if Active ở trên, người này chắc chắn được tính)
            totalActiveMembers++;

            // 4. Tạo Node giao diện (Badge) và phân loại hiển thị
            if ("Co-Project Manager".equalsIgnoreCase(role)) {
                Node badge = createMemberBadge(member);
                coManagerTagsContainer.getChildren().add(badge);
            } else if ("Project Member".equalsIgnoreCase(role)) {
                Node badge = createMemberBadge(member);
                memberTagsContainer.getChildren().add(badge);
            } else if ("Project Manager".equalsIgnoreCase(role)) {
                // Project Manager không hiển thị dạng Badge ở đây, nhưng vẫn được tính vào totalActiveMembers
            } else {
                System.out.println("CẢNH BÁO: Không nhận diện được Role để vẽ Badge -> [" + role + "]");
            }
        }

        // 5. Cập nhật con số đếm bằng TỔNG SỐ thành viên Active
        if (memberCountLabel != null) {
            memberCountLabel.setText(totalActiveMembers + " member(s)");
        }
    }

    // 3. Hàm tạo Badge (Cục Tên + Nút X)
    private Node createMemberBadge(ProjectMemberDTO member) {
        HBox badge = new HBox();
        badge.getStyleClass().add("member-badge");

        Label nameLabel = new Label(member.getUsername());
        nameLabel.getStyleClass().add("member-badge-text");

        Button removeBtn = new Button("x");
        removeBtn.getStyleClass().add("member-badge-remove-btn");

        // Sự kiện click nút X -> Xóa user (Sẽ làm ở luồng sau theo API DELETE)
        removeBtn.setOnAction(e -> {
            System.out.println("Sắp xóa member ID: " + member.getProjectMemberId());
            // Tạm để đây, bước sau làm API Xóa sẽ gọi viewModel.removeMember(...)
        });

        badge.getChildren().addAll(nameLabel, removeBtn);
        return badge;
    }

    private void setupSearchBindings() {
        // 1. Lắng nghe gõ phím trên Textfield để gọi API
        coManagerInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                viewModel.searchUsersForCoManager(newVal.trim());
            } else {
                coManagerDropdown.hide();
            }
        });

        memberInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                viewModel.searchUsersForMember(newVal.trim());
            } else {
                memberDropdown.hide();
            }
        });

        // 2. Hứng dữ liệu trả về và hiển thị Dropdown (Co-Manager)
        disposables.add(viewModel.getCoManagerSearchResults().subscribe(users -> {
            Platform.runLater(() -> {
                coManagerDropdown.getItems().clear();
                if (users == null || users.isEmpty()) {
                    coManagerDropdown.hide();
                    return;
                }
                for (UserDTO user : users) {
                    MenuItem item = new MenuItem(user.getUsername());
                    // TRUYỀN SỐ 2L CHO CO-MANAGER
                    item.setOnAction(e -> confirmAndInviteUser(user, 2L, "co-manager"));
                    coManagerDropdown.getItems().add(item);
                }
                // Nếu đang focus ở ô nhập liệu thì mới thả Dropdown ra
                if (coManagerInput.isFocused()) {
                    coManagerDropdown.show(coManagerInput, Side.BOTTOM, 0, 0);
                }
            });
        }));

        // 3. Hứng dữ liệu trả về và hiển thị Dropdown (Member)
        disposables.add(viewModel.getMemberSearchResults().subscribe(users -> {
            Platform.runLater(() -> {
                memberDropdown.getItems().clear();
                if (users == null || users.isEmpty()) {
                    memberDropdown.hide();
                    return;
                }
                for (UserDTO user : users) {
                    MenuItem item = new MenuItem(user.getUsername());
                    item.setOnAction(e -> confirmAndInviteUser(user, 3L, "member"));
                    memberDropdown.getItems().add(item);
                }
                if (memberInput.isFocused()) {
                    memberDropdown.show(memberInput, Side.BOTTOM, 0, 0);
                }
            });
        }));
    }

    // ==========================================
    // LOGIC KHI ẤN PHÍM ENTER
    // ==========================================
    // ==========================================
    // LOGIC KHI ẤN PHÍM ENTER (Hỗ trợ gõ Username hoặc Email)
    // ==========================================
    @FXML
    private void handleCoManagerSubmit(ActionEvent event) {
        String text = coManagerInput.getText().trim();

        // SỬA DÒNG NÀY: Gọi hàm mới tạo để lấy List
        List<UserDTO> currentResults = viewModel.getCurrentCoManagerList();

        if (currentResults != null && !text.isEmpty()) {
            currentResults.stream()
                    .filter(u -> text.equalsIgnoreCase(u.getUsername()) ||
                            (u.getEmail() != null && text.equalsIgnoreCase(u.getEmail())))
                    .findFirst()
                    // TRUYỀN SỐ 2L CHO CO-MANAGER
                    .ifPresent(u -> confirmAndInviteUser(u, 2L, "co-manager"));
        }
    }

    @FXML
    private void handleMemberSubmit(ActionEvent event) {
        String text = memberInput.getText().trim();

        // SỬA DÒNG NÀY: Gọi hàm mới tạo để lấy List
        List<UserDTO> currentResults = viewModel.getCurrentMemberList();

        if (currentResults != null && !text.isEmpty()) {
            currentResults.stream()
                    .filter(u -> text.equalsIgnoreCase(u.getUsername()) ||
                            (u.getEmail() != null && text.equalsIgnoreCase(u.getEmail())))
                    .findFirst()
                    .ifPresent(u -> confirmAndInviteUser(u, 3L, "member"));
        }
    }

    // ==========================================
    // ALERT XÁC NHẬN VÀ XỬ LÝ MỜI
    // ==========================================
    private void confirmAndInviteUser(UserDTO user, Long roleId, String roleName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Add " + roleName);
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to invite " + user.getUsername() + " to be " + roleName + "?");

        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(btnYes, btnNo);

        // Đặt thuộc tính "Nhấn mạnh" (Mặc định chọn) cho nút Yes
        Button yesBtn = (Button) alert.getDialogPane().lookupButton(btnYes);
        yesBtn.setDefaultButton(true);
        Button noBtn = (Button) alert.getDialogPane().lookupButton(btnNo);
        noBtn.setDefaultButton(false);

        alert.showAndWait().ifPresent(type -> {
            if (type == btnYes) {
                // Gửi request qua ViewModel (Lúc này roleId sẽ là 2 hoặc 3 tùy luồng)
                viewModel.inviteUser(user.getId(), roleId);

                // KIỂM TRA ROLE ID LÀ 2L ĐỂ ẨN Ô INPUT CO-MANAGER
                if (roleId == 2L) {
                    coManagerInput.clear();
                    coManagerInput.setVisible(false);
                    coManagerInput.setManaged(false);
                } else {
                    memberInput.clear();
                    memberInput.setVisible(false);
                    memberInput.setManaged(false);
                }
            }
        });
    }
}