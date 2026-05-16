package mx.uady.simulador.models;

public class Process {
    private final String id;
    private final int arrivalTime;
    private final int burstTime;
    private int remainingBurstTime;
    private int completionTime;
    private int turnaroundTime;
    private double waitingTime;
    private int contextSwitchCount;
    private ProcessState state;

    private static final double CONTEXT_SWITCH_COST = 0.2;

    public Process(String id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.completionTime = -1; // sin asignar
        this.turnaroundTime = -1; // sin asignar
        this.waitingTime = -1; // sin asignar
        this.contextSwitchCount = 0;
        this.state = ProcessState.NEW;
    }

    public void decreaseRemainingBurstTime() {
        if (remainingBurstTime > 0) {
            remainingBurstTime--;
        }
    }

    public void decreaseRemainingBurstTime(int delta) {
        if (delta <= 0) {
            return;
        }
        remainingBurstTime = Math.max(0, remainingBurstTime - delta);
    }

    public void incrementContextSwitchCount() {
        contextSwitchCount++;
    }

    public void finalizeProcess(int completionTime) {
        this.remainingBurstTime = 0;
        this.completionTime = completionTime;
        calculateTimes();
        this.state = ProcessState.TERMINATED;
    }

    public boolean isFinished() {
        return remainingBurstTime == 0;
    }

    public void reset() {
        this.remainingBurstTime = this.burstTime;
        this.completionTime = -1;
        this.turnaroundTime = -1;
        this.waitingTime = -1;
        this.contextSwitchCount = 0;
        this.state = ProcessState.NEW;
    }

    private void calculateTimes() {
        this.turnaroundTime = completionTime - arrivalTime;
        this.waitingTime = turnaroundTime - burstTime + (CONTEXT_SWITCH_COST * contextSwitchCount);
    }

    // getters
    public String getId() {
        return id;
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
    public int getTurnaroundTime() {
        return turnaroundTime;
    }
    public double getWaitingTime() {
        return waitingTime;
    }
    public int getContextSwitchCount() {
        return contextSwitchCount;
    }
    public ProcessState getState() {
        return state;
    }

    // setters
    public void setState(ProcessState state) {
        this.state = state;
    }
}