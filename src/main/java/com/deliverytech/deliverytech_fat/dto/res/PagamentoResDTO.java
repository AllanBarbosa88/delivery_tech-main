package com.deliverytech.deliverytech_fat.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoResDTO {
    private Long id;
    private Long pedidoId; // Retorna só o ID em vez do objeto pedido inteiro!
    private BigDecimal valorPago;
    private String statusPagamento;
    private LocalDateTime dataPagamento;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
}
