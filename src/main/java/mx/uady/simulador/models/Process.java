package mx.uady.simulador.models;

public class Process {
    private final String name;
    private final int arrivalTime;
    private final int burstTime;
    private int remainingBurstTime;
    private int completionTime;
    private ProcessState state;

    public Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.completionTime = -1; // sin asignar
        this.state = ProcessState.NEW;
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
    public int getCompletionTime() {
        return completionTime;
    }
    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        switch (state) {
            case NEW -> {
                throw new IllegalStateException("Cambio invalido: " + this.state + " a " + state);
            }
            case READY -> {
                if (this.state != ProcessState.NEW && this.state != ProcessState.RUNNING) {
                    throw new IllegalStateException("Cambio invalido: " + this.state + " a " + state);
                }
            }
            case RUNNING -> {
                if (this.state != ProcessState.READY) {
                    throw new IllegalStateException("Cambio invalido: " + this.state + " a " + state);
                }
            }
            case TERMINATED -> {
                if (this.state != ProcessState.RUNNING) {
                    throw new IllegalStateException("Cambio invalido: " + this.state + " a " + state);
                }
            }
        }
        this.state = state;
    }

    /**
     * Decrementa en una unidad el tiempo de ráfaga restante del proceso.
     *
     * <p>Este metodo modela la ejecución de una unidad de CPU sobre el proceso
     * (por ejemplo 0.1 ms en la granularidad del simulador). No realiza
     * validaciones adicionales: quien lo invoca debe garantizar que el
     * proceso está en estado {@code RUNNING} cuando corresponda.</p>
     */
    public void decreaseRemainingBurstTime() {
        this.remainingBurstTime--;
    }

    /**
     * Calcula el tiempo total que el proceso ha estado en el sistema desde su llegada hasta su finalización.
     * @return un {@code int} del tiempo total en el sistema.
     */
    public int calculateTurnaroundTime() {
        return completionTime - arrivalTime;
    }

    /**
     * Calcula el tiempo que el proceso estuvo en la cola de procesos listos (tournaroundTime - burstTime).
     * @return un {@code int} del tiempo de espera del proceso.
     */
    public int calculateWaitingTime() {
        return calculateTurnaroundTime() - burstTime;
    }

    /**
     * Marca el proceso como completado.
     * <p>Establece el {@code completionTime} (momento del simulador en el que
     * el proceso finalizó) y realiza la transición de estado a
     * {@code TERMINATED} usando {@link #setState(ProcessState)}. El metodo
     * debe invocarse cuando el {@code remainingBurstTime} llega a 0 o menor.</p>
     *
     * @param completionTime instante de tiempo (misma unidad que usa el simulador)
     *                       en el que el proceso finaliza
     * @throws IllegalStateException si la transición de estado no es válida
     */
    public void markAsComplete(int completionTime) {
        this.completionTime = completionTime;
        setState(ProcessState.TERMINATED);
    }

    /**
     * Restablece el proceso a su estado inicial para volver a ejecutar la simulación.
     *
     * <p>Este metodo asigna {@code remainingBurstTime = burstTime}, pone
     * {@code completionTime = -1} (sin asignar) y fuerza el {@code state}
     * a {@code NEW}. Está pensado para ser invocado por el planificador
     * cuando se reinicia la simulación (por ejemplo {@code Scheduler.restart()}).</p>
     *
     * <p>IMPORTANTE: {@code reset()} asigna el estado {@code NEW} directamente,
     * saltándose las comprobaciones de {@link #setState(ProcessState)} porque
     * esa transición no está permitida desde fuera según las reglas de estado.</p>
     */
    public void reset() {
        this.remainingBurstTime = this.burstTime;
        this.completionTime = -1;
        this.state = ProcessState.NEW;
    }

    /**
     * Indica si el proceso ha completado su ráfaga de CPU.
     *
     * @return {@code true} si {@code remainingBurstTime} es menor o igual a 0,
     *         {@code false} en caso contrario
     */
    public boolean hasCompletedHisBurstTime() {
        return remainingBurstTime <= 0;
    }
}