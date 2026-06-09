package com.deliverytech.deliverytech_fat.service;

import com.deliverytech.deliverytech_fat.dto.req.ProdutoReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ProdutoResDTO;
import java.util.List;

public interface ProdutoService {

    ProdutoResDTO cadastrar(ProdutoReqDTO dto);

    ProdutoResDTO buscarProdutoPorId(Long id);

    ProdutoResDTO atualizarProduto(Long id, ProdutoReqDTO dto);

    ProdutoResDTO alterarDisponibilidade(Long id);

    void removerProduto(Long id);

    List<ProdutoResDTO> buscarProdutosPorCategoria(String categoria);

    List<ProdutoResDTO> buscarProdutosPorNome(String nome);

    List<ProdutoResDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel);
}
