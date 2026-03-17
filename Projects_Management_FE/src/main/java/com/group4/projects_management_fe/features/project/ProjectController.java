package com.group4.projects_management_fe.features.project;

import com.group4.projects_management_fe.core.navigation.AppStageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProjectController implements Initializable {

    @FXML private HBox recentCardsContainer;
    @FXML private FlowPane mainCardsContainer;

    // UI Elements cho Search & Sort
    @FXML private TextField searchInput;
    @FXML private Button searchBtn;
    @FXML private Button sortBtn;

    // --- CẤU TRÚC LƯU TRỮ CARD ---
    // Class nội bộ dùng để map giữa Giao diện (Node) và Dữ liệu (Title)
    private static class ProjectItem {
        String title;
        Node cardNode;

        public ProjectItem(String title, Node cardNode) {
            this.title = title;
            this.cardNode = cardNode;
        }
    }

    // Danh sách gốc chứa TẤT CẢ các project đã load
    private final List<ProjectItem> allProjectItems = new ArrayList<>();

    // Trạng thái sắp xếp (true = A-Z, false = Z-A)
    private boolean isSortAscending = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRecentProjects();
        loadMainProjects();
        setupSearchListener();
    }

    private void setupSearchListener() {
        // Lắng nghe từng chữ người dùng gõ vào ô tìm kiếm
        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFlowPaneDisplay();
        });
    }

    // ==========================================
    // LOGIC LOAD DỮ LIỆU (MOCK DATA)
    // ==========================================

    private void loadRecentProjects() {
        try {
            for (int i = 0; i < 6; i++) {
                // Sửa lại đường dẫn nạp FXML cho đúng với cấu trúc thư mục của project bạn
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/RecentProjectCard.fxml"));
                Node recentCard = loader.load();
                recentCardsContainer.getChildren().add(recentCard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainProjects() {
        allProjectItems.clear();
        mainCardsContainer.getChildren().clear();

        // Tạo danh sách tên dự án giả lập để test Sort & Search
        String[] mockTitles = {
                "Website Redesign",
                "Mobile App Dev",
                "Alpha Testing Phase",
                "SEO Optimization",
                "Marketing Campaign"
        };

        try {
            for (String title : mockTitles) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group4/projects_management_fe/features/project/ProjectCard.fxml"));
                Node projectCard = loader.load();

                // Nếu ProjectCardController của bạn đã có hàm bindData thì gọi ở đây để gán tên lên UI
                // ProjectCardController cardCtrl = loader.getController();
                // cardCtrl.bindData("1", title, "On going", "Hunny", "Feb 2026");

                // Lưu vào danh sách gốc
                allProjectItems.add(new ProjectItem(title, projectCard));
            }

            // Hiển thị ra màn hình lần đầu tiên
            updateFlowPaneDisplay();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // LOGIC SEARCH VÀ SORT
    // ==========================================

    @FXML
    private void handleSearchToggle(ActionEvent event) {
        boolean isVisible = searchInput.isVisible();

        searchInput.setVisible(!isVisible);
        searchInput.setManaged(!isVisible);

        if (!isVisible) {
            searchInput.requestFocus(); // Tự động focus để gõ
        } else {
            searchInput.clear(); // Tắt search thì xóa text -> Tự động reset list
        }
    }

    @FXML
    private void handleSortClick(ActionEvent event) {
        isSortAscending = !isSortAscending; // Đảo chiều Sort
        updateFlowPaneDisplay(); // Render lại
    }

    /**
     * Hàm cốt lõi: Lọc (Search), Sắp xếp (Sort) và Vẽ lại lên FlowPane
     */
    private void updateFlowPaneDisplay() {
        String keyword = searchInput.getText().toLowerCase().trim();

        // 1. Lọc theo tên (Search)
        List<ProjectItem> filteredList = allProjectItems.stream()
                .filter(item -> item.title.toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        // 2. Sắp xếp (Sort)
        if (isSortAscending) {
            filteredList.sort(Comparator.comparing(item -> item.title, String.CASE_INSENSITIVE_ORDER));
        } else {
            filteredList.sort(Comparator.comparing((ProjectItem item) -> item.title, String.CASE_INSENSITIVE_ORDER).reversed());
        }

        // 3. Xóa các card cũ trong FlowPane và add lại các card đã lọc/sắp xếp
        mainCardsContainer.getChildren().clear();
        for (ProjectItem item : filteredList) {
            mainCardsContainer.getChildren().add(item.cardNode);
        }
    }

    // ==========================================
    // POPUP NEW PROJECT
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

