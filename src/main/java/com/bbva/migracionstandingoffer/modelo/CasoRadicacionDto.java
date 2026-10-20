package com.bbva.migracionstandingoffer.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CasoRadicacionDto {
    private String nombreCliente;
    private String consecutivo;
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

    public CasoRadicacionDto() {
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getDigitoVerificacion() {
        return digitoVerificacion;
    }

    public void setDigitoVerificacion(String digitoVerificacion) {
        this.digitoVerificacion = digitoVerificacion;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    public String getNumeroAltamiraCliente() {
        return numeroAltamiraCliente;
    }

    public void setNumeroAltamiraCliente(String numeroAltamiraCliente) {
        this.numeroAltamiraCliente = numeroAltamiraCliente;
    }

    public String getFechaVigenciaCampana() {
        return fechaVigenciaCampana;
    }

    public void setFechaVigenciaCampana(String fechaVigenciaCampana) {
        this.fechaVigenciaCampana = fechaVigenciaCampana;
    }

    public String getFechaHoraSolicitud() {
        return fechaHoraSolicitud;
    }

    public void setFechaHoraSolicitud(String fechaHoraSolicitud) {
        this.fechaHoraSolicitud = fechaHoraSolicitud;
    }

    public String getNumeroProducto() {
        return numeroProducto;
    }

    public void setNumeroProducto(String numeroProducto) {
        this.numeroProducto = numeroProducto;
    }

    public String getSubProducto() {
        return subProducto;
    }

    public void setSubProducto(String subProducto) {
        this.subProducto = subProducto;
    }

    public String getCodigoSubproducto() {
        return codigoSubproducto;
    }

    public void setCodigoSubproducto(String codigoSubproducto) {
        this.codigoSubproducto = codigoSubproducto;
    }

    public String getMarcaOneClic() {
        return marcaOneClic;
    }

    public void setMarcaOneClic(String marcaOneClic) {
        this.marcaOneClic = marcaOneClic;
    }

    public String getValorNuevaOperacion() {
        return valorNuevaOperacion;
    }

    public void setValorNuevaOperacion(String valorNuevaOperacion) {
        this.valorNuevaOperacion = valorNuevaOperacion;
    }

    public String getValorCuotaNuevaOperacion() {
        return valorCuotaNuevaOperacion;
    }

    public void setValorCuotaNuevaOperacion(String valorCuotaNuevaOperacion) {
        this.valorCuotaNuevaOperacion = valorCuotaNuevaOperacion;
    }

    public String getTasaCreditoNuevo() {
        return tasaCreditoNuevo;
    }

    public void setTasaCreditoNuevo(String tasaCreditoNuevo) {
        this.tasaCreditoNuevo = tasaCreditoNuevo;
    }

    public String getPlazo() {
        return plazo;
    }

    public void setPlazo(String plazo) {
        this.plazo = plazo;
    }

    public String getCuentaAbono() {
        return cuentaAbono;
    }

    public void setCuentaAbono(String cuentaAbono) {
        this.cuentaAbono = cuentaAbono;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getOficinaSegmentacion() {
        return oficinaSegmentacion;
    }

    public void setOficinaSegmentacion(String oficinaSegmentacion) {
        this.oficinaSegmentacion = oficinaSegmentacion;
    }

    public String getCreRetanquearLibranza() {
        return creRetanquearLibranza;
    }

    public void setCreRetanquearLibranza(String creRetanquearLibranza) {
        this.creRetanquearLibranza = creRetanquearLibranza;
    }

    public String getCreRetanquearConsumo() {
        return creRetanquearConsumo;
    }

    public void setCreRetanquearConsumo(String creRetanquearConsumo) {
        this.creRetanquearConsumo = creRetanquearConsumo;
    }

    public String getCreRetanquearHipoteca() {
        return creRetanquearHipoteca;
    }

    public void setCreRetanquearHipoteca(String creRetanquearHipoteca) {
        this.creRetanquearHipoteca = creRetanquearHipoteca;
    }

    public String getCreRetanquearVehiculo() {
        return creRetanquearVehiculo;
    }

    public void setCreRetanquearVehiculo(String creRetanquearVehiculo) {
        this.creRetanquearVehiculo = creRetanquearVehiculo;
    }

    public String getCreRetanquearLeasing() {
        return creRetanquearLeasing;
    }

    public void setCreRetanquearLeasing(String creRetanquearLeasing) {
        this.creRetanquearLeasing = creRetanquearLeasing;
    }

    public String getCodigoConvenio() {
        return codigoConvenio;
    }

    public void setCodigoConvenio(String codigoConvenio) {
        this.codigoConvenio = codigoConvenio;
    }

    public String getNombreConvenio() {
        return nombreConvenio;
    }

    public void setNombreConvenio(String nombreConvenio) {
        this.nombreConvenio = nombreConvenio;
    }

    public String getTipoConvenio() {
        return tipoConvenio;
    }

    public void setTipoConvenio(String tipoConvenio) {
        this.tipoConvenio = tipoConvenio;
    }

    public String getNumeroConsecutivo() {
        return numeroConsecutivo;
    }

    public void setNumeroConsecutivo(String numeroConsecutivo) {
        this.numeroConsecutivo = numeroConsecutivo;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
