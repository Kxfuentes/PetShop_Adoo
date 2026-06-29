package com.petshop.controller;

import com.petshop.dto.VentaResponse;
import com.petshop.model.Inventario;
import com.petshop.model.Mascota;
import com.petshop.model.Producto;
import com.petshop.model.CitaServicio;
import com.petshop.model.Servicio;
import com.petshop.service.ReporteService;
import com.petshop.subsistema.comercial.service.CategoriaService;
import com.petshop.subsistema.comercial.service.GestorVentas;
import com.petshop.subsistema.comercial.service.InventarioService;
import com.petshop.subsistema.comercial.service.ProductoService;
import com.petshop.subsistema.comercial.repository.NotificacionStockRepository;
import com.petshop.subsistema.servicios.service.ClienteService;
import com.petshop.subsistema.servicios.service.GestorCitas;
import com.petshop.subsistema.servicios.service.MascotaService;
import com.petshop.subsistema.servicios.service.ServicioOfrecidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final InventarioService inventarioService;
    private final ClienteService clienteService;
    private final MascotaService mascotaService;
    private final ServicioOfrecidoService servicioOfrecidoService;
    private final GestorVentas gestorVentas;
    private final GestorCitas gestorCitas;
    private final ReporteService reporteService;
    private final NotificacionStockRepository notificacionStockRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Producto> productos = productoService.listar();
        List<VentaResponse> ventas = gestorVentas.listar();
        List<VentaResponse> ventasRecientes = ventas.stream()
                .sorted(Comparator.comparing(VentaResponse::getFecha).reversed())
                .limit(5)
                .toList();

        BigDecimal totalVentas = ventas.stream()
                .map(VentaResponse::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("productosBajoStock", productos.stream().filter(Producto::requiereAlertaStock).count());
        model.addAttribute("totalClientes", clienteService.listar().size());
        model.addAttribute("totalMascotas", mascotaService.listar().size());
        model.addAttribute("cantidadVentas", ventas.size());
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("ventasRecientes", ventasRecientes);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/ventas/registrar")
    public String registrarVenta(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("productos", productoService.listar());
        return "ventas/registrar";
    }

    @GetMapping("/citas/agendar")
    public String agendarCita(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("mascotas", mascotaService.listar());
        model.addAttribute("servicios", servicioOfrecidoService.listar());
        model.addAttribute("citas", citasRows());
        return "citas/agendar";
    }

    @GetMapping("/citas/listar")
    public String listarCitas(Model model) {
        model.addAttribute("citas", citasRows());
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("mascotas", mascotaService.listar());
        model.addAttribute("servicios", servicioOfrecidoService.listar());
        return "citas/listar";
    }

    @GetMapping("/productos/listar")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("categorias", categoriaService.listar());
        return "productos/listar";
    }

    @GetMapping("/clientes/listar")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes/listar";
    }

    @GetMapping("/servicios/listar")
    public String listarServicios(Model model) {
        model.addAttribute("servicios", servicioOfrecidoService.listar());
        return "servicios/listar";
    }

    @GetMapping("/mascotas/listar")
    public String listarMascotas(Model model) {
        List<MascotaRow> mascotas = mascotaService.listar().stream()
                .map(mascota -> new MascotaRow(
                        mascota.getId(),
                        mascota.getNombre(),
                        mascota.getEspecie(),
                        mascota.getRaza(),
                        mascota.getEdad(),
                        mascota.getPeso(),
                        mascota.getCliente().getNombre()))
                .toList();
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("clientes", clienteService.listar());
        return "mascotas/listar";
    }

    @GetMapping("/inventario/movimientos")
    public String movimientosInventario(Model model) {
        List<InventarioRow> movimientos = inventarioService.listar().stream()
                .sorted(Comparator.comparing(Inventario::getFecha).reversed())
                .map(movimiento -> new InventarioRow(
                        movimiento.getId(),
                        movimiento.getProducto().getNombre(),
                        movimiento.getTipoMovimiento().name(),
                        movimiento.getCantidad(),
                        movimiento.getFecha(),
                        movimiento.getMotivo()))
                .toList();
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("notificacionesStock", notificacionStockRepository.findTop10ByOrderByFechaDesc());
        model.addAttribute("notificacionesPendientes", notificacionStockRepository.countByLeidaFalse());
        return "inventario/movimientos";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = fin.minusDays(30);
        Map<String, Object> ventas = reporteService.reporteVentas(inicio, fin);
        Map<String, Object> inventario = reporteService.reporteInventario();
        Map<String, Object> citas = reporteService.reporteCitas();

        model.addAttribute("reporteVentas", ventas);
        model.addAttribute("reporteInventario", inventario);
        model.addAttribute("reporteCitas", citas);
        model.addAttribute("notificacionesStock", notificacionStockRepository.findTop10ByOrderByFechaDesc());
        model.addAttribute("notificacionesPendientes", notificacionStockRepository.countByLeidaFalse());
        model.addAttribute("periodoInicio", inicio);
        model.addAttribute("periodoFin", fin);
        return "reportes";
    }

    public record MascotaRow(Long id, String nombre, String especie, String raza, Integer edad,
                             BigDecimal peso, String clienteNombre) {
    }

    public record InventarioRow(Long id, String productoNombre, String tipoMovimiento, Integer cantidad,
                                LocalDateTime fecha, String motivo) {
    }

    public record CitaRow(Long id, Long clienteId, String clienteNombre, Long mascotaId, String mascotaNombre,
                          Long servicioId, String servicioNombre, Integer duracionMinutos,
                          LocalDateTime fechaHora, String estado, String observacion) {
    }

    private List<CitaRow> citasRows() {
        return gestorCitas.listar().stream()
                .sorted(Comparator.comparing(CitaServicio::getFechaHora).reversed())
                .map(cita -> new CitaRow(
                        cita.getId(),
                        cita.getMascota().getCliente().getId(),
                        cita.getMascota().getCliente().getNombre(),
                        cita.getMascota().getId(),
                        cita.getMascota().getNombre(),
                        cita.getServicio().getId(),
                        cita.getServicio().getNombre(),
                        cita.getServicio().getDuracionMinutos(),
                        cita.getFechaHora(),
                        cita.getEstado().name(),
                        cita.getObservacion()))
                .toList();
    }
}
