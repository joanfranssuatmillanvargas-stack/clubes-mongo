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

import com.clubes.modelo.Entrenador;
import com.clubes.repositorio.entrenador_repositorio;
import com.clubes.repositorio.relaciones_repositorio;

/**
 * API REST de entrenadores.
 * Base: http://localhost:8080/api/entrenadores
 */
@RestController
@RequestMapping("/api/entrenadores")
public class entrenador_apirest {

	private final entrenador_repositorio repositorio;
	private final relaciones_repositorio relaciones;

	public entrenador_apirest(entrenador_repositorio repositorio, relaciones_repositorio relaciones) {
		this.repositorio = repositorio;
		this.relaciones = relaciones;
	}

	/** GET /api/entrenadores -> 200 con la lista. */
	@GetMapping
	public List<Entrenador> listar() {
		return repositorio.findAllByOrderByApellidoAsc();
	}

	/** GET /api/entrenadores/{id} -> 200 o 404. */
	@GetMapping("/{id}")
	public Entrenador buscar(@PathVariable("id") Long id) {
		return relaciones.entrenador(id);
	}

	/** POST /api/entrenadores -> 201 Created (400 si faltan datos). */
	@PostMapping
	public ResponseEntity<Entrenador> crear(@RequestBody Entrenador datos) {
		Entrenador guardado = relaciones.guardarEntrenador(null, datos);
		return ResponseEntity.created(URI.create("/api/entrenadores/" + guardado.getId())).body(guardado);
	}

	/** PUT /api/entrenadores/{id} -> 200, 400 o 404. */
	@PutMapping("/{id}")
	public Entrenador actualizar(@PathVariable("id") Long id, @RequestBody Entrenador datos) {
		return relaciones.guardarEntrenador(id, datos);
	}

	/** DELETE /api/entrenadores/{id} -> 204, 404 o 409 si dirige un club. */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
		relaciones.eliminarEntrenador(id);
		return ResponseEntity.noContent().build();
	}
}
