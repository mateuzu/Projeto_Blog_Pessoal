package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens") //endpoint
@CrossOrigin(origins = "*", allowedHeaders = "*") //determinando que a classe poderá receber requisição de outros domínios externos, token ainda não determinado
public class PostagemController {

	@Autowired //determinando inleção de dependência
	private PostagemRepository postagemRepository;
	
	@GetMapping //requisiçao GET
	public ResponseEntity<List<Postagem>> getAll() { //os métodos serão do tipo ResponseEntity, visto que ele tem a função de RESPONDER uma requisição HTTP
		return ResponseEntity.ok(postagemRepository.findAll()); //método traz a response .Ok e lista todas as postagens presentes no banco de dados
	}
	
	@GetMapping("/{id}") //as chaves significam que o método irá pegar um valor, dessa forma, ele irá receber um valor do banco de dados, por ex: localhost/postagens/1(id) 
	public ResponseEntity<Postagem> getById(@PathVariable Long id) { //@PathVariable = parametro do método será o mesmo informado no endpoint, por ex: localhost:postagens/4, dessa forma o parametro do método id será = 4
		return postagemRepository.findById(id) //tenta buscar o id através do método da interface
				.map(resp -> ResponseEntity.ok(resp)) //.map = encapsula o atributo e verifica se existe algum valor, se sim: response = .Ok e o objeto referente ao Id será retornado
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); //caso o atributo seja null, irá retornar um NOT FOUND
	}
	
	@GetMapping("/titulo/{titulo}") //endpoint
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo)); //retornando os objetos com base no titulo informado
	}
	
	@PostMapping //determina que ao método irá mandar informações para o banco de dados
	/*
	 * @Valid = valida se os atributos estão dentro das condições determinadas em Model (EX: not null, tamanho e etc)
	 * 
	 * @RequestBody = Recebe o objeto que foi enviado no corpo da requisição (no formato JSON) e insere no parametro postagem do método post
	 * Em resumo, a anotacao é usada para "converter" a solicitacao HTTP(request) em um objeto Java para atualização das informações
	*/
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) { //parametro recebe toda a estrutura da postagem (titulo, texto) e não somente um atributo
		return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem)); //salvando as informações dentro do banco de dados
	}
	
	@PutMapping //determinando que o método irá atualizar informações para o banco de dados, respondendo requisições do tipo HTTP PUT
	public ResponseEntity<Postagem> put (@Valid @RequestBody Postagem postagem) { 
		return postagemRepository.findById(postagem.getId()) //irá retornar uma postagem, caso seja encontrado a partir do id informado
				.map(resposta -> ResponseEntity.status(HttpStatus.OK) //encapsula e mapeia o objeto para verificar se existe, se sim, esse objeto será atualizado
						.body(postagemRepository.save(postagem))) // o objeto já presente no banco de dados será atualizado pelo objeto recebido como parametro do método
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); //caso não encontre nenhuma postagem, response = NOT FOUND 404
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT) //determina que o método terá um status HTTP específico quando a requisição for bem sucedida, nesse caso será retornado o status 204 No content
	@DeleteMapping("/{id}") //determina que o método responderá as requisições HTTP DELETE enviadas no endpoint localhost:postagens/id
	public void delete (@PathVariable Long id) { 
		Optional<Postagem> postagem = postagemRepository.findById(id);
		
		if(postagem.isEmpty()) { 
			throw new ResponseStatusException(HttpStatus.NOT_FOUND); 
		}
		
		postagemRepository.deleteById(id);
	}
	
}
