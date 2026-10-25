package com.example.avance_n2.controllers;

import com.example.avance_n2.models.Cliente;
import com.example.avance_n2.models.Usuario;
import com.example.avance_n2.repository.ClienteRepository;
import com.example.avance_n2.repository.TrabajadorRepository;
import com.example.avance_n2.repository.UsuarioRepository;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TrabajadorRepository trabajadorRepository;

    public ClienteController(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            TrabajadorRepository trabajadorRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.trabajadorRepository = trabajadorRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "clientes/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        cargarUsuariosDisponibles(model, null);
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult resultado,
            Model model) {

        Integer idUsuario = null;

        if (cliente.getUsuario() != null) {
            idUsuario = cliente.getUsuario().getIdUsuario();
        }

        if (idUsuario == null) {
            resultado.rejectValue(
                "usuario", "usuario.obligatorio",
                "Debes seleccionar una cuenta de usuario"
            );
        } else if (!usuarioRepository.existsById(idUsuario)) {
            resultado.rejectValue(
                "usuario", "usuario.inexistente",
                "La cuenta seleccionada no existe"
            );
        } else {
            boolean yaEsCliente = clienteRepository.existsById(idUsuario);
            boolean yaEsTrabajador = trabajadorRepository.existsById(idUsuario);

            if (yaEsTrabajador || (yaEsCliente
                    && !idUsuario.equals(cliente.getIdCliente()))) {
                resultado.rejectValue(
                    "usuario", "usuario.ocupado",
                    "Esta cuenta ya tiene un perfil registrado"
                );
            }
        }

        if (resultado.hasErrors()) {
            cargarUsuariosDisponibles(model, cliente.getIdCliente());
            return "clientes/formulario";
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow();

        cliente.setUsuario(usuario);
        clienteRepository.save(cliente);

        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Cliente no encontrado: " + id));

        model.addAttribute("cliente", cliente);
        cargarUsuariosDisponibles(model, cliente.getIdCliente());

        return "clientes/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        clienteRepository.deleteById(id);
        return "redirect:/clientes";
    }

    private void cargarUsuariosDisponibles(
            Model model, Integer idClienteActual) {

        List<Usuario> usuarios = usuarioRepository.findAll()
            .stream()
            .filter(usuario ->
                (!clienteRepository.existsById(usuario.getIdUsuario())
                    || usuario.getIdUsuario().equals(idClienteActual))
                && !trabajadorRepository.existsById(usuario.getIdUsuario()))
            .toList();

        model.addAttribute("usuariosDisponibles", usuarios);
    }
}
