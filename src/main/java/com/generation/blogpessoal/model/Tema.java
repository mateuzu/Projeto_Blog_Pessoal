package com.generation.blogpessoal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tb_temas")
public class Tema {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "O atributo Descrição é obrigatório")
	private String descricao;
	
	//mappedBy = Mapeia e relaciona o id na tabela estrageira
	//CascadeType.Remove = Determina que ao remover um registro da tabela, será removido os registros relacionados à ele, nesse caso: ao remover um tema, será removido as postagens relacionadas
	@OneToMany(mappedBy = "tema", cascade = CascadeType.REMOVE) 
	@JsonIgnoreProperties("tema") //ignore os temas posteriores relacionados à postagem no retorno do bando de dados
	private List<Postagem> postagem; //OBS: Vale ressaltar que o nome do atribito deve ser o mesmo informado na anotação @JsonIgnoreProperties para evitar o erro

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Postagem> getPostagem() {
		return postagem;
	}

	public void setPostagem(List<Postagem> postagem) {
		this.postagem = postagem;
	}
	
}
