package com.mensageria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mensageria.dto.MensagemRequest;
import com.mensageria.dto.MensagemResponse;
import com.mensageria.exception.GlobalExceptionHandler;
import com.mensageria.exception.MensagemNotFoundException;
import com.mensageria.service.MensagemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MensagemControllerTest {

    @Mock
    private MensagemService service;

    @InjectMocks
    private MensagemController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MensagemResponse response;
    private MensagemRequest request;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        request = new MensagemRequest();
        request.setTitulo("Título Teste");
        request.setConteudo("Conteúdo de teste");
        request.setRemetente("victor@teste.com");

        response = MensagemResponse.builder()
                .id(1L)
                .titulo("Título Teste")
                .conteudo("Conteúdo de teste")
                .remetente("victor@teste.com")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("POST /api/mensagens")
    class CriarMensagem {

        @Test
        @DisplayName("deve retornar 201 com a mensagem criada")
        void deveCriarMensagem() throws Exception {
            when(service.criar(any(MensagemRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.titulo").value("Título Teste"))
                    .andExpect(jsonPath("$.remetente").value("victor@teste.com"));

            verify(service).criar(any(MensagemRequest.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando campos obrigatórios estão ausentes")
        void deveRetornar400QuandoCamposAusentes() throws Exception {
            MensagemRequest invalido = new MensagemRequest();

            mockMvc.perform(post("/api/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.campos").exists());

            verify(service, never()).criar(any());
        }
    }

    @Nested
    @DisplayName("GET /api/mensagens/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 com mensagem quando id existe")
        void deveRetornarMensagemPorId() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(response);

            mockMvc.perform(get("/api/mensagens/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.titulo").value("Título Teste"));
        }

        @Test
        @DisplayName("deve retornar 404 quando id não existe")
        void deveRetornar404QuandoIdNaoExiste() throws Exception {
            when(service.buscarPorId(99L)).thenThrow(new MensagemNotFoundException(99L));

            mockMvc.perform(get("/api/mensagens/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensagem").value(containsString("99")));
        }
    }

    @Nested
    @DisplayName("GET /api/mensagens")
    class ListarTodas {

        @Test
        @DisplayName("deve retornar 200 com lista de mensagens")
        void deveListarTodasAsMensagens() throws Exception {
            MensagemResponse outra = MensagemResponse.builder()
                    .id(2L).titulo("Outra").conteudo("X").remetente("b@b.com")
                    .criadoEm(LocalDateTime.now()).atualizadoEm(LocalDateTime.now()).build();
            when(service.listarTodas()).thenReturn(List.of(response, outra));

            mockMvc.perform(get("/api/mensagens"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[1].id").value(2L));
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há mensagens")
        void deveRetornarListaVazia() throws Exception {
            when(service.listarTodas()).thenReturn(List.of());

            mockMvc.perform(get("/api/mensagens"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/mensagens/remetente/{remetente}")
    class BuscarPorRemetente {

        @Test
        @DisplayName("deve retornar mensagens filtradas pelo remetente")
        void deveRetornarMensagensPorRemetente() throws Exception {
            when(service.buscarPorRemetente("victor@teste.com")).thenReturn(List.of(response));

            mockMvc.perform(get("/api/mensagens/remetente/victor@teste.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].remetente").value("victor@teste.com"));
        }
    }

    @Nested
    @DisplayName("PUT /api/mensagens/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 200 com mensagem atualizada")
        void deveAtualizarMensagem() throws Exception {
            MensagemResponse atualizada = MensagemResponse.builder()
                    .id(1L).titulo("Atualizado").conteudo("Novo conteúdo").remetente("novo@teste.com")
                    .criadoEm(LocalDateTime.now()).atualizadoEm(LocalDateTime.now()).build();
            when(service.atualizar(eq(1L), any(MensagemRequest.class))).thenReturn(atualizada);

            mockMvc.perform(put("/api/mensagens/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value("Atualizado"));
        }

        @Test
        @DisplayName("deve retornar 404 ao atualizar id inexistente")
        void deveRetornar404AoAtualizarIdInexistente() throws Exception {
            when(service.atualizar(eq(99L), any())).thenThrow(new MensagemNotFoundException(99L));

            mockMvc.perform(put("/api/mensagens/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/mensagens/{id}")
    class Deletar {

        @Test
        @DisplayName("deve retornar 204 ao deletar mensagem existente")
        void deveDeletarMensagem() throws Exception {
            doNothing().when(service).deletar(1L);

            mockMvc.perform(delete("/api/mensagens/1"))
                    .andExpect(status().isNoContent());

            verify(service).deletar(1L);
        }

        @Test
        @DisplayName("deve retornar 404 ao deletar id inexistente")
        void deveRetornar404AoDeletarIdInexistente() throws Exception {
            doThrow(new MensagemNotFoundException(99L)).when(service).deletar(99L);

            mockMvc.perform(delete("/api/mensagens/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
