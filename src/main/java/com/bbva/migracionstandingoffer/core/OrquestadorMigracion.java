package com.bbva.migracionstandingoffer.core;

import com.bbva.migracionstandingoffer.comun.MigracionConstantes;
import com.bbva.migracionstandingoffer.config.MigracionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrquestadorMigracion {
    private static final Logger log = LoggerFactory.getLogger(OrquestadorMigracion.class);

    private final List<EtapaMigracion> etapas;
    private final MigracionProperties props;

    public OrquestadorMigracion(List<EtapaMigracion> etapas, MigracionProperties props) {
        this.props = props;
        this.etapas = etapas.stream()
                .sorted(Comparator.comparingInt(EtapaMigracion::getOrden))
                .collect(Collectors.toList());
        log.info("Orquestador de Migración inicializado con {} etapas en orden: {}",
                this.etapas.size(),
                this.etapas.stream().map(EtapaMigracion::getNombreEtapa).collect(Collectors.joining(" -> ")));
    }

    public ContextoMigracion ejecutar(ContextoMigracion contexto) {
        log.info("Iniciando orquestación de casoNegocio={} hacia CM", contexto.getCasoNegocio());
        int maxIntentos = props.getReintentos() != null ? props.getReintentos().getMaxIntentos() : 3;
        long esperaMs = props.getReintentos() != null ? props.getReintentos().getEsperaMs() : 3000L;

        for (EtapaMigracion etapa : etapas) {
            if (!etapa.debeEjecutar(contexto)) {
                log.info("Omitiendo etapa '{}' para caso={}", etapa.getNombreEtapa(), contexto.getCasoNegocio());
                continue;
            }

            log.info("Ejecutando etapa '{}' (orden={}) para casoNegocio={}",
                    etapa.getNombreEtapa(), etapa.getOrden(), contexto.getCasoNegocio());

            ResultadoEtapa res = null;
            int intento = 0;

            // Ciclo de reintentos ante fallas transitorias
            while (intento < maxIntentos) {
                intento++;
                res = etapa.procesar(contexto);
                if (res.isExitoso()) {
                    break;
                }

                if (intento < maxIntentos) {
                    log.warn("Etapa '{}' falló para caso={} (intento {}/{}): {}. Reintentando en {}ms...",
                            etapa.getNombreEtapa(), contexto.getCasoNegocio(), intento, maxIntentos, res.getMensaje(), esperaMs);
                    try {
                        Thread.sleep(esperaMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            contexto.agregarResultado(res);

            if (res != null && !res.isExitoso()) {
                log.error("Fallo definitivo en etapa '{}' para caso={} tras {} intentos: {}",
                        etapa.getNombreEtapa(), contexto.getCasoNegocio(), intento, res.getMensaje());
                contexto.setEstadoFinal(MigracionConstantes.ESTADO_ERROR);
                return contexto;
            }

            // Si se alcanzó la etapa objetivo
            if (etapa.getNombreEtapa().equalsIgnoreCase(contexto.getEtapaObjetivo())) {
                log.info("Caso {} alcanzó su etapa objetivo '{}'. Finalizando ejecución.",
                        contexto.getCasoNegocio(), contexto.getEtapaObjetivo());
                contexto.setEstadoFinal(MigracionConstantes.ESTADO_OK);
                return contexto;
            }
        }

        contexto.setEstadoFinal(MigracionConstantes.ESTADO_OK);
        return contexto;
    }
}
