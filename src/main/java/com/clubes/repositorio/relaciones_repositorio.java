package com.clubes.repositorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.clubes.modelo.Asociacion;
import com.clubes.modelo.Club;
import com.clubes.modelo.ClubFormulario;
import com.clubes.modelo.Competicion;
import com.clubes.modelo.Entrenador;
import com.clubes.modelo.Jugador;

/**
 * Repositorio de las relaciones: usa los 5 repositorios para guardar y borrar
 * respetando las reglas que en SQL harían las llaves foráneas
 * (MongoDB no tiene llaves foráneas, por eso se programan aquí):
 *  - @OneToOne:  un entrenador solo puede estar en un club.
 *  - @OneToMany: un jugador solo puede estar en un club.
 *  - ON DELETE CASCADE: al borrar un club se borran sus jugadores.
 *  - ON DELETE NO ACTION: no se borra un entrenador, asociación o
 *    competición que un club esté usando (error 409).
 * Lo usan la API REST y la parte web, por eso está en un solo lugar.
 * Los errores se lanzan como ResponseStatusException (clase de Spring) con su código HTTP.
 */
@Repository
public class relaciones_repositorio {

	private final club_repositorio clubes;
	private final entrenador_repositorio entrenadores;
	private final jugador_repositorio jugadores;
	private final asociacion_repositorio asociaciones;
	private final competicion_repositorio competiciones;

	public relaciones_repositorio(club_repositorio clubes, entrenador_repositorio entrenadores,
			jugador_repositorio jugadores, asociacion_repositorio asociaciones,
			competicion_repositorio competiciones) {
		this.clubes = clubes;
		this.entrenadores = entrenadores;
		this.jugadores = jugadores;
		this.asociaciones = asociaciones;
		this.competiciones = competiciones;
	}

	// ================= Buscar por id (404 si no existe) =================

	public Club club(Long id) {
		return clubes.findById(id)
				.orElseThrow(() -> noEncontrado("No existe un club con id " + id));
	}

	public Entrenador entrenador(Long id) {
		return entrenadores.findById(id)
				.orElseThrow(() -> noEncontrado("No existe un entrenador con id " + id));
	}

	public Jugador jugador(Long id) {
		return jugadores.findById(id)
				.orElseThrow(() -> noEncontrado("No existe un jugador con id " + id));
	}

	public Asociacion asociacion(Long id) {
		return asociaciones.findById(id)
				.orElseThrow(() -> noEncontrado("No existe una asociación con id " + id));
	}

	public Competicion competicion(Long id) {
		return competiciones.findById(id)
				.orElseThrow(() -> noEncontrado("No existe una competición con id " + id));
	}

	// ================= Club con todas sus relaciones =================

	/** Crea (id null) o actualiza un club con las relaciones que vienen en el formulario/JSON. */
	public Club guardarClub(Long id, ClubFormulario datos) {
		obligatorio(datos.getNombre(), "el nombre del club");

		Club club = (id == null) ? new Club() : club(id);
		club.setNombre(datos.getNombre().trim());

		// @OneToOne
		club.setEntrenador(datos.getEntrenadorId() == null ? null
				: entrenadorLibre(datos.getEntrenadorId(), club.getId()));

		// @ManyToOne
		club.setAsociacion(datos.getAsociacionId() == null ? null
				: asociacion(datos.getAsociacionId()));

		// @OneToMany
		List<Jugador> plantel = new ArrayList<>();
		for (Long jugadorId : datos.getJugadoresIds()) {
			plantel.add(jugadorLibre(jugadorId, club.getId()));
		}
		club.setJugadores(plantel);

		// @ManyToMany
		List<Competicion> torneos = new ArrayList<>();
		for (Long competicionId : datos.getCompeticionesIds()) {
			torneos.add(competicion(competicionId));
		}
		club.setCompeticiones(torneos);

		return clubes.save(club);
	}

	/** @OneToOne: pone o cambia el entrenador del club. */
	public Club asignarEntrenador(Long clubId, Long entrenadorId) {
		Club club = club(clubId);
		club.setEntrenador(entrenadorLibre(entrenadorId, clubId));
		return clubes.save(club);
	}

	public Club quitarEntrenador(Long clubId) {
		Club club = club(clubId);
		club.setEntrenador(null);
		return clubes.save(club);
	}

	/** @OneToMany: agrega un jugador al plantel. */
	public Club agregarJugador(Long clubId, Long jugadorId) {
		Club club = club(clubId);
		Jugador jugador = jugadorLibre(jugadorId, clubId);
		List<Jugador> plantel = new ArrayList<>(club.getJugadores());
		if (plantel.stream().noneMatch(j -> j.getId().equals(jugadorId))) {
			plantel.add(jugador);
		}
		club.setJugadores(plantel);
		return clubes.save(club);
	}

