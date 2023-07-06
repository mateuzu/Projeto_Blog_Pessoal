package com.generation.blogpessoal.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;

/*
 * A Classe UserDetailsServiceImpl é uma implementação da Interface UserDetailsService, responsável por validar a existência de um usuário no sistema através do Banco de dados
 * Deve retornar um Objeto da Classe UserDetailsImpl com os dados do Objeto encontrado no Banco de dados. 
 * A busca será feita através do atributo usuario (e-mail).
 * 
 * Bascimanete, ela verifica se existe o usuário dentro do banco de dados
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(username);
		
		if(usuario.isPresent())
			return new UserDetailsImpl(usuario.get());
		else 
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
	}

}
