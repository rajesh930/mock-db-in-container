package com.ontic.module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * @author rajesh
 * @since 01/03/25 10:36
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class ModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModuleApplication.class, args);
    }
}