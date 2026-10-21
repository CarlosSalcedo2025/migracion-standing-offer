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

    private final JdbcTemplate enterpriseJdbcTemplate;
    private final JdbcTemplate bonitaBdmJdbcTemplate;
    private final ObjectMapper objectMapper;

    public StandingOfferEERepository(@org.springframework.beans.factory.annotation.Qualifier("enterpriseJdbcTemplate") JdbcTemplate enterpriseJdbcTemplate,
                                     @org.springframework.beans.factory.annotation.Qualifier("bonitaBdmJdbcTemplate") JdbcTemplate bonitaBdmJdbcTemplate,
                                     ObjectMapper objectMapper) {
        this.enterpriseJdbcTemplate = enterpriseJdbcTemplate;
        this.bonitaBdmJdbcTemplate = bonitaBdmJdbcTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Consulta un caso existente. Soporta:
     * 1. Caso de negocio que inicia por 3000xxxx (consultando BONITA7BDM.STANDINGOFFERDM).
     * 2. Caso de cargue/staging por NROCASO o ID (consultando BBVA_BI.STF_CASE).
     */
    public CasoRadicacionDto consultarCasoEE(String nroCaso) {
        if (nroCaso == null || nroCaso.trim().isEmpty()) {
            return null;
        }

        String identificador = nroCaso.trim();

        // Si empieza por 3000 o se desea consultar en BDM primero
        if (identificador.startsWith("3000")) {
            CasoRadicacionDto desdeBdm = consultarEnBonitaBdm(identificador);
            if (desdeBdm != null) {
                return desdeBdm;
            }
        }

        // Consulta en BBVA_BI.STF_CASE
        CasoRadicacionDto desdeStfCase = consultarEnStfCase(identificador);
        if (desdeStfCase != null) {
            return desdeStfCase;
        }

        // Si no se encontró en STF_CASE pero no empezaba por 3000, intentar en BDM como fallback
        if (!identificador.startsWith("3000")) {
            return consultarEnBonitaBdm(identificador);
        }

        return null;
    }

    private CasoRadicacionDto consultarEnStfCase(String nroCaso) {
        String sql = "SELECT ID, NROCASO, JSON, NOMBRECLIENTE, NUMCONTRRT1, OFICINASEG, CANALPRESEN " +
                     "FROM BBVA_BI.STF_CASE " +
                     "WHERE NROCASO = ? OR TO_CHAR(ID) = ? " +
                     "ORDER BY ID DESC";

        try {
            return enterpriseJdbcTemplate.queryForObject(sql, new Object[]{nroCaso, nroCaso}, (rs, rowNum) -> {
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
            log.info("Caso {} no fue encontrado en BBVA_BI.STF_CASE", nroCaso);
            return null;
        } catch (Exception e) {
            log.warn("Error consultando caso {} en BBVA_BI.STF_CASE: {}", nroCaso, e.getMessage());
            return null;
        }
    }

    private CasoRadicacionDto consultarEnBonitaBdm(String noCaso) {
        String sql = "SELECT NOCASO, NOMBRECLIENTE, NUMERODOCUMENTO, TIPODOCUMENTO, " +
                     "CORREOCLIENTE, NUMEROCELULAR, NUMEROALTAMIRACLIENTE, FECHAVIGENCIACAMPANA, " +
                     "FECHAHORASOLICITUD, NUMEROPRODUCTO, SUBPRODUCTO, CODIGOSUBPRODUCTO, " +
                     "MARCAONECLIC, VALORNUEVAOPERACION, VALORCUOTANUEVAOPERACION, TASACREDITONUEVO, " +
                     "PLAZO, CUENTAABONO, CANAL, OFICINASEGMENTACION, CRERETANQUEARLIBRANZA, " +
                     "CODIGOCONVENIO, NOMBRECONVENIO, TIPOCONVENIO, NUMEROCONSECUTIVO, DIRECCIONIP, DESCRIPCION " +
                     "FROM BONITA7BDM.STANDINGOFFERDM " +
                     "WHERE NOCASO = ? AND ROWNUM = 1";

        try {
            return bonitaBdmJdbcTemplate.queryForObject(sql, new Object[]{noCaso}, (rs, rowNum) -> {
                CasoRadicacionDto dto = new CasoRadicacionDto();
                dto.setConsecutivo("00001");
                dto.setNombreCliente(rs.getString("NOMBRECLIENTE"));
                dto.setNumeroDocumento(rs.getString("NUMERODOCUMENTO"));
                dto.setTipoDocumento(rs.getString("TIPODOCUMENTO") != null ? rs.getString("TIPODOCUMENTO") : "1");
                dto.setDigitoVerificacion("0");
                dto.setCorreoCliente(rs.getString("CORREOCLIENTE"));
                dto.setNumeroCelular(rs.getString("NUMEROCELULAR"));
                dto.setNumeroAltamiraCliente(rs.getString("NUMEROALTAMIRACLIENTE"));
                dto.setFechaVigenciaCampana(rs.getString("FECHAVIGENCIACAMPANA"));
                dto.setFechaHoraSolicitud(rs.getString("FECHAHORASOLICITUD"));
                dto.setNumeroProducto(rs.getString("NUMEROPRODUCTO"));
                dto.setSubProducto(rs.getString("SUBPRODUCTO"));
                dto.setCodigoSubproducto(rs.getString("CODIGOSUBPRODUCTO"));
                dto.setMarcaOneClic(rs.getString("MARCAONECLIC"));
                dto.setValorNuevaOperacion(rs.getString("VALORNUEVAOPERACION"));
                dto.setValorCuotaNuevaOperacion(rs.getString("VALORCUOTANUEVAOPERACION"));
                dto.setTasaCreditoNuevo(rs.getString("TASACREDITONUEVO"));
                dto.setPlazo(rs.getString("PLAZO"));
                dto.setCuentaAbono(rs.getString("CUENTAABONO"));
                dto.setCanal(rs.getString("CANAL"));
                dto.setOficinaSegmentacion(rs.getString("OFICINASEGMENTACION"));
                dto.setCreRetanquearLibranza(rs.getString("CRERETANQUEARLIBRANZA"));
                dto.setCodigoConvenio(rs.getString("CODIGOCONVENIO"));
                dto.setNombreConvenio(rs.getString("NOMBRECONVENIO"));
                dto.setTipoConvenio(rs.getString("TIPOCONVENIO") != null ? rs.getString("TIPOCONVENIO") : "1");
                dto.setNumeroConsecutivo(rs.getString("NUMEROCONSECUTIVO"));
                dto.setDireccionIp(rs.getString("DIRECCIONIP"));
                dto.setDescripcion(rs.getString("DESCRIPCION"));
                log.info("Caso de negocio {} recuperado exitosamente desde BONITA7BDM.STANDINGOFFERDM para cliente: {}",
                        noCaso, dto.getNombreCliente());
                return dto;
            });
        } catch (EmptyResultDataAccessException e) {
            log.info("Caso {} no fue encontrado en BONITA7BDM.STANDINGOFFERDM", noCaso);
            return null;
        } catch (Exception e) {
            log.warn("Error consultando caso {} en BONITA7BDM.STANDINGOFFERDM: {}", noCaso, e.getMessage());
            return null;
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
        dto.setConsecutivo(extraerValor(contrato, "consecutivo"));
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
