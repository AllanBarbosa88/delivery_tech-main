package com.deliverytech.deliverytech_fat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliverytech.deliverytech_fat.entity.Entregador;
import com.deliverytech.deliverytech_fat.enums.StatusEntregador;

@Repository
public interface EntregadorRepository extends JpaRepository<Entregador, Long> {
    Optional<Entregador> findByEmail(String email);
    List<List<Entregador>> findByStatusAndAtivoTrue(StatusEntregador status);
    boolean existsByEmail(String email);
}
