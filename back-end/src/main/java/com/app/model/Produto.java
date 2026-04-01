package com.app.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Preço de custo é obrigatorio")
    private Double precoCusto;

    @NotNull(message = "Preço de venda é obrigatorio")
    private Double precoVenda;

    @NotNull(message = "Quantidade do estoque é obrigatorio")
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo é obrigatório")
    private TipoProduto tipo;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    @NotNull(message = "Fornecedor é obrigatório")
    @NotFound(action = NotFoundAction.IGNORE)
    private Fornecedor fornecedor;

    public Produto() {
    }

    public Produto(String nome, Double precoCusto, Double precoVenda, Integer quantidade, TipoProduto tipo, Fornecedor fornecedor) {
        this.nome = nome;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.fornecedor = fornecedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(double precoCusto) {
        this.precoCusto = precoCusto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setEstoque(int quantidade) {
        this.quantidade = quantidade;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProduto tipo) {
        this.tipo = tipo;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }
}