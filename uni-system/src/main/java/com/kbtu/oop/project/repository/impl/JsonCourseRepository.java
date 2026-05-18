package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.repository.CourseRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonCourseRepository extends AbstractJsonRepository<Course> implements CourseRepository {

    public JsonCourseRepository() {
        super(DataPaths.coursesPath(), Course[].class);
    }
}