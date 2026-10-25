package com.example.avance_n2.repository;

import com.example.avance_n2.models.DetalleVentaWeb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaWebRepository
        extends JpaRepository<DetalleVentaWeb, Integer> {

    List<DetalleVentaWeb> findByVentaWeb_IdVentaWeb(Integer idVentaWeb);
}