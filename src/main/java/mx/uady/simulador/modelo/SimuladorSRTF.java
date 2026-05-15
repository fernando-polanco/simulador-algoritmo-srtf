package mx.uady.simulador.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimuladorSRTF {
    private final List<Proceso> procesos;
    private final List<SegmentoDeGantt> segmentos = new ArrayList<>();
    private int tiempoActual = 0;
    private int paso = 1;
    private Proceso procesoActual = null;
    private boolean simulacionFinalizada = false;

    /// Constructor: Recibe una lista de procesos y lo asigna a la instancia.
    public SimuladorSRTF(List<Proceso> procesos) {
        this.procesos = procesos;
    }

    public void avanzarPaso() {

        if (simulacionFinalizada) {
            return;
        }

        /// Si están todos terminados, se regresa
        if (todosTerminados()) {
            simulacionFinalizada = true;
            return;
        }

        Proceso procesoElegido = escogerProceso();

        /// Si el proceso elegido arroja null, quiere decir que la CPU esta ociosa. Se marca como IDLE
        if (procesoElegido == null) {
            avanzarASiguienteEvento();
            paso++;
            return;

        }

        if (debeAplicarCambioContexto(procesoElegido)) {
            aplicarCambioContexto();
        }
        /// Se procesa el actual
        procesoActual = procesoElegido;
        /// Se calcula la aparición del proximo evento
        int siguienteEvento = SiguienteEvento(procesoActual);
        /// Se calcula cuanto tiempo se ejecuta el proceso actual hasta el siguiente evento
        int duracion = siguienteEvento - tiempoActual;

        /// Si duracion es mayor a 0, se ejecuta el proceso actual.
        if (duracion>0){
            int inicio = tiempoActual;
            /// Ejecución del proceso actual y calculo de tiempos tras su ejecución
            procesoActual.ejecutar(duracion);
            tiempoActual= siguienteEvento;
            segmentos.add(new SegmentoDeGantt
                    (procesoActual.getId(),inicio,tiempoActual,false));
        }
        /// Si la rafaga de tiempo ejecutada termina con el proceso se marca
        if (procesoActual.procesoTerminado()){
            procesoActual.setFinalizacion(tiempoActual);
            procesoActual=null;
        }
        /// Se actualiza la bandera de todos termiandos para la siguiente invocación del método
        if (todosTerminados()){
            simulacionFinalizada =true;
        }
        paso++;
    }

    /// Uso de stream para facilitar las busquedas dentro de la lista de procesos
    /// .filter es una función para acceder elementos de lista, arreglo etc. Que
    /// cumplen con una condición
    public Proceso escogerProceso() {
        return procesos.stream()
                /// se obtienen los procesos cuya llegada sea menor o igual al tiempo actual
                .filter(proceso -> proceso.getLlegada() <= tiempoActual)
                /// se obtienen procesos que no hayan finalizado
                .filter(proceso -> !proceso.procesoTerminado())
                /// se comparan los procesos por tiempo restante para SRTF
                .min(Comparator.comparingInt(Proceso::getRestante)
                        /// Si coinciden en tiempo restante, se selecciona
                        /// el proceso con el tiempo de llegada menor
                        .thenComparingInt(Proceso::getLlegada)
                        /// Si coinciden en las anteriores, se selecciona aquel proceso con
                        /// el valor de Id menor
                        .thenComparing(Proceso::getId))
                /// Si no se cumple nada, se devuelve un null indicando que
                /// no hay un proceso disponible actualmente
                .orElse(null);
    }

    /// Metodo que detecta momentos en donde la CPU esta ociosa
    public void avanzarASiguienteEvento() {
        /// Uso de stream()
        int siguienteLlegada = procesos.stream()
                ///Se filtran unicamente procesos cuya llegada sea mayor al tiempo actual
                .filter(proceso -> proceso.getLlegada() > tiempoActual)
                /// Usando "::" se hace referencia al metodo getLlegada() de cada proceso
                /// mapToInt convierte cada proceso en un entero igual a su llegada
                .mapToInt(Proceso::getLlegada)
                /// se obtiene el proceso con la llegada minima, es decir, el mas proximo
                .min()
                /// por defecto devuelve el tiempo actual
                .orElse(tiempoActual);

        /// Si la siguiente llegada es mayor al tiempo actual, la
        /// CPU se queda ociosa y se marca el segmento como IDLE
        if (siguienteLlegada > tiempoActual) {
            segmentos.add(new SegmentoDeGantt
                    ("IDLE", tiempoActual, siguienteLlegada, false));
        }
        tiempoActual = siguienteLlegada;

    }

    /// Se busca en la lista de procesos mediante stream()
    /// allMatch() devuelve booleano si todos satisfacen
    /// la condicion procesoTerminado == true
    /// /// "::" referencia al metodo procesoTerminado() de todos los procesos
    public boolean todosTerminados() {
        return procesos.stream().allMatch(Proceso::procesoTerminado);
    }

    /// Usando la escala de tiempo creada, se agrega un cambio de contexto
    public void aplicarCambioContexto() {
        int inicio = tiempoActual;
        tiempoActual += EscalaTiempo.CONTEXT_SWITCH;
        segmentos.add(new SegmentoDeGantt("CC", inicio, tiempoActual, true));
    }

    private boolean debeAplicarCambioContexto(Proceso elegido) {
        if (segmentos.isEmpty()) {
            return false;
        }
        /// se terminó el proceso actual
        if (procesoActual == null) {
            return true;
        }
        /// el proceso actual es distinto al elegido -> Hubo cambio
        return procesoActual != elegido;
    }

    /// Para no avanzar tick por tick, se avanza por eventos
    public int SiguienteEvento (Proceso actual){

        /// Se obtiene una lista de procesos futuros por medio de stream()
        List<Proceso> procesosFuturos = procesos.stream()
                ///El filtro busca procesos cuya llegada sea mayor al tiempo actual
                .filter(proceso -> proceso.getLlegada() > tiempoActual)
                ///Se ordenan los procesos de menor a mayor, comparando en enteros
                ///sus tiempos de llegada. "::" es una referencia al metodo getLlegada() de cada proceso
                .sorted(Comparator.comparingInt(Proceso::getLlegada))
                ///Se devuelve la lista
                .toList();

        ///Se calcula el momento en que terminaría el tiempo actual
        int finActual = tiempoActual + actual.getRestante();

        /// Se simulan los procesos
        int tiempoSimulado = tiempoActual;
        int restanteSimulado = actual.getRestante();

        ///Se recorren los procesos proximos
        for (Proceso proximo : procesosFuturos){
            ///Llegada del proximo proceso
            int llegadaProximo = proximo.getLlegada();

            /// Si la llegadaProximo del proximo es despues del actual, se continua el proceso sin interrupcion
            if (llegadaProximo >= finActual){
                return finActual;
            }

            ///Si no es después, se calcula cuánto tiempo se ejecutó y el estando del actual
            int ejecutado = llegadaProximo - tiempoSimulado;
            restanteSimulado -= ejecutado;
            tiempoSimulado = llegadaProximo;

            ///Si el restante del proximo es menor al restante del proceso actual simulado
            ///se devuelve el momento en que retorna el proximo proceso
            if (proximo.getRestante() < restanteSimulado){
                 return  llegadaProximo;
             }
        }
        ///Si no se cumple nada, simplemente se retorna el tiempo en que termina el proceso actual
        return finActual;
    }

    /// Getters
    public List<Proceso> getProcesos() {
        return procesos;
    }

    public List<SegmentoDeGantt> getSegmentos() {
        return segmentos;
    }

    public int getTiempoActual() {
        return tiempoActual;
    }

    public int getPaso() {
        return paso;
    }

    public Proceso getProcesoActual() {
        return procesoActual;
    }

    public boolean isSimulacionFinalizada() {
        return simulacionFinalizada;
    }
}
