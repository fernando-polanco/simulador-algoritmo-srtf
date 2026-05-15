package mx.uady.simulador.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import mx.uady.simulador.models.GanttEntry;
import mx.uady.simulador.models.Process;
import mx.uady.simulador.models.ProcessState;
import mx.uady.simulador.models.Scheduler;
import mx.uady.simulador.utils.ScaledTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainController {
    @FXML
    private TableView<Process> processTable;
    @FXML
    private TableColumn<Process, String> processColumn;
    @FXML
    private TableColumn<Process, String> arrivalColumn;
    @FXML
    private TableColumn<Process, String> burstColumn;
    @FXML
    private TableColumn<Process, String> remainingColumn;
    @FXML
    private TableColumn<Process, String> stateColumn;
    @FXML
    private Canvas ganttCanvas;
    @FXML
    private TextArea calculationArea;
    @FXML
    private Button stepButton;

    private final ObservableList<Process> processData = FXCollections.observableArrayList();
    private final Map<String, Color> ganttColors = new HashMap<>();
    private Scheduler scheduler;
    private int stepCount = 1;

    @FXML
    private void initialize() {
        configureColumns();
        configureColors();
        resetSimulation();
    }

    @FXML
    private void onStepButtonAction() {
        if (scheduler.isSimulationFinished()) {
            resetSimulation();
            return;
        }

        scheduler.executeEvent();
        processTable.refresh();
        renderGantt();

        if (scheduler.isSimulationFinished()) {
            stepButton.setText("Simulacion Finalizada");
            updateCalculationArea();
        } else {
            stepCount++;
            stepButton.setText("Paso " + stepCount);
        }
    }

    private void configureColumns() {
        processColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        arrivalColumn.setCellValueFactory(data -> new SimpleStringProperty(formatTime(data.getValue().getArrivalTime())));
        burstColumn.setCellValueFactory(data -> new SimpleStringProperty(formatTime(data.getValue().getBurstTime())));
        remainingColumn.setCellValueFactory(data -> new SimpleStringProperty(formatTime(data.getValue().getRemainingBurstTime())));
        stateColumn.setCellValueFactory(data -> new SimpleStringProperty(formatState(data.getValue().getState())));
        processTable.setItems(processData);
    }

    private void configureColors() {
        ganttColors.put("P1", Color.web("#8db4e2"));
        ganttColors.put("P2", Color.web("#f4b183"));
        ganttColors.put("P3", Color.web("#a9d18e"));
        ganttColors.put("P4", Color.web("#ffd966"));
        ganttColors.put("P5", Color.web("#c5b0d5"));
        ganttColors.put("IDLE", Color.web("#c9c9c9"));
        ganttColors.put("CC", Color.web("#d9d2e9"));
    }

    private void resetSimulation() {
        List<Process> processes = createInitialProcesses();
        processData.setAll(processes);
        scheduler = new Scheduler(processes);
        stepCount = 1;
        stepButton.setText("Paso 1");
        calculationArea.clear();
        renderGantt();
    }

    private List<Process> createInitialProcesses() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", ScaledTime.fromMilliseconds(0), ScaledTime.fromMilliseconds(8)));
        processes.add(new Process("P2", ScaledTime.fromMilliseconds(3), ScaledTime.fromMilliseconds(4)));
        processes.add(new Process("P3", ScaledTime.fromMilliseconds(6), ScaledTime.fromMilliseconds(2)));
        processes.add(new Process("P4", ScaledTime.fromMilliseconds(10), ScaledTime.fromMilliseconds(3)));
        processes.add(new Process("P5", ScaledTime.fromMilliseconds(15), ScaledTime.fromMilliseconds(6)));
        return processes;
    }

    private void renderGantt() {
        GraphicsContext gc = ganttCanvas.getGraphicsContext2D();
        double width = ganttCanvas.getWidth();
        double height = ganttCanvas.getHeight();

        gc.clearRect(0, 0, width, height);

        List<GanttEntry> entries = scheduler.getGanttEntries();
        int totalTime = Math.max(scheduler.getCurrentTime(), entries.stream()
                .mapToInt(GanttEntry::getEndTime)
                .max()
                .orElse(0));

        if (totalTime <= 0) {
            gc.setFill(Color.web("#444444"));
            gc.fillText("Sin ejecucion", 20, height / 2);
            return;
        }

        double padding = 20.0;
        double barTop = 40.0;
        double barHeight = 30.0;
        double usableWidth = width - padding * 2;

        for (GanttEntry entry : entries) {
            double startX = padding + (entry.getStartTime() / (double) totalTime) * usableWidth;
            double endX = padding + (entry.getEndTime() / (double) totalTime) * usableWidth;
            double rectWidth = Math.max(1.0, endX - startX);

            gc.setFill(resolveColor(entry.getId()));
            gc.fillRect(startX, barTop, rectWidth, barHeight);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(startX, barTop, rectWidth, barHeight);

            gc.setFill(Color.BLACK);
            gc.fillText(entry.getId(), startX + 4, barTop + 20);
            gc.fillText(formatTime(entry.getStartTime()), startX, barTop - 6);
        }

        GanttEntry last = entries.get(entries.size() - 1);
        double lastX = padding + (last.getEndTime() / (double) totalTime) * usableWidth;
        gc.setFill(Color.BLACK);
        gc.fillText(formatTime(last.getEndTime()), lastX, barTop - 6);
    }

    private void updateCalculationArea() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tiempos de espera por proceso (ms)\n");

        for (Process process : processData) {
            double waitMs = ScaledTime.toMilliseconds(process.calculateWaitingTime());
            builder.append(process.getName())
                    .append(": ")
                    .append(formatTimeValue(waitMs))
                    .append(" ms\n");
        }

        double averageWaitMs = scheduler.calculateAverageWaitingTime() / 10.0;
        double totalProcessingMs = ScaledTime.toMilliseconds(scheduler.getCurrentTime());
        double idlePercent = scheduler.calculateCpuIdlePercentage();

        builder.append("\nTiempo de espera promedio: ")
                .append(formatTimeValue(averageWaitMs))
                .append(" ms\n");
        builder.append("Tiempo total de procesamiento: ")
                .append(formatTimeValue(totalProcessingMs))
                .append(" ms\n");
        builder.append("Porcentaje del TTP que consume el TEP: ")
                .append(String.format(Locale.US, "%.2f%%", idlePercent))
                .append("\n");

        calculationArea.setText(builder.toString());
    }

    private Color resolveColor(String id) {
        return ganttColors.getOrDefault(id, Color.web("#9db9c7"));
    }

    private String formatState(ProcessState state) {
        return switch (state) {
            case NEW -> "NUEVO";
            case READY -> "LISTO";
            case RUNNING -> "EJECUTANDO";
            case TERMINATED -> "TERMINADO";
        };
    }

    private String formatTime(int scaledTime) {
        return formatTimeValue(ScaledTime.toMilliseconds(scaledTime));
    }

    private String formatTimeValue(double timeMs) {
        return String.format(Locale.US, "%.1f", timeMs);
    }
}

