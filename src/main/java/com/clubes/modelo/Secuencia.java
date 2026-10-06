package com.clubes.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Contador de ids: un documento por colección.
 * En Atlas: { "_id": "clubes", "valor": 4 }
 */
@Document(collection = "secuencias")
public class Secuencia {

	@Id
	private String id;     // nombre de la colección
	private long valor;    // último id entregado

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getValor() {
		return valor;
	}

	public void setValor(long valor) {
		this.valor = valor;
	}
}
