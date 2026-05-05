package mx.uady.simulador.models;


/**
 * Representa un nuevo proceso creado en memoria.
 * <p>
 * Todos los valores de tiempo (arrivalTime, burstTime, remainingBurstTime, finishTime) se manejan en escala x10 para
 * evitar problemas de precisión con números decimales.
 * </p>
 */
public class Process {
    private final String name;
    private final int arrivalTime;
    private final int burstTime;
    private int remainingBurstTime;
    private int finishTime;
    private ProcessState state;

    /**
     * Construye un nuevo proceso.
     * @param name nombre del proceso..
     * @param arrivalTime tiempo de llegada (en escala x10).
     * @param burstTime tiempo de ráfaga (en escala x10).
     */
    public Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = this.burstTime; // inicialmente, el tiempo restante es igual al tiempo de ráfaga total
        this.finishTime = -1; // inicialmente, el tiempo de finalización es desconocido
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
    public int getRemainingBurstTime() {
        return remainingBurstTime;
    }
    public int getFinishTime() {
        return finishTime;
    }
    public ProcessState getState() {
        return state;
    }

    // setters
    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * Cambia el estado del proceso, asegurando que los cambios de estado sean válidos según el ciclo de vida típico de un proceso.
     * @param newState nuevo estado del proceso.
     * @throws IllegalStateException si el cambio de estado no es válido.
     */
    public void setState(ProcessState newState) {
        if (this.state == ProcessState.FINISHED) {
            throw new IllegalStateException("No se puede cambiar el estado de un proceso que ya ha terminado.");
        }
        switch (newState) {
            case WAITING -> {
                if (this.state != ProcessState.NOT_ARRIVED && this.state != ProcessState.RUNNING) {
                    throw new IllegalStateException("Cambio invalido de: " + this.state + " a: " + newState);
                }
            }
            case RUNNING -> {
                if (this.state != ProcessState.WAITING) {
                    throw new IllegalStateException("Cambio invalido de: " + this.state  + " a: " + newState);
                }
            }
            case FINISHED -> {
                if (this.state != ProcessState.RUNNING) {
                    throw new IllegalStateException("Cambio invalido de: " + this.state  + " a: " + newState);
                }
            }
        }
        this.state = newState;
    }

    /**
     * Calcula el tiempo total que el proceso ha estado en el sistema desde su llegada hasta su finalización,
     * incluyendo el tiempo de espera y el tiempo de ejecución.
     * @return tiempo de total.
     */
    public double calculateTurnaroundTime() {
        return (finishTime - arrivalTime) / 10.0;
    }

    /**
     * Calcula el tiempo total que el proceso estuvo en estado de espera.
     * @return tiempo de espera.
     */
    public double calculateWaitingTime() {
        return (finishTime - arrivalTime - burstTime) / 10.0;
    }
}