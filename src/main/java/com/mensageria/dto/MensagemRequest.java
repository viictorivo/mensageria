package com.mensageria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MensagemRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String titulo;

    @NotBlank(message = "Conteúdo é obrigatório")
    private String conteudo;

    @NotBlank(message = "Remetente é obrigatório")
    @Size(max = 255, message = "Remetente deve ter no máximo 255 caracteres")
    private String remetente;
}
