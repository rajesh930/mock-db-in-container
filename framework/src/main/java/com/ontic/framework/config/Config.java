package com.ontic.framework.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rajesh
 * @since 21/02/25 09:08
 */
public class Config {
    private String type;
    private Map<String, Object> config;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public void set(String key, Object value) {
        if (config == null) {
            config = new HashMap<>();
        }
        config.put(key, value);
    }

    public <T> T get(String key) {
        if (config == null) {
            return null;
        }
        //noinspection unchecked
        return (T) config.get(key);
    }

    public Config type(String type) {
        setType(type);
        return this;
    }
}
