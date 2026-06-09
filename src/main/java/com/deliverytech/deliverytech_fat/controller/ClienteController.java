package com.deliverytech.deliverytech_fat.controller;

import com.deliverytech.deliverytech_fat.service.ClienteService;
import com.deliverytech.deliverytech_fat.dto.req.ClienteReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.ClienteResDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResDTO> cadastrarCliente(@Valid @RequestBody ClienteReqDTO dto) {
        ClienteResDTO cliente = clienteService.cadastrarCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResDTO> buscarPorId(@PathVariable Long id) {
        ClienteResDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResDTO>> listarClientesAtivos() {
        List<ClienteResDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResDTO> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteReqDTO dto) {
        ClienteResDTO cliente = clienteService.atualizarCliente(id, dto);
        return ResponseEntity.ok(cliente);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClienteResDTO> ativarDesativarCliente(@PathVariable Long id) {
        ClienteResDTO cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResDTO> buscarPorEmail(@PathVariable String email) {
        ClienteResDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

    

    @CacheEvict(value = "clientes",
                    allEntries = true)
    @GetMapping("/limpar-cache")
    public ResponseEntity<Void> limparCache() {
        return ResponseEntity.ok().build();
    }
}
