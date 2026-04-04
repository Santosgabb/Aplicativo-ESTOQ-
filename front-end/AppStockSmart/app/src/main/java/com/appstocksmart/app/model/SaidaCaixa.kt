package com.appstocksmart.app.model

/*
 * Representa uma saída de dinheiro do caixa.
 * Exemplo:
 * descricao = "gelo"
 * valor = 20.00
 */
data class SaidaCaixa(
    val descricao: String,
    val valor: Double
)