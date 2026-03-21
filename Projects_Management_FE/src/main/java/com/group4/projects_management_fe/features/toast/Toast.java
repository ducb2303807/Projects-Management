package com.group4.projects_management_fe.features.toast;

import com.group4.common.dto.SseNotificationDTO;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;

public class Toast {

    private static final ObservableList<Stage> activeToasts = FXCollections.observableArrayList();

    private static final double MARGIN = 20.0;
    private static final double GAP = 10.0; // Khoảng cách giữa các Toast
    private static final int DISPLAY_TIME = 4000; // 4 giây

    public static void showToast(Stage ownerStage, SseNotificationDTO dto) {
        Platform.runLater(() -> {
            if (ownerStage == null || dto == null) return;

            Stage toastStage = new Stage();
            toastStage.initOwner(ownerStage);
            toastStage.initStyle(StageStyle.TRANSPARENT);
            toastStage.setAlwaysOnTop(true);

            // --- NỘI DUNG ---
            VBox textContainer = new VBox(2);
            Label title = new Label(dto.getTitle());
            // Màu chữ Tím đậm (Indigo) để hợp với nút Login
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #4338ca; -fx-font-size: 14px; -fx-font-family: 'Segoe UI', sans-serif;");

            Label message = new Label(dto.getMessage());
            // Màu chữ xám thanh lịch
            message.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif;");
            message.setWrapText(true);
            message.setMaxWidth(260);
            textContainer.getChildren().addAll(title, message);

            // Nút đóng gọn gàng hơn
            Label closeBtn = new Label("✕");
            closeBtn.setStyle("-fx-text-fill: #cbd5e1; -fx-cursor: hand; -fx-font-size: 12px;");
            closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-text-fill: #6366f1; -fx-cursor: hand;"));
            closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-text-fill: #cbd5e1;"));
            closeBtn.setOnMouseClicked(e -> closeAndRearrange(toastStage));

            // --- BỐ CỤC CHÍNH ---
            HBox root = new HBox(12);
            root.setPadding(new Insets(15, 18, 15, 18));

            // Style mới: Nền trắng, viền tím nhẹ, bo góc 12px (giống ô Input của bạn)
            root.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-color: #e2e8f0; " + // Viền xám rất nhạt
                            "-fx-border-radius: 12px; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-left-color: #6366f1; " + // Điểm nhấn viền trái màu Tím
                            "-fx-border-left-width: 4px;" // Làm viền trái dày lên một chút cho chuyên nghiệp
            );

            root.getChildren().addAll(textContainer, closeBtn);
            HBox.setHgrow(textContainer, Priority.ALWAYS);

            // Đổ bóng màu Tím cực nhạt thay vì đen
            root.setEffect(new DropShadow(20, 0, 10, Color.rgb(99, 102, 241, 0.15)));

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            toastStage.setScene(scene);

            // --- HIỂN THỊ ---
            toastStage.setOpacity(0);
            toastStage.show();
            root.applyCss();
            root.layout();

            activeToasts.add(toastStage);
            repositionAllToasts(ownerStage);

            // Animation mượt mà
            toastStage.setOpacity(1);
            root.setOpacity(0);
            root.setTranslateX(30); // Trượt từ phải sang nhẹ nhàng

            FadeTransition ft = new FadeTransition(Duration.millis(400), root);
            ft.setToValue(1);
            TranslateTransition tt = new TranslateTransition(Duration.millis(400), root);
            tt.setToX(0);

            new ParallelTransition(ft, tt).play();

            PauseTransition delay = new PauseTransition(Duration.millis(4000));
            delay.setOnFinished(e -> closeAndRearrange(toastStage));
            delay.play();
        });
    }

    private static void closeAndRearrange(Stage stageToClose) {
        if (!activeToasts.contains(stageToClose)) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stageToClose.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            Stage owner = (Stage) stageToClose.getOwner();
            activeToasts.remove(stageToClose);
            stageToClose.close();
            // Sau khi xóa, sắp xếp lại các cái còn lại
            repositionAllToasts(owner);
        });
        fadeOut.play();
    }

    private static void repositionAllToasts(Stage ownerStage) {
        double currentY = ownerStage.getY() + ownerStage.getHeight() - MARGIN;

        for (Stage stage : new ArrayList<>(activeToasts)) {
            double toastHeight = stage.getHeight();
            if (toastHeight == 0) toastHeight = 70; // Giá trị dự phòng nếu stage chưa render xong

            double targetX = ownerStage.getX() + ownerStage.getWidth() - stage.getWidth() - MARGIN;
            double targetY = currentY - toastHeight;

            // Di chuyển mượt mà đến vị trí mới
            animateStagePosition(stage, targetX, targetY);

            currentY -= (toastHeight + GAP);
        }
    }

    private static void animateStagePosition(Stage stage, double targetX, double targetY) {
        stage.setX(targetX);

        DoubleProperty yProxy = new SimpleDoubleProperty(stage.getY());

        javafx.beans.value.ChangeListener<Number> yListener = (obs, oldVal, newVal) -> {
            stage.setY(newVal.doubleValue());
        };

        yProxy.addListener(yListener);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(yProxy, targetY, Interpolator.EASE_BOTH))
        );

        timeline.setOnFinished(e -> yProxy.removeListener(yListener));

        timeline.play();
    }
}