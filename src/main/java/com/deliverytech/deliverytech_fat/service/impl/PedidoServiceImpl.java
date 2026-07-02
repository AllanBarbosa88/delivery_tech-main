package com.deliverytech.deliverytech_fat.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.deliverytech_fat.dto.ItemPedidoDTO;
import com.deliverytech.deliverytech_fat.dto.req.PedidoReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.PedidoResDTO;
import com.deliverytech.deliverytech_fat.entity.Cliente;
import com.deliverytech.deliverytech_fat.entity.Entregador;
import com.deliverytech.deliverytech_fat.entity.ItemPedido;
import com.deliverytech.deliverytech_fat.entity.Pedido;
import com.deliverytech.deliverytech_fat.entity.Produto;
import com.deliverytech.deliverytech_fat.entity.Restaurante;
import com.deliverytech.deliverytech_fat.enums.StatusEntregador;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import com.deliverytech.deliverytech_fat.exception.BusinessException;
import com.deliverytech.deliverytech_fat.exception.EntityNotFoundException;
import com.deliverytech.deliverytech_fat.repository.ClienteRepository;
import com.deliverytech.deliverytech_fat.repository.EntregadorRepository;
import com.deliverytech.deliverytech_fat.repository.PedidoRepository;
import com.deliverytech.deliverytech_fat.repository.ProdutoRepository;
import com.deliverytech.deliverytech_fat.repository.RestauranteRepository;
import com.deliverytech.deliverytech_fat.service.PedidoService;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private EntregadorRepository entregadorRepository;
    @Autowired private ModelMapper modelMapper;

    @Override
    @Transactional
    public PedidoResDTO criarPedido(PedidoReqDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        if (!cliente.isAtivo())
            throw new BusinessException("Cliente inativo não pode fazer pedidos");

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));
        if (!restaurante.isAtivo())
            throw new BusinessException("Restaurante não está disponível");

        List<ItemPedido> itensPedido = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProdutoId()));

            if (!produto.isDisponivel())
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            if (!produto.getRestaurante().getId().equals(dto.getRestauranteId()))
                throw new BusinessException("Produto não belongs ao restaurante selecionado");

            BigDecimal subtotalItem = BigDecimal.valueOf(produto.getPreco())
                .multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setSubtotal(subtotalItem.doubleValue());

            itensPedido.add(item);
            subtotal = subtotal.add(subtotalItem);
        }

        BigDecimal taxaEntrega = restaurante.getTaxaEntrega();
        BigDecimal valorTotal = subtotal.add(taxaEntrega);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setEnderecoEntrega(dto.getEnderecoEntrega());
        pedido.setSubTotal(subtotal);                        
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(valorTotal);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        for (ItemPedido item : itensPedido) {
            item.setPedido(pedidoSalvo);
        }
        pedidoSalvo.setItens(itensPedido);

        return modelMapper.map(pedidoSalvo, PedidoResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));
        return modelMapper.map(pedido, PedidoResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResDTO> buscarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId)  
            .stream()
            .map(p -> modelMapper.map(p, PedidoResDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public PedidoResDTO atualizarStatusPedido(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (!isTransicaoValida(pedido.getStatus(), novoStatus))
            throw new BusinessException("Transição de status inválida: "
                + pedido.getStatus() + " -> " + novoStatus);

        pedido.setStatus(novoStatus);
        return modelMapper.map(pedidoRepository.save(pedido), PedidoResDTO.class);
    }

    @Override
    public PedidoResDTO alterarStatus(Long id, StatusPedido status) {
        return this.atualizarStatusPedido(id, status);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedidoDTO item : itens) {
            Produto produto = produtoRepository.findById(item.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
            total = total.add(
                BigDecimal.valueOf(produto.getPreco())
                    .multiply(BigDecimal.valueOf(item.getQuantidade()))
            );
        }
        return total;
    }

    @Override
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (!podeSerCancelado(pedido.getStatus()))
            throw new BusinessException("Pedido não pode ser cancelado no status: " + pedido.getStatus());

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    @Override
    public PedidoResDTO despacharPedido(Long pedidoId, Long entregadorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        Entregador entregador = entregadorRepository.findById(entregadorId)
            .orElseThrow(() -> new EntityNotFoundException("Entregador não encontrado"));

        // Regra de Negócio: Permite despachar se estiver PREPARANDO ou se já foi aceito como DESPACHADO
        if (pedido.getStatus() != StatusPedido.PREPARANDO && pedido.getStatus() != StatusPedido.DESPACHADO) {
            throw new BusinessException("Pedido não pode ser despachado no status atual: " + pedido.getStatus());
        }

        // Regra de Negócio: O motoboy precisa estar livre
        if (entregador.getStatus() != StatusEntregador.DISPONIVEL) {
            throw new BusinessException("O entregador selecionado está ocupado em outra corrida.");
        }

        // Atualização de estados operacionais
        entregador.setStatus(StatusEntregador.EM_ENTREGA);
        pedido.setEntregador(entregador);
        pedido.setStatus(StatusPedido.EM_ROTA); // Ou StatusPedido.SAIU_PARA_ENTREGA

        entregadorRepository.save(entregador);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return modelMapper.map(pedidoSalvo, PedidoResDTO.class);
    }

    private boolean isTransicaoValida(StatusPedido atual, StatusPedido novo) {
        return switch (atual) {
            case PENDENTE -> novo == StatusPedido.CONFIRMADO || novo == StatusPedido.CANCELADO;
            case CONFIRMADO -> novo == StatusPedido.PREPARANDO || novo == StatusPedido.CANCELADO;
            case PREPARANDO -> novo == StatusPedido.DESPACHADO || novo == StatusPedido.EM_ROTA || novo == StatusPedido.SAIU_PARA_ENTREGA;
            case DESPACHADO -> novo == StatusPedido.EM_ROTA || novo == StatusPedido.SAIU_PARA_ENTREGA;
            case EM_ROTA, SAIU_PARA_ENTREGA -> novo == StatusPedido.ENTREGUE;
            default -> false;
        };
    }

    private boolean podeSerCancelado(StatusPedido status) {
        return status == StatusPedido.PENDENTE || status == StatusPedido.CONFIRMADO;
    }
}