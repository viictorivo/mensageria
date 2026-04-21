package com.mensageria.exception;

public class MensagemNotFoundException extends RuntimeException {
    public MensagemNotFoundException(Long id) {
        super("Mensagem não encontrada com id: " + id);
    }
}
