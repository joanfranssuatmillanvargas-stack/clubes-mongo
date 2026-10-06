package com.clubes.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.clubes.modelo.Asociacion;
import com.clubes.modelo.ClubFormulario;
import com.clubes.modelo.Competicion;
import com.clubes.modelo.Entrenador;
import com.clubes.modelo.Jugador;
import com.clubes.repositorio.asociacion_repositorio;
import com.clubes.repositorio.club_repositorio;
import com.clubes.repositorio.competicion_repositorio;
import com.clubes.repositorio.entrenador_repositorio;
import com.clubes.repositorio.jugador_repositorio;
import com.clubes.repositorio.relaciones_repositorio;

/**
 * Controlador de las páginas web:
 *  - index.html: formularios para registrar/editar las 5 entidades (una pestaña por entidad).
 *  - listar.html: los clubes con todas sus relaciones.
 */
@Controller
public class club_web {

	private final relaciones_repositorio relaciones;
	private final club_repositorio clubes;
	private final entrenador_repositorio entrenadores;
	private final jugador_repositorio jugadores;
	private final asociacion_repositorio asociaciones;
	private final competicion_repositorio competiciones;

	public club_web(relaciones_repositorio relaciones, club_repositorio clubes,
			entrenador_repositorio entrenadores, jugador_repositorio jugadores,
			asociacion_repositorio asociaciones, competicion_repositorio competiciones) {
		this.relaciones = relaciones;
		this.clubes = clubes;
		this.entrenadores = entrenadores;
		this.jugadores = jugadores;
		this.asociaciones = asociaciones;
		this.competiciones = competiciones;
	}

	// ======================= Páginas =======================

	/** GET /?pestana=club|entrenador|jugador|asociacion|competicion */
	@GetMapping("/")
	public String index(@RequestParam(name = "pestana", defaultValue = "club") String pestana, Model model) {
		return mostrarIndex(model, pestana);
	}

	/** GET /listar : clubes con entrenador, plantel, asociación y competiciones. */
	@GetMapping("/listar")
	public String listar(Model model) {
		model.addAttribute("clubes", clubes.findAllByOrderByNombreAsc());
		return "listar";
	}

	// ======================= Club =======================

	@PostMapping("/clubes/guardar")
	public String guardarClub(@ModelAttribute("clubForm") ClubFormulario form, BindingResult errores, Model model,
			RedirectAttributes flash) {
		if (errores.hasErrors()) {
			model.addAttribute("error", "Revisa los campos: hay un número o una fecha con formato incorrecto.");
			return mostrarIndex(model, "club");
		}
		try {
			var club = relaciones.guardarClub(form.getId(), form);
			flash.addFlashAttribute("ok", "Club " + club.getNombre() + " guardado con sus relaciones");
			return "redirect:/listar";
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
			return mostrarIndex(model, "club");
		}
	}

	@GetMapping("/clubes/{id}/editar")
	public String editarClub(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
		try {
			model.addAttribute("clubForm", ClubFormulario.desde(relaciones.club(id)));
			return mostrarIndex(model, "club");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/listar";
		}
	}

