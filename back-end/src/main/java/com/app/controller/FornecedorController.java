package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.model.Fornecedor;
import com.app.service.FornecedorService;

/*
 * @RestController
 * Controller REST que retorna JSON
 */
@RestController

/*
 * Caminho base da API
 * http://localhost:8080/fornecedores
 */
@RequestMapping("/fornecedores")

/*
 * Permite acesso do Android
 */
@CrossOrigin(origins = "*")
public class FornecedorController {

    /*
     * Injeta o service automaticamente
     */
    @Autowired
    private FornecedorService fornecedorService;

    /*
     * POST
     * Cadastrar fornecedor
     */
    @PostMapping
    public Fornecedor salvar(@RequestBody Fornecedor fornecedor) {
        return fornecedorService.salvar(fornecedor);
    }

    /*
     * GET
     * Listar fornecedores
     */
    @GetMapping
    public List<Fornecedor> listar() {
        return fornecedorService.listar();
    }

    /*
     * GET por id
     */
    @GetMapping("/{id}")
    public Fornecedor buscar(@PathVariable Long id) {
        return fornecedorService.buscar(id);
    }

    /*
     * DELETE
     */
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        fornecedorService.deletar(id);
    }

    /*
     * PUT
     * Atualizar fornecedor
     */
    @PutMapping("/{id}")
    public Fornecedor atualizar(
            @PathVariable Long id,
            @RequestBody Fornecedor fornecedor
    ) {
        fornecedor.setId(id);
        return fornecedorService.salvar(fornecedor);
    }

}