package com.appstocksmart.app.ui.fornecedor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityEditarFornecedorBinding
import com.appstocksmart.app.model.Fornecedor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarFornecedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarFornecedorBinding
    private var idFornecedor: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarFornecedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idFornecedor = intent.getLongExtra("idFornecedor", -1L)

        if (idFornecedor == -1L) {
            Toast.makeText(this, "Fornecedor inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDadosFornecedor()

        binding.btnSalvarEdicaoFornecedor.setOnClickListener {
            salvarEdicao()
        }
    }

    private fun carregarDadosFornecedor() {

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.buscarFornecedorPorId(idFornecedor)
            .enqueue(object : Callback<Fornecedor> {

                override fun onResponse(
                    call: Call<Fornecedor>,
                    response: Response<Fornecedor>
                ) {

                    if (response.isSuccessful) {

                        val fornecedor = response.body()

                        if (fornecedor == null) {
                            Toast.makeText(
                                this@EditarFornecedorActivity,
                                "Fornecedor não encontrado",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                            return
                        }

                        binding.edtEditarNomeFornecedor.setText(fornecedor.nome)
                        binding.edtEditarTelefoneFornecedor.setText(fornecedor.telefone)
                        binding.edtEditarEmailFornecedor.setText(fornecedor.email)

                    } else {

                        Toast.makeText(
                            this@EditarFornecedorActivity,
                            "Erro ao buscar fornecedor",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<Fornecedor>, t: Throwable) {

                    Toast.makeText(
                        this@EditarFornecedorActivity,
                        "Falha na conexão: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
            })
    }

    private fun salvarEdicao() {

        val nome = binding.edtEditarNomeFornecedor.text.toString().trim()
        val telefone = binding.edtEditarTelefoneFornecedor.text.toString().trim()
        val email = binding.edtEditarEmailFornecedor.text.toString().trim()

        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val fornecedorAtualizado = Fornecedor(
            id = idFornecedor,
            nome = nome,
            telefone = telefone,
            email = email
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.atualizarFornecedor(idFornecedor, fornecedorAtualizado)
            .enqueue(object : Callback<Fornecedor> {

                override fun onResponse(
                    call: Call<Fornecedor>,
                    response: Response<Fornecedor>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@EditarFornecedorActivity,
                            "Fornecedor atualizado",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@EditarFornecedorActivity,
                            "Erro ao atualizar: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Fornecedor>, t: Throwable) {

                    Toast.makeText(
                        this@EditarFornecedorActivity,
                        "Falha na conexão",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}