package com.deliverytech.deliverytech_fat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deliverytech.deliverytech_fat.dto.res.RestauranteResDTO;
import com.deliverytech.deliverytech_fat.entity.Restaurante;
import com.deliverytech.deliverytech_fat.exception.EntityNotFoundException;
import com.deliverytech.deliverytech_fat.repository.RestauranteRepository;
import com.deliverytech.deliverytech_fat.service.impl.RestauranteServiceImpl;

@ExtendWith(MockitoExtension.class) // Inicializa o Mockito neste arquivo de teste
class RestauranteServiceImplTest {

    @Mock
    private RestauranteRepository restauranteRepository; // Repositório de brinquedo

    @Mock
    private org.modelmapper.ModelMapper modelMapper; // ModelMapper de brinquedo

    @InjectMocks
    private RestauranteServiceImpl restauranteService; // Injeta os brinquedos no serviço

    @Test
    @DisplayName("Deve retornar um restaurante quando o ID informado existir no banco")
    void deveBuscarRestaurantePorIdComSucesso() {
        // ARRANGE (Configuração dos dados fictícios e comportamentos)
        Long restauranteId = 1L;
        Restaurante pizzariaFake = new Restaurante();
        pizzariaFake.setId(restauranteId);
        pizzariaFake.setNome("Pizzaria Bella");
        pizzariaFake.setAtivo(true);

        RestauranteResDTO dtoFake = new RestauranteResDTO();
        dtoFake.setId(restauranteId);
        dtoFake.setNome("Pizzaria Bella");

        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(pizzariaFake));
        when(modelMapper.map(any(), eq(RestauranteResDTO.class))).thenReturn(dtoFake);

        // ACT (Execução da lógica real do serviço)
        RestauranteResDTO resultado = restauranteService.buscarPorId(restauranteId);

        // ASSERT (Validações finais de sucesso)
        assertNotNull(resultado, "O resultado não deveria ser nulo!");
        assertEquals("Pizzaria Bella", resultado.getNome());
        
        verify(restauranteRepository, times(1)).findById(restauranteId);
        verify(modelMapper, times(1)).map(any(), eq(RestauranteResDTO.class));
    }

    @Test
    @DisplayName("Deve estourar Exception quando o restaurante não for encontrado")
    void deveLancarExceptionQuandoRestauranteNaoExistir() {
        // ARRANGE: Configura o repositório fake para simular que o ID 99 não existe no banco H2
        Long idInexistente = 99L;
        when(restauranteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT: Executa e valida que a API lança a exceção de entidade não encontrada
        assertThrows(EntityNotFoundException.class, () -> {
            restauranteService.buscarPorId(idInexistente);
        });

        verify(restauranteRepository, times(1)).findById(idInexistente);
    }
}
