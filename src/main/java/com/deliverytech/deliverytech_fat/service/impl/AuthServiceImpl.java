package com.deliverytech.deliverytech_fat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.deliverytech_fat.dto.req.RegisterReqDTO;
import com.deliverytech.deliverytech_fat.entity.Cliente;
import com.deliverytech.deliverytech_fat.entity.Entregador;
import com.deliverytech.deliverytech_fat.entity.Restaurante;
import com.deliverytech.deliverytech_fat.entity.Usuario;
import com.deliverytech.deliverytech_fat.enums.Role;
import com.deliverytech.deliverytech_fat.enums.StatusEntregador;
import com.deliverytech.deliverytech_fat.repository.ClienteRepository;
import com.deliverytech.deliverytech_fat.repository.EntregadorRepository;
import com.deliverytech.deliverytech_fat.repository.RestauranteRepository;
import com.deliverytech.deliverytech_fat.repository.UsuarioRepository;
import com.deliverytech.deliverytech_fat.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntregadorRepository entregadorRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailAndAtivo(email, true)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Usuario criarUsuario(RegisterReqDTO request) {
        
        // 👤 FLUXO DE CADASTRO DO CLIENTE
        if (Role.CLIENTE.equals(request.getRole())) {
            Cliente cliente = new Cliente();
            cliente.setNome(request.getNome());
            cliente.setEmail(request.getEmail());
            cliente.setAtivo(true);

            Cliente savedCliente = clienteRepository.save(cliente);

            Usuario usuario = new Usuario();
            usuario.setNome(savedCliente.getNome());
            usuario.setEmail(savedCliente.getEmail());
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
            usuario.setRole(request.getRole());
            usuario.setAtivo(savedCliente.isAtivo());

            return usuarioRepository.save(usuario);
        }

        // 🛵 FLUXO DE CADASTRO DO ENTREGADOR
                // 🛵 FLUXO DE CADASTRO DO ENTREGADOR
        if (Role.ENTREGADOR.equals(request.getRole())) {
            Usuario usuario = new Usuario();
            usuario.setNome(request.getNome());
            usuario.setEmail(request.getEmail());
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
            usuario.setRole(request.getRole());
            usuario.setAtivo(true);
            
            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            Entregador entregador = new Entregador();
            entregador.setNome(usuarioSalvo.getNome());
            entregador.setEmail(usuarioSalvo.getEmail()); // 🔑 Preenche o Email obrigatório
            
            // 🔑 Preenche o Telefone e Placa obrigatórios (Dados fictícios para testes)
            entregador.setTelefone("(11) 99999-0000");
            entregador.setPlacaVeiculo("AAA-0000");
            
            // Os campos 'status' e 'ativo' já possuem valores padrão na Entity, 
            // mas setamos aqui para garantir o preenchimento no banco:
            entregador.setStatus(StatusEntregador.DISPONIVEL);
            entregador.setAtivo(true);
            
            entregadorRepository.save(entregador);

            return usuarioSalvo;
        }


        // 🍕 FLUXO DE CADASTRO DO RESTAURANTE
        if (Role.RESTAURANTE.equals(request.getRole())) {
            Restaurante restaurante = new Restaurante();
            restaurante.setNome(request.getNome());
            restaurante.setEmail(request.getEmail());
            restaurante.setAtivo(true);
            // status de funcionamento padrão definido diretamente na entidade se necessário
            restaurante.setAvaliacao(5);
            
            Restaurante restauranteSalvo = restauranteRepository.save(restaurante);

            Usuario usuario = new Usuario();
            usuario.setNome(restauranteSalvo.getNome());
            usuario.setEmail(restauranteSalvo.getEmail());
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
            usuario.setRole(request.getRole());
            usuario.setRestauranteId(restauranteSalvo.getId());
            usuario.setAtivo(true);

            return usuarioRepository.save(usuario);
        }

        // 🛡️ Fluxo padrão original restando apenas para ADMIN
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(request.getRole());
        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("ID não pode ser nulo");
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
} // 👈 Essa chave fecha a classe corretamente
