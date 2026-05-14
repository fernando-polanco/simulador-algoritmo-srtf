package mx.uady.simulador.modelo;

public class SegmentoDeGantt {
    private final String etiqueta;
    private final int inicio;
    private final int fin;
    private final boolean cambioDeContexto;

    public SegmentoDeGantt(String etiqueta, int inicio, int fin, boolean cambioDeContexto) {
        this.etiqueta = etiqueta;
        this.inicio = inicio;
        this.fin = fin;
        this.cambioDeContexto = cambioDeContexto;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public int getInicio() {
        return inicio;
    }

    public int getFin() {
        return fin;
    }

    public boolean isCambioDeContexto() {
        return cambioDeContexto;
    }
    public int duracion (){
        return fin - inicio;
    }
}
