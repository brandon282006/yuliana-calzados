package com.example.avance_n2.repository;

import com.example.avance_n2.models.ProductoVariacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoVariacionRepository
        extends JpaRepository<ProductoVariacion, Integer> {

    List<ProductoVariacion> findByProducto_IdProducto(Integer idProducto);
}