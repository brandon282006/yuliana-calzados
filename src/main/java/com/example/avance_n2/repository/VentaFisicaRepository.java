package com.example.avance_n2.repository;

import com.example.avance_n2.models.VentaFisica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaFisicaRepository
        extends JpaRepository<VentaFisica, Integer> {

    List<VentaFisica> findByFechaVentFisGreaterThanEqualAndFechaVentFisLessThan(
            LocalDateTime desde,
            LocalDateTime hasta);
}