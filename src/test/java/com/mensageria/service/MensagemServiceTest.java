package com.mensageria.service;

import com.mensageria.dto.MensagemRequest;
import com.mensageria.dto.MensagemResponse;
import com.mensageria.exception.MensagemNotFoundException;
import com.mensageria.model.Mensagem;
import com.mensageria.repository.MensagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemServiceTest {

    @Mock
    private MensagemRepository repository;

    @InjectMocks
    private MensagemService service;

    private Mensagem mensagemSalva;
    private MensagemRequest request;

    @BeforeEach
    void setUp() {
        request = new MensagemRequest();
        request.setTitulo("Título Teste");
        request.setConteudo("Conteúdo de teste");
        request.setRemetente("victor@teste.com");

        mensagemSalva = Mensagem.builder()
                .id(1L)
                .titulo("Título Teste")
                .conteudo("Conteúdo de teste")
                .remetente("victor@teste.com")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criar mensagem e retornar response com id gerado")
        void deveCriarMensagem() {
            when(repository.save(any(Mensagem.class))).thenReturn(mensagemSalva);

            MensagemResponse response = service.criar(request);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitulo()).isEqualTo("Título Teste");
            assertThat(response.getConteudo()).isEqualTo("Conteúdo de teste");
            assertThat(response.getRemetente()).isEqualTo("victor@teste.com");
            verify(repository, times(1)).save(any(Mensagem.class));
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar mensagem quando id existe")
        void deveRetornarMensagemQuandoIdExiste() {
            when(repository.findById(1L)).thenReturn(Optional.of(mensagemSalva));

            MensagemResponse response = service.buscarPorId(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitulo()).isEqualTo("Título Teste");
            verify(repository).findById(1L);
        }

        @Test
        @DisplayName("deve lançar MensagemNotFoundException quando id não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(99L))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("listarTodas()")
    class ListarTodas {

        @Test
        @DisplayName("deve retornar lista com todas as mensagens")
        void deveRetornarTodasAsMensagens() {
            Mensagem outra = Mensagem.builder()
                    .id(2L).titulo("Outra").conteudo("Conteúdo").remetente("a@b.com")
                    .criadoEm(LocalDateTime.now()).atualizadoEm(LocalDateTime.now()).build();
            when(repository.findAll()).thenReturn(List.of(mensagemSalva, outra));

            List<MensagemResponse> lista = service.listarTodas();

            assertThat(lista).hasSize(2);
            assertThat(lista).extracting(MensagemResponse::getId).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há mensagens")
        void deveRetornarListaVazia() {
            when(repository.findAll()).thenReturn(List.of());

            List<MensagemResponse> lista = service.listarTodas();

            assertThat(lista).isEmpty();
        }
    }

    @Nested
    @DisplayName("buscarPorRemetente()")
    class BuscarPorRemetente {

        @Test
        @DisplayName("deve retornar mensagens filtradas por remetente")
        void deveRetornarMensagensPorRemetente() {
            when(repository.findByRemetente("victor@teste.com")).thenReturn(List.of(mensagemSalva));

            List<MensagemResponse> lista = service.buscarPorRemetente("victor@teste.com");

            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).getRemetente()).isEqualTo("victor@teste.com");
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar mensagem existente")
        void deveAtualizarMensagem() {
            MensagemRequest novoRequest = new MensagemRequest();
            novoRequest.setTitulo("Novo Título");
            novoRequest.setConteudo("Novo Conteúdo");
            novoRequest.setRemetente("novo@teste.com");

            Mensagem atualizada = Mensagem.builder()
                    .id(1L).titulo("Novo Título").conteudo("Novo Conteúdo")
                    .remetente("novo@teste.com").criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now()).build();

            when(repository.findById(1L)).thenReturn(Optional.of(mensagemSalva));
            when(repository.save(any(Mensagem.class))).thenReturn(atualizada);

            MensagemResponse response = service.atualizar(1L, novoRequest);

            assertThat(response.getTitulo()).isEqualTo("Novo Título");
            assertThat(response.getConteudo()).isEqualTo("Novo Conteúdo");
            verify(repository).save(any(Mensagem.class));
        }

        @Test
        @DisplayName("deve lançar exceção ao atualizar id inexistente")
        void deveLancarExcecaoAoAtualizarIdInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.atualizar(99L, request))
                    .isInstanceOf(MensagemNotFoundException.class);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deletar()")
    class Deletar {

        @Test
        @DisplayName("deve deletar mensagem existente")
        void deveDeletarMensagem() {
            when(repository.existsById(1L)).thenReturn(true);

            service.deletar(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("deve lançar exceção ao deletar id inexistente")
        void deveLancarExcecaoAoDeletarIdInexistente() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.deletar(99L))
                    .isInstanceOf(MensagemNotFoundException.class);
            verify(repository, never()).deleteById(any());
        }
    }
}
