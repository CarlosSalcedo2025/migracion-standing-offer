package com.bbva.migracionstandingoffer.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CasoRadicacionDto {
    private String nombreCliente;
    private String tipoDocumento;
    private String numeroDocumento;
    private String digitoVerificacion;
    private String correoCliente;
    private String numeroCelular;
    private String numeroAltamiraCliente;
    private String fechaVigenciaCampana;
    private String fechaHoraSolicitud;
    private String numeroProducto;
    private String subProducto;
    private String codigoSubproducto;
    private String marcaOneClic;
    private String valorNuevaOperacion;
    private String valorCuotaNuevaOperacion;
    private String tasaCreditoNuevo;
    private String plazo;
    private String cuentaAbono;
    private String canal;
    private String oficinaSegmentacion;
    private String creRetanquearLibranza;
    private String creRetanquearConsumo;
    private String creRetanquearHipoteca;
    private String creRetanquearVehiculo;
    private String creRetanquearLeasing;
    private String codigoConvenio;
    private String nombreConvenio;
    private String tipoConvenio;
    private String numeroConsecutivo;
    private String direccionIp;
    private String descripcion;
}
