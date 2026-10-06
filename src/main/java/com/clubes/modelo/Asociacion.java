package com.clubes.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Asociación o federación (ej. FCF). Muchos clubes apuntan a la misma
 * asociación: relación @ManyToOne Club → Asociacion.
 */
@Document(collection = "asociaciones")
public class Asociacion implements Identificable {

	@Id
	private Long id;
	private String nombre;
	private String siglas;
	private String pais;
	private String presidente;

	public Asociacion() {
	}

	public Asociacion(String nombre, String siglas, String pais, String presidente) {
		this.nombre = nombre;
		this.siglas = siglas;
		this.pais = pais;
		this.presidente = presidente;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getSiglas() {
		return siglas;
	}

	public void setSiglas(String siglas) {
		this.siglas = siglas;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getPresidente() {
		return presidente;
	}

	public void setPresidente(String presidente) {
		this.presidente = presidente;
	}
}
