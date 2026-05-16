package mx.uady.simulador.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.ScrollPane;
import javafx.beans.property.SimpleStringProperty;

import mx.uady.simulador.models.GanttEntry;
import mx.uady.simulador.models.Process;
import mx.uady.simulador.models.Scheduler;

import java.util.List;
import java.util.Map;

public class SchedulerController {
    @FXML
    private TableView<Process> processTable;
    @FXML
    private TableColumn<Process, String> idColumn;
    @FXML
    private TableColumn<Process, String> arrivalColumn;
    @FXML
    private TableColumn<Process, String> burstColumn;
    @FXML
    private TableColumn<Process, String> remainingColumn;
    @FXML
    private TableColumn<Process, String> stateColumn;
    @FXML
    private TableColumn<Process, String> contextSwitchColumn;
    @FXML
    private TableColumn<Process, String> completionColumn;
    @FXML
    private TableColumn<Process, String> turnaroundColumn;
    @FXML
    private TableColumn<Process, String> waitingColumn;

    @FXML
    private HBox ganttContainer;
    @FXML
    private ScrollPane ganttScroll;

    @FXML
    private Label totalWaitingLabel;
    @FXML
    private Label avgWaitingLabel;
    @FXML
    private Label totalProcessingLabel;
    @FXML
    private Label idlePercentLabel;

    @FXML
    private Button stepButton;
    @FXML
    private Button resetButton;

    private final ObservableList<Process> processItems = FXCollections.observableArrayList();
    private Scheduler scheduler;
    private int stepNumber = 1;

    private static final double CONTEXT_SWITCH_COST = 0.2;
    private static final Map<String, String> PROCESS_COLORS = Map.of(
            "P1", "#2F80ED",
            "P2", "#27AE60",
            "P3", "#F2994A",
            "P4", "#9B51E0",
            "P5", "#EB5757",
            "IDLE", "#BDBDBD"
    );

    @FXML
    public void initialize() {
        processItems.setAll(createDefaultProcesses());
        scheduler = new Scheduler(processItems);

        processTable.setItems(processItems);
        configureColumns();
        updateGanttChart();
        updateCalculations();
        updateStepButtonLabel();

        ganttScroll.setFitToHeight(true);
    }

    @FXML
    private void handleStep() {
        if (scheduler.isSimulationFinished()) {
            stepButton.setText("Simulacion Finalizada");
            stepButton.setDisable(true);
            return;
        }

        boolean progressed = scheduler.step();
        if (progressed) {
            stepNumber++;
        }

        processTable.refresh();
        updateGanttChart();
        updateCalculations();
        updateStepButtonLabel();
    }

    @FXML
    private void handleReset() {
        scheduler.resetSimulation();
        stepNumber = 1;
        stepButton.setDisable(false);
        processTable.refresh();
        updateGanttChart();
        updateCalculations();
        updateStepButtonLabel();
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        arrivalColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getArrivalTime())));
        burstColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBurstTime())));
        remainingColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRemainingBurstTime())));
        stateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getState().name()));
        contextSwitchColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getContextSwitchCount())));
        completionColumn.setCellValueFactory(data -> new SimpleStringProperty(formatUnsetInt(data.getValue().getCompletionTime())));
        turnaroundColumn.setCellValueFactory(data -> new SimpleStringProperty(formatUnsetInt(data.getValue().getTurnaroundTime())));
        waitingColumn.setCellValueFactory(data -> new SimpleStringProperty(formatUnsetDouble(data.getValue().getWaitingTime())));
    }

    private void updateGanttChart() {
        ganttContainer.getChildren().clear();
        for (GanttEntry entry : scheduler.getGanttChart()) {
            Label segment = new Label(entry.getId() + "\n" + entry.getStartTime() + "-" + entry.getEndTime());
            int width = Math.max(40, entry.calculateDuration() * 30);
            segment.setMinWidth(width);
            segment.setPrefWidth(width);
            segment.setTextAlignment(TextAlignment.CENTER);
            segment.setWrapText(true);
            String color = PROCESS_COLORS.getOrDefault(entry.getId(), "#4F4F4F");
            String textColor = entry.getId().equals("IDLE") ? "#000000" : "#FFFFFF";
            segment.setStyle("-fx-background-color: " + color + "; -fx-border-color: #333333; -fx-padding: 6; -fx-text-fill: " + textColor + ";");
            ganttContainer.getChildren().add(segment);
        }
    }

    private void updateCalculations() {
        if (!scheduler.isSimulationFinished()) {
            totalWaitingLabel.setText("-");
            avgWaitingLabel.setText("-");
            totalProcessingLabel.setText("-");
            idlePercentLabel.setText("-");
            return;
        }

        double totalWaiting = scheduler.getProcesses().stream()
                .mapToDouble(Process::getWaitingTime)
                .sum();
        double avgWaiting = totalWaiting / scheduler.getProcesses().size();
        double totalProcessing = scheduler.getCurrentTime() + (scheduler.getContextSwitches() * CONTEXT_SWITCH_COST);
        double idlePercent = totalProcessing == 0 ? 0 : (avgWaiting / totalProcessing) * 100.0;

        totalWaitingLabel.setText(String.format("%.2f ms", totalWaiting));
        avgWaitingLabel.setText(String.format("%.2f ms", avgWaiting));
        totalProcessingLabel.setText(String.format("%.2f ms", totalProcessing));
        idlePercentLabel.setText(String.format("%.2f %%", idlePercent));
    }

    private void updateStepButtonLabel() {
        if (scheduler.isSimulationFinished()) {
            stepButton.setText("Simulacion Finalizada");
            return;
        }
        stepButton.setText("Paso " + stepNumber);
    }

    private List<Process> createDefaultProcesses() {
        return List.of(
                new Process("P1", 0, 8),
                new Process("P2", 3, 4),
                new Process("P3", 6, 2),
                new Process("P4", 10, 3),
                new Process("P5", 15, 6)
        );
    }

    private String formatUnsetInt(int value) {
        return value < 0 ? "-" : String.valueOf(value);
    }

    private String formatUnsetDouble(double value) {
        return value < 0 ? "-" : String.format("%.2f", value);
    }
}
