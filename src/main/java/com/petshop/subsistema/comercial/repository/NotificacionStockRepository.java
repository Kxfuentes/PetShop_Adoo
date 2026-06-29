package com.petshop.subsistema.comercial.repository;

import com.petshop.model.NotificacionStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionStockRepository extends JpaRepository<NotificacionStock, Long> {

    List<NotificacionStock> findTop10ByOrderByFechaDesc();

    long countByLeidaFalse();
}
