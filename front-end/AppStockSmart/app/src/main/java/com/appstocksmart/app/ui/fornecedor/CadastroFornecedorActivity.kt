package com.appstocksmart.app.ui.fornecedor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityCadastroFornecedorBinding
import com.appstocksmart.app.model.Fornecedor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroFornecedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroFornecedorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroFornecedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSalvarFornecedor.setOnClickListener {
            salvarFornecedor()
        }
    }

    private fun salvarFornecedor() {

        val nome = binding.edtNomeFornecedor.text.toString().trim()
        val telefone = binding.edtTelefoneFornecedor.text.toString().trim()
        val email = binding.edtEmailFornecedor.text.toString().trim()

        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val fornecedor = Fornecedor(
            id = null,
            nome = nome,
            telefone = telefone,
            email = email
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.salvarFornecedor(fornecedor)
            .enqueue(object : Callback<Fornecedor> {

                override fun onResponse(
                    call: Call<Fornecedor>,
                    response: Response<Fornecedor>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@CadastroFornecedorActivity,
                            "Fornecedor cadastrado",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@CadastroFornecedorActivity,
                            "Erro: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Fornecedor>, t: Throwable) {

                    Toast.makeText(
                        this@CadastroFornecedorActivity,
                        "Falha: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}