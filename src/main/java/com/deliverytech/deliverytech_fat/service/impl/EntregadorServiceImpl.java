package com.deliverytech.deliverytech_fat.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.deliverytech_fat.dto.req.EntregadorReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.EntregadorResDTO;
import com.deliverytech.deliverytech_fat.entity.Entregador;
import com.deliverytech.deliverytech_fat.enums.StatusEntregador;
import com.deliverytech.deliverytech_fat.exception.EntityNotFoundException;
import com.deliverytech.deliverytech_fat.repository.EntregadorRepository;
import com.deliverytech.deliverytech_fat.service.EntregadorService;

@Service
@Transactional
public class EntregadorServiceImpl implements EntregadorService {

    @Autowired
    private EntregadorRepository entregadorRepository;

    @Override
    public EntregadorResDTO cadastrar(EntregadorReqDTO dto) {
        if (entregadorRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email já cadastrado para outro entregador.");
        }

        Entregador entregador = new Entregador();
        entregador.setNome(dto.nome());
        entregador.setEmail(dto.email());
        entregador.setTelefone(dto.telefone());
        entregador.setPlacaVeiculo(dto.placaVeiculo());

        Entregador salvo = entregadorRepository.save(entregador);
        return mapearParaDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntregadorResDTO> listarDisponiveis() {
        // Correção de estrutura do repository convertendo a lista interna
        return entregadorRepository.findAll().stream()
            .filter(e -> e.getStatus() == StatusEntregador.DISPONIVEL && e.getAtivo())
            .map(this::mapearParaDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EntregadorResDTO buscarPorId(Long id) {
        Entregador entregador = entregadorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Entregador não encontrado com ID: " + id));
        return mapearParaDTO(entregador);
    }

    @Override
    public EntregadorResDTO alterarStatus(Long id, StatusEntregador status) {
        Entregador entregador = entregadorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Entregador não encontrado com ID: " + id));
        entregador.setStatus(status);
        return mapearParaDTO(entregadorRepository.save(entregador));
    }

    @Override
    public void deletar(Long id) {
        Entregador entregador = entregadorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Entregador não encontrado com ID: " + id));
        entregador.setAtivo(false); // Soft delete para histórico
        entregadorRepository.save(entregador);
    }

    private EntregadorResDTO mapearParaDTO(Entregador entregador) {
        return new EntregadorResDTO(
            entregador.getId(),
            entregador.getNome(),
            entregador.getEmail(),
            entregador.getTelefone(),
            entregador.getPlacaVeiculo(),
            entregador.getStatus(),
            entregador.getAtivo(),
            entregador.getDataCriacao()
        );
    }
}
