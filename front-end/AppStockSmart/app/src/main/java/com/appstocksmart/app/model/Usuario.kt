package com.appstocksmart.app.model

data class Usuario(
    val id: Long? = null,
    var nome: String,
    var login: String,
    var senha: String,
    var perfil: String
)