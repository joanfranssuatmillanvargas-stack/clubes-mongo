package com.clubes.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clubes.modelo.Jugador;

/** Hereda save, findAll, findById, existsById, deleteById y count. */
public interface jugador_repositorio extends MongoRepository<Jugador, Long> {

	List<Jugador> findAllByOrderByNumeroAsc();
}
