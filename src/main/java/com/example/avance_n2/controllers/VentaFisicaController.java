package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Caja;
import com.example.avance_n2.models.Cliente;
import com.example.avance_n2.models.DetalleVentaFisica;
import com.example.avance_n2.models.ProductoVariacion;
import com.example.avance_n2.models.VentaFisica;

import com.example.avance_n2.repository.CajaRepository;
import com.example.avance_n2.repository.ClienteRepository;
import com.example.avance_n2.repository.DetalleVentaFisicaRepository;
import com.example.avance_n2.repository.ProductoVariacionRepository;
import com.example.avance_n2.repository.VentaFisicaRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/ventas-fisicas")
public class VentaFisicaController {

    private final VentaFisicaRepository ventaFisicaRepository;
    private final DetalleVentaFisicaRepository detalleRepository;
    private final ClienteRepository clienteRepository;
    private final CajaRepository cajaRepository;
    private final ProductoVariacionRepository variacionRepository;

    public VentaFisicaController(
            VentaFisicaRepository ventaFisicaRepository,
            DetalleVentaFisicaRepository detalleRepository,
            ClienteRepository clienteRepository,
            CajaRepository cajaRepository,
            ProductoVariacionRepository variacionRepository) {

        this.ventaFisicaRepository = ventaFisicaRepository;
        this.detalleRepository = detalleRepository;
        this.clienteRepository = clienteRepository;
        this.cajaRepository = cajaRepository;
        this.variacionRepository = variacionRepository;
    }

    // Mostrar todas las ventas físicas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "ventasFisicas",
                ventaFisicaRepository.findAll()
        );

        return "ventas-fisicas/lista";
    }

    // Mostrar formulario de nueva venta
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ventaFisica", new VentaFisica());
        model.addAttribute("clientes", clienteRepository.findAll());

        // Solo se pueden usar cajas que continúen abiertas
        model.addAttribute(
                "cajas",
                cajaRepository.findAll().stream()
                        .filter(caja ->
                                caja.getEstadoCaja() == Caja.EstadoCaja.Abierta)
                        .toList()
        );

        model.addAttribute(
                "variaciones",
                variacionRepository.findAll().stream()
                        .filter(variacion -> variacion.getStock() > 0)
                        .toList()
        );

        return "ventas-fisicas/formulario";
    }

    // Registrar la venta y descontar el stock
    @PostMapping("/guardar")
    @Transactional
    public String guardar(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam Integer idCaja,
            @RequestParam Integer idVariacion,
            @RequestParam Integer cantidad,
            @RequestParam String metodoPagoFis,
            Model model) {

        // Validar cantidad
        if (cantidad == null || cantidad < 1) {
            model.addAttribute("error", "La cantidad debe ser al menos 1.");
            return cargarFormulario(model);
        }

        // Buscar la caja seleccionada
        Caja caja = cajaRepository.findById(idCaja).orElse(null);

        if (caja == null ||
                caja.getEstadoCaja() != Caja.EstadoCaja.Abierta) {
            model.addAttribute("error",
                    "La caja seleccionada no existe o está cerrada.");
            return cargarFormulario(model);
        }

        // Buscar la variación seleccionada
        ProductoVariacion variacion =
                variacionRepository.findById(idVariacion).orElse(null);

        if (variacion == null) {
            model.addAttribute("error",
                    "La talla o el color seleccionado no existe.");
            return cargarFormulario(model);
        }

        // Comprobar el stock disponible
        if (variacion.getStock() < cantidad) {
            model.addAttribute("error",
                    "Stock insuficiente. Unidades disponibles: "
                            + variacion.getStock());
            return cargarFormulario(model);
        }

        // Validar método de pago permitido por la base de datos
        if (!metodoPagoFis.equals("Tarjeta")
                && !metodoPagoFis.equals("Yape_Plin")
                && !metodoPagoFis.equals("Transferencia")
                && !metodoPagoFis.equals("Efectivo")) {

            model.addAttribute("error", "Selecciona un método de pago válido.");
            return cargarFormulario(model);
        }

        // Buscar cliente, si se seleccionó uno
        Cliente cliente = null;

        if (idCliente != null) {
            cliente = clienteRepository.findById(idCliente).orElse(null);

            if (cliente == null) {
                model.addAttribute("error",
                        "El cliente seleccionado no existe.");
                return cargarFormulario(model);
            }
        }

        // Obtener el precio desde la base de datos
        BigDecimal precio = variacion.getProducto().getPrecioProd();

        // Calcular el subtotal y el total
        BigDecimal subtotal = precio.multiply(
                BigDecimal.valueOf(cantidad)
        );

        // Crear la venta
        VentaFisica venta = new VentaFisica();
        venta.setCliente(cliente);
        venta.setCaja(caja);

        // El vendedor es el responsable de la caja
        venta.setVendedor(caja.getVendedor());

        venta.setFechaVentFis(LocalDateTime.now());
        venta.setMetodoPagoFis(metodoPagoFis);
        venta.setEstadoPagoFis("Pagado");
        venta.setEstadoVentFis("Completada");
        venta.setTotalVentFis(subtotal);

        // Guardar primero la venta para obtener su ID
        ventaFisicaRepository.save(venta);

        // Registrar el detalle de la venta
        DetalleVentaFisica detalle = new DetalleVentaFisica();
        detalle.setVentaFisica(venta);
        detalle.setVariacion(variacion);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precio);
        detalle.setSubtotal(subtotal);

        detalleRepository.save(detalle);

        // Descontar las unidades vendidas del stock
        variacion.setStock(variacion.getStock() - cantidad);
        variacionRepository.save(variacion);

        return "redirect:/ventas-fisicas";
    }

    // Volver a cargar los datos del formulario si hay un error
    private String cargarFormulario(Model model) {
        model.addAttribute("ventaFisica", new VentaFisica());
        model.addAttribute("clientes", clienteRepository.findAll());

        model.addAttribute(
                "cajas",
                cajaRepository.findAll().stream()
                        .filter(caja ->
                                caja.getEstadoCaja() == Caja.EstadoCaja.Abierta)
                        .toList()
        );

        model.addAttribute(
                "variaciones",
                variacionRepository.findAll().stream()
                        .filter(variacion -> variacion.getStock() > 0)
                        .toList()
        );

        return "ventas-fisicas/formulario";
    }

    // Consultar una venta
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        VentaFisica venta = ventaFisicaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Venta física no encontrada"));

        model.addAttribute("ventaFisica", venta);
        return "ventas-fisicas/ver";
    }
}