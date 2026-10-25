package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Carrito;
import com.example.avance_n2.models.DetalleCarrito;
import com.example.avance_n2.services.CarritoService;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoApiController {

    private final CarritoService carritoService;

    public CarritoApiController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // Consultar los productos del carrito
    @GetMapping("/{idUsuario}")
    public List<Map<String, Object>> listar(
            @PathVariable Integer idUsuario) {

        List<DetalleCarrito> detalles =
                carritoService.listarDetalles(idUsuario);

        return detalles.stream().map(detalle -> {

            Map<String, Object> datos = new LinkedHashMap<>();

            datos.put("idDetalle", detalle.getIdDetalleCarrito());
            datos.put("idProducto",
                    detalle.getProducto().getIdProducto());
            datos.put("modelo",
                    detalle.getProducto().getModeloProd());
            datos.put("precio",
                    detalle.getProducto().getPrecioProd());

            // Talla y color seleccionados
            datos.put("talla", detalle.getTalla());
            datos.put("color", detalle.getColor());

            datos.put("cantidad", detalle.getCantidad());

            datos.put(
                    "subtotal",
                    detalle.getProducto().getPrecioProd()
                            .multiply(BigDecimal.valueOf(
                                    detalle.getCantidad()))
            );

            return datos;

        }).toList();
    }

    // Agregar un producto al carrito
    @PostMapping("/{idUsuario}/productos")
    public Map<String, Object> agregar(
            @PathVariable Integer idUsuario,
            @RequestBody SolicitudProducto solicitud) {

        Carrito carrito = carritoService.agregarProducto(
                idUsuario,
                solicitud.getIdProducto(),
                solicitud.getCantidad(),
                solicitud.getTalla(),
                solicitud.getColor()
        );

        return Map.of(
                "mensaje", "Producto agregado al carrito",
                "idCarrito", carrito.getIdCarrito()
        );
    }

    // Consultar el total del carrito
    @GetMapping("/{idUsuario}/total")
    public BigDecimal total(@PathVariable Integer idUsuario) {
        return carritoService.calcularTotal(idUsuario);
    }

    // Actualizar la cantidad de un producto
    @PutMapping("/{idUsuario}/productos/{idDetalle}")
    public Map<String, String> actualizar(
            @PathVariable Integer idUsuario,
            @PathVariable Integer idDetalle,
            @RequestParam Integer cantidad) {

        carritoService.actualizarCantidad(
                idUsuario, idDetalle, cantidad);

        return Map.of(
                "mensaje", "Cantidad actualizada correctamente");
    }

    // Eliminar un producto del carrito
    @DeleteMapping("/{idUsuario}/productos/{idDetalle}")
    public Map<String, String> eliminar(
            @PathVariable Integer idUsuario,
            @PathVariable Integer idDetalle) {

        carritoService.eliminarProducto(idUsuario, idDetalle);

        return Map.of(
                "mensaje", "Producto eliminado del carrito");
    }

    // Datos que recibe el backend al agregar un producto
    public static class SolicitudProducto {

        private Integer idProducto;
        private Integer cantidad;
        private String talla;
        private String color;

        public Integer getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Integer idProducto) {
            this.idProducto = idProducto;
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
}