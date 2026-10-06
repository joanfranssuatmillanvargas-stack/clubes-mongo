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

import com.clubes.modelo.Asociacion;
import com.clubes.repositorio.asociacion_repositorio;
import com.clubes.repositorio.relaciones_repositorio;

/**
 * API REST de asociaciones.
 * Base: http://localhost:8080/api/asociaciones
 */
@RestController
@RequestMapping("/api/asociaciones")
public class asociacion_apirest {

	private final asociacion_repositorio repositorio;
	private final relaciones_repositorio relaciones;

	public asociacion_apirest(asociacion_repositorio repositorio, relaciones_repositorio relaciones) {
		this.repositorio = repositorio;
		this.relaciones = relaciones;
	}

	/** GET /api/asociaciones -> 200 con la lista. */
	@GetMapping
	public List<Asociacion> listar() {
		return repositorio.findAllByOrderByNombreAsc();
	}

	/** GET /api/asociaciones/{id} -> 200 o 404. */
	@GetMapping("/{id}")
	public Asociacion buscar(@PathVariable("id") Long id) {
		return relaciones.asociacion(id);
	}

	/** POST /api/asociaciones -> 201 Created (400 si faltan datos). */
	@PostMapping
	public ResponseEntity<Asociacion> crear(@RequestBody Asociacion datos) {
		Asociacion guardado = relaciones.guardarAsociacion(null, datos);
		return ResponseEntity.created(URI.create("/api/asociaciones/" + guardado.getId())).body(guardado);
	}

	/** PUT /api/asociaciones/{id} -> 200, 400 o 404. */
	@PutMapping("/{id}")
	public Asociacion actualizar(@PathVariable("id") Long id, @RequestBody Asociacion datos) {
		return relaciones.guardarAsociacion(id, datos);
	}

	/** DELETE /api/asociaciones/{id} -> 204, 404 o 409 si tiene clubes afiliados. */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
		relaciones.eliminarAsociacion(id);
		return ResponseEntity.noContent().build();
	}
}
