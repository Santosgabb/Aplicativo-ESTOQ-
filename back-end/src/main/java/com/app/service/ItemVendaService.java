package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.model.ItemVenda;
import com.app.repository.ItemVendaRepository;

/*
 * @Service
 * Classe de regra de negócio do ItemVenda
 * Fica entre Controller e Repository
 */
@Service
public class ItemVendaService {

    /*
     * Injeta o repository automaticamente
     */
    @Autowired
    private ItemVendaRepository itemVendaRepository;

    /*
     * Salvar ou atualizar item de venda
     */
    public ItemVenda salvar(ItemVenda itemVenda) {
        return itemVendaRepository.save(itemVenda);
    }

    /*
     * Listar todos os itens de venda
     */
    public List<ItemVenda> listar() {
        return itemVendaRepository.findAll();
    }

    /*
     * Buscar item de venda por id
     */
    public ItemVenda buscar(Long id) {
        return itemVendaRepository
                .findById(id)
                .orElse(null);
    }

    /*
     * Deletar item de venda
     */
    public void deletar(Long id) {
        itemVendaRepository.deleteById(id);
    }
}