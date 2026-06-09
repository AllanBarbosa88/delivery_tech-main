package com.deliverytech.deliverytech_fat.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private String categoria;
    private boolean ativo;
    private Integer avaliacao;

    private BigDecimal taxaEntrega;

   @OneToMany(mappedBy = "restaurante")
   private List<Pedido> pedidos;

   @OneToMany(mappedBy = "restaurante")
   private List<Pedido> produtos;

}
