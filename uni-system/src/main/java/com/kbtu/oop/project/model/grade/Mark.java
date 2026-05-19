package com.kbtu.oop.project.model.grade;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Double firstAttestation;
    private Double secondAttestation;
    private Double finalExam;

    @JsonIgnore
    public double getTotal() {
        return (firstAttestation != null ? firstAttestation : 0) + 
               (secondAttestation != null ? secondAttestation : 0) + 
               (finalExam != null ? finalExam : 0);
    }

    @JsonIgnore
    public boolean isComplete() {
        return firstAttestation != null && secondAttestation != null && finalExam != null;
    }
}