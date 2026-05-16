module mx.uady.simulador {
    requires javafx.controls;
    requires javafx.fxml;

    exports mx.uady.simulador;
    exports mx.uady.simulador.models;

    opens mx.uady.simulador.controllers to javafx.fxml;
}
