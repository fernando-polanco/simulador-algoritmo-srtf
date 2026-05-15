 /*
Integrantes:
Bachtold Gutiérrez Mitchell
Canto Medrano Russell Israel
Polanco Casares Fernando
Solís Chen Wimon Rafael
Zapata Góngora Diego Armín

El siguiente programa simula la ejecucion del algoritmo Shortest Remaining Time First aplicado
a la calendarizacion de procesos en un sistema operativo.
*/ 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define NUM_PROCESOS 5
#define CAMBIO_CONTEXTO .2

struct proceso {
    int tlleg;
    int traf;
    bool activado;
    bool finalizado;
    float tesp;
    struct proceso *apsig;
    struct proceso *apant;
};

void limpiar_pantalla();
void iniciar_tabla(struct proceso *procesos);
void dibujar_tabla(struct proceso *procesos);
void nuevo_proceso_activo(struct proceso *procesos, int tiempo, struct proceso **procesos_activos, int *num_procesos_activados);
void proceso_Actual(int *num_procesos_activados, struct proceso **procesos_activos, int *raf_menor, struct proceso **proceso_actual);
void resta_rafaga(struct proceso **proceso_actual);
void salida(struct proceso **procesos_activos, int *num_procesos_activados, int *num_procesos_finalizados);
void indice_gantt(int *tam_gantt, int **gantt, struct proceso *proceso_actual, struct proceso *procesos);
void cambio_contexto(struct proceso *proceso_actual, struct proceso *proceso_anterior, struct proceso *base_procesos);
float imprimir_gantt(int tam_gantt, int *gantt, struct proceso *base_procesos);
void calcular_espera(int num_activados, struct proceso **activos, struct proceso *actual, struct proceso *anterior);
void imprimir_espera(struct proceso *procesos);
void imprimir_metricas(struct proceso *procesos, float ttp);

int main() {
    struct proceso *proceso_anterior = NULL;
    bool continuar = true;
    int tiempo = 0;
    int num_procesos_activados = 0;
    struct proceso *proceso_actual = NULL;
    int *gantt = NULL;
    int tam_gantt = 0;
    float ttp = 0.0;
    struct proceso *procesos_activos[NUM_PROCESOS] = {NULL};
    struct proceso procesos[NUM_PROCESOS] = {0};

    iniciar_tabla(procesos);

    while (continuar == true) {
        limpiar_pantalla();
        dibujar_tabla(procesos);
        int raf_menor = 10000;
        int num_procesos_finalizados = 0;
        proceso_actual = NULL;

        nuevo_proceso_activo(procesos, tiempo, procesos_activos, &num_procesos_activados);
        proceso_Actual(&num_procesos_activados, procesos_activos, &raf_menor, &proceso_actual);
        cambio_contexto(proceso_actual, proceso_anterior, procesos);
        calcular_espera(num_procesos_activados, procesos_activos, proceso_actual, proceso_anterior);
        resta_rafaga(&proceso_actual);
        indice_gantt(&tam_gantt, &gantt, proceso_actual, procesos);
        ttp = imprimir_gantt(tam_gantt, gantt, procesos);
        imprimir_espera(procesos);

        proceso_anterior = proceso_actual;
        tiempo++;

        salida(procesos_activos, &num_procesos_activados, &num_procesos_finalizados);
        if (num_procesos_finalizados == NUM_PROCESOS) {
            continuar = false;
        } else {
            printf("\n- Presione enter para el instante %d.", tiempo);
            getchar();
        }
    }

    imprimir_metricas(procesos, ttp);
    printf("\nSimulacion finalizada.\n");
    free(gantt);
    return 0;
}

void limpiar_pantalla() {
#ifdef _WIN32
    system("cls");
#else
    printf("\033[2J\033[3J\033[H");
    fflush(stdout);
#endif
}

