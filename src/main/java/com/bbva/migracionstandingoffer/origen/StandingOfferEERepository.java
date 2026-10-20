package com.bbva.migracionstandingoffer.origen;

import com.bbva.migracionstandingoffer.modelo.CasoRadicacionDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class StandingOfferEERepository {
    private static final Logger log = LoggerFactory.getLogger(StandingOfferEERepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public StandingOfferEERepository(@org.springframework.beans.factory.annotation.Qualifier("enterpriseJdbcTemplate") JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Consulta un caso existente en la base de datos de Enterprise (tabla STF_CASE).
     * Soporta búsqueda por NROCASO (Bonita Enterprise ID) o por ID interno.
     */
    public CasoRadicacionDto consultarCasoEE(String nroCaso) {
        String sql = "SELECT ID, NROCASO, JSON, NOMBRECLIENTE, NUMCONTRRT1, OFICINASEG, CANALPRESEN " +
                     "FROM BBVA_BI.STF_CASE " +
                     "WHERE NROCASO = ? OR TO_CHAR(ID) = ? " +
                     "ORDER BY ID DESC";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{nroCaso, nroCaso}, (rs, rowNum) -> {
                String jsonClob = rs.getString("JSON");
                if (jsonClob != null && !jsonClob.trim().isEmpty()) {
                    try {
                        return mapearDesdeJsonClob(jsonClob);
                    } catch (Exception e) {
                        log.warn("No se pudo parsear el CLOB JSON del caso {}. Se usará mapeo por columnas.", nroCaso, e);
                    }
                }

                // Mapeo fallback por columnas básicas
                return CasoRadicacionDto.builder()
                        .nombreCliente(rs.getString("NOMBRECLIENTE"))
                        .creRetanquearLibranza(rs.getString("NUMCONTRRT1"))
                        .oficinaSegmentacion(rs.getString("OFICINASEG"))
                        .canal(rs.getString("CANALPRESEN"))
                        .build();
            });
        } catch (EmptyResultDataAccessException e) {
            log.warn("Caso {} no fue encontrado en BBVA_BI.STF_CASE", nroCaso);
            return null;
        } catch (Exception e) {
            log.error("Error consultando caso {} en BBVA_BI.STF_CASE: {}", nroCaso, e.getMessage());
            throw new RuntimeException("Error en base de datos al consultar caso " + nroCaso, e);
        }
    }

    /**
     * Extrae los valores del JSON guardado originalmente en STF_CASE.
     * En Standing Offer, el JSON guarda la estructura:
     * contrato -> { campo: { valor: "...", descripcion: "..." } }
     * o bien campo: "..." plano.
     */
    private CasoRadicacionDto mapearDesdeJsonClob(String jsonText) throws Exception {
        JsonNode root = objectMapper.readTree(jsonText);
        JsonNode contrato = root.has("contrato") ? root.get("contrato") : root;

        return CasoRadicacionDto.builder()
                .nombreCliente(extraerValor(contrato, "nombreCliente"))
                .tipoDocumento(extraerValor(contrato, "tipoDocumento"))
                .numeroDocumento(extraerValor(contrato, "numeroDocumento"))
                .digitoVerificacion(extraerValor(contrato, "digitoVerificacion"))
                .correoCliente(extraerValor(contrato, "correoCliente"))
                .numeroCelular(extraerValor(contrato, "numeroCelular"))
                .numeroAltamiraCliente(extraerValor(contrato, "numeroAltamiraCliente"))
                .fechaVigenciaCampana(extraerValor(contrato, "fechaVigenciaCampana"))
                .fechaHoraSolicitud(extraerValor(contrato, "fechaHoraSolicitud"))
                .numeroProducto(extraerValor(contrato, "numeroProducto"))
                .subProducto(extraerValor(contrato, "subProducto"))
                .codigoSubproducto(extraerValor(contrato, "codigoSubproducto"))
                .marcaOneClic(extraerValor(contrato, "marcaOneClic"))
                .valorNuevaOperacion(extraerValor(contrato, "valorNuevaOperacion"))
                .valorCuotaNuevaOperacion(extraerValor(contrato, "valorCuotaNuevaOperacion"))
                .tasaCreditoNuevo(extraerValor(contrato, "tasaCreditoNuevo"))
                .plazo(extraerValor(contrato, "plazo"))
                .cuentaAbono(extraerValor(contrato, "cuentaAbono"))
                .canal(extraerValor(contrato, "canal"))
                .oficinaSegmentacion(extraerValor(contrato, "oficinaSegmentacion"))
                .creRetanquearLibranza(extraerValor(contrato, "creRetanquearLibranza"))
                .creRetanquearConsumo(extraerValor(contrato, "creRetanquearConsumo"))
                .creRetanquearHipoteca(extraerValor(contrato, "creRetanquearHipoteca"))
                .creRetanquearVehiculo(extraerValor(contrato, "creRetanquearVehiculo"))
                .creRetanquearLeasing(extraerValor(contrato, "creRetanquearLeasing"))
                .codigoConvenio(extraerValor(contrato, "codigoConvenio"))
                .nombreConvenio(extraerValor(contrato, "nombreConvenio"))
                .tipoConvenio(extraerValor(contrato, "tipoConvenio"))
                .numeroConsecutivo(extraerValor(contrato, "numeroConsecutivo"))
                .direccionIp(extraerValor(contrato, "direccionIp"))
                .descripcion(extraerValor(contrato, "descripcion"))
                .build();
    }

    private String extraerValor(JsonNode nodo, String campo) {
        if (nodo == null || !nodo.has(campo)) {
            return "";
        }
        JsonNode campoNode = nodo.get(campo);
        if (campoNode.isObject() && campoNode.has("valor")) {
            return campoNode.get("valor").asText("");
        }
        return campoNode.asText("");
    }
}
