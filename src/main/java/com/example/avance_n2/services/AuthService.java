package com.example.avance_n2.services;

import com.example.avance_n2.models.Cliente;
import com.example.avance_n2.models.SesionUsuario;
import com.example.avance_n2.models.Trabajador;
import com.example.avance_n2.models.Usuario;
import com.example.avance_n2.repository.UsuarioRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public SesionUsuario autenticar(
            String correoUsu, String contrasenaUsu) {

        if (correoUsu == null || correoUsu.isBlank()
                || contrasenaUsu == null || contrasenaUsu.isBlank()) {
            return null;
        }

        return usuarioRepository.findByCorreoUsu(correoUsu.trim())
            .filter(usuario ->
                usuario.getEstadoUsu() == Usuario.EstadoUsuario.Activo)
            .filter(usuario ->
                usuario.getContrasenaUsu() != null
                && passwordEncoder.matches(
                    contrasenaUsu,
                    usuario.getContrasenaUsu()))
            .map(this::crearSesion)
            .orElse(null);
    }

    private SesionUsuario crearSesion(Usuario usuario) {

        Trabajador trabajador = usuario.getTrabajador();
        Cliente cliente = usuario.getCliente();

        if (trabajador != null && trabajador.getCargoTra() != null) {

            String nombre = trabajador.getNombreTra();
            String apellido = trabajador.getApellidoTra();

            String nombreCompleto =
                (nombre == null ? "" : nombre) +
                (apellido == null || apellido.isBlank()
                    ? "" : " " + apellido);

            return new SesionUsuario(
                usuario.getIdUsuario(),
                usuario.getCorreoUsu(),
                nombreCompleto.trim(),
                trabajador.getCargoTra().name()
            );
        }

        if (cliente != null) {

            String nombre = cliente.getNombreCli();
            String apellido = cliente.getApellidoCli();

            String nombreCompleto =
                (nombre == null ? "" : nombre) +
                (apellido == null || apellido.isBlank()
                    ? "" : " " + apellido);

            return new SesionUsuario(
                usuario.getIdUsuario(),
                usuario.getCorreoUsu(),
                nombreCompleto.trim(),
                "Cliente"
            );
        }

        // Si no tiene un perfil válido, no permitimos iniciar sesión.
        return null;
    }
}
