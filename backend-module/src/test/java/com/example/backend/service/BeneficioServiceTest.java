package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.backend.BeneficioMapper;
import com.example.backend.dto.request.BeneficioRequest;
import com.example.backend.dto.response.BeneficioResponse;
import com.example.backend.exception.RecursoNaoEncontradoException;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

	@Mock
	private BeneficioRepository repository;

	@Mock
	private BeneficioMapper mapper;

	@InjectMocks
	private BeneficioService service;

	@Test
	void inserir_deveInserirUmBeneficioE_RetornarBeneficioResponse() {
		// Given
		BeneficioRequest request = new BeneficioRequest("Beneficio A", "Descrição A", new BigDecimal("500.00"));

		Beneficio beneficioSemId = Beneficio.builder().nome("Beneficio A").descricao("Descrição A")
				.valor(new BigDecimal("500.00")).build();

		Beneficio beneficioSalvo = Beneficio.builder().id(1L).nome("Beneficio A").descricao("Descrição A")
				.valor(new BigDecimal("500.00")).ativo(true).build();

		BeneficioResponse response = new BeneficioResponse(1L, "Beneficio A", "Descrição A", new BigDecimal("500.00"),
				true);

		when(mapper.toEntity(request)).thenReturn(beneficioSemId);
		when(repository.save(beneficioSemId)).thenReturn(beneficioSalvo);
		when(mapper.toResponse(beneficioSalvo)).thenReturn(response);

		// When
		BeneficioResponse result = service.inserir(request);

		// Then
		assertEquals(response, result);
		verify(mapper).toEntity(request);
		verify(repository).save(beneficioSemId);
		verify(mapper).toResponse(beneficioSalvo);
	}

	@Test
	void buscaTodosBeneficiosAtivos_deveRetornarUmPage_deBenefioResponse() {
		// Given
		Beneficio b1 = Beneficio.builder().id(1L).nome("Beneficio A").descricao("Descrição A")
				.valor(new BigDecimal("500.00")).ativo(true).build();

		Beneficio b2 = Beneficio.builder().id(2L).nome("Beneficio B").descricao("Descrição B")
				.valor(new BigDecimal("1000.00")).ativo(true).build();

		List<Beneficio> beneficios = List.of(b1, b2);

		Pageable pageable = PageRequest.of(0, 5);

		Page<Beneficio> pagina = new PageImpl<>(beneficios, pageable, beneficios.size());

		when(repository.findByAtivoTrue(pageable)).thenReturn(pagina);

		BeneficioResponse resp1 = new BeneficioResponse(1L, "Beneficio A", "Descrição A", new BigDecimal("500.00"),
				true);
		BeneficioResponse resp2 = new BeneficioResponse(2L, "Beneficio B", "Descrição B", new BigDecimal("1000.00"),
				true);

		when(mapper.toResponse(b1)).thenReturn(resp1);
		when(mapper.toResponse(b2)).thenReturn(resp2);

		// When
		Page<BeneficioResponse> result = service.buscaTodosBeneficiosAtivos(pageable);

		// Then
		assertEquals(2, result.getContent().size());
		verify(repository).findByAtivoTrue(pageable);
		verify(mapper, times(2)).toResponse(any());
	}

	@Test
	void buscaBeneficioAtivoPorId_deveBuscarUmBeneficioE_retornarUmBeneficioResponse() {
		// Given
		Long id = 1L;

		Beneficio beneficio = Beneficio.builder().id(1L).nome("Beneficio A").descricao("Descrição A")
				.valor(new BigDecimal("500.00")).ativo(true).build();

		BeneficioResponse response = new BeneficioResponse(1L, "Beneficio A", "Descrição A", new BigDecimal("500.00"),
				true);

		when(repository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(beneficio));
		when(mapper.toResponse(beneficio)).thenReturn(response);

		// When
		BeneficioResponse result = service.buscaBeneficioAtivoPorId(id);

		// Then
		assertEquals(response, result);
		verify(repository).findByIdAndAtivoTrue(id);
		verify(mapper).toResponse(beneficio);
	}

	@Test
	void buscaBeneficioAtivoPorId_deveBuscarUmBeneficioE_lancarRecursoNaoEncontradoException() {
		// Given
		Long id = 999L;

		when(repository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(RecursoNaoEncontradoException.class, () -> service.buscaBeneficioAtivoPorId(id));
		verify(repository).findByIdAndAtivoTrue(id);
		verify(mapper, never()).toResponse(any());
	}

	@Test
	void atualizar_deveAtualizarUmBeneficioE_RetornarUmBeneficioResponse() {
		// Given
		Long beneficioId = 1L;

		Beneficio beneficioExistente = Beneficio.builder().id(1L).nome("Beneficio A").valor(new BigDecimal("500.00"))
				.descricao("Descrição A").ativo(true).build();

		BeneficioRequest request = new BeneficioRequest("Beneficio A", "Descrição A", new BigDecimal("2500.00"));

		BeneficioResponse response = new BeneficioResponse(1L, "Beneficio A", "Descrição A", new BigDecimal("2500.00"),
				true);

		when(repository.findByIdAndAtivoTrue(beneficioId)).thenReturn(Optional.of(beneficioExistente));
		when(repository.save(any(Beneficio.class))).thenReturn(beneficioExistente);
		when(mapper.toResponse(beneficioExistente)).thenReturn(response);

		// When
		BeneficioResponse result = service.atualizar(beneficioId, request);

		// Then
		assertEquals(response, result);
		verify(repository).findByIdAndAtivoTrue(beneficioId);
		verify(mapper).toResponse(beneficioExistente);
	}

	@Test
	void atualizar_deveBuscarUmBeneficioParaAtualizarE_lancarRecursoNaoEncontradoException() {
		// Given
		Long beneficioId = 999L;

		BeneficioRequest request = new BeneficioRequest("Beneficio A", "Descrição A", new BigDecimal("2500.00"));

		when(repository.findByIdAndAtivoTrue(beneficioId)).thenReturn(Optional.empty());

		String msg = "Beneficiário de Id: " + beneficioId + " não cadastrado ou desativado.";

		// When
		RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
				() -> service.atualizar(beneficioId, request));

		//Then
		assertEquals(msg, ex.getMessage());
		verify(repository).findByIdAndAtivoTrue(beneficioId);
		verify(repository, never()).save(any());
		verify(mapper, never()).toResponse(any());
	}
	
	@Test
	void desativar_deveBuscarUmBeneficioE_desativar() {
		// Given
		Long beneficioId = 1L;
		
		Beneficio beneficioExistente = Beneficio.builder().id(1L).nome("Beneficio A").valor(new BigDecimal("500.00"))
				.descricao("Descrição A").ativo(true).build();

		when(repository.findByIdAndAtivoTrue(beneficioId)).thenReturn(Optional.of(beneficioExistente));
		when(repository.save(any(Beneficio.class))).thenReturn(beneficioExistente);

		// When
		service.desativar(beneficioId);
		
		// Then
		assertFalse(beneficioExistente.getAtivo());
		verify(repository).findByIdAndAtivoTrue(beneficioId);
		verify(repository).save(beneficioExistente);
	}

	@Test
	void desativar_deveBuscarUmBeneficioParaDesativarE_lancarRecursoNaoEncontradoException() {
		// Given
		Long beneficioId = 999L;

		when(repository.findByIdAndAtivoTrue(beneficioId)).thenReturn(Optional.empty());

		String msg = "Beneficiário de Id: " + beneficioId + " não cadastrado ou desativado.";

		// When
		RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
				() -> service.desativar(beneficioId));

		// Then
		assertEquals(msg, ex.getMessage());
		verify(repository, never()).save(any());
	}

}
