package com.bbva.migracionstandingoffer.etapas.scoring;

import com.bbva.migracionstandingoffer.modelo.ScoringContratoDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ScoringMapper {

    /**
     * Fusiona los datos base del contrato (precarga) con los valores específicos de la etapa Scoring
     * tal como se capturó en scoring.har.
     */
    public Map<String, Object> enriquecerContrato(Map<String, Object> contratoBase, ScoringContratoDto scoring) {
        Map<String, Object> data = new LinkedHashMap<>(contratoBase);

        if (scoring != null) {
            data.put("techo", safe(scoring.getTecho(), "12000000"));
            data.put("buro", safe(scoring.getBuro(), "213"));
            data.put("contrato", safe(scoring.getContrato())); // Nuevo Contrato

            if (scoring.getDictamen() != null) {
                data.put("dictamen", scoring.getDictamen());
            } else {
                Map<String, Object> dictamenDefecto = new HashMap<>();
                dictamenDefecto.put("dictamen", "Continuar");
                dictamenDefecto.put("id", 1);
                dictamenDefecto.put("esTipologia", true);
                data.put("dictamen", dictamenDefecto);
            }

            data.put("dictamenScoring", scoring.getDictamenScoring() != null ? scoring.getDictamenScoring() : 11);

            if (scoring.getTipologiasCasuisticas() != null) {
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

            data.put("banco", scoring.getBanco());
            data.put("numeroObligacion", scoring.getNumeroObligacion());
            data.put("valor", scoring.getValor());
            data.put("tipologiaScoring", scoring.getTipologiaScoring());
            data.put("archivos", scoring.getArchivos() != null ? scoring.getArchivos() : Collections.emptyList());
            data.put("fechaInicio", scoring.getFechaInicio() != null ? scoring.getFechaInicio() : System.currentTimeMillis());
            data.put("observaciones", safe(scoring.getObservaciones(), "ok"));
            data.put("proximoPaso", safe(scoring.getProximoPaso(), "Continuar"));
        }

        Map<String, Object> root = new HashMap<>();
        root.put("contrato", data);
        return root;
    }

    private String safe(String s) {
        return s != null ? s : "";
    }

    private String safe(String s, String def) {
        return (s != null && !s.trim().isEmpty()) ? s : def;
    }
}
