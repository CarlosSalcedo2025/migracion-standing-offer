package com.bbva.migracionstandingoffer.core;

import lombok.Data;

import java.util.*;

@Data
public class ContextoMigracion {
    private String casoNegocio;
    private String caseIdCM;
    private String etapaObjetivo; // Por ejemplo "SCORING", "FORMALIZACION", etc.
    private String estadoFinal = "EN_PROCESO"; // OK, PARCIAL, ERROR

    /**
     * Contrato acumulativo de negocio.
     * Inicia con los datos de Radicación y se va enriqueciendo en cada etapa.
     */
    private Map<String, Object> contrato = new LinkedHashMap<>();

    /**
     * Historial de resultados de cada etapa ejecutada.
     */
    private List<ResultadoEtapa> historial = new ArrayList<>();

    public ContextoMigracion(String casoNegocio) {
        this.casoNegocio = casoNegocio;
    }

    public void agregarResultado(ResultadoEtapa resultado) {
        this.historial.add(resultado);
    }

    public void agregarDatoContrato(String clave, Object valor) {
        this.contrato.put(clave, valor);
    }

    public void fusionarContrato(Map<String, Object> nuevosDatos) {
        if (nuevosDatos != null) {
            this.contrato.putAll(nuevosDatos);
        }
    }
}
