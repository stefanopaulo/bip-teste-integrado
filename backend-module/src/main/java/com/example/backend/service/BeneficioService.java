package com.example.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.example.backend.dto.request.BeneficioRequest;
import com.example.backend.dto.request.BeneficioUpdateRequest;
import com.example.backend.dto.response.BeneficioResponse;
import com.example.backend.exception.ConcorrenciaException;
import com.example.backend.exception.OperacaoIlegalException;
import com.example.backend.exception.RecursoNaoEncontradoException;
import com.example.backend.exception.SaldoInsuficienteException;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;

import jakarta.transaction.Transactional;

@Service
public class BeneficioService {

	private final BeneficioRepository beneficioRepository;
	private final BeneficioMapper beneficioMapper;

	public BeneficioService(BeneficioRepository beneficioRepository, BeneficioMapper beneficioMapper) {
		this.beneficioRepository = beneficioRepository;
		this.beneficioMapper = beneficioMapper;

	}

	@Transactional
	public BeneficioResponse inserir(BeneficioRequest request) {
		Beneficio beneficio = beneficioMapper.toEntity(request);
		return beneficioMapper.toResponse(beneficioRepository.save(beneficio));
	}
	
	public Page<BeneficioResponse> buscaTodosBeneficiosAtivos(Pageable pageable) {
		return beneficioRepository.findByAtivoTrue(pageable).map(beneficioMapper::toResponse);
	}

	public BeneficioResponse buscaBeneficioAtivoPorId(Long id) {
		return beneficioRepository.findByIdAndAtivoTrue(id).map(beneficioMapper::toResponse)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						"Beneficiário de Id: " + id + " não cadastrado ou desativado."));
	}

	@Transactional
	public BeneficioResponse atualizar(Long id, BeneficioUpdateRequest request) {
		Beneficio beneficio = buscarBeneficioAtivo(id);

		beneficioMapper.updateRequest(beneficio, request);
			
		return beneficioMapper.toResponse(beneficioRepository.save(beneficio));
	}

	@Transactional
	public void desativar(Long id) {
		Beneficio beneficio = buscarBeneficioAtivo(id);

		beneficio.setAtivo(false);

		beneficioRepository.save(beneficio);
	}

	@Retryable(retryFor = {
			ObjectOptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 500))
	@Transactional
	public void transfer(Long fromId, Long toId, BigDecimal amount) {
		validarDadosTransferencia(fromId, toId, amount);

		Beneficio from = buscarBeneficioAtivo(fromId);
		Beneficio to = buscarBeneficioAtivo(toId);

		if (from.getValor().compareTo(amount) < 0) {
			throw new SaldoInsuficienteException(
					"Beneficiário " + from.getNome() + " não tem saldo suficiente para realizar essa operação.");
		}

		from.setValor(from.getValor().subtract(amount));
		to.setValor(to.getValor().add(amount));

		try {
			beneficioRepository.saveAllAndFlush(List.of(from, to));
		} catch (ObjectOptimisticLockingFailureException e) {
			throw new ConcorrenciaException("Erro de concorrência. Tente novamente.");
		}
	}

	private void validarDadosTransferencia(Long fromId, Long toId, BigDecimal amount) {
		if (fromId == null || toId == null) {
			throw new OperacaoIlegalException("Ids dos beneficios não podem ser nulos");
		}

		if (fromId.equals(toId)) {
			throw new OperacaoIlegalException("Não é possível transferir para o mesmo beneficiário");
		}

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new OperacaoIlegalException("Valor da transferência deve ser maior que zero");
		}
	}

	private Beneficio buscarBeneficioAtivo(Long id) {
		return beneficioRepository.findByIdAndAtivoTrue(id).orElseThrow(() -> new RecursoNaoEncontradoException(
				"Beneficiário de Id: " + id + " não cadastrado ou desativado."));
	}
}
