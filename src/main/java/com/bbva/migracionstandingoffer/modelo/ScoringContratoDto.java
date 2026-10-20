package com.bbva.migracionstandingoffer.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private List<Object> archivos = Collections.emptyList();
    private String banco;
    private String numeroObligacion;
    private String valor;
    private String tipologiaScoring;
}
