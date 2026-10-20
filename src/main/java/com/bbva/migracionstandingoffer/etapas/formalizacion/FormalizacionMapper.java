package com.bbva.migracionstandingoffer.etapas.formalizacion;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FormalizacionMapper {

    /**
     * Enriquece el contrato con los datos finales de Formalización
     * según Formalizacion.har.
     */
    public Map<String, Object> enriquecerContrato(Map<String, Object> contratoAcumulado, String taskId, FormalizacionDto dto) {
        Map<String, Object> data = new LinkedHashMap<>(contratoAcumulado);

        data.put("id", taskId);
        data.put("cuotaContratoFormalizacion", dto != null && dto.getCuotaContratoFormalizacion() != null ? dto.getCuotaContratoFormalizacion() : "30");
        data.put("valorMaximoTotalCredito", dto != null && dto.getValorMaximoTotalCredito() != null ? dto.getValorMaximoTotalCredito() : "1,520,137,860.00.00");
        data.put("archivos", dto != null && dto.getArchivos() != null ? dto.getArchivos() : Collections.emptyList());

        data.put("observaciones", dto != null && dto.getObservaciones() != null ? dto.getObservaciones() : "ok");
        data.put("proximoPaso", dto != null && dto.getProximoPaso() != null ? dto.getProximoPaso() : "Continuar");

        Map<String, Object> root = new HashMap<>();
        root.put("contrato", data);
        return root;
    }
}
