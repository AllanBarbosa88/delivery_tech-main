package com.deliverytech.deliverytech_fat.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EntregadorReqDTO(
    @NotBlank(message = "O nome é obrigatório") String nome,
    @NotBlank(message = "O email é obrigatório") @Email(message = "Email inválido") String email,
    @NotBlank(message = "O telefone é obrigatório") String telefone,
    @NotBlank(message = "A placa do veículo é obrigatória") String placaVeiculo
) {}
