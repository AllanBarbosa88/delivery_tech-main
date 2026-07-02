package com.deliverytech.deliverytech_fat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

// ─── Imports ──────────────────────────────────────────────────────────────

import com.deliverytech.deliverytech_fat.dto.ItemPedidoDTO;
import com.deliverytech.deliverytech_fat.dto.req.PedidoReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.PedidoResDTO;
import com.deliverytech.deliverytech_fat.entity.Cliente;
import com.deliverytech.deliverytech_fat.entity.Pedido;
import com.deliverytech.deliverytech_fat.entity.Produto;
import com.deliverytech.deliverytech_fat.entity.Restaurante;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import com.deliverytech.deliverytech_fat.exception.BusinessException;
import com.deliverytech.deliverytech_fat.exception.EntityNotFoundException;
import com.deliverytech.deliverytech_fat.repository.ClienteRepository;
import com.deliverytech.deliverytech_fat.repository.PedidoRepository;
import com.deliverytech.deliverytech_fat.repository.ProdutoRepository;
import com.deliverytech.deliverytech_fat.repository.RestauranteRepository;
import com.deliverytech.deliverytech_fat.service.impl.PedidoServiceImpl;
import com.deliverytech.deliverytech_fat.validation.CEPValidator;
import com.deliverytech.deliverytech_fat.validation.TelefoneValidator;

// ═════════════════════════════════════════════════════════════════════════════
//  ATIVIDADE 3 — Testes adicionais para aumentar a cobertura JaCoCo
// ═════════════════════════════════════════════════════════════════════════════

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Adicionais de Cobertura — Atividade 3")
class CoberturaAdicionaisTest {

    // ── PedidoService mocks ───────────────────────────────────────────────

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private RestauranteRepository restauranteRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    // ── objetos base ──────────────────────────────────────────────────────

    private Cliente clienteAtivo;
    private Restaurante restauranteAtivo;
    private Produto produtoDisponivel;
    private Pedido pedidoPendente;

