package mx.uady.simulador.models;

/**
 * Representa un proceso en memoria listo para ser asignado a la CPU.
 */
public class Process {
    private final String name;
    private final int arrivalTime;
    private final int burstTime;
    private int remainingBurst;
    private int turnaroundTime;
    private ProcessState state;

    public Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurst = burstTime; // Un proceso recién creado tiene un tiempo de ráfaga restante es igual al tiempo de ráfaga total.
        this.turnaroundTime = 0;
        this.state = ProcessState.WAITING;
    }

    public String getName() {
        return name;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public int getBurstTime() {
        return burstTime;
    }
    public int getRemainingBurst() {
        return remainingBurst;
    }
    public int getTurnaroundTime() {
        return turnaroundTime;
    }
    public ProcessState getState() {
        return state;
    }

    /**
     * Decrementa en una unidad de tiempo al tiempo de ráfaga restante del proceso en la CPU, siempre que este sea mayor a cero.
     */
    public void decreaseRemainingBurst() {
        if (remainingBurst > 0) {
            remainingBurst--;
        }
    }

    /**
     * Calcula las unidades de tiempo total que transcurre desde que un proceso llega al sistema hasta que termina su ejecución por completo.
     * @param finishTime El tiempo en el que el proceso termina su ejecución por completo.
     */
    public void calculateTurnaroundTime(int finishTime) {
        this.turnaroundTime = finishTime - this.arrivalTime;
    }

    /**
     * Actualiza el estado del proceso.
     * @param state El nuevo estado del proceso ({@code WAITING}, {@code RUNNING}, {@code FINISHED}).
     */
    public void updateState(ProcessState state) {
        this.state = state;
    }
}
