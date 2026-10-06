package com.clubes.controlador;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clubes.modelo.Club;
import com.clubes.modelo.ClubFormulario;
import com.clubes.repositorio.club_repositorio;
import com.clubes.repositorio.relaciones_repositorio;

/**
 * API REST de clubes: CRUD + endpoints para manejar cada relación.
 * Base: http://localhost:8080/api/clubes
 */
@RestController
@RequestMapping("/api/clubes")
public class club_apirest {

	private final club_repositorio repositorio;
	private final relaciones_repositorio relaciones;

	public club_apirest(club_repositorio repositorio, relaciones_repositorio relaciones) {
		this.repositorio = repositorio;
		this.relaciones = relaciones;
	}

	// ---------------- CRUD ----------------

	/** GET /api/clubes -> 200 con todos los clubes y sus relaciones. */
	@GetMapping
	public List<Club> listar() {
		return repositorio.findAllByOrderByNombreAsc();
	}

	/** GET /api/clubes/{id} -> 200 o 404. */
	@GetMapping("/{id}")
	public Club buscar(@PathVariable("id") Long id) {
		return relaciones.club(id);
	}

	/** POST /api/clubes -> 201 Created. Body: ClubFormulario (los id de las relaciones son opcionales). */
	@PostMapping
	public ResponseEntity<Club> crear(@RequestBody ClubFormulario datos) {
		Club club = relaciones.guardarClub(null, datos);
		return ResponseEntity.created(URI.create("/api/clubes/" + club.getId())).body(club);
	}

	/** PUT /api/clubes/{id} -> 200, 404 o 409. Reemplaza nombre y todas las relaciones. */
	@PutMapping("/{id}")
	public Club actualizar(@PathVariable("id") Long id, @RequestBody ClubFormulario datos) {
		return relaciones.guardarClub(id, datos);
	}

	/** DELETE /api/clubes/{id} -> 204. Borra también sus jugadores (cascada). */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
		relaciones.eliminarClub(id);
		return ResponseEntity.noContent().build();
	}

	// ---------------- @OneToOne: entrenador ----------------

	/** PUT /api/clubes/{id}/entrenador/{entrenadorId} -> 200, 404 o 409 si ya dirige otro club. */
	@PutMapping("/{id}/entrenador/{entrenadorId}")
	public Club asignarEntrenador(@PathVariable("id") Long id, @PathVariable("entrenadorId") Long entrenadorId) {
		return relaciones.asignarEntrenador(id, entrenadorId);
	}

	/** DELETE /api/clubes/{id}/entrenador -> 200 (el club queda sin entrenador). */
	@DeleteMapping("/{id}/entrenador")
	public Club quitarEntrenador(@PathVariable("id") Long id) {
		return relaciones.quitarEntrenador(id);
	}

	// ---------------- @OneToMany: jugadores ----------------

	/** POST /api/clubes/{id}/jugadores/{jugadorId} -> 200, 404 o 409 si juega en otro club. */
	@PostMapping("/{id}/jugadores/{jugadorId}")
	public Club agregarJugador(@PathVariable("id") Long id, @PathVariable("jugadorId") Long jugadorId) {
		return relaciones.agregarJugador(id, jugadorId);
	}

	/** DELETE /api/clubes/{id}/jugadores/{jugadorId} -> 200 (el jugador queda libre, no se borra). */
	@DeleteMapping("/{id}/jugadores/{jugadorId}")
	public Club quitarJugador(@PathVariable("id") Long id, @PathVariable("jugadorId") Long jugadorId) {
		return relaciones.quitarJugador(id, jugadorId);
	}

	// ---------------- @ManyToOne: asociación ----------------

	/** PUT /api/clubes/{id}/asociacion/{asociacionId} -> 200 o 404. */
	@PutMapping("/{id}/asociacion/{asociacionId}")
	public Club asignarAsociacion(@PathVariable("id") Long id, @PathVariable("asociacionId") Long asociacionId) {
		return relaciones.asignarAsociacion(id, asociacionId);
	}

	/** DELETE /api/clubes/{id}/asociacion -> 200 (el club queda sin asociación). */
	@DeleteMapping("/{id}/asociacion")
	public Club quitarAsociacion(@PathVariable("id") Long id) {
		return relaciones.quitarAsociacion(id);
	}

	// ---------------- @ManyToMany: competiciones ----------------

	/** POST /api/clubes/{id}/competiciones/{competicionId} -> 200, 404 o 409 si ya está inscrito. */
	@PostMapping("/{id}/competiciones/{competicionId}")
	public Club inscribir(@PathVariable("id") Long id, @PathVariable("competicionId") Long competicionId) {
		return relaciones.inscribirCompeticion(id, competicionId);
	}

	/** DELETE /api/clubes/{id}/competiciones/{competicionId} -> 200. */
	@DeleteMapping("/{id}/competiciones/{competicionId}")
	public Club retirar(@PathVariable("id") Long id, @PathVariable("competicionId") Long competicionId) {
		return relaciones.retirarCompeticion(id, competicionId);
	}
}
