package com.example.avance_n2.repository;

import com.example.avance_n2.models.VentaFisica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaFisicaRepository
        extends JpaRepository<VentaFisica, Integer> {
}