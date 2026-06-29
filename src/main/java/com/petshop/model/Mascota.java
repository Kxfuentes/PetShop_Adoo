package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mascotas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cliente", "citas"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    private String especie;

    private String raza;

    private Integer edad;

    private BigDecimal peso;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "mascota")
    @Builder.Default
    private List<CitaServicio> citas = new ArrayList<>();
}
