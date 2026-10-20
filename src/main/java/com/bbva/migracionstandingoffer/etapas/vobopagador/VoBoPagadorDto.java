package com.bbva.migracionstandingoffer.etapas.vobopagador;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoBoPagadorDto {
    @Builder.Default
    private Object cuotaContratoFormalizacion = 30;

    @Builder.Default
    private String cuotaVoBo = "30000";

    @Builder.Default
    private String valorMaximoTotalCredito = "61,800,000.00";

    @Builder.Default
    private String cuotaRetanqueo = "30";

    @Builder.Default
    private String valorCuotaRecoger = "30";

    @Builder.Default
    private String observaciones = "ok";

    @Builder.Default
    private String proximoPaso = "Continuar";
}