	@PostMapping("/clubes/{id}/eliminar")
	public String eliminarClub(@PathVariable("id") Long id, RedirectAttributes flash) {
		try {
			String nombre = relaciones.club(id).getNombre();
			relaciones.eliminarClub(id);
			flash.addFlashAttribute("ok", nombre + " eliminado junto con sus jugadores (cascada)");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:/listar";
	}

	// ======================= Entrenador =======================

	@PostMapping("/entrenadores/guardar")
	public String guardarEntrenador(@ModelAttribute("entrenador") Entrenador entrenador, BindingResult errores, Model model,
			RedirectAttributes flash) {
		if (errores.hasErrors()) {
			model.addAttribute("error", "Revisa los campos: hay un número o una fecha con formato incorrecto.");
			return mostrarIndex(model, "entrenador");
		}
		try {
			relaciones.guardarEntrenador(entrenador.getId(), entrenador);
			flash.addFlashAttribute("ok", "Entrenador " + entrenador.nombreCompleto() + " guardado");
			return "redirect:/?pestana=entrenador";
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
			return mostrarIndex(model, "entrenador");
		}
	}

	@GetMapping("/entrenadores/{id}/editar")
	public String editarEntrenador(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
		try {
			model.addAttribute("entrenador", relaciones.entrenador(id));
			return mostrarIndex(model, "entrenador");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/?pestana=entrenador";
		}
	}

	@PostMapping("/entrenadores/{id}/eliminar")
	public String eliminarEntrenador(@PathVariable("id") Long id, RedirectAttributes flash) {
		try {
			relaciones.eliminarEntrenador(id);
			flash.addFlashAttribute("ok", "Entrenador eliminado");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:/?pestana=entrenador";
	}

	// ======================= Jugador =======================

	@PostMapping("/jugadores/guardar")
	public String guardarJugador(@ModelAttribute("jugador") Jugador jugador, BindingResult errores, Model model,
			RedirectAttributes flash) {
		if (errores.hasErrors()) {
			model.addAttribute("error", "Revisa los campos: hay un número o una fecha con formato incorrecto.");
			return mostrarIndex(model, "jugador");
		}
		try {
			relaciones.guardarJugador(jugador.getId(), jugador);
			flash.addFlashAttribute("ok", "Jugador " + jugador.nombreCompleto() + " guardado");
			return "redirect:/?pestana=jugador";
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
			return mostrarIndex(model, "jugador");
		}
	}

	@GetMapping("/jugadores/{id}/editar")
	public String editarJugador(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
		try {
			model.addAttribute("jugador", relaciones.jugador(id));
			return mostrarIndex(model, "jugador");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/?pestana=jugador";
		}
	}

	@PostMapping("/jugadores/{id}/eliminar")
	public String eliminarJugador(@PathVariable("id") Long id, RedirectAttributes flash) {
		try {
			relaciones.eliminarJugador(id);
			flash.addFlashAttribute("ok", "Jugador eliminado (si estaba en un club, salió del plantel)");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:/?pestana=jugador";
	}

	// ======================= Asociación =======================

	@PostMapping("/asociaciones/guardar")
	public String guardarAsociacion(@ModelAttribute("asociacion") Asociacion asociacion, BindingResult errores, Model model,
			RedirectAttributes flash) {
		if (errores.hasErrors()) {
			model.addAttribute("error", "Revisa los campos: hay un número o una fecha con formato incorrecto.");
			return mostrarIndex(model, "asociacion");
		}
		try {
			relaciones.guardarAsociacion(asociacion.getId(), asociacion);
			flash.addFlashAttribute("ok", "Asociación " + asociacion.getSiglas() + " guardada");
			return "redirect:/?pestana=asociacion";
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
			return mostrarIndex(model, "asociacion");
		}
	}

	@GetMapping("/asociaciones/{id}/editar")
	public String editarAsociacion(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
		try {
			model.addAttribute("asociacion", relaciones.asociacion(id));
			return mostrarIndex(model, "asociacion");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/?pestana=asociacion";
		}
	}

	@PostMapping("/asociaciones/{id}/eliminar")
	public String eliminarAsociacion(@PathVariable("id") Long id, RedirectAttributes flash) {
		try {
			relaciones.eliminarAsociacion(id);
			flash.addFlashAttribute("ok", "Asociación eliminada");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:/?pestana=asociacion";
	}

	// ======================= Competición =======================

	@PostMapping("/competiciones/guardar")
	public String guardarCompeticion(@ModelAttribute("competicion") Competicion competicion, BindingResult errores, Model model,
			RedirectAttributes flash) {
		if (errores.hasErrors()) {
			model.addAttribute("error", "Revisa los campos: hay un número o una fecha con formato incorrecto.");
			return mostrarIndex(model, "competicion");
		}
		try {
			relaciones.guardarCompeticion(competicion.getId(), competicion);
			flash.addFlashAttribute("ok", "Competición " + competicion.getNombre() + " guardada");
			return "redirect:/?pestana=competicion";
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
			return mostrarIndex(model, "competicion");
		}
	}

	@GetMapping("/competiciones/{id}/editar")
	public String editarCompeticion(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
		try {
			model.addAttribute("competicion", relaciones.competicion(id));
			return mostrarIndex(model, "competicion");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/?pestana=competicion";
		}
	}

	@PostMapping("/competiciones/{id}/eliminar")
	public String eliminarCompeticion(@PathVariable("id") Long id, RedirectAttributes flash) {
		try {
			relaciones.eliminarCompeticion(id);
			flash.addFlashAttribute("ok", "Competición eliminada");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:/?pestana=competicion";
	}

	// ======================= Ayuda =======================

	/** Llena el modelo de index.html: formularios vacíos (si no vienen ya llenos) y las listas. */
	private String mostrarIndex(Model model, String pestana) {
		if (!model.containsAttribute("clubForm")) {
			model.addAttribute("clubForm", new ClubFormulario());
		}
		if (!model.containsAttribute("entrenador")) {
			model.addAttribute("entrenador", new Entrenador());
		}
		if (!model.containsAttribute("jugador")) {
			model.addAttribute("jugador", new Jugador());
		}
		if (!model.containsAttribute("asociacion")) {
			model.addAttribute("asociacion", new Asociacion());
		}
		if (!model.containsAttribute("competicion")) {
			model.addAttribute("competicion", new Competicion());
		}

		Long clubEditado = ((ClubFormulario) model.getAttribute("clubForm")).getId();

		model.addAttribute("pestana", pestana);
		model.addAttribute("entrenadoresLibres", relaciones.entrenadoresDisponibles(clubEditado));
		model.addAttribute("jugadoresLibres", relaciones.jugadoresDisponibles(clubEditado));
		model.addAttribute("listaClubes", clubes.findAllByOrderByNombreAsc());
		model.addAttribute("listaEntrenadores", entrenadores.findAllByOrderByApellidoAsc());
		model.addAttribute("listaJugadores", jugadores.findAllByOrderByNumeroAsc());
		model.addAttribute("listaAsociaciones", asociaciones.findAllByOrderByNombreAsc());
		model.addAttribute("listaCompeticiones", competiciones.findAllByOrderByFechaInicioAsc());
		model.addAttribute("clubDelEntrenador", relaciones.clubPorEntrenador());
		model.addAttribute("clubDelJugador", relaciones.clubPorJugador());
		return "index";
	}
}
