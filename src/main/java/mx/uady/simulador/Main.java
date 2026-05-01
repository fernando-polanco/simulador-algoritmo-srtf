package mx.uady.simulador;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Nodo raíz de la escena: un panel centrado
        Label label = new Label("simulador SRTF - En construcción");
        StackPane root = new StackPane(label);

        // La Scene define el tamaño y contenido de la ventana
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("simulador algoritmo SRTF");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
