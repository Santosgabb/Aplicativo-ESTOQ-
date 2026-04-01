package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.model.Produto;
import com.app.repository.ProdutoRepository;

///Regra de negocio
@Service
public class ProdutoService {

	@Autowired // injeta a dependencia
	private ProdutoRepository produtoRepository;

	// salva pu atualiza
	public Produto salvar(Produto produto) {
		return produtoRepository.save(produto);
	}

	// lista produtos
	public List<Produto> listar() {
		return produtoRepository.findAll();
	}

	// busca por id
	public Produto buscar(Long id) {
		return produtoRepository.findById(id).orElse(null);
	}

	// deleta
	public void deletar(Long id) {
		produtoRepository.deleteById(id);
	}

}
/*find all lista
 * findById busca
 * deleteById deleta
 */
