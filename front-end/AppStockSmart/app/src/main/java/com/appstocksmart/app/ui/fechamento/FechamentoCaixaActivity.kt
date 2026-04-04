package com.appstocksmart.app.ui.fechamento

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.databinding.ActivityFechamentoCaixaBinding
import com.appstocksmart.app.model.FechamentoCaixa
import com.appstocksmart.app.model.ResumoFechamentoCaixa
import com.appstocksmart.app.model.SaidaCaixa
import com.appstocksmart.app.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class FechamentoCaixaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFechamentoCaixaBinding

    /*
     * Serviço da API
     */
    private val apiService by lazy { ApiClient.apiService }

    /*
     * Lista de saídas lançadas pelo gerente
     */
    private val listaSaidas = mutableListOf<SaidaCaixa>()

    /*
     * Lista de operadores carregados para o spinner
     */
    private val listaOperadores = mutableListOf<Usuario>()

    /*
     * Dados do gerente logado
     */
    private var idUsuarioLogado: Long = 0L
    private var nomeUsuarioLogado: String = ""
    private var perfilUsuarioLogado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFechamentoCaixaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Fechamento de Caixa"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recuperarDadosUsuarioLogado()
        validarAcesso()
        configurarTela()
        configurarEventos()
        carregarOperadoresAtivos()
    }

    /*
     * Recupera os dados enviados pela tela anterior
     */
    private fun recuperarDadosUsuarioLogado() {
        idUsuarioLogado = intent.getLongExtra("idUsuario", 0L)
        nomeUsuarioLogado = intent.getStringExtra("nomeUsuario").orEmpty()
        perfilUsuarioLogado = intent.getStringExtra("perfilUsuario").orEmpty()
    }

    /*
     * Somente gerente pode fechar caixa
     */
    private fun validarAcesso() {
        val ehGerente = perfilUsuarioLogado.equals("GERENTE", ignoreCase = true)

        if (!ehGerente) {
            Toast.makeText(
                this,
                "Somente gerente pode acessar o fechamento de caixa.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    /*
     * Configuração inicial da tela
     */
    private fun configurarTela() {
        binding.progressBarFechamento.visibility = View.GONE
        binding.tvUsuarioLogado.text = "Gerente logado: $nomeUsuarioLogado"
        binding.tvDataValor.text = obterDataAtualFormatadaExibicao()
        atualizarListaSaidasNaTela()

        /*
         * Sempre que o valor inicial mudar,
         * recalcula a previsão do dinheiro em caixa.
         */
        binding.etValorInicialCaixa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                atualizarResumoPrevio()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    /*
     * Eventos dos botões
     */
    private fun configurarEventos() {
        binding.btnAdicionarSaida.setOnClickListener {
            adicionarSaida()
        }

        binding.btnLimparSaidas.setOnClickListener {
            limparSaidas()
        }

        binding.btnFecharCaixa.setOnClickListener {
            fecharCaixa()
        }
    }

    /*
     * Carrega os operadores ativos no spinner.
     * Aqui estamos usando listarUsuarios() porque esse é o endpoint
     * que você já tem no ApiService atual.
     */
    private fun carregarOperadoresAtivos() {
        mostrarCarregamento(true)

        apiService.listarUsuarios().enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                mostrarCarregamento(false)

                if (response.isSuccessful && response.body() != null) {
                    listaOperadores.clear()
                    listaOperadores.addAll(response.body()!!)
                    configurarSpinnerOperadores()
                } else {
                    Toast.makeText(
                        this@FechamentoCaixaActivity,
                        "Não foi possível carregar os operadores.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                mostrarCarregamento(false)
                Toast.makeText(
                    this@FechamentoCaixaActivity,
                    "Erro ao carregar operadores: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /*
     * Configura o spinner com os nomes dos operadores
     */
    private fun configurarSpinnerOperadores() {
        if (listaOperadores.isEmpty()) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listOf("Nenhum operador disponível")
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerOperador.adapter = adapter
            return
        }

        val nomesOperadores = listaOperadores.map { usuario ->
            usuario.nome
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nomesOperadores
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOperador.adapter = adapter
    }

    /*
     * Adiciona uma saída na lista
     */
    private fun adicionarSaida() {
        val descricao = binding.etDescricaoSaida.text.toString().trim()
        val valorTexto = binding.etValorSaida.text.toString()
            .trim()
            .replace(",", ".")

        if (descricao.isEmpty()) {
            binding.etDescricaoSaida.error = "Informe a descrição da saída"
            binding.etDescricaoSaida.requestFocus()
            return
        }

        val valor = valorTexto.toDoubleOrNull()
        if (valor == null || valor <= 0.0) {
            binding.etValorSaida.error = "Informe um valor válido"
            binding.etValorSaida.requestFocus()
            return
        }

        val saida = SaidaCaixa(
            descricao = descricao,
            valor = valor
        )

        listaSaidas.add(saida)

        binding.etDescricaoSaida.text?.clear()
        binding.etValorSaida.text?.clear()

        atualizarListaSaidasNaTela()

        Toast.makeText(this, "Saída adicionada com sucesso.", Toast.LENGTH_SHORT).show()
    }

    /*
     * Limpa todas as saídas
     */
    private fun limparSaidas() {
        if (listaSaidas.isEmpty()) {
            Toast.makeText(this, "Não há saídas para limpar.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Limpar saídas")
            .setMessage("Deseja remover todas as saídas lançadas?")
            .setPositiveButton("Sim") { _, _ ->
                listaSaidas.clear()
                atualizarListaSaidasNaTela()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    /*
     * Atualiza a lista textual de saídas e chama também
     * o resumo prévio da tela.
     */
    private fun atualizarListaSaidasNaTela() {
        if (listaSaidas.isEmpty()) {
            binding.tvListaSaidas.text = "Nenhuma saída adicionada."
        } else {
            val texto = buildString {
                listaSaidas.forEachIndexed { index, saida ->
                    append("${index + 1}. ${saida.descricao} - ${formatarMoeda(saida.valor)}")
                    append("\n")
                }
            }
            binding.tvListaSaidas.text = texto.trim()
        }

        atualizarResumoPrevio()
    }

    /*
     * Lê o valor inicial digitado
     */
    private fun obterValorInicialCaixa(): Double? {
        return binding.etValorInicialCaixa.text.toString()
            .trim()
            .replace(",", ".")
            .toDoubleOrNull()
    }

    /*
     * Faz o cálculo prévio mostrado na tela:
     * valor inicial - total de saídas
     *
     * Aqui ainda não entra o dinheiro das vendas,
     * porque esse total oficial vem do servidor no fechamento.
     */
    private fun atualizarResumoPrevio() {
        val valorInicial = obterValorInicialCaixa() ?: 0.0
        val totalSaidas = listaSaidas.sumOf { it.valor }
        val dinheiroPrevisto = valorInicial - totalSaidas

        binding.tvTotalSaidasValor.text = formatarMoeda(totalSaidas)
        binding.tvDinheiroPrevistoValor.text = formatarMoeda(dinheiroPrevisto)
    }

    /*
     * Envia o fechamento para o back-end
     */
    private fun fecharCaixa() {
        if (listaOperadores.isEmpty()) {
            Toast.makeText(
                this,
                "Nenhum operador disponível para fechamento.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val posicaoSelecionada = binding.spinnerOperador.selectedItemPosition
        if (posicaoSelecionada < 0 || posicaoSelecionada >= listaOperadores.size) {
            Toast.makeText(this, "Selecione um operador válido.", Toast.LENGTH_LONG).show()
            return
        }

        if (idUsuarioLogado <= 0L) {
            Toast.makeText(this, "Gerente logado inválido.", Toast.LENGTH_LONG).show()
            return
        }

        val valorInicialCaixa = obterValorInicialCaixa()
        if (valorInicialCaixa == null) {
            binding.etValorInicialCaixa.error = "Informe o valor inicial do caixa"
            binding.etValorInicialCaixa.requestFocus()
            return
        }

        if (valorInicialCaixa < 0.0) {
            binding.etValorInicialCaixa.error = "O valor inicial não pode ser negativo"
            binding.etValorInicialCaixa.requestFocus()
            return
        }

        val operadorSelecionado = listaOperadores[posicaoSelecionada]
        val operadorId = operadorSelecionado.id?.toString()?.toLongOrNull() ?: 0L

        if (operadorId == 0L) {
            Toast.makeText(this, "ID do operador inválido.", Toast.LENGTH_LONG).show()
            return
        }

        val observacao = binding.etObservacaoFechamento.text.toString().trim()

        val fechamento = FechamentoCaixa(
            operadorId = operadorId,
            gerenteResponsavelId = idUsuarioLogado,
            dataReferencia = obterDataAtualFormatoBackend(),
            valorInicialCaixa = valorInicialCaixa,
            saidas = listaSaidas.toList(),
            observacao = if (observacao.isBlank()) null else observacao
        )

        mostrarCarregamento(true)

        apiService.fecharCaixa(fechamento).enqueue(object : Callback<ResumoFechamentoCaixa> {
            override fun onResponse(
                call: Call<ResumoFechamentoCaixa>,
                response: Response<ResumoFechamentoCaixa>
            ) {
                mostrarCarregamento(false)

                if (response.isSuccessful && response.body() != null) {
                    val resumo = response.body()!!
                    mostrarResumoFechamento(resumo)
                } else {
                    val mensagemErro = try {
                        response.errorBody()?.string().orEmpty()
                    } catch (e: Exception) {
                        ""
                    }

                    Toast.makeText(
                        this@FechamentoCaixaActivity,
                        tratarMensagemErro(mensagemErro),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResumoFechamentoCaixa>, t: Throwable) {
                mostrarCarregamento(false)
                Toast.makeText(
                    this@FechamentoCaixaActivity,
                    "Erro ao fechar caixa: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /*
     * Mostra o resumo devolvido pelo back-end
     */
    private fun mostrarResumoFechamento(resumo: ResumoFechamentoCaixa) {
        val mensagem = """
            Operador: ${resumo.operadorNome ?: "-"}
            Data: ${formatarDataParaExibicao(resumo.data)}

            Valor inicial do caixa: ${formatarMoeda(resumo.valorInicialCaixa)}
            Quantidade de vendas: ${resumo.quantidadeVendas}
            Total de vendas: ${formatarMoeda(resumo.totalVendas)}
            Total em dinheiro: ${formatarMoeda(resumo.totalDinheiro)}
            Total em PIX: ${formatarMoeda(resumo.totalPix)}
            Total em débito: ${formatarMoeda(resumo.totalDebito)}
            Total em crédito: ${formatarMoeda(resumo.totalCredito)}
            Total de saídas: ${formatarMoeda(resumo.totalSaidas)}

            Dinheiro final em caixa: ${formatarMoeda(resumo.dinheiroFinalEmCaixa)}
            Observação: ${resumo.observacao ?: "-"}
        """.trimIndent()

        AlertDialog.Builder(this@FechamentoCaixaActivity)
            .setTitle("Fechamento realizado com sucesso")
            .setMessage(mensagem)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }

    /*
     * Ativa / desativa campos enquanto faz chamada na API
     */
    private fun mostrarCarregamento(mostrar: Boolean) {
        binding.progressBarFechamento.visibility = if (mostrar) View.VISIBLE else View.GONE
        binding.btnAdicionarSaida.isEnabled = !mostrar
        binding.btnLimparSaidas.isEnabled = !mostrar
        binding.btnFecharCaixa.isEnabled = !mostrar
        binding.spinnerOperador.isEnabled = !mostrar
        binding.etDescricaoSaida.isEnabled = !mostrar
        binding.etValorSaida.isEnabled = !mostrar
        binding.etValorInicialCaixa.isEnabled = !mostrar
        binding.etObservacaoFechamento.isEnabled = !mostrar
    }

    /*
     * Data para exibição
     */
    private fun obterDataAtualFormatadaExibicao(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } else {
            ""
        }
    }

    /*
     * Data enviada ao back-end
     */
    private fun obterDataAtualFormatoBackend(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        } else {
            ""
        }
    }

    /*
     * Formata data para exibição
     */
    private fun formatarDataParaExibicao(data: String?): String {
        if (data.isNullOrBlank()) return "-"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.parse(data).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } else {
                data
            }
        } catch (e: Exception) {
            data
        }
    }

    /*
     * Formata valor monetário
     */
    private fun formatarMoeda(valor: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)
    }

    /*
     * Traduz erros do back-end para mensagens melhores
     */
    private fun tratarMensagemErro(mensagemBruta: String): String {
        if (mensagemBruta.isBlank()) {
            return "Não foi possível concluir o fechamento de caixa."
        }

        return when {
            mensagemBruta.contains("gerente responsável", ignoreCase = true) ->
                "O gerente responsável não foi enviado corretamente."

            mensagemBruta.contains("data de referência", ignoreCase = true) ->
                "A data de referência não foi enviada corretamente."

            mensagemBruta.contains("valor inicial", ignoreCase = true) ->
                "Informe corretamente o valor inicial do caixa."

            mensagemBruta.contains("já existe fechamento", ignoreCase = true) ||
                    mensagemBruta.contains("já foi realizado", ignoreCase = true) ->
                "Este operador já teve o caixa fechado nesta data."

            mensagemBruta.contains("dinheiro disponível", ignoreCase = true) ->
                "O total das saídas não pode ser maior que o dinheiro disponível em caixa."

            mensagemBruta.contains("saídas", ignoreCase = true) &&
                    mensagemBruta.contains("maior", ignoreCase = true) ->
                "O total das saídas não pode ser maior que o dinheiro disponível em caixa."

            else -> mensagemBruta
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}