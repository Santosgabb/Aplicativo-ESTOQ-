package com.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.model.Usuario;
import com.app.repository.UsuarioRepository;

/*
 * @Service
 * Classe de regra de negócio
 * Fica entre Controller e Repository
 */
@Service
public class UsuarioService {

    /*
     * Injeta o repository automaticamente
     */
    @Autowired
    private UsuarioRepository usuarioRepository;

    /*
     * Salvar ou atualizar usuário
     */
    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /*
     * Listar todos os usuários
     */
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    /*
     * Buscar usuário por id
     */
    public Usuario buscar(Long id) {
        return usuarioRepository
                .findById(id)
                .orElse(null);
    }

    /*
     * Deletar usuário
     */
    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    /*
     * Login do sistema
     * Usado no Android
     */
    public Optional<Usuario> autenticar(String login, String senha) {

        return usuarioRepository
                .findByLoginAndSenha(login, senha);
    }
}