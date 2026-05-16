package mx.uady.simulador.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
    private final List<Process> processes;
    private final List<Process> readyQueue;
    private final List<GanttEntry> ganttChart;
    private final List<Integer> contextSwitches;
    private Process currentProcess;
    private int currentTime;
    private int startTime;
    private boolean firstProcessHasRun;

    public Scheduler(List<Process> processes) {
        this.processes = processes;
        this.readyQueue = new ArrayList<>();
        // guarda los segmentos del diagrama de Gantt
        this.ganttChart = new ArrayList<>();
        // guarda los tiempos donde hubo un cambio de contexto (CC)
        this.contextSwitches = new ArrayList<>();
        this.currentProcess = null;
        this.currentTime = 0;
        // ayuda a marcar cuando inicio un nuevo segmento del diagrama de Gantt
        this.startTime = -1;
        this.firstProcessHasRun = false;
    }

    public void advanceToNextEvent() {
        if (isSimulationFinished()) {
            return;
        }

    }

    public boolean isSimulationFinished() {
        return processes.stream().allMatch(Process::isFinished);
    }

    public void resetSimulation() {
        processes.forEach(Process::reset);
        readyQueue.clear();
        ganttChart.clear();
        contextSwitches.clear();
        currentProcess = null;
        currentTime = 0;
        startTime = -1;
        firstProcessHasRun = false;
    }

    private void executeOneUnit() {
        if (isSimulationFinished()) {
            return;
        }
        // actualiza la cola de procesos listos
        moveArrivedProcessesToReadyQueue();
        // seleccionar el candidato a ejecutar
        Process candidate = getShortestReminingTimeProcess();

        // caso 1) si no hay proceso ejecutándose en la CPU
        if (currentProcess == null) {
            // subcaso 1) no hay candidato a ejecutar
            if (candidate == null) {
                // avanza hasta el tiempo hasta el siguiente proceso en llegar
                int nextArrivalTime = processes.stream()
                        .filter(p -> p.getArrivalTime() > currentTime && p.getState() == ProcessState.NEW)
                        .mapToInt(Process::getArrivalTime)
                        .min()
                        .orElse(-1); // no hay más procesos por llegar

                if (nextArrivalTime == -1) {
                    return;
                }

                // registra el tiempo total que la CPU estuvo IDLE
                ganttChart.add(new GanttEntry("IDLE", currentTime, nextArrivalTime));
                // avanza el tiempo hasta la siguiente llegada
                currentTime = nextArrivalTime;
                // termina de ejecutar esta unidad de tiempo, la siguiente llamada al metodo se encargará de mover el
                // proceso a la cola de listos
                return;
            }
            // subcaso 2) si hay candidato a ejecutar, se asigna a la CPU
            else {
                // se elimina el proceso a ejecutar de la cola de listos para no ser tomado en cuenta a la hora de
                // buscar al siguiente candidato
                readyQueue.remove(candidate);

                if (!firstProcessHasRun) {
                    // se asigna sin CC
                    currentProcess = candidate;
                    currentProcess.setState(ProcessState.RUNNING);
                    firstProcessHasRun = true;
                } else {
                    // se asigna con CC
                    contextSwitches.add(currentTime);
                    currentProcess = candidate;
                    currentProcess.setState(ProcessState.RUNNING);
                }
                // guarda el tiempo de inicio del nuevo segmento de Gantt
                startTime = currentTime;
            }
        }
        // caso 2) hay un proceso en ejecución en la CPU
        else {
            // subcaso 1) hay preemption
            if (candidate != null && candidate != currentProcess
                    && candidate.getArrivalTime() < currentProcess.getArrivalTime()) {
                // registra el segmento del proceso que se interrumpió
                ganttChart.add(new GanttEntry(currentProcess.getId(), startTime, currentTime));
                // guarda el tiempo en que ocurrió el CC
                contextSwitches.add(currentTime);
                // cambiar currentProcess a READY y moverlo a la cola de listos
                currentProcess.setState(ProcessState.READY);
                if (!readyQueue.contains(candidate)) {
                    readyQueue.add(candidate);
                }
                // elimina candidate de la cola de listos
                readyQueue.remove(candidate);
                // asigna candidate a la CPU
                currentProcess = candidate;
                currentProcess.setState(ProcessState.RUNNING);
                // actualiza el tiempo de inicio del nuevo segmento
                startTime = currentTime;
            }

            // ejecuta el proceso en la CPU (si aplica)
            if (currentProcess != null) {
                // decrementa una unidad de tiempo (1 ms) al proceso ejecutándose en la CPU
                currentProcess.decreaseRemainingBurstTime();
                // aumenta una unidad de tiempo (1 ms) al tiempo actual
                currentTime++;

                if (currentProcess.isFinished()) {
                    int totalContextSwitches = contextSwitches.stream()
                            .filter(t -> t >= currentProcess.getArrivalTime() && t < currentTime)
                            .toArray().length;
                    currentProcess.finalizeProcess(currentTime, totalContextSwitches);
                    // registra el segmento
                    ganttChart.add(new GanttEntry(currentProcess.getId(), startTime, currentTime));
                    currentProcess = null;
                    currentTime = startTime;
                }
            }
        }
    }

    private void moveArrivedProcessesToReadyQueue() {
        processes.forEach(process -> {
            if (process.getArrivalTime() <= currentTime && process.getState() == ProcessState.NEW) {
                process.setState(ProcessState.READY);
                // si el proceso no se encuentra ya en la cola de listos
                if (!readyQueue.contains(process)) {
                    readyQueue.add(process);
                }
            }
        });
    }

    private Process getShortestReminingTimeProcess() {
        return readyQueue.stream()
                .min(Comparator.comparingInt(Process::getRemainingBurstTime)
                        .thenComparingInt(Process::getArrivalTime) // desempate 1
                        .thenComparing(Process::getId)) // desempate 2
                .orElse(null); // no hay procesos listos
    }

    // getters
    public List<Process> getProcesses() {
        return processes;
    }
    public List<Process> getReadyQueue() {
        return readyQueue;
    }
    public List<GanttEntry> getGanttChart() {
        return ganttChart;
    }
    public List<Integer> getContextSwitches() {
        return contextSwitches;
    }
    public Process getCurrentProcess() {
        return currentProcess;
    }
    public int getCurrentTime() {
        return currentTime;
    }
    public int getStartTime() {
        return startTime;
    }
}