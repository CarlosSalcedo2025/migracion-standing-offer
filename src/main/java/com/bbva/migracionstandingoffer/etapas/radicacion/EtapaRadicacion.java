package com.bbva.migracionstandingoffer.etapas.radicacion;

import com.bbva.migracionstandingoffer.bonita.BonitaClient;
import com.bbva.migracionstandingoffer.comun.MigracionConstantes;
import com.bbva.migracionstandingoffer.config.BonitaProperties;
import com.bbva.migracionstandingoffer.core.ContextoMigracion;
import com.bbva.migracionstandingoffer.core.EtapaMigracion;
import com.bbva.migracionstandingoffer.core.ResultadoEtapa;
import com.bbva.migracionstandingoffer.modelo.CasoRadicacionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EtapaRadicacion implements EtapaMigracion {
    private static final Logger log = LoggerFactory.getLogger(EtapaRadicacion.class);

    private final BonitaClient bonitaClient;
    private final BonitaProperties properties;
    private final RadicacionMapper mapper;

    public EtapaRadicacion(BonitaClient bonitaClient, BonitaProperties properties, RadicacionMapper mapper) {
        this.bonitaClient = bonitaClient;
        this.properties = properties;
        this.mapper = mapper;
    }

    @Override
    public int getOrden() {
        return 1;
    }

    @Override
    public String getNombreEtapa() {
        return MigracionConstantes.ETAPA_RADICACION;
    }

    @Override
    public String getNombreTareaBonita() {
        return null; // Es el evento de inicio / instanciación del proceso
    }

    @Override
    public boolean debeEjecutar(ContextoMigracion contexto) {
        // Solo ejecuta radicación si el caso aún no tiene caseId en CM
        return contexto.getCaseIdCM() == null || contexto.getCaseIdCM().isEmpty();
    }

    @Override
    public ResultadoEtapa procesar(ContextoMigracion contexto) {
        long inicio = System.currentTimeMillis();
        try {
            log.info("[RADICACION] Instanciando proceso en Bonita CM para casoNegocio={}", contexto.getCasoNegocio());
            String processId = bonitaClient.obtenerProcesoId(properties.getProcessName(), properties.getProcessVersion());

            // Si el contrato ya tiene datos precargados, los envía en el formato esperado
            Map<String, Object> payload;
            if (contexto.getContrato() != null && !contexto.getContrato().containsKey("consecutivo")) {
                contexto.getContrato().put("consecutivo", "1");
            }

            payload = wrapContrato(contexto.getContrato());

            String caseId = bonitaClient.instanciar(processId, payload);
            contexto.setCaseIdCM(caseId);

            long duracion = System.currentTimeMillis() - inicio;
            return ResultadoEtapa.ok(getNombreEtapa(), null, "Caso instanciado exitosamente con caseId=" + caseId, duracion);
        } catch (Exception e) {
            long duracion = System.currentTimeMillis() - inicio;
            log.error("[RADICACION] Error al instanciar caso: {}", e.getMessage(), e);
            return ResultadoEtapa.error(getNombreEtapa(), e.getMessage(), duracion);
        }
    }

    private Map<String, Object> wrapContrato(Map<String, Object> data) {
        java.util.Map<String, Object> root = new java.util.HashMap<>();
        root.put("contrato", data);
        return root;
    }
}
