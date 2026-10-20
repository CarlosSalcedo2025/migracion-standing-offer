package com.bbva.migracionstandingoffer.etapas.formalizacion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormalizacionDto {
    @Builder.Default
    private String cuotaContratoFormalizacion = "30";

    @Builder.Default
    private String valorMaximoTotalCredito = "1,520,137,860.00.00";

    @Builder.Default
    private List<Object> archivos = Collections.emptyList();

    @Builder.Default
    private String observaciones = "ok";

    @Builder.Default
    private String proximoPaso = "Continuar";
}
