package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

/*
 * @SpringBootTest = Cria e inicializa o ambiente de teste
 * webEnvironent = Porta onde será executado o teste
 * WebEnvironment.Random_Port = Garante que durante os teste o Spring não utilize a porta da aplicação (padrão 8080) caso esteja em execução. Com isso, o Spring busca uma porta livre para executar testes
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Ciclo de vida do teste (só funciona durante sua execução)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate; //Envia requisições para a aplicação
	
	@Autowired
	private UsuarioService usuarioService; //Acessa o banco de dados através da classe UsuarioService
	
	@Autowired
	private UsuarioRepository usuarioRepository; //Acessa a classe UsuarioRepository para limpar o banco de dados
	
	@BeforeAll //garante que o método sempre será executado primeiro 
	void start() {
		
		usuarioRepository.deleteAll(); //apaga os dados da tabelamdo banco de dados virtual
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", "-")); //cria o usário para testar os métodos protegidos por autenticação
	}
	
	@Test //anotação para determinar que esse método será de teste
	@DisplayName("Cadastrar um Usuário") //configura uma mensagem que será exibida ao invés do nome do Método
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Paulo Antues", "paulo_antunes@gmial.com", "12345678", "-")); //Objeto do tipo HttpEntity cria um corpo para enviar as requisições
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class); //Objeto do tipo Response para armazenar a resposta HTTP
		/*
		 * exchange = Método para enviar a requisição, recebe como parâmetro o endponint, o método específico que será testado e o corpo(body) da requisição
		 * assertEquals = Afirma que dois valores são iguais.
		 */
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode()); //verifica se os dois valores são iguais. No ex: Compara se o status da resposta(Response) é igual a CREATED
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario((new Usuario(0L, "Maria da Silva", "maria_silva@email.com", "12345678", "-")));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva",
				"maria_silva@email.com", "12345678", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar",HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), "Juliana Andrew Ramos", "juliana_ramos@email.com.br", "juliana123", "-");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot") //autenticar o usuário com o login definido no método start pois para atualizar um usuario é necessario autenticar um usuario
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os Usuarios")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
		usuarioService.cadastrarUsuario(new Usuario(0L, "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
}
