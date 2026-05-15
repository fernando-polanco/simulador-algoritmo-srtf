package mx.uady.simulador.models;

public record GanttEntry (String id, int startTime, int endTime) {
    public int calculateDuration() {
        return endTime - startTime;
    }
    public String getId() {
        return id;
    }
    public int getStartTime() {
        return startTime;
    }
    public int getEndTime() {
        return endTime;
    }
}
