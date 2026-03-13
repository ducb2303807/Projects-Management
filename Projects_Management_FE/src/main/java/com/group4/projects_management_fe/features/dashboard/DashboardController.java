package com.group4.projects_management_fe.features.dashboard;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.util.Duration;

public class DashboardController {

    @FXML
    private VBox calendarBox;

    @FXML
    private ListView<HBox> taskListView;

    /* -------- POPUP -------- */

    @FXML
    private Pane overlay;

    @FXML
    private HBox createProjectPopup;

    @FXML
    private void openPopup() {
        overlay.setVisible(true);
        createProjectPopup.setVisible(true);

        // Hiệu ứng nhẹ nhàng
        FadeTransition fadeOverlay = new FadeTransition(Duration.millis(200), overlay);
        fadeOverlay.setFromValue(0);
        fadeOverlay.setToValue(1);
        overlay.setOpacity(0);

        ScaleTransition scalePopup = new ScaleTransition(Duration.millis(250), createProjectPopup);
        scalePopup.setFromX(0.7);
        scalePopup.setFromY(0.7);
        scalePopup.setToX(1);
        scalePopup.setToY(1);

        ParallelTransition combined = new ParallelTransition(fadeOverlay, scalePopup);
        combined.play();
    }

    // Hàm này để ngăn việc click vào bên trong popup bị hiểu lầm là click ra ngoài (đóng popup)
    @FXML
    private void consumeMouseEvent(javafx.scene.input.MouseEvent event) {
        event.consume();
    }

    @FXML
    private void closePopup() {

        ScaleTransition scale = new ScaleTransition(Duration.millis(180), createProjectPopup);
        scale.setToX(0.85);
        scale.setToY(0.85);

        FadeTransition fade = new FadeTransition(Duration.millis(180), overlay);
        fade.setToValue(0);

        ParallelTransition animation = new ParallelTransition(scale, fade);

        animation.setOnFinished(e -> {

            overlay.setVisible(false);
            createProjectPopup.setVisible(false);   // thiếu dòng này

            overlay.setOpacity(1);

            createProjectPopup.setScaleX(1);
            createProjectPopup.setScaleY(1);
        });

        animation.play();
    }

    /**
     * Thêm hàm này để khi click vào vùng trắng của Popup
     * nó KHÔNG bị tắt (ngăn sự kiện truyền lên Overlay)
     */
    @FXML
    private void handlePopupClick(javafx.scene.input.MouseEvent event) {
        event.consume();
    }

    /* -------- INIT -------- */

    @FXML
    public void initialize() {

        /* ---------------- CALENDAR ---------------- */

        DatePicker datePicker = new DatePicker();

        DatePickerSkin skin = new DatePickerSkin(datePicker);
        Node calendar = skin.getPopupContent();

        calendarBox.getChildren().add(calendar);

        /* ---------------- TODAY TASK LIST ---------------- */

        taskListView.getItems().add(createTask(
                "Create a user flow of social application design",
                "Completed"
        ));

        taskListView.getItems().add(createTask(
                "Create a user flow of social application design",
                "In review"
        ));

        taskListView.getItems().add(createTask(
                "Landing page design for Fintech project of singapore",
                "In review"
        ));

        taskListView.getItems().add(createTask(
                "Interactive prototype for app screens of deltamime project",
                "On going"
        ));

        taskListView.getItems().add(createTask(
                "Interactive prototype for app screens of deltamime project",
                "Completed"
        ));
    }

    private HBox createTask(String title, String status) {

        Label icon = new Label("✓");
        icon.getStyleClass().add("task-check");

        Label text = new Label(title);
        text.getStyleClass().add("task-text");

        Label badge = new Label(status);
        badge.getStyleClass().add("task-badge");

        switch (status) {
            case "Completed" -> badge.getStyleClass().add("badge-completed");
            case "In review" -> badge.getStyleClass().add("badge-review");
            case "On going" -> badge.getStyleClass().add("badge-going");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, icon, text, spacer, badge);
        row.setAlignment(Pos.CENTER_LEFT);

        return row;
    }
}