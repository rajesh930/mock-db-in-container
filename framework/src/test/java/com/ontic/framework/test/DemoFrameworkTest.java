package com.ontic.framework.test;

import com.ontic.framework.config.ConfigService;
import com.ontic.framework.db.es.ESService;
import com.ontic.framework.db.mongo.MongoService;
import com.ontic.spring.FrameworkAutoConfig;
import com.ontic.test.base.BaseTestFramework;
import com.ontic.test.base.RequireES;
import com.ontic.test.base.RequireMongo;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author rajesh
 * @since 25/02/25 13:30
 */
@RequireMongo
@RequireES
@SpringBootTest(classes = FrameworkAutoConfig.class)
public class DemoFrameworkTest extends BaseTestFramework {
    private static final String POJO = "pojo";
    private static final String ID = new ObjectId().toString();
    @Autowired
    private MongoService mongoService;
    @Autowired
    private ESService esService;
    @Autowired
    private ConfigService configService;

    @Test
    public void testMongoESMocked() {
        Assertions.assertNotNull(configService.getConfig("MONGO").get("url"));
        Assertions.assertNotNull(configService.getConfig("ES").get("url"));

        esService.createIndex(POJO);
        esService.index(POJO, ID, new Pojo().id(ID).name(POJO));
        Pojo read = esService.read(POJO, ID, Pojo.class);
        Assertions.assertNotNull(read);
        Assertions.assertEquals(POJO, read.getName());
        Assertions.assertEquals(ID, read.getId());

        mongoService.save(POJO, POJO, new Pojo().id(ID).name(POJO));
        read = mongoService.get(POJO, POJO, ID, Pojo.class);
        Assertions.assertNotNull(read);
        Assertions.assertEquals(POJO, read.getName());
        Assertions.assertEquals(ID, read.getId());
    }

    @Test
    @RequireES(false)
    @RequireMongo(false)
    public void testMongoESNotMocked() {
        Assertions.assertNull(configService.getConfig("MONGO").get("url"));
        Assertions.assertNull(configService.getConfig("ES").get("url"));
    }

    @Test
    @RequireES(false)
    public void testOnlyMongoMocked() {
        Assertions.assertNotNull(configService.getConfig("MONGO").get("url"));
        Assertions.assertNull(configService.getConfig("ES").get("url"));
    }

    @Test
    @RequireMongo(false)
    public void testOnlyESMocked() {
        Assertions.assertNull(configService.getConfig("MONGO").get("url"));
        Assertions.assertNotNull(configService.getConfig("ES").get("url"));
    }

    public static class Pojo {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Pojo id(String id) {
            this.id = id;
            return this;
        }

        public Pojo name(String name) {
            this.name = name;
            return this;
        }
    }
}
