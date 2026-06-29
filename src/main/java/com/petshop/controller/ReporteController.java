package com.petshop.controller;

import com.petshop.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas")
    public Map<String, Object> reporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return reporteService.reporteVentas(inicio, fin);
    }

    @GetMapping("/inventario")
    public Map<String, Object> reporteInventario() {
        return reporteService.reporteInventario();
    }

    @GetMapping("/citas")
    public Map<String, Object> reporteCitas() {
        return reporteService.reporteCitas();
    }
}
