package com.kbtu.oop.project.model.user;

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
public class GraduateStudent extends Student implements Researcher {

    private UUID supervisorId;
    private List<UUID> diplomaProjectPaperIds = new ArrayList<>();
    private List<UUID> researchPaperIds = new ArrayList<>();
}