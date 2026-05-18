package com.kbtu.oop.project.model.user;

import com.kbtu.oop.project.model.common.TeacherPosition;
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
public class Teacher extends Employee implements Researcher {

    private TeacherPosition position = TeacherPosition.TUTOR;
    private List<UUID> courseIds = new ArrayList<>();
    private List<UUID> researchPaperIds = new ArrayList<>();
    private double averageRating;
    private int ratingCount;

    public void addRating(int rating) {
        double sum = averageRating * ratingCount + rating;
        ratingCount++;
        averageRating = sum / ratingCount;
    }
}