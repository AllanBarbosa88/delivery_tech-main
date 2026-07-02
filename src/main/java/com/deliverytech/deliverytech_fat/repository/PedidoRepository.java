package com.deliverytech.deliverytech_fat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deliverytech.deliverytech_fat.entity.Cliente;
import com.deliverytech.deliverytech_fat.entity.Pedido;
import com.deliverytech.deliverytech_fat.enums.StatusPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos por cliente
    List<Pedido> findByClienteOrderByDataCriacaoDesc(Cliente cliente);

    // Buscar pedidos por cliente ID
    List<Pedido> findByClienteIdOrderByDataCriacaoDesc(Long clienteId);

    // Buscar por status
    List<Pedido> findByStatusOrderByDataCriacaoDesc(StatusPedido status);

    // Buscar por número do pedido
    Pedido findByNumeroPedido(String numeroPedido);

    // Buscar pedidos por período genérico
    List<Pedido> findByDataCriacaoBetweenOrderByDataCriacaoDesc(LocalDateTime inicio, LocalDateTime fim);

    // Buscar pedidos por restaurante
    @Query("SELECT p FROM Pedido p WHERE p.restaurante.id = :restauranteId ORDER BY p.dataCriacao DESC")
    List<Pedido> findByRestauranteId(@Param("restauranteId") Long restauranteId);

    // Relatório - pedidos por status
    @Query("SELECT p.status, COUNT(p) FROM Pedido p GROUP BY p.status")
    List<Object[]> countPedidosByStatus();

    // Pedidos pendentes (para dashboard)
    @Query("SELECT p FROM Pedido p WHERE p.status IN ('PENDENTE', 'CONFIRMADO', 'PREPARANDO') " +
           "ORDER BY p.dataCriacao ASC")
    List<Pedido> findPedidosPendentes();

    // CORREÇÃO: Buscar pedidos de um dia específico usando os parâmetros informados
    @Query("SELECT p FROM Pedido p WHERE p.dataCriacao >= :inicioDia AND p.dataCriacao <= :fimDia ORDER BY p.dataCriacao DESC")
    List<Pedido> findPedidosDoDia(
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("fimDia") LocalDateTime fimDia);
}