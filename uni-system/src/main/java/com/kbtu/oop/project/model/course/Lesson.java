package com.kbtu.oop.project.model.course;

import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.common.LessonType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Lesson extends BaseEntity {

    private UUID courseId;
    private UUID instructorId;
    private LessonType lessonType;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private int capacity;
    private List<UUID> enrolledStudentIds = new ArrayList<>();
}
