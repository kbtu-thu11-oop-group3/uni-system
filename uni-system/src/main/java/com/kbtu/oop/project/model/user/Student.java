package com.kbtu.oop.project.model.user;

import com.kbtu.oop.project.model.common.StudentType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Student extends User {

    private String studentCode;
    private String major;
    private int yearOfStudy;
    private int credits;
    private double gpa;
    private int failedAttempts;
    private StudentType studentType = StudentType.STUDENT;
    private List<UUID> enrolledCourseIds = new ArrayList<>();
}