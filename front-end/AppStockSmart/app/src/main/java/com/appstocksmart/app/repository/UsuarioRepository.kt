package com.appstocksmart.app.repository

import com.appstocksmart.app.model.Usuario

object UsuarioRepository {

    private val usuarios = mutableListOf(
        Usuario(1, "Gerente", "gerente", "123", "GERENTE"),
        Usuario(2, "Caixa", "caixa", "123", "CAIXA")
    )

    fun autenticar(login: String, senha: String): Usuario? {
        return usuarios.find {
            it.login == login && it.senha == senha
        }
    }

    fun validarGerente(login: String, senha: String): Boolean {
        return usuarios.any {
            it.login == login &&
                    it.senha == senha &&
                    it.perfil == "GERENTE"
        }
    }

    fun listar(): List<Usuario> {
        return usuarios
    }

    fun adicionar(usuario: Usuario) {
        usuarios.add(usuario)
    }

    fun atualizar(usuarioAtualizado: Usuario) {
        val index = usuarios.indexOfFirst { it.id == usuarioAtualizado.id }
        if (index != -1) {
            usuarios[index] = usuarioAtualizado
        }
    }

    fun remover(id: Long) {
        usuarios.removeIf { it.id == id }
    }

    fun buscarPorId(id: Long): Usuario? {
        return usuarios.find { it.id == id }
    }

    fun proximoId(): Long {
        return if (usuarios.isEmpty()) {
            1L
        } else{
            (usuarios.maxOf { it.id ?: 0L }) + 1L
        }
    }
}