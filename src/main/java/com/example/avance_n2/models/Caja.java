package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja")
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Integer idCaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor", nullable = false)
    private Trabajador vendedor;

    @Column(name = "fecha_apertura_caja", nullable = false)
    private LocalDateTime fechaAperturaCaja;

    @Column(name = "fecha_cierre_caja")
    private LocalDateTime fechaCierreCaja;

    @NotNull(message = "El monto inicial es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto inicial no puede ser negativo")
    @Column(name = "monto_inicial_caja", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoInicialCaja;

    @DecimalMin(value = "0.00", message = "El monto final no puede ser negativo")
    @Column(name = "monto_final_caja", precision = 10, scale = 2)
    private BigDecimal montoFinalCaja;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_caja", nullable = false)
    private EstadoCaja estadoCaja = EstadoCaja.Abierta;

    public Caja() {
    }

    public Integer getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Integer idCaja) {
        this.idCaja = idCaja;
    }

    public Trabajador getVendedor() {
        return vendedor;
    }

    public void setVendedor(Trabajador vendedor) {
        this.vendedor = vendedor;
    }

    public LocalDateTime getFechaAperturaCaja() {
        return fechaAperturaCaja;
    }

    public void setFechaAperturaCaja(LocalDateTime fechaAperturaCaja) {
        this.fechaAperturaCaja = fechaAperturaCaja;
    }

    public LocalDateTime getFechaCierreCaja() {
        return fechaCierreCaja;
    }

    public void setFechaCierreCaja(LocalDateTime fechaCierreCaja) {
        this.fechaCierreCaja = fechaCierreCaja;
    }

    public BigDecimal getMontoInicialCaja() {
        return montoInicialCaja;
    }

    public void setMontoInicialCaja(BigDecimal montoInicialCaja) {
        this.montoInicialCaja = montoInicialCaja;
    }

    public BigDecimal getMontoFinalCaja() {
        return montoFinalCaja;
    }

    public void setMontoFinalCaja(BigDecimal montoFinalCaja) {
        this.montoFinalCaja = montoFinalCaja;
    }

    public EstadoCaja getEstadoCaja() {
        return estadoCaja;
    }

    public void setEstadoCaja(EstadoCaja estadoCaja) {
        this.estadoCaja = estadoCaja;
    }

    public enum EstadoCaja {
        Abierta,
        Cerrada
    }
}