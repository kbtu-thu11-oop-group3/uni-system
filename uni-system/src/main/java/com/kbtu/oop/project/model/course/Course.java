package com.kbtu.oop.project.model.course;

import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.common.CourseType;
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
public class Course extends BaseEntity {

    private String code;
    private String title;
    private String description;
    private int credits;
    private CourseType courseType = CourseType.MAJOR;
    private List<UUID> instructorIds = new ArrayList<>();
    private List<UUID> studentIds = new ArrayList<>();
    private int requiredLectures;
    private int requiredLabs;
    private int requiredPractices;
}
