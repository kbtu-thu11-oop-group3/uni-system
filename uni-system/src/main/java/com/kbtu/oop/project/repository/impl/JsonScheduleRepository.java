package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.course.Schedule;
import com.kbtu.oop.project.repository.ScheduleRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonScheduleRepository extends AbstractJsonRepository<Schedule> implements ScheduleRepository {

    public JsonScheduleRepository() {
        super(DataPaths.schedulesPath(), Schedule[].class);
    }
}
