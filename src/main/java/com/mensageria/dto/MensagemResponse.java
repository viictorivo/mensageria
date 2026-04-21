package com.mensageria.dto;

import com.mensageria.model.Mensagem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MensagemResponse {

    private Long id;
    private String titulo;
    private String conteudo;
    private String remetente;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static MensagemResponse from(Mensagem m) {
        return MensagemResponse.builder()
                .id(m.getId())
                .titulo(m.getTitulo())
                .conteudo(m.getConteudo())
                .remetente(m.getRemetente())
                .criadoEm(m.getCriadoEm())
                .atualizadoEm(m.getAtualizadoEm())
                .build();
    }
}
