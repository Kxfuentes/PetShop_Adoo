package com.petshop.service;

import com.petshop.model.CitaServicio;
import com.petshop.model.Producto;
import com.petshop.model.Venta;
import com.petshop.model.enums.EstadoCita;
import com.petshop.model.enums.EstadoVenta;
import com.petshop.subsistema.comercial.repository.VentaRepository;
import com.petshop.subsistema.servicios.repository.CitaServicioRepository;
import com.petshop.subsistema.comercial.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final VentaRepository ventaRepository;
    private final InventarioService inventarioService;
    private final CitaServicioRepository citaServicioRepository;

    public Map<String, Object> reporteVentas(LocalDateTime inicio, LocalDateTime fin) {
        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin).stream()
                .filter(v -> v.getEstado() == EstadoVenta.PAGADA)
                .toList();

        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> ventasResumen = ventas.stream()
                .map(v -> Map.<String, Object>of(
                        "id", v.getId(),
                        "fecha", v.getFecha(),
                        "total", v.getTotal(),
                        "cliente", v.getCliente().getNombre(),
                        "estado", v.getEstado()))
                .toList();

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("periodoInicio", inicio);
        reporte.put("periodoFin", fin);
        reporte.put("cantidadVentas", ventas.size());
        reporte.put("totalRecaudado", totalVentas);
        reporte.put("ticketPromedio", ventas.isEmpty()
                ? BigDecimal.ZERO
                : totalVentas.divide(BigDecimal.valueOf(ventas.size()), 2, RoundingMode.HALF_UP));
        reporte.put("ventas", ventasResumen);
        return reporte;
    }

    public Map<String, Object> reporteInventario() {
        List<Producto> productos = inventarioService.generarAlertasStockMinimo();
        List<Map<String, Object>> productosResumen = productos.stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.getId(),
                        "nombre", p.getNombre(),
                        "stock", p.getStock(),
                        "stockMinimo", p.getStockMinimo(),
                        "precio", p.getPrecio()))
                .toList();

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("productosConAlerta", productos.size());
        reporte.put("productos", productosResumen);
        return reporte;
    }

    public Map<String, Object> reporteCitas() {
        List<CitaServicio> citas = citaServicioRepository.findAll();
        long agendadas = citas.stream().filter(c -> c.getEstado() == EstadoCita.AGENDADA).count();
        long reprogramadas = citas.stream().filter(c -> c.getEstado() == EstadoCita.REPROGRAMADA).count();
        long confirmadas = citas.stream().filter(c -> c.getEstado() == EstadoCita.CONFIRMADA).count();
        long enAtencion = citas.stream().filter(c -> c.getEstado() == EstadoCita.EN_ATENCION).count();
        long finalizadas = citas.stream().filter(c -> c.getEstado() == EstadoCita.FINALIZADA).count();
        long canceladas = citas.stream().filter(c -> c.getEstado() == EstadoCita.CANCELADA).count();

        List<Map<String, Object>> citasResumen = citas.stream()
                .map(c -> Map.<String, Object>of(
                        "id", c.getId(),
                        "fechaHora", c.getFechaHora(),
                        "estado", c.getEstado(),
                        "mascota", c.getMascota().getNombre(),
                        "servicio", c.getServicio().getNombre()))
                .toList();

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("totalCitas", citas.size());
        reporte.put("agendadas", agendadas);
        reporte.put("reprogramadas", reprogramadas);
        reporte.put("confirmadas", confirmadas);
        reporte.put("enAtencion", enAtencion);
        reporte.put("finalizadas", finalizadas);
        reporte.put("canceladas", canceladas);
        reporte.put("citas", citasResumen);
        return reporte;
    }
}
