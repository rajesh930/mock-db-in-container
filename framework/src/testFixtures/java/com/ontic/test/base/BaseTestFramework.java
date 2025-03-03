package com.ontic.test.base;

import com.ontic.framework.config.Config;
import com.ontic.framework.config.ConfigService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

/**
 * @author rajesh
 * @since 27/02/25 13:53
 */
public abstract class BaseTestFramework {

    private MongoDBContainer mongoDBContainer;
    private ElasticsearchContainer elasticsearchContainer;

    @MockitoSpyBean
    private ConfigService configService;

    @BeforeEach
    public void setupDBs(TestInfo testInfo) {
        setupMongoIfRequired(testInfo);
        setupESIfRequired(testInfo);
    }

    @AfterEach
    public void stopDBs() {
        if (mongoDBContainer != null) {
            mongoDBContainer.stop();
        }

        if (elasticsearchContainer != null) {
            elasticsearchContainer.stop();
        }
    }

    private void setupMongoIfRequired(TestInfo testInfo) {
        RequireMongo requireMongo = testInfo.getTestMethod().map(m -> m.getAnnotation(RequireMongo.class)).orElse(null);
        if (requireMongo == null) {
            requireMongo = this.getClass().getAnnotation(RequireMongo.class);
        }
        if (requireMongo != null && requireMongo.value()) {
            this.mongoDBContainer = new MongoDBContainer("mongo:" + requireMongo.version());
            this.mongoDBContainer.start();
            Mockito.doAnswer((Answer<Config>) invocationOnMock -> {
                Config config = new Config();
                config.setType("MONGO");
                config.set("url", mongoDBContainer.getConnectionString());
                return config;
            }).when(configService).getConfig("MONGO");
        }
    }

    private void setupESIfRequired(TestInfo testInfo) {
        RequireES requireES = testInfo.getTestMethod().map(m -> m.getAnnotation(RequireES.class)).orElse(null);
        if (requireES == null) {
            requireES = this.getClass().getAnnotation(RequireES.class);
        }
        if (requireES != null && requireES.value()) {
            this.elasticsearchContainer = new ElasticsearchContainer(
                    "docker.elastic.co/elasticsearch/elasticsearch:" + requireES.version())
                    .withEnv("xpack.security.transport.ssl.enabled", "false")
                    .withEnv("xpack.security.http.ssl.enabled", "false");
            this.elasticsearchContainer.start();
            Mockito.doAnswer((Answer<Config>) invocationOnMock -> {
                Config config = new Config();
                config.setType("ES");
                config.set("url", elasticsearchContainer.getHttpHostAddress());
                config.set("user", "elastic");
                config.set("password", "changeme");
                return config;
            }).when(configService).getConfig("ES");
        }
    }
}
