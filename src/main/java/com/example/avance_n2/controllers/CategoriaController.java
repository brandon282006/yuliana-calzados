package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Categoria;
import com.example.avance_n2.repository.CategoriaRepository;
import com.example.avance_n2.validators.CategoriaValidator;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaValidator categoriaValidator;

    public CategoriaController(
            CategoriaRepository categoriaRepository,
            CategoriaValidator categoriaValidator) {

        this.categoriaRepository = categoriaRepository;
        this.categoriaValidator = categoriaValidator;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
            "categorias",
            categoriaRepository.findAll()
        );

        return "categorias/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("categoria", new Categoria());

        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("categoria") Categoria categoria,
            BindingResult resultado) {

        // Ejecutar nuestro validador personalizado
        categoriaValidator.validate(categoria, resultado);

        if (resultado.hasErrors()) {
            return "categorias/formulario";
        }

        categoriaRepository.save(categoria);

        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model model) {

        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Categoría no encontrada: " + id
                )
            );

        model.addAttribute("categoria", categoria);

        return "categorias/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {

        categoriaRepository.deleteById(id);

        return "redirect:/categorias";
    }
}