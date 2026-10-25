package com.example.avance_n2.repository;

import com.example.avance_n2.models.DetalleVentaFisica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaFisicaRepository
        extends JpaRepository<DetalleVentaFisica, Integer> {

    List<DetalleVentaFisica> findByVentaFisica_IdVentaFisica(Integer idVentaFisica);
}