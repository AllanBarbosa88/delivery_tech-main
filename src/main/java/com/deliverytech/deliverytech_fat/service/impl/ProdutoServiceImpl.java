package com.deliverytech.deliverytech_fat.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.deliverytech_fat.dto.ProdutoDTO;
import com.deliverytech.deliverytech_fat.dto.req.ProdutoReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ProdutoResDTO;
import com.deliverytech.deliverytech_fat.entity.Produto;
import com.deliverytech.deliverytech_fat.entity.Restaurante;
import com.deliverytech.deliverytech_fat.exception.EntityNotFoundException;
import com.deliverytech.deliverytech_fat.repository.ProdutoRepository;
import com.deliverytech.deliverytech_fat.repository.RestauranteRepository;
import com.deliverytech.deliverytech_fat.service.ProdutoService;

@Service
@Transactional
public class ProdutoServiceImpl implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ProdutoResDTO cadastrar(ProdutoReqDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + dto.getRestauranteId()));

        Produto produto = modelMapper.map(dto, Produto.class);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);

        return modelMapper.map(produtoRepository.save(produto), ProdutoResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "'cardapio'")
    public List<ProdutoDTO> listarCardapio() {
        System.out.println(">>>> BUSCANDO NO BANCO DE DADOS H2 <<<<"); // Log para você ver o cache funcionando
        return produtoRepository.findAll()
            .stream()
            .map(produto -> modelMapper.map(produto, ProdutoDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    @Transactional
    public ProdutoDTO salvar(ProdutoDTO dto) {
        Produto produto = modelMapper.map(dto, Produto.class);
        Produto salvo = produtoRepository.save(produto);
        return modelMapper.map(salvo, ProdutoDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        return modelMapper.map(produto, ProdutoResDTO.class);
    }

    @Override
    public ProdutoResDTO atualizarProduto(Long id, ProdutoReqDTO dto) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco().doubleValue());
        produto.setCategoria(dto.getCategoria());

        return modelMapper.map(produtoRepository.save(produto), ProdutoResDTO.class);
    }

    @Override
    public ProdutoResDTO alterarDisponibilidade(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        produto.setDisponivel(!produto.isDisponivel());

        return modelMapper.map(produtoRepository.save(produto), ProdutoResDTO.class);
    }

    @Override
    public void removerProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        produtoRepository.delete(produto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResDTO> buscarProdutosPorCategoria(String categoria) {
        return produtoRepository.findByCategoriaAndDisponivelTrue(categoria)
            .stream()
            .map(p -> modelMapper.map(p, ProdutoResDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResDTO> buscarProdutosPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue(nome)
            .stream()
            .map(p -> modelMapper.map(p, ProdutoResDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel) {
        List<Produto> produtos;

        if (Boolean.TRUE.equals(disponivel)) {
            produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
        } else {
            produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
        }

        return produtos.stream()
            .map(p -> modelMapper.map(p, ProdutoResDTO.class))
            .collect(Collectors.toList());
    }
}
