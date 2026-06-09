package com.deliverytech.deliverytech_fat.dto.res;

import com.deliverytech.deliverytech_fat.entity.Usuario;
import com.deliverytech.deliverytech_fat.enums.Role;
 
import java.time.LocalDateTime;
 
public class UserResDTO {
 
    private Long id;
    private String nome;
    private String email;
    private Role role;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private Long restauranteId;
 
    public UserResDTO() {}
 
    public UserResDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
        this.ativo = usuario.getAtivo();
        this.dataCriacao = usuario.getDataCriacao();
        this.restauranteId = usuario.getRestauranteId();
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
 
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
 
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
 
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
 
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
 
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
}
