package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta_fisica")
public class VentaFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta_fisica")
    private Integer idVentaFisica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @NotNull(message = "Debes seleccionar un vendedor")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor", nullable = false)
    private Trabajador vendedor;

    @NotNull(message = "Debes seleccionar una caja")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    @Column(name = "fecha_vent_fis", nullable = false)
    private LocalDateTime fechaVentFis;

    @NotNull(message = "El método de pago es obligatorio")
    @Column(name = "metodo_pago_fis", nullable = false)
    private String metodoPagoFis;

    @NotNull(message = "El estado del pago es obligatorio")
    @Column(name = "estado_pago_fis", nullable = false)
    private String estadoPagoFis = "Pendiente";

    @NotNull(message = "El estado de la venta es obligatorio")
    @Column(name = "estado_vent_fis", nullable = false)
    private String estadoVentFis = "Completada";

    @NotNull(message = "El total de la venta es obligatorio")
    @DecimalMin(value = "0.00", message = "El total no puede ser negativo")
    @Column(name = "total_vent_fis", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVentFis;

    public VentaFisica() {
    }

    public Integer getIdVentaFisica() {
        return idVentaFisica;
    }

    public void setIdVentaFisica(Integer idVentaFisica) {
        this.idVentaFisica = idVentaFisica;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Trabajador getVendedor() {
        return vendedor;
    }

    public void setVendedor(Trabajador vendedor) {
        this.vendedor = vendedor;
    }

    public Caja getCaja() {
        return caja;
    }

    public void setCaja(Caja caja) {
        this.caja = caja;
    }

    public LocalDateTime getFechaVentFis() {
        return fechaVentFis;
    }

    public void setFechaVentFis(LocalDateTime fechaVentFis) {
        this.fechaVentFis = fechaVentFis;
    }

    public String getMetodoPagoFis() {
        return metodoPagoFis;
    }

    public void setMetodoPagoFis(String metodoPagoFis) {
        this.metodoPagoFis = metodoPagoFis;
    }

    public String getEstadoPagoFis() {
        return estadoPagoFis;
    }

    public void setEstadoPagoFis(String estadoPagoFis) {
        this.estadoPagoFis = estadoPagoFis;
    }

    public String getEstadoVentFis() {
        return estadoVentFis;
    }

    public void setEstadoVentFis(String estadoVentFis) {
        this.estadoVentFis = estadoVentFis;
    }

    public BigDecimal getTotalVentFis() {
        return totalVentFis;
    }

    public void setTotalVentFis(BigDecimal totalVentFis) {
        this.totalVentFis = totalVentFis;
    }
}
