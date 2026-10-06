package com.clubes.controlador;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Convierte los errores de los controladores *_apirest en JSON:
 * { "estado": 409, "error": "Conflict", "mensaje": "..." }
 * Solo aplica a los @RestController (la parte web maneja sus propios mensajes).
 */
@RestControllerAdvice(annotations = RestController.class)
public class ManejadorErroresApi {

	/** Errores de las reglas (400, 404 o 409) lanzados por relaciones_repositorio. */
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> regla(ResponseStatusException ex) {
		return respuesta(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> jsonMalo(HttpMessageNotReadableException ex) {
		return respuesta(HttpStatus.BAD_REQUEST,
				"El JSON no se pudo leer. Revisa comas, comillas y que las fechas sean yyyy-MM-dd.");
	}

	private ResponseEntity<Map<String, Object>> respuesta(HttpStatus estado, String mensaje) {
		Map<String, Object> cuerpo = new LinkedHashMap<>();
		cuerpo.put("estado", estado.value());
		cuerpo.put("error", estado.getReasonPhrase());
		cuerpo.put("mensaje", mensaje);
		return ResponseEntity.status(estado).body(cuerpo);
	}
}
