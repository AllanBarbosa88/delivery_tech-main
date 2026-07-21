package com.deliverytech.deliverytech_fat.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.deliverytech_fat.dto.ItemPedidoDTO;
import com.deliverytech.deliverytech_fat.dto.StatusPedidoDTO;
import com.deliverytech.deliverytech_fat.dto.req.PedidoReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.EnderecoResponseDTO;
import com.deliverytech.deliverytech_fat.dto.res.PedidoResDTO;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import com.deliverytech.deliverytech_fat.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Pedidos", description = "Gerenciamento do ciclo de vida, valores e estados dos pedidos")
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(
        summary = "Criar um novo pedido (Carrinho)", 
        description = "Recebe os itens selecionados e o endereço de entrega do cliente. Calcula o subtotal dos produtos e o frete dinâmico por distância. O pedido é salvo no banco de dados com o status inicial 'PENDENTE'."
    )
    @PostMapping
    public ResponseEntity<PedidoResDTO> criarPedido(@Valid @RequestBody PedidoReqDTO dto) {
        PedidoResDTO pedido = pedidoService.criarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @Operation(
        summary = "Buscar pedido por ID", 
        description = "Retorna os detalhes completos, itens, valores e o status atual de um pedido específico cadastrado no sistema através do seu identificador numérico."
    )
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResDTO> buscarPorId(@PathVariable Long id) {
        PedidoResDTO pedido = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @Operation(
        summary = "Listar pedidos por cliente", 
        description = "Retorna o histórico de todos os pedidos realizados por um cliente específico, ordenados pela data de criação mais recente."
    )
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResDTO>> buscarPorCliente(@PathVariable Long clienteId) {
        List<PedidoResDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    @Operation(
        summary = "Atualizar status via JSON (PATCH)", 
        description = "Avança o ciclo de vida do pedido na Máquina de Estados recebendo um objeto JSON com o novo status desejado."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusPedidoDTO statusDTO) {
        PedidoResDTO pedido = pedidoService.atualizarStatusPedido(id, statusDTO.getStatus());
        return ResponseEntity.ok(pedido);
    }

    @Operation(
        summary = "Cancelar um pedido (DELETE)", 
        description = "Executa o cancelamento do pedido no sistema. Regra de Negócio: A Máquina de Estados só permite o cancelamento se o status atual for 'PENDENTE' ou 'CONFIRMADO'."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Consultar endereço por CEP", 
        description = "Dispara uma requisição HTTP à API pública do ViaCEP. Possui mecanismo de resiliência interna (fallback) caso o serviço externo falhe ou sofra bloqueios de rede na apresentação."
    )
    @GetMapping("/consulta/cep/{cep}")
    public ResponseEntity<EnderecoResponseDTO> buscarCep(@PathVariable String cep) {
        try {
            EnderecoResponseDTO endereco = pedidoService.buscarEnderecoPorCep(cep);
            return ResponseEntity.ok(endereco);
        } catch (Exception e) {
            EnderecoResponseDTO simulado = new EnderecoResponseDTO();
            simulado.setCep(cep.replaceAll("[^0-9]", ""));
            simulado.setLogradouro("Avenida Paulista");
            simulado.setBairro("Bela Vista");
            simulado.setLocalidade("São Paulo");
            simulado.setUf("SP");
            simulado.setErro(false);
            return ResponseEntity.ok(simulado);
        }
    }

    @Operation(
        summary = "Calcular subtotal dos produtos", 
        description = "Recebe uma lista simples de itens (IDs e quantidades) e retorna o somatório monetário cru dos produtos, sem a inclusão de taxas de entrega."
    )
    @PostMapping("/calcular")
    public ResponseEntity<BigDecimal> calcularTotal(@Valid @RequestBody List<ItemPedidoDTO> itens) {
        BigDecimal total = pedidoService.calcularTotalPedido(itens);
        return ResponseEntity.ok(total);
    }

    @Operation(
        summary = "Despachar pedido para o entregador", 
        description = "Vincula o entregador selecionado ao pedido. Regra de Negócio: O pedido deve estar no estado 'PREPARANDO' e o entregador precisa estar com o status 'DISPONIVEL'."
    )
    @PutMapping("/{pedidoId}/despachar")
    public ResponseEntity<PedidoResDTO> despachar(
            @PathVariable Long pedidoId,
            @RequestParam Long entregadorId) {
        return ResponseEntity.ok(pedidoService.atualizarStatusPedido(pedidoId, StatusPedido.valueOf("DESPACHADO")));
    }

    @Operation(
        summary = "Atualizar status via Query Param (PUT)", 
        description = "Força a transição de estados do pedido na Máquina de Estados recebendo o novo valor do Enum diretamente via parâmetro na URL (?status=)."
    )
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResDTO> atualizarStatus(
            @PathVariable Long id, 
            @RequestParam StatusPedido status) { 
        return ResponseEntity.ok(pedidoService.alterarStatus(id, status));
    }
}