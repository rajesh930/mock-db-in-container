package com.ontic.framework.db.mongo;

/**
 * @author rajesh
 * @since 28/02/25 20:57
 */
public interface MongoService {

    <T> String save(String db, String coll, T obj);

    <T> T get(String db, String coll, String id, Class<T> clazz);
}
