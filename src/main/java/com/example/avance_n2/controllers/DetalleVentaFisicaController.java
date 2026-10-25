package com.example.avance_n2.controllers;

import com.example.avance_n2.models.VentaFisica;
import com.example.avance_n2.repository.DetalleVentaFisicaRepository;
import com.example.avance_n2.repository.VentaFisicaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/detalles-ventas-fisicas")
public class DetalleVentaFisicaController {

    private final DetalleVentaFisicaRepository detalleRepository;
    private final VentaFisicaRepository ventaFisicaRepository;

    public DetalleVentaFisicaController(
            DetalleVentaFisicaRepository detalleRepository,
            VentaFisicaRepository ventaFisicaRepository) {
        this.detalleRepository = detalleRepository;
        this.ventaFisicaRepository = ventaFisicaRepository;
    }

    // Consultar los detalles de una venta física
    @GetMapping("/{idVentaFisica}")
    public String listar(
            @PathVariable Integer idVentaFisica,
            Model model) {

        VentaFisica venta = ventaFisicaRepository
                .findById(idVentaFisica)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venta física no encontrada"));

        model.addAttribute("ventaFisica", venta);

        model.addAttribute(
                "detalles",
                detalleRepository.findByVentaFisica_IdVentaFisica(
                        idVentaFisica));

        return "detalles-ventas-fisicas/lista";
    }
}
