package com.generation.blogpessoal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.generation.blogpessoal.model.Usuario;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) //Indica que é uma classe de teste e diz para pegar uma automáticamente
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Vida do teste é pela classe
public class UsuarioRepositoryTest {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll //Vai rodar uma vez e vai valer para todos os testes na sequencia
	public void Start () { //Utilizamos esse método para popular nosso DB
		
		usuarioRepository.deleteAll(); //Primeiro limpamos o DB para caso executemos vários testes.

		usuarioRepository.save(new Usuario
				(0L, "João da Silva", "joao@email.com.br", "13465278", "https://i.imgur.com/FETvs2O.jpg"));
		
		usuarioRepository.save(new Usuario(0L, "Manuela da Silva", "manuela@email.com.br", "13465278", 
                                           "https://i.imgur.com/NtyGneo.jpg"));
		
		usuarioRepository.save(new Usuario(0L, "Adriana da Silva", "adriana@email.com.br", "13465278",
                                           "https://i.imgur.com/mB3VM2N.jpg"));

        usuarioRepository.save(new Usuario(0L, "Paulo Antunes", "paulo@email.com.br", "13465278", 
                                           "https://i.imgur.com/JR7kUFU.jpg"));
	
	}
	
	@Test
	@DisplayName ("Deve mostrar um usuário pelo email")
	public void deveRetornarUmUsuario() {
		//Utilizamos o Optional por que ele pode não encontrar nada
		
		Optional<Usuario> usuario = usuarioRepository.findByUsuario("joao@email.com.br");
		//Aqui estamos perguntando se é verdade se ele achou o usuario
		assertTrue(usuario.get().getUsuario().equals("joao@email.com.br"));
	}

	@Test
	@DisplayName ("Não deve mostrar um usuário")
	public void naoDeveRetornarUmUsuario() {
		//Utilizamos o Optional por que ele pode não encontrar nada
		
		Optional<Usuario> usuario = usuarioRepository.findByUsuario("joao@email.com.br");
		//Aqui estamos perguntando se é false se ele achou o usuario
		assertFalse(usuario.get().getUsuario().equals("joao_vitor@email.com.br"));
	}
	
	@Test
	@DisplayName ("Deve Retornar 3 usuários")
	public void deveRetornar3Usuarios() {
		List<Usuario> listaDeUsuarios = usuarioRepository.findAllByNomeContainingIgnoreCase("Silva");
		assertEquals(3, listaDeUsuarios.size());
		assertTrue(listaDeUsuarios.get(0).getNome().equals("João da Silva"));
		assertTrue(listaDeUsuarios.get(1).getNome().equals("Manuela da Silva"));
		assertTrue(listaDeUsuarios.get(2).getNome().equals("Adriana da Silva"));
	}
	
}
