package com.kbtu.oop.project.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private final Properties properties = new Properties();

    public AppConfig() {
        try (InputStream inputStream = AppConfig.class.getResourceAsStream("/config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load config.properties", exception);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}