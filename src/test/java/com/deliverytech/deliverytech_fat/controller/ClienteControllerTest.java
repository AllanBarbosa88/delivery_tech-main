package com.deliverytech.deliverytech_fat.controller;

import com.deliverytech.deliverytech_fat.dto.req.ClienteReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ClienteResDTO;
import com.deliverytech.deliverytech_fat.entity.Cliente;
import com.deliverytech.deliverytech_fat.repository.ClienteRepository;
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
@DisplayName("Testes de Integração do ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    // ─── dados de suporte ──────────────────────────────────────────────────

    private Cliente clienteSalvo;

    @BeforeEach
    void setUp() {
        // clienteRepository.deleteAll();

        Cliente cliente = new Cliente();
        cliente.setNome("João Teste");
        cliente.setEmail("joao.teste@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua Teste, 100");
        cliente.setAtivo(true);
        clienteSalvo = clienteRepository.save(cliente);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // POST /api/clientes
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve criar cliente com dados válidos → 201")
    void should_CreateCliente_When_ValidData() throws Exception {
        ClienteReqDTO novoCliente = new ClienteReqDTO();
        novoCliente.setNome("Maria Silva");
        novoCliente.setEmail("maria.nova@email.com");
        novoCliente.setTelefone("11888888888");
        novoCliente.setEndereco("Rua B, 200");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoCliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Maria Silva")))
                .andExpect(jsonPath("$.email", is("maria.nova@email.com")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.ativo", is(true)));
    }

    @Test
    @DisplayName("Deve retornar 409 quando email já existe")
    void should_ReturnConflict_When_EmailAlreadyExists() throws Exception {
        // email já está cadastrado no setUp
        ClienteReqDTO duplicado = new ClienteReqDTO();
        duplicado.setNome("Outro Nome");
        duplicado.setEmail("joao.teste@email.com"); // duplicado
        duplicado.setTelefone("11777777777");
        duplicado.setEndereco("Rua C, 300");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicado)))
                .andExpect(status().is4xxClientError()); // 409 Conflict ou 400 Bad Request
    }

    @Test
    @DisplayName("Deve retornar 400 quando dados inválidos (nome vazio)")
    void should_ReturnBadRequest_When_InvalidData() throws Exception {
        ClienteReqDTO invalido = new ClienteReqDTO();
        invalido.setNome("");        // inválido
        invalido.setEmail("email-invalido"); // inválido

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GET /api/clientes/{id}
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve buscar cliente por ID existente → 200")
    void should_ReturnCliente_When_IdExists() throws Exception {
        mockMvc.perform(get("/api/clientes/{id}", clienteSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clienteSalvo.getId().intValue())))
                .andExpect(jsonPath("$.nome", is("João Teste")))
                .andExpect(jsonPath("$.email", is("joao.teste@email.com")));
    }

    @Test
    @DisplayName("Deve retornar 404 quando cliente não existe")
    void should_ReturnNotFound_When_ClienteNotExists() throws Exception {
        mockMvc.perform(get("/api/clientes/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GET /api/clientes
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve listar clientes ativos → 200")
    void should_ReturnActiveClientes_When_Listed() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].ativo", is(true)));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GET /api/clientes/email/{email}
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve buscar cliente por email existente → 200")
    void should_ReturnCliente_When_EmailExists() throws Exception {
        mockMvc.perform(get("/api/clientes/email/{email}", "joao.teste@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("joao.teste@email.com")));
    }

    @Test
    @DisplayName("Deve retornar 404 quando email não existe")
    void should_ReturnNotFound_When_EmailNotExists() throws Exception {
        mockMvc.perform(get("/api/clientes/email/{email}", "naoexiste@email.com"))
                .andExpect(status().isNotFound());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PUT /api/clientes/{id}
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve atualizar cliente existente → 200")
    void should_UpdateCliente_When_ClienteExists() throws Exception {
        ClienteReqDTO atualizado = new ClienteReqDTO();
        atualizado.setNome("Nome Atualizado");
        atualizado.setEmail("joao.teste@email.com"); // mesmo email
        atualizado.setTelefone("11777777777");
        atualizado.setEndereco("Rua Nova, 999");

        mockMvc.perform(put("/api/clientes/{id}", clienteSalvo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Nome Atualizado")))
                .andExpect(jsonPath("$.telefone", is("11777777777")));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar cliente inexistente")
    void should_ReturnNotFound_When_UpdateNonExistentCliente() throws Exception {
        ClienteReqDTO atualizado = new ClienteReqDTO();
        atualizado.setNome("Qualquer");
        atualizado.setEmail("qualquer@email.com");
        atualizado.setTelefone("11000000000");
        atualizado.setEndereco("Rua X");

        mockMvc.perform(put("/api/clientes/{id}", 999999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isNotFound());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PATCH /api/clientes/{id}/status
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve ativar/desativar cliente → 200")
    void should_ToggleStatus_When_ClienteExists() throws Exception {
        // cliente está ativo → deve desativar
        mockMvc.perform(patch("/api/clientes/{id}/status", clienteSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo", is(false)));

        // segunda chamada → deve ativar novamente
        mockMvc.perform(patch("/api/clientes/{id}/status", clienteSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo", is(true)));
    }
}