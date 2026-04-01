package com.app.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.model.Produto;

//JpaRepository<Classe entiti,tipo do campo que esta no id Long > 
public interface ProdutoRepository extends JpaRepository<Produto, Long > {
	
	//Busca por nome 
	List<Produto> findByNomeContainingIgnoreCase(String nome);

}
