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
import com.generation.blogpessoal.repository.TemaRepository;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;


	/*@RestController 	| Diz que essa é a camada de controller
	 *@RequestMapping 	| Diz o link da página
	 *@CrossOrigin 		| O front end fica em servidor diferente do BackEnd, por isso temos que ter esse comando*/
@RestController
@RequestMapping ("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

		/*Injeção de dependencias | Inversão de controle
		 * Transfere a responsabilidade da classe PostagemRepository para postagemRepository
		 * Basicamente tira a responsabilidade da gente de criar os objetos e coloca a responsabilidade na classe
		*/
	
	@Autowired
	private TemaRepository temaRepository;
	
	@Autowired
	private PostagemRepository postagemRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	
	@GetMapping 
	public ResponseEntity<List<Postagem>> getAll(){ 
		return ResponseEntity.ok(postagemRepository.findAll());
	}
	
	@GetMapping ("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id){
		return postagemRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping ("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){ 
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	@PostMapping
	public ResponseEntity<Postagem> postPostagem (@Valid @RequestBody Postagem postagem){
		if (temaRepository.existsById(postagem.getTema().getId())) 
			return ResponseEntity.ok(postagemRepository.save(postagem));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	//Put abaixo não estava verificando se o tema existe
	/*@PutMapping
	public ResponseEntity<Postagem> putPostagem (@Valid @RequestBody Postagem postagem){
		if (postagem.getId() == null) 
			return ResponseEntity.notFound().build();
		return postagemRepository.findById(postagem.getId())
				.map(mensagem -> ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem)))
				.orElse(ResponseEntity.notFound().build());
	}*/
	
	
	@PutMapping
	public ResponseEntity<Postagem> putPostagemm (@Valid @RequestBody Postagem postagem){
			if (postagemRepository.existsById(postagem.getId())){
				if (temaRepository.existsById(postagem.getTema().getId()))
					return ResponseEntity.status(HttpStatus.OK)
							.body(postagemRepository.save(postagem));
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}			
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
