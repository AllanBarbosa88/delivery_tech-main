package com.deliverytech.deliverytech_fat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.deliverytech_fat.dto.req.EntregadorReqDTO;
import com.deliverytech.deliverytech_fat.dto.res.EntregadorResDTO;
import com.deliverytech.deliverytech_fat.enums.StatusEntregador;
import com.deliverytech.deliverytech_fat.service.EntregadorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/entregadores")
@CrossOrigin(origins = "*")
public class EntregadorController {

    @Autowired
    private EntregadorService entregadorService;

    @PostMapping
    public ResponseEntity<EntregadorResDTO> cadastrar(@Valid @RequestBody EntregadorReqDTO dto) {
        return ResponseEntity.status(201).body(entregadorService.cadastrar(dto));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<EntregadorResDTO>> listarDisponiveis() {
        return ResponseEntity.ok(entregadorService.listarDisponiveis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregadorResDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entregadorService.buscarPorId(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EntregadorResDTO> alterarStatus(
            @PathVariable Long id, 
            @RequestParam StatusEntregador status) {
        return ResponseEntity.ok(entregadorService.alterarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        entregadorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
