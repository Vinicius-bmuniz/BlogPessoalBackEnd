package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.generation.blogpessoal.model.Postagem;

@Repository 
	/* @Repository | Diz que essa classe é um repositório | Se não colocarmos o @Repository o sistema até funciona, porem por boas práticas colocamos.
 	* JpaRepository | é para termos alguns métodos para manipularmos os itens da nossas tabelas e fornece ferramentas para criarmos métodos personalizados (methodCarrys)
 	* JpaRepository<Postagem, Long> | A classe Postagem Repository é uma classe filha da JpaRepository para termos acesso aos métodos
 	* JpaRepository<Postagem, Long> | <Postagem, Long> diz qual Classe estamos trabalhando e o tipo do ID*/
public interface PostagemRepository extends JpaRepository<Postagem, Long>{

	public List <Postagem> findAllByTituloContainingIgnoreCase (@Param("titulo") String titulo);
	/*@Param("titulo") | diz que estamos buscando a informação da tabela com atributo titulo
	 * Esse método é mesmas coisa que:
	 *	SELECT * FROM tb_postagens WHERE titulo LIKE "%titulo%"*/

}
