package com.kbtu.oop.project.model.course;

import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.common.RequestStatus;
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
public class Enrollment extends BaseEntity {

    private UUID studentId;
    private UUID courseId;
    private RequestStatus status = RequestStatus.NEW;
}