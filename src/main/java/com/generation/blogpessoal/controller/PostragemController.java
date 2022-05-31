package com.generation.blogpessoal.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;


	/*@RestController 	| Diz que essa é a camada de controller
	 *@RequestMapping 	| Diz o link da página
	 *@CrossOrigin 		| O front end fica em servidor diferente do BackEnd, por isso temos que ter esse comando*/
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
	@GetMapping // se não estiver com parâmetros, ele está utilizando o request mapping acima
	public ResponseEntity<List<Postagem>> getAll(){ //Aqui estamos criando um método para listar todas as postagens
		return ResponseEntity.ok(postagemRepository.findAll());//Aqui estamos retornando a resposta do método e retornando um ok (2##)
		
		/*.findAll = SELECT * FROM	|	Ou seja, estamos puxando todos os itens da tabela que selecionamos
		 * No caso acima puxamos todos os itens da tabela postagemRepository > PostagemRepository > Postagem > tb_postagens*/
	}
	
	
	/*@PathVariable Long id | Puxa uma váriavel chamada id do tipo long
	 *Optional é uma classe que faz um encapsulamento de um objeto que não pode ser nulo
	 *EX: se eu colocar que quero o id 100 e não existir ele irá retornar um objeto nulo e dara o erro NullPointException
	 *Expressões Lambda | O mercado todo pede.
	 *Expressões Lambda | Substitui o if de uma maneira muito mais curta e simples, não tem um corpo definido que nem o if
	 *Optional | é uma opção que verifica se existe o objeto e caso não exista retorna vazio ou erro 4xx
	 * */
	@GetMapping ("/{id}") // O que estiver dentro das {} significa que é uma variável
	public ResponseEntity<Postagem> getById(@PathVariable Long id){
		return postagemRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta)) //resposta é o objeto que irá receber o resultado da minha procura caso encontre | .ok retorna o código de acerto 2xx
				.orElse(ResponseEntity.notFound().build()); //se não encontrar ele retornar um erro | .notfound diz que é quando não encontra | .build constroi a resposta no corpo | retorna o erro 4xx
				
		/*SELECT * FROM tb_postagens WHERE id = 3; | Seria mesma coisa que utilizar assim*/
	}
	
	@GetMapping ("/titulo/{titulo}") //Estamos criando o endpoint /titulo/{tituloDaPostagem}
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){ //Aqui estamos criando um método para listar todas as postagens
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
		
		/*SELECT * FROM	tb_postagens WHERE titulo LIKE "%titulo%"*/
	}
	
	@PostMapping
	public ResponseEntity<Postagem> postPostagem (@Valid @RequestBody Postagem postagem){
		return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));	
		
	}
	@PutMapping
	public ResponseEntity<Postagem> putPostagem (@Valid @RequestBody Postagem postagem){
		if (postagem.getId() == null) 
			return ResponseEntity.notFound().build();
		return postagemRepository.findById(postagem.getId())
				.map(mensagem -> ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}") 
	public ResponseEntity<?> deletaPostagem (@PathVariable long id){
		return postagemRepository.findById(id)
				.map(resposta -> {
					postagemRepository.deleteById(id);
					return ResponseEntity.status(HttpStatus.NO_CONTENT).build();		
				})
				.orElse(ResponseEntity.notFound().build());
	}
}
