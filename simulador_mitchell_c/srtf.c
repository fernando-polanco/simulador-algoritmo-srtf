/*
Integrantes:
Bachtold Gutiérrez Mitchell
Canto Medrano Russell Israel
Polanco Casares Fernando
Solís Chen Wimon Rafael
Zapata Góngora Diego Armín

El siguiente programa simula la ejecucion del algoritmo Shortest Remaining Time First aplicado a la calendarizacion de procesos en un sistema operativo.
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define NUM_PROCESOS 5
#define CAMBIO_CONTEXTO .2

struct proceso {
    //Tiempo de llegada.
    int tlleg;
    //Tiempo de rafaga.
    int traf;
    bool activado;
    bool finalizado;
    struct proceso *apsig;
    struct proceso *apant;
};

void iniciar_tabla(struct proceso *procesos);
void dibujar_tabla(struct proceso *procesos);
void nuevo_proceso_activo(struct proceso *procesos, int tiempo, struct proceso **procesos_activos, int *num_procesos_activados);
void proceso_Actual(int *num_procesos_activados, struct proceso **procesos_activos, int *raf_menor, struct proceso **proceso_actual);
void resta_rafaga(struct proceso **proceso_actual);
void salida(struct proceso **procesos_activos, int *num_procesos_activados, int *num_procesos_finalizados);

int main () {
    bool continuar = true;
    int tiempo = 0;
    int num_procesos_activados = 0;
    struct proceso *proceso_actual = NULL;

    //Arreglo de procesos activos.
    struct proceso *procesos_activos[NUM_PROCESOS] = {NULL};

    //Tabla de procesos.
    struct proceso procesos[NUM_PROCESOS] = {0};
    iniciar_tabla(procesos);
    dibujar_tabla(procesos);
    
    //Siguiente tiempo.
    printf("\n- Presione enter para proceder al siguiente instante.");
    getchar();

    //Mientras variable de control que detecte cuando todos los procesos finalicen.
    while (continuar == true) {
        int raf_menor = 10000;
        int num_procesos_finalizados = 0;
        proceso_actual = NULL;

        nuevo_proceso_activo(procesos, tiempo, procesos_activos, &num_procesos_activados);
        proceso_Actual(&num_procesos_activados, procesos_activos, &raf_menor, &proceso_actual);

        if (proceso_actual != NULL) {
            int indice = 0;
            while(&procesos[indice] != proceso_actual) {
                indice++;
            }
            printf("| P%d ", indice + 1);
        } else {
            printf("| -- ");
        }

        resta_rafaga(&proceso_actual);

        tiempo++;

        salida(procesos_activos,&num_procesos_activados, &num_procesos_finalizados);
        if (num_procesos_finalizados == NUM_PROCESOS) {
            continuar = false;
        } else {
            printf("\n- Presione enter para proceder al siguiente instante.");
            getchar();
        }
    }
}

void iniciar_tabla(struct proceso *procesos) {
    //Tabla de procesos.
    int tabla_procesos[NUM_PROCESOS][2] = {
        {0,8},
        {3,4},
        {6,2},
        {10,3},
        {15,6},
    };

    //Asignamos los valores de la tabla a cada uno de los procesos.
    for (int i = 0; i < NUM_PROCESOS; i++) {
        procesos[i].tlleg = tabla_procesos[i][0];
        procesos[i].traf = tabla_procesos[i][1];
        procesos[i].activado = false;
        procesos[i].finalizado = false;
        
        //Si es el primer elemento.
        if (i < NUM_PROCESOS -1) {
            procesos[i].apsig = &procesos[i+1];
        } else {
            procesos[i].apsig = NULL;
        }

        if (i > 0) {
            procesos[i].apant = &procesos[i-1];
        } else {
            procesos[i].apant = NULL;
        }
    }
}

void dibujar_tabla(struct proceso *procesos) {
    printf("_________________________\n");
    printf("| Pr | Llegada | Rafaga |");
    printf("\n|____|_________|________|");
    for (int i = 0; i < NUM_PROCESOS; i++) {
        printf("\nP[%d] | %7d | %6d |",i+1,procesos[i].tlleg,procesos[i].traf);
    }
    printf("\n|____|_________|________|");
}

void nuevo_proceso_activo(struct proceso *procesos, int tiempo, struct proceso **procesos_activos, int *num_procesos_activados) {
    for (int i = 0; i < NUM_PROCESOS; i++) {
        //Checamos si el proceso llego y lo agregamos a los procesos activos.
        if (procesos[i].tlleg == tiempo /*Posible caso esquina: && procesos[i].finalizado == false*/) {
            procesos[i].activado = true;
            procesos_activos[*num_procesos_activados] = &procesos[i];
            (*num_procesos_activados)++;
        }
    }
}

void proceso_Actual(int *num_procesos_activados, struct proceso **procesos_activos, int *raf_menor, struct proceso **proceso_actual) {
    for (int i = 0; i < *num_procesos_activados; i++) {
        if (procesos_activos[i]->traf < *raf_menor && procesos_activos[i]->finalizado != true) {
            *raf_menor = procesos_activos[i]->traf;
            *proceso_actual = procesos_activos[i];
        }
    }
}

void resta_rafaga(struct proceso **proceso_actual) {
    if (*proceso_actual != NULL) {
        (*proceso_actual)->traf--;
        if ((*proceso_actual)->traf == 0) {
            (*proceso_actual)->finalizado = true;
        }
    }
}

void salida(struct proceso **procesos_activos, int *num_procesos_activados, int *num_procesos_finalizados) {
    for (int i = 0; i < *num_procesos_activados; i++) {
        if (procesos_activos[i]->finalizado == true) {
            (*num_procesos_finalizados)++;
        }
    }
}