
package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "producto_variacion", uniqueConstraints = {
        @UniqueConstraint(name = "uq_producto_talla", columnNames = { "id_producto", "talla" })
})
public class ProductoVariacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variacion")
    private Integer idVariacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotBlank(message = "La talla es obligatoria")
    @Size(max = 10, message = "La talla no puede superar los 10 caracteres")
    @Column(name = "talla", nullable = false, length = 10)
    private String talla;

    @NotBlank(message = "El color es obligatorio")
    @Size(max = 50, message = "El color no puede superar los 50 caracteres")
    @Column(name = "color_pv", nullable = false, length = 50)
    private String colorPv;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    public ProductoVariacion() {
    }

    public Integer getIdVariacion() {
        return idVariacion;
    }

    public void setIdVariacion(Integer idVariacion) {
        this.idVariacion = idVariacion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColorPv() {
        return colorPv;
    }

    public void setColorPv(String colorPv) {
        this.colorPv = colorPv;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}