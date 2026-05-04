package mx.uady.simulador.models;

/**
 * Representa un proceso en memoria esperando ser atendido por la CPU.
 */
public class Process {
    private final String name;
    private final int arrivalTime;
    private final int burstTime;
    private double finishTime;
    private double remainingBurstTime;
    private ProcessState state;

    /**
     * Construye un proceso con su nombre, tiempo de llegada y tiempo de ráfaga. El tiempo de ráfaga restante se inicializa
     * al tiempo de ráfaga, el tiempo de finalización se inicializa a 0 y el estado se inicializa a {@code NOT_ARRIVED}.
     * @param name Nombre del proceso.
     * @param arrivalTime Tiempo de llegada a la cola de procesos listos para ser atendidos.
     * @param burstTime Tiempo de ráfaga en la CPU.
     */
    public Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.finishTime = 0;
        this.state = ProcessState.NOT_ARRIVED;
    }

    // getters
    public String getName() {
        return name;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public int getBurstTime() {
        return burstTime;
    }
    public double getRemainingBurstTime() {
        return remainingBurstTime;
    }
    public double getFinishTime() {
        return finishTime;
    }
    public ProcessState getState() {
        return state;
    }

    // setters
    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }
    public void setState(ProcessState state) {
        this.state = state;
    }

    /**
     * Calcula el tiempo total que el proceso ha estado en el sistema, desde su llegada hasta su finalización.
     * @return El tiempo de turnaround del proceso.
     */
    public double calculateTurnaroundTime() {
        return finishTime - arrivalTime;
    }

    /**
     * Calcula el tiempo que el proceso ha estado esperando en la cola de procesos listos sin ser atendido por la CPU.
     * @return Tiempo de espera del proceso.
     */
    public double calculateWaitingTime() {
        return calculateTurnaroundTime() - burstTime;
    }

}
