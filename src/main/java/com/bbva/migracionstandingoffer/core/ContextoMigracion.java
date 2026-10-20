package com.bbva.migracionstandingoffer.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public ContextoMigracion() {
    }

    public ContextoMigracion(String casoNegocio) {
        this.casoNegocio = casoNegocio;
    }

    public String getCasoNegocio() {
        return casoNegocio;
    }

    public void setCasoNegocio(String casoNegocio) {
        this.casoNegocio = casoNegocio;
    }

    public String getCaseIdCM() {
        return caseIdCM;
    }

    public void setCaseIdCM(String caseIdCM) {
        this.caseIdCM = caseIdCM;
    }

    public String getEtapaObjetivo() {
        return etapaObjetivo;
    }

    public void setEtapaObjetivo(String etapaObjetivo) {
        this.etapaObjetivo = etapaObjetivo;
    }

    public String getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(String estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public Map<String, Object> getContrato() {
        return contrato;
    }

    public void setContrato(Map<String, Object> contrato) {
        this.contrato = contrato;
    }

    public List<ResultadoEtapa> getHistorial() {
        return historial;
    }

    public void setHistorial(List<ResultadoEtapa> historial) {
        this.historial = historial;
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
