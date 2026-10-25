
package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Usuario;
import com.example.avance_n2.repository.UsuarioRepository;
import com.example.avance_n2.validators.UsuarioValidator;

import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioValidator usuarioValidator;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            UsuarioValidator usuarioValidator) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioValidator = usuarioValidator;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult resultado) {

        usuarioValidator.validate(usuario, resultado);

        if (resultado.hasErrors()) {
            return "usuarios/formulario";
        }

        usuario.setContrasenaUsu(
            passwordEncoder.encode(usuario.getContrasenaUsu())
        );

        usuarioRepository.save(usuario);

        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Usuario no encontrado: " + id));

        model.addAttribute("usuario", usuario);
        return "usuarios/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuarios";
    }
}
