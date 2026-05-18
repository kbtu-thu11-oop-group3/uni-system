package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.grade.Mark;
import com.kbtu.oop.project.repository.GradeRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonGradeRepository extends AbstractJsonRepository<Mark> implements GradeRepository {

    public JsonGradeRepository() {
        super(DataPaths.gradesPath(), Mark[].class);
    }
}