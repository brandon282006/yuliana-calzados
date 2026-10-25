
package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Producto;
import com.example.avance_n2.models.ProductoVariacion;
import com.example.avance_n2.repository.ProductoRepository;
import com.example.avance_n2.repository.ProductoVariacionRepository;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/variaciones")
public class ProductoVariacionController {

    private final ProductoVariacionRepository variacionRepository;
    private final ProductoRepository productoRepository;

    public ProductoVariacionController(
            ProductoVariacionRepository variacionRepository,
            ProductoRepository productoRepository) {

        this.variacionRepository = variacionRepository;
        this.productoRepository = productoRepository;
    }

    // Mostrar todas las variaciones
    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "variaciones",
                variacionRepository.findAll());

        return "variaciones/lista";
    }

    // Mostrar el formulario para registrar
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("variacion", new ProductoVariacion());
        model.addAttribute("productos", productoRepository.findAll());

        return "variaciones/formulario";
    }

    // Guardar una variación nueva
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("variacion") ProductoVariacion variacion,
            BindingResult resultado,
            @RequestParam("idProducto") Integer idProducto,
            Model model) {

        // Buscar el producto seleccionado
        Producto producto = productoRepository.findById(idProducto)
                .orElse(null);

        // Comprobar que el producto exista
        if (producto == null) {
            resultado.rejectValue(
                    "producto",
                    "producto.invalido",
                    "Debes seleccionar un producto válido");
        } else {
            variacion.setProducto(producto);
        }

        // Si hay errores, volver al formulario
        if (resultado.hasErrors()) {
            model.addAttribute("productos", productoRepository.findAll());
            return "variaciones/formulario";
        }

        // Guardar la variación
        variacionRepository.save(variacion);

        return "redirect:/variaciones";
    }

    // Mostrar el formulario para editar
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model model) {

        ProductoVariacion variacion = variacionRepository.findById(id).orElse(null);

        if (variacion == null) {
            return "redirect:/variaciones";
        }

        model.addAttribute("variacion", variacion);
        model.addAttribute("productos", productoRepository.findAll());

        return "variaciones/formulario";
    }

    // Eliminar una variación
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {

        if (variacionRepository.existsById(id)) {
            variacionRepository.deleteById(id);
        }

        return "redirect:/variaciones";
    }
}
