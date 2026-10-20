package com.bbva.migracionstandingoffer.etapas.vobopagador;

import com.bbva.migracionstandingoffer.bonita.BonitaClient;
import com.bbva.migracionstandingoffer.bonita.FlowNodeDto;
import com.bbva.migracionstandingoffer.comun.MigracionConstantes;
import com.bbva.migracionstandingoffer.core.ContextoMigracion;
import com.bbva.migracionstandingoffer.core.EtapaMigracion;
import com.bbva.migracionstandingoffer.core.ResultadoEtapa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EtapaVoBoPagador implements EtapaMigracion {
    private static final Logger log = LoggerFactory.getLogger(EtapaVoBoPagador.class);

    private final BonitaClient bonitaClient;
    private final VoBoPagadorMapper mapper;

    public EtapaVoBoPagador(BonitaClient bonitaClient, VoBoPagadorMapper mapper) {
        this.bonitaClient = bonitaClient;
        this.mapper = mapper;
    }

    @Override
    public int getOrden() {
        return 4;
    }

    @Override
    public String getNombreEtapa() {
        return MigracionConstantes.ETAPA_VOBO_PAGADOR;
    }

    @Override
    public String getNombreTareaBonita() {
        return MigracionConstantes.TAREA_VOBO_PAGADOR;
    }

    @Override
    public boolean debeEjecutar(ContextoMigracion contexto) {
        return contexto.getCaseIdCM() != null && !contexto.getCaseIdCM().isEmpty();
    }

    @Override
    public ResultadoEtapa procesar(ContextoMigracion contexto) {
        long inicio = System.currentTimeMillis();
        try {
            String caseId = contexto.getCaseIdCM();
            log.info("[VOBO_PAGADOR] Esperando tarea '{}' para caseId={}", getNombreTareaBonita(), caseId);

            FlowNodeDto task = bonitaClient.esperarTarea(caseId, getNombreTareaBonita());

            VoBoPagadorDto dto = (VoBoPagadorDto) contexto.getContrato().get(MigracionConstantes.CLAVE_DATOS_VOBO_PAGADOR);
            Map<String, Object> payload = mapper.enriquecerContrato(contexto.getContrato(), dto);

            Map<String, Object> contratoActualizado = (Map<String, Object>) payload.get(MigracionConstantes.CLAVE_CONTRATO);
            contexto.fusionarContrato(contratoActualizado);

            log.info("[VOBO_PAGADOR] Ejecutando tarea taskId={} en Bonita CM", task.getId());
            bonitaClient.ejecutarTarea(task.getId(), payload);

            long duracion = System.currentTimeMillis() - inicio;
            return ResultadoEtapa.ok(getNombreEtapa(), task.getId(), "Tarea VoBo Pagador ejecutada exitosamente", duracion);
        } catch (Exception e) {
            long duracion = System.currentTimeMillis() - inicio;
            log.error("[VOBO_PAGADOR] Error en etapa VoBo Pagador: {}", e.getMessage(), e);
            return ResultadoEtapa.error(getNombreEtapa(), e.getMessage(), duracion);
        }
    }
}
