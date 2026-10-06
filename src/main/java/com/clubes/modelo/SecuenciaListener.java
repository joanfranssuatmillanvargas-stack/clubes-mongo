package com.clubes.modelo;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * MongoDB no tiene AUTO_INCREMENT. Antes de guardar cualquier entidad
 * Identificable sin id, este listener le asigna el siguiente número
 * del contador de su colección.
 */
@Component
public class SecuenciaListener extends AbstractMongoEventListener<Object> {

	private final MongoOperations mongo;

	public SecuenciaListener(@Lazy MongoOperations mongo) {
		this.mongo = mongo;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<Object> evento) {
		if (evento.getSource() instanceof Identificable entidad && entidad.getId() == null) {
			entidad.setId(siguiente(evento.getCollectionName()));
		}
	}

	/** Suma 1 al contador de la colección (lo crea si no existe) y devuelve el valor nuevo. */
	private long siguiente(String coleccion) {
		Secuencia secuencia = mongo.findAndModify(
				Query.query(Criteria.where("_id").is(coleccion)),
				new Update().inc("valor", 1),
				FindAndModifyOptions.options().returnNew(true).upsert(true),
				Secuencia.class);
		return secuencia == null ? 1L : secuencia.getValor();
	}
}
