package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.generation.blogpessoal.model.Postagem;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long>{
	
	//assinatura do método é determinada utilizando palavras chave, dessa forma o Jpa/Hibernate pode realizar uma query(consulta) dentro do banco de dados
	public List<Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo") String titulo); //@Param = valida e vincula a informação externa vindo do valor entre aspas (URl/Endpoint) para o parametro do método
}
