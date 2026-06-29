package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "servicios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "citas"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer duracionMinutos;

    private String descripcion;

    @OneToMany(mappedBy = "servicio")
    @Builder.Default
    private List<CitaServicio> citas = new ArrayList<>();
}
