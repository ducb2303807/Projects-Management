package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.ProjectResponseDTO;
import com.group4.projects_management_fe.core.navigation.AppStageManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProjectController implements Initializable {

    @FXML private HBox recentCardsContainer;
    @FXML private FlowPane mainCardsContainer;

    @FXML private TextField searchInput;
    @FXML private Button sortBtn;
    @FXML private ComboBox<String> sortCriteriaComboBox;

    private final ProjectViewModel viewModel = new ProjectViewModel();

    private boolean isSortAscending = true;
    private String currentSortCriteria = "Name";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);

    // ==========================================
    // LOGIC LƯU TRỮ RECENT PROJECTS VÀ MAIN PROJECTS
    // ==========================================

    // Dùng LinkedList static để giữ lại danh sách khi người dùng chuyển trang
    private static final LinkedList<ProjectResponseDTO> recentProjectsList = new LinkedList<>();
    private final List<ProjectItem> allProjectItems = new ArrayList<>();

    private static class ProjectItem {
        Node cardNode;
        String title;
        LocalDateTime date;
        String status;

        public ProjectItem(Node cardNode, String title, LocalDateTime date, String status) {
            this.cardNode = cardNode;
            this.title = title != null ? title : "";
            this.date = date != null ? date : LocalDateTime.MIN;
            this.status = status != null ? status : "";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup ComboBox Sắp xếp
        if (sortCriteriaComboBox != null) {
            sortCriteriaComboBox.getItems().addAll("Name", "Date", "Status");
            sortCriteriaComboBox.setValue("Name");
            sortCriteriaComboBox.setOnAction(e -> {
                currentSortCriteria = sortCriteriaComboBox.getValue();
                updateFlowPaneDisplay();
            });
        }

        // 2. Lắng nghe ô Search
        if (searchInput != null) {
            searchInput.textProperty().addListener((obs, oldV, newV) -> updateFlowPaneDisplay());
        }

        // 3. Render danh sách Recent (Lần đầu mở trang)
        renderRecentProjects();

        // 4. Lắng nghe dữ liệu API từ ViewModel
        viewModel.projectsObservable().subscribe(projects -> {
            Platform.runLater(() -> renderProjectsToUI(projects));
        }, Throwable::printStackTrace);

        // 5. Bắt đầu gọi API
        viewModel.fetchMyProjects();
    }

    // ==========================================
    // RECENT PROJECTS (DỰ ÁN XEM GẦN ĐÂY)
    // ==========================================

    private void handleProjectClicked(ProjectResponseDTO clickedProject) {
        // 1. Xóa nếu project này đã tồn tại trong list (để đưa nó lên lại đầu tiên)
        recentProjectsList.removeIf(p -> p.getId().equals(clickedProject.getId()));

        // 2. Thêm vào vị trí đầu tiên
        recentProjectsList.addFirst(clickedProject);

        // 3. Giữ tối đa 6 projects, nếu dư thì cắt bỏ cái cũ nhất ở cuối danh sách
        if (recentProjectsList.size() > 6) {
            recentProjectsList.removeLast();
        }

        // 4. Vẽ lại danh sách Recent
        renderRecentProjects();
    }

    private void renderRecentProjects() {
        recentCardsContainer.getChildren().clear();

        try {
            for (ProjectResponseDTO dto : recentProjectsList) {
                // Tải file FXML của Recent Card
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/RecentProjectCard.fxml"));
                Node recentCard = loader.load();

                // 1. Lấy controller của thẻ Recent vừa load
                RecentProjectCardController ctrl = loader.getController();

                // 2. Gắn tên dự án vào thẻ
                if (ctrl != null) {
                    ctrl.bindData(String.valueOf(dto.getId()), dto.getProjectName());
                }

                recentCardsContainer.getChildren().add(recentCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // MAIN PROJECTS (DANH SÁCH CHÍNH)
    // ==========================================

    private void renderProjectsToUI(List<ProjectResponseDTO> apiProjects) {
        allProjectItems.clear();

        try {
            for (ProjectResponseDTO dto : apiProjects) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/ProjectCard.fxml"));
                Node projectCard = loader.load();
                ProjectCardController cardCtrl = loader.getController();

                LocalDateTime projectDate = dto.getStartDate();
                String dateStr = projectDate != null ? projectDate.format(dateFormatter) : "N/A";
                String creatorName = dto.getUserCreatedFullName() != null ? dto.getUserCreatedFullName() : dto.getUserCreatedUsername();

                // Đổ data vào giao diện Card
                cardCtrl.bindData(String.valueOf(dto.getId()), dto.getProjectName(), dto.getStatusName(), creatorName, dateStr);

                // TRUYỀN CALLBACK ĐỂ BẮT SỰ KIỆN SAU KHI ĐÓNG POPUP DETAILS
                cardCtrl.setOnProjectUpdatedCallback(() -> {
                    if (viewModel != null) {
                        viewModel.fetchMyProjects(); // Load lại data từ API
                    }
                });

                // GẮN SỰ KIỆN CLICK CHO THẺ CARD CHÍNH ĐỂ THÊM VÀO RECENT
                projectCard.setOnMouseClicked(event -> {
                    handleProjectClicked(dto);
                });

                // Lưu lại nội bộ để hỗ trợ Sort và Search
                allProjectItems.add(new ProjectItem(projectCard, dto.getProjectName(), projectDate, dto.getStatusName()));
            }

            updateFlowPaneDisplay();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // TÌM KIẾM VÀ SẮP XẾP
    // ==========================================

    @FXML
    public void handleSortClick(ActionEvent event) {
        if (sortCriteriaComboBox != null) {
            boolean isCurrentlyVisible = sortCriteriaComboBox.isVisible();

            // Đảo ngược trạng thái bật/tắt của ComboBox
            sortCriteriaComboBox.setVisible(!isCurrentlyVisible);
            sortCriteriaComboBox.setManaged(!isCurrentlyVisible);

            if (!isCurrentlyVisible) {
                // Nếu vừa được bật lên -> tự động xổ danh sách ra luôn cho tiện (UX tốt hơn)
                sortCriteriaComboBox.show();
            } else {
                // (Tùy chọn) Nếu ẩn đi thì có muốn reset về mặc định không?
                // Nếu không thì cứ để nguyên dòng này trống.
            }
        }
    }

    private void updateFlowPaneDisplay() {
        String keyword = searchInput != null ? searchInput.getText().toLowerCase().trim() : "";

        // 1. Filter
        List<ProjectItem> filteredList = allProjectItems.stream()
                .filter(item -> item.title.toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        // 2. Sort
        Comparator<ProjectItem> comparator;
        switch (currentSortCriteria) {
            case "Date":
                comparator = Comparator.comparing(item -> item.date, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "Status":
                comparator = Comparator.comparing(item -> item.status, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Name":
            default:
                comparator = Comparator.comparing(item -> item.title, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        if (!isSortAscending) {
            comparator = comparator.reversed();
        }
        filteredList.sort(comparator);

        // 3. Render
        mainCardsContainer.getChildren().clear();
        for (ProjectItem item : filteredList) {
            mainCardsContainer.getChildren().add(item.cardNode);
        }
    }


    // ==========================================
    // XỬ LÝ NÚT TÌM KIẾM (SEARCH)
    // ==========================================
    @FXML
    public void handleSearchToggle(ActionEvent event) {
        if (searchInput != null) {
            boolean isCurrentlyVisible = searchInput.isVisible();

            // Đảo ngược trạng thái bật/tắt của ô nhập chữ
            searchInput.setVisible(!isCurrentlyVisible);
            searchInput.setManaged(!isCurrentlyVisible);

            if (!isCurrentlyVisible) {
                // Nếu vừa được bật lên -> Đưa con trỏ chuột vào luôn để người dùng gõ ngay
                searchInput.requestFocus();
            } else {
                // Nếu bị tắt đi -> Xóa sạch text bên trong.
                // Do mình đã cài Listener ở hàm initialize, việc clear() này sẽ tự động báo cho UI vẽ lại danh sách gốc.
                searchInput.clear();
            }
        }
    }

    // ==========================================
    // POPUP TẠO DỰ ÁN MỚI
    // ==========================================

    @FXML
    public void openNewProjectPopup(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewProjectForm.fxml"));
            Parent root = fxmlLoader.load();
            Stage popupStage = new Stage();
            popupStage.setTitle("New Project");
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            Stage mainStage = AppStageManager.getInstance().getStage();
            if (mainStage != null) {
                popupStage.initOwner(mainStage);
            }
            popupStage.showAndWait();
            if (viewModel != null) {
                viewModel.fetchMyProjects();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}