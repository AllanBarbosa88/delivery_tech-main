package com.deliverytech.deliverytech_fat.enums;

public enum StatusPedido {
    PENDENTE,
    CONFIRMADO,
    PREPARANDO,
    DESPACHADO,
    EM_ROTA,
    SAIU_PARA_ENTREGA, // Mantém para o código antigo não quebrar
    ENTREGUE,
    CANCELADO
}
