package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Carrito;
import com.example.avance_n2.models.DetalleCarrito;
import com.example.avance_n2.models.DetalleVentaWeb;
import com.example.avance_n2.models.ProductoVariacion;
import com.example.avance_n2.models.VentaWeb;

import com.example.avance_n2.repository.CarritoRepository;
import com.example.avance_n2.repository.DetalleVentaWebRepository;
import com.example.avance_n2.repository.ProductoVariacionRepository;
import com.example.avance_n2.repository.VentaWebRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ventas-web")
public class VentaWebController {

    private final VentaWebRepository ventaWebRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoVariacionRepository variacionRepository;
    private final DetalleVentaWebRepository detalleRepository;

    public VentaWebController(
            VentaWebRepository ventaWebRepository,
            CarritoRepository carritoRepository,
            ProductoVariacionRepository variacionRepository,
            DetalleVentaWebRepository detalleRepository) {

        this.ventaWebRepository = ventaWebRepository;
        this.carritoRepository = carritoRepository;
        this.variacionRepository = variacionRepository;
        this.detalleRepository = detalleRepository;
    }

    // Listar las ventas web
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ventasWeb", ventaWebRepository.findAll());
        return "ventas-web/lista";
    }

    // Formulario informativo de nueva venta
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ventaWeb", new VentaWeb());
        return "ventas-web/formulario";
    }

    // Procesar una compra enviada desde el checkout
    @PostMapping("/comprar/{idUsuario}")
    @ResponseBody
    @Transactional
    public Map<String, Object> comprar(
            @PathVariable Integer idUsuario,
            @RequestBody SolicitudCompra solicitud) {

        // Validar los datos de envío
        if (solicitud.getDireccion() == null
                || solicitud.getDireccion().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes ingresar una dirección de envío."
            );
        }

        String direccion = solicitud.getDireccion().trim();

        if (solicitud.getDepartamento() != null
                && !solicitud.getDepartamento().isBlank()) {
            direccion += ", " + solicitud.getDepartamento().trim();
        }

        if (solicitud.getReferencia() != null
                && !solicitud.getReferencia().isBlank()) {
            direccion += " (Referencia: "
                    + solicitud.getReferencia().trim() + ")";
        }

        if (direccion.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La dirección completa no puede superar los 255 caracteres."
            );
        }

        // Convertir el método de pago al valor aceptado por MySQL
        String metodoPago;

        switch (solicitud.getMetodoPago() == null
                ? ""
                : solicitud.getMetodoPago()) {

            case "Tarjeta de Crédito / Débito":
                metodoPago = "Tarjeta";
                break;

            case "Yape / Plin":
                metodoPago = "Yape_Plin";
                break;

            case "Transferencia Bancaria":
                metodoPago = "Transferencia";
                break;

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecciona un método de pago válido."
                );
        }

        // Buscar el carrito activo del usuario
        Carrito carrito = carritoRepository
                .findByUsuario_IdUsuarioAndEstadoCarrito(
                        idUsuario,
                        Carrito.EstadoCarrito.Activo
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No tienes un carrito activo."
                ));

        List<DetalleCarrito> productos = carrito.getDetalles();

        if (productos == null || productos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tu carrito está vacío."
            );
        }

        // Preparar los detalles y calcular el subtotal
        List<DetalleVentaWeb> detallesVenta = new ArrayList<>();
        BigDecimal subtotalGeneral = BigDecimal.ZERO;

        for (DetalleCarrito detalleCarrito : productos) {

            if (detalleCarrito.getCantidad() == null
                    || detalleCarrito.getCantidad() < 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Hay una cantidad inválida en el carrito."
                );
            }

            Integer idProducto =
                    detalleCarrito.getProducto().getIdProducto();

            String talla = detalleCarrito.getTalla();

            // Buscar las variaciones de ese producto, talla y color
            String color = detalleCarrito.getColor();

            List<ProductoVariacion> coincidencias =
                    variacionRepository
                            .findByProducto_IdProducto(idProducto)
                            .stream()
                            .filter(v -> v.getTalla().equalsIgnoreCase(talla))
                            .filter(v -> v.getColorPv().equalsIgnoreCase(color))
                            .toList();

            // No elegimos un color al azar
            if (coincidencias.size() != 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede identificar una única variación para "
                                + detalleCarrito.getProducto().getModeloProd()
                                + ", talla " + talla
                                + ". Revisa las tallas y colores registrados."
                );
            }

            ProductoVariacion variacion = coincidencias.get(0);

            // Verificar que haya stock suficiente
            if (variacion.getStock() < detalleCarrito.getCantidad()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para "
                                + detalleCarrito.getProducto().getModeloProd()
                                + ", talla " + talla
                                + ". Disponible: " + variacion.getStock()
                );
            }

            // El precio se obtiene desde MySQL
            BigDecimal precio =
                    detalleCarrito.getProducto().getPrecioProd();

            BigDecimal subtotal = precio.multiply(
                    BigDecimal.valueOf(detalleCarrito.getCantidad())
            );

            DetalleVentaWeb detalleVenta = new DetalleVentaWeb();
            detalleVenta.setVariacion(variacion);
            detalleVenta.setCantidad(detalleCarrito.getCantidad());
            detalleVenta.setPrecioUnitario(precio);
            detalleVenta.setSubtotal(subtotal);

            detallesVenta.add(detalleVenta);
            subtotalGeneral = subtotalGeneral.add(subtotal);
        }

        // Sumar el envío configurado en el checkout
        BigDecimal costoEnvio = new BigDecimal("15.00");
        BigDecimal total = subtotalGeneral.add(costoEnvio);

        // Crear la venta web
        VentaWeb venta = new VentaWeb();
        venta.setCarrito(carrito);
        venta.setFechaVentWeb(LocalDateTime.now());
        venta.setDireccionEnvioWeb(direccion);
        venta.setMetodoPagoWeb(metodoPago);
        venta.setEstadoPagoWeb("Pendiente");
        venta.setEstadoVentWeb("Pendiente");
        venta.setTotalVentWeb(total);

        ventaWebRepository.save(venta);

        // Guardar cada producto comprado
        for (DetalleVentaWeb detalle : detallesVenta) {

            detalle.setVentaWeb(venta);
            detalleRepository.save(detalle);

            // Descontar stock de la variación correspondiente
            ProductoVariacion variacion = detalle.getVariacion();

            variacion.setStock(
                    variacion.getStock() - detalle.getCantidad()
            );

            variacionRepository.save(variacion);
        }

        // Marcar el carrito como finalizado
        carrito.setEstadoCarrito(Carrito.EstadoCarrito.Finalizado);
        carritoRepository.save(carrito);

        // Devolver el número real de venta al navegador
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Compra registrada correctamente.");
        respuesta.put("idVentaWeb", venta.getIdVentaWeb());
        respuesta.put("total", total);

        return respuesta;
    }

    // Consultar una venta web
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        VentaWeb venta = ventaWebRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Venta web no encontrada"));

        model.addAttribute("ventaWeb", venta);
        return "ventas-web/ver";
    }

    // Datos que recibimos desde el checkout
    public static class SolicitudCompra {

        private String direccion;
        private String departamento;
        private String referencia;
        private String metodoPago;

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        public String getDepartamento() {
            return departamento;
        }

        public void setDepartamento(String departamento) {
            this.departamento = departamento;
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }

        public String getMetodoPago() {
            return metodoPago;
        }

        public void setMetodoPago(String metodoPago) {
            this.metodoPago = metodoPago;
        }
    }
}