package com.clubes.modelo;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Competición (Liga, Copa, Libertadores...). Un club juega varias
 * y cada una tiene varios clubes: relación @ManyToMany Club → Competicion.
 */
@Document(collection = "competiciones")
public class Competicion implements Identificable {

	@Id
	private Long id;
	private String nombre;
	private int montoPremio;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // formato yyyy-MM-dd del <input type="date">
	private LocalDate fechaInicio;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaFin;

	public Competicion() {
	}

	public Competicion(String nombre, int montoPremio, LocalDate fechaInicio, LocalDate fechaFin) {
		this.nombre = nombre;
		this.montoPremio = montoPremio;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getMontoPremio() {
		return montoPremio;
	}

	public void setMontoPremio(int montoPremio) {
		this.montoPremio = montoPremio;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}
}
