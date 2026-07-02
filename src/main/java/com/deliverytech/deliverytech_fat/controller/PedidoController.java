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
import com.deliverytech.deliverytech_fat.dto.res.PedidoResDTO;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;
import com.deliverytech.deliverytech_fat.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResDTO> criarPedido(@Valid @RequestBody PedidoReqDTO dto) {
        PedidoResDTO pedido = pedidoService.criarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResDTO> buscarPorId(@PathVariable Long id) {
        PedidoResDTO pedido = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResDTO>> buscarPorCliente(@PathVariable Long clienteId) {
        List<PedidoResDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusPedidoDTO statusDTO) {
        PedidoResDTO pedido = pedidoService.atualizarStatusPedido(id, statusDTO.getStatus());
        return ResponseEntity.ok(pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calcular")
    public ResponseEntity<BigDecimal> calcularTotal(@Valid @RequestBody List<ItemPedidoDTO> itens) {
        BigDecimal total = pedidoService.calcularTotalPedido(itens);
        return ResponseEntity.ok(total);
    }
    @PutMapping("/{pedidoId}/despachar")
    public ResponseEntity<PedidoResDTO> despachar(
            @PathVariable Long pedidoId,
            @RequestParam Long entregadorId) {
        return ResponseEntity.ok(pedidoService.atualizarStatusPedido(pedidoId, StatusPedido.valueOf("DESPACHADO")));
    }
        // SE O ERRO FOR NA ROTA DE ATUALIZAÇÃO GERAL:
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResDTO> atualizarStatus(
        @PathVariable Long id, 
        @RequestParam StatusPedido status) { // <-- Alterado de String para StatusPedido
    return ResponseEntity.ok(pedidoService.alterarStatus(id, status));
    }


}
