package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.model.ItemVenda;
import com.app.service.ItemVendaService;

/*
 * @RestController
 * Controller REST que retorna JSON
 */
@RestController

/*
 * Caminho base da API
 * http://localhost:8080/itens
 */
@RequestMapping("/itens")

/*
 * Permite acesso do Android
 */
@CrossOrigin(origins = "*")
public class ItemVendaController {

    /*
     * Injeta o service automaticamente
     */
    @Autowired
    private ItemVendaService itemVendaService;

    /*
     * POST
     * Cadastrar item de venda
     */
    @PostMapping
    public ItemVenda salvar(@RequestBody ItemVenda item) {
        return itemVendaService.salvar(item);
    }

    /*
     * GET
     * Listar itens
     */
    @GetMapping
    public List<ItemVenda> listar() {
        return itemVendaService.listar();
    }

    /*
     * GET por id
     */
    @GetMapping("/{id}")
    public ItemVenda buscar(@PathVariable Long id) {
        return itemVendaService.buscar(id);
    }

    /*
     * DELETE
     */
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        itemVendaService.deletar(id);
    }

    /*
     * PUT
     * Atualizar item
     */
    @PutMapping("/{id}")
    public ItemVenda atualizar(
            @PathVariable Long id,
            @RequestBody ItemVenda item
    ) {
        item.setId(id);
        return itemVendaService.salvar(item);
    }
}