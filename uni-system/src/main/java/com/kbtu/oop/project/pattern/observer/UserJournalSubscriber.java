package com.kbtu.oop.project.pattern.observer;

import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.UUID;

public class UserJournalSubscriber implements JournalSubscriber {

    private final UUID userId;

    public UserJournalSubscriber(UUID userId) {
        this.userId = userId;
    }

    @Override
    public void onPaperPublished(ResearchPaper paper) {
        ActionLogger.getInstance().log(userId, "JOURNAL_NOTIFY", "New paper published: " + paper.getTitle());
    }
}