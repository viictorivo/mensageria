package com.mensageria.controller;

import com.mensageria.dto.MensagemRequest;
import com.mensageria.dto.MensagemResponse;
import com.mensageria.service.MensagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService service;

    @PostMapping
    public ResponseEntity<MensagemResponse> criar(@Valid @RequestBody MensagemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MensagemResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<MensagemResponse>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/remetente/{remetente}")
    public ResponseEntity<List<MensagemResponse>> buscarPorRemetente(@PathVariable String remetente) {
        return ResponseEntity.ok(service.buscarPorRemetente(remetente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MensagemRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
