package mx.uady.simulador.models;

/**
 * Representa los estados por los que pasa un proceso durante su ciclo de vida en el sistema operativo.
 */
public enum ProcessState {
    NOT_ARRIVED, // aún no ha llegado a la cola de procesos listos para ser atendidos
    WAITING,     // en la cola de procesos listos para ser atendidos
    RUNNING,     // actualmente en ejecución en la CPU
    FINISHED,    // ha terminado su ejecución
}
