package com.deliverytech.deliverytech_fat.service;

import java.util.List;

import com.deliverytech.deliverytech_fat.dto.ProdutoDTO;
import com.deliverytech.deliverytech_fat.dto.req.ProdutoReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ProdutoResDTO;

/**
 * Contrato (interface) para operações sobre produtos.
 *
 * Observação: antes esse componente era uma classe com implementação e anotações de Spring.
 * Para permitir que `ProdutoServiceImpl` seja injetado como bean e resolva conflitos de
 * implementação, este arquivo foi convertido em interface contendo as assinaturas usadas
 * pelos controllers e pela implementação.
 */
public interface ProdutoService {

    List<ProdutoDTO> listarCardapio();

    ProdutoDTO salvar(ProdutoDTO dto);

    ProdutoResDTO cadastrar(ProdutoReqDTO dto);

    ProdutoResDTO buscarProdutoPorId(Long id);

    ProdutoResDTO atualizarProduto(Long id, ProdutoReqDTO dto);

    ProdutoResDTO alterarDisponibilidade(Long id);

    void removerProduto(Long id);

    List<ProdutoResDTO> buscarProdutosPorCategoria(String categoria);

    List<ProdutoResDTO> buscarProdutosPorNome(String nome);

    List<ProdutoResDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel);
}
