//package com.group4.projects_management_fe;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class MainWindow extends Application {
//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("main-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//    }
//}

package com.group4.projects_management_fe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class MainWindow extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load LoginCard đầu tiên
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/group4/projects_management_fe/ui/components/auth/LoginCard.fxml")));

        // Dùng StackPane làm nền để căn giữa card
        StackPane layout = new StackPane(root);
        layout.setStyle("-fx-background-color: #ecf0f1;");

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setTitle("Project Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
