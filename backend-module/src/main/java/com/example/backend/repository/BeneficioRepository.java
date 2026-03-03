package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.Beneficio;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
	Page<Beneficio> findByAtivoTrue(Pageable pageable);
	
	Optional<Beneficio> findByIdAndAtivoTrue(Long id);
}
