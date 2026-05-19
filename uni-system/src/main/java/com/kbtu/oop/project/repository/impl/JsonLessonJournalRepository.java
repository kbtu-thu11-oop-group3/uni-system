package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.course.LessonJournalRecord;
import com.kbtu.oop.project.repository.LessonJournalRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonLessonJournalRepository extends AbstractJsonRepository<LessonJournalRecord> implements LessonJournalRepository {

    public JsonLessonJournalRepository() {
        super(DataPaths.lessonJournalPath(), LessonJournalRecord[].class);
    }
}
