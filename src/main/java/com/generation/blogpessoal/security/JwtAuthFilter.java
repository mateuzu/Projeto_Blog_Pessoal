package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * A Classe JwtAuthFilter é responsável por pré-processar todas as Requisições HTTP
 * Ele busca do Token JWT enviado no Cabeçalho (header) da Requisição, na propriedade Authorization, cria a autenticação e insere o usuário autenticado na SecurityContext.
 * A SecurityContext é responsável por manter a Autenticação e solicitar informações de segurança específicas, quando o usuário envia uma Requisição HTTP solicitando acesso em algum endpoint da aplicação.
 * 
 * A Classe JwtAuthFilter é o que chamamos no Spring Security de Filtro de Servlet Personalizado (Custom Servlet Filter)
 * Em nossa implementação, foi criado para interceptar e validar o Token JWT em toda e qualquer Requisição HTTP.
 * 
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	/*
	 * Parametros do método:
	 * HttpServletRequest = Requisição HTTP que será analisada pelo filtro
	 * HttpServletResponse = Resposta da requisição analisada pelo filtro. Essa resposta será construída e enviada pelo Filtro de Servlet
	 * FilterChain = É um objeto fornecido pela Spring Security, indicando qual será o próximo filtro que será executado
	 * 
	 * Exceptions:
	 * ServletException = Erros específicos nas operações do filtro
	 * IOException = Erro nas operações de entrada e saída da aplicação
	 * 
	 * Bearer (Palavra reservada):
	 * Para enviar um Token JWT através do Header da requisição, é necessário acrescentar a palavrar Bearer (ao portador), seguido de um espaço em branco, e na sequência o Token
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) //implementa um filtro de servlet personalizado
			throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization"); //recebe o Token presente no cabeçalho (header) da requisição
		String token = null; //recebe o Token presente no cabeçalho da requisição da propriedade Authorization, porém sem a palavra Bearer
		String username = null; //recebe o e-mail presente no payload do Token
		
		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) { 
				token = authHeader.substring(7); //token recebe authHeader sem a palavra com espaço em branco "Bearer "
				username = jwtService.extractUsername(token);
			}
			
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //verifica se o usuário é diferente de nulo e checa o contexto de segurança que retorna se o usuário não está autenticado na Spring Security
				UserDetails userDetails = userDetailsService.loadUserByUsername(username); //construção do Objeto UserDetails: armazena informações do usuário autenticado através da classe UserDetailsServiceImpl, que checa se o usuário existe no banco de dados
				
				if (jwtService.validateToken(token, userDetails)) { //valida o Token
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					/* Autenticar o usuário na Spring Security e definir um objeto Authentication, utilizado ára autenticar o usuário na Spring Context com o objetivo de manter o usuário conectado na Security Context até o Token expirar
					 * O construtor do método recebe 3 parâmetros:
					 * userDetails: Objeto da Classe UserDetails, que contém os dados do usuário autenticado na Spring Security.
					 * credentials: Senha do usuário. Geralmente enviamos null neste parâmetro, porque o Objeto UserDetails já possui a senha criptografada.
					 * authorities: São as Autorizações do usuário (Roles), que será obtida através do Método getAuthorities() da Classe UserDetails, implementado na Classe UserDetailsImpl. Como não estamos implementando as Autorizações, será enviada uma Collection vazia.
					 */
					
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
				
			}
			
			filterChain.doFilter(request, response);
			
		} catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
				| SignatureException | ResponseStatusException e) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return;
		}
		/*
		 * ExpiredJwtException: O Token JWT Expirou!
		 * UnsupportedJwtException: O Token não está no formato JWT.
		 * MalformedJwtException: A construção do Token está errada e ele deve ser rejeitado.
		 * SignatureException: A assinatura do Token JWT não confere.
		 * ResponseStatusException: Retorna um HTTP Status em conjunto com uma Exception.
		 */
	}

}
