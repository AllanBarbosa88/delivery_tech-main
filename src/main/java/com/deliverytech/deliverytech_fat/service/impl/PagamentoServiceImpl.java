package com.deliverytech.deliverytech_fat.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.deliverytech_fat.entity.Pagamento;
import com.deliverytech.deliverytech_fat.entity.Pedido;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import com.deliverytech.deliverytech_fat.exception.BusinessException;
import com.deliverytech.deliverytech_fat.exception.EntityNotFoundException;
import com.deliverytech.deliverytech_fat.repository.PagamentoRepository;
import com.deliverytech.deliverytech_fat.repository.PedidoRepository;
import com.deliverytech.deliverytech_fat.service.PagamentoService;

@Service
@Transactional
public class PagamentoServiceImpl implements PagamentoService {

    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private PedidoRepository pedidoRepository;

    @Override
    public Pagamento processarPagamento(Long pedidoId, BigDecimal valor) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new BusinessException("Pagamento só pode ser processado para pedidos PENDENTES");
        }

        if (pedido.getValorTotal().compareTo(valor) != 0) {
            throw new BusinessException("Valor pago divergente do valor total do pedido!");
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValorPago(valor);
        pagamento.setStatusPagamento("APROVADO"); // Simulação de gateway

        // Regra de Negócio: Se o pagamento foi aprovado, o pedido avança de PENDENTE para CONFIRMADO
        pedido.setStatus(StatusPedido.CONFIRMADO);
        pedidoRepository.save(pedido);

        return pagamentoRepository.save(pagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagamento buscarPorPedidoId(Long pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId)
            .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado para o pedido: " + pedidoId));
    }
}
