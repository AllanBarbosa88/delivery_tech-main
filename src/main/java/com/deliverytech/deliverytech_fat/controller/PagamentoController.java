package com.deliverytech.deliverytech_fat.controller;

import java.math.BigDecimal;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.deliverytech_fat.dto.res.PagamentoResDTO;
import com.deliverytech.deliverytech_fat.entity.Pagamento;
import com.deliverytech.deliverytech_fat.service.PagamentoService;

import io.swagger.v3.oas.annotations.Operation; // 🔑 Adicionado import para as instruções
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Pagamentos", description = "Operações e integrações relacionadas à validação e processamento de pagamentos")
@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(
        summary = "Processar pagamento do pedido", 
        description = "Aciona o motor financeiro para validar se o valor enviado via Query Param bate centavo por centavo com o total do banco. Se aprovado, registra o pagamento e altera o status do pedido para 'CONFIRMADO' de forma automática na Máquina de Estados."
    )
    @PostMapping("/processar")
    public ResponseEntity<PagamentoResDTO> processarPagamento(
            @RequestParam Long pedidoId, 
            @RequestParam BigDecimal valor) {
        
        Pagamento pagamento = pagamentoService.processarPagamento(pedidoId, valor);
        PagamentoResDTO dto = modelMapper.map(pagamento, PagamentoResDTO.class);
        
        if (pagamento.getPedido() != null) {
            dto.setPedidoId(pagamento.getPedido().getId());
        }
        
        return ResponseEntity.ok(dto);
    }

    @Operation(
        summary = "Buscar pagamento por ID do Pedido", 
        description = "Busca no banco de dados e retorna o recibo de pagamento consolidado e seu respectivo status (APROVADO ou RECUSADO) vinculado ao identificador do pedido."
    )
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagamentoResDTO> buscarPorPedidoId(@PathVariable Long pedidoId) {
        
        Pagamento pagamento = pagamentoService.buscarPorPedidoId(pedidoId);
        PagamentoResDTO dto = modelMapper.map(pagamento, PagamentoResDTO.class);
        
        if (pagamento.getPedido() != null) {
            dto.setPedidoId(pagamento.getPedido().getId());
        }
        
        return ResponseEntity.ok(dto);
    }
}
