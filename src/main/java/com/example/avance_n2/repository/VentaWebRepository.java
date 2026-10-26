package com.example.avance_n2.repository;

import com.example.avance_n2.models.VentaWeb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaWebRepository extends JpaRepository<VentaWeb, Integer> {

    List<VentaWeb> findByFechaVentWebGreaterThanEqualAndFechaVentWebLessThan(
            LocalDateTime desde,
            LocalDateTime hasta);
}