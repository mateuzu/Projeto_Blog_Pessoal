package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/*
 * A Classe JwtService é responsável por criar e validar o Token JWT.
 * O Token JWT será criado durante o processo de autenticação (login) do usuário e será validado em todas as requisições HTTP enviadas para os endpoints protegidos
 * Basicamente, cria o Token e valida as requisições enviadas para endpoints protegidos
 */

@Component
public class JwtService {

	public static final String SECRET = "2d586054629ace4adde1535d3dd9e12a884f3a701996ec4089ffa82321566237";

	
	private Key getSignKey() { //método responsável por codificar a String SECRET
		byte[] keyBytes = Decoders.BASE64.decode(SECRET); //vetor recebe o resultado da codificação em base 64
		return Keys.hmacShaKeyFor(keyBytes); //retorna a chave de assinatura do Token JWT 
	}
	
	//Claims = Declarações inseridas no payload do Token JWT, ou seja, informações declaradas sobre o assunto/entidade
	private Claims extractAllClaims (String token) { //recebe um token como parametro (será enviado no cabeçalho da requisição HTTP)
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build() //verifica se a assinatura do Token é válida
				.parseClaimsJws(token).getBody(); //caso seja válida, extrai todas as claims do corpo do Token e retornar as claims encontradas
	}
	
	/*Funcion = interface funcional do Java que recebe 2 args (Claims e T) O 1° é o tipo de entrada e o 2° é o tipo de saída, ou seja, receberá o Método get equivalente a claim que se deseja recuperar o valor
	 * Observe que o tipo <T> é o mesmo do retorno do método
	 * 
	 * No exemplo, a Interface Funcional Function recebeu como entrada a Classe Claims e na saída, receberá a execução do Método getSubject(), que está sendo chamado através do operador de referência de métodos (::), que retorna o valor da claim sub
	 * Por isso que o Retorno do Método extractClaim será T, porque o tipo do parâmetro T vai depender do Método get referenciado no parâmetro da Interface Funcional Function< Claims, T > claimsResolver.
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { //retorna uma claim específica inserida no payload (sub, exp), basicamente ele "quebra" o token em payloads distintos
		final Claims claims = extractAllClaims(token); //recebe todas as claims que foram encontradas no corpo do Token
		return claimsResolver.apply(claims);
	}
	
	public String extractUsername(String token) { //retorna os dados da Claim sub onde se encontra o e-mail do usuário
		return extractClaim(token, Claims::getSubject); //interface recebeu a entrada da classe Claims e na saída executa o método getSubject(), que está referenciado através do operador ::
	}
	
	public Date extracExpiration(String token) { //retorna os dados da Claim exp, onde se encontra a data e hora da expiração do token JWT
		return extractClaim(token, Claims::getExpiration); //retorna o método getExpiratoin() da interface funcional referenciado pelo operador ::
	}
	
	private Boolean isTokenExpired(String token) {//retorna os dados da Claim exp e verifica se o Token está ou não expirado. Se a data/hora do token for anterior a data/hora atual, então o Token está expirado
		return extracExpiration(token).before(new Date());
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) { //valida se o Token pertece ao usuário que enviou o Token através do cabeçalho de requisição HTTP
		final String username = extractUsername(token); //extrai a claim sub (contendo email do usuario) inserida no corpo do Token (enviado no parametro do método)
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); //verifica se o usuário extraído é o mesmo do usuário autenticado e se o Token não está expirado
	}
	
	public String createToken(Map<String, Object> claims, String userName) { //Método recebe uma Collection Map como argumento, será utilizada para receber Claims personalizadas
		return Jwts.builder() //método responsável por criar o Token JWR
								.setClaims(claims) //insere as claims personalizadas no payload do Token JWT
								.setSubject(userName) //insere a claim sub (subject) preenchida com usuario(email) no payload do JWT
								.setIssuedAt(new Date(System.currentTimeMillis())) //insere a claim iat (data/hora criação) preenchida com data/hora exata da criação do Token
								.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) //insere a claim exp (data/hora expiração) preenchida com data/hora exata da criação do Token, somada ao tempo limite do Token (no ex: 60 min)
								.signWith(getSignKey(), SignatureAlgorithm.HS256).compact(); //insere a assinatura do Tok e o algoritmo de encriptação do Token
	}
	
	public String generateToken(String userName) { //gera um novo Token a partir do usuario (email) que será recebido como parametro. O método irá criar o Token sempre que o usuario se autenticar e o Token estiver expirado ou ainda naõ tiver sido gerado
		Map<String, Object> claims = new HashMap<>(); //cria uma collection para enviar claims personalizadas
		return createToken(claims, userName); //retorna a execução do método createToken, ao ser executado irá gerar um Token JWT
	}
}
