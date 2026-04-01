package com.appstocksmart.app.model

class EntradaEstoque (
    //atributos
    val id: Int,
    val produto: Produto,
    val fornecedor: Fornecedor,
    val quantidade: Int,
    val dataHora: String
    )