	public Club quitarJugador(Long clubId, Long jugadorId) {
		Club club = club(clubId);
		jugador(jugadorId);
		List<Jugador> plantel = new ArrayList<>(club.getJugadores());
		plantel.removeIf(j -> j.getId().equals(jugadorId));
		club.setJugadores(plantel);
		return clubes.save(club);
	}

	/** @ManyToOne: pone o cambia la asociación del club. */
	public Club asignarAsociacion(Long clubId, Long asociacionId) {
		Club club = club(clubId);
		club.setAsociacion(asociacion(asociacionId));
		return clubes.save(club);
	}

	public Club quitarAsociacion(Long clubId) {
		Club club = club(clubId);
		club.setAsociacion(null);
		return clubes.save(club);
	}

	/** @ManyToMany: inscribe el club en una competición. */
	public Club inscribirCompeticion(Long clubId, Long competicionId) {
		Club club = club(clubId);
		Competicion competicion = competicion(competicionId);
		List<Competicion> torneos = new ArrayList<>(club.getCompeticiones());
		if (torneos.stream().anyMatch(c -> c.getId().equals(competicionId))) {
			throw conflicto(club.getNombre() + " ya está inscrito en " + competicion.getNombre());
		}
		torneos.add(competicion);
		club.setCompeticiones(torneos);
		return clubes.save(club);
	}

	public Club retirarCompeticion(Long clubId, Long competicionId) {
		Club club = club(clubId);
		competicion(competicionId);
		List<Competicion> torneos = new ArrayList<>(club.getCompeticiones());
		torneos.removeIf(c -> c.getId().equals(competicionId));
		club.setCompeticiones(torneos);
		return clubes.save(club);
	}

	/** ON DELETE CASCADE: borra el club y también los jugadores de su plantel. */
	public void eliminarClub(Long id) {
		Club club = club(id);
		List<Long> idsJugadores = club.getJugadores().stream().map(Jugador::getId).toList();
		jugadores.deleteAllById(idsJugadores);
		clubes.delete(club);
	}

	// ================= Entrenador =================

	public Entrenador guardarEntrenador(Long id, Entrenador datos) {
		obligatorio(datos.getNombre(), "el nombre del entrenador");
		obligatorio(datos.getApellido(), "el apellido del entrenador");
		if (datos.getEdad() < 18 || datos.getEdad() > 100) {
			throw datoInvalido("La edad del entrenador debe estar entre 18 y 100 años");
		}
		if (id != null) {
			entrenador(id);
		}
		datos.setId(id);
		return entrenadores.save(datos);
	}

	/** ON DELETE NO ACTION: no se puede borrar si dirige un club. */
	public void eliminarEntrenador(Long id) {
		Entrenador entrenador = entrenador(id);
		List<Club> usados = clubes.buscarPorEntrenador(id);
		if (!usados.isEmpty()) {
			throw conflicto("No se puede eliminar a " + entrenador.nombreCompleto()
					+ ": es el entrenador de " + usados.get(0).getNombre()
					+ ". Primero quítalo del club o asigna otro entrenador.");
		}
		entrenadores.delete(entrenador);
	}

	// ================= Jugador =================

	public Jugador guardarJugador(Long id, Jugador datos) {
		obligatorio(datos.getNombre(), "el nombre del jugador");
		obligatorio(datos.getApellido(), "el apellido del jugador");
		obligatorio(datos.getPosicion(), "la posición del jugador");
		if (datos.getNumero() < 1 || datos.getNumero() > 99) {
			throw datoInvalido("El número de camiseta debe estar entre 1 y 99");
		}
		if (id != null) {
			jugador(id);
		}
		datos.setId(id);
		return jugadores.save(datos);
	}

	/** Si el jugador está en un club, primero se saca del plantel y luego se borra. */
	public void eliminarJugador(Long id) {
		Jugador jugador = jugador(id);
		for (Club club : clubes.buscarPorJugador(id)) {
			List<Jugador> plantel = new ArrayList<>(club.getJugadores());
			plantel.removeIf(j -> j.getId().equals(id));
			club.setJugadores(plantel);
			clubes.save(club);
		}
		jugadores.delete(jugador);
	}

	// ================= Asociación =================

	public Asociacion guardarAsociacion(Long id, Asociacion datos) {
		obligatorio(datos.getNombre(), "el nombre de la asociación");
		obligatorio(datos.getSiglas(), "las siglas de la asociación");
		if (id != null) {
			asociacion(id);
		}
		datos.setId(id);
		datos.setSiglas(datos.getSiglas().trim().toUpperCase());
		return asociaciones.save(datos);
	}

