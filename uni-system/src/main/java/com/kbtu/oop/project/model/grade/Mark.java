package com.kbtu.oop.project.model.grade;

import com.kbtu.oop.project.model.common.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Mark extends BaseEntity {

    private UUID studentId;
    private UUID courseId;
    private UUID teacherId;

    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;

    @com.fasterxml.jackson.annotation.JsonIgnore
    public double getTotal() {
        return firstAttestation + secondAttestation + finalExam;
    }
}