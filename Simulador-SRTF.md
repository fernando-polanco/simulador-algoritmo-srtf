# Universidad Autónoma de Yucatán
## Facultad de Matemáticas
### Lic. En Ingeniería de Software Enero – Mayo 2026

# SISTEMAS OPERATIVOS
## PROYECTO FINAL
### ADMINISTRACIÓN DEL PROCESADOR UTILIZANDO EL ALGORITMO SRTF (Shortest Remaining Time First)

---

## Descripción de la Actividad

### Planteamiento
En la tarea que tiene el sistema operativo de administrar el procesador el algoritmo SRTF (Shortest Remaining Time First) funciona utilizando un esquema *preemptive*, es decir, si un nuevo proceso llega con una longitud de ráfaga de CPU menor que el tiempo de CPU restante del actual proceso en ejecución, entonces este último proceso es "sacado" del CPU. El algoritmo SRTF ha mostrado tener un buen desempeño en la disminución de los tiempos implicados en la ejecución de los procesos en la CPU.

### Desarrollo
En equipo, realizar un programa (simulación) que resuelva el siguiente problema específico de administración del procesador. Se requiere simular la solución paso a paso de forma gráfica hasta su conclusión.

> Para el desarrollo de esta actividad se puede utilizar cualquier lenguaje de programación.

### Problema
Para esta lista de procesos, aplique el algoritmo de planificación SRTF para realizar la simulación solicitada.

| Proceso | Tiempo de llegada | Tiempo de ráfaga |
|---------|-------------------|------------------|
| P1      | 0                 | 8                |
| P2      | 3                 | 4                |
| P3      | 6                 | 2                |
| P4      | 10                | 3                |
| P5      | 15                | 6                |

### Consideraciones:
1. Cada cambio de contexto se realiza en un tiempo de **0.2 milisegundos**.
2. Los ocho puntos contenidos en el archivo PDF "Consideraciones para la aplicación de los Algoritmos de Planificación de Procesos de la Unidad 2".

---

## Lineamientos:

1. Al inicio de la simulación, se debe visualizar en la pantalla de la computadora:
    - El nombre de la simulación: **"SIMULACIÓN DE LA APLICACIÓN DEL ALGORITMO SRTF"**.
    - La tabla de procesos dada por el problema.
    - El gráfico que represente al procesador.
    - Un cuadro con el **"Cálculo de los tiempos"** (enfatizando que el tiempo de cambio de contexto que se considerará será de 0.2 milisegundos) que inicialmente no mostrará ningún cálculo, sino hasta que termine toda la simulación de los procesos.
    - Un control (botón) etiquetado con **"Paso n"** (iniciando con el "Paso 1") para realizar la simulación paso a paso hasta su conclusión.

2. Conforme se presione el botón de control de la simulación (el botón con la etiqueta "Paso n"), este debe indicar el paso realizado hasta la finalización de la simulación en que la etiqueta del botón debe cambiar a **"Simulación Finalizada"**. Si se presionara de nuevo el botón, el programa deberá cerrarse o permitir reiniciar de nuevo.

3. Los colores a utilizar para los elementos en pantalla como:
    - La porción del gráfico de Gantt para cada uno de los procesos que van entrando a la CPU
    - La del botón "Paso n"
    - La del cuadro del "Cálculo de los tiempos"

   Podrán ser como se muestra en la imagen o se podrán utilizar otros.

4. El **Gráfico de Gantt** que representa a la CPU deberá mostrar su estado correspondiente en cada uno de los pasos de la simulación.

5. La simulación deberá finalizar hasta que la duración (tiempo de ráfaga) de cada uno de los procesos se haya alcanzado de acuerdo al funcionamiento del Algoritmo SRTF y hasta ese momento aparecerá en el área correspondiente de la pantalla, es decir, en el cuadro del "Cálculo de los tiempos" las cantidades calculadas de acuerdo a los valores obtenidos en la simulación y será cuando el botón de paso a paso de la simulación cambie su etiqueta a **"Simulación Finalizada"**. Considerar un mecanismo que permita al usuario terminar la simulación o iniciar una nueva ejecución de la misma.

## Cálculo de los tiempos a mostrar al finalizar la simulación:
- **Tiempo de espera**: El tiempo de espera de cada proceso, que se calcula como el tiempo de retorno menos el tiempo de ráfaga.
- **Tiempo de espera promedio**: El tiempo de espera promedio de todos los procesos, que se calcula como la suma de los tiempos de espera de cada proceso dividida entre el número total de procesos.
- **Tiempo total de procesamiento**: El tiempo total de procesamiento equivale al tiempo actual en que se finaliza la ejecución del último proceso, es decir, el tiempo de finalización del último proceso.
- **Porcentaje del TTP que consume el TEP**: El porcentaje de tiempo que la CPU estuvo sin procesos en ejecución, es decir, el tiempo de espera total dividido entre el tiempo total de procesamiento multiplicado por 100.

---

## Cierre

- **Entrega**: mediante la plataforma EnLinea.
- **Responsable**: Solo un integrante del equipo deberá entregar la actividad.
- **Fecha límite**: Viernes 15 de mayo de 2026, hasta las 23:59 horas.
- **Recursos y materiales**: Referencias del curso, lenguaje de programación.

---

## Evaluación

Para la evaluación se consideran los siguientes aspectos:

- **Adecuación del programa (simulación)** a los lineamientos establecidos.
- **Estructura correcta** del programa.
- **IMPORTANTE**: La entrega se realizará mediante un archivo comprimido (`*.ZIP`) conteniendo además del código fuente del programa, un **ARCHIVO "TOTALMENTE" EJECUTABLE**, es decir, un archivo del programa que se pueda ejecutar sin requerir tener instalado el lenguaje de programación utilizado para su desarrollo o alguno de los componentes requeridos en su elaboración. Este archivo totalmente ejecutable será el que se revisará para asignar la calificación preliminar.
- Incluir en el archivo comprimido del programa una **portada (archivo PDF)** que contenga los nombres de todos los integrantes del equipo.
- **REVISIÓN PARA ASIGNACIÓN DE LA CALIFICACIÓN**: La revisión del Proyecto Final se realizará en la sesión de retroalimentación de las actividades del Segundo Período programada para el **martes 26/mayo/2026, de 13:00 a 14:30 horas en el aula D2**, de la siguiente forma: En la laptop o equipo de cómputo de alguno de los integrantes del equipo se ejecutará el programa que se calificará de acuerdo a los aspectos que se han solicitado en los "Lineamientos" y se han establecido en la "Evaluación" para asignarle la calificación correspondiente que se dará a conocer al equipo en ese momento.
