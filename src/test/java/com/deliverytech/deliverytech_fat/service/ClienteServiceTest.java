package com.deliverytech.deliverytech_fat.service;

import com.deliverytech.deliverytech_fat.dto.req.ClienteReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ClienteResDTO;
import com.deliverytech.deliverytech_fat.entity.Cliente;
import com.deliverytech.deliverytech_fat.exception.BusinessException;
import com.deliverytech.deliverytech_fat.repository.ClienteRepository;
import com.deliverytech.deliverytech_fat.service.impl.ClienteServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    void deveCadastrarClienteComSucesso() {
        // Given
        ClienteReqDTO dto = new ClienteReqDTO();
        dto.setNome("João Silva");
        dto.setEmail("joao@email.com");
        dto.setTelefone("11999999999");
        dto.setEndereco("Rua A, 123");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setAtivo(true);

        ClienteResDTO responseDTO = new ClienteResDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João Silva");
        responseDTO.setEmail("joao@email.com");
        responseDTO.setAtivo(true);

        when(clienteRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(modelMapper.map(dto, Cliente.class)).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResDTO.class)).thenReturn(responseDTO);

        // When
        ClienteResDTO resultado = clienteService.cadastrarCliente(dto);

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@email.com", resultado.getEmail());
        assertTrue(resultado.isAtivo());
        verify(clienteRepository).existsByEmail(dto.getEmail());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void deveRejeitarClienteComEmailDuplicado() {
        // Given
        ClienteReqDTO dto = new ClienteReqDTO();
        dto.setEmail("joao@email.com");

        when(clienteRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> clienteService.cadastrarCliente(dto)
        );

        assertEquals("Email já cadastrado: joao@email.com", exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
}
