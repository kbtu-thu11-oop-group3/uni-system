package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.course.Lesson;
import com.kbtu.oop.project.repository.LessonRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonLessonRepository extends AbstractJsonRepository<Lesson> implements LessonRepository {

    public JsonLessonRepository() {
        super(DataPaths.lessonsPath(), Lesson[].class);
    }
}
