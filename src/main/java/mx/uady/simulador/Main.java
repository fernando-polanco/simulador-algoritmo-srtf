package mx.uady.simulador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/mx/uady/simulador/views/SchedulerView.fxml")));
        Scene scene = new Scene(root, 1100, 650);

        primaryStage.setTitle("Simulacion SRTF");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