void iniciar_tabla(struct proceso *procesos) {
    int tabla_procesos[NUM_PROCESOS][2] = {{0,8}, {3,4}, {6,2}, {10,3}, {15,6}};
    for (int i = 0; i < NUM_PROCESOS; i++) {
        procesos[i].tlleg = tabla_procesos[i][0];
        procesos[i].traf = tabla_procesos[i][1];
        procesos[i].activado = false;
        procesos[i].finalizado = false;
        procesos[i].tesp = 0.0;
        if (i < NUM_PROCESOS - 1) {
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
    printf("\nALGORITMO SRTF\n_________________________\n| Pr | Llegada | Rafaga |\n|____|_________|________|");
    for (int i = 0; i < NUM_PROCESOS; i++) {
        printf("\nP[%d] | %7d | %6d |", i+1, procesos[i].tlleg, procesos[i].traf);
    }
    printf("\n|____|_________|________|\n");
}

void nuevo_proceso_activo(struct proceso *procesos, int tiempo, struct proceso **procesos_activos, int *num_procesos_activados) {
    for (int i = 0; i < NUM_PROCESOS; i++) {
        if (procesos[i].tlleg == tiempo) {
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
    *num_procesos_finalizados = 0;
    for (int i = 0; i < *num_procesos_activados; i++) {
        if (procesos_activos[i]->finalizado == true) {
            (*num_procesos_finalizados)++;
        }
    }
}

void indice_gantt(int *tam_gantt, int **gantt, struct proceso *proceso_actual, struct proceso *procesos) {
    (*tam_gantt)++;
    *gantt = (int *)realloc(*gantt, (*tam_gantt) * sizeof(int));
    if (proceso_actual != NULL) {
        int indice = 0;
        while (&procesos[indice] != proceso_actual) {
            indice++;
        }
        (*gantt)[(*tam_gantt) - 1] = indice + 1;
    } else {
        (*gantt)[(*tam_gantt) - 1] = -1;
    }
}

void cambio_contexto(struct proceso *proceso_actual, struct proceso *proceso_anterior, struct proceso *base_procesos) {
    if (proceso_actual != NULL && proceso_anterior != NULL && proceso_actual != proceso_anterior) {
        printf("\n[Cambio de contexto: P%d -> P%d]\n",
            (int)((proceso_anterior - base_procesos) + 1),
            (int)((proceso_actual - base_procesos) + 1));
    }
}

float imprimir_gantt(int tam_gantt, int *gantt, struct proceso *base_procesos) {
    if (gantt == NULL) {
        return 0.0;
    }
    printf("\nGANTT:\n");
    for (int i = 0; i < tam_gantt; i++) {
        if (gantt[i] != -1) {
            printf("|  P%d  ", gantt[i]);
        } else {
            printf("|  --  ");
        }
    }
    printf("|\n");

    float eje = 0.0;
    printf("%-7.1f", eje);
    for (int i = 0; i < tam_gantt; i++) {
        if (i > 0 && gantt[i] != gantt[i-1]) {
            int idx_ant = gantt[i-1] - 1;
            if (idx_ant >= 0 && base_procesos[idx_ant].finalizado) {
                eje += CAMBIO_CONTEXTO;
            } else {
                eje += (CAMBIO_CONTEXTO * 2);
            }
        }
        eje += 1.0;
        printf("%-7.1f", eje);
    }
    printf("\n");
    return eje;
}

void calcular_espera(int num_activados, struct proceso **activos, struct proceso *actual, struct proceso *anterior) {
    bool hubo_cambio = (actual != anterior && anterior != NULL && actual != NULL);
    bool anterior_termina = hubo_cambio && (anterior->traf == 1);

    for (int i = 0; i < num_activados; i++) {
        if (activos[i]->finalizado == false && activos[i] != actual) {
            activos[i]->tesp += 1.0;
        }

        if (hubo_cambio) {
            if (anterior_termina) {
                if (activos[i] == actual) {
                    activos[i]->tesp += CAMBIO_CONTEXTO;
                }
            } else {
                if (activos[i]->finalizado == false && activos[i] != actual) {
                    activos[i]->tesp += CAMBIO_CONTEXTO;
                }
                if (activos[i] == actual) {
                    activos[i]->tesp += CAMBIO_CONTEXTO;
                }
            }
        }
    }
}

void imprimir_espera(struct proceso *procesos) {
    printf("\n__________________________\n| Proceso | Tiempo Espera |\n|_________|_______________|");
    for (int i = 0; i < NUM_PROCESOS; i++) {
        printf("\n|   P%d    |      %4.1f     |", i + 1, procesos[i].tesp);
    }
    printf("\n|_________|_______________|\n");
}

void imprimir_metricas(struct proceso *procesos, float ttp) {
    float tep = 0.0;
    for (int i = 0; i < NUM_PROCESOS; i++) {
        tep += procesos[i].tesp;
    }
    tep /= NUM_PROCESOS;

    float porcentaje = 0.0;
    if (ttp > 0.0f) {
        porcentaje = tep / ttp * 100.0f;
    } else {
        porcentaje = 0.0f;
    }

    printf("\n============================\n");
    printf("  METRICAS GLOBALES\n");
    printf("============================\n");
    printf("  b) TEP (Tiempo Espera Prom.) : %6.2f ms\n", tep);
    printf("  c) TTP (Tiempo Total Proc.)  : %6.2f ms\n", ttp);
    printf("  d) TEP como %% del TTP        : %6.2f %%\n", porcentaje);
    printf("============================\n");
}