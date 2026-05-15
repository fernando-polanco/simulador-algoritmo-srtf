package mx.uady.simulador.modelo;

/// Para evitar errores de precisión en operaciones, se trabaja con escala de tiempo
public class EscalaTiempo {
    /// Escala en enteros, 1 cambio de contexto = 0.2 ms
    /// 1 unidad de tiempo = (0.2) * 5
    public static final int UNITS_PER_MS = 5;
    public static final int CONTEXT_SWITCH = 1;

    /// Convierte ms a unidades relativas
    public static int msToUnits (int ms){
        return ms * UNITS_PER_MS;
    }
    /// Convierte las unidades de tiempo relativas a decimal Ms
    public static double unitsToMs (int units){
        return units / 5.0;

    }
    /// Todos los tiempos son convertidos a un formato estándar para
    /// representar los tiempos con 1 decimal
    public static String formato(int units) {
        double ms = unitsToMs(units);
        if (ms == Math.floor(ms)) {
            return String.format("%.0f", ms);
        }
        return String.format("%.1f", ms);
    }
}
