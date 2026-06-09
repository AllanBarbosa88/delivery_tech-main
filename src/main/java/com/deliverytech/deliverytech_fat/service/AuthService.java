package com.deliverytech.deliverytech_fat.service;

import com.deliverytech.deliverytech_fat.dto.req.RegisterReqDTO;
import com.deliverytech.deliverytech_fat.entity.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    boolean existsByEmail(String email);

    Usuario criarUsuario(RegisterReqDTO request);

    Usuario buscarPorId(Long id);

    Usuario buscarPorEmail(String email);
}
