package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/main.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Borsa / Crypto Hesap Takip Uygulaması");
            primaryStage.show();
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("app_icon.png")));
         // Eğer Main.java'da:
            scene.getStylesheets().add(getClass().getResource("application-light.css").toExternalForm());
            // Eğer istersen dark ile de başlatabilirsin, kodu ona göre ayarla.
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}