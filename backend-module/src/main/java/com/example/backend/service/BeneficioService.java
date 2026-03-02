package com.example.backend.service;

import java.math.BigDecimal;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.example.backend.exception.OperacaoIlegalException;
import com.example.backend.exception.RecursoNaoEncontradoException;
import com.example.backend.exception.SaldoInsuficienteException;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;

import jakarta.transaction.Transactional;

@Service
public class BeneficioService {

	private final BeneficioRepository beneficioRepository;

	public BeneficioService(BeneficioRepository beneficioRepository) {
		this.beneficioRepository = beneficioRepository;

	}

	// BUG: sem validações, sem locking, pode gerar saldo negativo e lost update
	// TODO: Versão inicial da correção. Ajustes ainda se fazem necessário
	@Transactional
	public void transfer(Long fromId, Long toId, BigDecimal amount) {
		Beneficio from = beneficioRepository.findById(fromId).orElseThrow(
				() -> new RecursoNaoEncontradoException("Beneficiário de Id: " + fromId + " não encontrado."));

		Beneficio to = beneficioRepository.findById(toId).orElseThrow(
				() -> new RecursoNaoEncontradoException("Beneficiário de Id: " + toId + " não encontrado."));

		if (from.getValor().compareTo(amount) < 0) {
			throw new SaldoInsuficienteException(
					"Beneficiário " + from.getNome() + " não tem saldo suficiente para realizar essa operação.");
		}

		from.setValor(from.getValor().subtract(amount));
		to.setValor(to.getValor().add(amount));

		beneficioRepository.save(from);
		beneficioRepository.save(to);
		beneficioRepository.flush();
	}
}
