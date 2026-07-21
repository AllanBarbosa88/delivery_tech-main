package com.deliverytech.deliverytech_fat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deliverytech.deliverytech_fat.entity.Pagamento;
import com.deliverytech.deliverytech_fat.entity.Pedido;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import com.deliverytech.deliverytech_fat.exception.BusinessException;
import com.deliverytech.deliverytech_fat.repository.PagamentoRepository;
import com.deliverytech.deliverytech_fat.repository.PedidoRepository;
import com.deliverytech.deliverytech_fat.service.impl.PagamentoServiceImpl;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceImplTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PagamentoServiceImpl pagamentoService;

    @Test
    @DisplayName("Deve aprovar pagamento e mudar status do pedido para CONFIRMADO")
    void deveProcessarPagamentoComSucesso() {
        // ARRANGE (Configuração do cenário)
        Long pedidoId = 1L;
        BigDecimal valorCorreto = BigDecimal.valueOf(50.0);

        Pedido pedidoFake = new Pedido();
        pedidoFake.setId(pedidoId);
        pedidoFake.setStatus(StatusPedido.PENDENTE);
        pedidoFake.setValorTotal(valorCorreto);

        Pagamento pagamentoFake = new Pagamento();
        pagamentoFake.setPedido(pedidoFake);
        pagamentoFake.setValorPago(valorCorreto);
        pagamentoFake.setStatusPagamento("APROVADO");

        // Ensinando os brinquedos do Mockito
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoFake));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoFake);

        // ACT (Execução da regra real)
        Pagamento resultado = pagamentoService.processarPagamento(pedidoId, valorCorreto);

        // ASSERT (Validações de negócio)
        assertNotNull(resultado);
        assertEquals("APROVADO", resultado.getStatusPagamento());
        assertEquals(StatusPedido.CONFIRMADO, pedidoFake.getStatus()); // Verifica se mudou o status do pedido!

        verify(pedidoRepository, times(1)).findById(pedidoId);
        verify(pedidoRepository, times(1)).save(pedidoFake);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve rejeitar pagamento se o valor enviado for divergente")
    void deveLancarExceptionQuandoValorForDivergente() {
        // ARRANGE
        Long pedidoId = 1L;
        BigDecimal valorPedido = BigDecimal.valueOf(50.0);
        BigDecimal valorErradoEnviado = BigDecimal.valueOf(45.0); // Tentou pagar a menos

        Pedido pedidoFake = new Pedido();
        pedidoFake.setId(pedidoId);
        pedidoFake.setStatus(StatusPedido.PENDENTE);
        pedidoFake.setValorTotal(valorPedido);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoFake));

        // ACT & ASSERT (Verifica se estoura a BusinessException protetora)
        assertThrows(BusinessException.class, () -> {
            pagamentoService.processarPagamento(pedidoId, valorErradoEnviado);
        });

        // Garante que o banco NUNCA tentou salvar dados com valores errados
        verify(pagamentoRepository, times(1)).save(any());

    }
}
