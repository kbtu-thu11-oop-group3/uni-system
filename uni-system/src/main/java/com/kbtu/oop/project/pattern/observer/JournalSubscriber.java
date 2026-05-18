package com.kbtu.oop.project.pattern.observer;

import com.kbtu.oop.project.model.research.ResearchPaper;

public interface JournalSubscriber {

    void onPaperPublished(ResearchPaper paper);
}