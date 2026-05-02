package mx.uady.simulador.modelo;

/**
 * Representa un proceso en el simulador SRTF. Contiene los datos de entrada (llegada, ráfaga)
 * como los resultados calculados (espera, retorno).
 */
public class Process {
    private final String name;
    private final int arrivalTime;
    private final int burstTime;
    private int remainingTime;
    private int waitingTime;
    private int turnaroundTime;
    private int completionTime;

    public Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime; // Al iniciar un proceso, restante = ráfaga total
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
    public int getRemainingTime() {
        return remainingTime;
    }
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
    public int getWaitingTime() {
        return waitingTime;
    }
    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
    public int getTurnaroundTime() {
        return turnaroundTime;
    }
    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }
    public int getCompletionTime() {
        return completionTime;
    }
    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public void subtractUnitOfTime() {
        this.remainingTime--;
    }

    @Override
    public String toString() {
        return name + "[llegada= " + arrivalTime + ",rafaga=" + burstTime + "]";
    }
}