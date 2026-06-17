package com.deliverytech.deliverytech_fat.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para representar informações de produtos.
 * Utiliza Java Records para imutabilidade e concisão de código.
 */
public record ProdutoDTO(
    UUID id,
    String nome,
    String descricao,
    BigDecimal preco,
    Integer estoque,
    LocalDateTime dataCadastro,
    List<String> tags
) {
    // Compact Constructor (Recurso de Records) para validações de integridade
    public ProdutoDTO {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }
        // Garante que a lista de tags nunca seja nula (imutabilidade defensiva)
        tags = tags == null ? List.of() : List.copyOf(tags);
    }

    // Exemplo de Factory Method (Método de fábrica estático)
    public static ProdutoDTO criarNovo(String nome, BigDecimal preco) {
        return new ProdutoDTO(
            UUID.randomUUID(),
            nome,
            null,
            preco,
            0,
            LocalDateTime.now(),
            List.of()
        );
    }
}
