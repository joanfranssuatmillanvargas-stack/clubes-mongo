package com.clubes.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Jugador: pertenece a un solo club. El club guarda la lista
 * de jugadores (relación @OneToMany Club → Jugador).
 */
@Document(collection = "jugadores")
public class Jugador implements Identificable {

	@Id
	private Long id;
	private String nombre;
	private String apellido;
	private int numero;
	private String posicion;

	public Jugador() {
	}

	public Jugador(String nombre, String apellido, int numero, String posicion) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.numero = numero;
		this.posicion = posicion;
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

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public String nombreCompleto() {
		return nombre + " " + apellido;
	}
}
