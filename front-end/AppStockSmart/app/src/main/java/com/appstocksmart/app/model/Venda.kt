package com.appstocksmart.app.model

data class Venda(

    val id: Long? = null,

    val usuario: Usuario,

    val itens: List<ItemVenda>,

    val subtotal: Double,
    val desconto: Double,
    val acrescimo: Double,
    val totalFinal: Double,

    val pagamentoDinheiro: Double,
    val pagamentoPix: Double,
    val pagamentoDebito: Double,
    val pagamentoCredito: Double,

    val descontoAutorizadoPorGerente: Boolean,

    val dataHora: String? = null

)