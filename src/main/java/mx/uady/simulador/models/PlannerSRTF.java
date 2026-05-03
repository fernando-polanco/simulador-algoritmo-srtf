package mx.uady.simulador.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Contiene la lógica del algoritmo SRTF (Shortest Remaining Time First). Se encarga de gestionar el tiempo y los procesos. <p>
 * Es un algoritmo preemptive: en cada unidad de tiempo, la CPU se asigna al proceso con el menor tiempo restante de ráfaga.
 */
public class PlannerSRTF {
    private final static double CONTEXT_CHANGE_TIME = 0.2;
    private final List<Process> processes;
    private Process currentProcess;
    private int currentTime;

    /**
     * Constructor que crea un ArrayList vacío de procesos.
     */
    public PlannerSRTF() {
        processes = new ArrayList<>();
        currentProcess = null;
        currentTime = 0;
    }

    /**
     * Constructor que recibe una lista de procesos predefinida.
     * @param processes Lista de procesos.
     */
    public PlannerSRTF(List<Process> processes) {
        this.processes = processes;
        currentProcess = null;
        currentTime = 0;
    }

    public List<Process> getProcesses() {
        return processes;
    }
    public Process getCurrentProcess() {
        return currentProcess;
    }
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * Avanza el tiempo en una unidad, asignando la CPU al proceso con el menor tiempo restante de ráfaga.
     * Si el proceso actual cambia, se simula un tiempo de cambio de contexto.
     */
    public void advanceTime() {
        // Avanza el tiempo en una unidad
        this.currentTime++;
        // Si el proceso actual no es nulo, se decrementa su tiempo de ráfaga restante
        if (currentProcess != null) {
            currentProcess.decreaseRemainingBurst();
            if (currentProcess.getRemainingBurst() == 0) {
                currentProcess.updateState(ProcessState.FINISHED);
                currentProcess.calculateTurnaroundTime(currentTime);
                currentProcess = null; // Libera la CPU al terminar la ejecución del proceso actual
            }
        }

        // Filtra los procesos que ya han llegado el sistema y que no han terminado su ejecución por completo,
        // y selecciona al proceso con menor tiempo de ráfaga restante.
        Process nextProcess = processes.stream()
                .filter(p -> p.getArrivalTime() <= currentTime && p.getState() != ProcessState.FINISHED)
                .min(Comparator.comparingInt(Process::getRemainingBurst))
                .orElse(null);

        if (nextProcess != null) {
            // Si el proceso que va a entrar a la CPU es diferente al actual
            if (currentProcess != null && currentProcess != nextProcess && currentProcess.getState() != ProcessState.FINISHED) {
                currentProcess.updateState(ProcessState.WAITING); // El proceso actual pasa ha estado de espera
            }
            currentProcess = nextProcess; // Se asigna el proceso con menor tiempo de ráfaga restante a la CPU
            if (currentProcess.getState() != ProcessState.RUNNING) {
                currentProcess.updateState(ProcessState.RUNNING); // El proceso que entra a la CPU pasa ha estado de ejecución
            }
        }
    }

    /**
     * Verifica si todos los procesos han terminado su ejecución por completo.
     * @return {@code true} si todos los procesos han terminado su ejecución por completo, {@code false} en caso contrario.
     */
    public boolean isFinished() {
        // Evalúa si todos los procesos cumplen la condición de haber terminado su ejecución por completo.
        return processes.stream().allMatch(p -> p.getState() == ProcessState.FINISHED);
    }

    /**
     * Añade un proceso nuevo a la cola de procesos listos para ser atendidos por la CPU.
     * @param process Proceso a añadir a la cola de procesos listos.
     */
    public void addProcess(Process process) {
        processes.add(process);
    }


}
