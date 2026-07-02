package com.deliverytech.deliverytech_fat.service;

import java.util.List;

import com.deliverytech.deliverytech_fat.dto.req.EntregadorReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.EntregadorResDTO;
import com.deliverytech.deliverytech_fat.enums.StatusEntregador;

public interface EntregadorService {
    EntregadorResDTO cadastrar(EntregadorReqDTO dto);
    List<EntregadorResDTO> listarDisponiveis();
    EntregadorResDTO buscarPorId(Long id);
    EntregadorResDTO alterarStatus(Long id, StatusEntregador status);
    void deletar(Long id);
}
