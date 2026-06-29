package com.petshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequest {

    @NotBlank
    private String nombre;

    private String descripcion;
}
