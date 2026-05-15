package mx.uady.simulador.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import mx.uady.simulador.modelo.EscalaTiempo;
import mx.uady.simulador.modelo.Proceso;

import mx.uady.simulador.modelo.SegmentoDeGantt;
import mx.uady.simulador.modelo.SimuladorSRTF;

import javax.print.DocFlavor;
import java.util.List;
import java.util.Random;

public class SimulacionSRTFController {
    @FXML
    private TableView<Proceso> tablaProcesos;
    @FXML
    private TableColumn<Proceso, String> colProcesoID;
    @FXML
    private TableColumn<Proceso, String> colTiempoLlegada;
    @FXML
    private TableColumn<Proceso, String> colTiempoRafaga;

    @FXML
    private HBox hboxGantt;

    @FXML
    private HBox hboxTiempo;

    @FXML
    private TextArea txtCalculos;

    @FXML
    private Button btnPaso;

    private SimuladorSRTF simulador;

    /// Etiquetas especiales para cambio de contexto e IDLE
    private static final String ETIQUETA_CC  = "CC";
    private static final String ETIQUETA_IDLE = "IDLE";

    /// Estilos de la hoja css. Cada proceso posee un color distintivo
    /// 0 escalable pero resuelve el trabajo
    private static final String[] CLASES_PROCESO = {
            "gantt-p1",
            "gantt-p2",
            "gantt-p3",
            "gantt-p4",
            "gantt-p5",
    };

    /// TextBlock para el area de calculo. Se ve mejor y es más facil de modificar que un StringBuilder
    private static final String textoCalculo = """
            Cálculo de los tiempos. El cambio de contexto se realiza en 0.2 milisegundos.
                            a) Tiempo de espera de cada proceso: P1= | P2= | P3= | P4= | P5= |
                            b) Tiempo de espera promedio de todos los procesos (TEP): 
                            c) Tiempo total de procesamiento de todos los procesos (TTP):  
                            d) Porcentaje del TTP que consume el TEP: ; """;


    @FXML
    public void initialize (){
        cargarTabla();
        iniciar(cargarProcesos());

    }

    public List<Proceso> cargarProcesos (){
        return List.of(
                new Proceso("P1", 0, 8),
                new Proceso("P2", 3, 4),
                new Proceso("P3", 6, 2),
                new Proceso("P4", 10, 3),
                new Proceso("P5", 15, 6)
        );
    }

    public void cargarTabla (){
        colProcesoID.setCellValueFactory(
                data ->
                        new SimpleStringProperty(data.getValue().getId()));
        colTiempoLlegada.setCellValueFactory(
                data ->
                        new SimpleStringProperty(EscalaTiempo.formato(data.getValue().getLlegada())));
        colTiempoRafaga.setCellValueFactory(
                data ->
                        new SimpleStringProperty(EscalaTiempo.formato(data.getValue().getRafaga())));
    }

    public void iniciar (List<Proceso> procesos){

        simulador = new SimuladorSRTF(procesos);
        tablaProcesos.setItems(FXCollections.observableArrayList(procesos));
        hboxGantt.getChildren().clear();
        hboxTiempo.getChildren().clear();
        txtCalculos.setText(textoCalculo);
        btnPaso.setText("Paso 1");
    }

    public void siguientePaso (){
        if (simulador.isSimulacionFinalizada()){
            iniciar(cargarProcesos());
            return;
        }
        simulador.avanzarPaso();
        dibujarGantt();
        dibujarTiempos();
        tablaProcesos.refresh();
        if (simulador.isSimulacionFinalizada()){
            btnPaso.setText("Reiniciar simulación. ");
            mostrarCalculosFinales();

        }
        else {
            btnPaso.setText("Paso "+ simulador.getPaso());
        }

    }
    public void dibujarGantt (){
        /// En cada iteración, se recalcula el diagrama gantt
        hboxGantt.getChildren().clear();
        /// Se obtienen todos los sgementos a cargar
        for (SegmentoDeGantt segmento: simulador.getSegmentos()){
            Label bloque = new Label(segmento.getEtiqueta());
            /// El ancho de cada bloque es proporcional a su tiempo de duración
            double ancho= getAncho(segmento);
            bloque.setMinWidth(ancho);
            bloque.setPrefWidth(ancho);
            bloque.setMinHeight(45);
            bloque.setAlignment(Pos.CENTER);
            /// Se usa la baase generica de los bloques + el color de cada proceso espcifico
            bloque.getStyleClass().addAll("gantt-block",resolverClase(segmento));
            hboxGantt.getChildren().add(bloque);

        }
    }

