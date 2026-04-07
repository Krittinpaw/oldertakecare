package com.elderlycare;

import com.elderlycare.ui.MainFrame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ElderlyCareApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.initializeDatabase();

        MainFrame mainFrame = new MainFrame();
        Scene scene = new Scene(mainFrame, 450, 850);
        
        // Load the beautiful high-contrast dark theme CSS
        String css = getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Elderly Care");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
