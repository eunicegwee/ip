package kira.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Kira using FXML.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
            stage.setScene(scene);

            // Create Kira backend with a ResponseUi so GUI can capture output
            kira.Kira kiraBackend = new kira.Kira("data/kira.txt", new kira.ResponseUi());
            KiraResponder responder = new KiraResponder(kiraBackend);

            // inject the responder into controller
            fxmlLoader.<MainWindow>getController().setKiraResponder(responder);

            stage.setTitle("Kira");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
