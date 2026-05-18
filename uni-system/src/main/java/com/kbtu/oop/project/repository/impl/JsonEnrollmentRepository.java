package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.course.Enrollment;
import com.kbtu.oop.project.repository.EnrollmentRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonEnrollmentRepository extends AbstractJsonRepository<Enrollment> implements EnrollmentRepository {

    public JsonEnrollmentRepository() {
        super(DataPaths.enrollmentsPath(), Enrollment[].class);
    }
}