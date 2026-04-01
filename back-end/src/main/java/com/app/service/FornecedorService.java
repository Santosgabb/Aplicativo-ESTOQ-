package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.model.Fornecedor;
import com.app.repository.FornecedorRepository;

@Service
public class FornecedorService {

	//injeta o repository automaticamente
	@Autowired
	private FornecedorRepository fornecedorRepository;

	//save or update
	public Fornecedor salvar(Fornecedor fornecedor) {
	    return fornecedorRepository.save(fornecedor);
	}

	//list the fornececedor
	public List<Fornecedor> listar() {
		return fornecedorRepository.findAll();
	}

	//busca
	public Fornecedor buscar(Long id) {
		return fornecedorRepository.findById(id).orElse(null);
	}

	//deleta
	public void deletar(Long id) {
		fornecedorRepository.deleteById(id);
	}
}
