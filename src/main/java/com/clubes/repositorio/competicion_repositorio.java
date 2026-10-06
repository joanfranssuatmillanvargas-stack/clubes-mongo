package com.clubes.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clubes.modelo.Competicion;

/** Hereda save, findAll, findById, existsById, deleteById y count. */
public interface competicion_repositorio extends MongoRepository<Competicion, Long> {

	List<Competicion> findAllByOrderByFechaInicioAsc();
}
