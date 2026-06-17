package com.deliverytech.deliverytech_fat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.deliverytech_fat.dto.ProdutoDTO;
import com.deliverytech.deliverytech_fat.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Cardápio do Delivery", description = "Endpoints para listagem e cadastro de itens")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Retorna todos os itens do cardápio armazenados no H2/Redis")
    public ResponseEntity<List<ProdutoDTO>> listarCardapio() {
        return ResponseEntity.ok(produtoService.listarCardapio());
    }

    @PostMapping
    @Operation(summary = "Adicionar item", description = "Cadastra um novo produto no banco e limpa o cache do Redis")
    public ResponseEntity<ProdutoDTO> cadastrarProduto(@RequestBody ProdutoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.salvar(dto));
    }
}
