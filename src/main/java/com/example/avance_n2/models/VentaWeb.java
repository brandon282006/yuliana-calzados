package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta_web")
public class VentaWeb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta_web")
    private Integer idVentaWeb;

    @NotNull(message = "Debes seleccionar un carrito")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito", nullable = false, unique = true)
    private Carrito carrito;

    @Column(name = "fecha_vent_web", nullable = false)
    private LocalDateTime fechaVentWeb;

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    @Column(name = "direccion_envio_web", nullable = false, length = 255)
    private String direccionEnvioWeb;

    @NotBlank(message = "El método de pago es obligatorio")
    @Column(name = "metodo_pago_web", nullable = false)
    private String metodoPagoWeb;

    @NotBlank(message = "El estado del pago es obligatorio")
    @Column(name = "estado_pago_web", nullable = false)
    private String estadoPagoWeb = "Pendiente";

    @NotBlank(message = "El estado de la venta es obligatorio")
    @Column(name = "estado_vent_web", nullable = false)
    private String estadoVentWeb = "Pendiente";

    @NotNull(message = "El total de la venta es obligatorio")
    @DecimalMin(value = "0.00", message = "El total no puede ser negativo")
    @Column(name = "total_vent_web", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVentWeb;

    public VentaWeb() {
    }

    public Integer getIdVentaWeb() {
        return idVentaWeb;
    }

    public void setIdVentaWeb(Integer idVentaWeb) {
        this.idVentaWeb = idVentaWeb;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public LocalDateTime getFechaVentWeb() {
        return fechaVentWeb;
    }

    public void setFechaVentWeb(LocalDateTime fechaVentWeb) {
        this.fechaVentWeb = fechaVentWeb;
    }

    public String getDireccionEnvioWeb() {
        return direccionEnvioWeb;
    }

    public void setDireccionEnvioWeb(String direccionEnvioWeb) {
        this.direccionEnvioWeb = direccionEnvioWeb;
    }

    public String getMetodoPagoWeb() {
        return metodoPagoWeb;
    }

    public void setMetodoPagoWeb(String metodoPagoWeb) {
        this.metodoPagoWeb = metodoPagoWeb;
    }

    public String getEstadoPagoWeb() {
        return estadoPagoWeb;
    }

    public void setEstadoPagoWeb(String estadoPagoWeb) {
        this.estadoPagoWeb = estadoPagoWeb;
    }

    public String getEstadoVentWeb() {
        return estadoVentWeb;
    }

    public void setEstadoVentWeb(String estadoVentWeb) {
        this.estadoVentWeb = estadoVentWeb;
    }

    public BigDecimal getTotalVentWeb() {
        return totalVentWeb;
    }

    public void setTotalVentWeb(BigDecimal totalVentWeb) {
        this.totalVentWeb = totalVentWeb;
    }
}
