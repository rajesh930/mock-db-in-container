package com.ontic.test.base;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

/**
 * Assets which support Tests can be kept in this Fixtures
 *
 * @author rajesh
 * @since 04/03/25 18:21
 */
public class Fixtures {
    private static MongoDBContainer mongoDBContainer;
    private static ElasticsearchContainer elasticsearchContainer;

    public static void setUp() {
    }

    public static void tearDown() {
        if (mongoDBContainer != null) {
            mongoDBContainer.stop();
        }

        if (elasticsearchContainer != null) {
            elasticsearchContainer.stop();
        }
    }

    public static void createNewMongoDB() {
        if (mongoDBContainer != null) {
            mongoDBContainer.stop();
            mongoDBContainer = null;
        }
        mongoDBContainer = new MongoDBContainer("mongo:4.0.10");
        mongoDBContainer.start();
    }

    public static void ensureMongoDBRunning() {
        if (mongoDBContainer != null) {
            return;
        }
        mongoDBContainer = new MongoDBContainer("mongo:4.0.10");
        mongoDBContainer.start();
    }

    public static MongoDBContainer getMongoDB() {
        return mongoDBContainer;
    }

    public static void createNewElasticSearch() {
        if (elasticsearchContainer != null) {
            elasticsearchContainer.stop();
            elasticsearchContainer = null;
        }
        elasticsearchContainer = new ElasticsearchContainer(
                "docker.elastic.co/elasticsearch/elasticsearch:8.17.2")
                .withEnv("xpack.security.transport.ssl.enabled", "false")
                .withEnv("xpack.security.http.ssl.enabled", "false")
                .withEnv("action.destructive_requires_name", "false");
        elasticsearchContainer.start();
    }

    public static void ensureElasticSearchRunning() {
        if (elasticsearchContainer != null) {
            return;
        }
        elasticsearchContainer = new ElasticsearchContainer(
                "docker.elastic.co/elasticsearch/elasticsearch:8.17.2")
                .withEnv("xpack.security.transport.ssl.enabled", "false")
                .withEnv("xpack.security.http.ssl.enabled", "false")
                .withEnv("action.destructive_requires_name", "false");
        elasticsearchContainer.start();
    }

    public static ElasticsearchContainer getElasticSearch() {
        return elasticsearchContainer;
    }
}
