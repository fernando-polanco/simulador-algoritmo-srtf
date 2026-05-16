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

    public boolean step() {
        return advanceToNextEvent();
    }

    private boolean advanceToNextEvent() {
        if (isSimulationFinished()) {
            return false;
        }

        while (!isSimulationFinished()) {
            moveArrivedProcessesToReadyQueue();

            if (currentProcess == null) {
                Process candidate = getShortestRemainingTimeProcess();
                if (candidate != null) {
                    assignProcessToCpu(candidate);
                    startTime = currentTime;
                    return true;
                }

                int nextArrivalTime = getNextArrivalTime();
                if (nextArrivalTime == -1) {
                    return false;
                }

                appendGanttSegment("IDLE", currentTime, nextArrivalTime);
                currentTime = nextArrivalTime;
                return true;
            }

            Process running = currentProcess;
            int remainingTime = running.getRemainingBurstTime();
            int nextArrivalTime = getNextArrivalTime();

            if (nextArrivalTime == -1 || nextArrivalTime >= currentTime + remainingTime) {
                int finishTime = currentTime + remainingTime;
                appendGanttSegment(running.getId(), startTime, finishTime);
                currentTime = finishTime;
                running.finalizeProcess(currentTime);
                currentProcess = null;
                return true;
            }

            int timeToNextArrival = nextArrivalTime - currentTime;
            if (timeToNextArrival > 0) {
                appendGanttSegment(running.getId(), startTime, nextArrivalTime);
                currentProcess.decreaseRemainingBurstTime(timeToNextArrival);
                currentTime = nextArrivalTime;
                startTime = nextArrivalTime;
            }

            moveArrivedProcessesToReadyQueue();
            Process candidate = getShortestRemainingTimeProcess();
            if (candidate != null && candidate != currentProcess
                    && candidate.getRemainingBurstTime() < currentProcess.getRemainingBurstTime()) {

                if (!currentProcess.isFinished()) {
                    currentProcess.setState(ProcessState.READY);
                    readyQueue.add(currentProcess);
                }

                assignProcessToCpu(candidate);
                startTime = currentTime;
                return true;
            }
        }

        return false;
    }

    private void assignProcessToCpu(Process process) {
        if (!firstProcessHasRun) {
            currentProcess = process;
            currentProcess.setState(ProcessState.RUNNING);
            firstProcessHasRun = true;
        }
        else {
            contextSwitches++;
            incrementProcessesContextSwitchCount();
            currentProcess = process;
            currentProcess.setState(ProcessState.RUNNING);
        }
        readyQueue.remove(process);
    }

    private void incrementProcessesContextSwitchCount() {
        readyQueue.forEach(Process::incrementContextSwitchCount);
    }

    private void appendGanttSegment(String id, int segmentStart, int segmentEnd) {
        if (segmentEnd <= segmentStart) {
            return;
        }
        if (!ganttChart.isEmpty()) {
            GanttEntry last = ganttChart.get(ganttChart.size() - 1);
            if (last.getId().equals(id) && last.getEndTime() == segmentStart) {
                ganttChart.set(ganttChart.size() - 1, new GanttEntry(id, last.getStartTime(), segmentEnd));
                return;
            }
        }
        ganttChart.add(new GanttEntry(id, segmentStart, segmentEnd));
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

    private Process getShortestRemainingTimeProcess() {
        return readyQueue.stream()
                .min(Comparator.comparingInt(Process::getRemainingBurstTime)
                        .thenComparingInt(Process::getArrivalTime)
                        .thenComparing(Process::getId))
                .orElse(null);
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