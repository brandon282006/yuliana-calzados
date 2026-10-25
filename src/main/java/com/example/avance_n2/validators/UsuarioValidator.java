
package com.example.avance_n2.validators;

import com.example.avance_n2.models.Usuario;
import com.example.avance_n2.repository.UsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UsuarioValidator implements Validator {

    private final UsuarioRepository usuarioRepository;

    public UsuarioValidator(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Usuario.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Usuario usuario = (Usuario) target;

        if (usuario.getCorreoUsu() != null
                && !usuario.getCorreoUsu().isBlank()) {

            usuarioRepository.findByCorreoUsu(usuario.getCorreoUsu())
                .ifPresent(existente -> {
                    if (!existente.getIdUsuario().equals(usuario.getIdUsuario())
                            && !errors.hasFieldErrors("correoUsu")) {
                        errors.rejectValue(
                            "correoUsu",
                            "correo.duplicado",
                            "Este correo ya está registrado"
                        );
                    }
                });
        }
    }
}
