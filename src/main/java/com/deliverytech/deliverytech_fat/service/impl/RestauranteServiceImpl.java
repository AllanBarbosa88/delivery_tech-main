package com.deliverytech.deliverytech_fat.service.impl;

import com.deliverytech.deliverytech_fat.dto.req.RestauranteReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.RestauranteResDTO;
import com.deliverytech.deliverytech_fat.entity.Restaurante;
import com.deliverytech.deliverytech_fat.exception.BusinessException;
import com.deliverytech.deliverytech_fat.exception.EntityNotFoundException;
import com.deliverytech.deliverytech_fat.repository.RestauranteRepository;
import com.deliverytech.deliverytech_fat.service.RestauranteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestauranteServiceImpl implements RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RestauranteResDTO cadastrarRestaurante(RestauranteReqDTO dto) {
        if (restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new BusinessException("Restaurante já cadastrado: " + dto.getNome());
        }

        Restaurante restaurante = modelMapper.map(dto, Restaurante.class);
        restaurante.setAtivo(true);

        return modelMapper.map(restauranteRepository.save(restaurante), RestauranteResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public RestauranteResDTO buscarPorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        return modelMapper.map(restaurante, RestauranteResDTO.class);
    }

    @Override
    public RestauranteResDTO atualizarRestaurante(Long id, RestauranteReqDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        if (!restaurante.getNome().equals(dto.getNome()) &&
            restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new BusinessException("Nome já cadastrado: " + dto.getNome());
        }

        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());

        return modelMapper.map(restauranteRepository.save(restaurante), RestauranteResDTO.class);
    }

    @Override
    public RestauranteResDTO alterarStatusRestaurante(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        restaurante.setAtivo(!restaurante.isAtivo());

        return modelMapper.map(restauranteRepository.save(restaurante), RestauranteResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestauranteResDTO> listarRestaurantes(String categoria, Boolean ativo, Pageable pageable) {
        Page<Restaurante> page;

        if (categoria != null && ativo != null) {
            page = restauranteRepository.findByCategoriaAndAtivo(categoria, ativo, pageable);
        } else if (categoria != null) {
            page = restauranteRepository.findByCategoria(categoria, pageable);
        } else if (ativo != null) {
            page = restauranteRepository.findByAtivo(ativo, pageable);
        } else {
            page = restauranteRepository.findAll(pageable);
        }

        return page.map(r -> modelMapper.map(r, RestauranteResDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestauranteResDTO> buscarRestaurantesPorCategoria(String categoria) {
        return restauranteRepository.findByCategoriaAndAtivoTrue(categoria)
            .stream()
            .map(r -> modelMapper.map(r, RestauranteResDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long id, String cep) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        // Simulação — em produção integraria com API de CEP
        return restaurante.getTaxaEntrega();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestauranteResDTO> buscarRestaurantesProximos(String cep, Integer raio) {
        // Simulação — em produção integraria com API de geolocalização
        return restauranteRepository.findByAtivoTrue()
            .stream()
            .map(r -> modelMapper.map(r, RestauranteResDTO.class))
            .collect(Collectors.toList());
    }
}
