package com.kbtu.oop.project.model.course;

import com.kbtu.oop.project.model.common.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LessonJournalRecord extends BaseEntity {

    private UUID lessonId;
    private UUID courseId;
    private UUID teacherId;
    private UUID studentId;
    private LocalDate lessonDate = LocalDate.now();
    private Double score;
    private Boolean present;
    private String comment;
}
