package com.deliverytech.deliverytech_fat.controller;

import com.deliverytech.deliverytech_fat.dto.ItemPedidoDTO;
import com.deliverytech.deliverytech_fat.dto.StatusPedidoDTO;
import com.deliverytech.deliverytech_fat.dto.req.PedidoReqDTO;
import com.deliverytech.deliverytech_fat.entity.Cliente;
import com.deliverytech.deliverytech_fat.entity.Produto;
import com.deliverytech.deliverytech_fat.entity.Restaurante;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import com.deliverytech.deliverytech_fat.repository.ClienteRepository;
import com.deliverytech.deliverytech_fat.repository.PedidoRepository;
import com.deliverytech.deliverytech_fat.repository.ProdutoRepository;
import com.deliverytech.deliverytech_fat.repository.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(roles = "ADMIN")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Testes de Integração do PedidoController")
class PedidoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private PedidoRepository pedidoRepository;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        clienteRepository.deleteAll();
        restauranteRepository.deleteAll();

        restaurante = new Restaurante();
        restaurante.setNome("Restaurante Teste IT");
        restaurante.setCategoria("Brasileira");
        restaurante.setEndereco("Rua Teste, 1");
        restaurante.setTelefone("11999990000");
        restaurante.setTaxaEntrega(BigDecimal.valueOf(5.00));
        restaurante.setAtivo(true);
        restaurante = restauranteRepository.save(restaurante);

        cliente = new Cliente();
        cliente.setNome("Cliente Teste IT");
        cliente.setEmail("cliente.it@email.com");
        cliente.setTelefone("11999991111");
        cliente.setEndereco("Rua Cliente, 100");
        cliente.setAtivo(true);
        cliente = clienteRepository.save(cliente);

        produto = new Produto();
        produto.setNome("Pizza Teste");
        produto.setDescricao("Pizza para testes de integração");
        produto.setPreco(29.90);
        produto.setCategoria("Pizza");
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);
        produto = produtoRepository.save(produto);
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    private PedidoReqDTO buildPedidoDTO(int quantidade) {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId((long) produto.getId());
        item.setQuantidade(quantidade);

        PedidoReqDTO dto = new PedidoReqDTO();
        dto.setClienteId(cliente.getId());
        dto.setRestauranteId(restaurante.getId());
        dto.setEnderecoEntrega("Rua Entrega, 200");
        dto.setItens(List.of(item));
        return dto;
    }

    private Long criarPedidoERetornarId() throws Exception {
        String response = mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildPedidoDTO(1))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // POST /api/pedidos
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve criar pedido com dados válidos → 201")
    void should_CreatePedido_When_ValidData() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildPedidoDTO(1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    @DisplayName("Deve retornar 404 quando produto não existe")
    void should_ReturnError_When_ProductNotExists() throws Exception {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(999999L);
        item.setQuantidade(1);

        PedidoReqDTO dto = new PedidoReqDTO();
        dto.setClienteId(cliente.getId());
        dto.setRestauranteId(restaurante.getId());
        dto.setEnderecoEntrega("Rua Entrega, 200");
        dto.setItens(List.of(item));

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", containsString("não encontrado")));
    }

    @Test
    @DisplayName("Deve retornar erro quando produto não pertence ao restaurante")
    void should_ReturnError_When_ProductNotFromRestaurante() throws Exception {
        Restaurante outro = new Restaurante();
        outro.setNome("Outro Restaurante");
        outro.setCategoria("Japonesa");
        outro.setEndereco("Rua Outra, 2");
        outro.setTelefone("11999992222");
        outro.setTaxaEntrega(BigDecimal.valueOf(8.00));
        outro.setAtivo(true);
        outro = restauranteRepository.save(outro);

        // produto pertence ao restaurante original, mas dto usa "outro"
        PedidoReqDTO dto = buildPedidoDTO(1);
        dto.setRestauranteId(outro.getId());

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar pedido sem itens")
    void should_ReturnBadRequest_When_NoItems() throws Exception {
        PedidoReqDTO dto = new PedidoReqDTO();
        dto.setClienteId(cliente.getId());
        dto.setRestauranteId(restaurante.getId());
        dto.setEnderecoEntrega("Rua Entrega, 200");
        dto.setItens(List.of());

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro quando cliente inativo tenta criar pedido")
    void should_ReturnError_When_ClienteInativo() throws Exception {
        cliente.setAtivo(false);
        clienteRepository.save(cliente);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildPedidoDTO(1))))
                .andExpect(status().is4xxClientError());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GET /api/pedidos/{id}
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve buscar pedido por ID existente → 200")
    void should_ReturnPedido_When_IdExists() throws Exception {
        Long pedidoId = criarPedidoERetornarId();

        mockMvc.perform(get("/api/pedidos/{id}", pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(pedidoId.intValue())))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    @DisplayName("Deve retornar 404 quando pedido não existe")
    void should_ReturnNotFound_When_PedidoNotExists() throws Exception {
        mockMvc.perform(get("/api/pedidos/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GET /api/pedidos/cliente/{clienteId}
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve buscar histórico de pedidos do cliente → 200")
    void should_ReturnClientePedidos_When_ClienteExists() throws Exception {
        criarPedidoERetornarId();

        mockMvc.perform(get("/api/pedidos/cliente/{id}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para cliente sem pedidos")
    void should_ReturnEmptyList_When_ClienteHasNoPedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos/cliente/{id}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PATCH /api/pedidos/{id}/status
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve atualizar status PENDENTE → CONFIRMADO")
    void should_UpdateStatus_When_TransicaoValida() throws Exception {
        Long pedidoId = criarPedidoERetornarId();

        StatusPedidoDTO statusDTO = new StatusPedidoDTO();
        statusDTO.setStatus(StatusPedido.CONFIRMADO);

        mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMADO")));
    }

    @Test
    @DisplayName("Deve retornar erro ao fazer transição inválida PENDENTE → ENTREGUE")
    void should_ReturnError_When_TransicaoInvalida() throws Exception {
        Long pedidoId = criarPedidoERetornarId();

        StatusPedidoDTO statusDTO = new StatusPedidoDTO();
        statusDTO.setStatus(StatusPedido.ENTREGUE);

        mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().is4xxClientError());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DELETE /api/pedidos/{id}
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve cancelar pedido PENDENTE → 204")
    void should_CancelPedido_When_StatusPendente() throws Exception {
        Long pedidoId = criarPedidoERetornarId();

        mockMvc.perform(delete("/api/pedidos/{id}", pedidoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/pedidos/{id}", pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELADO")));
    }

    @Test
    @DisplayName("Deve retornar 404 ao cancelar pedido inexistente")
    void should_ReturnNotFound_When_CancelNonExistentPedido() throws Exception {
        mockMvc.perform(delete("/api/pedidos/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // POST /api/pedidos/calcular
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve calcular total de itens sem criar pedido → 200")
    void should_CalculateTotal_When_ValidItems() throws Exception {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId((long) produto.getId());
        item.setQuantidade(3);

        mockMvc.perform(post("/api/pedidos/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(item))))
                .andExpect(status().isOk());
    }
}