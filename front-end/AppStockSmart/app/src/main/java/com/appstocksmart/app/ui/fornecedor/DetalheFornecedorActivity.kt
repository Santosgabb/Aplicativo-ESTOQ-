package com.appstocksmart.app.ui.fornecedor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityDetalheFornecedorBinding
import com.appstocksmart.app.model.Fornecedor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalheFornecedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheFornecedorBinding
    private var idFornecedor: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheFornecedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idFornecedor = intent.getLongExtra("idFornecedor", -1L)

        if (idFornecedor == -1L) {
            Toast.makeText(this, "Fornecedor inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDetalhes()

        binding.btnEditarDetalheFornecedor.setOnClickListener {
            val intent = Intent(this, EditarFornecedorActivity::class.java)
            intent.putExtra("idFornecedor", idFornecedor)
            startActivity(intent)
        }

        binding.btnExcluirDetalheFornecedor.setOnClickListener {
            excluirFornecedor()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDetalhes()
    }

    private fun carregarDetalhes() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.buscarFornecedorPorId(idFornecedor).enqueue(object : Callback<Fornecedor> {

            override fun onResponse(call: Call<Fornecedor>, response: Response<Fornecedor>) {
                if (response.isSuccessful) {
                    val fornecedor = response.body()

                    if (fornecedor == null) {
                        Toast.makeText(
                            this@DetalheFornecedorActivity,
                            "Fornecedor não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    binding.txtDetalheNomeFornecedor.text = "Nome: ${fornecedor.nome}"
                    binding.txtDetalheTelefoneFornecedor.text = "Telefone: ${fornecedor.telefone}"
                    binding.txtDetalheEmailFornecedor.text = "Email: ${fornecedor.email}"

                } else {
                    Toast.makeText(
                        this@DetalheFornecedorActivity,
                        "Erro ao buscar fornecedor: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Fornecedor>, t: Throwable) {
                Toast.makeText(
                    this@DetalheFornecedorActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }

    private fun excluirFornecedor() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.deletarFornecedor(idFornecedor).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DetalheFornecedorActivity,
                        "Fornecedor excluído com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@DetalheFornecedorActivity,
                        "Erro ao excluir fornecedor: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@DetalheFornecedorActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}