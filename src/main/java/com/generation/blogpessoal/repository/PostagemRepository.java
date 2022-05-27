package com.generation.blogpessoal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.generation.blogpessoal.model.Postagem;

@Repository 
	/* @Repository | Diz que essa classe é um repositório
 	*
 	* JpaRepository<Postagem, Long> | A classe Postagem Repository é uma classe filha da JpaRepository para termos acesso aos métodos
 	* JpaRepository<Postagem, Long> | <Postagem, Long> diz qual Classe estamos trabalhando e o tipo do ID*/
public interface PostagemRepository extends JpaRepository<Postagem, Long>{

}
