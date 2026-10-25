package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Categoria;
import com.example.avance_n2.models.Producto;
import com.example.avance_n2.repository.CategoriaRepository;
import com.example.avance_n2.repository.ProductoRepository;
import com.example.avance_n2.validators.ProductoValidator;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoValidator productoValidator;

    public ProductoController(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            ProductoValidator productoValidator) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoValidator = productoValidator;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "productos/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        cargarCategorias(model);
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult resultado,
            Model model) {

        productoValidator.validate(producto, resultado);

        // Comprobar que la categoría seleccionada exista en la BD.
        if (producto.getCategoria() != null
                && producto.getCategoria().getIdCategoria() != null
                && !categoriaRepository.existsById(
                    producto.getCategoria().getIdCategoria())) {

            if (!resultado.hasFieldErrors("categoria")) {
                resultado.rejectValue(
                    "categoria",
                    "categoria.noexiste",
                    "La categoría seleccionada no existe"
                );
            }
        }

        if (resultado.hasErrors()) {
            cargarCategorias(model);
            return "productos/formulario";
        }

        // Asociar una categoría obtenida de la base de datos.
        Integer idCategoria = producto.getCategoria().getIdCategoria();

        Categoria categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow(() ->
                new IllegalArgumentException("Categoría no encontrada")
            );

        producto.setCategoria(categoria);
        productoRepository.save(producto);

        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model model) {

        Producto producto = productoRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Producto no encontrado: " + id
                )
            );

        model.addAttribute("producto", producto);
        cargarCategorias(model);

        return "productos/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoRepository.deleteById(id);
        return "redirect:/productos";
    }

    private void cargarCategorias(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
    }
}