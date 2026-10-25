package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Trabajador;
import com.example.avance_n2.models.Usuario;
import com.example.avance_n2.repository.TrabajadorRepository;
import com.example.avance_n2.repository.ClienteRepository;
import com.example.avance_n2.repository.UsuarioRepository;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/trabajadores")
public class TrabajadorController {

    private final TrabajadorRepository trabajadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public TrabajadorController(
            TrabajadorRepository trabajadorRepository,
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository) {
        this.trabajadorRepository = trabajadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    // Mostrar todos los trabajadores
    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
            "trabajadores",
            trabajadorRepository.findAll()
        );

        return "trabajadores/listar";
    }

    // Mostrar formulario para registrar un trabajador
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("trabajador", new Trabajador());
        cargarUsuariosDisponibles(model, null);

        return "trabajadores/formulario";
    }

    // Guardar un trabajador nuevo o actualizarlo
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("trabajador") Trabajador trabajador,
            BindingResult resultado,
            Model model) {

        Integer idUsuario = null;

        if (trabajador.getUsuario() != null) {
            idUsuario = trabajador.getUsuario().getIdUsuario();
        }

        // Verificar que se haya seleccionado una cuenta
        if (idUsuario == null) {
            resultado.rejectValue(
                "usuario",
                "usuario.obligatorio",
                "Debes seleccionar una cuenta de usuario"
            );
        } else if (!usuarioRepository.existsById(idUsuario)) {
            resultado.rejectValue(
                "usuario",
                "usuario.inexistente",
                "La cuenta seleccionada no existe"
            );
        } else {
            boolean yaEsCliente =
                clienteRepository.existsById(idUsuario);

            boolean yaEsTrabajador =
                trabajadorRepository.existsById(idUsuario);

            // No permitir que una cuenta tenga dos perfiles
            if (yaEsCliente || (yaEsTrabajador
                    && !idUsuario.equals(trabajador.getIdTrabajador()))) {

                resultado.rejectValue(
                    "usuario",
                    "usuario.ocupado",
                    "Esta cuenta ya tiene un perfil registrado"
                );
            }
        }

        if (resultado.hasErrors()) {
            cargarUsuariosDisponibles(
                model,
                trabajador.getIdTrabajador()
            );

            return "trabajadores/formulario";
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow();

        trabajador.setUsuario(usuario);
        trabajadorRepository.save(trabajador);

        return "redirect:/trabajadores";
    }

    // Mostrar formulario para editar
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model model) {

        Trabajador trabajador = trabajadorRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Trabajador no encontrado: " + id
                )
            );

        model.addAttribute("trabajador", trabajador);

        cargarUsuariosDisponibles(
            model,
            trabajador.getIdTrabajador()
        );

        return "trabajadores/formulario";
    }

    // Eliminar trabajador
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        trabajadorRepository.deleteById(id);

        return "redirect:/trabajadores";
    }

    // Cargar únicamente cuentas sin perfil asignado
    private void cargarUsuariosDisponibles(
            Model model,
            Integer idTrabajadorActual) {

        List<Usuario> usuarios = usuarioRepository.findAll()
            .stream()
            .filter(usuario ->
                !clienteRepository.existsById(usuario.getIdUsuario())
                &&
                (!trabajadorRepository.existsById(
                    usuario.getIdUsuario())
                ||
                usuario.getIdUsuario().equals(idTrabajadorActual))
            )
            .toList();

        model.addAttribute("usuariosDisponibles", usuarios);
    }
}
