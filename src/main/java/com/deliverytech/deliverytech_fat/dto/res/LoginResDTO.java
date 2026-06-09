package com.deliverytech.deliverytech_fat.dto.res;
 
public class LoginResDTO {
 
    private String token;
    private String tipo = "Bearer";
    private Long expiracao;
    private UserResDTO usuario;
 
    public LoginResDTO() {}
 
    public LoginResDTO(String token, Long expiracao, UserResDTO usuario) {
        this.token = token;
        this.expiracao = expiracao;
        this.usuario = usuario;
    }
 
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
 
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
 
    public Long getExpiracao() { return expiracao; }
    public void setExpiracao(Long expiracao) { this.expiracao = expiracao; }
 
    public UserResDTO getUsuario() { return usuario; }
    public void setUsuario(UserResDTO usuario) { this.usuario = usuario; }
}