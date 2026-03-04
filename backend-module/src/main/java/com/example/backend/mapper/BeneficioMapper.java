package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.request.BeneficioRequest;
import com.example.backend.dto.request.BeneficioUpdateRequest;
import com.example.backend.dto.response.BeneficioResponse;
import com.example.backend.model.Beneficio;

@Component
public class BeneficioMapper {

	public Beneficio toEntity(BeneficioRequest request) {
		Beneficio beneficio = new Beneficio();
		beneficio.setNome(request.nome());
		
		if (request.descricao() != null && !request.descricao().isEmpty()) {
			beneficio.setDescricao(request.descricao());
		}
		
		beneficio.setValor(request.valor());
		return beneficio;
	}

	public BeneficioResponse toResponse(Beneficio beneficio) {
		return new BeneficioResponse(beneficio.getId(), beneficio.getNome(), beneficio.getDescricao(),
				beneficio.getValor(), beneficio.getAtivo());
	}
	
	public void updateRequest(Beneficio beneficio, BeneficioUpdateRequest request) {
		beneficio.setNome(request.nome());

		if (request.descricao() != null)
			beneficio.setDescricao(request.descricao());
	}

}
