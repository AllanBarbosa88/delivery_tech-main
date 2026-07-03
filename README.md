# 🚀 Delivery Tech API

Sistema de delivery moderno desenvolvido com Spring Boot 3.2.x e Java 21, utilizando as mais recentes funcionalidades da linguagem.

## 👤 Identificação do Aluno
* **Nome:** Allan Gutierez Barbosa
* **Turma:** 04104
* **Professor:** Anderson Demoner


## 📋 Sobre o Projeto

Este projeto foi desenvolvido em Conjuto como parte da disciplina **Arquitetura de Sistemas** da Escola Tecnica - FAT, atravez do projeto Qualifica-São Paulo, e representa a base de um sistema de delivery completo. A aplicação demonstra o uso de tecnologias modernas e boas práticas de desenvolvimento.

### 🛠️ Tecnologias Utilizadas


As tecnologias aplicadas neste projeto foram divididas por camadas e responsabilidades técnicas para garantir uma arquitetura escalável e limpa.


### 🌟 Framework Principal & Core

* **Java:** Linguagem de programação base do ecossistema.
* **Spring Boot:** Framework utilizado para acelerar o desenvolvimento do ecossistema backend.
* **Spring Boot Security:** Camada responsável pela segurança, autenticação e autorização do sistema.


### 💾 Persistência de Dados & Infraestrutura

* **Spring Data JPA:** Abstração de persistência para gerenciamento e mapeamento das entidades do banco de dados.
* **H2 Database / SQL:** Banco de dados relacional (com massa de dados configurada via `data.sql` e configurações mapeadas no `application.properties`).


### 🧪 Testes Automatizados & Qualidade de Código

* **JUnit:** Framework padrão para a escrita e execução dos testes unitários.
* **Mockito:** Ferramenta para criação de objetos simulados (Mocks), isolando as regras de negócio durante os testes de serviço.


### 🔄 Mapeamento & Utilitários

* **ModelMapper:** Biblioteca utilizada para realizar o mapeamento entre Entidades e Objetos de Transferência de Dados (DTOs).



## ⚙️ Métodos, Padrões e Arquitetura do Projeto

O desenvolvimento seguiu as melhores práticas de engenharia de software para garantir que o código seja testável, legível e de fácil manutenção.

### 🏛️ Padrões de Projeto & Arquitetura (Design Patterns)

* **Arquitetura em Camadas (Layered Architecture):** Divisão clara de responsabilidades entre `Controller`, `Service`, `Repository`, `DTO` e `Entity`.
* **Data Transfer Object (DTO):** Separação estrita entre os dados que transitam na API (`ReqDTO` e `ResDTO`) e os modelos de dados internos (`Entity`), evitando a exposição direta do banco de dados.
* **Inversão de Controle & Injeção de Dependências:** Gerenciamento de componentes delegados ao Spring IoC.


### 🔄 Regras de Negócio & Fluxos Implementados

* **Máquina de Estados de Pedidos:** Implementação lógica e consistente para gerenciar e validar a transição dos fluxos de estados de um pedido (`StatusPedido`).
* **Gerenciamento de Entregadores:** Fluxo completo para cadastro, atualização e integração do perfil do entregador (`StatusEntregador`).
* **Motor de Pagamentos:** Entidade, controllers, repositórios e regras de validação financeira integrados ao ecossistema da aplicação.


### 🛠️ DevOps, Integração Contínua & Boas Práticas de Repositório

* **CI/CD com GitHub Actions:** Automação configurada via workflow (`ci_cd.yml`) para integração contínua do projeto.
* **Organização de Histórico com Git:** Aplicação de conceitos avançados de Git para reestruturação do histórico de commits separados estritamente por funcionalidade (`feat`, `chore`, `test`).
* **Otimização de Escopo de Repositório:** Configuração rigorosa do `.gitignore` e remoção de dados voláteis (`logs/`, `Registro/`) para manter o repositório remoto limpo e seguro.
