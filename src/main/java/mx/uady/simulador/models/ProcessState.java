package mx.uady.simulador.models;

/**
 * Representa los estados por los que pasa un proceso durante su ciclo de vida en el sistema operativo.
 */
public enum ProcessState {
    // recién creado
    NEW,
    // esperando en la cola de procesos listos para ejecutarse
    READY,
    // ejecutándose en la CPU
    RUNNING,
    // terminó de ejecutar su tiempo de ráfaga
    TERMINATED,
}
