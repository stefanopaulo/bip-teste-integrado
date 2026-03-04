package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.example.backend.BeneficioMapper;
import com.example.backend.dto.request.BeneficioRequest;
import com.example.backend.dto.response.BeneficioResponse;
import com.example.backend.exception.ConcorrenciaException;
import com.example.backend.exception.RecursoNaoEncontradoException;
import com.example.backend.exception.SaldoInsuficienteException;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock private BeneficioRepository repository;
    @Mock private BeneficioMapper mapper;
    @InjectMocks private BeneficioService service;

    private static final Long ID_VAL_1 = 1L;
    private static final Long ID_VAL_2 = 2L;
    private static final Long ID_INEXISTENTE = 999L;
    private static final BigDecimal CEM = new BigDecimal("100.00");
    private static final BigDecimal QUINHENTOS = new BigDecimal("500.00");

    // --- TESTES DE CRUD ---

    @Test
    void inserir_devePersistirERetornarResponse() {
        var request = novoRequest();
        var entidadeSemId = novaEntidade().id(null).build();
        var entidadeSalva = novaEntidade().id(ID_VAL_1).build();
        var response = novaResponse();

        when(mapper.toEntity(request)).thenReturn(entidadeSemId);
        when(repository.save(entidadeSemId)).thenReturn(entidadeSalva);
        when(mapper.toResponse(entidadeSalva)).thenReturn(response);

        var result = service.inserir(request);

        assertEquals(response, result);
        verify(repository).save(entidadeSemId);
    }

    @Test
    void buscaBeneficioAtivoPorId_deveRetornarResponse_QuandoExiste() {
        var beneficio = novaEntidade().id(ID_VAL_1).build();
        var response = novaResponse();

        mockRepositorioBusca(ID_VAL_1, beneficio);
        when(mapper.toResponse(beneficio)).thenReturn(response);

        var result = service.buscaBeneficioAtivoPorId(ID_VAL_1);

        assertEquals(response, result);
    }

    @Test
    void atualizar_deveAlterarDadosERetornarResponse() {
        var existente = novaEntidade().valor(QUINHENTOS).build();
        var request = new BeneficioRequest("Novo Nome", "Nova Desc", CEM);
        
        mockRepositorioBusca(ID_VAL_1, existente);
        when(repository.save(any())).thenReturn(existente);
        when(mapper.toResponse(any())).thenReturn(novaResponse());

        service.atualizar(ID_VAL_1, request);

        verify(repository).save(argThat(b -> b.getValor().equals(CEM)));
    }

    // --- TESTES DE TRANSFERÊNCIA ---

    @Test
    void transfer_deveMovimentarSaldosComSucesso() {
        var origem = novaEntidade().id(ID_VAL_1).valor(QUINHENTOS).build();
        var destino = novaEntidade().id(ID_VAL_2).valor(BigDecimal.ZERO).build();

        mockRepositorioBusca(ID_VAL_1, origem);
        mockRepositorioBusca(ID_VAL_2, destino);

        service.transfer(ID_VAL_1, ID_VAL_2, CEM);

        assertAll(
            () -> assertEquals(new BigDecimal("400.00"), origem.getValor()),
            () -> assertEquals(CEM, destino.getValor()),
            () -> verify(repository).saveAll(anyList())
        );
    }

    @Test
    void transfer_deveLancarSaldoInsuficiente_QuandoSaldoForBaixo() {
        var origem = novaEntidade().nome("Origem").valor(BigDecimal.TEN).build();
        mockRepositorioBusca(ID_VAL_1, origem);
        mockRepositorioBusca(ID_VAL_2, novaEntidade().build());

        var ex = assertThrows(SaldoInsuficienteException.class, 
            () -> service.transfer(ID_VAL_1, ID_VAL_2, CEM));

        assertTrue(ex.getMessage().contains("saldo suficiente"));
        verify(repository, never()).saveAll(any());
    }

    // --- TESTES DE EXCEÇÃO ---

    @Test
    void buscar_deveLancarRecursoNaoEncontrado_QuandoInativoOuInexistente() {
        when(repository.findByIdAndAtivoTrue(ID_INEXISTENTE)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, 
            () -> service.buscaBeneficioAtivoPorId(ID_INEXISTENTE));
    }

    @Test
    void transfer_deveLancarConcorrenciaException_EmCasoDeErroOptimisticLock() {
        mockRepositorioBusca(ID_VAL_1, novaEntidade().build());
        mockRepositorioBusca(ID_VAL_2, novaEntidade().build());
        when(repository.saveAll(anyList())).thenThrow(ObjectOptimisticLockingFailureException.class);

        assertThrows(ConcorrenciaException.class, () -> service.transfer(ID_VAL_1, ID_VAL_2, CEM));
    }

    // --- MÉTODOS AUXILIARES ---

    private Beneficio.BeneficioBuilder novaEntidade() {
        return Beneficio.builder()
                .id(ID_VAL_1).nome("Beneficio Teste").descricao("Desc")
                .valor(QUINHENTOS).ativo(true);
    }

    private BeneficioRequest novoRequest() {
        return new BeneficioRequest("Beneficio Teste", "Desc", QUINHENTOS);
    }

    private BeneficioResponse novaResponse() {
        return new BeneficioResponse(ID_VAL_1, "Beneficio Teste", "Desc", QUINHENTOS, true);
    }

    private void mockRepositorioBusca(Long id, Beneficio retorno) {
        when(repository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(retorno));
    }
}
