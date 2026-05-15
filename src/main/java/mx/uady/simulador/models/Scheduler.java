package mx.uady.simulador.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
    /**
     * Tiempo de cambio de contexto expresado en décimas de milisegundo (escala x10).
     * Equivale a 0.2 ms reales. El simulador trabaja internamente en esta escala
     * para evitar aritmética de punto flotante.
     */
    private final static int SWITCH_CONTEXT_TIME = 2; // escalado
    private final List<Process> processes;
    private final List<Process> readyQueue;
    private final List<GanttEntry> ganttEntries;
    private Process runningProcess;
    private int currentTime;
    private int startTime;
    private boolean simulationFinished;
    private boolean firstProcessHasRun;

    public Scheduler(List<Process> processes) {
        this.processes = processes;
        this.readyQueue = new ArrayList<>();
        this.ganttEntries = new ArrayList<>();
        this.runningProcess = null;
        this.currentTime = 0;
        this.startTime = 0;
        this.simulationFinished = false;
        this.firstProcessHasRun = false;
    }

    public List<GanttEntry> getGanttEntries() {
        return ganttEntries;
    }
    public int getCurrentTime() {
        return currentTime;
    }

    public void executeEvent(){
        if (isSimulationFinished()) {
            return;
        }

        // estado previo para detectar cambios
        Process prevRunning = runningProcess;
        int prevTerminated = (int) processes.stream()
                .filter(p -> p.getState() == ProcessState.TERMINATED)
                .count();

        // avanza hasta que ocurra un evento
        while (true) {
            executeOneUnit();

            if (runningProcess != prevRunning) {
                break;
            }

            int terminatedNow = (int) processes.stream()
                    .filter(p -> p.getState() == ProcessState.TERMINATED)
                    .count();

            if  (terminatedNow != prevTerminated) {
                break;
            }

            if (isSimulationFinished()) {
                break;
            }
        }

    }

    public double calculateAverageWaitingTime() {
        return processes.stream()
                .filter(p -> p.getState() == ProcessState.TERMINATED)
                .mapToInt(Process::calculateWaitingTime)
                .average()
                .orElse(0.0);
    }

    public double calculateCpuIdlePercentage() {
        if (currentTime <= 0) return 0.0;
        int idleTime = ganttEntries.stream()
                .filter(e -> "IDLE".equals(e.getId()) || "CC".equals(e.getId()))
                .mapToInt(GanttEntry::calculateDuration)
                .sum();
        return (idleTime / (double) currentTime) * 100.0;
    }

    /**
     * Indica si la simulación ha finalizado.
     * <p>La bandera {@code simulationFinished} se pone a {@code true} cuando el planificador
     * determina que no quedan procesos por ejecutar (todos los procesos tienen estado {@code TERMINATED}).</p>
     * El valor se reinicia a {@code false} al invocar {@link #restart()} para permitir
     * ejecutar la simulación nuevamente con las mismas instancias de {@code Process}.
     *
     * @return {@code true} si la simulación ha terminado, {@code false} en caso contrario
     */
    public boolean isSimulationFinished() {
        return simulationFinished;
    }

    /**
     * Reinicia la simulación reutilizando las mismas instancias de {@code Process} y limpiando
     * las estructuras internas del planificador. Esto permite ejecutar la simulación varias veces
     * sin necesidad de crear nuevas instancias de {@code Scheduler} o {@code Process}.
     */
    public void restart() {
        // regresa cada proceso a su estado inicial
        processes.forEach(Process::reset);
        // limpia estructuras internas
        readyQueue.clear();
        ganttEntries.clear();
        // reinicia el estado del planificador
        runningProcess = null;
        currentTime = 0;
        simulationFinished = false;
        firstProcessHasRun = false;
    }

    private void executeOneUnit() {
        if (isSimulationFinished()) {
            return;
        }

        // encolar llegadas en el tiempo actual
        enqueueArrivedProcess();
        //seleccionar candidato a ejecutar
        Process candidate = findShortestRemainingTimeProcess();

        // si no hay procesos corriendo
        if (runningProcess == null) {
            if (candidate == null) {
                // calcula el tiempo hasta la siguiente llegada
                // obtiene el siguiente proceso en llegar (arrivalTime > currentTime)
                int nextArrivalTime = processes.stream()
                        .filter(p -> p.getState() == ProcessState.NEW && p.getArrivalTime() > currentTime)
                        .mapToInt(Process::getArrivalTime)
                        .min()
                        .orElse(-1);

                // no hay más llegadas, termina la simulación
                if (nextArrivalTime == -1) {
                    checkAndSetSimulationFinished();
                    return;
                }

                // registra el tiempo total que la CPU estuvo IDLE
                GanttEntry newGanttEntry = new GanttEntry("IDLE", currentTime, nextArrivalTime);
                ganttEntries.add(newGanttEntry);

                // avanza el tiempo hasta la siguiente llegada
                currentTime = nextArrivalTime;
                // no encolamos aquí, la llegada será manejada en la siguiente invocación de executeOneUnit()
                return;
            }
            else {
                // sí hay un candidato a ejecutar
                readyQueue.remove(candidate);

                if (!firstProcessHasRun) {
                    // se asigna sin CC
                    runningProcess = candidate;
                    candidate.setState(ProcessState.RUNNING);
                    firstProcessHasRun = true;
                }
                else {
                    // se asignan con CC
                    GanttEntry ganttEntry = new GanttEntry("CC", currentTime, currentTime + SWITCH_CONTEXT_TIME);
                    ganttEntries.add(ganttEntry);
                    currentTime += SWITCH_CONTEXT_TIME;
                    runningProcess = candidate;
                    candidate.setState(ProcessState.RUNNING);
                }
                startTime = currentTime;
            }
        }
        else {
            // hay proceso en ejecución, comprobamos preemption
            if (candidate != null && candidate != runningProcess
                    && candidate.getRemainingBurstTime() < runningProcess.getRemainingBurstTime()) {
                // registrar segmento del proceso interrumpido
                GanttEntry newGanttEntry = new GanttEntry(runningProcess.getName(), startTime, currentTime);
                ganttEntries.add(newGanttEntry);

                // mover runningProcess a READY y añadirlo a la cola
                runningProcess.setState(ProcessState.READY);
                if (!readyQueue.contains(runningProcess)) {
                    readyQueue.add(runningProcess);
                }

                readyQueue.remove(candidate);
                // suma el CC al tiempo actual
                GanttEntry ganttEntry = new GanttEntry("CC", currentTime, currentTime + SWITCH_CONTEXT_TIME);
                ganttEntries.add(ganttEntry);
                currentTime += SWITCH_CONTEXT_TIME;
                runningProcess = candidate;
                candidate.setState(ProcessState.RUNNING);
                startTime = currentTime;
            }
        }

        // ejecutar el proceso en la CPU (si hay alguno)
        if (runningProcess != null) {
            currentTime++;
            runningProcess.decreaseRemainingBurstTime();

            // si terminó su ráfaga, marcarlo como completo y registrar su segmento en el diagrama de Gantt
            if (runningProcess.hasCompletedHisBurstTime()) {
                runningProcess.markAsComplete(currentTime);
                GanttEntry newGanttEntry = new GanttEntry(runningProcess.getName(), startTime, currentTime);
                ganttEntries.add(newGanttEntry);
                runningProcess = null;
            }
        }

        // comprobar si la simulación ha terminado
        checkAndSetSimulationFinished();
    }

    /**
     * Mueve todos los procesos recién llegados (arrivalTime == currentTime) a la cola de procesos
     * listos para ser atendidos por la CPU y cambia su estado a {@code READY}.
     */
    private void enqueueArrivedProcess() {
        processes.forEach(p -> {
                    if (p.getArrivalTime() <= currentTime && p.getState() == ProcessState.NEW) {
                        p.setState(ProcessState.READY);
                        // evita duplicados
                        if (!readyQueue.contains(p)) {
                            readyQueue.add(p);
                        }
                    }
                });
    }

    /**
     * Selecciona el proceso con menor tiempo de ráfaga restante (Shortest Remaining Time)
     * de la {@code readyQueue} sin modificar la colección.
     *
     * <p>La selección utiliza como criterio principal {@code remainingBurstTime} y,
     * en caso de empate, utiliza como criterio de desempate {@code arrivalTime}
     * (se prefiere el proceso que llegó antes).</p>
     *
     * @return la referencia al {@code Process} con menor {@code remainingBurstTime}
     *         (según los criterios descritos), o {@code null} si no hay procesos listos
     */
    private Process findShortestRemainingTimeProcess() {
        return readyQueue.stream()
                .min(Comparator.comparingInt(Process::getRemainingBurstTime)
                        .thenComparingInt(Process::getArrivalTime))
                .orElse(null);
    }

    private void checkAndSetSimulationFinished() {
        simulationFinished = processes.stream()
                .allMatch(p -> p.getState() == ProcessState.TERMINATED);
    }
}
