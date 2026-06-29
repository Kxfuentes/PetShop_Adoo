package com.petshop.dto;

import com.petshop.model.enums.RolUsuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private RolUsuario rol;
    private Boolean estado;
}
