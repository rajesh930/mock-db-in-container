package com.ontic.framework.db.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.ontic.framework.config.Config;
import com.ontic.framework.config.ConfigService;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * @author rajesh
 * @since 28/02/25 20:59
 */
@Service
public class MongoServiceImpl implements MongoService {

    private final ConfigService configService;

    @Autowired
    public MongoServiceImpl(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public <T> String save(String db, String coll, T obj) {
        Config mongo = configService.getConfig("MONGO");
        try (MongoClient mongoClient = MongoClients.create((String) mongo.get("url"))) {
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
            CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
            MongoDatabase database = mongoClient.getDatabase(db).withCodecRegistry(pojoCodecRegistry);
            //noinspection unchecked
            MongoCollection<T> collection = (MongoCollection<T>) database.getCollection(coll, obj.getClass());
            return Objects.requireNonNull(collection.insertOne(obj).getInsertedId()).toString();
        }
    }

    @Override
    public <T> T get(String db, String coll, String id, Class<T> clazz) {
        Config mongo = configService.getConfig("MONGO");
        try (MongoClient mongoClient = MongoClients.create((String) mongo.get("url"))) {
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
            CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
            MongoDatabase database = mongoClient.getDatabase(db).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<T> collection = database.getCollection(coll, clazz);
            return collection.find(Filters.eq("_id", id)).first();
        }
    }
}
