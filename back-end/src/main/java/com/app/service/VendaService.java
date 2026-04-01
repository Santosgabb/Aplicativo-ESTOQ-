package com.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.model.ItemVenda;
import com.app.model.Produto;
import com.app.model.Usuario;
import com.app.model.Venda;
import com.app.repository.ProdutoRepository;
import com.app.repository.UsuarioRepository;
import com.app.repository.VendaRepository;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public Venda salvar(Venda venda) {

        BigDecimal subtotalGeral = BigDecimal.ZERO;

        if (venda.getDesconto() == null) {
            venda.setDesconto(BigDecimal.ZERO);
        }

        if (venda.getAcrescimo() == null) {
            venda.setAcrescimo(BigDecimal.ZERO);
        }

        if (venda.getPagamentoDinheiro() == null) {
            venda.setPagamentoDinheiro(BigDecimal.ZERO);
        }

        if (venda.getPagamentoPix() == null) {
            venda.setPagamentoPix(BigDecimal.ZERO);
        }

        if (venda.getPagamentoDebito() == null) {
            venda.setPagamentoDebito(BigDecimal.ZERO);
        }

        if (venda.getPagamentoCredito() == null) {
            venda.setPagamentoCredito(BigDecimal.ZERO);
        }

        if (venda.getDescontoAutorizadoPorGerente() == null) {
            venda.setDescontoAutorizadoPorGerente(false);
        }

        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new RuntimeException("Venda sem itens");
        }

        if (venda.getUsuario() == null || venda.getUsuario().getId() == null) {
            throw new RuntimeException("Usuário inválido");
        }

        Usuario usuarioBanco = usuarioRepository
                .findById(venda.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        venda.setUsuario(usuarioBanco);

        for (ItemVenda item : venda.getItens()) {

            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new RuntimeException("Produto inválido");
            }

            Produto produtoBanco = produtoRepository
                    .findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produtoBanco.getQuantidade() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente: " + produtoBanco.getNome());
            }

            item.setVenda(venda);
            item.setProduto(produtoBanco);
            item.setPrecoUnitario(BigDecimal.valueOf(produtoBanco.getPrecoVenda()));

            BigDecimal subtotalItem = item.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(item.getQuantidade()));

            item.setSubtotal(subtotalItem);
            subtotalGeral = subtotalGeral.add(subtotalItem);

            produtoBanco.setEstoque(produtoBanco.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produtoBanco);
        }

        venda.setDataHora(LocalDateTime.now());
        venda.setSubtotal(subtotalGeral);

        BigDecimal totalFinal = subtotalGeral
                .subtract(venda.getDesconto())
                .add(venda.getAcrescimo());

        venda.setTotalFinal(totalFinal);

        BigDecimal totalPago = venda.getPagamentoDinheiro()
                .add(venda.getPagamentoPix())
                .add(venda.getPagamentoDebito())
                .add(venda.getPagamentoCredito());

        if (totalPago.compareTo(totalFinal) < 0) {
            throw new RuntimeException("Pagamento insuficiente");
        }

        return vendaRepository.save(venda);
    }

    public List<Venda> listar() {
        return vendaRepository.findAll();
    }

    public Venda buscar(Long id) {
        return vendaRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        vendaRepository.deleteById(id);
    }
}