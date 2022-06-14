package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) //Indica que é uma classe de teste e diz para pegar uma automáticamente
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Vida do teste é pela classe
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {

	@Autowired //Criar requisições - Basicamente faz a mesma coisa que o insomnia, ou seja ele cria as requisições que nem o insomnia
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;

    @Autowired
	private UsuarioRepository usuarioRepository;
	
    @BeforeAll //Vai limpar nosso banco de dados | O recomendado é criarmos os objetos dentro do teste
	void start(){
		usuarioRepository.deleteAll();
	}

    
	@Test
	@Order(1) //Aqui definimos a ordem dos testes.
	@DisplayName("Cadastrar um usuário")
	public void deveCriarUmUsuario() {

		//Estamos criando um objeto Http com o objeto Usuario dentro dele
		//Esse seria a requisição com o corpo Json igual o insomnia
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L, 
			"Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

		//Estamos Criando as configurações da requisição
		// Endereço do endpoint, Verbo, o corpo da requisição e a resposta esperada.
		ResponseEntity<Usuario> resposta = testRestTemplate
			// Esse método tem 4 parâmetros:
				//EndPoint | o Verbo | o Corpo da requisição (body) | o que espero que ele retorne. no caso é a classe usuário
			.exchange("/usuarios/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		//Aqui estamos fazendo 3 verificações
		// O Status da requisição
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		//Pegamos o corpo do envio e comparamos a resposta do corpo
		assertEquals(requisicao.getBody().getNome(), resposta.getBody().getNome());
		//Pegamos o usuário do envio e comparamos com o usuario do corpo da resposta
		assertEquals(requisicao.getBody().getUsuario(), resposta.getBody().getUsuario());
	}
	
	@Test
	@Order(2)
	@DisplayName("Não deve permitir duplicação do usuário")
	public void naoDeveDuplicarUsuario() {

		//Aqui estamos inserindo o usuario da maria manualmente
		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

		//Aqui estamos inserindo via requisição
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

		ResponseEntity<Usuario> resposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, requisicao, Usuario.class);

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
	}
	
	@Test
	@Order(3)
	@DisplayName("Alterar um usuário")
	public void deveAtualizarUmUsuario() {

		Optional<Usuario> usuarioCreate = 
			usuarioService.cadastrarUsuario(
					new Usuario(0L,"Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "https://i.imgur.com/yDRVeK7.jpg"));

		Usuario usuarioUpdate = new Usuario(usuarioCreate.get().getId(), 
			"Juliana Andrews Ramos", "juliana_ramos@email.com.br", 
			"juliana123", "https://i.imgur.com/yDRVeK7.jpg");
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);

		ResponseEntity<Usuario> resposta = testRestTemplate
			.withBasicAuth("root", "root")
			.exchange("/usuarios/atualizar", HttpMethod.PUT, requisicao, Usuario.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals(usuarioUpdate.getNome(), resposta.getBody().getNome());
		assertEquals(usuarioUpdate.getUsuario(), resposta.getBody().getUsuario());
	}
	
	@Test
	@Order(4)
	@DisplayName("Listar todos os usuários")
	public void deveMostrarTodosUsuarios() {

		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Sabrina Sanches", "sabrina_sanches@email.com.br", 
			"sabrina123", "https://i.imgur.com/5M2p5Wb.jpg"));
		
		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Ricardo Marques", "ricardo_marques@email.com.br", 
			"ricardo123", "https://i.imgur.com/Sk5SjWE.jpg"));

		ResponseEntity<String> resposta = testRestTemplate
			.withBasicAuth("root", "root")
			//Como é um get não existe corpo da mensagem por isso o 3 parâmetro é null
			//Retorno esperado é String pois ele retorna um Json de strings
			.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@Order(5)
	@DisplayName("Deve achar usuário por ID")
	public void deveMostrarUsuarioPorId() {
		
		//Criando Usuário dentro do DB
		Optional<Usuario> user = usuarioService.cadastrarUsuario
			(new Usuario (0L, "Roberto Marinho", "roberto_mari@email.com", "roberto123", null));
		
		//Verificar por que funciona com tipo String e tipo Usuario...
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuarios/" + user.get().getId(), HttpMethod.GET, null, String.class);
				
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@Order(6)
	@DisplayName("Deve não encontrar usuario por ID")
	public void NaoDeveMostrarUsuarioPorId() {
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuarios/5", HttpMethod.GET, null, String.class);
				
		assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
	}
	
	@Test
	@Order(7)
	@DisplayName("Deve logar")
	public void deveLogar() {
		
		//Criar o objeto usuário no DB
		usuarioService.cadastrarUsuario(new Usuario (0L, "Teste Logar", "logando@email.com", "logando123", null));
		
		//Criar o corpo da requisição | Criado Construtor na Model UsuarioLogin para utilizarmos aqui
		//O HttpEntity cria o corpo da requisição
		HttpEntity<UsuarioLogin> corpoLogar = 
				new HttpEntity<UsuarioLogin> (new UsuarioLogin(0L, "", "logando@email.com", "logando123", "", ""));
	
		ResponseEntity<UsuarioLogin> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuarios/logar", HttpMethod.POST, corpoLogar, UsuarioLogin.class);
				
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@Order(8)
	@DisplayName("Não deve logar")
	public void naoDeveLogar() {
		
		//Criar o objeto usuário no DB
		usuarioService.cadastrarUsuario(new Usuario (0L, "Teste Logar", "logando@email.com", "logando123", null));
		
		//Criar o corpo da requisição | Criado Construtor na Model UsuarioLogin para utilizarmos aqui
		//O HttpEntity cria o corpo da requisição
		HttpEntity<UsuarioLogin> corpoLogar = 
				new HttpEntity<UsuarioLogin> (new UsuarioLogin(0L, "", "logando_outro@email.com", "logando123", "", ""));
	
		ResponseEntity<UsuarioLogin> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuarios/logar", HttpMethod.POST, corpoLogar, UsuarioLogin.class);
				
		assertEquals(HttpStatus.UNAUTHORIZED, resposta.getStatusCode());
	}
	
}