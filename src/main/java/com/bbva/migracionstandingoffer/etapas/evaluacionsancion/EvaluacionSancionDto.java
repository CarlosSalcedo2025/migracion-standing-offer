package com.bbva.migracionstandingoffer.etapas.evaluacionsancion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluacionSancionDto {
    private String producto = "LIBRANZA";
    private String correccionScoring = "No";
    private String garantia = "No";
    private String instancia;
    private String acta;
    private String fechaActa;
    private String montoAprobado;
    private Map<String, Object> dictamen;
    private List<Map<String, Object>> tipologiasCasuisticas;
    private String observaciones = "ok";
    private String proximoPaso = "Continuar";

    public EvaluacionSancionDto() {
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCorreccionScoring() {
        return correccionScoring;
    }

    public void setCorreccionScoring(String correccionScoring) {
        this.correccionScoring = correccionScoring;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public String getInstancia() {
        return instancia;
    }

    public void setInstancia(String instancia) {
        this.instancia = instancia;
    }

    public String getActa() {
        return acta;
    }

    public void setActa(String acta) {
        this.acta = acta;
    }

    public String getFechaActa() {
        return fechaActa;
    }

    public void setFechaActa(String fechaActa) {
        this.fechaActa = fechaActa;
    }

    public String getMontoAprobado() {
        return montoAprobado;
    }

    public void setMontoAprobado(String montoAprobado) {
        this.montoAprobado = montoAprobado;
    }

    public Map<String, Object> getDictamen() {
        return dictamen;
    }

    public void setDictamen(Map<String, Object> dictamen) {
        this.dictamen = dictamen;
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
}
