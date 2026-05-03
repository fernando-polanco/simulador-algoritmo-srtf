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
    private Process cpuProcess;
    private double currentTime;

    /**
     * Constructor que crea un ArrayList vacío de procesos.
     */
    public PlannerSRTF() {
        processes = new ArrayList<>();
        cpuProcess = null;
        currentTime = 0;
    }

    /**
     * Constructor que recibe una lista de procesos predefinida.
     * @param processes Lista de procesos.
     */
    public PlannerSRTF(List<Process> processes) {
        this.processes = processes;
        cpuProcess = null;
        currentTime = 0;
    }

    public List<Process> getProcesses() {
        return processes;
    }
    public Process getCpuProcess() {
        return cpuProcess;
    }
    public double getCurrentTime() {
        return currentTime;
    }

    /**
     * Avanza el tiempo en una unidad, asignando la CPU al proceso con el menor tiempo restante de ráfaga.
     * Si el proceso actual cambia, se simula un tiempo de cambio de contexto.
     */
    public void advanceTime() {
        // avanza el tiempo en una unidad
        this.currentTime++;
        // guarda referencia al proceso que actualmente ocupa a la CPU
        Process previousProcess = cpuProcess;
        // actualiza el proceso actualmente en la CPU (decrementa ráfaga, finaliza si corresponde)
        updateCpuProcess(cpuProcess);
        // selecciona y asigna el siguiente proceso a la CPU si corresponde
        Process nextProcess = selectNextProcess();
        // agrega el tiempo de cambio de contexto si el proceso actual cambia a uno diferente (y el actual no ha terminado)
        if (previousProcess != null && nextProcess != null && previousProcess != nextProcess) {
            this.currentTime += CONTEXT_CHANGE_TIME; // simula el tiempo de cambio de contexto al cambiar de proceso en la CPU
        }
        // asigna el nuevo proceso a la CPU
        assignCpuProcess(nextProcess);
    }

    /**
     * Verifica si todos los procesos han terminado su ejecución por completo.
     * @return {@code true} si todos los procesos han terminado su ejecución por completo, {@code false} en caso contrario.
     */
    public boolean isFinished() {
        // evalúa si todos los procesos cumplen la condición de haber terminado su ejecución por completo.
        return processes.stream().allMatch(p -> p.getState() == ProcessState.FINISHED);
    }

    /**
     * Añade un proceso nuevo a la cola de procesos listos para ser atendidos por la CPU.
     * @param process Proceso a añadir a la cola de procesos listos.
     */
    public void addProcess(Process process) {
        processes.add(process);
    }

    /**
     * Actualiza el proceso actualmente asignado a la CPU:
     * <ul>
     *   <li>Decrementa su tiempo de ráfaga restante.</li>
     *   <li>Si termina su ráfaga, actualiza su estado a FINISHED, calcula turnaroundTime y libera la CPU.</li>
     * </ul>
     * @param process El proceso actualmente en la CPU (puede ser null).
     */
    private void updateCpuProcess(Process process) {
        if (process != null) {
            process.decreaseRemainingBurst();
            if (process.getRemainingBurst() == 0) {
                process.updateState(ProcessState.FINISHED);
                process.calculateTurnaroundTime(currentTime);
                cpuProcess = null; // libera la CPU al terminar la ejecución del proceso actual
            }
        }
    }

    /**
     * Filtra la lista de procesos listos para ser asignados a la CPU y selecciona el proceso con el menor tiempo restante de ráfaga.
     * @return El proceso con menor tiempo de ráfaga restante, o {@code null} si no hay procesos listos para ser asignados a la CPU.
     */
    private Process selectNextProcess() {
        // selecciona el proceso listo con menor tiempo de ráfaga restante
        return processes.stream()
                .filter(p -> p.getArrivalTime() <= currentTime && p.getState() != ProcessState.FINISHED)
                .min(Comparator.comparingInt(Process::getRemainingBurst))
                .orElse(null);
    }


    /**
     * Asigna el proceso seleccionado a la CPU y actualiza los estados:
     * <ul>
     *   <li>Si el proceso que va a entrar es diferente al actual y el actual no ha terminado, lo pone en WAITING.</li>
     *   <li>Asigna el nuevo proceso a la CPU y lo pone en RUNNING si no lo está.</li>
     * </ul>
     * @param process El proceso seleccionado para entrar a la CPU (puede ser null).
     */
    private void assignCpuProcess(Process process) {
        if (process != null) {
            // se añade una capa de seguridad al verificar si el proceso no está terminado antes de cambiar su estado
            // para evitar cambiar el estado de un proceso terminado
            if (cpuProcess != null && cpuProcess != process && cpuProcess.getState() != ProcessState.FINISHED) {
                cpuProcess.updateState(ProcessState.WAITING); // el proceso actual pasa ha estado de espera
            }
            cpuProcess = process; // asigna el proceso con menor tiempo de ráfaga restante a la CPU
            if (cpuProcess.getState() != ProcessState.RUNNING) {
                cpuProcess.updateState(ProcessState.RUNNING); // el proceso que entra a la CPU pasa ha estado de ejecución
            }
        }
    }




}
