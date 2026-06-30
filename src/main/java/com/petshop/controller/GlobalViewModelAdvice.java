package com.petshop.controller;

import com.petshop.model.Producto;
import com.petshop.subsistema.comercial.service.ProductoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Comparator;
import java.util.List;

@ControllerAdvice(annotations = Controller.class)
public class GlobalViewModelAdvice {

    private final ProductoService productoService;

    public GlobalViewModelAdvice(ProductoService productoService) {
        this.productoService = productoService;
    }

    @ModelAttribute("stockAlerts")
    public List<StockAlertView> stockAlerts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return List.of();
        }

        return productoService.listarConAlertaStock().stream()
                .sorted(Comparator.comparing(Producto::getStock)
                        .thenComparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(producto -> new StockAlertView(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getStock(),
                        producto.getStockMinimo(),
                        producto.getStock() == 0
                                ? "Producto Agotado"
                                : "Quedan " + producto.getStock() + " de minimo " + producto.getStockMinimo()))
                .toList();
    }

    public record StockAlertView(Long productoId, String producto, Integer stock, Integer stockMinimo, String mensaje) {
    }
}
