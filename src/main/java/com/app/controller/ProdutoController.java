package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.model.Produto;
import com.app.service.ProdutoService;

/*Diz que esta classe é um controller REST
Retorna JSON automaticamente*/

@RestController

// Caminho base da API http://localhost:8080/usuario

@RequestMapping("/produtos")
public class ProdutoController {

	// Injeta o service automaticamente
	@Autowired
	private ProdutoService produtoService;

	// POST Cadastrar produto http://localhost:8080/produtos
	@PostMapping
	public Produto salvar(@RequestBody Produto produto) {
		return produtoService.salvar(produto);
	}

	// GET Listar todos os produtos
	@GetMapping
	public List<Produto> listar() {
		return produtoService.listar();
	}

	// GET por id http://localhost:8080/produtos/1
	@GetMapping("/{id}")
	public Produto buscar(@PathVariable Long id) {
		return produtoService.buscar(id);
	}

	// DELETE Deletar produto
	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		produtoService.deletar(id);
	}

	// PUT Atualizar produto

	@PutMapping("/{id}")
	public Produto atualizar(@PathVariable Long id, @RequestBody Produto produto) {
		produto.setId(id);
		return produtoService.salvar(produto);
	}
}