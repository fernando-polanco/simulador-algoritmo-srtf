package mx.uady.simulador.modelo;

public class EscalaTiempo {
    /// Escala en enteros, 1 cambio de contexto = 0.2 ms
    /// 1 unidad de tiempo = (0.2) * 5
    public static final int UNITS_PER_MS = 5;
    public static final int CONTEXT_SWITCH = 1;

    public static int msToUnits (int ms){
        return ms * UNITS_PER_MS;
    }
    public static double unitsToMs (int units){
        return units / 5.0;

    }
    public static String format(int units) {
        double ms = unitsToMs(units);
        if (ms == Math.floor(ms)) {
            return String.format("%.0f", ms);
        }
        return String.format("%.1f", ms);
    }
}
