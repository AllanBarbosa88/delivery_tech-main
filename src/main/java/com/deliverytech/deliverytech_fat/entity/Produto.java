package com.deliverytech.deliverytech_fat.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nome;
    private String descricao;
    private double preco;
    private String categoria;
    private boolean disponivel;
    private Integer validade;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

}
