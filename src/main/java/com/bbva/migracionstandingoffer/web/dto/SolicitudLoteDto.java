package com.bbva.migracionstandingoffer.web.dto;

import java.util.List;

public class SolicitudLoteDto {

    private List<String> casos;
    private String etapaObjetivo;

    public SolicitudLoteDto() {
    }

    public List<String> getCasos() {
        return casos;
    }

    public void setCasos(List<String> casos) {
        this.casos = casos;
    }

    public String getEtapaObjetivo() {
        return etapaObjetivo;
    }

    public void setEtapaObjetivo(String etapaObjetivo) {
        this.etapaObjetivo = etapaObjetivo;
    }
}
