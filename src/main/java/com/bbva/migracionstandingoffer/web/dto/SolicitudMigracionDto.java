package com.bbva.migracionstandingoffer.web.dto;

import com.bbva.migracionstandingoffer.etapas.evaluacionsancion.EvaluacionSancionDto;
import com.bbva.migracionstandingoffer.etapas.formalizacion.FormalizacionDto;
import com.bbva.migracionstandingoffer.etapas.vobopagador.VoBoPagadorDto;
import com.bbva.migracionstandingoffer.modelo.CasoRadicacionDto;
import com.bbva.migracionstandingoffer.modelo.ScoringContratoDto;

public class SolicitudMigracionDto {

    private String casoNegocio;
    private String etapaObjetivo;
    private CasoRadicacionDto precarga;
    private ScoringContratoDto scoring;
    private EvaluacionSancionDto evaluacionSancion;
    private VoBoPagadorDto voBoPagador;
    private FormalizacionDto formalizacion;

    public SolicitudMigracionDto() {
    }

    public String getCasoNegocio() {
        return casoNegocio;
    }

    public void setCasoNegocio(String casoNegocio) {
        this.casoNegocio = casoNegocio;
    }

    public String getEtapaObjetivo() {
        return etapaObjetivo;
    }

    public void setEtapaObjetivo(String etapaObjetivo) {
        this.etapaObjetivo = etapaObjetivo;
    }

    public CasoRadicacionDto getPrecarga() {
        return precarga;
    }

    public void setPrecarga(CasoRadicacionDto precarga) {
        this.precarga = precarga;
    }

    public ScoringContratoDto getScoring() {
        return scoring;
    }

    public void setScoring(ScoringContratoDto scoring) {
        this.scoring = scoring;
    }

    public EvaluacionSancionDto getEvaluacionSancion() {
        return evaluacionSancion;
    }

    public void setEvaluacionSancion(EvaluacionSancionDto evaluacionSancion) {
        this.evaluacionSancion = evaluacionSancion;
    }

    public VoBoPagadorDto getVoBoPagador() {
        return voBoPagador;
    }

    public void setVoBoPagador(VoBoPagadorDto voBoPagador) {
        this.voBoPagador = voBoPagador;
    }

    public FormalizacionDto getFormalizacion() {
        return formalizacion;
    }

    public void setFormalizacion(FormalizacionDto formalizacion) {
        this.formalizacion = formalizacion;
    }
}
