package com.deliverytech.deliverytech_fat.service;

import com.deliverytech.deliverytech_fat.dto.req.ClienteReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ClienteResDTO;
import java.util.List;

public interface ClienteService {

    ClienteResDTO  cadastrarCliente(ClienteReqDTO dto);

    ClienteResDTO buscarClientePorId(Long id);

    ClienteResDTO buscarClientePorEmail(String email);

    ClienteResDTO atualizarCliente(Long id, ClienteReqDTO dto);

    ClienteResDTO ativarDesativarCliente(Long id);

    List<ClienteResDTO> listarClientesAtivos();
}
