package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity //determinando que a classe será uma entidade em forma de tabela dentro de um banco de dados
@Table(name = "tb_postagens") //determinando o nome da tabela
public class Postagem {
	
	@Id //determinando que o ID (identificador único) da tabela será a chave primária
	@GeneratedValue(strategy = GenerationType.IDENTITY) //determinando que o id será incrementado automaticamente
	private Long id;
	
	@NotBlank(message = "O título é obrigatório") //determinando que o atributo não poderá ficar vazio, sendo obrigatório o preenchimento
	@Size(min = 3, max = 100) //determinando o tamanho do atributo
	private String titulo;
	
	@NotBlank
	@Size(min = 5, max = 1000)
	private String texto;
	
	@UpdateTimestamp
	private LocalDateTime data;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
}
