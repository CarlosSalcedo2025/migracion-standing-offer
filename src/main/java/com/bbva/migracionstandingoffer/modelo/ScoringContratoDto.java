package com.bbva.migracionstandingoffer.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoringContratoDto {
    private String techo;
    private String buro;
    private String contrato; // Nuevo Contrato
    private Map<String, Object> dictamen;
    private Integer dictamenScoring;
    private List<Map<String, Object>> tipologiasCasuisticas;
    private String observaciones;
    private String proximoPaso;
    private Long fechaInicio;
    private List<Object> archivos = Collections.emptyList();
    private String banco;
    private String numeroObligacion;
    private String valor;
    private String tipologiaScoring;

    public ScoringContratoDto() {
    }

    public String getTecho() {
        return techo;
    }

    public void setTecho(String techo) {
        this.techo = techo;
    }

    public String getBuro() {
        return buro;
    }

    public void setBuro(String buro) {
        this.buro = buro;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public Map<String, Object> getDictamen() {
        return dictamen;
    }

    public void setDictamen(Map<String, Object> dictamen) {
        this.dictamen = dictamen;
    }

    public Integer getDictamenScoring() {
        return dictamenScoring;
    }

    public void setDictamenScoring(Integer dictamenScoring) {
        this.dictamenScoring = dictamenScoring;
    }

    public List<Map<String, Object>> getTipologiasCasuisticas() {
        return tipologiasCasuisticas;
    }

    public void setTipologiasCasuisticas(List<Map<String, Object>> tipologiasCasuisticas) {
        this.tipologiasCasuisticas = tipologiasCasuisticas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getProximoPaso() {
        return proximoPaso;
    }

    public void setProximoPaso(String proximoPaso) {
        this.proximoPaso = proximoPaso;
    }

    public Long getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Long fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public List<Object> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Object> archivos) {
        this.archivos = archivos;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroObligacion() {
        return numeroObligacion;
    }

    public void setNumeroObligacion(String numeroObligacion) {
        this.numeroObligacion = numeroObligacion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipologiaScoring() {
        return tipologiaScoring;
    }

    public void setTipologiaScoring(String tipologiaScoring) {
        this.tipologiaScoring = tipologiaScoring;
    }
}
