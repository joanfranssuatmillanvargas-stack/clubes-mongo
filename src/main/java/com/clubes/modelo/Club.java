package com.clubes.modelo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * Club: es el dueño de todas las relaciones (como en el PowerPoint).
 * Con @DocumentReference, MongoDB guarda solo los id de los documentos
 * relacionados; Spring los busca y arma los objetos al leer el club.
 *
 * Documento en Atlas:
 * { "_id": 1, "nombre": "Millonarios", "entrenador": 1,
 *   "jugadores": [1, 2], "asociacion": 1, "competiciones": [1, 2] }
 */
@Document(collection = "clubes")
public class Club implements Identificable {

	@Id
	private Long id;
	private String nombre;

	/** @OneToOne: un club tiene un entrenador y un entrenador dirige un solo club. */
	@DocumentReference
	private Entrenador entrenador;

	/** @OneToMany: un club tiene muchos jugadores; se cargan solo cuando se usan (LAZY). */
	@DocumentReference(lazy = true)
	private List<Jugador> jugadores = new ArrayList<>();

	/** @ManyToOne: muchos clubes pertenecen a la misma asociación. */
	@DocumentReference
	private Asociacion asociacion;

	/** @ManyToMany: un club juega varias competiciones y cada una tiene varios clubes (LAZY). */
	@DocumentReference(lazy = true)
	private List<Competicion> competiciones = new ArrayList<>();

	public Club() {
	}

	public Club(String nombre) {
		this.nombre = nombre;
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

	public Entrenador getEntrenador() {
		return entrenador;
	}

	public void setEntrenador(Entrenador entrenador) {
		this.entrenador = entrenador;
	}

	public List<Jugador> getJugadores() {
		if (jugadores == null) {
			jugadores = new ArrayList<>();
		}
		return jugadores;
	}

	public void setJugadores(List<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public Asociacion getAsociacion() {
		return asociacion;
	}

	public void setAsociacion(Asociacion asociacion) {
		this.asociacion = asociacion;
	}

	public List<Competicion> getCompeticiones() {
		if (competiciones == null) {
			competiciones = new ArrayList<>();
		}
		return competiciones;
	}

	public void setCompeticiones(List<Competicion> competiciones) {
		this.competiciones = competiciones;
	}
}
