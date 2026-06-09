package com.deliverytech.deliverytech_fat.service.impl;

import com.deliverytech.deliverytech_fat.dto.req.ClienteReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ClienteResDTO;
import com.deliverytech.deliverytech_fat.entity.Cliente;
import com.deliverytech.deliverytech_fat.exception.BusinessException;
import com.deliverytech.deliverytech_fat.exception.EntityNotFoundException;
import com.deliverytech.deliverytech_fat.repository.ClienteRepository;
import com.deliverytech.deliverytech_fat.service.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ClienteResDTO cadastrarCliente(ClienteReqDTO dto) {
        // Validar email único
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        // Converter DTO para entidade
        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setAtivo(true);

        // Salvar cliente
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // Retornar DTO de resposta
        return modelMapper.map(clienteSalvo, ClienteResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResDTO  buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        return modelMapper.map(cliente, ClienteResDTO .class);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResDTO  buscarClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com email: " + email));

        return modelMapper.map(cliente, ClienteResDTO .class);
    }

    @Override
    public ClienteResDTO  atualizarCliente(Long id, ClienteReqDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        // Validar email único (se mudou)
        if (!cliente.getEmail().equals(dto.getEmail()) &&
            clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        // Atualizar dados
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());

        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return modelMapper.map(clienteAtualizado, ClienteResDTO .class);
    }

    @Override
    public ClienteResDTO  ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        cliente.setAtivo(!cliente.isAtivo());
        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return modelMapper.map(clienteAtualizado, ClienteResDTO .class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResDTO > listarClientesAtivos() {
        List<Cliente> clientesAtivos = clienteRepository.findByAtivoTrue();

        return clientesAtivos.stream()
            .map(cliente -> modelMapper.map(cliente, ClienteResDTO .class))
            .collect(Collectors.toList());
    }
}
