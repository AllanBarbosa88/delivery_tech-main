package com.deliverytech.deliverytech_fat.service;

import com.deliverytech.deliverytech_fat.dto.req.PedidoReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.PedidoResDTO;
import com.deliverytech.deliverytech_fat.dto.ItemPedidoDTO;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import java.math.BigDecimal;
import java.util.List;

public interface PedidoService {

    PedidoResDTO criarPedido(PedidoReqDTO dto);

    PedidoResDTO buscarPedidoPorId(Long id);

    List<PedidoResDTO> buscarPedidosPorCliente(Long clienteId);

    PedidoResDTO atualizarStatusPedido(Long id, StatusPedido status);

    BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens);

    void cancelarPedido(Long id);
}
