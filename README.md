# Sistemas Operativos
## Proyecto Final

## Descripción del proyecto

### Planteamiento
En la tarea que tiene el sistema operativo de administrar el procesador el algoritmo SRTF (Shortest Remaining Time First) 
funciona utilizando un esquema *preemptive*, es decir, si un nuevo proceso llega con una longitud de ráfaga de CPU menor 
que el tiempo de CPU restante del actual proceso en ejecución, entonces este último proceso es "sacado" del CPU. 
El algoritmo SRTF ha mostrado tener un buen desempeño en la disminución de los tiempos implicados en la ejecución de los 
procesos en la CPU.

### Problema
Para esta lista de procesos, aplique el algoritmo de planificación SRTF para realizar la simulación solicitada.


| Proceso | Tiempo de llegada | Tiempo de ráfaga |
|---------|-------------------|------------------|
| P1      | 0                 | 8                |
| P2      | 3                 | 4                |
| P3      | 6                 | 2                |
| P4      | 10                | 3                |
| P5      | 15                | 6                |

### Desarrollo
En equipo, realizar un programa (simulación) que resuelva el siguiente problema de administración del proceso. Se
requiere simular la solución paso a paso de forma gráfica hasta su conclusión.

Para el desarrollo de este proyecto se debe utilizar el lenguaje de programación Java y JavaFX para la parte gráfica 
de la simulación.

---

## Lineamientos

1. Al inicio de la simulación, se debe visualizar en la pantalla de la computadora:
    - El nombre de la simulación: **"SIMULACIÓN DE LA APLICACIÓN DEL ALGORITMO SRTF"**.
    - La tabla de procesos dada por el problema.
    - El gráfico que represente al procesador.
    - Un cuadro con el **"Cálculo de los tiempos"** (enfatizando que el tiempo de cambio de contexto que se considerará 
   será de 0.2 milisegundos) que inicialmente no mostrará ningún cálculo, sino hasta que termine toda la simulación de 
   los procesos.
    - Un control (botón) etiquetado con **"Paso n"** (iniciando con el "Paso 1") para realizar la simulación paso a paso 
   hasta su conclusión.
    - Un botón para re indicar la simulación.

2. Conforme se presione el botón de control de la simulación (el botón con la etiqueta "Paso n"), este debe indicar el 
paso realizado hasta la finalización de la simulación en que la etiqueta del botón debe cambiar a **"Simulación Finalizada"**. 
Si se presionara de nuevo el botón, el programa deberá cerrarse o permitir reiniciar de nuevo.
3. Utilizar colores para los elementos en pantalla como:
    - La porción del gráfico de Gantt para cada uno de los procesos que van entrando a la CPU
    - El del botón "Paso n"
    - El cuadro del "Cálculo de los tiempos"
4. El **Gráfico de Gantt** que representa a la CPU deberá mostrar su estado correspondiente (segmentos) en cada uno de 
los pasos de la simulación.
5. La simulación deberá finalizar hasta que la duración (tiempo de ráfaga) de cada uno de los procesos se haya alcanzado 
de acuerdo al funcionamiento del Algoritmo SRTF y hasta ese momento aparecerá en el área correspondiente de la pantalla, 
es decir, en el cuadro del "Cálculo de los tiempos" las cantidades calculadas de acuerdo a los valores obtenidos en la 
simulación y será cuando el botón de paso a paso de la simulación cambie su etiqueta a **"Simulación Finalizada"**. 
Considerar un mecanismo que permita al usuario terminar la simulación o iniciar una nueva ejecución de la misma.
6. Cálculos de los tiempos a mostrar al finalizar la simulación:
    - **TE**: Tiempo de espera de cada proceso, que se calcula como el turnaround menos el tiempo de ráfaga más los 
   cambios de contexto que se hayan realizado a partir del tiempo de llegada del proceso.
    - **TEP**: Tiempo de espera promedio, que se calcula como la suma de los tiempos de espera de cada proceso dividida 
   entre el número total de procesos.
    - **TTP**: Tiempo total de procesamiento, que se calcula como el tiempo actual en que se finaliza la ejecución del 
   último proceso más los cambios de contexto realizados en total (0.2 * (No. CC)).
    - **%TTP que consume el TEP**: El porcentaje de tiempo que la CPU estuvo sin procesos en ejecución, se calcula como
   el TEP dividido entre el TTP multiplicado por 100.
7. El orden de los elementos en pantalla debe ser el siguiente:
    - El nombre de la simulación en la parte superior.
    - La tabla de procesos centrada y debajo del nombre de la simulación.
    - El gráfico de Gantt debajo de la tabla de procesos.
    - El cuadro del "Cálculo de los tiempos" debajo del gráfico de Gantt.
    - El botón de control para realizar la simulación paso a paso debajo del cuadro de los cálculos de los tiempos.

## Restricciones

1. Cada cambio de contexto se realiza en un tiempo de **0.2 milisegundos**.
2. Los cambios de contexto (CC) no se suman al tiempo actual de la simulación, es decir, el tiempo de CC se considera un 
tiempo adicional que se suma al TE de cada proceso y al TTP, pero no se suma al tiempo actual de la simulación, por lo 
que se debe guardar un registro de los CC realizados y en que tiempos se realizaron.
3. A excepción del primer proceso en entrar a la CPU (P1 en este caso), cada proceso que entre a la CPU deberá esperar
   un tiempo de 0.2 milisegundos por el cambio de contexto.
4. El programa debe trabajar únicamente con los procesos dados en la tabla del problema, no se deben agregar procesos 
adicionales ni editar los tiempos de llegada o ráfaga de los procesos.
5. Todos los procesos de llegada y ráfaga se manejarán como enteros y se considerarán en milisegundos.
6. El programa debe ser desarrollado utilizando el lenguaje de programación Java y JavaFX para la parte gráfica de la simulación.
7. Cada clic en el botón de control de la simulación debe avanzar al siguiente evento de la simulación, siendo este evento 
la entrada de un proceso a la CPU (ya sea por preempción o porque la CPU esté libre) o la finalización de un proceso en la CPU.
8. El código debe permitir la creación de un archivo totalmente ejecutable, es decir, un archivo que permita ejecutar
el programa sin necesidad de tener instalado el lenguaje de programación o alguno de sus componentes requeridos en su elaboración.