	/** ON DELETE NO ACTION: no se puede borrar si tiene clubes afiliados. */
	public void eliminarAsociacion(Long id) {
		Asociacion asociacion = asociacion(id);
		long afiliados = clubes.contarPorAsociacion(id);
		if (afiliados > 0) {
			throw conflicto("No se puede eliminar " + asociacion.getSiglas() + ": tiene "
					+ afiliados + " club(es) afiliado(s). Primero cambia o quita la asociación de esos clubes.");
		}
		asociaciones.delete(asociacion);
	}

	// ================= Competición =================

	public Competicion guardarCompeticion(Long id, Competicion datos) {
		obligatorio(datos.getNombre(), "el nombre de la competición");
		if (datos.getMontoPremio() < 0) {
			throw datoInvalido("El monto del premio no puede ser negativo");
		}
		if (datos.getFechaInicio() == null || datos.getFechaFin() == null) {
			throw datoInvalido("Escribe la fecha de inicio y la fecha de fin");
		}
		if (datos.getFechaFin().isBefore(datos.getFechaInicio())) {
			throw datoInvalido("La fecha de fin no puede ser anterior a la de inicio");
		}
		if (id != null) {
			competicion(id);
		}
		datos.setId(id);
		return competiciones.save(datos);
	}

	/** ON DELETE NO ACTION: no se puede borrar si hay clubes inscritos. */
	public void eliminarCompeticion(Long id) {
		Competicion competicion = competicion(id);
		long inscritos = clubes.contarPorCompeticion(id);
		if (inscritos > 0) {
			throw conflicto("No se puede eliminar " + competicion.getNombre() + ": tiene "
					+ inscritos + " club(es) inscrito(s). Primero retíralos de la competición.");
		}
		competiciones.delete(competicion);
	}

	// ================= Datos para las pantallas =================

	/** Entrenadores sin club, más el del club que se está editando. */
	public List<Entrenador> entrenadoresDisponibles(Long clubId) {
		return entrenadores.findAllByOrderByApellidoAsc().stream()
				.filter(e -> estaLibre(clubes.buscarPorEntrenador(e.getId()), clubId))
				.toList();
	}

	/** Jugadores sin club, más los del club que se está editando. */
	public List<Jugador> jugadoresDisponibles(Long clubId) {
		return jugadores.findAllByOrderByNumeroAsc().stream()
				.filter(j -> estaLibre(clubes.buscarPorJugador(j.getId()), clubId))
				.toList();
	}

	/** id del entrenador -> nombre del club que dirige. */
	public Map<Long, String> clubPorEntrenador() {
		Map<Long, String> mapa = new LinkedHashMap<>();
		for (Club c : clubes.findAll()) {
			if (c.getEntrenador() != null) {
				mapa.put(c.getEntrenador().getId(), c.getNombre());
			}
		}
		return mapa;
	}

	/** id del jugador -> nombre de su club. */
	public Map<Long, String> clubPorJugador() {
		Map<Long, String> mapa = new LinkedHashMap<>();
		for (Club c : clubes.findAll()) {
			for (Jugador j : c.getJugadores()) {
				mapa.put(j.getId(), c.getNombre());
			}
		}
		return mapa;
	}

	// ================= Ayudas =================

	private Entrenador entrenadorLibre(Long entrenadorId, Long clubId) {
		Entrenador entrenador = entrenador(entrenadorId);
		List<Club> usados = clubes.buscarPorEntrenador(entrenadorId);
		if (!estaLibre(usados, clubId)) {
			throw conflicto(entrenador.nombreCompleto() + " ya es el entrenador de "
					+ usados.get(0).getNombre() + ". Un entrenador solo puede dirigir un club (@OneToOne).");
		}
		return entrenador;
	}

	private Jugador jugadorLibre(Long jugadorId, Long clubId) {
		Jugador jugador = jugador(jugadorId);
		List<Club> usados = clubes.buscarPorJugador(jugadorId);
		if (!estaLibre(usados, clubId)) {
			throw conflicto(jugador.nombreCompleto() + " ya juega en "
					+ usados.get(0).getNombre() + ". Un jugador solo puede estar en un club (@OneToMany).");
		}
		return jugador;
	}

	/** Libre = no está en ningún club, o solo está en el club que se está editando. */
	private boolean estaLibre(List<Club> usados, Long clubId) {
		return usados.stream().allMatch(c -> Objects.equals(c.getId(), clubId));
	}

	private void obligatorio(String valor, String campo) {
		if (valor == null || valor.isBlank()) {
			throw datoInvalido("Falta " + campo);
		}
	}

	// ================= Errores con su código HTTP =================

	/** 404: el id no existe. */
	private ResponseStatusException noEncontrado(String mensaje) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, mensaje);
	}

	/** 409: la operación rompe una regla de las relaciones. */
	private ResponseStatusException conflicto(String mensaje) {
		return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
	}

	/** 400: faltan datos o vienen mal. */
	private ResponseStatusException datoInvalido(String mensaje) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
	}
}
