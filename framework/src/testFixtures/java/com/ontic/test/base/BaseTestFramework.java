package com.ontic.test.base;

import com.ontic.framework.config.Config;
import com.ontic.framework.config.ConfigService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.lang.annotation.Annotation;

/**
 * @author rajesh
 * @since 27/02/25 13:53
 */
public abstract class BaseTestFramework {
    @MockitoSpyBean
    private ConfigService configService;

    @BeforeAll
    public static void setupDBsAtClassLevel(TestInfo testInfo) {
        setupMongoIfRequired(testInfo);
        setupESIfRequired(testInfo);
    }

    @BeforeEach
    public void setupDBsAtTestMethodLevel(TestInfo testInfo) {
        setupMongoIfRequired(testInfo);
        setupESIfRequired(testInfo);
        mockConfigService(testInfo);
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
