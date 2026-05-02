package mx.uady.simulador.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Contiene la lógica del algoritmo SRTF (Shortest Remaining Time First). Se encarga de gestionar el tiempo, los procesos y el historial del diagrama de Gantt. <p>
 * Es un algoritmo preemptive: en cada unidad de tiempo, la CPU se asigna al proceso con el menor tiempo restante de ráfaga.
 */

public class PlannerSRTF {
    public static final double CONTEXT_CHANGE_TIME = 0.2;
    private final List<Process> processes;
    private final List<String> ganttHistory;
    private Process currentProcess;
    private int currentTime;

    public PlannerSRTF(List<Process> processes) {
        this.processes = processes;
        this.ganttHistory = new ArrayList<>();
        this.currentProcess = null;
        this.currentTime = 0;
    }

    public List<String> getGanttHistory() {
        return ganttHistory;
    }
    public Process getCurrentProcess() {
        return currentProcess;
    }


}
