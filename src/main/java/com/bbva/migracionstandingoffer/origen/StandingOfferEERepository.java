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
                CasoRadicacionDto dto = new CasoRadicacionDto();
                dto.setNombreCliente(rs.getString("NOMBRECLIENTE"));
                dto.setCreRetanquearLibranza(rs.getString("NUMCONTRRT1"));
                dto.setOficinaSegmentacion(rs.getString("OFICINASEG"));
                dto.setCanal(rs.getString("CANALPRESEN"));
                return dto;
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

        CasoRadicacionDto dto = new CasoRadicacionDto();
        dto.setNombreCliente(extraerValor(contrato, "nombreCliente"));
        dto.setTipoDocumento(extraerValor(contrato, "tipoDocumento"));
        dto.setNumeroDocumento(extraerValor(contrato, "numeroDocumento"));
        dto.setDigitoVerificacion(extraerValor(contrato, "digitoVerificacion"));
        dto.setCorreoCliente(extraerValor(contrato, "correoCliente"));
        dto.setNumeroCelular(extraerValor(contrato, "numeroCelular"));
        dto.setNumeroAltamiraCliente(extraerValor(contrato, "numeroAltamiraCliente"));
        dto.setFechaVigenciaCampana(extraerValor(contrato, "fechaVigenciaCampana"));
        dto.setFechaHoraSolicitud(extraerValor(contrato, "fechaHoraSolicitud"));
        dto.setNumeroProducto(extraerValor(contrato, "numeroProducto"));
        dto.setSubProducto(extraerValor(contrato, "subProducto"));
        dto.setCodigoSubproducto(extraerValor(contrato, "codigoSubproducto"));
        dto.setMarcaOneClic(extraerValor(contrato, "marcaOneClic"));
        dto.setValorNuevaOperacion(extraerValor(contrato, "valorNuevaOperacion"));
        dto.setValorCuotaNuevaOperacion(extraerValor(contrato, "valorCuotaNuevaOperacion"));
        dto.setTasaCreditoNuevo(extraerValor(contrato, "tasaCreditoNuevo"));
        dto.setPlazo(extraerValor(contrato, "plazo"));
        dto.setCuentaAbono(extraerValor(contrato, "cuentaAbono"));
        dto.setCanal(extraerValor(contrato, "canal"));
        dto.setOficinaSegmentacion(extraerValor(contrato, "oficinaSegmentacion"));
        dto.setCreRetanquearLibranza(extraerValor(contrato, "creRetanquearLibranza"));
        dto.setCreRetanquearConsumo(extraerValor(contrato, "creRetanquearConsumo"));
        dto.setCreRetanquearHipoteca(extraerValor(contrato, "creRetanquearHipoteca"));
        dto.setCreRetanquearVehiculo(extraerValor(contrato, "creRetanquearVehiculo"));
        dto.setCreRetanquearLeasing(extraerValor(contrato, "creRetanquearLeasing"));
        dto.setCodigoConvenio(extraerValor(contrato, "codigoConvenio"));
        dto.setNombreConvenio(extraerValor(contrato, "nombreConvenio"));
        dto.setTipoConvenio(extraerValor(contrato, "tipoConvenio"));
        dto.setNumeroConsecutivo(extraerValor(contrato, "numeroConsecutivo"));
        dto.setDireccionIp(extraerValor(contrato, "direccionIp"));
        dto.setDescripcion(extraerValor(contrato, "descripcion"));
        return dto;
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
