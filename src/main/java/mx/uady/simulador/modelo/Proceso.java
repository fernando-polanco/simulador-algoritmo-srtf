package mx.uady.simulador.modelo;

public class Proceso {
    private final String id;
    private final int llegada;
    private final int rafaga;
    private int restante;
    /// Valor bandera, -1 indica que el proceso no ha terminado
    private int finalizacion = -1;

    public Proceso (String id, int llegada, int rafaga){
        this.id=id;
        this.llegada=EscalaTiempo.msToUnits(llegada);
        this.rafaga=EscalaTiempo.msToUnits(rafaga);
        this.restante=this.rafaga;
    }

    public String getId() {
        return id;
    }
    public int getLlegada() {
        return llegada;
    }
    public int getRafaga() {
        return rafaga;
    }
    public int getRestante() {
        return restante;
    }

    public void ejecutar(int unidades){
        restante-=unidades;
        if (restante<0) restante=0;
    }

    public boolean procesoTerminado (){
        return restante==0;
    }

    public int getFinalizacion() {
        return finalizacion;
    }

    public void setFinalizacion(int finalizacion) {
        this.finalizacion = finalizacion;
    }

    public int tiempoDeEspera () {
        if (finalizacion == -1){
            throw new IllegalStateException("El proceso aún no ha terminado. ");
        }
        return finalizacion-llegada-rafaga;
        }
    }




