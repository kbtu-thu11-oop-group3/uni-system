package com.kbtu.oop.project.util;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static UUID generate() {
        return UUID.randomUUID();
    }
}