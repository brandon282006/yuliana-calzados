package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "Detalle_Carrito",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_carrito_producto_talla_color",
            columnNames = {
                "id_carrito",
                "id_producto",
                "talla",
                "color"
            }
        )
    }
)
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_carrito")
    private Integer idDetalleCarrito;

    @NotNull(message = "El carrito es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito", nullable = false)
    private Carrito carrito;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser como mínimo 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    @NotBlank(message = "Debes seleccionar una talla")
    @Size(max = 10, message = "La talla no puede superar los 10 caracteres")
    @Column(name = "talla", nullable = false, length = 10)
    private String talla;

    // Nuevo atributo: color seleccionado
    @NotBlank(message = "Debes seleccionar un color")
    @Size(max = 50, message = "El color no puede superar los 50 caracteres")
    @Column(name = "color", nullable = false, length = 50)
    private String color;

    public DetalleCarrito() {
    }

    public Integer getIdDetalleCarrito() {
        return idDetalleCarrito;
    }

    public void setIdDetalleCarrito(Integer idDetalleCarrito) {
        this.idDetalleCarrito = idDetalleCarrito;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}