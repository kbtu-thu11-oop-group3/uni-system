package com.kbtu.oop.project.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonUtil() {
    }

    public static <T> List<T> readList(Path path, Class<T[]> arrayType) {
        try {
            if (Files.notExists(path)) {
                return List.of();
            }
            T[] items = MAPPER.readValue(path.toFile(), arrayType);
            return new ArrayList<>(List.of(items));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read JSON from " + path, exception);
        }
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static void writeCollection(Path path, Collection<?> items) {
        try {
            Files.createDirectories(path.getParent());
            MAPPER.writeValue(path.toFile(), items);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write JSON to " + path, exception);
        }
    }
}