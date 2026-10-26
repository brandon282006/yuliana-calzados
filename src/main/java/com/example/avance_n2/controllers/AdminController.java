package com.example.avance_n2.controllers;

import com.example.avance_n2.models.SesionUsuario;
import com.example.avance_n2.models.VentaFisica;
import com.example.avance_n2.models.VentaWeb;
import com.example.avance_n2.repository.CategoriaRepository;
import com.example.avance_n2.repository.ProductoRepository;
import com.example.avance_n2.repository.UsuarioRepository;
import com.example.avance_n2.repository.VentaFisicaRepository;
import com.example.avance_n2.repository.VentaWebRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaFisicaRepository ventaFisicaRepository;
    private final VentaWebRepository ventaWebRepository;

    public AdminController(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository,
            VentaFisicaRepository ventaFisicaRepository,
            VentaWebRepository ventaWebRepository) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.ventaFisicaRepository = ventaFisicaRepository;
        this.ventaWebRepository = ventaWebRepository;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {

        SesionUsuario usuario =
                (SesionUsuario) session.getAttribute("usuarioSesion");

        // Si no inició sesión, lo enviamos al login.
        if (usuario == null) {
            return "redirect:/login";
        }

        // Solo el administrador puede ver este panel.
        if (!"Administrador".equals(usuario.getRol())) {
            return "redirect:/index";
        }

        model.addAttribute("usuarioSesion", usuario);
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalCategorias", categoriaRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());

        LocalDate hoy = LocalDate.now();
        LocalDate desde = hoy.minusDays(6);
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();

        Map<LocalDate, Long> ventasFisicasPorFecha = new HashMap<>();
        Map<LocalDate, Long> ventasWebPorFecha = new HashMap<>();
        for (int dia = 0; dia < 7; dia++) {
            LocalDate fecha = desde.plusDays(dia);
            ventasFisicasPorFecha.put(fecha, 0L);
            ventasWebPorFecha.put(fecha, 0L);
        }

        for (VentaFisica venta : ventaFisicaRepository
                .findByFechaVentFisGreaterThanEqualAndFechaVentFisLessThan(
                        inicio, fin)) {
            ventasFisicasPorFecha.merge(
                    venta.getFechaVentFis().toLocalDate(), 1L, Long::sum);
        }

        for (VentaWeb venta : ventaWebRepository
                .findByFechaVentWebGreaterThanEqualAndFechaVentWebLessThan(
                        inicio, fin)) {
            ventasWebPorFecha.merge(
                    venta.getFechaVentWeb().toLocalDate(), 1L, Long::sum);
        }

        DateTimeFormatter formatoEtiqueta = DateTimeFormatter
                .ofPattern("EEE dd/MM", Locale.forLanguageTag("es-PE"));
        List<String> etiquetasVentas = new ArrayList<>();
        List<Long> cantidadesVentasFisicas = new ArrayList<>();
        List<Long> cantidadesVentasWeb = new ArrayList<>();

        for (int dia = 0; dia < 7; dia++) {
            LocalDate fecha = desde.plusDays(dia);
            etiquetasVentas.add(formatoEtiqueta.format(fecha));
            cantidadesVentasFisicas.add(ventasFisicasPorFecha.get(fecha));
            cantidadesVentasWeb.add(ventasWebPorFecha.get(fecha));
        }

        model.addAttribute("etiquetasVentas", etiquetasVentas);
        model.addAttribute("cantidadesVentasFisicas", cantidadesVentasFisicas);
        model.addAttribute("cantidadesVentasWeb", cantidadesVentasWeb);

        return "admin/dashboard";
    }
}
