package com.appstocksmart.app.ui.relatorio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appstocksmart.app.adapter.VendaRelatorioAdapter
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityRelatorioBinding
import com.appstocksmart.app.model.Venda
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RelatorioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRelatorioBinding
    private lateinit var adapter: VendaRelatorioAdapter
    private lateinit var apiService: ApiService

    // Guarda todas as vendas vindas da API
    private var listaCompletaVendas: List<Venda> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatorioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o serviço da API
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Configura a lista
        configurarRecyclerView()

        // Configura os filtros
        configurarFiltros()

        // Configura os botões
        configurarAcoes()

        // Carrega os dados do relatório
        carregarRelatorio()
    }

    override fun onResume() {
        super.onResume()

        // Recarrega os dados ao voltar para a tela
        carregarRelatorio()
    }

    /**
     * Configura o RecyclerView da lista de vendas.
     */
    private fun configurarRecyclerView() {
        adapter = VendaRelatorioAdapter(emptyList()) { venda ->

            val idVenda = venda.id

            // Valida se a venda possui ID
            if (idVenda == null) {
                Toast.makeText(this, "Venda sem ID válido", Toast.LENGTH_SHORT).show()
                return@VendaRelatorioAdapter
            }

            // Abre a tela de detalhe da venda
            val intent = Intent(this, DetalheRelatorioActivity::class.java)
            intent.putExtra("idVenda", idVenda)
            startActivity(intent)
        }

        binding.recyclerRelatorioVendas.layoutManager = LinearLayoutManager(this)
        binding.recyclerRelatorioVendas.adapter = adapter
    }

    /**
     * Configura os spinners e o comportamento visual dos filtros.
     */
    private fun configurarFiltros() {
        configurarSpinnerTipoPeriodo()
        configurarSpinnerMes()
        configurarSpinnerTrimestre()
        configurarSpinnerSemestre()

        // Preenche o campo ano com o ano atual
        binding.edtAnoRelatorio.setText(Calendar.getInstance().get(Calendar.YEAR).toString())

        // Quando o tipo de período muda, ajusta os campos visíveis
        binding.spinnerTipoPeriodoRelatorio.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    atualizarCamposVisiveisPorPeriodo()
                    atualizarTextoPeriodoSelecionado()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        atualizarCamposVisiveisPorPeriodo()
        atualizarTextoPeriodoSelecionado()
    }

    /**
     * Configura os botões de aplicar e limpar filtro.
     */
    private fun configurarAcoes() {
        binding.btnAplicarFiltroRelatorio.setOnClickListener {
            aplicarFiltroPeriodo(mostrarMensagemSeVazio = true)
        }

        binding.btnLimparFiltroRelatorio.setOnClickListener {
            limparFiltros()
        }
    }

    /**
     * Spinner principal com os tipos de período.
     */
    private fun configurarSpinnerTipoPeriodo() {
        val opcoes = listOf("Todos", "Mensal", "Trimestral", "Semestral", "Anual")
        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opcoes
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoPeriodoRelatorio.adapter = adapterSpinner
    }

    /**
     * Spinner com os meses do ano.
     */
    private fun configurarSpinnerMes() {
        val meses = listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            meses
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMesRelatorio.adapter = adapterSpinner
    }

    /**
     * Spinner com os 4 trimestres.
     */
    private fun configurarSpinnerTrimestre() {
        val trimestres = listOf(
            "1º Trimestre", "2º Trimestre", "3º Trimestre", "4º Trimestre"
        )

        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            trimestres
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTrimestreRelatorio.adapter = adapterSpinner
    }

    /**
     * Spinner com os 2 semestres.
     */
    private fun configurarSpinnerSemestre() {
        val semestres = listOf(
            "1º Semestre", "2º Semestre"
        )

        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            semestres
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSemestreRelatorio.adapter = adapterSpinner
    }

    /**
     * Mostra ou esconde os campos conforme o tipo de período selecionado.
     */
    private fun atualizarCamposVisiveisPorPeriodo() {
        val tipoPeriodo = binding.spinnerTipoPeriodoRelatorio.selectedItem.toString()

        binding.layoutMesRelatorio.visibility =
            if (tipoPeriodo == "Mensal") View.VISIBLE else View.GONE

        binding.layoutTrimestreRelatorio.visibility =
            if (tipoPeriodo == "Trimestral") View.VISIBLE else View.GONE

        binding.layoutSemestreRelatorio.visibility =
            if (tipoPeriodo == "Semestral") View.VISIBLE else View.GONE

        binding.layoutAnoRelatorio.visibility =
            if (tipoPeriodo == "Todos") View.GONE else View.VISIBLE
    }

    /**
     * Busca todas as vendas da API.
     * Depois aplica o filtro em cima da lista recebida.
     */
    private fun carregarRelatorio() {
        apiService.listarVendas().enqueue(object : Callback<List<Venda>> {

            override fun onResponse(
                call: Call<List<Venda>>,
                response: Response<List<Venda>>
            ) {
                if (response.isSuccessful) {
                    val vendas = response.body() ?: emptyList()

                    // Guarda a lista completa já ordenada
                    listaCompletaVendas = ordenarVendas(vendas)

                    // Aplica o filtro atual
                    aplicarFiltroPeriodo()
                } else {
                    Toast.makeText(
                        this@RelatorioActivity,
                        "Erro ao buscar vendas: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Venda>>, t: Throwable) {
                Toast.makeText(
                    this@RelatorioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * Aplica o filtro selecionado pelo usuário.
     */
    private fun aplicarFiltroPeriodo(mostrarMensagemSeVazio: Boolean = false) {
        val vendasFiltradas = filtrarVendasPorPeriodo(listaCompletaVendas)

        preencherResumo(vendasFiltradas)
        adapter.atualizarLista(vendasFiltradas)
        atualizarTextoPeriodoSelecionado()

        if (mostrarMensagemSeVazio && vendasFiltradas.isEmpty()) {
            Toast.makeText(
                this,
                "Nenhuma venda encontrada para o filtro selecionado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Limpa os filtros e volta para a visão geral.
     */
    private fun limparFiltros() {
        binding.spinnerTipoPeriodoRelatorio.setSelection(0)
        binding.spinnerMesRelatorio.setSelection(0)
        binding.spinnerTrimestreRelatorio.setSelection(0)
        binding.spinnerSemestreRelatorio.setSelection(0)
        binding.edtAnoRelatorio.setText(Calendar.getInstance().get(Calendar.YEAR).toString())

        atualizarCamposVisiveisPorPeriodo()
        aplicarFiltroPeriodo()
    }

    /**
     * Filtra a lista de vendas conforme o período escolhido.
     */
    private fun filtrarVendasPorPeriodo(vendas: List<Venda>): List<Venda> {
        val tipoPeriodo = binding.spinnerTipoPeriodoRelatorio.selectedItem.toString()

        // Se for "Todos", retorna a lista completa
        if (tipoPeriodo == "Todos") {
            return ordenarVendas(vendas)
        }

        val anoFiltro = obterAnoFiltro()

        return ordenarVendas(
            vendas.filter { venda ->
                val calendarioVenda = obterCalendarioDaVenda(venda) ?: return@filter false

                val anoVenda = calendarioVenda.get(Calendar.YEAR)
                val mesVenda = calendarioVenda.get(Calendar.MONTH) + 1

                if (anoVenda != anoFiltro) {
                    return@filter false
                }

                when (tipoPeriodo) {
                    "Mensal" -> {
                        val mesSelecionado = binding.spinnerMesRelatorio.selectedItemPosition + 1
                        mesVenda == mesSelecionado
                    }

                    "Trimestral" -> {
                        val trimestreSelecionado = binding.spinnerTrimestreRelatorio.selectedItemPosition + 1
                        mesVenda in obterMesesDoTrimestre(trimestreSelecionado)
                    }

                    "Semestral" -> {
                        val semestreSelecionado = binding.spinnerSemestreRelatorio.selectedItemPosition + 1
                        mesVenda in obterMesesDoSemestre(semestreSelecionado)
                    }

                    "Anual" -> true

                    else -> true
                }
            }
        )
    }

    /**
     * Atualiza o texto que mostra qual filtro está ativo.
     */
    private fun atualizarTextoPeriodoSelecionado() {
        val tipoPeriodo = binding.spinnerTipoPeriodoRelatorio.selectedItem?.toString() ?: "Todos"
        val ano = obterAnoFiltro()

        val texto = when (tipoPeriodo) {
            "Mensal" -> {
                val nomeMes = binding.spinnerMesRelatorio.selectedItem?.toString() ?: "Mês"
                "Período selecionado: $nomeMes / $ano"
            }

            "Trimestral" -> {
                val nomeTrimestre = binding.spinnerTrimestreRelatorio.selectedItem?.toString() ?: "Trimestre"
                "Período selecionado: $nomeTrimestre / $ano"
            }

            "Semestral" -> {
                val nomeSemestre = binding.spinnerSemestreRelatorio.selectedItem?.toString() ?: "Semestre"
                "Período selecionado: $nomeSemestre / $ano"
            }

            "Anual" -> "Período selecionado: Ano de $ano"

            else -> "Período selecionado: Todas as vendas"
        }

        binding.txtPeriodoSelecionado.text = texto
    }

    /**
     * Retorna o ano digitado no filtro.
     * Se estiver vazio ou inválido, usa o ano atual.
     */
    private fun obterAnoFiltro(): Int {
        val anoDigitado = binding.edtAnoRelatorio.text.toString().trim().toIntOrNull()
        return anoDigitado ?: Calendar.getInstance().get(Calendar.YEAR)
    }

    /**
     * Retorna os meses correspondentes ao trimestre selecionado.
     */
    private fun obterMesesDoTrimestre(trimestre: Int): IntRange {
        return when (trimestre) {
            1 -> 1..3
            2 -> 4..6
            3 -> 7..9
            4 -> 10..12
            else -> 1..12
        }
    }

    /**
     * Retorna os meses correspondentes ao semestre selecionado.
     */
    private fun obterMesesDoSemestre(semestre: Int): IntRange {
        return when (semestre) {
            1 -> 1..6
            2 -> 7..12
            else -> 1..12
        }
    }

    /**
     * Preenche os cards de resumo com base na lista filtrada.
     */
    private fun preencherResumo(vendas: List<Venda>) {
        val moeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        val quantidadeVendas = vendas.size
        val totalBruto = vendas.sumOf { it.subtotal }
        val totalDescontos = vendas.sumOf { it.desconto }
        val totalAcrescimos = vendas.sumOf { it.acrescimo }
        val totalLiquido = vendas.sumOf { it.totalFinal }

        val totalDinheiro = vendas.sumOf { it.pagamentoDinheiro }
        val totalPix = vendas.sumOf { it.pagamentoPix }
        val totalDebito = vendas.sumOf { it.pagamentoDebito }
        val totalCredito = vendas.sumOf { it.pagamentoCredito }

        val vendasComDesconto = vendas.count { it.desconto > 0.0 }

        binding.txtQuantidadeVendas.text = "Quantidade de vendas: $quantidadeVendas"
        binding.txtTotalBruto.text = "Total bruto: ${moeda.format(totalBruto)}"
        binding.txtTotalDescontos.text = "Total de descontos: ${moeda.format(totalDescontos)}"
        binding.txtTotalAcrescimos.text = "Total de acréscimos: ${moeda.format(totalAcrescimos)}"
        binding.txtTotalLiquido.text = "Total líquido: ${moeda.format(totalLiquido)}"

        binding.txtTotalDinheiro.text = "Total em dinheiro: ${moeda.format(totalDinheiro)}"
        binding.txtTotalPix.text = "Total em Pix: ${moeda.format(totalPix)}"
        binding.txtTotalDebito.text = "Total em débito: ${moeda.format(totalDebito)}"
        binding.txtTotalCredito.text = "Total em crédito: ${moeda.format(totalCredito)}"

        binding.txtVendasComDesconto.text = "Vendas com desconto: $vendasComDesconto"
    }

    /**
     * Ordena as vendas da mais recente para a mais antiga.
     * Se não conseguir ler a data, usa o ID como apoio.
     */
    private fun ordenarVendas(vendas: List<Venda>): List<Venda> {
        return vendas.sortedWith(
            compareByDescending<Venda> { obterTimestampVenda(it) ?: Long.MIN_VALUE }
                .thenByDescending { it.id ?: -1L }
        )
    }

    /**
     * Converte a data da venda em timestamp.
     */
    private fun obterTimestampVenda(venda: Venda): Long? {
        return converterTextoParaDate(venda.dataHora)?.time
    }

    /**
     * Converte a data/hora da venda em Calendar.
     */
    private fun obterCalendarioDaVenda(venda: Venda): Calendar? {
        val data = converterTextoParaDate(venda.dataHora) ?: return null
        return Calendar.getInstance().apply {
            time = data
        }
    }

    /**
     * Tenta converter diferentes formatos de data/hora.
     * Isso ajuda caso o back-end devolva datas em formatos diferentes.
     */
    private fun converterTextoParaDate(texto: String?): Date? {
        if (texto.isNullOrBlank()) return null

        val original = texto.trim()
        val candidatos = linkedSetOf<String>()

        // Texto original
        candidatos.add(original)

        // Texto ajustado sem nanossegundos e sem timezone
        var ajustado = original
        ajustado = ajustado.replace("T", " ")
        ajustado = ajustado.replace(Regex("\\.\\d+"), "")
        ajustado = ajustado.replace(Regex("Z$"), "")
        ajustado = ajustado.replace(Regex("[+-]\\d{2}:\\d{2}$"), "")
        candidatos.add(ajustado)

        val formatos = listOf(
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm"
        )

        for (candidato in candidatos) {
            for (formato in formatos) {
                try {
                    val sdf = SimpleDateFormat(formato, Locale.getDefault())
                    sdf.isLenient = false
                    val data = sdf.parse(candidato)
                    if (data != null) {
                        return data
                    }
                } catch (_: Exception) {
                }
            }
        }

        return null
    }
}