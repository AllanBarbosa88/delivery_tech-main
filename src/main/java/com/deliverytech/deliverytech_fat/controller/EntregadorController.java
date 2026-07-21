package com.deliverytech.deliverytech_fat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.deliverytech_fat.dto.req.EntregadorReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.EntregadorResDTO;
import com.deliverytech.deliverytech_fat.enums.StatusEntregador;
import com.deliverytech.deliverytech_fat.service.EntregadorService;

import io.swagger.v3.oas.annotations.Operation; // 🔑 Importação necessária
import io.swagger.v3.oas.annotations.tags.Tag;     // 🔑 Importação necessária
import jakarta.validation.Valid;

@Tag(name = "Entregadores", description = "Gerenciamento de perfis, disponibilidade e status da frota de entregadores")
@RestController
@RequestMapping("/api/entregadores")
@CrossOrigin(origins = "*")
public class EntregadorController {

    @Autowired
    private EntregadorService entregadorService;

    @Operation(
        summary = "Cadastrar um novo entregador", 
        description = "Persiste as informações de perfil, veículo e dados obrigatórios de um novo entregador diretamente na tabela correspondente do banco de dados."
    )
    @PostMapping
    public ResponseEntity<EntregadorResDTO> cadastrar(@Valid @RequestBody EntregadorReqDTO dto) {
        return ResponseEntity.status(201).body(entregadorService.cadastrar(dto));
    }

    @Operation(
        summary = "Listar entregadores disponíveis", 
        description = "Retorna uma lista em tempo real contendo todos os entregadores que estão com o status lógico igual a 'DISPONIVEL', ou seja, prontos para receber novas chamadas de entrega."
    )
    @GetMapping("/disponiveis")
    public ResponseEntity<List<EntregadorResDTO>> listarDisponiveis() {
        return ResponseEntity.ok(entregadorService.listarDisponiveis());
    }

    @Operation(
        summary = "Buscar entregador por ID", 
        description = "Localiza e retorna as informações completas de um entregador específico através do seu identificador numérico único."
    )
    @GetMapping("/{id}")
    public ResponseEntity<EntregadorResDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entregadorService.buscarPorId(id));
    }

    @Operation(
        summary = "Alterar status do entregador (PUT)", 
        description = "Atualiza o estado operacional do entregador (como DISPONIVEL, EM_TRANSITO ou INDISPONIVEL) diretamente via parâmetro na URL (?status=)."
    )
    @PutMapping("/{id}/status")
    public ResponseEntity<EntregadorResDTO> alterarStatus(
            @PathVariable Long id, 
            @RequestParam StatusEntregador status) {
        return ResponseEntity.ok(entregadorService.alterarStatus(id, status));
    }

    @Operation(
        summary = "Remover um entregador (DELETE)", 
        description = "Executa a exclusão física ou desativação permanente do registro do entregador na base de dados com base no ID fornecido."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        entregadorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