    public void dibujarTiempos (){
        /// En cada iteración, se recalculan los tiempos
        hboxTiempo.getChildren().clear();
        for (SegmentoDeGantt segmento : simulador.getSegmentos()){
            Label tiempo= new Label(EscalaTiempo.formato(segmento.getInicio()));
            double ancho= getAncho(segmento);
            tiempo.setMinWidth(ancho);
            tiempo.setPrefWidth(ancho);
            tiempo.setAlignment(Pos.CENTER_LEFT);
            hboxTiempo.getChildren().add(tiempo);
        }
        /// Etiqueta de tiempo final tiene un estilo propio
        if (!simulador.getSegmentos().isEmpty()){
            SegmentoDeGantt ultimo = simulador.getSegmentos().
                    get(simulador.getSegmentos().size()-1);
            Label fin = new Label(EscalaTiempo.formato(ultimo.getFin()));
            fin.getStyleClass().add("time-label");
            hboxTiempo.getChildren().add(fin);
        }

    }
    /// Por cada unidad de tiempo se obtiene un ancho más grande
    public double getAncho (SegmentoDeGantt segmento){
        return Math.max(segmento.duracion()*12,30);
    }

    /// Formato de reporte enorme
    public void mostrarCalculosFinales (){
        StringBuilder formatoTexto = new StringBuilder();
        double sumaEspera=0;
        formatoTexto.append("Cálculo de los tiempos. El cambio de contexto se " +
                "realiza en 0.2 milisegundos. \n\n");
        formatoTexto.append("a) Tiempo de espera de cada proceso: \n");
        for (Proceso proceso : simulador.getProcesos()){
            double espera = EscalaTiempo.unitsToMs(proceso.tiempoDeEspera());
            sumaEspera+=espera;
            formatoTexto.append(" ")
                    .append(proceso.getId())
                    .append("= ")
                    .append(String.format("%.1f",espera))
                    .append(" ms\n\n");
        }

        /// Obtención de TTP, TEP Y Porcentaje usando formulas
        double TEP = sumaEspera / simulador.getProcesos().size();
        double TTP = EscalaTiempo.unitsToMs(simulador.getTiempoActual());
        double porcentajeFinal = (TEP / TTP) * 100;

        formatoTexto.append(String.format("b) Tiempo de espera promedio de los procesos (TEP): " +
                "%.2f\n",TEP));
        formatoTexto.append(String.format("c) Tiempo total de procesamiento de todos los procesos (TTP): " +
                "%.2f\n",TTP));
        formatoTexto.append(String.format("d) Porcentaje del TTP que consume el TEP: " +
                "%.2f",porcentajeFinal));
        formatoTexto.append("%");
        txtCalculos.setText(formatoTexto.toString());
    }

    /// AÑADIDO FINAL. Ponerle estilos a la UI

    private String resolverClase (SegmentoDeGantt segmento){
        String etiqueta = segmento.getEtiqueta().trim().toUpperCase();
        if (etiqueta.contains(ETIQUETA_CC)) return "gantt-cc";
        if (etiqueta.contains(ETIQUETA_IDLE)) return "gantt-idle";
        return resolverClaseProceso(segmento.getEtiqueta().trim());


    }
    private String resolverClaseProceso (String idProceso){
        try {
            /// Reemplaza todo lo que no sea un digito por un espacio en blanco
            String digitos = idProceso.replaceAll("[^0-9]", "");
            if (!digitos.isEmpty()){
                /// Convertirmos el string a entero y restamos 1 porque es un arreglo
                int indice = Integer.parseInt(digitos)-1;
                /// Se comprueba que esté en rango
                if (indice>=0 && indice <= CLASES_PROCESO.length -1){
                    return CLASES_PROCESO[indice];

                }
            }
        } catch (NumberFormatException e){
            System.out.println("Error al resolver indices" + e);
        }
        /// si falla todo se devuelve el primer color
        return CLASES_PROCESO[0];
    }


    }

