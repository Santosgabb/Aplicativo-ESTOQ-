package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.Usuario;
import com.app.service.UsuarioService;

@RestController // Base para que os outros metodos sejam utilizados
@RequestMapping("/usuarios") // defini o caminho
public class UsuarioController {

	@Autowired // rInjeta o service automaticamente
	private UsuarioService usuarioService;

	 //POST Usado para cadastrar.
	@PostMapping
	public Usuario salvar(@RequestBody Usuario usuario) {
		// RequestBody Pega o JSON enviado.
		return usuarioService.salvar(usuario);
	}

	//GET Usado para listar ou buscar.
	@GetMapping
	public List<Usuario> listar() {
		return usuarioService.listar();
	}
	//GET por id busca pelo id
	@GetMapping("/{id}")
	public Usuario buscar(@PathVariable Long id) {
		// @PathVariable Pega o id da URL.
		return usuarioService.buscar(id);
	}

	//DELETE Usado para apagar
	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		usuarioService.deletar(id);
	}
	//Login
	@PostMapping("/login")
	public ResponseEntity<Usuario> login(@RequestBody Usuario usuario) {
		
	    return usuarioService
	    		.autenticar(usuario.getLogin(), usuario.getSenha())
	            .map(u -> ResponseEntity.ok(u)) // Se achou, retorna 200 OK com o usuário
	            .orElse(ResponseEntity.status(401).build()); // Se não achou, retorna 401
	}
}