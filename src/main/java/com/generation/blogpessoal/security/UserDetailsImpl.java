package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;

/*
 * A Classe UserDetailsImpl implementa a Interface UserDetails
 * Tem como principal funcionalidade fornecer as informações básicas do usuário para o Spring Security (Usuário, Senha, Direitos de acesso e as Restrições da conta).
 */
public class UserDetailsImpl implements UserDetails{

	//o atributo seriaversion é usado para verificar se uma classe carregada e o objeto serializado são compatíveis ou gerados pela mesma versão da classe
	private static final long serialVersionUID = 1L; //identificador da versão da classe
	
	private String userName;
	private String passWord;
	private List<GrantedAuthority> authorities; //recebe direitos de acesso do usuário (autorizações ou roles)
	
	public UserDetailsImpl(Usuario user) { //construtor preenchido com os métodos de acesso do objeto Usuario
		this.userName = user.getUsuario();
		this.passWord = user.getSenha();
	}

	public UserDetailsImpl() {
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { //retorna os direitos de acesso do usuário, retorna uma collection vazia pois não iremos implementar esses direitos
		return authorities;
	}

	@Override
	public String getPassword() {
		return passWord;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() { //Indica se tempo de acesso do usuário expirou. Uma conta expirada não pode ser autenticada (return false).
		return true;
	}

	@Override
	public boolean isAccountNonLocked() { //Indica se o usuário está bloqueado ou desbloqueado. Um usuário bloqueado não pode ser autenticado (return false)
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() { //Indica se a senha do usuário expirou (precisa ser trocada). Senha expirada impede a autenticação (return false).
		return true;
	}

	@Override
	public boolean isEnabled() { //Indica se o usuário está habilitado ou desabilitado. Um usuário desabilitado não pode ser autenticado (return false).
		return true;
	}

}
