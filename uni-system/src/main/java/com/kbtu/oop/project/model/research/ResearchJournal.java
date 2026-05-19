package com.kbtu.oop.project.model.research;

import com.kbtu.oop.project.model.common.BaseEntity;
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
public class ResearchJournal extends BaseEntity {

    private String name;
    private String description;
    private String issn;
    private List<UUID> subscriberIds = new ArrayList<>();
}
