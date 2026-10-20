package com.bbva.migracionstandingoffer.etapas.formalizacion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormalizacionDto {
    private String cuotaContratoFormalizacion = "30";
    private String valorMaximoTotalCredito = "1,520,137,860.00.00";
    private List<Object> archivos = Collections.emptyList();
    private String observaciones = "ok";
    private String proximoPaso = "Continuar";

    public FormalizacionDto() {
    }

    public String getCuotaContratoFormalizacion() {
        return cuotaContratoFormalizacion;
    }

    public void setCuotaContratoFormalizacion(String cuotaContratoFormalizacion) {
        this.cuotaContratoFormalizacion = cuotaContratoFormalizacion;
    }

    public String getValorMaximoTotalCredito() {
        return valorMaximoTotalCredito;
    }

    public void setValorMaximoTotalCredito(String valorMaximoTotalCredito) {
        this.valorMaximoTotalCredito = valorMaximoTotalCredito;
    }

    public List<Object> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Object> archivos) {
        this.archivos = archivos;
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
