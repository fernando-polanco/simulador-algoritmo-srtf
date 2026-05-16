package mx.uady.simulador.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
    private final List<Process> processes;
    private final List<Process> readyQueue;
    private final List<GanttEntry> ganttChart;
    private int contextSwitches;
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
        this.contextSwitches = 0;
        this.currentProcess = null;
        this.currentTime = 0;
        // ayuda a marcar cuando inicio un nuevo segmento del diagrama de Gantt
        this.startTime = -1;
        this.firstProcessHasRun = false;
    }

    public boolean isSimulationFinished() {
        return processes.stream().allMatch(Process::isFinished);
    }

    public void resetSimulation() {
        processes.forEach(Process::reset);
        readyQueue.clear();
        ganttChart.clear();
        contextSwitches = 0;
        currentProcess = null;
        currentTime = 0;
        startTime = -1;
        firstProcessHasRun = false;
    }

    private void advanceToNextEvent() {
        if (isSimulationFinished()) {
            return;
        }

        while (!isSimulationFinished()) {
            // actualiza la cola de procesos listos
            moveArrivedProcessesToReadyQueue();

            // caso 1) si no hay proceso ejecutándose en la CPU
            if (currentProcess == null) {
                // seleccionar el candidato a ejecutar
                Process candidate = getShortestReminingTimeProcess();
                if (candidate != null) {
                    // asigna el proceso a la CPI
                    assignProcessToCpu(candidate);
                    startTime = currentTime;
                    // evento: entrada a la CPU, salimos del bule
                    return;
                }
                else {
                    // no hay procesos listos, avanzar hasta la próxima llegada
                    int nextArrivalTime = getNextArrivalTime();
                    if (nextArrivalTime == -1) {
                        // no hay más procesos listos
                        return;
                    }
                    // registra IDLE
                    ganttChart.add(new GanttEntry("IDLE", currentTime, nextArrivalTime));
                    currentTime = nextArrivalTime;
                    // el siguiente ciclo moverá los procesos que llegan
                    continue;
                }
            }

            // hay un proceso en la CPU
            Process running = currentProcess;
            int remainingTime = running.getRemainingBurstTime();
            int nextArrivalTime = getNextArrivalTime();

            // si no hay mas llegadas o la próxima llegada es después de que el proceso termine
            if (nextArrivalTime == -1 || nextArrivalTime >= currentTime + remainingTime) {
                // el proceso actual termina sin interrupciones
                // avanza el tiempo hasta su finalización
                int finishTime = currentTime + remainingTime;
                // registra el segmento en el diagrama de Gantt
                ganttChart.add(new GanttEntry(running.getId(), startTime, finishTime));
                // actualiza el tiempo actual
                currentTime = finishTime;
                // finaliza el proceso
                currentProcess.finalizeProcess(currentTime);
                currentProcess.setState(ProcessState.TERMINATED);
                currentProcess = null;

                // continúa al siguiente ciclo para asignar proceso a la CPU y parar en el siguiente evento
                continue;
            }

            // hay una llegada antes de que termine el proceso actual
            int timeToNextArrival = nextArrivalTime - currentTime;
            if (timeToNextArrival > 0) {
                // registra el segmento
                ganttChart.add(new GanttEntry(running.getId(), startTime, nextArrivalTime));
                // reduce el tiempo restante del proceso
                for (int i = 0; i < timeToNextArrival; i++) {
                    currentProcess.decreaseRemainingBurstTime();
                }
                currentTime = nextArrivalTime;
                startTime = nextArrivalTime;
            }

            moveArrivedProcessesToReadyQueue();
            Process candidate = getShortestReminingTimeProcess();
            if (candidate != null && candidate != currentProcess
                    && candidate.getRemainingBurstTime() < currentProcess.getRemainingBurstTime()) {

                if (!currentProcess.isFinished()) {
                    currentProcess.setState(ProcessState.READY);
                    readyQueue.add(currentProcess);
                }

                assignProcessToCpu(candidate);
                startTime = currentTime;
                // evento: entra un nuevo proceso
                return;
            }
            // no hubo preemption, el proceso actual continuo
            // el bucle continúa para procesar el siguiente evento (llegada o finalización)
        }
    }

    private void assignProcessToCpu(Process process) {
        if (!firstProcessHasRun) {
            currentProcess = process;
            currentProcess.setState(ProcessState.RUNNING);
            firstProcessHasRun = true;
        }
        else {
            contextSwitches++;
            incrementProcesesContextSwitchCount();
            currentProcess = process;
            currentProcess.setState(ProcessState.RUNNING);
        }
        readyQueue.remove(process);
    }

    private void incrementProcesesContextSwitchCount() {
        readyQueue.forEach(Process::incrementContextSwitchCount);
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

    private int getNextArrivalTime() {
        return processes.stream()
                .filter(p -> p.getArrivalTime() > currentTime && p.getState() == ProcessState.NEW)
                .mapToInt(Process::getArrivalTime)
                .min()
                .orElse(-1); // no hay más procesos por llegar
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
    public int getContextSwitches() {
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