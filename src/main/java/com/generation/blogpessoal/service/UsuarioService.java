package com.generation.blogpessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();
		
		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		return Optional.of(usuarioRepository.save(usuario));
	}
	
	
		/* Método de atualizar fazendo a verificação se o usuário existe
		 * e se o usuario existe e for o mesmo que está fazendo a solicitação
		 * Vai poder mudar algum dado
		 * 
		 * Primeiro verificando se o usuarioID existe no nosso banco de dados
		 * usuarioRepository.findById(usuario.getId()).isPresent()
		 * usuarioRepository.findById | Busca no banco de dados
		 * (usuario.getId()) | pega o id que foi enviado na nossa requisição (lembrando que o método put precisa ter o id no corpo)
		 * .isPresent() | Verifica se aquele id é true ou false
		 * 
		 * Se True ele cria uma nova váriavel para podermos verificar se o USUARIO (e-mail) do corpo da requisição é o mesmo que está no banco de dados
		 * Optional <Usuario> checarUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
		 * Optional <Usuario> | Temos que ter uma variavel do tipo Optional para tratar nulos
		 * checarUsuario | é o nome da nossa váriavel
		 * usuarioRepositoryfindByUsuario 	| Usamos o método que criamos na classe repository que busca por usuários (e-mail)
		 * 									| Para verificarmos se já existe algum e-mail igual ao que estamos enviando no corpo da requisição
		 * 									| Dentro do nosso banco de dados, se true existe um e-mail igual, se false não existe e podemos prosseguir.
		 * (usuario.getUsuario()); |Dentro do parâmetro estamos passando o e-mail que o usuário DIGITOU no corpo da requisição
		 */
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		if(usuarioRepository.findById(usuario.getId()).isPresent()) {
				//Nessa VARIAVEL de OPTIONAL estamos verificando se o usuário(email) existe no nosso DB e retorna um boolean
			Optional <Usuario> checarUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
	//Optional <Usuario> checarId = usuarioRepository.findById(usuario.getId());
				//Após recebermos o boolean, verificamos se essa VARIAVEL checarUsuario é true E checamos TAMBÉM se o ID do usuário já existe no DB
				//Se após as duas verificações, retornar um TRUE, retornamos um Optional Vazio, para indicar que não foi autorizado.
			
			/* Checamos se o EMAIL existe Se TRUE Continua a segunda verificação
			 * Se segunda verificação for TRUE ele retorna um Optional vazio, ou seja não foi autorizado.
			 * 
			 * checarUsuario.isPresent() | Checa se o EMAIL que o usuario DIGITOU no CORPO requisição existe no DB
			 * checarUsuario.get().getId()	| Checa se o ID do usuário que DIGITOU no CORPO da requisição é DIFERENTE do
			 * 								| ID da tabela tb_usuarios, pois se for diferente quer dizer que não podemos alterar
			 * 								| O ID DA REQUISIÇÃO TEM QUE SER O MESMO DO ID QUE ESTAMOS ALTERANDO
			 * 								| Isso para não deixarmos outros usuários alterarmos o cadastro de terceiros
			 */

			if((checarUsuario.isPresent()) && (checarUsuario.get().getId() != usuario.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
					//Verificar com o Professor como declarar um retorno diferente.
					//Para podermos separar as respostas e não ser duas respostas iguais para erros diferentes
			
				/* Se nas duas verificações acima derem FALSE, saira do laço e executara os comandos abaixo
				 * usuario.setSenha | Primeiro iremos chamar o método setSenha da classe model Usuario
				 * (criptografarSenha(usuario.getSenha())) | Utilizaremos o método PRIVADO criado nessa Service para criptografar a senha novamente
				 * usuario.getSenha() | E no parâmetro iremos puxar a NOVA senha que o usuário DIGITOU no corpo da requisição
				 * Assim estaremos salvando a nova senha já criptografada no objeto usuario.
				 */
			usuario.setSenha(criptografarSenha(usuario.getSenha()));
				/* Após salvar a senha criptografada no objeto, iremos persistir o objeto atualizado no nosso DB 
				 * return Optional.ofNullable(usuarioRepository.save(usuario));
				 */
			return Optional.ofNullable(usuarioRepository.save(usuario));
		}
		return Optional.empty();
	}	
	
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());
		if (usuario.isPresent()) {
			if (compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {
				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setFoto(usuario.get().getFoto());
				usuarioLogin.get().setToken(gerarBasicToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha()));
				usuarioLogin.get().setSenha(usuario.get().getSenha());
				return usuarioLogin;
			}
		}
		return Optional.empty();
	}

	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha);
	}

	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.matches(senhaDigitada, senhaBanco);
	}
	
	private String gerarBasicToken(String usuario, String senha) {
		String token = usuario + ":" + senha;
		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));
		return "Basic " + new String(tokenBase64);
	}
}