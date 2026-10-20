package com.bbva.migracionstandingoffer.flujo;

import com.bbva.migracionstandingoffer.comun.MigracionConstantes;
import com.bbva.migracionstandingoffer.config.MigracionProperties;
import com.bbva.migracionstandingoffer.core.ContextoMigracion;
import com.bbva.migracionstandingoffer.core.OrquestadorMigracion;
import com.bbva.migracionstandingoffer.core.ResultadoEtapa;
import com.bbva.migracionstandingoffer.etapas.radicacion.RadicacionMapper;
import com.bbva.migracionstandingoffer.modelo.CasoRadicacionDto;
import com.bbva.migracionstandingoffer.modelo.ScoringContratoDto;
import com.bbva.migracionstandingoffer.origen.StandingOfferEERepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class MigracionStandingOfferService {
    private static final Logger log = LoggerFactory.getLogger(MigracionStandingOfferService.class);

    private final OrquestadorMigracion orquestador;
    private final RadicacionMapper radicacionMapper;
    private final StandingOfferEERepository eeRepository;
    private final RelacionMigradosStandingOffer relacionMigrados;
    private final MigracionProperties props;

    private ExecutorService threadPool;

    public MigracionStandingOfferService(OrquestadorMigracion orquestador,
                                        RadicacionMapper radicacionMapper,
                                        StandingOfferEERepository eeRepository,
                                        RelacionMigradosStandingOffer relacionMigrados,
                                        MigracionProperties props) {
        this.orquestador = orquestador;
        this.radicacionMapper = radicacionMapper;
        this.eeRepository = eeRepository;
        this.relacionMigrados = relacionMigrados;
        this.props = props;
    }

    @PostConstruct
    public void init() {
        int hilos = props.getHilos() > 0 ? props.getHilos() : 8;
        log.info("Inicializando ThreadPool de migración con {} hilos concurrentes", hilos);
        this.threadPool = Executors.newFixedThreadPool(hilos);
    }

    @PreDestroy
    public void cleanup() {
        if (this.threadPool != null) {
            log.info("Cerrando ThreadPool de migración...");
            this.threadPool.shutdown();
        }
    }

    /**
     * Consulta un caso existente en la base de datos de Enterprise por su número de caso,
     * arma el nuevo contrato de radicación para Bonita Community y ejecuta la migración
     * hasta la etapa solicitada.
     */
    public ContextoMigracion migrarPorNumeroCasoEE(String casoEE, String etapaObjetivo) {
        log.info("[HILO: {}] Iniciando migración para caso Enterprise: {}", Thread.currentThread().getName(), casoEE);
        ContextoMigracion ctx = new ContextoMigracion(casoEE);
        ctx.setEtapaObjetivo(etapaObjetivo != null && !etapaObjetivo.trim().isEmpty() 
                ? etapaObjetivo.toUpperCase() 
                : MigracionConstantes.ETAPA_FORMALIZACION);

        // 1. Consultar caso en la base de datos Oracle de Enterprise
        CasoRadicacionDto datosEE = eeRepository.consultarCasoEE(casoEE);
        if (datosEE == null) {
            log.warn("Caso {} no encontrado en base de datos Enterprise.", casoEE);
            ctx.setEstadoFinal(MigracionConstantes.ESTADO_NO_ENCONTRADO);
            ctx.agregarResultado(ResultadoEtapa.error("CONSULTA_EE", "Caso " + casoEE + " no existe en STF_CASE", 0));
            return ctx;
        }

        // 2. Mapear y armar el nuevo contrato para Bonita Community
        Map<String, Object> contratoNuevo = (Map<String, Object>) radicacionMapper.construirContrato(datosEE).get(MigracionConstantes.CLAVE_CONTRATO);
        ctx.fusionarContrato(contratoNuevo);

        // 3. Ejecutar pipeline en Bonita Community (Radicación -> etapas subsiguientes)
        ContextoMigracion res = orquestador.ejecutar(ctx);

        // 4. Registrar relación casoEE -> caseIdCM
        if (res.getCaseIdCM() != null) {
            relacionMigrados.registrar(casoEE, res.getCaseIdCM(), res.getEstadoFinal(), "Migrado exitosamente");
        }

        return res;
    }

    /**
     * Procesa un lote de casos de Enterprise EN PARALELO utilizando el pool de hilos configurable.
     * Evita cuellos de botella al distribuir los casos simultáneamente.
     */
    public List<ContextoMigracion> migrarLoteCasosEE(List<String> casosEE, String etapaObjetivo) {
        log.info("Iniciando migración concurrente de lote con {} casos usando {} hilos", casosEE.size(), props.getHilos());
        List<ContextoMigracion> resultados = new ArrayList<>();
        List<Future<ContextoMigracion>> futures = new ArrayList<>();

        for (String casoEE : casosEE) {
            final String casoLimpio = casoEE.trim();
            futures.add(threadPool.submit(() -> migrarPorNumeroCasoEE(casoLimpio, etapaObjetivo)));
        }

        for (int i = 0; i < futures.size(); i++) {
            String casoOriginal = casosEE.get(i);
            try {
                ContextoMigracion res = futures.get(i).get();
                resultados.add(res);
            } catch (Exception e) {
                log.error("Error en ejecución concurrente de caso {}: {}", casoOriginal, e.getMessage(), e);
                ContextoMigracion errCtx = new ContextoMigracion(casoOriginal);
                errCtx.setEstadoFinal(MigracionConstantes.ESTADO_ERROR);
                errCtx.agregarResultado(ResultadoEtapa.error("CONCURRENCIA", e.getMessage(), 0));
                resultados.add(errCtx);
            }
        }
        return resultados;
    }

    /**
     * Método directo para cuando se pasa el payload completo manualmente.
     */
    public ContextoMigracion migrarCaso(String casoNegocio,
                                       CasoRadicacionDto precarga,
                                       ScoringContratoDto scoring,
                                       com.bbva.migracionstandingoffer.etapas.evaluacionsancion.EvaluacionSancionDto evaluacionSancion,
                                       com.bbva.migracionstandingoffer.etapas.vobopagador.VoBoPagadorDto voBoPagador,
                                       com.bbva.migracionstandingoffer.etapas.formalizacion.FormalizacionDto formalizacion,
                                       String etapaObjetivo) {
        String idCaso = casoNegocio != null ? casoNegocio :
                (precarga != null ? precarga.getNumeroDocumento() : "SIN_ID");

        ContextoMigracion ctx = new ContextoMigracion(idCaso);
        ctx.setEtapaObjetivo(etapaObjetivo != null && !etapaObjetivo.trim().isEmpty() 
                ? etapaObjetivo.toUpperCase() 
                : MigracionConstantes.ETAPA_FORMALIZACION);

        if (precarga != null) {
            Map<String, Object> base = (Map<String, Object>) radicacionMapper.construirContrato(precarga).get(MigracionConstantes.CLAVE_CONTRATO);
            ctx.fusionarContrato(base);
        }

        if (scoring != null) {
            ctx.agregarDatoContrato(MigracionConstantes.CLAVE_DATOS_SCORING, scoring);
        }

        if (evaluacionSancion != null) {
            ctx.agregarDatoContrato(MigracionConstantes.CLAVE_DATOS_EVALUACION_SANCION, evaluacionSancion);
        }

        if (voBoPagador != null) {
            ctx.agregarDatoContrato(MigracionConstantes.CLAVE_DATOS_VOBO_PAGADOR, voBoPagador);
        }

        if (formalizacion != null) {
            ctx.agregarDatoContrato(MigracionConstantes.CLAVE_DATOS_FORMALIZACION, formalizacion);
        }

        ContextoMigracion res = orquestador.ejecutar(ctx);
        if (res.getCaseIdCM() != null) {
            relacionMigrados.registrar(idCaso, res.getCaseIdCM(), res.getEstadoFinal(), "Migrado directo");
        }
        return res;
    }
}
