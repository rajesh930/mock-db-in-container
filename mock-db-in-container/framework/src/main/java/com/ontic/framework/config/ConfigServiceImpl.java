package com.ontic.framework.config;

import org.springframework.stereotype.Service;

/**
 * @author rajesh
 * @since 20/02/25 13:33
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Override
    public Config getConfig(String type) {
        return new Config().type(type);
    }
}
