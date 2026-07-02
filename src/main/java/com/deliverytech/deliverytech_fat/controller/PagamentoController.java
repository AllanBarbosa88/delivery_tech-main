package com.deliverytech.deliverytech_fat.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.deliverytech_fat.entity.Pagamento;
import com.deliverytech.deliverytech_fat.service.PagamentoService;

@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/processar")
    public ResponseEntity<Pagamento> processarPagamento(
            @RequestParam Long pedidoId, 
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(pagamentoService.processarPagamento(pedidoId, valor));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Pagamento> buscarPorPedidoId(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.buscarPorPedidoId(pedidoId));
    }
}
