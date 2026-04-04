package com.appstocksmart.app.ui.fechamento

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appstocksmart.app.adapter.HistoricoFechamentoCaixaAdapter
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.databinding.ActivityFechamentoCaixaHistoricoBinding
import com.appstocksmart.app.model.HistoricoFechamentoCaixa
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FechamentoCaixaHistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFechamentoCaixaHistoricoBinding
    private lateinit var adapter: HistoricoFechamentoCaixaAdapter

    // Serviço da API
    private val apiService by lazy { ApiClient.apiService }

    // Lista usada no RecyclerView
    private val lista = mutableListOf<HistoricoFechamentoCaixa>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout com ViewBinding
        binding = ActivityFechamentoCaixaHistoricoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura título da barra superior
        supportActionBar?.title = "Histórico de Fechamentos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configurarRecyclerView()
        carregarHistorico()
    }

    /*
     * Configura o RecyclerView da tela.
     */
    private fun configurarRecyclerView() {
        adapter = HistoricoFechamentoCaixaAdapter(lista)
        binding.recyclerHistoricoFechamento.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistoricoFechamento.adapter = adapter
    }

    /*
     * Busca o histórico no back-end.
     */
    private fun carregarHistorico() {
        // Mostra loading
        binding.progressBarHistorico.visibility = View.VISIBLE
        binding.recyclerHistoricoFechamento.visibility = View.GONE
        binding.tvSemHistorico.visibility = View.GONE

        apiService.listarHistoricoFechamentosCaixa()
            .enqueue(object : Callback<List<HistoricoFechamentoCaixa>> {

                override fun onResponse(
                    call: Call<List<HistoricoFechamentoCaixa>>,
                    response: Response<List<HistoricoFechamentoCaixa>>
                ) {
                    binding.progressBarHistorico.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        lista.clear()
                        lista.addAll(response.body()!!)

                        if (lista.isEmpty()) {
                            binding.tvSemHistorico.visibility = View.VISIBLE
                            binding.recyclerHistoricoFechamento.visibility = View.GONE
                        } else {
                            /*
                             * Ordena por data decrescente.
                             *
                             * Como a data vem no formato ISO (yyyy-MM-dd),
                             * a ordenação por String já funciona corretamente,
                             * sem precisar usar LocalDate.
                             */
                            lista.sortByDescending { it.dataReferencia ?: "" }

                            adapter.notifyDataSetChanged()
                            binding.recyclerHistoricoFechamento.visibility = View.VISIBLE
                            binding.tvSemHistorico.visibility = View.GONE
                        }
                    } else {
                        binding.tvSemHistorico.visibility = View.VISIBLE
                        binding.recyclerHistoricoFechamento.visibility = View.GONE

                        Toast.makeText(
                            this@FechamentoCaixaHistoricoActivity,
                            "Não foi possível carregar o histórico.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<HistoricoFechamentoCaixa>>,
                    t: Throwable
                ) {
                    binding.progressBarHistorico.visibility = View.GONE
                    binding.tvSemHistorico.visibility = View.VISIBLE
                    binding.recyclerHistoricoFechamento.visibility = View.GONE

                    Toast.makeText(
                        this@FechamentoCaixaHistoricoActivity,
                        "Erro ao carregar histórico: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /*
     * Ação da seta de voltar da ActionBar.
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}