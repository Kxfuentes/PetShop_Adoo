package com.petshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteRequest {

    @NotBlank
    private String nombre;

    private String telefono;
    private String direccion;

    @Email
    private String correo;
}
