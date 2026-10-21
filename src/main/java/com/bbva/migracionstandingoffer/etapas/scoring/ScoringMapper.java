package com.bbva.migracionstandingoffer.etapas.scoring;

import com.bbva.migracionstandingoffer.modelo.ScoringContratoDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ScoringMapper {

    /**
     * Fusiona los datos base del contrato con los valores específicos de la etapa Scoring
     * tal como se capturó en scoring.har.
     * Si scoring es null, aplica los valores aprobatorios por defecto para que la tarea avance.
     */
    public Map<String, Object> enriquecerContrato(Map<String, Object> contratoBase, ScoringContratoDto scoring) {
        Map<String, Object> data = aplanarContrato(contratoBase);

        // Valores de Scoring
        data.put("techo", scoring != null && scoring.getTecho() != null ? scoring.getTecho() : "12000000");
        data.put("buro", scoring != null && scoring.getBuro() != null ? scoring.getBuro() : "213");
        data.put("contrato", scoring != null && scoring.getContrato() != null ? scoring.getContrato() : "00132443242342343234");

        // Dictamen Scoring
        if (scoring != null && scoring.getDictamen() != null) {
            data.put("dictamen", scoring.getDictamen());
        } else {
            Map<String, Object> dictamenDefecto = new HashMap<>();
            dictamenDefecto.put("dictamen", "Continuar");
            dictamenDefecto.put("id", 1);
            dictamenDefecto.put("esTipologia", true);
            data.put("dictamen", dictamenDefecto);
        }

        data.put("dictamenScoring", scoring != null && scoring.getDictamenScoring() != null ? scoring.getDictamenScoring() : 11);

        // Tipologías y Casuísticas
        if (scoring != null && scoring.getTipologiasCasuisticas() != null) {
            data.put("tipologiasCasuisticas", scoring.getTipologiasCasuisticas());
        } else {
            Map<String, Object> tc = new HashMap<>();
            Map<String, Object> tip = new HashMap<>();
            tip.put("nombre", "Novedad Datos Formulario");
            tip.put("id", 1013);
            Map<String, Object> cas = new HashMap<>();
            cas.put("nombre", "Avanza Vía Excepción");
            cas.put("id", 63);
            tc.put("tipologias", tip);
            tc.put("casuisticas", cas);
            data.put("tipologiasCasuisticas", Collections.singletonList(tc));
        }

        data.put("banco", scoring != null ? scoring.getBanco() : null);
        data.put("numeroObligacion", scoring != null ? scoring.getNumeroObligacion() : null);
        data.put("valor", scoring != null ? scoring.getValor() : null);
        data.put("tipologiaScoring", scoring != null ? scoring.getTipologiaScoring() : null);
        data.put("archivos", scoring != null && scoring.getArchivos() != null ? scoring.getArchivos() : Collections.emptyList());
        data.put("fechaInicio", scoring != null && scoring.getFechaInicio() != null ? scoring.getFechaInicio() : System.currentTimeMillis());
        data.put("observaciones", scoring != null && scoring.getObservaciones() != null ? scoring.getObservaciones() : "ok");
        
        // Campo obligatorio de transición de Bonita
        data.put("proximoPaso", scoring != null && scoring.getProximoPaso() != null ? scoring.getProximoPaso() : "Continuar");

        Map<String, Object> root = new HashMap<>();
        root.put("contrato", data);
        return root;
    }

    /**
     * En las tareas humanas (Scoring, Evaluación, VoBo, Formalización), el formulario de Bonita
     * maneja los datos en formato plano (clave: valor), aplanando cualquier { valor: "..." } que viniera de radicación.
     */
    private Map<String, Object> aplanarContrato(Map<String, Object> contratoBase) {
        Map<String, Object> plano = new LinkedHashMap<>();
        if (contratoBase == null) {
            return plano;
        }

        for (Map.Entry<String, Object> entry : contratoBase.entrySet()) {
            Object valor = entry.getValue();
            if (valor instanceof Map) {
                Map<?, ?> mapValor = (Map<?, ?>) valor;
                if (mapValor.containsKey("valor") && mapValor.size() <= 2) {
                    plano.put(entry.getKey(), mapValor.get("valor"));
                    continue;
                }
            }
            plano.put(entry.getKey(), valor);
        }
        return plano;
    }
}
