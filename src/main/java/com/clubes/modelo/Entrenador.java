package com.clubes.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entrenador: no conoce a su club (la relación la guarda Club).
 * Lado "uno" de la relación @OneToOne Club → Entrenador.
 */
@Document(collection = "entrenadores")
public class Entrenador implements Identificable {

	@Id
	private Long id;
	private String nombre;
	private String apellido;
	private int edad;
	private String nacionalidad;

	public Entrenador() {
	}

	public Entrenador(String nombre, String apellido, int edad, String nacionalidad) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.edad = edad;
		this.nacionalidad = nacionalidad;
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	/** "Alberto Gamero" (para mostrar en pantallas y mensajes). */
	public String nombreCompleto() {
		return nombre + " " + apellido;
	}
}
