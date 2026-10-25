package com.example.avance_n2.repository;

import com.example.avance_n2.models.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoRepository
        extends JpaRepository<Carrito, Integer> {

    List<Carrito> findByUsuario_IdUsuario(Integer idUsuario);

    Optional<Carrito> findByUsuario_IdUsuarioAndEstadoCarrito(
        Integer idUsuario,
        Carrito.EstadoCarrito estadoCarrito
    );
}
