package com.deliverytech.deliverytech_fat.dto.res;

import java.time.LocalDateTime;

import com.deliverytech.deliverytech_fat.enums.StatusEntregador;

public record EntregadorResDTO(
    Long id,
    String nome,
    String email,
    String telefone,
    String placaVeiculo,
    StatusEntregador status,
    Boolean ativo,
    LocalDateTime dataCriacao
) {}
