package com.ontic.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author rajesh
 * @since 20/02/25 14:02
 */
@Configuration
@AutoConfiguration
@ComponentScan({"com.ontic.framework.config", "com.ontic.framework.db"})
public class FrameworkAutoConfig {
}
