package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @NotBlank(message = "El modelo del producto es obligatorio")
    @Size(max = 50, message = "El modelo no puede superar los 50 caracteres")
    @Column(name = "modelo_prod", nullable = false, length = 50)
    private String modeloProd;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Column(name = "descripcion_prod", length = 500)
    private String descripcionProd;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_prod", nullable = false)
    private EstadoProducto estadoProd = EstadoProducto.Activo;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Column(name = "precio_prod", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioProd;

    @NotNull(message = "Debes seleccionar una categoría")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    public Producto() {
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getModeloProd() {
        return modeloProd;
    }

    public void setModeloProd(String modeloProd) {
        this.modeloProd = modeloProd;
    }

    public String getDescripcionProd() {
        return descripcionProd;
    }

    public void setDescripcionProd(String descripcionProd) {
        this.descripcionProd = descripcionProd;
    }

    public EstadoProducto getEstadoProd() {
        return estadoProd;
    }

    public void setEstadoProd(EstadoProducto estadoProd) {
        this.estadoProd = estadoProd;
    }

    public BigDecimal getPrecioProd() {
        return precioProd;
    }

    public void setPrecioProd(BigDecimal precioProd) {
        this.precioProd = precioProd;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public enum EstadoProducto {
        Activo,
        Descontinuado
    }
}