package com.deliverytech.deliverytech_fat.dto;

import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public class StatusPedidoDTO {

    @NotNull(message = "Status é obrigatório")
    private StatusPedido status;

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
}
