package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;
import com.generation.blogpessoal.repository.UsuarioRepository;

@RestController
@RequestMapping ("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {
	
	@Autowired
	private TemaRepository temaRepository;
	
	@Autowired
	private PostagemRepository postagemRepository;
	
	@Autowired 
	private UsuarioRepository usuarioRepository;
	
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
		Optional<Usuario> postPostagem = usuarioRepository.findById(postagem.getUsuario().getId());
		if(postPostagem.isPresent()) {
			return temaRepository.findById(postagem.getTema().getId())
					.map(resp -> ResponseEntity.ok(postagemRepository.save(postagem)))
					.orElseThrow (() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe", null));
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não existe!", null);
	}
	
	@PutMapping
	public ResponseEntity<Postagem> putPostagem (@Valid @RequestBody Postagem postagem){
		Optional<Usuario> putPostagem = usuarioRepository.findById(postagem.getUsuario().getId());
		if (putPostagem.isPresent() && (postagemRepository.existsById(postagem.getId()))){
			return temaRepository.findById(postagem.getTema().getId())
					.map(resp -> ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem)))
					.orElseThrow (() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe", null));
		}			
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Usuário não existe!", null);
	}

	@DeleteMapping("/{id}") 
	public ResponseEntity<?> deletaPostagem (@PathVariable Long id){
		return postagemRepository.findById(id)
				.map(resposta -> {
					postagemRepository.deleteById(id);
					return ResponseEntity.status(HttpStatus.NO_CONTENT).build();		
				})
				.orElse(ResponseEntity.notFound().build());
	}
}