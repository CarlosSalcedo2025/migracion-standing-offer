package com.bbva.migracionstandingoffer.comun;

/**
 * Constantes transversales de negocio, etapas y configuración para la migración de Standing Offer.
 */
public final class MigracionConstantes {

    private MigracionConstantes() {
        // Prevenir instanciación
    }

    // Nombres oficiales de etapas en el Pipeline
    public static final String ETAPA_RADICACION = "RADICACION";
    public static final String ETAPA_SCORING = "SCORING";
    public static final String ETAPA_EVALUACION_SANCION = "EVALUACION_SANCION";
    public static final String ETAPA_VOBO_PAGADOR = "VOBO_PAGADOR";
    public static final String ETAPA_FORMALIZACION = "FORMALIZACION";

    // Tareas humanas en Bonita Community
    public static final String TAREA_SCORING = "Scoring";
    public static final String TAREA_EVALUACION_SANCION = "Evaluacion y sancion";
    public static final String TAREA_VOBO_PAGADOR = "VoBo Pagador";
    public static final String TAREA_FORMALIZACION = "Formalización";

    // Estados de ejecución
    public static final String ESTADO_OK = "OK";
    public static final String ESTADO_ERROR = "ERROR";
    public static final String ESTADO_PARCIAL = "PARCIAL";
    public static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    public static final String ESTADO_NO_ENCONTRADO = "NO_ENCONTRADO";

    // Valores por defecto de negocio en los contratos
    public static final String VALOR_DEFAULT_CONTINUAR = "Continuar";
    public static final String VALOR_DEFAULT_OK = "ok";
    public static final String VALOR_DEFAULT_NO = "No";
    public static final String VALOR_DEFAULT_PRODUCTO_LIBRANZA = "LIBRANZA";

    // Claves de contexto para datos temporales entre etapas
    public static final String CLAVE_DATOS_SCORING = "_DATOS_SCORING";
    public static final String CLAVE_DATOS_EVALUACION_SANCION = "_DATOS_EVALUACION_SANCION";
    public static final String CLAVE_DATOS_VOBO_PAGADOR = "_DATOS_VOBO_PAGADOR";
    public static final String CLAVE_DATOS_FORMALIZACION = "_DATOS_FORMALIZACION";
    public static final String CLAVE_CONTRATO = "contrato";
}
