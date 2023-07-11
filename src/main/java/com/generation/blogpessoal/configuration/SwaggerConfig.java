package com.generation.blogpessoal.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
/*
 * A Camada Configuration é responsável por implementar configurações específicas da aplicação.
 * Em nosso Projeto, vamos utilizar esta Camada para implementar a Documentação da API com o Swagger
 */
@Configuration
public class SwaggerConfig {

	@Bean //entidade da aplicação - objeto que é instanciado, montado e gerenciado pelo Spring
	OpenAPI springBlogPessoalOpenAPI() {
		return new OpenAPI()
				.info(new Info() //insere informações sobre a API
						.title("Projeto Blog Pessoal")
						.description("Projeto Blog Pessoal - Generation Brasil")
						.version("v0.0.1")
						.license(new License() //insere informações referentes a licenças da API
								.name("Generation Brasil")
								.url("https://brazil.generation.org/"))
						.contact(new Contact() //insere informações de contato da pessoa desenvolvedora
								.name("Mateus Ferreira")
								.url("https://github.com/mateuzu")
								.email("mateusf63@gmail.com")))
				.externalDocs(new ExternalDocumentation() //insere informações referentes à documentações externas
						.description("Github")
						.url("https://github.com/mateuzu/"));
	}
	
	//OpenApiCustomiser permite personalizar o Swagger, baseado na Especificação OpenAPI
	@Bean
	public OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() { //neste método, irá personalizar todas as mensagems HTTP responses do Swagger em mensagems personalizadas
		
		return openApi -> {
			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
					.forEach(operation -> {

				ApiResponses apiResponses = operation.getResponses();

				apiResponses.addApiResponse("200", createApiResponse("Sucesso!")); //objeto recebe respostas HTTP(status) e adiciona as novas respostas ao endpoint
				apiResponses.addApiResponse("201", createApiResponse("Objeto Persistido!"));
				apiResponses.addApiResponse("204", createApiResponse("Objeto Excluído!"));
				apiResponses.addApiResponse("400", createApiResponse("Erro na Requisição!"));
				apiResponses.addApiResponse("401", createApiResponse("Acesso Não Autorizado!"));
				apiResponses.addApiResponse("403", createApiResponse("Acesso Proibido!"));
				apiResponses.addApiResponse("404", createApiResponse("Objeto Não Encontrado!"));
				apiResponses.addApiResponse("500", createApiResponse("Erro na Aplicação!"));

			}));
		};
	}
	
	private ApiResponse createApiResponse(String message) { //adiciona uma descrição (mensagem) em cada resposta HTTP
		
		return new ApiResponse().description(message);
	}
}
