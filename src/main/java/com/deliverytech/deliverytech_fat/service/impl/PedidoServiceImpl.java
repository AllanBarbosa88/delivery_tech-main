package com.deliverytech.deliverytech_fat.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.deliverytech.deliverytech_fat.dto.ItemPedidoDTO;
import com.deliverytech.deliverytech_fat.dto.req.PedidoReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.EnderecoResponseDTO;
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

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final EntregadorRepository entregadorRepository;
    private final ModelMapper modelMapper;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             ClienteRepository clienteRepository,
                             RestauranteRepository restauranteRepository,
                             ProdutoRepository produtoRepository,
                             EntregadorRepository entregadorRepository,
                             ModelMapper modelMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
        this.entregadorRepository = entregadorRepository;
        this.modelMapper = modelMapper;
    }
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
                throw new BusinessException("Produto não pertence ao restaurante selecionado");

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

        // 🌟 CÁLCULO DO FRETE DINÂMICO INTEGRADO
        BigDecimal taxaEntregaBase = restaurante.getTaxaEntrega();
        double distanciaSimuladaKm = Math.min(15.0, Math.max(1.5, dto.getEnderecoEntrega().length() / 4.0));
        BigDecimal freteVariavel = BigDecimal.valueOf(distanciaSimuladaKm).multiply(new BigDecimal("1.50"));
        
        BigDecimal taxaEntregaFinalCalculada = taxaEntregaBase.add(freteVariavel);
        BigDecimal valorTotalGeral = subtotal.add(taxaEntregaFinalCalculada);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataCriacao(LocalDateTime.now());
        
        // 🌟 CORREÇÃO: O pedido nasce PENDENTE (Aguardando o pagamento do cliente)
        pedido.setStatus(StatusPedido.PENDENTE); 
        
        pedido.setEnderecoEntrega(dto.getEnderecoEntrega());
        pedido.setSubTotal(subtotal);                        
        pedido.setTaxaEntrega(taxaEntregaFinalCalculada); // Devolve o frete calculado
        pedido.setValorTotal(valorTotalGeral);            // Devolve o total calculado

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
            throw new BusinessException("Transição de status inválida: " + pedido.getStatus() + " -> " + novoStatus);

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

        if (pedido.getStatus() != StatusPedido.PREPARANDO && pedido.getStatus() != StatusPedido.DESPACHADO) {
            throw new BusinessException("Pedido não pode ser despachado no status atual: " + pedido.getStatus());
        }

        if (entregador.getStatus() != StatusEntregador.DISPONIVEL) {
            throw new BusinessException("O entregador selecionado não está disponível");
        }

        pedido.setStatus(StatusPedido.DESPACHADO);
        return modelMapper.map(pedidoRepository.save(pedido), PedidoResDTO.class);
    }

            // 🛠️ Métodos Auxiliares da Máquina de Estados
         private boolean isTransicaoValida(StatusPedido atual, StatusPedido novo) {
         if (atual == StatusPedido.PENDENTE && novo == StatusPedido.CONFIRMADO) return true;
         if (atual == StatusPedido.CONFIRMADO && novo == StatusPedido.PREPARANDO) return true;
         if (atual == StatusPedido.PREPARANDO && novo == StatusPedido.DESPACHADO) return true;
         if (atual == StatusPedido.DESPACHADO && novo == StatusPedido.EM_ROTA) return true;
         if (atual == StatusPedido.EM_ROTA && novo == StatusPedido.ENTREGUE) return true;
         if (novo == StatusPedido.CANCELADO && podeSerCancelado(atual)) return true;
         return false;
        }
        private boolean podeSerCancelado(StatusPedido status) {
            return status == StatusPedido.PENDENTE || status == StatusPedido.CONFIRMADO;
        }
            @Override
    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscarEnderecoPorCep(String cep) {
        // 1. Limpa o CEP tirando traços e espaços
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new BusinessException("CEP inválido. Deve conter exatamente 8 dígitos.");
        }
        
        // 2. Monta a URL perfeita exigida pelo ViaCEP (com as barras corretas!)
        String url = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Faz a busca real na internet
            EnderecoResponseDTO endereco = restTemplate.getForObject(url, EnderecoResponseDTO.class);
            
            if (endereco == null || endereco.isErro()) {
                throw new EntityNotFoundException("CEP não encontrado na base do ViaCEP.");
            }
            
            return endereco;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao conectar com o serviço de CEP externo.");
        }
    }

    }
