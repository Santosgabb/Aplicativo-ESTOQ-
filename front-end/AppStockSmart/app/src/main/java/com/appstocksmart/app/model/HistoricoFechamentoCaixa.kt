package com.appstocksmart.app.model

import com.google.gson.annotations.SerializedName

/*
 * Modelo usado para listar o histórico de fechamentos.
 */
data class HistoricoFechamentoCaixa(
    val id: Long? = null,
    val operadorId: Long? = null,
    val operadorNome: String? = null,
    val gerenteNome: String? = null,

    @SerializedName("dataReferencia")
    val dataReferencia: String? = null,

    val quantidadeVendas: Int = 0,
    val valorInicialCaixa: Double = 0.0,
    val totalDinheiro: Double = 0.0,
    val totalPix: Double = 0.0,
    val totalDebito: Double = 0.0,
    val totalCredito: Double = 0.0,
    val totalSaidas: Double = 0.0,

    @SerializedName("dinheiroFinalCaixa")
    val dinheiroFinalEmCaixa: Double = 0.0,

    val observacao: String? = null,
    val dataHoraFechamento: String? = null
)