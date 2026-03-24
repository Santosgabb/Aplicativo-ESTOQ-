package com.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto;

    @Column(precision = 10, scale = 2)
    private BigDecimal acrescimo;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalFinal;

    @Column(precision = 10, scale = 2)
    private BigDecimal pagamentoDinheiro;

    @Column(precision = 10, scale = 2)
    private BigDecimal pagamentoPix;

    @Column(precision = 10, scale = 2)
    private BigDecimal pagamentoDebito;

    @Column(precision = 10, scale = 2)
    private BigDecimal pagamentoCredito;

    private Boolean descontoAutorizadoPorGerente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ItemVenda> itens = new ArrayList<>();
 
    public Venda() {
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public BigDecimal getAcrescimo() {
        return acrescimo;
    }

    public BigDecimal getTotalFinal() {
        return totalFinal;
    }

    public BigDecimal getPagamentoDinheiro() {
        return pagamentoDinheiro;
    }

    public BigDecimal getPagamentoPix() {
        return pagamentoPix;
    }

    public BigDecimal getPagamentoDebito() {
        return pagamentoDebito;
    }

    public BigDecimal getPagamentoCredito() {
        return pagamentoCredito;
    }

    public Boolean getDescontoAutorizadoPorGerente() {
        return descontoAutorizadoPorGerente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public void setAcrescimo(BigDecimal acrescimo) {
        this.acrescimo = acrescimo;
    }

    public void setTotalFinal(BigDecimal totalFinal) {
        this.totalFinal = totalFinal;
    }

    public void setPagamentoDinheiro(BigDecimal pagamentoDinheiro) {
        this.pagamentoDinheiro = pagamentoDinheiro;
    }

    public void setPagamentoPix(BigDecimal pagamentoPix) {
        this.pagamentoPix = pagamentoPix;
    }

    public void setPagamentoDebito(BigDecimal pagamentoDebito) {
        this.pagamentoDebito = pagamentoDebito;
    }

    public void setPagamentoCredito(BigDecimal pagamentoCredito) {
        this.pagamentoCredito = pagamentoCredito;
    }

    public void setDescontoAutorizadoPorGerente(Boolean descontoAutorizadoPorGerente) {
        this.descontoAutorizadoPorGerente = descontoAutorizadoPorGerente;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }
}