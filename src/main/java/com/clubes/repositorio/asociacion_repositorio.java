package com.clubes.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clubes.modelo.Asociacion;

/** Hereda save, findAll, findById, existsById, deleteById y count. */
public interface asociacion_repositorio extends MongoRepository<Asociacion, Long> {

	List<Asociacion> findAllByOrderByNombreAsc();
}
