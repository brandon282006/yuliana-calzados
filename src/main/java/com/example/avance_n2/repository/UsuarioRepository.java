package com.example.avance_n2.repository;

import com.example.avance_n2.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoUsu(String correoUsu);

    boolean existsByCorreoUsu(String correoUsu);
}
