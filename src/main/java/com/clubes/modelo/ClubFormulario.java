package com.clubes.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo del formulario del club (no se guarda en Mongo).
 * Trae los datos tal como llegan del formulario web o del JSON de la API:
 * en vez de objetos completos trae los id de lo que se quiere relacionar.
 *
 * JSON de ejemplo:
 * { "nombre": "Millonarios", "entrenadorId": 1, "asociacionId": 1,
 *   "jugadoresIds": [1, 2], "competicionesIds": [1, 2] }
 */
public class ClubFormulario {

	private Long id;
	private String nombre;
	private Long entrenadorId;
	private Long asociacionId;
	private List<Long> jugadoresIds = new ArrayList<>();
	private List<Long> competicionesIds = new ArrayList<>();

	/** Llena el formulario con los datos de un club existente (para editar). */
	public static ClubFormulario desde(Club club) {
		ClubFormulario f = new ClubFormulario();
		f.setId(club.getId());
		f.setNombre(club.getNombre());
		f.setEntrenadorId(club.getEntrenador() == null ? null : club.getEntrenador().getId());
		f.setAsociacionId(club.getAsociacion() == null ? null : club.getAsociacion().getId());
		for (Jugador j : club.getJugadores()) {
			f.getJugadoresIds().add(j.getId());
		}
		for (Competicion c : club.getCompeticiones()) {
			f.getCompeticionesIds().add(c.getId());
		}
		return f;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getEntrenadorId() {
		return entrenadorId;
	}

	public void setEntrenadorId(Long entrenadorId) {
		this.entrenadorId = entrenadorId;
	}

	public Long getAsociacionId() {
		return asociacionId;
	}

	public void setAsociacionId(Long asociacionId) {
		this.asociacionId = asociacionId;
	}

	public List<Long> getJugadoresIds() {
		if (jugadoresIds == null) {
			jugadoresIds = new ArrayList<>();
		}
		return jugadoresIds;
	}

	public void setJugadoresIds(List<Long> jugadoresIds) {
		this.jugadoresIds = jugadoresIds;
	}

	public List<Long> getCompeticionesIds() {
		if (competicionesIds == null) {
			competicionesIds = new ArrayList<>();
		}
		return competicionesIds;
	}

	public void setCompeticionesIds(List<Long> competicionesIds) {
		this.competicionesIds = competicionesIds;
	}
}
