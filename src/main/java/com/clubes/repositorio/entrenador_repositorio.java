package com.clubes.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clubes.modelo.Entrenador;

/** Hereda save, findAll, findById, existsById, deleteById y count. */
public interface entrenador_repositorio extends MongoRepository<Entrenador, Long> {

	List<Entrenador> findAllByOrderByApellidoAsc();
}
