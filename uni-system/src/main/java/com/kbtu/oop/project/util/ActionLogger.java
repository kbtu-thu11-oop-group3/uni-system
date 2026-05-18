package com.kbtu.oop.project.util;

import com.kbtu.oop.project.model.common.ActionLogEntry;

import java.util.List;
import java.util.UUID;

public class ActionLogger {

    private static final ActionLogger INSTANCE = new ActionLogger();

    private ActionLogger() {
    }

    public static ActionLogger getInstance() {
        return INSTANCE;
    }

    public synchronized void log(UUID actorId, String action, String details) {
        List<ActionLogEntry> entries = JsonUtil.readList(DataPaths.actionLogPath(), ActionLogEntry[].class);
        ActionLogEntry entry = new ActionLogEntry();
        entry.setActorId(actorId);
        entry.setAction(action);
        entry.setDetails(details);
        entries.add(entry);
        JsonUtil.writeCollection(DataPaths.actionLogPath(), entries);
    }
}