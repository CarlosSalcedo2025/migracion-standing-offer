package com.bbva.migracionstandingoffer.core;

/**
 * Contrato base para cada etapa del flujo de migración en Bonita.
 * Cada etapa es un componente modular independiente.
 */
public interface EtapaMigracion {

    /**
     * Orden secuencial en el que debe ejecutarse la etapa dentro del proceso Bonita.
     */
    int getOrden();

    /**
     * Nombre identificador de la etapa (ej: "RADICACION", "SCORING", "EVALUACION_SANCION", etc.)
     */
    String getNombreEtapa();

    /**
     * Nombre de la tarea humana en Bonita CM (null si es la radicación inicial).
     */
    String getNombreTareaBonita();

    /**
     * Determina si esta etapa debe ejecutarse para el contexto actual.
     */
    boolean debeEjecutar(ContextoMigracion contexto);

    /**
     * Ejecuta la lógica propia de la etapa (armado de contrato, invocación a Bonita y validación).
     */
    ResultadoEtapa procesar(ContextoMigracion contexto);
}
