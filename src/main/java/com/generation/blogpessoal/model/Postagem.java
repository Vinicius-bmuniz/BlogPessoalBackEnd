package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tb_postagens")	// Se não utilizarmos essa anotação, ele vai gerar o a table no MySQL com o nome
								// da classe | Postagens
public class Postagem {
	
	@Id // aqui diz que é uma chave primária
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-Increment no banco de dados 
	private Long id;
	
	
	/*@NotBlank | Só funciona com string | diz que não podemos deixar em branco e retorna uma mensagem | Obrigatório
	 *@Size | Define o tamanho minimo e tamanho máximo do campo do tipo String, Colocando o max definimos o tamanho máximo da coluna do BD*/
	@NotBlank(message = "O atributo Título é obrigatório e não pode utilizar utilizar espaço em branco!!")
	@Size(min = 5, max = 100, message = "O atributo título deve conter no mínimo 5 e no máximo 100 caracteres")
	private String titulo;
	
	
	@NotNull(message = "O atributo Texto é obrigatório e não pode utilizar utilizar espaço em branco!!") //Só funciona com string | podemos deixar em branco e retorna uma mensagem
	@Size(min = 10, max = 1000, message = "O atributo Texto deve conter no mínimo 10 e no máximo 1000 caracteres") //Define o tamanho minimo e tamanho máximo do campo do tipo String | Colocando o max definimos o tamanho máximo da coluna do BD
	private String texto;
	
	@UpdateTimestamp //Muda a data toda vez que editamos a postagem | para marcar apenas a data de postagem devemos usar o CreateTimeTamp | Utiliza a data e hora do sistema
	private LocalDateTime data;

	
	@ManyToOne
	@JsonIgnoreProperties("postagem")
	private Tema tema;
	
	
	// ====== Getters and Setters ===== //
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
	// ===== Getters and Setters Tema ===== //
	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

}
