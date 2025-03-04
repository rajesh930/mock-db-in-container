package com.ontic.test.base;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.ontic.framework.config.Config;
import com.ontic.framework.config.ConfigService;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * @author rajesh
 * @since 27/02/25 13:53
 */
public abstract class BaseTestFramework {
    @MockitoSpyBean
    private ConfigService configService;

    @BeforeAll
    public static void setupDBsAtClass(TestInfo testInfo) {
        setupMongoIfRequired(testInfo);
        setupESIfRequired(testInfo);
    }

    @BeforeEach
    public void setupDBsAtTest(TestInfo testInfo) {
        setupMongoIfRequired(testInfo);
        setupESIfRequired(testInfo);
        mockConfigService(testInfo);
    }

    @AfterEach
    public void cleanupDBs(TestInfo testInfo) {
        RequireMongo requireMongo = getApplicableAnnotation(testInfo, RequireMongo.class);
        if (requireMongo != null && requireMongo.value()) {
            try (MongoClient mongoClient = MongoClients.create(Fixtures.getMongoDB().getConnectionString())) {
                mongoClient.listDatabaseNames().forEach(s -> {
                    if (!TestConstants.systemDbs.contains(s)) {
                        mongoClient.getDatabase(s).drop();
                    }
                });
            }
        }

        RequireES requireES = getApplicableAnnotation(testInfo, RequireES.class);
        if (requireES != null && requireES.value()) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "changeme"));
            RestClient rest = RestClient.builder(HttpHost.create(Fixtures.getElasticSearch().getHttpHostAddress()))
                    .setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider)).build();
            try (RestClientTransport transport = new RestClientTransport(rest, new JacksonJsonpMapper())) {
                ElasticsearchClient esClient = new ElasticsearchClient(transport);
                esClient.indices().delete(builder -> builder.index("_all"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void setupMongoIfRequired(TestInfo testInfo) {
        RequireMongo requireMongo = getApplicableAnnotation(testInfo, RequireMongo.class);

        if (requireMongo != null) {
            if (requireMongo.requiresNew()) {
                Fixtures.createNewMongoDB();
            } else if (requireMongo.value()) {
                Fixtures.ensureMongoDBRunning();
            }
        }
    }

    private static void setupESIfRequired(TestInfo testInfo) {
        RequireES requireES = getApplicableAnnotation(testInfo, RequireES.class);

        if (requireES != null) {
            if (requireES.requiresNew()) {
                Fixtures.createNewElasticSearch();
            } else if (requireES.value()) {
                Fixtures.ensureElasticSearchRunning();
            }
        }
    }

    private static <T extends Annotation> T getApplicableAnnotation(TestInfo testInfo, Class<T> annClass) {
        T annotation = testInfo.getTestMethod().map(m -> m.getAnnotation(annClass)).orElse(null);
        if (annotation == null) {
            annotation = testInfo.getTestClass().map(m -> m.getAnnotation(annClass)).orElse(null);
        }
        return annotation;
    }

    private void mockConfigService(TestInfo testInfo) {
        RequireMongo requireMongo = getApplicableAnnotation(testInfo, RequireMongo.class);
        if (requireMongo != null && requireMongo.value()) {
            Mockito.doAnswer((Answer<Config>) invocationOnMock -> {
                Config config = new Config();
                config.setType("MONGO");
                config.set("url", Fixtures.getMongoDB().getConnectionString());
                return config;
            }).when(configService).getConfig("MONGO");
        }

        RequireES requireES = getApplicableAnnotation(testInfo, RequireES.class);
        if (requireES != null && requireES.value()) {
            Mockito.doAnswer((Answer<Config>) invocationOnMock -> {
                Config config = new Config();
                config.setType("ES");
                config.set("url", Fixtures.getElasticSearch().getHttpHostAddress());
                config.set("user", "elastic");
                config.set("password", "changeme");
                return config;
            }).when(configService).getConfig("ES");
        }
    }
}
