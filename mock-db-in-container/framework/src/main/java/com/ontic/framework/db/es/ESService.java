package com.ontic.framework.db.es;

/**
 * @author rajesh
 * @since 28/02/25 20:57
 */
public interface ESService {
    void createIndex(String indexName);

    void index(String index, String id, Object obj);

    <T> T read(String index, String id, Class<T> clazz);
}
