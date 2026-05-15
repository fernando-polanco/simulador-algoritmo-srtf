package mx.uady.simulador.utils;

public class ScaledTime {
    private static final int SCALE_FACTOR = 10;
    private static final double DISPLAY_FACTOR = 1.0 / SCALE_FACTOR;

    /**
     * Convierte un valor de tiempo en milisegundos a su representación escalada (x10).
     * @param value tiempo en ms.
     * @return {@code int} del valor escalado.
     */
    public static int fromMilliseconds(double value) {
        return ((int) Math.round(value * SCALE_FACTOR));
    }

    /**
     * Convierte un valor de tiempo escalado a su representación en milisegundos (x0.1)
     * @param value valor escalado
     * @return {@code double} del tiempo en milisegundos.
     */
    public static double toMilliseconds(int value) {
        return value * DISPLAY_FACTOR;
    }
}
