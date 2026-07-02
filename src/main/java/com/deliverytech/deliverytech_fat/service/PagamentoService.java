package com.deliverytech.deliverytech_fat.service;

import java.math.BigDecimal;

import com.deliverytech.deliverytech_fat.entity.Pagamento;

public interface PagamentoService {
    Pagamento processarPagamento(Long pedidoId, BigDecimal valor);
    Pagamento buscarPorPedidoId(Long pedidoId);
}
