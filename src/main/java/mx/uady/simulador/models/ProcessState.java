package mx.uady.simulador.models;

/**
 * Representa los estados por los que pasa un proceso durante su estancia en la CPU y la cola
 * de procesos listos para ser atendidos.
 */
public enum ProcessState {
    WAITING,
    RUNNING,
    FINISHED,
}
