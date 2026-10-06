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

import com.clubes.modelo.Competicion;
import com.clubes.repositorio.competicion_repositorio;
import com.clubes.repositorio.relaciones_repositorio;

/**
 * API REST de competiciones.
 * Base: http://localhost:8080/api/competiciones
 */
@RestController
@RequestMapping("/api/competiciones")
public class competicion_apirest {

	private final competicion_repositorio repositorio;
	private final relaciones_repositorio relaciones;

	public competicion_apirest(competicion_repositorio repositorio, relaciones_repositorio relaciones) {
		this.repositorio = repositorio;
		this.relaciones = relaciones;
	}

	/** GET /api/competiciones -> 200 con la lista. */
	@GetMapping
	public List<Competicion> listar() {
		return repositorio.findAllByOrderByFechaInicioAsc();
	}

	/** GET /api/competiciones/{id} -> 200 o 404. */
	@GetMapping("/{id}")
	public Competicion buscar(@PathVariable("id") Long id) {
		return relaciones.competicion(id);
	}

	/** POST /api/competiciones -> 201 Created (400 si faltan datos). */
	@PostMapping
	public ResponseEntity<Competicion> crear(@RequestBody Competicion datos) {
		Competicion guardado = relaciones.guardarCompeticion(null, datos);
		return ResponseEntity.created(URI.create("/api/competiciones/" + guardado.getId())).body(guardado);
	}

	/** PUT /api/competiciones/{id} -> 200, 400 o 404. */
	@PutMapping("/{id}")
	public Competicion actualizar(@PathVariable("id") Long id, @RequestBody Competicion datos) {
		return relaciones.guardarCompeticion(id, datos);
	}

	/** DELETE /api/competiciones/{id} -> 204, 404 o 409 si tiene clubes inscritos. */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
		relaciones.eliminarCompeticion(id);
		return ResponseEntity.noContent().build();
	}
}
