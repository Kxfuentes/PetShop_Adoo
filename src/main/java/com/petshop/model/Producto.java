package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.petshop.exception.StockInsuficienteException;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "movimientos", "categoria"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @NotNull
    @Min(0)
    @Column(name = "stock_minimo", nullable = false)
    @Builder.Default
    private Integer stockMinimo = 5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "producto")
    @Builder.Default
    private List<Inventario> movimientos = new ArrayList<>();

    public boolean verificarDisponibilidad(int cantidad) {
        return this.stock >= cantidad;
    }

    public BigDecimal calcularSubtotal(int cantidad) {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public void actualizarStock(int cantidad, boolean esEntrada) {
        int nuevoStock = esEntrada ? this.stock + cantidad : this.stock - cantidad;
        if (nuevoStock < 0) {
            throw new StockInsuficienteException(
                    "El stock del producto '" + nombre + "' no puede ser negativo. Stock actual: " + stock);
        }
        this.stock = nuevoStock;
    }

    public boolean requiereAlertaStock() {
        return stock <= stockMinimo;
    }
}