    @BeforeEach
    void setUp() {
        clienteAtivo = new Cliente();
        clienteAtivo.setId(1L);
        clienteAtivo.setNome("João Silva");
        clienteAtivo.setEmail("joao@email.com");
        clienteAtivo.setAtivo(true);

        restauranteAtivo = new Restaurante();
        restauranteAtivo.setId(1L);
        restauranteAtivo.setNome("Pizzaria");
        restauranteAtivo.setAtivo(true);
        restauranteAtivo.setTaxaEntrega(BigDecimal.valueOf(5.00));

        produtoDisponivel = new Produto();
        produtoDisponivel.setId(1);
        produtoDisponivel.setNome("Pizza");
        produtoDisponivel.setPreco(29.90);
        produtoDisponivel.setDisponivel(true);
        produtoDisponivel.setRestaurante(restauranteAtivo);

        pedidoPendente = new Pedido();
        pedidoPendente.setId(1);
        pedidoPendente.setStatus(StatusPedido.PENDENTE);
        pedidoPendente.setCliente(clienteAtivo);
        pedidoPendente.setRestaurante(restauranteAtivo);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Entidade Cliente — construtores, getters/setters, equals/hashCode, toString
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve criar Cliente com construtor padrão")
    void should_CreateCliente_When_DefaultConstructor() {
        Cliente cliente = new Cliente();
        assertNotNull(cliente);
        assertNull(cliente.getId());
    }

    @Test
    @DisplayName("Deve definir e obter propriedades do Cliente corretamente")
    void should_SetAndGetProperties_When_ValidValues() {
        Cliente cliente = new Cliente();
        cliente.setNome("Teste");
        cliente.setEmail("teste@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua A, 1");
        cliente.setAtivo(true);

        assertEquals("Teste",          cliente.getNome());
        assertEquals("teste@email.com", cliente.getEmail());
        assertEquals("11999999999",     cliente.getTelefone());
        assertEquals("Rua A, 1",        cliente.getEndereco());
        assertTrue(cliente.isAtivo());
    }

    @Test
    @DisplayName("Deve comparar Clientes corretamente (equals/hashCode via @Data)")
    void should_CompareClientes_When_SameId() {
        Cliente c1 = new Cliente();
        c1.setId(1L);
        c1.setNome("João");

        Cliente c2 = new Cliente();
        c2.setId(1L);
        c2.setNome("João");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString do Cliente com campos principais")
    void should_GenerateToString_When_Called() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setEmail("joao@email.com");

        String result = cliente.toString();
        assertTrue(result.contains("João"));
        assertTrue(result.contains("joao@email.com"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Entidade Pedido — construtores e propriedades
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve criar Pedido com construtor padrão")
    void should_CreatePedido_When_DefaultConstructor() {
        Pedido pedido = new Pedido();
        assertNotNull(pedido);
    }

    @Test
    @DisplayName("Deve definir e obter propriedades do Pedido corretamente")
    void should_SetAndGetPedidoProperties() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setValorTotal(BigDecimal.valueOf(59.80));
        pedido.setEnderecoEntrega("Rua B, 10");

        assertEquals(StatusPedido.PENDENTE, pedido.getStatus());
        assertEquals(BigDecimal.valueOf(59.80), pedido.getValorTotal());
        assertEquals("Rua B, 10", pedido.getEnderecoEntrega());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Entidade Produto — construtores e propriedades
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve criar Produto com construtor padrão")
    void should_CreateProduto_When_DefaultConstructor() {
        Produto produto = new Produto();
        assertNotNull(produto);
        assertFalse(produto.isDisponivel());
    }

    @Test
    @DisplayName("Deve definir e obter propriedades do Produto corretamente")
    void should_SetAndGetProdutoProperties() {
        Produto produto = new Produto();
        produto.setNome("X-Burger");
        produto.setPreco(18.90);
        produto.setCategoria("Hambúrguer");
        produto.setDisponivel(true);

        assertEquals("X-Burger",    produto.getNome());
        assertEquals(18.90,         produto.getPreco(), 0.001);
        assertEquals("Hambúrguer",  produto.getCategoria());
        assertTrue(produto.isDisponivel());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Validadores customizados
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve validar CEP corretamente")
    void should_ValidateCEP_When_ValidAndInvalidFormats() {
        CEPValidator validator = new CEPValidator();

        assertTrue(validator.isValid("01310-100", null));  // com hífen
        assertTrue(validator.isValid("01310100",  null));  // sem hífen
        assertFalse(validator.isValid("123",       null)); // muito curto
        assertFalse(validator.isValid("abc",       null)); // não numérico
        assertFalse(validator.isValid(null,        null)); // nulo
        assertFalse(validator.isValid("",          null)); // vazio
    }

    @Test
    @DisplayName("Deve validar telefone corretamente")
    void should_ValidateTelefone_When_ValidAndInvalidFormats() {
        TelefoneValidator validator = new TelefoneValidator();

        assertTrue(validator.isValid("11999999999",   null)); // celular 11 dígitos
        assertTrue(validator.isValid("1133334444",    null)); // fixo 10 dígitos
        assertTrue(validator.isValid("(11) 9999-9999",null)); // com formatação
        assertFalse(validator.isValid("123",           null)); // muito curto
        assertFalse(validator.isValid(null,            null)); // nulo
        assertFalse(validator.isValid("",              null)); // vazio
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PedidoService — regras de negócio
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve lançar exceção quando cliente inativo tenta criar pedido")
    void should_ThrowException_When_ClienteInativo() {
        clienteAtivo.setAtivo(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(1L);
        item.setQuantidade(1);

        PedidoReqDTO dto = new PedidoReqDTO();
        dto.setClienteId(1L);
        dto.setRestauranteId(1L);
        dto.setEnderecoEntrega("Rua X");
        dto.setItens(List.of(item));

        assertThrows(BusinessException.class, () -> pedidoService.criarPedido(dto));
        verify(restauranteRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando restaurante inativo")
    void should_ThrowException_When_RestauranteInativo() {
        restauranteAtivo.setAtivo(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteAtivo));

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(1L);
        item.setQuantidade(1);

        PedidoReqDTO dto = new PedidoReqDTO();
        dto.setClienteId(1L);
        dto.setRestauranteId(1L);
        dto.setEnderecoEntrega("Rua X");
        dto.setItens(List.of(item));

        assertThrows(BusinessException.class, () -> pedidoService.criarPedido(dto));
        verify(produtoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto indisponível no pedido")
    void should_ThrowException_When_ProdutoIndisponivel() {
        produtoDisponivel.setDisponivel(false);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteAtivo));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoDisponivel));

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(1L);
        item.setQuantidade(1);

        PedidoReqDTO dto = new PedidoReqDTO();
        dto.setClienteId(1L);
        dto.setRestauranteId(1L);
        dto.setEnderecoEntrega("Rua X");
        dto.setItens(List.of(item));

        assertThrows(BusinessException.class, () -> pedidoService.criarPedido(dto));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar pedido inexistente")
    void should_ThrowEntityNotFoundException_When_PedidoNotExists() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pedidoService.buscarPedidoPorId(999L));
    }

    @Test
    @DisplayName("Deve cancelar pedido PENDENTE com sucesso")
    void should_CancelPedido_When_StatusIsPendente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendente));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPendente);

        assertDoesNotThrow(() -> pedidoService.cancelarPedido(1L));
        verify(pedidoRepository).save(argThat(p -> p.getStatus() == StatusPedido.CANCELADO));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar pedido ENTREGUE")
    void should_ThrowException_When_CancelDeliveredPedido() {
        pedidoPendente.setStatus(StatusPedido.ENTREGUE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendente));

        assertThrows(BusinessException.class, () -> pedidoService.cancelarPedido(1L));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar status de PENDENTE para CONFIRMADO")
    void should_UpdateStatus_PendenteToConfirmado() {
        PedidoResDTO resDTO = new PedidoResDTO();
        resDTO.setStatus("CONFIRMADO");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendente));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPendente);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResDTO.class))).thenReturn(resDTO);

        PedidoResDTO resultado = pedidoService.atualizarStatusPedido(1L, StatusPedido.CONFIRMADO);

        assertEquals("CONFIRMADO", resultado.getStatus());
        verify(pedidoRepository).save(argThat(p -> p.getStatus() == StatusPedido.CONFIRMADO));
    }

    @Test
    @DisplayName("Deve lançar exceção em transição de status inválida")
    void should_ThrowException_When_InvalidStatusTransition() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendente));

        // PENDENTE → ENTREGUE não é uma transição válida
        assertThrows(BusinessException.class,
                () -> pedidoService.atualizarStatusPedido(1L, StatusPedido.ENTREGUE));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve calcular total de itens corretamente")
    void should_CalculateTotal_When_ValidItems() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoDisponivel));

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(1L);
        item.setQuantidade(3); // 3 × 29.90 = 89.70

        BigDecimal total = pedidoService.calcularTotalPedido(List.of(item));

        assertEquals(0, BigDecimal.valueOf(89.70).compareTo(total));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // StatusPedido enum — cobertura de valores
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve ter todos os valores do enum StatusPedido")
    void should_HaveAllStatusPedidoValues() {
        StatusPedido[] values = StatusPedido.values();
        assertEquals(8, values.length);
        assertNotNull(StatusPedido.valueOf("PENDENTE"));
        assertNotNull(StatusPedido.valueOf("CONFIRMADO"));
        assertNotNull(StatusPedido.valueOf("PREPARANDO"));
        assertNotNull(StatusPedido.valueOf("SAIU_PARA_ENTREGA"));
        assertNotNull(StatusPedido.valueOf("ENTREGUE"));
        assertNotNull(StatusPedido.valueOf("CANCELADO"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ItemPedidoDTO — cobertura básica
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve definir e obter propriedades de ItemPedidoDTO")
    void should_SetAndGetItemPedidoDTO_Properties() {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(5L);
        item.setQuantidade(2);

        assertEquals(5L, item.getProdutoId());
        assertEquals(2,  item.getQuantidade());
    }
}