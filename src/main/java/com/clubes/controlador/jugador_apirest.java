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

import com.clubes.modelo.Jugador;
import com.clubes.repositorio.jugador_repositorio;
import com.clubes.repositorio.relaciones_repositorio;

/**
 * API REST de jugadores.
 * Base: http://localhost:8080/api/jugadores
 */
@RestController
@RequestMapping("/api/jugadores")
public class jugador_apirest {

	private final jugador_repositorio repositorio;
	private final relaciones_repositorio relaciones;

	public jugador_apirest(jugador_repositorio repositorio, relaciones_repositorio relaciones) {
		this.repositorio = repositorio;
		this.relaciones = relaciones;
	}

	/** GET /api/jugadores -> 200 con la lista. */
	@GetMapping
	public List<Jugador> listar() {
		return repositorio.findAllByOrderByNumeroAsc();
	}

	/** GET /api/jugadores/{id} -> 200 o 404. */
	@GetMapping("/{id}")
	public Jugador buscar(@PathVariable("id") Long id) {
		return relaciones.jugador(id);
	}

	/** POST /api/jugadores -> 201 Created (400 si faltan datos). */
	@PostMapping
	public ResponseEntity<Jugador> crear(@RequestBody Jugador datos) {
		Jugador guardado = relaciones.guardarJugador(null, datos);
		return ResponseEntity.created(URI.create("/api/jugadores/" + guardado.getId())).body(guardado);
	}

	/** PUT /api/jugadores/{id} -> 200, 400 o 404. */
	@PutMapping("/{id}")
	public Jugador actualizar(@PathVariable("id") Long id, @RequestBody Jugador datos) {
		return relaciones.guardarJugador(id, datos);
	}

	/** DELETE /api/jugadores/{id} -> 204, 404 (si estaba en un club, primero se saca del plantel). */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
		relaciones.eliminarJugador(id);
		return ResponseEntity.noContent().build();
	}
}
