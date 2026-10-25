package com.example.avance_n2.controllers;

import com.example.avance_n2.models.VentaWeb;
import com.example.avance_n2.repository.DetalleVentaWebRepository;
import com.example.avance_n2.repository.VentaWebRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/detalles-ventas-web")
public class DetalleVentaWebController {

    private final DetalleVentaWebRepository detalleRepository;
    private final VentaWebRepository ventaWebRepository;

    public DetalleVentaWebController(
            DetalleVentaWebRepository detalleRepository,
            VentaWebRepository ventaWebRepository) {
        this.detalleRepository = detalleRepository;
        this.ventaWebRepository = ventaWebRepository;
    }

    // Consultar los detalles de una venta web
    @GetMapping("/{idVentaWeb}")
    public String listar(
            @PathVariable Integer idVentaWeb,
            Model model) {

        VentaWeb venta = ventaWebRepository.findById(idVentaWeb)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venta web no encontrada"));

        model.addAttribute("ventaWeb", venta);

        model.addAttribute(
                "detalles",
                detalleRepository.findByVentaWeb_IdVentaWeb(idVentaWeb));

        return "detalles-ventas-web/lista";
    }
}