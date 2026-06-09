package com.deliverytech.deliverytech_fat.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.deliverytech_fat.enums.StatusPedido;

@Entity
@Data
public class Pedido {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private LocalDateTime dataPedido;
	private Boolean entrega;
	private BigDecimal subTotal;
	private BigDecimal taxaEntrega;
	private BigDecimal valorTotal;
	private String numeroPedido;
	private String enderecoEntrega;

	@Enumerated(EnumType.STRING)
	private StatusPedido status;

	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "restaurante_id")
	private Restaurante restaurante;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<ItemPedido> itens;

}
