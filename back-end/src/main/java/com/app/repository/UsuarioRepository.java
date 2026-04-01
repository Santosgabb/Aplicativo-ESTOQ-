package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
   
	//usado no login
	Optional<Usuario> findByLoginAndSenha(String login, String senha);
}