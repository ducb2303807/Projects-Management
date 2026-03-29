package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.TaskHistoryDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskHistoryController {

    @FXML private VBox       taskListVBox;
    @FXML private ScrollPane historyScrollPane;

    private Stage           popupStage;

    // ── Setter cho Stage ──────────────────────────────────────────────────────
    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    // ── Nhận danh sách lịch sử ────────────────────────────────────────────────
    public void setHistories(List<TaskHistoryDTO> histories) {
        taskListVBox.getChildren().clear();

        if (histories == null || histories.isEmpty()) {
            Label empty = new Label("No history found.");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 10 0;");
            taskListVBox.getChildren().add(empty);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");

        for (TaskHistoryDTO item : histories) {
            VBox row = new VBox(3);
            row.setStyle(
                    "-fx-background-color: #F7F7FB;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 10 14;" +
                            "-fx-border-color: #E8E4F3;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-width: 1;"
            );

            // Dòng 1: "FullName has edited ColumnName"
            String fullName   = (item.getChangedByFullName() != null
                    && !item.getChangedByFullName().isBlank())
                    ? item.getChangedByFullName() : "Unknown";
            String columnName = item.getColumnName() != null
                    ? item.getColumnName() : "unknown field";

            Label actionLabel = new Label(fullName + " has edited " + columnName);
            actionLabel.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #2D2D2D;"
            );
            actionLabel.setWrapText(true);

            // Dòng 2: thời gian
            String timeText = item.getChangedAt() != null
                    ? item.getChangedAt().format(formatter) : "Unknown time";

            Label timeLabel = new Label(timeText);
            timeLabel.setStyle(
                    "-fx-font-size: 11px;" +
                            "-fx-text-fill: #386dfd;"
            );

            row.getChildren().addAll(actionLabel, timeLabel);
            taskListVBox.getChildren().add(row);
        }
    }

    // ── Close ─────────────────────────────────────────────────────────────────
    @FXML
    private void handleClose(ActionEvent event) {
        if (popupStage != null) {
            popupStage.close();
        }
    }
}