package com.group4.projects_management_fe.core.ui;

import com.group4.projects_management_fe.core.interfaces.ErrorNotifier;
import com.group4.projects_management_fe.core.navigation.AppStageManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaFxErrorNotifier implements ErrorNotifier {
    private final AtomicBoolean isAlertShowing = new AtomicBoolean(false);

    @Override
    public void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message, null);
    }

    @Override
    public void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message, null);
    }

    @Override
    public void showCrashReport(String title, Throwable exception) {
        showAlert(Alert.AlertType.ERROR, "Sự cố hệ thống", title, exception);
    }

    @Override
    public void navigateToLogin() {
        runOnUIThread(() -> {
            AppStageManager.getInstance().navigateToLogin();
            System.out.println("Navigating to Login...");
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content, Throwable ex) {
        runOnUIThread(() -> {
            if (isAlertShowing.get()) return; // Nếu đang có 1 cái hiện rồi thì bỏ qua cái sau

            isAlertShowing.set(true);
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);

            // Nếu có Exception, tạo khu vực hiển thị chi tiết (Expandable)
            if (ex != null) {
                VBox dialogPaneContent = new VBox();
                TextArea textArea = new TextArea(getStackTrace(ex));
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);

                alert.getDialogPane().setExpandableContent(new VBox(new Label("Chi tiết lỗi:"), textArea));
            }

            alert.showAndWait();
            isAlertShowing.set(false);
        });
    }

    private String getStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    private void runOnUIThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
