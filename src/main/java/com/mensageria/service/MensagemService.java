package com.mensageria.service;

import com.mensageria.dto.MensagemRequest;
import com.mensageria.dto.MensagemResponse;
import com.mensageria.exception.MensagemNotFoundException;
import com.mensageria.model.Mensagem;
import com.mensageria.repository.MensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MensagemService {

    private final MensagemRepository repository;

    @Transactional
    public MensagemResponse criar(MensagemRequest request) {
        Mensagem mensagem = Mensagem.builder()
                .titulo(request.getTitulo())
                .conteudo(request.getConteudo())
                .remetente(request.getRemetente())
                .build();
        return MensagemResponse.from(repository.save(mensagem));
    }

    @Transactional(readOnly = true)
    public MensagemResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(MensagemResponse::from)
                .orElseThrow(() -> new MensagemNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<MensagemResponse> listarTodas() {
        return repository.findAll()
                .stream()
                .map(MensagemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MensagemResponse> buscarPorRemetente(String remetente) {
        return repository.findByRemetente(remetente)
                .stream()
                .map(MensagemResponse::from)
                .toList();
    }

    @Transactional
    public MensagemResponse atualizar(Long id, MensagemRequest request) {
        Mensagem mensagem = repository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException(id));

        mensagem.setTitulo(request.getTitulo());
        mensagem.setConteudo(request.getConteudo());
        mensagem.setRemetente(request.getRemetente());

        return MensagemResponse.from(repository.save(mensagem));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new MensagemNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
