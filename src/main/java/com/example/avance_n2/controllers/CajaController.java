package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Caja;
import com.example.avance_n2.models.Trabajador;
import com.example.avance_n2.repository.CajaRepository;
import com.example.avance_n2.repository.TrabajadorRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cajas")
public class CajaController {

    private final CajaRepository cajaRepository;
    private final TrabajadorRepository trabajadorRepository;

    public CajaController(
            CajaRepository cajaRepository,
            TrabajadorRepository trabajadorRepository) {
        this.cajaRepository = cajaRepository;
        this.trabajadorRepository = trabajadorRepository;
    }

    // Mostrar todas las cajas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cajas", cajaRepository.findAll());
        return "cajas/lista";
    }

    // Mostrar formulario para registrar una caja
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("caja", new Caja());
        model.addAttribute("trabajadores",
                trabajadorRepository.findAll());

        return "cajas/formulario";
    }

    // Guardar una caja nueva o actualizar una existente
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("caja") Caja caja,
            BindingResult resultado,
            @RequestParam(value = "vendedorId", required = false) Integer vendedorId,
            Model model) {

        // Asignar la fecha y el estado antes de validar
        if (caja.getIdCaja() == null) {
            // Valores iniciales para una caja nueva
            caja.setFechaAperturaCaja(LocalDateTime.now());
            caja.setEstadoCaja(Caja.EstadoCaja.Abierta);
            caja.setMontoFinalCaja(null);
        } else {
            // Conservar los datos originales al editar
            Caja cajaExistente = cajaRepository.findById(caja.getIdCaja())
                    .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));

            // No permitir modificar una caja cerrada
            if (cajaExistente.getEstadoCaja() == Caja.EstadoCaja.Cerrada) {
                return "redirect:/cajas";
            }

            caja.setFechaAperturaCaja(cajaExistente.getFechaAperturaCaja());
            caja.setFechaCierreCaja(cajaExistente.getFechaCierreCaja());
            caja.setEstadoCaja(cajaExistente.getEstadoCaja());
            caja.setMontoFinalCaja(cajaExistente.getMontoFinalCaja());
        }

        // Buscar y asignar el vendedor seleccionado
        if (vendedorId == null) {
            resultado.rejectValue(
                    "vendedor",
                    "vendedor.obligatorio",
                    "Debes seleccionar un vendedor");
        } else {
            Trabajador vendedor = trabajadorRepository
                    .findById(vendedorId)
                    .orElse(null);

            if (vendedor == null) {
                resultado.rejectValue(
                        "vendedor",
                        "vendedor.inexistente",
                        "El vendedor seleccionado no existe");
            } else {
                caja.setVendedor(vendedor);
            }
        }

        // Si existen errores, volver al formulario
        if (resultado.hasErrors()) {
            model.addAttribute(
                    "trabajadores",
                    trabajadorRepository.findAll());

            return "cajas/formulario";
        }

        // Guardar en MySQL
        cajaRepository.save(caja);

        return "redirect:/cajas";
    }

    // Mostrar formulario para editar una caja
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model model) {

        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la caja con ID: " + id));

        if (caja.getEstadoCaja() == Caja.EstadoCaja.Cerrada) {
            return "redirect:/cajas";
        }

        model.addAttribute("caja", caja);
        model.addAttribute("trabajadores",
                trabajadorRepository.findAll());

        return "cajas/formulario";
    }

    // Mostrar el formulario para cerrar una caja
    @GetMapping("/cerrar/{id}")
    public String mostrarCerrar(
            @PathVariable Integer id,
            Model model) {

        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Caja no encontrada: " + id));

        if (caja.getEstadoCaja() == Caja.EstadoCaja.Cerrada) {
            return "redirect:/cajas";
        }

        model.addAttribute("caja", caja);

        return "cajas/cerrar";
    }

    // Registrar el cierre de una caja
    @PostMapping("/cerrar/{id}")
    public String cerrar(
            @PathVariable Integer id,
            @RequestParam java.math.BigDecimal montoFinalCaja) {

        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Caja no encontrada: " + id));

        // No permitir cerrar una caja que ya está cerrada
        if (caja.getEstadoCaja() == Caja.EstadoCaja.Cerrada) {
            return "redirect:/cajas";
        }

        // Validar el monto final
        if (montoFinalCaja == null
                || montoFinalCaja.compareTo(
                        java.math.BigDecimal.ZERO) < 0) {
            return "redirect:/cajas/cerrar/" + id;
        }

        caja.setMontoFinalCaja(montoFinalCaja);
        caja.setFechaCierreCaja(java.time.LocalDateTime.now());
        caja.setEstadoCaja(Caja.EstadoCaja.Cerrada);

        cajaRepository.save(caja);

        return "redirect:/cajas";
    }

    // Eliminar una caja
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        cajaRepository.deleteById(id);
        return "redirect:/cajas";
    }
}