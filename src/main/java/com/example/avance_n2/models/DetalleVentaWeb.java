package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta_web")
public class DetalleVentaWeb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_web")
    private Integer idDetalleWeb;

    @NotNull(message = "Debes seleccionar una venta web")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta_web", nullable = false)
    private VentaWeb ventaWeb;

    @NotNull(message = "Debes seleccionar una variación")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_variacion", nullable = false)
    private ProductoVariacion variacion;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.00", message = "El subtotal no puede ser negativo")
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public DetalleVentaWeb() {
    }

    public Integer getIdDetalleWeb() {
        return idDetalleWeb;
    }

    public void setIdDetalleWeb(Integer idDetalleWeb) {
        this.idDetalleWeb = idDetalleWeb;
    }

    public VentaWeb getVentaWeb() {
        return ventaWeb;
    }

    public void setVentaWeb(VentaWeb ventaWeb) {
        this.ventaWeb = ventaWeb;
    }

    public ProductoVariacion getVariacion() {
        return variacion;
    }

    public void setVariacion(ProductoVariacion variacion) {
        this.variacion = variacion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
