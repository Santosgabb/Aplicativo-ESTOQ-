package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.model.Venda;
import com.app.service.VendaService;

/*
 * @RestController
 * Controller REST que retorna JSON
 */
@RestController

/*
 * Caminho base da API
 * http://localhost:8080/vendas
 */
@RequestMapping("/vendas")

/*
 * Permite acesso do Android / navegador / outro app
 */
@CrossOrigin(origins = "*")
public class VendaController {

    /*
     * Injeta o service automaticamente
     */
    @Autowired
    private VendaService vendaService;

    /*
     * POST
     * Salvar venda
     * Envia JSON com:
     * usuario
     * itens
     * subtotal
     * desconto
     * acrescimo
     * total
     * formaPagamento
     */
    @PostMapping
    public Venda salvar(@RequestBody Venda venda) {
        return vendaService.salvar(venda);
    }

    /*
     * GET
     * Listar todas as vendas
     */
    @GetMapping
    public List<Venda> listar() {
        return vendaService.listar();
    }

    /*
     * GET por id
     */
    @GetMapping("/{id}")
    public Venda buscar(@PathVariable Long id) {
        return vendaService.buscar(id);
    }

    /*
     * DELETE venda
     */
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        vendaService.deletar(id);
    }

}