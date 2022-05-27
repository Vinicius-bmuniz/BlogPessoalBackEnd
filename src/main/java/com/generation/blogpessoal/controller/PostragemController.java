package com.generation.blogpessoal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;


	/*@RestController | Diz que essa é a camada de controller
	 * @RequestMapping | Diz o link da página
	 * @CrossOrigin | O front end fica em servidor diferente do BackEnd, por isso temos que ter esse comando*/
@RestController
@RequestMapping ("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostragemController {

	
		/*Injeção de dependencias | Inversão de controle
		 * Transfere a responsabilidade da classe PostagemRepository para postagemRepository
		 * Basicamente tira a responsabilidade da gente de criar os objetos e coloca a responsabilidade na classe
		*/
	@Autowired
	private PostagemRepository postagemRepository;
	
		/*@GetMapping 			é como se fosse o atalho do anottation para simplificar a chamada do método de GET para retornar um valor|
		 *ResponseEntity 		é para retornar um objeto|
		 *.findAll()  			Retorna todos da lista
		 *postagemRepository 	está referenciando a Classe PostagemRepository que está referenciando a Classe Postagem
		 *	| === postagemRepository > PostagemRepository > Postagem === |
		 *						Ou seja, estamos usando uma chamada em cascata*/
	@GetMapping
	public ResponseEntity<List<Postagem>> getAll(){ //Aqui estamos criando um método para listar todas as postagens
		return ResponseEntity.ok(postagemRepository.findAll());//Aqui estamos retornando a resposta do método e retornando um ok (2##)
		
		/*.findAll = SELECT * FROM	|	Ou seja, estamos puxando todos os itens da tabela que selecionamos
		 * No caso acima puxamos todos os itens da tabela postagemRepository > PostagemRepository > Postagem > tb_postagens*/
		
		
		
	}
}
