package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ventas"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    private String telefono;

    private String direccion;

    @Email
    private String correo;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Mascota> mascotas = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    @Builder.Default
    private List<Venta> ventas = new ArrayList<>();

    public void agregarMascota(Mascota mascota) {
        mascotas.add(mascota);
        mascota.setCliente(this);
    }
}
