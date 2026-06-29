package com.petshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReprogramarCitaRequest {

    @NotNull
    private LocalDateTime fechaHora;
}
