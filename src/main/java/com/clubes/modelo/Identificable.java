package com.clubes.modelo;

/**
 * Lo implementan las 5 entidades para que SecuenciaListener
 * les pueda asignar un id numérico incremental (1, 2, 3...).
 */
public interface Identificable {

	Long getId();

	void setId(Long id);
}
