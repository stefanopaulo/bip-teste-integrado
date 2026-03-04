package com.example.backend.integracao;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.backend.exception.OperacaoIlegalException;
import com.example.backend.exception.SaldoInsuficienteException;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.service.BeneficioService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class BeneficioServiceIT {

    @Autowired
    private BeneficioService beneficioService;

    @Autowired
    private BeneficioRepository beneficioRepository;

    private Beneficio from;
    private Beneficio to;

    @BeforeEach
    void setup() {
        from = new Beneficio();
        from.setNome("João");
        from.setValor(new BigDecimal("100.00"));
        from.setAtivo(true);

        to = new Beneficio();
        to.setNome("Maria");
        to.setValor(new BigDecimal("50.00"));
        to.setAtivo(true);

        beneficioRepository.saveAll(List.of(from, to));
    }

    @Test
    void transfer_deveTransferirComSucesso() {
        beneficioService.transfer(from.getId(), to.getId(), new BigDecimal("30.00"));

        Beneficio fromAtualizado = beneficioRepository.findById(from.getId()).get();
        Beneficio toAtualizado = beneficioRepository.findById(to.getId()).get();

        assertEquals(new BigDecimal("70.00"), fromAtualizado.getValor());
        assertEquals(new BigDecimal("80.00"), toAtualizado.getValor());
    }
    
    @Test
    void transfer_naoDeveTransferirSemSaldo() {
        assertThrows(SaldoInsuficienteException.class, () -> {
            beneficioService.transfer(from.getId(), to.getId(), new BigDecimal("1000.00"));
        });
    }
    
    @Test
    void transfer_naoDeveTransferirParaMesmaConta() {
        assertThrows(OperacaoIlegalException.class, () -> {
            beneficioService.transfer(from.getId(), from.getId(), new BigDecimal("10.00"));
        });
    }
}