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
    @Autowired private PedidoRepository pedidoRepository; // 👈 Injetar o repositório de pedidos

    @Override
    @Transactional
    public Pagamento processarPagamento(Long pedidoId, BigDecimal valor) {
        // 1. Busca o pedido no banco
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        // 2. Valida se o valor pago é idêntico ao valor_total calculado com o frete
        if (valor.compareTo(pedido.getValorTotal()) != 0) {
            // Se o valor estiver errado, salva o pagamento como RECUSADO
            Pagamento pagamentoFalho = new Pagamento();
            pagamentoFalho.setPedido(pedido);
            pagamentoFalho.setValorPago(valor);
            pagamentoFalho.setStatusPagamento("RECUSADO");
            pagamentoRepository.save(pagamentoFalho);
            
            throw new BusinessException("Pagamento Recusado: O valor enviado não confere com o total do pedido.");
        }

        // 3. Se o valor estiver correto, o motor APROVA o pagamento
        Pagamento pagamentoAprovado = new Pagamento();
        pagamentoAprovado.setPedido(pedido);
        pagamentoAprovado.setValorPago(valor);
        pagamentoAprovado.setStatusPagamento("APROVADO");
        pagamentoRepository.save(pagamentoAprovado);

        // 🌟 4. VIRA A CHAVE DA MÁQUINA DE ESTADOS: Altera o status do pedido para CONFIRMADO!
        pedido.setStatus(StatusPedido.CONFIRMADO);
        pedidoRepository.save(pedido); // Salva o pedido atualizado no H2

        return pagamentoAprovado;
    }

    @Override
    @Transactional(readOnly = true)
    public Pagamento buscarPorPedidoId(Long pedidoId) {
        return pagamentoRepository.findAll().stream()
            .filter(p -> p.getPedido() != null && pedidoId.equals(p.getPedido().getId()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Pagamento não Realizado para o pedido."));
    }
}
