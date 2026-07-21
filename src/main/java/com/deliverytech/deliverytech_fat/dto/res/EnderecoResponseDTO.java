package com.deliverytech.deliverytech_fat.dto.res;

public class EnderecoResponseDTO {
    private String cep;
    private String logradouro; // Nome da rua
    private String bairro;
    private String localidade; // Cidade
    private String uf;
    private boolean erro; // Campo que o ViaCEP usa se o CEP não existir

    // Getters e Setters
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public boolean isErro() { return erro; }
    public void setErro(boolean erro) { this.erro = erro; }
}
