package com.bbva.migracionstandingoffer.etapas.evaluacionsancion;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EvaluacionSancionMapper {

    /**
     * Enriquece el contrato acumulado con los datos de Evaluación y Sanción
     * tal como se extrajeron de Evaluacion y sancion.har.
     */
    public Map<String, Object> enriquecerContrato(Map<String, Object> contratoAcumulado, EvaluacionSancionDto dto) {
        Map<String, Object> data = new LinkedHashMap<>(contratoAcumulado);

        data.put("producto", dto != null && dto.getProducto() != null ? dto.getProducto() : "LIBRANZA");
        data.put("correccionScoring", dto != null && dto.getCorreccionScoring() != null ? dto.getCorreccionScoring() : "No");
        data.put("garantia", dto != null && dto.getGarantia() != null ? dto.getGarantia() : "No");
        data.put("instancia", dto != null ? dto.getInstancia() : null);
        data.put("acta", dto != null ? dto.getActa() : null);
        data.put("fechaActa", dto != null ? dto.getFechaActa() : null);

        // Dictamen en Evaluación y Sanción
        if (dto != null && dto.getDictamen() != null) {
            data.put("dictamen", dto.getDictamen());
        } else {
            Map<String, Object> dictamen = new HashMap<>();
            dictamen.put("dictamen", "Continuar");
            dictamen.put("id", 11);
            dictamen.put("esTipologia", true);
            data.put("dictamen", dictamen);
        }

        // Tipologías y Casuísticas: Decisión Inicial -> Aprobado En Condiciones Propuestas
        if (dto != null && dto.getTipologiasCasuisticas() != null) {
            data.put("tipologiasCasuisticas", dto.getTipologiasCasuisticas());
        } else {
            Map<String, Object> item = new HashMap<>();
            Map<String, Object> tip = new HashMap<>();
            tip.put("nombre", "Decision Inicial");
            tip.put("id", 26);
            Map<String, Object> cas = new HashMap<>();
            cas.put("nombre", "Aprobado En Condiciones Propuestas");
            cas.put("id", 298);
            item.put("tipologias", tip);
            item.put("casuisticas", cas);
            data.put("tipologiasCasuisticas", Collections.singletonList(item));
        }

        data.put("tipologiasCasuistica", Collections.emptyList());
        data.put("observaciones", dto != null && dto.getObservaciones() != null ? dto.getObservaciones() : "ok");
        data.put("proximoPaso", dto != null && dto.getProximoPaso() != null ? dto.getProximoPaso() : "Continuar");

        Map<String, Object> root = new HashMap<>();
        root.put("contrato", data);
        return root;
    }
}
