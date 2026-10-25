package com.example.avance_n2.repository;

import com.example.avance_n2.models.DetalleCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleCarritoRepository
        extends JpaRepository<DetalleCarrito, Integer> {

    List<DetalleCarrito> findByCarrito_IdCarrito(
        Integer idCarrito
    );

    Optional<DetalleCarrito>
    findByCarrito_IdCarritoAndProducto_IdProductoAndTallaAndColor(
        Integer idCarrito,
        Integer idProducto,
        String talla,
        String color
    );
}