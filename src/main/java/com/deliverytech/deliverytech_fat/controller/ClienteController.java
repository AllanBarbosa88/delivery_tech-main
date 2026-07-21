package com.deliverytech.deliverytech_fat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.deliverytech_fat.dto.req.ClienteReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ClienteResDTO;
import com.deliverytech.deliverytech_fat.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation; // 🔑 Importação necessária
import io.swagger.v3.oas.annotations.tags.Tag;     // 🔑 Importação necessária
import jakarta.validation.Valid;

@Tag(name = "Clientes", description = "Mapeamento, cadastro e gerenciamento de perfis de Clientes")
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(
        summary = "Cadastrar um novo cliente", 
        description = "Salva as informações cadastrais básicas do cliente na tabela filha correspondente no banco de dados H2."
    )
    @PostMapping
    public ResponseEntity<ClienteResDTO> cadastrarCliente(@Valid @RequestBody ClienteReqDTO dto) {
        ClienteResDTO cliente = clienteService.cadastrarCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @Operation(
        summary = "Buscar cliente por ID", 
        description = "Localiza e retorna o perfil completo de um cliente específico cadastrado através do seu identificador numérico."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResDTO> buscarPorId(@PathVariable Long id) {
        ClienteResDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @Operation(
        summary = "Listar todos os clientes ativos", 
        description = "Retorna uma lista contendo todos os clientes que estão com o status de ativação marcado como verdadeiro no sistema."
    )
    @GetMapping
    public ResponseEntity<List<ClienteResDTO>> listarClientesAtivos() {
        List<ClienteResDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    @Operation(
        summary = "Atualizar dados do cliente (PUT)", 
        description = "Substitui os dados cadastrais (como telefone ou endereço) de um cliente existente com base no ID fornecido."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResDTO> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteReqDTO dto) {
        ClienteResDTO cliente = clienteService.atualizarCliente(id, dto);
        return ResponseEntity.ok(cliente);
    }

    @Operation(
        summary = "Ativar ou desativar cliente (PATCH)", 
        description = "Inverte o estado lógico do atributo 'ativo' do cliente. Clientes desativados ficam impedidos de realizar novos pedidos."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClienteResDTO> ativarDesativarCliente(@PathVariable Long id) {
        ClienteResDTO cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(cliente);
    }

    @Operation(
        summary = "Buscar cliente por e-mail", 
        description = "Busca na base de dados as informações de um perfil de cliente correspondente ao endereço de e-mail fornecido."
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResDTO> buscarPorEmail(@PathVariable String email) {
        ClienteResDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

    @Operation(
        summary = "Limpar cache de clientes", 
        description = "Evita inconsistência de leitura de dados limpando de forma atômica o cache de memória do ecossistema de clientes."
    )
    @CacheEvict(value = "clientes", allEntries = true)
    @GetMapping("/limpar-cache")
    public ResponseEntity<Void> limparCache() {
        return ResponseEntity.ok().build();
    }
}
