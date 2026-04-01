package com.app.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity //diz que essa classe pertence ao banco
@Table(name = "itens_venda") // nome tabela no banco de dados
public class ItemVenda {

    @Id //chave primaria e gerada cm auto Incremento
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //quantidade de vendas
    private Integer quantidade;

    /*Preço unitario do produto do momento da venda
     * precision = total de numeros
     * scale = casas decimais
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    //subtotal = quantidade * preco
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    //muitos itens  -> um produto
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id")
    private Produto produto;
    
    /*Muitos itens -> uma venda
     * JsonBackReference evita loop infinito no json
     */
    @ManyToOne
    @JoinColumn(name = "venda_id")
    @JsonBackReference
    private Venda venda;

    //construtor
    public ItemVenda() {
    }

    
    //getters e setters
    public Long getId() {
        return id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Produto getProduto() {
        return produto;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }
}