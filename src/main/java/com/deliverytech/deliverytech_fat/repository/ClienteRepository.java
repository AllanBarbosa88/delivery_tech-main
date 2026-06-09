package com.deliverytech.deliverytech_fat.repository;

import com.deliverytech.deliverytech_fat.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    //Buscar o cliente por email
    Optional<Cliente> findByEmail(String email);

    // verificar se o cliente existe por email
    boolean existsByEmail(String email);

    // Buscar clientes ativos
    List<Cliente> findByAtivoTrue();

    // buscar nome
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    //buscar telefoneç
    List<Cliente> findByTelefone(String telefone);

    //buscar endereco
    List<Cliente> findByEnderecoContainingIgnoreCase(String endereco);

    // query custom o- clientes com pedidos ativos
    @Query("SELECT c FROM Cliente c JOIN c.pedidos p WHERE p.status = 'ATIVO'")
    List<Cliente> findClientesComPedidosAtivos();

    // clientes por cidade
    @Query("SELECT c FROM Cliente c WHERE c.endereco LIKE %:cidade%")
    List<Cliente> findClientesPorCidade(@Param("cidade") String cidade);   

    // contar clientes
    @Query("SELECT COUNT(c) FROM Cliente c")
    long countClientes();

}
