package com.clubes.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.clubes.modelo.Club;

/**
 * Además de los métodos heredados, consultas por los campos de referencia.
 * Como @DocumentReference guarda solo el id, se busca por ese número:
 * { 'entrenador': 3 } o { 'jugadores': 7 } (Mongo busca dentro del arreglo).
 */
public interface club_repositorio extends MongoRepository<Club, Long> {

	List<Club> findAllByOrderByNombreAsc();

	/** Clubes que tienen a este entrenador (máximo debería haber uno). */
	@Query("{ 'entrenador': ?0 }")
	List<Club> buscarPorEntrenador(Long entrenadorId);

	/** Clubes que tienen a este jugador en su plantel (máximo debería haber uno). */
	@Query("{ 'jugadores': ?0 }")
	List<Club> buscarPorJugador(Long jugadorId);

	/** Cuántos clubes pertenecen a esta asociación. */
	@Query(value = "{ 'asociacion': ?0 }", count = true)
	long contarPorAsociacion(Long asociacionId);

	/** Cuántos clubes están inscritos en esta competición. */
	@Query(value = "{ 'competiciones': ?0 }", count = true)
	long contarPorCompeticion(Long competicionId);
}
