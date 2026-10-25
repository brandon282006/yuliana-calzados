package com.example.avance_n2.repository;

import com.example.avance_n2.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository
        extends JpaRepository<Producto, Integer> {
}