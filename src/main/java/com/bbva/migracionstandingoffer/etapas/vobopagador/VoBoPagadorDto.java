package com.bbva.migracionstandingoffer.etapas.vobopagador;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoBoPagadorDto {
    private Object cuotaContratoFormalizacion = 30;
    private String cuotaVoBo = "30000";
    private String valorMaximoTotalCredito = "61,800,000.00";
    private String cuotaRetanqueo = "30";
    private String valorCuotaRecoger = "30";
    private String observaciones = "ok";
    private String proximoPaso = "Continuar";

    public VoBoPagadorDto() {
    }

    public Object getCuotaContratoFormalizacion() {
        return cuotaContratoFormalizacion;
    }

    public void setCuotaContratoFormalizacion(Object cuotaContratoFormalizacion) {
        this.cuotaContratoFormalizacion = cuotaContratoFormalizacion;
    }

    public String getCuotaVoBo() {
        return cuotaVoBo;
    }

    public void setCuotaVoBo(String cuotaVoBo) {
        this.cuotaVoBo = cuotaVoBo;
    }

    public String getValorMaximoTotalCredito() {
        return valorMaximoTotalCredito;
    }

    public void setValorMaximoTotalCredito(String valorMaximoTotalCredito) {
        this.valorMaximoTotalCredito = valorMaximoTotalCredito;
    }

    public String getCuotaRetanqueo() {
        return cuotaRetanqueo;
    }

    public void setCuotaRetanqueo(String cuotaRetanqueo) {
        this.cuotaRetanqueo = cuotaRetanqueo;
    }

    public String getValorCuotaRecoger() {
        return valorCuotaRecoger;
    }

    public void setValorCuotaRecoger(String valorCuotaRecoger) {
        this.valorCuotaRecoger = valorCuotaRecoger;
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
