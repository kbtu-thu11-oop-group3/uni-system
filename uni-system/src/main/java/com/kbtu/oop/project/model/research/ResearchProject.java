package com.kbtu.oop.project.model.research;

import com.kbtu.oop.project.model.common.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ResearchProject extends BaseEntity {

    private String topic;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<UUID> participantIds = new ArrayList<>();
    private List<UUID> publishedPaperIds = new ArrayList<>();

    public void addParticipant(UUID researcherId) {
        participantIds.add(researcherId);
    }
}