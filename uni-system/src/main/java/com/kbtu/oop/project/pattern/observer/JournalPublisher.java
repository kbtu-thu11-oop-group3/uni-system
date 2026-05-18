package com.kbtu.oop.project.pattern.observer;

import com.kbtu.oop.project.model.research.ResearchPaper;

import java.util.ArrayList;
import java.util.List;

public class JournalPublisher {

    private final List<JournalSubscriber> subscribers = new ArrayList<>();

    public void subscribe(JournalSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(JournalSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void notifyPublished(ResearchPaper paper) {
        for (JournalSubscriber subscriber : subscribers) {
            subscriber.onPaperPublished(paper);
        }
    }
}