package com.bbva.migracionstandingoffer.etapas.evaluacionsancion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluacionSancionDto {
    @Builder.Default
    private String producto = "LIBRANZA";

    @Builder.Default
    private String correccionScoring = "No";

    @Builder.Default
    private String garantia = "No";

    private String instancia;
    private String acta;
    private String fechaActa;
    private String montoAprobado;

    private Map<String, Object> dictamen;
    private List<Map<String, Object>> tipologiasCasuisticas;

    @Builder.Default
    private String observaciones = "ok";

    @Builder.Default
    private String proximoPaso = "Continuar";
}
