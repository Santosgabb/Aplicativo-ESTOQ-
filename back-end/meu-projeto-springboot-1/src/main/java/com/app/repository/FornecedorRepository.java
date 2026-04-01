package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

	List<Fornecedor> findByNomeContainingIgnoreCase(String nome);
}
